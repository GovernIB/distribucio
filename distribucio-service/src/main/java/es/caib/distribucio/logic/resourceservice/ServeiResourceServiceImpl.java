package es.caib.distribucio.logic.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;

import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.distribucio.persist.resourcerepository.BustiaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UnitatOrganitzativaResourceRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ServeiHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.model.ServeiResource;
import es.caib.distribucio.logic.intf.resourceservice.ServeiResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.ServeiEntity;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.ServeiResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServeiResourceServiceImpl extends BaseMutableResourceService<ServeiResource, Long, ServeiResourceEntity> implements ServeiResourceService {

	private final ServeiHelper serveiHelper;
	private final EntitatResourceRepository entitatResourceRepository;
    private final BustiaResourceRepository bustiaResourceRepository;
    private final UnitatOrganitzativaResourceRepository unitatOrganitzativaResourceRepository;

    @PostConstruct
	public void init() {
		register(ServeiResource.ACTION_ACTUALITZAR_CODE, new ActualitzarActionExecutor());
		register(ServeiResource.ACTION_ACTUALITZAR_SERVEI_CODE, new ActualitzarServeiActionExecutor());
		register(ServeiResource.ACTION_PROGRES_CODE, new ProgresActionExecutor());
	}

    @Override
    protected Specification<ServeiResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            /// Entitat
            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }
            /// BUSTIA
            if (mapaNamedQueries.containsKey("BUSTIA")) {
                String id = mapaNamedQueries.get("BUSTIA");
                if (id != null) {
                    BustiaResourceEntity bustia = bustiaResourceRepository.findById(Long.valueOf(id)).get();

                    List<Long> idsJerarquia = unitatOrganitzativaResourceRepository.findUnitatAndAllDescendentsIds(
                            bustia.getUnitatOrganitzativa().getId() );
                    if (idsJerarquia.isEmpty()) {
                        return cb.disjunction(); // No existe tal unidad, no devolver nada
                    }

                    int chunkSize = 900;
                    List<Predicate> orPredicates = new ArrayList<>();

                    for (int i = 0; i < idsJerarquia.size(); i += chunkSize) {
                        List<Long> chunk = idsJerarquia.subList(i, Math.min(i + chunkSize, idsJerarquia.size()));
                        orPredicates.add(root.get("unitatOrganitzativa").get("id").in(chunk));
                    }

                    predicates.add( cb.or(orPredicates.toArray(new Predicate[0])) );
                } else {
                    return cb.disjunction();
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

	@Override
	protected void afterConversion(ServeiResourceEntity entity, ServeiResource resource) {
		if (entity.getUnitatOrganitzativa() != null) {
			resource.setUnitatOrganitzativa(ResourceReference.toResourceReference(
					entity.getUnitatOrganitzativa().getId(),
					entity.getUnitatOrganitzativa().getCodi() + " - " + entity.getUnitatOrganitzativa().getDenominacio()));
		}
		if (entity.getEntitat() != null) {
			resource.setEntitat(ResourceReference.toResourceReference(
					entity.getEntitat().getId(),
					entity.getEntitat().getCodi() + " - " + entity.getEntitat().getNom()));
		}
	}

	@Override
	protected void beforeCreateSave(
			ServeiResourceEntity entity,
			ServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (entity.getEntitat() == null) {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId != null) {
				EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(entitatId);
				entity.setEntitat(entitat);
			}
		}
	}

	@Override
	protected void beforeUpdateSave(
			ServeiResourceEntity entity,
			ServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
	}

	/**
	 * Acció per sincronitzar tots els serveis des de ROLSAC per a l'entitat actual.
	 */
	private class ActualitzarActionExecutor implements ActionExecutor<ServeiResourceEntity, Serializable, Serializable> {

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

		@Override
		public Serializable exec(
				String code,
				ServeiResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId == null && entity != null && entity.getEntitat() != null) {
				entitatId = entity.getEntitat().getId();
			}
			if (entitatId == null) {
				throw new ActionExecutionException(
						ServeiResource.class,
						null,
						code,
						"No s'ha pogut determinar l'entitat de context.");
			}
			try {
				serveiHelper.findAndUpdateServeis(entitatId);
				return null;
			} catch (Exception e) {
				log.error("Error actualitzant serveis per a l'entitat " + entitatId, e);
				throw new ActionExecutionException(
						ServeiResource.class,
						null,
						code,
						e.getMessage());
			}
		}

	}

	/**
	 * Acció per sincronitzar un servei concret des de ROLSAC.
	 */
	private class ActualitzarServeiActionExecutor implements ActionExecutor<ServeiResourceEntity, Serializable, Serializable> {

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

		@Override
		public Serializable exec(
				String code,
				ServeiResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId == null && entity.getEntitat() != null) {
				entitatId = entity.getEntitat().getId();
			}
			if (entitatId == null) {
				throw new ActionExecutionException(
						ServeiResource.class,
						entity.getId(),
						code,
						"No s'ha pogut determinar l'entitat de context.");
			}
			try {
				ServeiEntity serveiEntity = serveiHelper.findAndUpdateServei(entitatId, entity.getCodi());
				return serveiEntity != null ? serveiEntity.getId() : null;
			} catch (Exception e) {
				log.warn("No s'ha pogut actualitzar el servei {}: {}", entity.getCodi(), e.getMessage());
				throw new ActionExecutionException(
						ServeiResource.class,
						entity.getId(),
						code,
						"El servei amb codi " + entity.getCodi() + " no s'ha trobat a ROLSAC.");
			}
		}

	}

	/**
	 * Acció per consultar el progrés de l'actualització de serveis.
	 */
	private class ProgresActionExecutor implements ActionExecutor<ServeiResourceEntity, Serializable, Serializable> {

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

		@Override
		public Serializable exec(
				String code,
				ServeiResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId == null && entity != null && entity.getEntitat() != null) {
				entitatId = entity.getEntitat().getId();
			}
			if (entitatId == null) {
				return null;
			}
			UpdateProgressDto progres = serveiHelper.serveisActualitzacio.get(entitatId);

			return progres;
		}

	}

}
