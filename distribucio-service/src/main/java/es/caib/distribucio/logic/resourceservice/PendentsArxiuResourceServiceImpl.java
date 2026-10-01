package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.HistogramPendentsHelper;
import es.caib.distribucio.logic.helper.RegistreHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.distribucio.logic.intf.model.PendentsArxiuResource;
import es.caib.distribucio.logic.intf.resourceservice.PendentsArxiuResourceService;
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
 * Implementació del servei del recurs {@link PendentsArxiuResource}: monitor d'anotacions pendents d'Arxiu.
 * Les dades es llegeixen en temps real de l'històric en memòria. No hi ha accés a base de dades.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PendentsArxiuResourceServiceImpl
		extends BaseNoDatabaseMutableResourceService<PendentsArxiuResource, String>
		implements PendentsArxiuResourceService {

	private static final String CRON_INACTIVITAT_PROPERTY = "es.caib.distribucio.tasca.guardar.annexos.innectivitat.cron";

	private final HistogramPendentsHelper histogramPendentsHelper;
	private final RegistreHelper registreHelper;
	private final ConfigHelper configHelper;

	@PostConstruct
	public void init() {
		register(PendentsArxiuResource.ACTION_OBTENIR_HISTOGRAMA_CODE, new ObtenirHistogramaActionExecutor());
	}

	@Override
	public Page<PendentsArxiuResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		return new PageImpl<>(new ArrayList<>(), pageable, 0);
	}

	@Override
	public PendentsArxiuResource getOne(String id, String[] perspectives) throws ResourceNotFoundException {
		throw new ResourceNotFoundException(PendentsArxiuResource.class, id);
	}

	private class ObtenirHistogramaActionExecutor implements
			ActionExecutor<NoDatabaseResourceEntity<PendentsArxiuResource, String>, Serializable, PendentsArxiuResource.Histograma> {

		@Override
		public PendentsArxiuResource.Histograma exec(
				String code,
				NoDatabaseResourceEntity<PendentsArxiuResource, String> entity,
				Serializable params) throws ActionExecutionException {
			try {
				log.debug("Consultant l'històric d'anotacions pendents d'Arxiu");
				PendentsArxiuResource.Histograma histograma = new PendentsArxiuResource.Histograma();
				histograma.setNumeroThreads(registreHelper.getMaxThreadsParallelProperty());
				histograma.setExpressioInactivitat(configHelper.getConfig(CRON_INACTIVITAT_PROPERTY));
				histograma.setEntrades(histogramPendentsHelper.getSnapshot());
				return histograma;
			} catch (Exception ex) {
				log.error("Error al consultar l'històric d'anotacions pendents d'Arxiu", ex);
				throw new ActionExecutionException(PendentsArxiuResource.class, null, code, ex);
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
