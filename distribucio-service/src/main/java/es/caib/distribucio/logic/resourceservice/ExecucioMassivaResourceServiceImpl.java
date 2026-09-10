package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.ReportGenerationException;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.model.ExecucioMassivaResource;
import es.caib.distribucio.logic.intf.resourceservice.ExecucioMassivaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity.ExecucioMassivaContingutResourceEntity;
import es.caib.distribucio.persist.resourceentity.ExecucioMassivaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.ExecucioMassivaResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecucioMassivaResourceServiceImpl extends BaseMutableResourceService<ExecucioMassivaResource, Long, ExecucioMassivaResourceEntity> implements ExecucioMassivaResourceService {

    private final AuthenticationHelper authenticationHelper;
    private final ConfigHelper configHelper;
    private final ExecucioMassivaResourceRepository execucioMassivaResourceRepository;

    @PostConstruct
    public void init() {
        register(ExecucioMassivaResource.ACTION_CANVI_ESTAT_CODE, new CanviEstatActionExecutor());
        register(ExecucioMassivaResource.REPORT_DOWNLOAD_CODE, new DownloadReportGenerator());
    }

    @Override
    protected Specification<ExecucioMassivaResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();

//        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }

            if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER)) {
                predicates.add(cb.equal(root.get("createdBy"), authenticationHelper.getCurrentUserName()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private double getPercent(int value, int total) {
        if (total == 0)
            return 100L;
        else if (value == 0L)
            return 0L;
        return Math.round((float) (value * 100) / total);
    }

    @Override
    protected void afterConversion(ExecucioMassivaResourceEntity entity, ExecucioMassivaResource resource) {
        int total = entity.getContinguts().size();
        int errors = (int) entity.getContinguts().stream()
                .filter(c -> ExecucioMassivaContingutEstatDto.ERROR.equals(c.getEstat()))
                .count();
        int pendents = (int) entity.getContinguts().stream()
                .filter(c -> c.getDataFi() == null)
                .count();

        resource.setExec(total);
        resource.setErrors(errors);
        resource.setPercExec( this.getPercent((total - pendents), total) );
    }

    private class CanviEstatActionExecutor implements ActionExecutor<ExecucioMassivaResourceEntity, ExecucioMassivaAccioDto, Serializable> {

        @Override
        public Serializable exec(String code, ExecucioMassivaResourceEntity entity, ExecucioMassivaAccioDto estat) throws ActionExecutionException {
            if (!ExecucioMassivaEstatDto.FINALITZADA.equals(entity.getEstat())) {
                ExecucioMassivaEstatDto e = null;

                switch (estat) {
                    case REPRENDRE: e = ExecucioMassivaEstatDto.PENDENT; break;
                    case PAUSAR: e = ExecucioMassivaEstatDto.PAUSADA; break;
                    case CANCELAR: e = ExecucioMassivaEstatDto.CANCELADA; break;
                }

                if (e != null) {
                    entity.setEstat(e);
                    for (ExecucioMassivaContingutResourceEntity emc : entity.getContinguts()) {
                        if (emc.getError() == null
                                && !ExecucioMassivaContingutEstatDto.FINALITZADA.equals(emc.getEstat()))
                            emc.setEstat(ExecucioMassivaContingutEstatDto.valueOf(e.name()));
                    }
                    execucioMassivaResourceRepository.save(entity);
                }
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExecucioMassivaAccioDto previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExecucioMassivaAccioDto target) {
        }
    }

    private class DownloadReportGenerator implements ReportGenerator<ExecucioMassivaResourceEntity, Serializable, ExecucioMassivaResource> {

        @Override
        public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
            ExecucioMassivaResource execucioMassiva = ((List<ExecucioMassivaResource>) data).get(0);
            DownloadableFile resultat = new DownloadableFile(null, null, null);

            try {
                String directoriDesti = configHelper.getConfig("es.caib.distribucio.fitxers");
                String nomDocument = execucioMassiva.getNomDocument();

                byte[] bytes = Files.readAllBytes(Paths.get(directoriDesti + nomDocument));
                resultat.setContent(bytes);
                if (nomDocument.contains("/")) {
                    resultat.setName(
                            nomDocument.substring(nomDocument.lastIndexOf("/") + 1));
                } else {
                    resultat.setName(nomDocument);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return resultat;
        }

        @Override
        public List<ExecucioMassivaResource> generateData(String code, ExecucioMassivaResourceEntity entity, Serializable params) throws ReportGenerationException {
            return List.of(objectMappingHelper.newInstanceMap(entity, ExecucioMassivaResource.class));
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

        }
    }
}