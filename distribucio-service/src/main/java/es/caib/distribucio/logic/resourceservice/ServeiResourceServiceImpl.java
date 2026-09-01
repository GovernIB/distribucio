package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.util.RequestSessionUtil;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.model.ServeiResource;
import es.caib.distribucio.logic.intf.model.UserSession;
import es.caib.distribucio.logic.intf.resourceservice.ServeiResourceService;
import es.caib.distribucio.logic.intf.service.ServeiService;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.ServeiResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.ServeiResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServeiResourceServiceImpl extends BaseMutableResourceService<ServeiResource, Long, ServeiResourceEntity> implements ServeiResourceService {

	private final ServeiService serveiService;
	private final ServeiResourceRepository serveiResourceRepository;
	private final UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	private final EntitatResourceRepository entitatResourceRepository;

	@PostConstruct
	public void init() {
		register(ServeiResource.ACTION_ACTUALITZAR_CODE, new ActualitzarActionExecutor());
		register(ServeiResource.ACTION_ACTUALITZAR_SERVEI_CODE, new ActualitzarServeiActionExecutor());
		register(ServeiResource.ACTION_PROGRES_CODE, new ProgresActionExecutor());
	}

	@Override
	protected Specification<ServeiResourceEntity> additionalSpecification(String[] namedQueries) {
		Long entitatId = getCurrentEntitatId();
		if (entitatId != null) {
			return (root, query, cb) -> cb.equal(root.get("entitat").get("id"), entitatId);
		}
		return null;
	}

	@Override
	protected void afterConversion(ServeiResourceEntity entity, ServeiResource resource) {
		if (entity.getUnitatOrganitzativa() != null) {
//			resource.setUnitatOrganitzativaId(entity.getUnitatOrganitzativa().getId());
//			resource.setUnitatOrganitzativaCodi(entity.getUnitatOrganitzativa().getCodi());
//			resource.setUnitatOrganitzativaDenominacio(entity.getUnitatOrganitzativa().getDenominacio());
//			resource.setUnitatOrganitzativaEstat(entity.getUnitatOrganitzativa().getEstat());
//			resource.setUnitatOrganitzativaDescripcio(entity.getUnitatOrganitzativa().getCodiAndNom());
		}
	}

	@Override
	protected void beforeCreateSave(
			ServeiResourceEntity entity,
			ServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (entity.getEntitat() == null) {
			Long entitatId = getCurrentEntitatId();
			if (entitatId != null) {
				EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(entitatId);
				entity.setEntitat(entitat);
			}
		}
//		if (resource.getUnitatOrganitzativaId() != null) {
//			entity.setUnitatOrganitzativa(unitatOrganitzativaRepository.getReferenceById(resource.getUnitatOrganitzativaId()));
//		}
	}

	@Override
	protected void beforeUpdateSave(
			ServeiResourceEntity entity,
			ServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
//		if (resource.getUnitatOrganitzativaId() != null) {
//			entity.setUnitatOrganitzativa(unitatOrganitzativaRepository.getReferenceById(resource.getUnitatOrganitzativaId()));
//		} else {
//			entity.setUnitatOrganitzativa(null);
//		}
	}

	private Long getCurrentEntitatId() {
		Object session = RequestSessionUtil.getRequestSession();
		if (session instanceof UserSession) {
			return ((UserSession) session).getEntitatId();
		}
		return null;
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
			Long entitatId = getCurrentEntitatId();
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
				serveiService.findAndUpdateServeis(entitatId);
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
			Long entitatId = getCurrentEntitatId();
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
				ServeiDto serveiDto = serveiService.findAndUpdateServei(entitatId, entity.getCodi());
				return serveiDto != null ? serveiDto.getId() : null;
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
			Long entitatId = getCurrentEntitatId();
			if (entitatId == null && entity != null && entity.getEntitat() != null) {
				entitatId = entity.getEntitat().getId();
			}
			if (entitatId == null) {
				return null;
			}
			UpdateProgressDto progres = serveiService.getProgresActualitzacio(entitatId);
			return (Serializable) progres;
		}

	}

}
