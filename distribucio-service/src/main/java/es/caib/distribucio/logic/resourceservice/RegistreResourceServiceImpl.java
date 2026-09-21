package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.helper.ObjectMappingHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ContingutHelper;
import es.caib.distribucio.logic.helper.ContingutLogResourceHelper;
import es.caib.distribucio.logic.helper.ExecucioMassivaResourceHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.distribucio.logic.intf.base.exception.ReportGenerationException;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.model.*;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.distribucio.logic.intf.resourceservice.ContingutMovimentResourceService;
import es.caib.distribucio.logic.intf.resourceservice.RegistreResourceService;
import es.caib.distribucio.logic.intf.service.*;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaContingutEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.ExecucioMassivaContingutRepository;
import es.caib.distribucio.persist.resourceentity.*;
import es.caib.distribucio.persist.resourcerepository.ProcedimentResourceRepository;
import es.caib.distribucio.persist.resourcerepository.RegistreResourceRepository;
import es.caib.distribucio.persist.resourcerepository.ServeiResourceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistreResourceServiceImpl extends BaseMutableResourceService<RegistreResource, Long, RegistreResourceEntity> implements RegistreResourceService {

    private final AclEntryResourceService aclEntryResourceService;
    private final AuthenticationHelper authenticationHelper;
    private final ContingutHelper contingutHelper;
    private final EntitatRepository entitatRepository;
    private final ConfigHelper configHelper;
    private final ContingutMovimentResourceService contingutMovimentResourceService;
    private final ObjectMappingHelper objectMappingHelper;
    private final ContingutLogResourceHelper contingutLogResourceHelper;
    private final RegistreResourceRepository registreResourceRepository;
    private final ProcedimentResourceRepository procedimentResourceRepository;
    private final ServeiResourceRepository serveiResourceRepository;
    private final BustiaService bustiaService;
    private final AplicacioService aplicacioService;
    private final ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
    private final ContingutService contingutService;
    private final RegistreService registreService;
    private final ExecucioMassivaResourceHelper execucioMassivaResourceHelper;

    @PostConstruct
    public void init() {
        register(RegistreResource.PERSPECTIVE_DARRER_MOVIMENT_CODE, new DarrerMovimentPerspectiveApplicator());
        register(ContingutResource.PERSPECTIVE_COMMENT_NUM_CODE, new CommentNumPerspectiveApplicator());
        register(RegistreResource.REPORT_INFORME_LOGS_CODE, new InformeLogsReportGenerator());
        register(RegistreResource.ACTION_CLASSIFICAR_CODE, new ClassificarActionExecutor());
        register(RegistreResource.ACTION_ENVIAR_EMAIL_CODE, new EnviarEmailActionExecutor());
        register(RegistreResource.ACTION_REENVIAR_CODE, new ReenviarActionExecutor());
        register(RegistreResource.ACTION_MARCAR_PROCESSADA_CODE, new MarcarProcessadaActionExecutor());
        register(RegistreResource.ACTION_MARCAR_PENDENT_CODE, new MarcarPendentActionExecutor());
    }

    @Override
	protected Specification<RegistreResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<BustiaResourceEntity, ContingutResourceEntity<?>> pareJoin = root.join("pare", JoinType.LEFT);
            Join<BustiaResourceEntity, BustiaResourceEntity> pareBustia = cb.treat(pareJoin, BustiaResourceEntity.class);

            /// Entitat
            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }

            /// Permisos
            boolean isUser = List.of(authenticationHelper.getCurrentUserRoles()).contains(BaseConfig.ROLE_USER);
            if ( isUser ) {
                Set<Serializable> ids = aclEntryResourceService.findIdsWithAnyPermission(
                        ResourceType.BUSTIA,
                        List.of(PermissionEnum.READ),
                        authenticationHelper.getCurrentUserName(),
                        new ArrayList<>(List.of(BaseConfig.ROLE_USER))
                );

                if (ids.isEmpty()) {
                    return cb.disjunction();
                }

                int chunkSize = 900;
                List<Predicate> orPredicates = new ArrayList<>();
                for (int i = 0; i < ids.size(); i += chunkSize) {
                    List<Serializable> chunk = new ArrayList<>(ids).subList(i, Math.min(i + chunkSize, ids.size()));
                    orPredicates.add(pareBustia.get("id").in(chunk));
                }

                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
            }
            /// UNITAT_ORGANITZATIVA
            if (mapaNamedQueries.containsKey("UNITAT_ORGANITZATIVA")) {
                Long unitatOrganitzativaId = Long.valueOf(mapaNamedQueries.get("UNITAT_ORGANITZATIVA"));
                predicates.add( cb.equal(pareBustia.get("unitatOrganitzativa").get("id"), unitatOrganitzativaId) );
            }
            /// INACTIVES
            if (!mapaNamedQueries.containsKey("INACTIVES")) {
                predicates.add( cb.isTrue(pareBustia.get("activa")) );
            }
            /// AMB_REINTENTS
            if (mapaNamedQueries.containsKey("AMB_REINTENTS") || mapaNamedQueries.containsKey("SENSE_REINTENTS")) {
                EntitatEntity entitat = entitatRepository.findById(entitatActualId).get();
                int maxAnnexos = contingutHelper.getGuardarAnnexosMaxReintentsProperty();
//                int maxBackoffice = contingutHelper.getBackofficeMaxReintentsProperty();
                int maxAnotacions = contingutHelper.getEnviarIdsAnotacionsMaxReintentsProperty(entitat);
                int maxRegla = Integer.parseInt( configHelper.getConfigForEntitat(entitat != null ? entitat.getCodi() : null, "es.caib.distribucio.tasca.aplicar.regles.max.reintents") );
                int maxError = Integer.parseInt( configHelper.getConfigForEntitat(entitat != null ? entitat.getCodi() : null, "es.caib.distribucio.backoffice.reintentar.processament.max.reintents") );

                if (mapaNamedQueries.containsKey("AMB_REINTENTS")) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.ARXIU_PENDENT),
                                    cb.lessThan(root.get("procesIntents"), maxAnnexos)
                            ),
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.REGLA_PENDENT),
                                    cb.lessThan(root.get("procesIntents"), maxRegla)
                            ),
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.BACK_ERROR),
                                    cb.lessThan(root.get("procesIntents"), maxError)
                            ),
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.BACK_PENDENT),
                                    cb.lessThan(root.get("procesIntents"), maxAnotacions)
                            )
                    ));
                }
                if (mapaNamedQueries.containsKey("SENSE_REINTENTS")) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.ARXIU_PENDENT),
                                    cb.greaterThanOrEqualTo(root.get("procesIntents"), maxAnnexos)
                            ),
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.REGLA_PENDENT),
                                    cb.greaterThanOrEqualTo(root.get("procesIntents"), maxRegla)
                            ),
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.BACK_ERROR),
                                    cb.greaterThanOrEqualTo(root.get("procesIntents"), maxError)
                            ),
                            cb.and(
                                    cb.equal(root.get("procesEstat"), RegistreProcesEstatEnum.BACK_PENDENT),
                                    cb.greaterThanOrEqualTo(root.get("procesIntents"), maxAnotacions)
                            )
                    ));
                }
            }
            /// INACTIVES
            if (mapaNamedQueries.containsKey("NOMBRE_ANNEXOS")) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<RegistreAnnexResourceEntity> annex = subquery.from(RegistreAnnexResourceEntity.class);

                List<Predicate> annexPredicates = new ArrayList<>();

                annexPredicates.add( cb.equal(annex.get("registre"), root) );
                annexPredicates.add( cb.or(
                        cb.isNull(root.get("justificant").get("id")),
                        cb.notEqual(annex.get("id"), root.get("justificant").get("id"))
                ) );
                annexPredicates.add( cb.or(
                        cb.isNull(root.get("justificantArxiuUuid")),
                        cb.isNull(annex.get("fitxerArxiuUuid")),
                        cb.notEqual(annex.get("fitxerArxiuUuid"), root.get("justificantArxiuUuid"))
                ) );

                if ( isUser ) {
                    annexPredicates.add( cb.or(
                            cb.isNull(annex.get("sicresTipusDocument")),
                            cb.notEqual(annex.get("sicresTipusDocument"), RegistreAnnexSicresTipusDocumentEnum.INTERN.getValor())
                    ) );
                }

                subquery.select(cb.countDistinct(annex))
                        .where(cb.and( annexPredicates.toArray(new Predicate[0]) ));

                RegistreNombreAnnexesEnumDto nombreAnnexes = RegistreNombreAnnexesEnumDto.valueOf( mapaNamedQueries.get("NOMBRE_ANNEXOS") );
                switch (nombreAnnexes) {
                    case AMB_1:
                        predicates.add( cb.equal(subquery, 1L) );
                        break;
                    case AMB_2:
                        predicates.add( cb.equal(subquery, 2L) );
                        break;
                    case AMB_3:
                        predicates.add( cb.equal(subquery, 3L) );
                        break;
                    case AMB_4:
                        predicates.add( cb.equal(subquery, 4L) );
                        break;
                    case AMB_5:
                        predicates.add( cb.equal(subquery, 5L) );
                        break;
                    case DE_6_A_10:
                        predicates.add( cb.between(subquery, 6L, 10L) );
                        break;
                    case DE_11_A_20:
                        predicates.add( cb.between(subquery, 11L, 20L) );
                        break;
                    case DE_21_A_50:
                        predicates.add( cb.between(subquery, 21L, 50L) );
                        break;
                    case DE_51_A_100:
                        predicates.add( cb.between(subquery, 51L, 100L) );
                        break;
                    case MES_DE_100:
                        predicates.add( cb.greaterThan(subquery, 100L) );
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
	}

    @Override
    protected void afterConversion(RegistreResourceEntity entity, RegistreResource resource) {
        if (entity.getAlertes() != null && !entity.getAlertes().isEmpty()) {
            resource.setAlerta(
                    entity.getAlertes().stream()
                            .anyMatch(alertaEntity -> alertaEntity.getLlegida() == Boolean.FALSE)
            );
        }
        if (!entity.getInteressats().isEmpty()) {
            resource.setInteressatsString(
                    StringUtils.join(
                    entity.getInteressats().stream()
                            .map(i -> "- " + i.getNomComplet())
                            .collect(Collectors.toList()), "")
            );
        }

        if (RegistreProcesEstatEnum.isPendent(resource.getProcesEstat())) {
            resource.setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto.PENDENT);
        } else {
            resource.setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto.PROCESSAT);
        }

        ExecucioMassivaContingutEntity execucioMassivaPendent = execucioMassivaContingutRepository.findByElementIdAndEstatIn(
                resource.getId(),
                new ArrayList<> (
                        Arrays.asList(
                                ExecucioMassivaContingutEstatDto.PENDENT,
                                ExecucioMassivaContingutEstatDto.PROCESSANT,
                                ExecucioMassivaContingutEstatDto.PAUSADA)
                )
        );
        resource.setPendentExecucioMassiva(execucioMassivaPendent != null);
    }

    @Override
    protected void afterConversion(List<RegistreResourceEntity> entities, List<RegistreResource> resources) {
        Long entitatActualId = SessioActualUtil.getEntitatId();
        EntitatEntity entitat = entitatRepository.findById(entitatActualId).get();
        int maxAnnexos = contingutHelper.getGuardarAnnexosMaxReintentsProperty();
        int maxBackoffice = contingutHelper.getBackofficeMaxReintentsProperty();
        int maxAnotacions = contingutHelper.getEnviarIdsAnotacionsMaxReintentsProperty(entitat);
        int maxRegla = Integer.parseInt( configHelper.getConfigForEntitat(entitat != null ? entitat.getCodi() : null, "es.caib.distribucio.tasca.aplicar.regles.max.reintents") );
        int maxError = Integer.parseInt( configHelper.getConfigForEntitat(entitat != null ? entitat.getCodi() : null, "es.caib.distribucio.backoffice.reintentar.processament.max.reintents") );

        for (RegistreResource resource: resources) {
            switch (resource.getProcesEstat()) {
                case ARXIU_PENDENT:
                    resource.setMaxReintents(maxAnnexos);
                    break;
                case BACK_COMUNICADA:
                case BACK_ERROR:
                case BACK_REBUDA:
                    resource.setMaxReintents(maxBackoffice);
                    break;
                case BACK_PENDENT:
                    resource.setMaxReintents(maxAnotacions);
                    break;
            }
            if (resource.getProcesIntents() >= resource.getMaxReintents()) {
                resource.setReintentsEsgotat(true);
            }
        }

        super.afterConversion(entities, resources);
    }

    private class DarrerMovimentPerspectiveApplicator implements PerspectiveApplicator<RegistreResourceEntity, RegistreResource> {

        @Override
        public void applySingle(String code, RegistreResourceEntity entity, RegistreResource resource) throws PerspectiveApplicationException {
            if (entity.getDarrerMoviment() != null) {
                resource.setDarrerMovimentResource(
                    contingutMovimentResourceService.getOne(entity.getDarrerMoviment().getId(), null));
            }
        }
    }

    protected static class CommentNumPerspectiveApplicator implements PerspectiveApplicator<RegistreResourceEntity, RegistreResource> {
        @Override
        public void applySingle(String code, RegistreResourceEntity entity, RegistreResource resource) {
            resource.setNumComentaris(entity.getComentaris().size());
        }
    }

    public class InformeLogsReportGenerator implements ReportGenerator<RegistreResourceEntity, Serializable, ContingutLogResource> {
        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
            // TODO: generar informe historico
            return null;
        }

        @Override
        public List<ContingutLogResource> generateData(String code, RegistreResourceEntity entity, Serializable params) throws ReportGenerationException {
            List<ContingutLogResourceEntity> logEntityList = entity.getLogs();
            List<ContingutLogResource> logResourceList = logEntityList.stream()
                    .map(log -> objectMappingHelper.newInstanceMap(log, ContingutLogResource.class))
                    .collect(Collectors.toList());

            for (int i = 0; i<logEntityList.size(); i++){
                ContingutLogResourceEntity e = logEntityList.get(i);
                ContingutLogResource r = logResourceList.get(i);

                contingutLogResourceHelper.setLogText(e, r);
            }

            return logResourceList;
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
    }

    protected class ClassificarActionExecutor implements ActionExecutor<RegistreResourceEntity, RegistreResource.ClassificarForm, HashMap> {

        private final String ERROR_IDS_EMPTY_CODE = "IDS_EMPTY";
        private final String ERROR_DIFERENT_PARE_CODE = "DIFERENT_PARE";

        private boolean isSameBustia(List<RegistreResourceEntity> entityList) {
            long busties = entityList.stream().map(e -> e.getPare().getId()).distinct().count();
            return (busties == 1);
        }

        @Override
        public HashMap exec(String code, RegistreResourceEntity entity, RegistreResource.ClassificarForm params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            String codiSia;

            if (RegistreClassificarTipusEnum.PROCEDIMENT.equals(params.getTipus())) {
                ProcedimentResourceEntity procediment = procedimentResourceRepository.findById(params.getProcediment().getId()).get();
                codiSia = procediment.getCodiSia();
            } else {
                ServeiResourceEntity servei = serveiResourceRepository.findById(params.getServei().getId()).get();
                codiSia = servei.getCodiSia();
            }

            if (params.isMassive()) {
                List<RegistreResourceEntity> registreList = registreResourceRepository.findAllById(params.getIds());
                if (this.isSameBustia( registreList )) {
                    /// TODO: revisar versión massiva
                    Map<String, Object> map = new HashMap<>();
                    map.put("titol", null);
                    map.put("tipus", params.getTipus());
                    map.put("codiProcediment", RegistreClassificarTipusEnum.PROCEDIMENT.equals(params.getTipus())
                            ?codiSia :null);
                    map.put("codiServei", RegistreClassificarTipusEnum.SERVEI.equals(params.getTipus())
                            ?codiSia :null);

                    execucioMassivaResourceHelper.executarAccioMassivaRegistres(
                            ExecucioMassivaTipusDto.CLASSIFICAR,
                            registreList,
                            map);
                } else {
                    throw new ActionExecutionException(
                            RegistreResource.class, null, code,
                            I18nUtil.getInstance().getI18nMessage("bustia.controller.pendent.contingut.classificar.no.mateix.pare.error")
                    );
                }
            } else {
                Map<String, Object> map = new HashMap<>();
                RegistreResourceEntity registre = registreResourceRepository.findById(params.getIds().get(0)).get();
                /// TODO: revisar versión individual
                ClassificacioResultatDto resultat = registreService.classificar(
                        entitatActualId,
                        registre.getId(),
                        params.getTipus().name(),
                        (RegistreClassificarTipusEnum.PROCEDIMENT.equals(params.getTipus())
                                ?codiSia :null),
                        (RegistreClassificarTipusEnum.SERVEI.equals(params.getTipus())
                                ?codiSia :null),
                        null);

                Map<String, String> mssg = new HashMap<>();
                switch (resultat.getResultat()) {
                    case SENSE_CANVIS:
                        break;
                    case REGLA_BUSTIA:
                    case REGLA_UNITAT:
                        mssg.put("severity", "info");
                        mssg.put("text", I18nUtil.getInstance().getI18nMessage(
                                "bustia.controller.pendent.contingut.classificat.mogut",
                                resultat.getBustiaNom(),
                                resultat.getBustiaUnitatOrganitzativa().getDenominacio() ));
                        break;
                    case REGLA_BACKOFFICE:
                        mssg.put("severity", "info");
                        mssg.put("text", I18nUtil.getInstance().getI18nMessage(
                                "bustia.controller.pendent.contingut.classificat.backoffice",
                                resultat.getBackofficeDesti() ));
                        break;
                    case REGLA_ERROR:
                        mssg.put("severity", "warning");
                        mssg.put("text", I18nUtil.getInstance().getI18nMessage(
                                "bustia.controller.pendent.contingut.classificat.error" ));
                        break;
                    case TITOL_MODIFICAT:
                        mssg.put("severity", "info");
                        mssg.put("text", I18nUtil.getInstance().getI18nMessage(
                                "bustia.controller.pendent.contingut.classificat.titol" ));
                        break;
                }
                if (!mssg.isEmpty())
                    map.put("message", mssg);

                map.put("numero", registre.getNumero());
                if (RegistreClassificarTipusEnum.PROCEDIMENT.equals( params.getTipus() )) {
                    map.put("tipus", RegistreClassificarTipusEnum.PROCEDIMENT.name());
                    map.put("sia", params.getProcediment().getDescription());
                }
                if (RegistreClassificarTipusEnum.SERVEI.equals( params.getTipus() )) {
                    map.put("tipus", RegistreClassificarTipusEnum.SERVEI.name());
                    map.put("sia", params.getProcediment().getDescription());
                }
                return new HashMap<>(map);
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, RegistreResource.ClassificarForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, RegistreResource.ClassificarForm target) {
            if (fieldName == null) {
                if (previous.isMassive()) {
                    if (previous.getIds() != null && !previous.getIds().isEmpty()) {
                        List<RegistreResourceEntity> entityList = registreResourceRepository.findAllById(previous.getIds());
                        if (this.isSameBustia(entityList)) {
                            target.setBustiaId(entityList.get(0).getPare().getId());
                        } else {
                            if (!answers.containsKey(ERROR_DIFERENT_PARE_CODE))
                                throw new AnswerRequiredException(RegistreResource.ClassificarForm.class, ERROR_DIFERENT_PARE_CODE,
                                        I18nUtil.getInstance().getI18nMessage("bustia.controller.pendent.contingut.classificar.no.mateix.pare.error"));
                        }
                    } else {
                        if (!answers.containsKey(ERROR_IDS_EMPTY_CODE))
                            throw new AnswerRequiredException(RegistreResource.ClassificarForm.class, ERROR_IDS_EMPTY_CODE,
                                    I18nUtil.getInstance().getI18nMessage("bustia.controller.pendent.contingut.classificar.seleccio.buida"));
                    }
                } else {
                    Long entitatActualId = SessioActualUtil.getEntitatId();
                    RegistreResourceEntity registre = registreResourceRepository.findById(previous.getIds().get(0)).get();

                    target.setBustiaId(registre.getPare().getId());

                    if (registre.getProcedimentCodi() != null) {
                        target.setTipus(RegistreClassificarTipusEnum.PROCEDIMENT);
                        procedimentResourceRepository.findByEntitatIdAndCodiSia(entitatActualId, registre.getProcedimentCodi())
                                .ifPresent(procediment -> {
                                    target.setProcediment(ResourceReference.toResourceReference(
                                            procediment.getId(), procediment.getCodiSia() + " - " + procediment.getNom()
                                    ));
                                });
                    }
                    if (registre.getServeiCodi() != null) {
                        target.setTipus(RegistreClassificarTipusEnum.SERVEI);
                        serveiResourceRepository.findByEntitatIdAndCodiSia(entitatActualId, registre.getServeiCodi())
                                .ifPresent(servei -> {
                                    target.setServei(ResourceReference.toResourceReference(
                                            servei.getId(), servei.getCodiSia() + " - " + servei.getNom()
                                    ));
                                });
                    }
                }
            }
        }
    }
    protected class EnviarEmailActionExecutor implements ActionExecutor<RegistreResourceEntity, RegistreResource.EnviarEmailForm, HashMap> {

        @Override
        public HashMap exec(String code, RegistreResourceEntity entity, RegistreResource.EnviarEmailForm params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            String adreces = Arrays.stream(params.getDestinatari()
                            .replaceAll("\\s*,\\s*|\\s+", ",").split(","))
                    .distinct()
                    .collect(Collectors.joining(","));

            if (params.isMassive()) {
                List<RegistreResourceEntity> registreList = registreResourceRepository.findAllById(params.getIds());
                /// TODO: implementar versión massiva
            } else {
                RegistreResourceEntity registre = registreResourceRepository.findById(params.getIds().get(0)).get();
                RegistreResource registreResource = objectMappingHelper.newInstanceMap(registre, RegistreResource.class);

                afterConversion(List.of(registre), List.of(registreResource));

                if (RegistreProcesEstatEnum.ARXIU_PENDENT.equals( registreResource.getProcesEstat() ) && !registreResource.isReintentsEsgotat()) {
                    throw new ActionExecutionException(
                            RegistreResource.class,
                            null, code,
                            I18nUtil.getInstance().getI18nMessage("bustia.controller.pendent.contingut.enviar.email.validacio.estat")
                    );
                }

                try {
                    /// TODO: revisar versión individual
                    bustiaService.registreAnotacioEnviarPerEmail(
                            entitatActualId,
                            params.getIds().get(0),
                            adreces,
                            params.getMotiu(),
                            false,
                            authenticationHelper.getCurrentUserRoles()[0]
                    );

                    Map<String, String> map = new HashMap<>();
                    map.put("numero", registre.getNumero());
                    return new HashMap<>(map);
                } catch (Exception e) {
                    throw new ActionExecutionException(
                            RegistreResource.class,
                            null, code,
                            "S'ha produit un error al intentar enviar correu: " + e.getMessage()
                    );
                }
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, RegistreResource.EnviarEmailForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, RegistreResource.EnviarEmailForm target) {
        }
    }
    protected class ReenviarActionExecutor implements ActionExecutor<RegistreResourceEntity, RegistreResource.ReenviarForm, HashMap> {

        private boolean isConeixementActiva(){
            return Boolean.parseBoolean( aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.enviar.coneixement") );
        }

        @Override
        public HashMap exec(String code, RegistreResourceEntity entity, RegistreResource.ReenviarForm params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            if (params.isMassive()) {
                List<RegistreResourceEntity> registreList = registreResourceRepository.findAllById(params.getIds());
                /// TODO: implementar versión massiva
            } else {
                RegistreResourceEntity registre = registreResourceRepository.findById(params.getIds().get(0)).get();
                /// TODO: revisar versión individual
//                bustiaService.registreReenviar(
//                        entitatActualId,
//                        params.getBusties().toArray(Long[]::new),
//                        registre.getId(),
//                        params.isAmbCopia(),
//                        params.getComentari(),
//                        params.getConeixement().toArray(Long[]::new),
////                        params.getDestinsUsuari(),
//                        new HashMap<>(),
//                        null);

                Map<String, String> map = new HashMap<>();
                map.put("numero", registre.getNumero());
                return new HashMap<>(map);
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, RegistreResource.ReenviarForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, RegistreResource.ReenviarForm target) {
            if (fieldName == null) {
                target.setConeixementActiva( this.isConeixementActiva() );
            }
        }
    }
    protected class MarcarProcessadaActionExecutor implements ActionExecutor<RegistreResourceEntity, RegistreResource.MarcarForm, HashMap> {

        @Override
        public HashMap exec(String code, RegistreResourceEntity entity, RegistreResource.MarcarForm params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            if (params.isMassive()) {
                List<RegistreResourceEntity> registreList = registreResourceRepository.findAllById(params.getIds());
                /// TODO: revisar versión massiva
                Map<String, Object> map = new HashMap<>();
                map.put("motiu", params.getMotiu());

                execucioMassivaResourceHelper.executarAccioMassivaRegistres(
                        ExecucioMassivaTipusDto.MARCAR_PROCESSAT,
                        registreList,
                        map);
            } else {
                RegistreResourceEntity registre = registreResourceRepository.findById(params.getIds().get(0)).get();
                /// TODO: revisar versión individual
                contingutService.marcarProcessat(
                        entitatActualId,
                        registre.getId(),
                        "<span class='label label-default'>" +
                                I18nUtil.getInstance().getI18nMessage(
                                        "bustia.pendent.accio.marcat.processat") +
                                "</span> " + params.getMotiu(),
                        authenticationHelper.getCurrentUserRoles()[0]);

                Map<String, String> map = new HashMap<>();
                map.put("numero", registre.getNumero());
                return new HashMap<>(map);
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, RegistreResource.MarcarForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, RegistreResource.MarcarForm target) {

        }
    }
    protected class MarcarPendentActionExecutor implements ActionExecutor<RegistreResourceEntity, RegistreResource.MarcarForm, HashMap> {

        @Override
        public HashMap exec(String code, RegistreResourceEntity entity, RegistreResource.MarcarForm params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            if (params.isMassive()) {
                List<RegistreResourceEntity> registreList = registreResourceRepository.findAllById(params.getIds());
                /// TODO: revisar versión massiva
                Map<String, Object> map = new HashMap<>();
                map.put("motiu", params.getMotiu());

                execucioMassivaResourceHelper.executarAccioMassivaRegistres(
                        ExecucioMassivaTipusDto.MARCAR_PENDENT,
                        registreList,
                        map);
            } else {
                RegistreResourceEntity registre = registreResourceRepository.findById(params.getIds().get(0)).get();
                /// TODO: revisar versión individual
                registreService.marcarPendent(
                        entitatActualId,
                        registre.getId(),
                        "<span class='label label-default'>" +
                                I18nUtil.getInstance().getI18nMessage(
                                        "registre.user.controller.accio.marcat.pendent") +
                                "</span> " + params.getMotiu(),
                        authenticationHelper.getCurrentUserRoles()[0]);

                Map<String, String> map = new HashMap<>();
                map.put("numero", registre.getNumero());
                return new HashMap<>(map);
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, RegistreResource.MarcarForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, RegistreResource.MarcarForm target) {

        }
    }
}