package es.caib.distribucio.logic.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotDeletedException;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ReglaResource;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.resourceservice.ReglaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.ReglaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.ReglaResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del recurs de regles.
 * <p>
 * Les accions ACTIVAR/DESACTIVAR i ACCIO_MASSIVA ja tenen {@code ActionExecutor} registrat. La resta
 * d'accions declarades a {@link ReglaResource} (APLICAR_MANUALMENT, AMUNT, AVALL) encara no s'han
 * desenvolupat: si s'invoquessin, el motor genèric respon amb un {@code ArtifactNotFoundException}.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReglaResourceServiceImpl
        extends BaseMutableResourceService<ReglaResource, Long, ReglaResourceEntity>
        implements ReglaResourceService {

    private final ReglaResourceRepository reglaResourceRepository;
    private final EntitatResourceRepository entitatResourceRepository;
    private final RegistreRepository registreRepository;

    @PostConstruct
    public void init() {
        register(ReglaResource.Fields.tipus, new TipusOnChangeLogicProcessor());
        register(ReglaResource.Fields.tipusSia, new TipusSiaOnChangeLogicProcessor());
        ActivaActionExecutor activaActionExecutor = new ActivaActionExecutor();
        register(ReglaResource.ACTION_ACTIVAR_CODE, activaActionExecutor);
        register(ReglaResource.ACTION_DESACTIVAR_CODE, activaActionExecutor);
        register(ReglaResource.ACTION_ACCIO_MASSIVA_CODE, new AccioMassivaActionExecutor());
    }

    /** Només es veuen les regles de l'entitat actual. */
    @Override
    protected Specification<ReglaResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Estat de la unitat organitzativa de filtre, perquè el llistat pugui pintar l'avís d'obsoleta
     * sense necessitat de consultar-la a part.
     */
    @Override
    protected void afterConversion(ReglaResourceEntity entity, ReglaResource resource) {
        if (entity.getUnitatOrganitzativaFiltre() != null) {
            resource.setUnitatOrganitzativaFiltreEstat(entity.getUnitatOrganitzativaFiltre().getEstat());
        }
    }

    /**
     * Abans de desar una regla nova: assigna l'entitat actual de la sessió (com
     * {@code ProcedimentResourceServiceImpl.beforeCreateSave}) i calcula l'ordre com al final de la llista de l'entitat.
     */
    @Override
    protected void beforeCreateSave(
            ReglaResourceEntity entity,
            ReglaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        if (entity.getEntitat() == null) {
            Long entitatId = SessioActualUtil.getEntitatId();
            if (entitatId != null) {
                EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(entitatId);
                entity.setEntitat(entitat);
                entity.setOrdre(reglaResourceRepository.countByEntitat(entitat));
            }
        }
    }

    /**
     * Abans d'esborrar una regla real: allibera els registres que ja l'han aplicada
     * i bloqueja l'esborrat si encara hi ha registres pendents d'aplicar-la.
     */
    @Override
    protected void beforeDelete(
            ReglaResourceEntity entity,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        try {
            alliberarRegistresAbansEsborrar(entity.getId());
        } catch (IllegalStateException e) {
            throw new ResourceNotDeletedException(ReglaResource.class, String.valueOf(entity.getId()), e.getMessage());
        }
    }

    /**
     * Desassocia de la regla els registres que ja l'han aplicada, perquè es puguin esborrar.
     * Si encara hi ha registres pendents d'aplicar-la, no toca res i llança una excepció
     * (la regla no es pot esborrar mentre hi hagi feina pendent).
     */
    private void alliberarRegistresAbansEsborrar(Long reglaId) {
        List<RegistreEntity> registres = registreRepository.findByRegla_Id(reglaId);
        List<String> registresPendents = registres.stream().
                filter(registre -> RegistreProcesEstatEnum.REGLA_PENDENT.equals(registre.getProcesEstat())).
                map(RegistreEntity::getNumero).
                collect(Collectors.toList());
        if (!registresPendents.isEmpty()) {
            throw new IllegalStateException(
                    "No es pot esborrar la regla perquè hi ha " + registresPendents.size() +
                            " registres pendents de processar-la: " + registresPendents);
        }
        registres.forEach(RegistreEntity::removeRegla);
    }

    /** Activar/desactivar una regla */
    private class ActivaActionExecutor implements ActionExecutor<ReglaResourceEntity, Serializable, Serializable> {
        @Override
        public Serializable exec(String code, ReglaResourceEntity entity, Serializable params) throws ActionExecutionException {
            boolean activa = ReglaResource.ACTION_ACTIVAR_CODE.equals(code);
            try {
                entity.setActiva(activa);
            } catch (Exception e) {
                throw new ActionExecutionException(
                        ReglaResource.class,
                        entity.getId(),
                        code,
                        e.getMessage(),
                        e);
            }
            return null;
        }

        @Override
        public void onChange(
                Serializable id,
                Serializable previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                Serializable target) {
        }
    }

    /** Executa una acció massiva sobre múltiples regles. */
    private class AccioMassivaActionExecutor
            implements ActionExecutor<ReglaResourceEntity, ReglaResource.FormAccioMassiva, Serializable> {

        @Override
        public Serializable exec(
                String code,
                ReglaResourceEntity entity,
                ReglaResource.FormAccioMassiva params) throws ActionExecutionException {
            List<ReglaResourceEntity> reglaEntities = reglaResourceRepository.findAllById(params.getIds());

            if ("activar".equalsIgnoreCase(params.getAccio())) {
                reglaEntities.forEach(reglaEntity -> reglaEntity.setActiva(true));
                reglaResourceRepository.saveAll(reglaEntities);
            } else if ("desactivar".equalsIgnoreCase(params.getAccio())) {
                reglaEntities.forEach(reglaEntity -> reglaEntity.setActiva(false));
                reglaResourceRepository.saveAll(reglaEntities);
            } else if ("eliminar".equalsIgnoreCase(params.getAccio())) {
                for (Long reglaId : params.getIds()) {
                    try {
                        alliberarRegistresAbansEsborrar(reglaId);
                    } catch (IllegalStateException e) {
                        throw new ActionExecutionException(ReglaResource.class, reglaId, code, e.getMessage());
                    }
                }
                reglaResourceRepository.deleteAllById(params.getIds());
            } else {
                throw new ActionExecutionException(
                        ReglaResource.class,
                        null,
                        code,
                        "Tipus d'acció massiva desconegut: " + params.getAccio());
            }
            return null;
        }

        @Override
        public void onChange(
                Serializable id,
                ReglaResource.FormAccioMassiva previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ReglaResource.FormAccioMassiva target) {
        }
    }

    private class TipusOnChangeLogicProcessor implements OnChangeLogicProcessor<ReglaResource> {
        @Override
        public void onChange(
                Serializable id,
                ReglaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ReglaResource target) {
            if (ReglaTipusEnumDto.BACKOFFICE.equals(fieldValue)) {
                target.setBustiaFiltre(null);
                target.setAssumpteCodiFiltre(null);
            }
        }
    }

    /**
     * "tipusSia" és un camp només de formulari (no es desa) que decideix si el filtre per codi SIA
     * es fa per procediment o per servei: en canviar-lo es buida el camp que ha deixat d'aplicar.
     */
    private class TipusSiaOnChangeLogicProcessor implements OnChangeLogicProcessor<ReglaResource> {
        @Override
        public void onChange(
                Serializable id,
                ReglaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ReglaResource target) {
            if (RegistreClassificarTipusEnum.PROCEDIMENT.equals(fieldValue)) {
                target.setServeiCodiFiltre(null);
            } else if (RegistreClassificarTipusEnum.SERVEI.equals(fieldValue)) {
                target.setProcedimentCodiFiltre(null);
            }
        }
    }

}
