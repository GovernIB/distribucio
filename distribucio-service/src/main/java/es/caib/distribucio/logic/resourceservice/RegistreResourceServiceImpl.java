package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ContingutHelper;
import es.caib.distribucio.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.distribucio.logic.intf.base.model.FieldOption;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.RegistreNombreAnnexesEnumDto;
import es.caib.distribucio.logic.intf.model.*;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.distribucio.logic.intf.resourceservice.ContingutMovimentResourceService;
import es.caib.distribucio.logic.intf.resourceservice.RegistreResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.resourceentity.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.*;
import java.io.Serializable;
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

    @PostConstruct
    public void init() {
        register(RegistreResource.PERSPECTIVE_DARRER_MOVIMENT_CODE, new DarrerMovimentPerspectiveApplicator());
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
}