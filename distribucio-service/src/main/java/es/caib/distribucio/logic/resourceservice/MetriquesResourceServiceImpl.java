package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.distribucio.logic.helper.MetriquesHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.distribucio.logic.intf.model.MetriquesResource;
import es.caib.distribucio.logic.intf.resourceservice.MetriquesResourceService;
import es.caib.distribucio.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Implementació del servei del recurs {@link MetriquesResource}: mètriques de l'aplicació (registre de Dropwizard).
 * Les dades es llegeixen en temps real. No hi ha accés a base de dades.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetriquesResourceServiceImpl
		extends BaseNoDatabaseMutableResourceService<MetriquesResource, String>
		implements MetriquesResourceService {

	private final MetriquesHelper metriquesHelper;

	@PostConstruct
	public void init() {
		register(MetriquesResource.ACTION_OBTENIR_METRIQUES_CODE, new ObtenirMetriquesActionExecutor());
	}

	@Override
	public Page<MetriquesResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		return new PageImpl<>(new ArrayList<>(), pageable, 0);
	}

	@Override
	public MetriquesResource getOne(String id, String[] perspectives) throws ResourceNotFoundException {
		throw new ResourceNotFoundException(MetriquesResource.class, id);
	}

	private class ObtenirMetriquesActionExecutor implements
			ActionExecutor<NoDatabaseResourceEntity<MetriquesResource, String>, Serializable, MetriquesResource.Metriques> {

		@Override
		public MetriquesResource.Metriques exec(
				String code,
				NoDatabaseResourceEntity<MetriquesResource, String> entity,
				Serializable params) throws ActionExecutionException {
			try {
				log.debug("Consultant les mètriques de l'aplicació");
				return metriquesHelper.getMetriques();
			} catch (Exception ex) {
				log.error("Error al generar les mètriques de l'aplicació", ex);
				throw new ActionExecutionException(MetriquesResource.class, null, code, ex);
			}
		}

		@Override
		public void onChange(
				Serializable id,
				Serializable previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerValue> answers,
				String[] previousFieldNames,
				Serializable target) {
		}

	}

}
