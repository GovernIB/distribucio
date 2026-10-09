package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.EntitatHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnosticDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDto;
import es.caib.distribucio.logic.intf.model.MonitorIntegracioResource;
import es.caib.distribucio.logic.intf.resourceservice.MonitorIntegracioResourceService;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.model.EntitatResource;
import es.caib.distribucio.logic.intf.service.MonitorIntegracioService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.resourceentity.MonitorIntegracioResourceEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.resourcerepository.MonitorIntegracioResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
	private final IntegracioHelper integracioHelper;
	private final MonitorIntegracioService monitorIntegracioService;
	private final AplicacioService aplicacioService;
	private final EntitatRepository entitatRepository;
	private final EntitatHelper entitatHelper;

	@PostConstruct
	public void init() {
		register(MonitorIntegracioResource.ACTION_COUNT_ERRORS_CODE, new CountErrorsActionExecutor());
		register(MonitorIntegracioResource.ACTION_INTEGRACIONS_DIAGNOSTIC_CODE, new IntegracionsDiagnosticActionExecutor());
		register(MonitorIntegracioResource.ACTION_DIAGNOSTIC_CODE, new DiagnosticActionExecutor());
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
	 * Retorna els codis d'integració a mostrar com a pestanyes ({@link IntegracioHelper#findAll()},
	 * mateixa font que la pantalla legacy) i, per cadascun, el nombre d'entrades en estat ERROR que
	 * compleixen el filtre indicat. Els codis d'integració sense cap error no apareixen al mapa
	 * d'errors.
	 */
	private class CountErrorsActionExecutor implements
			ActionExecutor<MonitorIntegracioResourceEntity, MonitorIntegracioResource.FormFilter, MonitorIntegracioResource.CountErrorsResult> {

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
		public MonitorIntegracioResource.CountErrorsResult exec(
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
			return new MonitorIntegracioResource.CountErrorsResult(integracioHelper.findAllCodis(), errors);
		}

	}

	/** Codis de les integracions que es poden diagnosticar (mateixa llista que la pantalla JSP). */
	private String[] codisPerDiagnostic() {
		return integracioHelper.findPerDiagnostic().stream().
				map(IntegracioDto::getCodi).
				toArray(String[]::new);
	}

	/**
	 * Entitat seleccionada per defecte al diagnòstic: la que la interfície JSP tindria com a entitat
	 * actual ({@code EntitatHelper.getEntitatActual} del back), és a dir, l'entitat per defecte de
	 * l'usuari si hi té accés o, si no, la primera entitat a la qual té accés. Si l'usuari no té accés
	 * a cap entitat, la primera de les entitats actives.
	 */
	private Long entitatPerDefecteDiagnosticId(List<EntitatEntity> entitatsActives) {
		List<EntitatDto> accessibles = entitatHelper.findAccessiblesUsuariActual();
		if (accessibles != null && !accessibles.isEmpty()) {
			UsuariDto usuari = aplicacioService.getUsuariActual();
			Long entitatPerDefecteId = usuari != null ? usuari.getEntitatPerDefecteId() : null;
			if (entitatPerDefecteId != null
					&& accessibles.stream().anyMatch(e -> Objects.equals(e.getId(), entitatPerDefecteId))) {
				return entitatPerDefecteId;
			}
			return accessibles.get(0).getId();
		}
		return !entitatsActives.isEmpty() ? entitatsActives.get(0).getId() : null;
	}

	private class IntegracionsDiagnosticActionExecutor implements
			ActionExecutor<MonitorIntegracioResourceEntity, Serializable, MonitorIntegracioResource.IntegracionsDiagnosticResult> {

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
		public MonitorIntegracioResource.IntegracionsDiagnosticResult exec(
				String code,
				MonitorIntegracioResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			List<EntitatEntity> entitatsActives = entitatRepository.findByActiva(true).stream().
					sorted(Comparator.comparing(EntitatEntity::getNom, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))).
					collect(Collectors.toList());
			List<ResourceReference<EntitatResource, Long>> entitats = entitatsActives.stream().
					map(e -> ResourceReference.<EntitatResource, Long>toResourceReference(
							e.getId(),
							e.getCodi() + " - " + e.getNom())).
					collect(Collectors.toList());
			return new MonitorIntegracioResource.IntegracionsDiagnosticResult(
					Arrays.asList(codisPerDiagnostic()),
					entitats,
					entitatPerDefecteDiagnosticId(entitatsActives));
		}

	}

	/**
	 * Executa la prova d'una integració cridant el mateix mètode que la pantalla JSP
	 * ({@link MonitorIntegracioService#diagnostic}). Un error de la integració no és un error de
	 * l'acció: es retorna com a resultat amb {@code correcte = false}, el missatge i la traça.
	 * <p>
	 * Els plugins es configuren per entitat i llegeixen el codi de l'entitat de
	 * {@link ConfigHelper#getEntitatActualCodi()}. La JSP l'estableix a cada petició amb l'entitat
	 * actual de la sessió (LlistaEntitatsInterceptor); aquí s'estableix amb l'entitat seleccionada
	 * i després es restaura el valor anterior del fil.
	 */
	private class DiagnosticActionExecutor implements
			ActionExecutor<MonitorIntegracioResourceEntity, MonitorIntegracioResource.DiagnosticForm, MonitorIntegracioResource.DiagnosticResult> {

		@Override
		public void onChange(
				Serializable id,
				MonitorIntegracioResource.DiagnosticForm previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				MonitorIntegracioResource.DiagnosticForm target) {
		}

		@Override
		public MonitorIntegracioResource.DiagnosticResult exec(
				String code,
				MonitorIntegracioResourceEntity entity,
				MonitorIntegracioResource.DiagnosticForm params) throws ActionExecutionException {
			String codiIntegracio = params != null ? params.getCodiIntegracio() : null;
			if (!Arrays.asList(codisPerDiagnostic()).contains(codiIntegracio)) {
				throw new ActionExecutionException(
						MonitorIntegracioResource.class,
						null,
						code,
						"Codi d'integració no diagnosticable: " + codiIntegracio);
			}
			EntitatEntity entitat = null;
			if (params.getEntitatId() != null) {
				entitat = entitatRepository.findById(params.getEntitatId()).orElseThrow(
						() -> new ActionExecutionException(
								MonitorIntegracioResource.class,
								null,
								code,
								"No existeix l'entitat amb id " + params.getEntitatId()));
			}
			String entitatActualCodiAnterior = ConfigHelper.getEntitatActualCodi();
			IntegracioDiagnosticDto diagnostic;
			try {
				ConfigHelper.setEntitatActualCodi(entitat != null ? entitat.getCodi() : null);
				diagnostic = monitorIntegracioService.diagnostic(
						codiIntegracio,
						aplicacioService.getUsuariActual());
			} finally {
				ConfigHelper.setEntitatActualCodi(entitatActualCodiAnterior);
			}
			return new MonitorIntegracioResource.DiagnosticResult(
					diagnostic.isCorrecte(),
					diagnostic.getProva(),
					diagnostic.getErrMsg(),
					diagnostic.getExcepcioStacktrace());
		}

	}

}
