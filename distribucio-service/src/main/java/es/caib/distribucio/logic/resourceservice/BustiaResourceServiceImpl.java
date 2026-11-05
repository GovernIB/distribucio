package es.caib.distribucio.logic.resourceservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.*;
import es.caib.distribucio.logic.intf.base.exception.*;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.FieldOption;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.model.BustiaResource;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.logic.intf.resourceservice.BustiaResourceService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.entity.*;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.resourceentity.*;
import es.caib.distribucio.persist.resourcerepository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BustiaResourceServiceImpl extends BaseMutableResourceService<BustiaResource, Long, BustiaResourceEntity> implements BustiaResourceService {

    private final AuthenticationHelper authenticationHelper;
    private final PermisosHelper permisosHelper;
    private final UnitatOrganitzativaResourceRepository unitatOrganitzativaResourceRepository;
    private final BustiaResourceRepository bustiaResourceRepository;
    private final BustiaService bustiaService;
    private final UsuariResourceRepository usuariResourceRepository;
    private final UnitatOrganitzativaHelper unitatOrganitzativaHelper;
    private final AclResourceHelper aclResourceHelper;
    private final BustiaHelper bustiaHelper;
    private final EntitatRepository entitatRepository;
    private final ContingutResourceRepository contingutResourceRepository;
    private final ReglaResourceRepository reglaResourceRepository;
    private final ContingutLogResourceHelper contingutLogResourceHelper;

    @PostConstruct
    public void init() {
        register(BustiaResource.PERSPECTIVE_PERMISOS_COUNT_CODE, new PermisosCountPerspectiveApplicator());
        register(BustiaResource.PERSPECTIVE_FAVORITA_CODE, new FavoritaPerspectiveApplicator());
        register(BustiaResource.PERSPECTIVE_USUARIS_PERMIS_CODE, new UsuarisPermisPerspectiveApplicator());
        ActivaActionExecutor activaActionExecutor = new ActivaActionExecutor();
        register(BustiaResource.ACTION_ACTIVAR_CODE, activaActionExecutor);
        register(BustiaResource.ACTION_DESACTIVAR_CODE, activaActionExecutor);
        register(BustiaResource.ACTION_PRINCIPAL_CODE, new PrincipalActionExecutor());
        register(BustiaResource.ACTION_MOURE_ANOTACIO_CODE, new MoureAnotacioActionExecutor());
        register(BustiaResource.ACTION_TOOGLE_FAVORITA_CODE, new FavotitaActionExecutor());
        register(BustiaResource.REPORT_USUARIS_BUSTIA_CODE, new UsuarisBustiaReportGenerator());
        register(BustiaResource.REPORT_TRANSICIO_INFO_CODE, new TransicioInfoReportGenerator());
    }

    private void beforeSave(BustiaResourceEntity entity, BustiaResource resource) {
        BustiaResourceEntity pare = bustiaResourceRepository.findByEntitatAndUnitatOrganitzativaAndPareNull(
                entity.getEntitat(), entity.getUnitatOrganitzativa());

        if (pare == null) {
            pare = new BustiaResourceEntity();
            pare.setEntitat(entity.getEntitat());
            pare.setNom(entity.getUnitatOrganitzativa().getDenominacio());
            pare.setUnitatOrganitzativa(entity.getUnitatOrganitzativa());
            bustiaResourceRepository.save(pare);
        }
        entity.setPare(pare);
    }

    @Override
    protected void beforeCreateSave(BustiaResourceEntity entity, BustiaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
    }

    @Override
    protected void afterCreateSave(BustiaResourceEntity entity, BustiaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
        contingutLogResourceHelper.logCreacio(entity, false);

        if (!this.hasAlternativaPerDefecte(entity)) {
            entity.setPerDefecte(true);

            List<String> params = new ArrayList<>();
            params.add("true");

            contingutLogResourceHelper.log(
                    entity,
                    LogTipusEnumDto.PER_DEFECTE,
                    params,
                    false);
        }
    }

    @Override
    protected void beforeUpdateEntity(BustiaResourceEntity entity, BustiaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        Long entitatActualId = SessioActualUtil.getEntitatId();
        EntitatEntity entitat = entitatRepository.findById(entitatActualId).get();

        boolean unitatChanged = !Objects.equals(entity.getUnitatOrganitzativa().getId(), resource.getUnitatOrganitzativa().getId());

        if (unitatChanged) {
            UnitatOrganitzativaResourceEntity unitatDesti = unitatOrganitzativaResourceRepository.findById(resource.getUnitatOrganitzativa().getId()).get();
            boolean bustiaPerDefecteInUnitatDesti = bustiaHelper.findBustiaPerDefecte(entitat, unitatDesti.getCodi()) != null;
            resource.setPerDefecte(!bustiaPerDefecteInUnitatDesti);

            if (entity.isPerDefecte()) {
                UnitatOrganitzativaResourceEntity unitatOrganitzativaSource = entity.getUnitatOrganitzativa();
                boolean unitatSourceObsoleta = !UnitatOrganizzativaEstatEnumDto.V.equals(unitatOrganitzativaSource.getEstat());
                if (!unitatSourceObsoleta && !this.hasAlternativaPerDefecte(entity)) {
                    String missatgeError = "No es pot moure la bústia principal si no n'hi ha cap altra superior definida com principal (" +
                            "bustiaId=" + entity.getId() + ", " +
                            "unitatOrganitzativaCodi=" + entity.getUnitatOrganitzativa().getCodi() + ")";
                    throw new ValidationException(
                            entity.getId(),
                            BustiaEntity.class,
                            missatgeError);
                }
            }
        }

        List<String> params = new ArrayList<>();
        if (!Objects.equals(entity.getNom(), resource.getNom()))
            params.add(resource.getNom());
        if (unitatChanged)
            params.add(resource.getUnitatOrganitzativa().getDescription());

        contingutLogResourceHelper.log(
                entity,
                LogTipusEnumDto.MODIFICACIO,
                params,
                false);
    }

    @Override
    protected void beforeUpdateSave(BustiaResourceEntity entity, BustiaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
    }

    @Override
    protected void beforeDelete(BustiaResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotDeletedException {
        // Valida que si s'esborra encara hi hagi una altra principal d'alternativa
         if (entity.isPerDefecte() && !this.hasAlternativaPerDefecte(entity)) {
            String missatgeError = "No es pot esborrar la bústia principal si no n'hi ha cap altra superior definida com principal (" +
                    "bustiaId=" + entity.getId() + ", " +
                    "unitatOrganitzativaCodi=" + entity.getUnitatOrganitzativa().getCodi() + ")";
            throw new ValidationException(
                    entity.getId(),
                    BustiaEntity.class,
                    missatgeError);
        }
        // cannot remove busties containing any anotacions
        List<ContingutResourceEntity> continguts = contingutResourceRepository.findAllByPareId(entity.getId());
        if (continguts != null && !continguts.isEmpty()) {
            String missatgeError = "No es pot esborrar la bústia amb anotacions a dins (" +
                    "bustiaId=" + entity.getId() + ")";
            throw new ValidationException(
                    entity.getId(),
                    BustiaEntity.class,
                    missatgeError);
        }

        // cannot remove busties with regles
        List<ReglaResourceEntity> reglesBustiaDesti = reglaResourceRepository.findAllByBustiaDestiId(entity.getId());
        List<ReglaResourceEntity> reglesBustiaFiltre = reglaResourceRepository.findAllByBustiaFiltreId(entity.getId());
        if ((reglesBustiaDesti != null && !reglesBustiaDesti.isEmpty()) || (reglesBustiaFiltre != null && !reglesBustiaFiltre.isEmpty())) {
            String missatgeError = I18nUtil.getInstance().getI18nMessage("bustia.service.esborrat.error.regles", new Object[] { entity.getId() });
            throw new ValidationException(
                    entity.getId(),
                    BustiaEntity.class,
                    missatgeError);
        }
    }

    private boolean hasAlternativaPerDefecte(BustiaResourceEntity bustia) {
        Long entitatActualId = SessioActualUtil.getEntitatId();
        EntitatEntity entitat = entitatRepository.findById(entitatActualId).get();

        BustiaEntity bustiaPerDefecteAlternativa = null;
        List<UnitatOrganitzativaDto> path = unitatOrganitzativaHelper.findPath(
                entitat.getCodiDir3(),
                bustia.getUnitatOrganitzativa().getCodi());
        if (path != null && !path.isEmpty()) {
            BustiaEntity bustiaAux;
            for (UnitatOrganitzativaDto unitat: path) {
                bustiaAux = bustiaHelper.findBustiaPerDefecte(
                        entitat,
                        unitat.getCodi());
                if (bustiaAux != null && ! bustiaAux.getId().equals(bustia.getId())) {
                    bustiaPerDefecteAlternativa = bustiaAux;
                    break;
                }
            }
        }
        return bustiaPerDefecteAlternativa != null;
    }

    @Override
	protected Specification<BustiaResourceEntity> additionalSpecification(String[] namedQueries) {
		// S'exclou la bústia arrel de cada unitat organitzativa (pare_id null), que és un
		// element merament tècnic i mai s'ha de gestionar directament (veure BustiaRepository,
		// que aplica sempre el mateix filtre a l'equivalent JSP).
        Long entitatActualId = SessioActualUtil.getEntitatId();

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNotNull(root.get("pare").get("id")));

            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }

            /// Permisos (amb tots els rols de l'usuari, no només el rol amb què opera)
            if ( mapaNamedQueries.containsKey("WITH_PERMISION") ) {
                Set<Serializable> ids = aclResourceHelper.findIdsWithAnyPermission(
                        ResourceType.BUSTIA,
                        List.of(PermissionEnum.READ));

                if (ids.isEmpty()) {
                    return cb.disjunction();
                }

                int chunkSize = 900;
                List<Predicate> orPredicates = new ArrayList<>();
                for (int i = 0; i < ids.size(); i += chunkSize) {
                    List<Serializable> chunk = new ArrayList<>(ids).subList(i, Math.min(i + chunkSize, ids.size()));
                    orPredicates.add(root.get("id").in(chunk));
                }

                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
            }

            if (mapaNamedQueries.containsKey("PERMIS_PER_USUARI")) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<AclEntryEntity> entryRoot = subquery.from(AclEntryEntity.class);

                Join<AclEntryEntity, AclSidEntity> sidJoin = entryRoot.join("aclSid");

                subquery.select(cb.countDistinct(sidJoin.get("id")));
                subquery.where(
                        cb.equal(entryRoot.get("aclObjectIdentity").get("objectIdIdentity"), root.get("id"))
//                        cb.equal(sidJoin.get("principal"), true) // Solo usuarios, no roles
                );

                predicates.add( cb.greaterThan(subquery, 1L) );
            }

            if (mapaNamedQueries.containsKey("UNITAT_SUPERIOR")) {
                String unitatId = mapaNamedQueries.get("UNITAT_SUPERIOR");
                List<Long> idsJerarquia = unitatOrganitzativaResourceRepository.findUnitatAndAllDescendentsIds(Long.valueOf(unitatId));
                if (idsJerarquia.isEmpty()) {
                    return cb.disjunction(); // No existe tal unidad, no devolver nada
                }

                int chunkSize = 900;
                List<Predicate> orPredicates = new ArrayList<>();

                for (int i = 0; i < idsJerarquia.size(); i += chunkSize) {
                    List<Long> chunk = idsJerarquia.subList(i, Math.min(i + chunkSize, idsJerarquia.size()));
                    orPredicates.add(root.get("unitatOrganitzativa").get("id").in(chunk));
                }

                predicates.add(cb.notEqual(root.get("id"), unitatId));
                predicates.add( cb.or(orPredicates.toArray(new Predicate[0])) );
            }

            if (mapaNamedQueries.containsKey("FAVORITA")) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<UsuariBustiaFavoritResourceEntity> favoritRoot = subquery.from(UsuariBustiaFavoritResourceEntity.class);

                subquery.select(favoritRoot.get("bustia").get("id"))
                        .where(cb.equal(favoritRoot.get("usuari").get("id"),
                                authenticationHelper.getCurrentUserName()));

                predicates.add( root.get("id").in(subquery) );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
	}

    @Override
    protected void afterConversion(BustiaResourceEntity entity, BustiaResource resource) {
        resource.setPendent(!UnitatOrganizzativaEstatEnumDto.V.equals(entity.getUnitatOrganitzativa().getEstat()));
    }

    private class PermisosCountPerspectiveApplicator implements PerspectiveApplicator<BustiaResourceEntity, BustiaResource> {

        @Override
        public boolean applyMultiple(
                String code,
                List<BustiaResourceEntity> entities,
                List<BustiaResource> resources) {
            List<Long> ids = entities.stream().
                    map(BustiaResourceEntity::getId).
                    collect(Collectors.toList());
            Map<Long, List<PermisDto>> permisosPerEntitat = permisosHelper.findPermisos(
                    ids,
                    BustiaEntity.class);
            IntStream.range(0, entities.size()).forEach(i -> {
                List<PermisDto> permisos = permisosPerEntitat.get(entities.get(i).getId());
                resources.get(i).setPermisosCount(permisos != null ? permisos.size() : 0);
            });
            return true;
        }

        @Override
        public void applySingle(
                String code,
                BustiaResourceEntity entity,
                BustiaResource resource) {
            applyMultiple(
                    code,
                    Collections.singletonList(entity),
                    Collections.singletonList(resource));
        }

    }
    private class FavoritaPerspectiveApplicator implements PerspectiveApplicator<BustiaResourceEntity, BustiaResource> {

        @Override
        public boolean applyMultiple(String code, List<BustiaResourceEntity> entities, List<BustiaResource> resources) {
            UsuariResourceEntity usuari = usuariResourceRepository.findById(
                    authenticationHelper.getCurrentUserName()).get();

            List<Long> favoriteId = usuari.getBustiesFavoritas().stream()
                    .map(ContingutResourceEntity::getId)
                    .collect(Collectors.toList());

            for (BustiaResource resource : resources) {
                resource.setFavorita( favoriteId.contains(resource.getId()) );
            }
            return true;
        }

        @Override
        public void applySingle(String code, BustiaResourceEntity entity, BustiaResource resource) {
            applyMultiple(code, Collections.singletonList(entity), Collections.singletonList(resource));
        }
    }
    private class UsuarisPermisPerspectiveApplicator implements PerspectiveApplicator<BustiaResourceEntity, BustiaResource> {
        @Override
        public void applySingle(String code, BustiaResourceEntity entity, BustiaResource resource) throws PerspectiveApplicationException {
            resource.setUsuarisPermis(bustiaService.getUsuarisPerBustia(entity.getId()));
        }
    }
    private class ActivaActionExecutor implements ActionExecutor<BustiaResourceEntity, BustiaResource.DesactivarForm, Serializable> {

        @Override
        public Serializable exec(String code, BustiaResourceEntity entity, BustiaResource.DesactivarForm params) throws ActionExecutionException {
            boolean activa = BustiaResource.ACTION_ACTIVAR_CODE.equals(code);
            Long entitatActualId = SessioActualUtil.getEntitatId();
            try {
                if (!activa && params.getBustia() != null) {
                    bustiaService.marcarPerDefecte(
                            entitatActualId,
                            params.getBustia().getId());
                }

                bustiaService.updateActiva(entitatActualId, entity.getId(), activa);
            } catch (Exception e) {
                throw new ActionExecutionException(
                        BustiaResource.class,
                        entity.getId(),
                        code,
                        e.getMessage()
                );
            }

            return null;
        }

        @Override
        public void onChange(Serializable id, BustiaResource.DesactivarForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, BustiaResource.DesactivarForm target) {
        }
    }
    private class PrincipalActionExecutor implements ActionExecutor<BustiaResourceEntity, Serializable, Serializable> {

        @Override
        public Serializable exec(String code, BustiaResourceEntity entity, Serializable params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            try {
                bustiaService.marcarPerDefecte(entitatActualId, entity.getId());
            } catch (Exception e) {
                throw new ActionExecutionException(
                        BustiaResource.class,
                        entity.getId(),
                        code,
                        e.getMessage()
                );
            }

            return null;
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
    }
    private class MoureAnotacioActionExecutor implements ActionExecutor<BustiaResourceEntity, BustiaResource.MoureAnotacioForm, Serializable> {

        @Override
        public Serializable exec(String code, BustiaResourceEntity entity, BustiaResource.MoureAnotacioForm params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            try {
                bustiaService.moureAnotacions(
                        entitatActualId,
                        entity.getId(),
                        params.getBustia().getId(),
                        params.getComment()
                );
            } catch (Exception e) {
                throw new ActionExecutionException(
                        BustiaResource.class,
                        entity.getId(),
                        code,
                        e.getMessage()
                );
            }

            return null;
        }

        @Override
        public void onChange(Serializable id, BustiaResource.MoureAnotacioForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, BustiaResource.MoureAnotacioForm target) {
        }
    }
    private class FavotitaActionExecutor implements ActionExecutor<BustiaResourceEntity, Boolean, Serializable> {

        @Override
        public Serializable exec(String code, BustiaResourceEntity entity, Boolean params) throws ActionExecutionException {
            UsuariResourceEntity usuari = usuariResourceRepository.findById(
                    authenticationHelper.getCurrentUserName()).get();

            if (Boolean.TRUE.equals(params)) {
                UsuariBustiaFavoritResourceEntity favorit = new UsuariBustiaFavoritResourceEntity();
                favorit.setUsuari(usuari);
                favorit.setBustia(entity);

                usuari.getFavorits().add(favorit);
            } else {
                // Buscar y eliminar la entidad intermedia correspondiente
                usuari.getFavorits().removeIf(fav -> fav.getBustia().getId().equals(entity.getId()));
            }

            usuariResourceRepository.save(usuari);
            return null;
        }

        @Override
        public void onChange(Serializable id, Boolean previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Boolean target) {

        }
    }
    private class UsuarisBustiaReportGenerator implements ReportGenerator<BustiaResourceEntity, BustiaResource.UsuariBustiaForm, BustiaResource> {

        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
            List<BustiaResource> bustiaResources = data.stream()
                    .map(b -> (BustiaResource) b)
                    .sorted(
                            Comparator.comparing((BustiaResource b) -> b.getUnitatOrganitzativa().getDescription())
                                    .thenComparing(BustiaResource::getNom)
                    )
                    .collect(Collectors.toList());
            try {

                Map<Long, List<UsuariPermisDto>> permisosPerEntitat = new HashMap<>();
                for (BustiaResource bustia : bustiaResources) {
                    permisosPerEntitat.put(bustia.getId(), bustiaService.getUsuarisPerBustia(bustia.getId()));
                }

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Hoja 1");

                // Crear cabecera

                Font bold = workbook.createFont();
                bold.setBold(true);
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
                headerStyle.setFont(bold);
                headerStyle.setWrapText(true);

                int headerNum = 0;
                Row headerRow = sheet.createRow(0);
                List<String> headers = List.of(
//                        "Codi unitat organitzativa",
//                        "Nom unitat organitzativa",
                        "Unitat organitzativa",
                        "Bustia",
                        "Codi usuari",
                        "Nom usuari"
                );
                for (String header : headers) {
                    Cell cell = headerRow.createCell(headerNum++);
                    cell.setCellValue(header);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                for (BustiaResource bustia : bustiaResources) {
                    if (permisosPerEntitat.containsKey(bustia.getId())) {
                        for (UsuariPermisDto permis : permisosPerEntitat.get(bustia.getId())) {
                            Row row1 = sheet.createRow(rowNum++);
                            row1.createCell(0).setCellValue(bustia.getUnitatOrganitzativa().getDescription());
                            row1.createCell(1).setCellValue(bustia.getNom());
                            row1.createCell(2).setCellValue(permis.getCodi());
                            row1.createCell(3).setCellValue(permis.getNom());
                        }
                    }
                }

                for (int i = 0; i < 5; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Convertir a byte[]
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                byte[] content = outputStream.toByteArray();

                return new DownloadableFile("UsuarisPerBustia.xls", "application/xls", content);
            } catch (Exception e) {
                throw new ReportGenerationException(
                        BustiaResource.class,
                        null,
                        code,
                        e.getMessage());
            }
        }

        @Override
        public List<BustiaResource> generateData(String code, BustiaResourceEntity entity, BustiaResource.UsuariBustiaForm params) throws ReportGenerationException {
            return findPage(null, params.getFilter(), params.getNamedQueries(), null, Pageable.unpaged()).getContent();
        }

        @Override
        public void onChange(Serializable id, BustiaResource.UsuariBustiaForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, BustiaResource.UsuariBustiaForm target) {
        }
    }
    private class TransicioInfoReportGenerator implements ReportGenerator<BustiaResourceEntity, Serializable, HashMap> {
        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

                byte[] jsonBytes = objectMapper.writeValueAsBytes(data.get(0));

                out.write(jsonBytes);
                out.flush();
                return new DownloadableFile("transicio_info", "application/json", jsonBytes);
            } catch (Exception e) {
                throw new RuntimeException("Error generant JSON", e);
            }
        }

        @Override
        public List<HashMap> generateData(String code, BustiaResourceEntity entity, Serializable params) throws ReportGenerationException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            Map<String, List<String>> map = new HashMap<>();

            BustiaDto bustia = bustiaService.findById(
                    entitatActualId,
                    entity.getId());
            map.put("newUnitats",
                    Optional.ofNullable(unitatOrganitzativaHelper.getLastHistoricos(bustia.getUnitatOrganitzativa()))
                            .map(UnitatOrganitzativaDto::getLastHistoricosUnitats)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(h -> h.getDenominacio() + " (" + h.getCodi() + ")")
                            .collect(Collectors.toList())
            );

            map.put("afectedBusties",
                    bustiaResourceRepository.findByEntitatIdAndUnitatOrganitzativaIdAndPareNotNull(
                                entitatActualId, entity.getUnitatOrganitzativa().getId() ).stream()
                            .filter(b -> !Objects.equals(b.getId(), entity.getId()))
                            .map(ContingutResourceEntity::getNom)
                            .collect(Collectors.toList())
            );

            return List.of((HashMap) map);
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
    }
}