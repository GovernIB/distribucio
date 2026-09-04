package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.model.UnitatOrganitzativaResource;
import es.caib.distribucio.logic.intf.resourceservice.UnitatOrganitzativaResourceService;
import es.caib.distribucio.logic.intf.service.ReglaService;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.resourceentity.UnitatOrganitzativaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.UnitatOrganitzativaResourceRepository;
import lombok.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UnitatOrganitzativaResourceServiceImpl extends BaseMutableResourceService<UnitatOrganitzativaResource, Long, UnitatOrganitzativaResourceEntity> implements UnitatOrganitzativaResourceService {

    private final UnitatOrganitzativaService unitatOrganitzativaService;
    private final ReglaService reglaService;
    private final EntitatRepository entitatRepository;
    private final UnitatOrganitzativaResourceRepository unitatOrganitzativaResourceRepository;

    @PostConstruct
    public void init() {
        register(UnitatOrganitzativaResource.ACTION_SYNCHRONIZE_CODE, new SynchronizeActionExecutor());
        register(UnitatOrganitzativaResource.ACTION_SYNCHRONIZE_INFO_CODE, new SynchronizeInfoActionExecutor());
    }

    @Override
    protected Specification<UnitatOrganitzativaResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();
        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
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
                    orPredicates.add(root.get("id").in(chunk));
                }

                predicates.add(cb.notEqual(root.get("id"), unitatId));
                predicates.add( cb.or(orPredicates.toArray(new Predicate[0])) );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private class SynchronizeActionExecutor implements ActionExecutor<UnitatOrganitzativaResourceEntity, Boolean, Serializable> {

        @Override
        public Serializable exec(String code, UnitatOrganitzativaResourceEntity entity, Boolean forced) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            entitatRepository.findById(entitatActualId)
                    .ifPresent((entitat) -> ConfigHelper.setEntitatActualCodi(entitat.getCodi()));
            if (forced) {
                unitatOrganitzativaService.forcedSynchronize(entitatActualId);
            } else {
                unitatOrganitzativaService.synchronize(entitatActualId);
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, Boolean previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Boolean target) {

        }
    }

    private class SynchronizeInfoActionExecutor implements ActionExecutor<UnitatOrganitzativaResourceEntity, Serializable, Serializable> {

        private void diferentiateBetweenSplitAndSubstOrMerge(MultiValuedMap splitMap, MultiValuedMap mergeOrSubstMap,
                                                             List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto) {
            // differentiate between split and (subst or merge)
            for (UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsoleteDto) {
                if (vigentObsolete.getLastHistoricosUnitats().size() > 1) {
                    for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
                        splitMap.put(vigentObsolete, hist);
                    }
                } else if (vigentObsolete.getLastHistoricosUnitats().size() == 1) {
                    // check if the map already contains key with this codi
                    UnitatOrganitzativaDto mergeOrSubstKeyWS = vigentObsolete.getLastHistoricosUnitats().get(0);
                    UnitatOrganitzativaDto keyWithTheSameCodi = null;
                    Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
                    for (UnitatOrganitzativaDto mergeOrSubstKeyMap : keysMergeOrSubst) {
                        if (mergeOrSubstKeyMap.getCodi().equals(mergeOrSubstKeyWS.getCodi())) {
                            keyWithTheSameCodi = mergeOrSubstKeyMap;
                        }
                    }
                    // if it contains already key with the same codi, assign
                    // found key
                    if (keyWithTheSameCodi != null) {
                        mergeOrSubstMap.put(keyWithTheSameCodi, vigentObsolete);
                    } else {
                        mergeOrSubstMap.put(mergeOrSubstKeyWS, vigentObsolete);
                    }
                }
            }
        }

        private void diffSubsAndMerge(MultiValuedMap mergeOrSubstMap, MultiValuedMap mergeMap, MultiValuedMap substMap) {
            Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
            for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
                List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap.get(mergeOrSubstKey);

                if (values.size() > 1) {
                    for (UnitatOrganitzativaDto value : values) {
                        mergeMap.put(mergeOrSubstKey, value);
                    }
                } else {
                    substMap.put(mergeOrSubstKey, values.get(0));
                }
            }
        }

        @Override
        public Serializable exec(
                String code,
                UnitatOrganitzativaResourceEntity entity,
                Serializable params) throws ActionExecutionException {
            Long entitatActualId = SessioActualUtil.getEntitatId();
            entitatRepository.findById(entitatActualId)
                    .ifPresent((entitat) -> ConfigHelper.setEntitatActualCodi(entitat.getCodi()));

            MultiValuedMap splitMap = new ArrayListValuedHashMap();
            MultiValuedMap mergeOrSubstMap = new ArrayListValuedHashMap();
            MultiValuedMap mergeMap = new ArrayListValuedHashMap();
            MultiValuedMap substMap = new ArrayListValuedHashMap();
            List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();
            List<UnitatOrganitzativaDto> unitatsVigentsFirstSincro = new ArrayList<>();
            List<UnitatOrganitzativaDto> unitatsNew = new ArrayList<>();
            List<ReglaDto> rulesFiltre =  new ArrayList<ReglaDto>();
            List<ReglaDto> rulesDesti =  new ArrayList<ReglaDto>();

            boolean isFirstSincronization = unitatOrganitzativaService.isFirstSincronization(entitatActualId);

            if(isFirstSincronization){
                unitatsVigentsFirstSincro = unitatOrganitzativaService.predictFirstSynchronization(entitatActualId);
            } else {
                try {
                    //Getting list of unitats that are now vigent in db but syncronization is marking them as obsolete
                    List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = unitatOrganitzativaService
                            .getObsoletesFromWS(entitatActualId);

                    // 1, differentiate between split and (subst or merge)
                    // 2, check if the map already contains key with this codi
                    // 3, if it contains already key with the same codi, assign found key
                    diferentiateBetweenSplitAndSubstOrMerge(splitMap, mergeOrSubstMap, unitatsVigentObsoleteDto);

                    // differantiate between substitution and merge
                    diffSubsAndMerge(mergeOrSubstMap, mergeMap, substMap);

                    // Getting list of unitats that are now vigent in db and in syncronization are also vigent but with properties changed
                    unitatsVigents = unitatOrganitzativaService
                            .getVigentsFromWebService(entitatActualId);


                    // Getting list of unitats that are totally new (doesnt exist in database)
                    unitatsNew = unitatOrganitzativaService
                            .getNewFromWS(entitatActualId);


                    // For all merges find related rules to old UO
                    Set<UnitatOrganitzativaDto> valuesMerge = new HashSet<UnitatOrganitzativaDto>(mergeOrSubstMap.values());
                    List<String> codisUosFusionadesSubstituides = new ArrayList<String>();
                    for (UnitatOrganitzativaDto uo : valuesMerge) {
                        codisUosFusionadesSubstituides.add(uo.getCodi());
                    }

                    for  ( String codiUo : codisUosFusionadesSubstituides) {
                        // Regles per filtre
                        for(ReglaDto r: reglaService.findByEntitatAndUnitatFiltreCodi(entitatActualId,codiUo)) {
                            rulesFiltre.add(r);
                        }
                        // Regles per destí
                        for(ReglaDto r: reglaService.findByEntitatAndUnitatDestiCodi(entitatActualId,codiUo)) {
                            rulesDesti.add(r);
                        }
                    }

                } catch (Exception exception){
                    throw new ActionExecutionException(
                            UnitatOrganitzativaResource.class,
                            null,
                            code,
                            exception.getMessage()
                    );
                }
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("isFirstSincronization", isFirstSincronization);
            map.put("unitatsVigentsFirstSincro", unitatsVigentsFirstSincro);

            map.put("splitMap", toEntries(splitMap));
            map.put("mergeMap", toEntries(mergeMap));
            map.put("substMap", toEntries(substMap));
            map.put("unitatsVigents", unitatsVigents);
            map.put("unitatsNew", unitatsNew);
            map.put("rulesFiltre", rulesFiltre);
            map.put("rulesDesti", rulesDesti);
            map.put("isAllEmpty", substMap.isEmpty() && splitMap.isEmpty() && mergeMap.isEmpty() && unitatsVigents.isEmpty() && unitatsNew.isEmpty());

            return map;
        }

        @Getter @Setter @AllArgsConstructor @NoArgsConstructor
        public class MapEntry implements Serializable {
            private UnitatOrganitzativaDto key;
            private List<UnitatOrganitzativaDto> values;
        }

        private List<MapEntry> toEntries(MultiValuedMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto> map) {
            List<MapEntry> entries = new ArrayList<>();
            for (UnitatOrganitzativaDto key : map.keySet()) {
                entries.add(new MapEntry(key, new ArrayList<>(map.get(key)) ));
            }
            return entries;
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
    }

}
