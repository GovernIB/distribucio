package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.distribucio.logic.config.SegonPlaConfig;
import es.caib.distribucio.logic.helper.MessageHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.distribucio.logic.intf.model.MonitorTascaResource;
import es.caib.distribucio.logic.intf.monitor.MonitorTascaEstatEnum;
import es.caib.distribucio.logic.intf.monitor.MonitorTascaInfo;
import es.caib.distribucio.logic.intf.resourceservice.MonitorTascaResourceService;
import es.caib.distribucio.logic.intf.service.MonitorTasquesService;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementació del servei del recurs {@link MonitorTascaResource}: estat de les tasques en segon pla i reinici.
 * L'estat es manté en memòria. No hi ha accés a base de dades.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorTascaResourceServiceImpl
		extends BaseNoDatabaseMutableResourceService<MonitorTascaResource, String>
		implements MonitorTascaResourceService {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final String NO_VALUE = "-";

	private final MonitorTasquesService monitorTasquesService;
	private final SegonPlaConfig segonPlaConfig;
	private final MessageHelper messageHelper;

	@PostConstruct
	public void init() {
		register(MonitorTascaResource.ACTION_REINICIAR_CODE, new ReiniciarActionExecutor());
	}

	@Override
	public Page<MonitorTascaResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		List<MonitorTascaInfo> tasques = new ArrayList<>(monitorTasquesService.findAll());
		tasques.sort(Comparator.comparing(MonitorTascaInfo::getCodi));
		String quickFilterLower = Utils.hasValue(quickFilter) ? quickFilter.toLowerCase(Locale.ROOT) : null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		List<MonitorTascaResource> resultat = new ArrayList<>();
		for (MonitorTascaInfo tasca : tasques) {
			String nom = messageHelper.getMessage("monitor.tasques.tasca.codi." + tasca.getCodi());
			if (quickFilterLower != null
					&& !nom.toLowerCase(Locale.ROOT).contains(quickFilterLower)
					&& !(tasca.getObservacions() != null
							&& tasca.getObservacions().toLowerCase(Locale.ROOT).contains(quickFilterLower))) {
				continue;
			}
			MonitorTascaResource resource = new MonitorTascaResource();
			resource.setId(tasca.getCodi());
			resource.setNom(nom);
			resource.setEstat(messageHelper.getMessage("monitor.tasques.estat." + tasca.getEstat()));
			resource.setDataInici(formatData(sdf, tasca.getDataInici()));
			resource.setTempsExecucio(tasca.getTempsExecucio());
			resource.setProperaExecucio(
					MonitorTascaEstatEnum.EN_EXECUCIO.equals(tasca.getEstat())
							? NO_VALUE
							: formatData(sdf, tasca.getProperaExecucio()));
			resource.setObservacions(tasca.getObservacions());
			resultat.add(resource);
		}
		return new PageImpl<>(resultat, pageable, resultat.size());
	}

	@Override
	public MonitorTascaResource getOne(String id, String[] perspectives) throws ResourceNotFoundException {
		throw new ResourceNotFoundException(MonitorTascaResource.class, id);
	}

	private String formatData(SimpleDateFormat sdf, Date data) {
		return data != null ? sdf.format(data) : NO_VALUE;
	}

	/** Mateixa lògica que ScheduledController.schedulingRestart de la interfície JSP, tasca per tasca. */
	private class ReiniciarActionExecutor implements
			ActionExecutor<NoDatabaseResourceEntity<MonitorTascaResource, String>, MonitorTascaResource.ReiniciarForm, Serializable> {

		@Override
		public Serializable exec(
				String code,
				NoDatabaseResourceEntity<MonitorTascaResource, String> entity,
				MonitorTascaResource.ReiniciarForm params) throws ActionExecutionException {
			try {
				for (String id : params.getIds()) {
					monitorTasquesService.reiniciarTasquesEnSegonPla(id);
					segonPlaConfig.restartSchedulledTasks(id);
				}
				return null;
			} catch (Exception ex) {
				log.error("Error reiniciant les tasques en segon pla " + params.getIds(), ex);
				throw new ActionExecutionException(MonitorTascaResource.class, null, code, ex);
			}
		}

		@Override
		public void onChange(
				Serializable id,
				MonitorTascaResource.ReiniciarForm previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerValue> answers,
				String[] previousFieldNames,
				MonitorTascaResource.ReiniciarForm target) {
		}

	}

}
