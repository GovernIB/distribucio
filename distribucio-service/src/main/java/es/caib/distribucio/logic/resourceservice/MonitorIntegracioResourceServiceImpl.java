package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.model.MonitorIntegracioResource;
import es.caib.distribucio.logic.intf.resourceservice.MonitorIntegracioResourceService;
import es.caib.distribucio.persist.resourceentity.MonitorIntegracioResourceEntity;
import es.caib.distribucio.persist.resourcerepository.MonitorIntegracioResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementació del servei de recurs de consulta del monitor d'integracions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorIntegracioResourceServiceImpl
		extends BaseMutableResourceService<MonitorIntegracioResource, Long, MonitorIntegracioResourceEntity>
		implements MonitorIntegracioResourceService {

	private final MonitorIntegracioResourceRepository monitorIntegracioResourceRepository;

	@PostConstruct
	public void init() {
		register(MonitorIntegracioResource.ACTION_COUNT_ERRORS_CODE, new CountErrorsActionExecutor());
	}

	@Override
	protected void afterConversion(MonitorIntegracioResourceEntity entity, MonitorIntegracioResource resource) {
		if (entity.getEntitat() != null) {
			resource.setEntitat(ResourceReference.toResourceReference(
					entity.getEntitat().getId(),
					entity.getEntitat().getCodi() + " - " + entity.getEntitat().getNom()));
		}
	}

	/**
	 * Compta, per codi d'integració, el nombre d'entrades en estat ERROR que compleixen el filtre
	 * indicat. Els codis d'integració sense cap error no apareixen al mapa retornat.
	 */
	private class CountErrorsActionExecutor implements
			ActionExecutor<MonitorIntegracioResourceEntity, MonitorIntegracioResource.FormFilter, HashMap<String, Integer>> {

		@Override
		public void onChange(
				Serializable id,
				MonitorIntegracioResource.FormFilter previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				MonitorIntegracioResource.FormFilter target) {
		}

		@Override
		public HashMap<String, Integer> exec(
				String code,
				MonitorIntegracioResourceEntity entity,
				MonitorIntegracioResource.FormFilter params) throws ActionExecutionException {
			Date dataInici = params != null ? params.getDataInici() : null;
			Date dataFi = params != null ? params.getDataFi() : null;
			Date dataFiExclusiva = dataFi != null ? DateUtils.addDays(dataFi, 1) : null;
			String descripcio = params != null ? params.getDescripcio() : null;
			String usuari = params != null ? params.getUsuari() : null;
			Long entitatId = params != null && params.getEntitat() != null ? params.getEntitat().getId() : null;
			String numeroRegistre = params != null ? params.getNumeroRegistre() : null;

			HashMap<String, Integer> errors = new HashMap<>();
			for (Object[] resultat : monitorIntegracioResourceRepository.countErrorsGroupByCodi(
					dataInici == null,
					dataInici,
					dataFiExclusiva == null,
					dataFiExclusiva,
					descripcio == null || descripcio.isEmpty(),
					descripcio,
					usuari == null || usuari.isEmpty(),
					usuari,
					params == null || params.getTipus() == null,
					params != null ? params.getTipus() : null,
					entitatId == null,
					entitatId,
					numeroRegistre == null || numeroRegistre.isEmpty(),
					numeroRegistre)) {
				errors.put((String) resultat[0], ((Long) resultat[1]).intValue());
			}
			return errors;
		}

	}

}
