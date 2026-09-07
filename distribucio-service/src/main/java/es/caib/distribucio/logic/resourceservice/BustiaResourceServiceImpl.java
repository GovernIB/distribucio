package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.PermisosHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.ReportGenerationException;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganizzativaEstatEnumDto;
import es.caib.distribucio.logic.intf.model.BustiaResource;
import es.caib.distribucio.logic.intf.model.ContingutResource;
import es.caib.distribucio.logic.intf.resourceservice.BustiaResourceService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.BustiaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UnitatOrganitzativaResourceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
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

    @PostConstruct
    public void init() {
        register(BustiaResource.PERSPECTIVE_PERMISOS_COUNT_CODE, new PermisosCountPerspectiveApplicator());
        ActivaActionExecutor activaActionExecutor = new ActivaActionExecutor();
        register(BustiaResource.ACTION_ACTIVAR_CODE, activaActionExecutor);
        register(BustiaResource.ACTION_DESACTIVAR_CODE, activaActionExecutor);
        register(BustiaResource.ACTION_PRINCIPAL_CODE, new PrincipalActionExecutor());
        register(BustiaResource.ACTION_MOURE_ANOTACIO_CODE, new MoureAnotacioActionExecutor());
        register(BustiaResource.REPORT_USUARIS_BUSTIA_CODE, new UsuarisBustiaReportGenerator());
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
    protected void beforeUpdateSave(BustiaResourceEntity entity, BustiaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
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

            if (mapaNamedQueries.containsKey("PERMIS_PER_USUARI")) {
                // TODO: implementar filtro por numero de permisos
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
            if (!authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
                return true;
            }
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

    private class ActivaActionExecutor implements ActionExecutor<BustiaResourceEntity, Serializable, Serializable> {

        @Override
        public Serializable exec(String code, BustiaResourceEntity entity, Serializable params) throws ActionExecutionException {
            boolean activa = BustiaResource.ACTION_ACTIVAR_CODE.equals(code);
            Long entitatActualId = SessioActualUtil.getEntitatId();
            try {
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
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
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

                Map<Long, List<PermisDto>> permisosPerEntitat = permisosHelper.findPermisos(
                        bustiaResources.stream().map(BaseAuditableResource::getId).collect(Collectors.toList()),
                        BustiaEntity.class);

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
                        for (PermisDto permis : permisosPerEntitat.get(bustia.getId())) {
                            if (PrincipalTipusEnumDto.USUARI.equals(permis.getPrincipalTipus())) {
                                Row row1 = sheet.createRow(rowNum++);
                                row1.createCell(0).setCellValue(bustia.getUnitatOrganitzativa().getDescription());
                                row1.createCell(1).setCellValue(bustia.getNom());
                                row1.createCell(2).setCellValue(permis.getPrincipalNom());
                                row1.createCell(3).setCellValue(permis.getPrincipalDescripcio());
                            }
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
}