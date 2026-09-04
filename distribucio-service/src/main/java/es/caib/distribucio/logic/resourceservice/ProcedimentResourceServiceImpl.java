package es.caib.distribucio.logic.resourceservice;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;

import es.caib.distribucio.logic.helper.ProcedimentHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.model.ProcedimentResource;
import es.caib.distribucio.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.ProcedimentEntity;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcedimentResourceServiceImpl extends BaseMutableResourceService<ProcedimentResource, Long, ProcedimentResourceEntity> implements ProcedimentResourceService {

	private final EntitatResourceRepository entitatResourceRepository;
	private final ProcedimentHelper procedimentHelper;

	@PostConstruct
	public void init() {
		register(ProcedimentResource.ACTION_ACTUALITZAR_CODE, new ActualitzarActionExecutor());
		register(ProcedimentResource.ACTION_ACTUALITZAR_PROCEDIMENT_CODE, new ActualitzarProcedimentActionExecutor());
		register(ProcedimentResource.ACTION_PROGRES_CODE, new ProgresActionExecutor());
	}

	@Override
	protected Specification<ProcedimentResourceEntity> additionalSpecification(String[] namedQueries) {
		Long entitatId = SessioActualUtil.getEntitatId();
		if (entitatId != null) {
			return (root, query, cb) -> cb.equal(root.get("entitat").get("id"), entitatId);
		}
		return null;
	}

	@Override
	protected void afterConversion(ProcedimentResourceEntity entity, ProcedimentResource resource) {
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
			ProcedimentResourceEntity entity,
			ProcedimentResource resource,
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
			ProcedimentResourceEntity entity,
			ProcedimentResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
	}

	/**
	 * Acció per sincronitzar tots els procediments per a l'entitat actual.
	 */
	private class ActualitzarActionExecutor implements ActionExecutor<ProcedimentResourceEntity, Serializable, Serializable> {

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
				ProcedimentResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId == null && entity != null && entity.getEntitat() != null) {
				entitatId = entity.getEntitat().getId();
			}
			if (entitatId == null) {
				throw new ActionExecutionException(
						ProcedimentResource.class,
						null,
						code,
						"No s'ha pogut determinar l'entitat de context.");
			}
			try {
				procedimentHelper.findAndUpdateProcediments(entitatId);
				return null;
			} catch (Exception e) {
				log.error("Error actualitzant procediments per a l'entitat " + entitatId, e);
				throw new ActionExecutionException(
						ProcedimentResource.class,
						null,
						code,
						e.getMessage());
			}
		}

	}

	/**
	 * Acció per sincronitzar un procediment concret.
	 */
	private class ActualitzarProcedimentActionExecutor implements ActionExecutor<ProcedimentResourceEntity, Serializable, Serializable> {

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
				ProcedimentResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId == null && entity != null && entity.getEntitat() != null) {
				entitatId = entity.getEntitat().getId();
			}
			if (entitatId == null) {
				throw new ActionExecutionException(
						ProcedimentResource.class,
						entity != null ? entity.getId() : null,
						code,
						"No s'ha pogut determinar l'entitat de context.");
			}
			try {
				ProcedimentEntity procedimentEntity = procedimentHelper.findAndUpdateProcediment(entitatId, entity.getCodi());
				return procedimentEntity != null ? procedimentEntity.getId() : null;
			} catch (Exception e) {
				log.warn("No s'ha pogut actualitzar el procediment {}: {}", entity != null ? entity.getCodi() : null, e.getMessage());
				throw new ActionExecutionException(
						ProcedimentResource.class,
						entity != null ? entity.getId() : null,
						code,
						"El procediment amb codi " + (entity != null ? entity.getCodi() : "") + " no s'ha trobat.");
			}
		}

	}

	/**
	 * Acció per consultar el progrés de l'actualització de procediments.
	 */
	private class ProgresActionExecutor implements ActionExecutor<ProcedimentResourceEntity, Serializable, Serializable> {

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
				ProcedimentResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId == null && entity != null && entity.getEntitat() != null) {
				entitatId = entity.getEntitat().getId();
			}
			if (entitatId == null) {
				return null;
			}
			UpdateProgressDto progres = procedimentHelper.progressosActualitzacio.get(entitatId);

			return progres;
		}

	}

}
