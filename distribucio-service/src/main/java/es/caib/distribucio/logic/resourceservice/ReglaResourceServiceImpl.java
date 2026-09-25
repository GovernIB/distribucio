package es.caib.distribucio.logic.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ReglaValidacioHelper;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.distribucio.logic.helper.ReglaHelper;
import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotDeletedException;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ReglaResource;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.resourceservice.ReglaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.persist.repository.ReglaRepository;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.ReglaResourceEntity;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.ReglaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del recurs de regles.
 * <p>
 * Totes les accions declarades a {@link ReglaResource} (ACTIVAR/DESACTIVAR, ACCIO_MASSIVA, AMUNT/AVALL/MOURE i
 * APLICAR_MANUALMENT i SIMULAR) tenen el seu {@code ActionExecutor} registrat.
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
    private final ReglaValidacioHelper reglaValidacioHelper;
    private final RegistreRepository registreRepository;
    private final UsuariResourceRepository usuariResourceRepository;
    private final ReglaHelper reglaHelper;
    private final ReglaRepository reglaRepository;
    private final EntitatRepository entitatRepository;

    @PostConstruct
    public void init() {
        register(ReglaResource.Fields.tipus, new TipusOnChangeLogicProcessor());
        register(ReglaResource.Fields.tipusSia, new TipusSiaOnChangeLogicProcessor());
        ActivaActionExecutor activaActionExecutor = new ActivaActionExecutor();
        register(ReglaResource.ACTION_ACTIVAR_CODE, activaActionExecutor);
        register(ReglaResource.ACTION_DESACTIVAR_CODE, activaActionExecutor);
        register(ReglaResource.ACTION_ACCIO_MASSIVA_CODE, new AccioMassivaActionExecutor());
        AmuntAvallActionExecutor amuntAvallActionExecutor = new AmuntAvallActionExecutor();
        register(ReglaResource.ACTION_AMUNT_CODE, amuntAvallActionExecutor);
        register(ReglaResource.ACTION_AVALL_CODE, amuntAvallActionExecutor);
        register(ReglaResource.ACTION_MOURE_CODE, new MoureActionExecutor());
        register(ReglaResource.ACTION_APLICAR_MANUALMENT_CODE, new AplicarManualmentActionExecutor());
        register(ReglaResource.ACTION_SIMULAR_CODE, new SimularActionExecutor());
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
     * sense necessitat de consultar-la a part; i nom complet (codiAndNom) de qui ha creat/modificat
     * la regla, per al bloc d'auditoria del formulari .
     */
    @Override
    protected void afterConversion(ReglaResourceEntity entity, ReglaResource resource) {
        if (entity.getUnitatOrganitzativaFiltre() != null) {
            resource.setUnitatOrganitzativaFiltreEstat(entity.getUnitatOrganitzativaFiltre().getEstat());
        }
        resource.setCreatedByFullName(codiAndNom(entity.getCreatedBy()));
        resource.setLastModifiedByFullName(codiAndNom(entity.getLastModifiedBy()));
        if (entity.getEntitat() != null) {
            resource.setTotalRegles(reglaResourceRepository.countByEntitat(entity.getEntitat()));
        }
    }

    private String codiAndNom(String usuariCodi) {
        if (usuariCodi == null) {
            return null;
        }
        UsuariResourceEntity usuari = usuariResourceRepository.findById(usuariCodi).orElse(null);
        return usuari != null && usuari.getNom() != null ? usuari.getNom() + " (" + usuari.getId() + ")" : usuariCodi;
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
        renumerarOrdre(entity.getEntitat(), Collections.singleton(entity.getId()));
    }

    /**
     * Torna a numerar l'ordre (0..n-1, sense forats) de les regles de l'entitat, deixant fora les que s'estan esborrant.
     */
    private void renumerarOrdre(EntitatResourceEntity entitat, Collection<Long> excloureIds) {
        if (entitat == null) {
            return;
        }
        int ordre = 0;
        for (ReglaResourceEntity regla : reglaResourceRepository.findByEntitatOrderByOrdreAsc(entitat)) {
            if (!excloureIds.contains(regla.getId())) {
                regla.setOrdre(ordre++);
            }
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
            // Només es toquen les regles de l'entitat actual, igual que al llistat.
            Long entitatActualId = SessioActualUtil.getEntitatId();
            List<ReglaResourceEntity> reglaEntities = reglaResourceRepository.findAllById(params.getIds()).stream().
                    filter(regla -> entitatActualId == null ||
                            (regla.getEntitat() != null && entitatActualId.equals(regla.getEntitat().getId()))).
                    collect(Collectors.toList());

            if ("activar".equalsIgnoreCase(params.getAccio())) {
                reglaEntities.forEach(reglaEntity -> reglaEntity.setActiva(true));
                reglaResourceRepository.saveAll(reglaEntities);
            } else if ("desactivar".equalsIgnoreCase(params.getAccio())) {
                reglaEntities.forEach(reglaEntity -> reglaEntity.setActiva(false));
                reglaResourceRepository.saveAll(reglaEntities);
            } else if ("eliminar".equalsIgnoreCase(params.getAccio())) {
                List<Long> reglaIds = reglaEntities.stream().map(ReglaResourceEntity::getId).collect(Collectors.toList());
                for (Long reglaId : reglaIds) {
                    try {
                        alliberarRegistresAbansEsborrar(reglaId);
                    } catch (IllegalStateException e) {
                        throw new ActionExecutionException(ReglaResource.class, reglaId, code, e.getMessage());
                    }
                }
                if (!reglaEntities.isEmpty()) {
                    renumerarOrdre(reglaEntities.get(0).getEntitat(), reglaIds);
                    reglaResourceRepository.deleteAll(reglaEntities);
                }
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

    /** Mou la regla una posició amunt o avall */
    private class AmuntAvallActionExecutor implements ActionExecutor<ReglaResourceEntity, Serializable, Serializable> {
        @Override
        public Serializable exec(String code, ReglaResourceEntity entity, Serializable params) throws ActionExecutionException {
            int delta = ReglaResource.ACTION_AMUNT_CODE.equals(code) ? -1 : 1;
            List<ReglaResourceEntity> regles = reglaResourceRepository.findByEntitatOrderByOrdreAsc(entity.getEntitat());
            ReglaHelper.canviPosicio(regles, entity, entity.getOrdre() + delta, ReglaResourceEntity::setOrdre);
            reglaResourceRepository.saveAll(regles);
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

    /** Mou la regla a una posició absoluta concreta, usat per la reordenació per drag&drop del llistat. */
    private class MoureActionExecutor implements ActionExecutor<ReglaResourceEntity, ReglaResource.FormMoure, Serializable> {
        @Override
        public Serializable exec(String code, ReglaResourceEntity entity, ReglaResource.FormMoure params) throws ActionExecutionException {
            List<ReglaResourceEntity> regles = reglaResourceRepository.findByEntitatOrderByOrdreAsc(entity.getEntitat());
            ReglaHelper.canviPosicio(regles, entity, params.getPosicio(), ReglaResourceEntity::setOrdre);
            reglaResourceRepository.saveAll(regles);
            return null;
        }

        @Override
        public void onChange(
                Serializable id,
                ReglaResource.FormMoure previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ReglaResource.FormMoure target) {
        }
    }

    /**
     * Aplicar manualment: assigna la regla a les anotacions pendents de bústia que compleixen el seu filtre
     * (les deixa en REGLA_PENDENT) perquè la tasca en segon pla les processi. No mou res immediatament.
     * La consulta de les anotacions afectades és la mateixa que fa servir la previsualització (ReglaHelper).
     */
    private class AplicarManualmentActionExecutor implements ActionExecutor<ReglaResourceEntity, Serializable, HashMap> {
        @Override
        public HashMap exec(String code, ReglaResourceEntity entity, Serializable params) throws ActionExecutionException {
            if (!entity.isActiva()) {
                throw new ActionExecutionException(
                        ReglaResource.class,
                        entity.getId(),
                        code,
                        I18nUtil.getInstance().getI18nMessage("regla.controller.aplicada.errorInactiva"));
            }
            try {
                EntitatEntity entitat = entitatRepository.getReferenceById(entity.getEntitat().getId());
                ReglaEntity regla = reglaRepository.getReferenceById(entity.getId());
                List<String> numeros = reglaHelper.aplicarManualment(entitat, regla);
                HashMap<String, Object> resultat = new HashMap<>();
                resultat.put("count", numeros.size());
                return resultat;
            } catch (Exception e) {
                throw new ActionExecutionException(
                        ReglaResource.class,
                        entity.getId(),
                        code,
                        e.getMessage(),
                        e);
            }
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

    /**
     * Simulador de regles: calcula, sense modificar cap dada, què passaria amb una anotació hipotètica.
     * Es simula sempre contra l'entitat de la sessió.
     */
    private class SimularActionExecutor
            implements ActionExecutor<ReglaResourceEntity, ReglaResource.FormSimular, ArrayList<ReglaResource.SimulacioAccio>> {
        @Override
        public ArrayList<ReglaResource.SimulacioAccio> exec(
                String code,
                ReglaResourceEntity entity,
                ReglaResource.FormSimular params) throws ActionExecutionException {
            try {
                EntitatEntity entitat = entitatRepository.getReferenceById(SessioActualUtil.getEntitatId());
                RegistreSimulatDto dto = new RegistreSimulatDto();
                dto.setUnitatId(params.getUnitat().getId());
                dto.setBustiaId(params.getBustia() != null ? params.getBustia().getId() : null);
                dto.setProcedimentCodi(params.getProcedimentCodi());
                dto.setServeiCodi(params.getServeiCodi());
                dto.setTramitCodi(params.getTramitCodi());
                dto.setAssumpteCodi(params.getAssumpteCodi());
                dto.setPresencial(params.getPresencial());
                return reglaHelper.simular(entitat, dto).stream().
                        map(accio -> new ReglaResource.SimulacioAccio(
                                accio.getAccion(),
                                accio.getParam(),
                                accio.getReglaNom())).
                        collect(Collectors.toCollection(ArrayList::new));
            } catch (Exception e) {
                throw new ActionExecutionException(
                        ReglaResource.class,
                        null,
                        code,
                        e.getMessage(),
                        e);
            }
        }

        @Override
        public void onChange(
                Serializable id,
                ReglaResource.FormSimular previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ReglaResource.FormSimular target) {
            // Onchange inicial (en obrir el formulari): informa de la propietat "avaluar totes les regles".
            if (fieldName == null) {
                target.setAvaluarTotes(reglaHelper.isAvaluarTotesLesRegles());
            }
        }
    }

    @Override
    protected void beforeCreateEntity(
            ReglaResourceEntity entity,
            ReglaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        String error = validarCreuades(resource, null);
        if (error != null) {
            throw new ResourceNotCreatedException(ReglaResource.class, error);
        }
    }

    @Override
    protected void beforeUpdateEntity(
            ReglaResourceEntity entity,
            ReglaResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        String error = validarCreuades(resource, entity.getId());
        if (error != null) {
            throw new ResourceNotUpdatedException(ReglaResource.class, String.valueOf(entity.getId()), error);
        }
    }

    /**
     * Validacions contra les altres regles: nom+tipus+assumpte únic per entitat i, per a regles BACKOFFICE, codis SIA únics per unitat.
     *
     * @return el missatge d'error, o null si és vàlida.
     */
    private String validarCreuades(ReglaResource resource, Long reglaId) {
        Long entitatId = SessioActualUtil.getEntitatId();
        if (entitatId == null) {
            return null;
        }
        List<String> errors = new ArrayList<>();
        if (resource.getNom() != null && resource.getTipus() != null
                && reglaValidacioHelper.existeixNomTipusAssumpte(
                        entitatId,
                        resource.getNom(),
                        resource.getTipus(),
                        resource.getAssumpteCodiFiltre(),
                        reglaId)) {
            errors.add(reglaValidacioHelper.missatgeNomDuplicat());
        }
        if (ReglaTipusEnumDto.BACKOFFICE.equals(resource.getTipus())) {
            errors.addAll(reglaValidacioHelper.validarCodisSiaBackoffice(
                    reglaId,
                    entitatId,
                    resource.getUnitatOrganitzativaFiltre() != null ? resource.getUnitatOrganitzativaFiltre().getId() : null,
                    resource.getProcedimentCodiFiltre(),
                    resource.getServeiCodiFiltre(),
                    resource.getTramitCodiFiltre()));
        }
        // Cada missatge és una frase: es garanteix el punt final perquè, un cop units, no quedin enganxades.
        return errors.isEmpty() ? null : errors.stream().
                map(error -> error.endsWith(".") ? error : error + ".").
                collect(Collectors.joining(" "));
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
