package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.logic.intf.model.VistaMovimentResource;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.distribucio.logic.intf.resourceservice.VistaMovimentResourceService;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.entity.ContingutLogEntity;
import es.caib.distribucio.persist.repository.ContingutLogRepository;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourceentity.ContingutResourceEntity;
import es.caib.distribucio.persist.resourceentity.RegistreInteressatResourceEntity;
import es.caib.distribucio.persist.resourceentity.VistaMovimentResourceEntity;
import es.caib.distribucio.persist.resourcerepository.RegistreInteressatResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementació del servei de recurs de la "Vista de moviments".
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class VistaMovimentResourceServiceImpl
		extends BaseMutableResourceService<VistaMovimentResource, String, VistaMovimentResourceEntity>
		implements VistaMovimentResourceService {

	private final RegistreInteressatResourceRepository registreInteressatResourceRepository;
	private final AclEntryResourceService aclEntryResourceService;
	private final AuthenticationHelper authenticationHelper;
	private final ContingutLogRepository contingutLogRepository;
	/**
	 * Pont temporal cap al servei antic per reutilitzar {@link RegistreService#getZipDocumentacio}
	 * (ContingutController.descarregarZipDocumentacio a la JSP), que genera el ZIP amb un pool de fils, i
	 * no és un mètode trivial. TODO: s'hauria d'extreure a un helper de {@code logic.helper}.
	 */
	private final RegistreService registreService;
	private final BustiaService bustiaService;
	private final AplicacioService aplicacioService;

	private static final DateTimeFormatter ENVIAMENT_EMAIL_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	@PostConstruct
	public void init() {
		register(VistaMovimentResource.REPORT_DESCARREGAR_ZIP_ORIGINAL_CODE, new DescarregarZipReportGenerator(false));
		register(VistaMovimentResource.REPORT_DESCARREGAR_ZIP_CAI_CODE, new DescarregarZipReportGenerator(true));
		register(VistaMovimentResource.ACTION_ENVIAR_EMAIL_CODE, new EnviarEmailActionExecutor());
		register(VistaMovimentResource.ACTION_REENVIAR_CODE, new ReenviarActionExecutor());
	}

	/**
	 * Completa camps que no es poden obtenir per mapeig directe de l'entitat:
	 * el resum de la columna d'interessats i si les bústies origen/destí encara estan actives.
	 */
	@Override
	protected void afterConversion(VistaMovimentResourceEntity entity, VistaMovimentResource resource) {
		List<RegistreInteressatResourceEntity> interessats = registreInteressatResourceRepository.findByRegistreId(entity.getIdRegistre());
		resource.setInteressatsString(interessatsResum(interessats));

		resource.setBustiaOrigenActiva(entity.getBustiaOrigen() != null && entity.getBustiaOrigen().isActiva());
		resource.setBustiaDestiActiva(entity.getBustiaDesti() != null && entity.getBustiaDesti().isActiva());

		resource.setBustiaOrigenPath(buildBustiaPath(entity.getBustiaOrigen()));
		resource.setBustiaDestiPath(buildBustiaPath(entity.getBustiaDesti()));
	}

	/**
	 * Breadcrumb complet d'una bústia (unitat organitzativa arrel fins a la pròpia bústia, inclosa),
	 * mateix algorisme que {@code ContingutResourceServiceImpl.buildPath}/{@code displayNom} (que
	 * exclou el propi contingut.
	 */
	private List<String> buildBustiaPath(BustiaResourceEntity bustia) {
		if (bustia == null) {
			return Collections.emptyList();
		}
		List<ContingutResourceEntity<?>> ancestors = new ArrayList<>();
		ContingutResourceEntity<?> current = bustia;
		while (current != null) {
			ancestors.add(current);
			current = current.getPare();
		}
		Collections.reverse(ancestors);
		List<String> path = new ArrayList<>(ancestors.size());
		for (ContingutResourceEntity<?> ancestor : ancestors) {
			path.add(displayNomBustiaPath(ancestor));
		}
		return path;
	}

	/** Una bústia arrel (sense pare) es mostra amb la denominació de la unitat organitzativa, no amb el seu propi nom. */
	private String displayNomBustiaPath(ContingutResourceEntity<?> entity) {
		if (entity instanceof BustiaResourceEntity) {
			BustiaResourceEntity bustia = (BustiaResourceEntity) entity;
			if (bustia.getPare() == null && bustia.getUnitatOrganitzativa() != null) {
				return bustia.getUnitatOrganitzativa().getDenominacio();
			}
		}
		return entity.getNom();
	}

	/** Un interessat per línia, amb el seu representant (si en té) entre parèntesis */
	private String interessatsResum(List<RegistreInteressatResourceEntity> interessats) {
		List<String> linies = new ArrayList<>();
		for (RegistreInteressatResourceEntity interessat : interessats) {
			if (interessat.getRepresentat() != null) {
				continue;
			}
			StringBuilder linia = new StringBuilder("- ").append(nomComplet(interessat));
			if (interessat.getRepresentant() != null) {
				linia.append(" (R: ").append(nomComplet(interessat.getRepresentant())).append(")");
			}
			linies.add(linia.toString());
		}
		return String.join("\n", linies);
	}

	private String nomComplet(RegistreInteressatResourceEntity persona) {
		if (persona.getTipus() == RegistreInteressatTipusEnum.PERSONA_FIS) {
			return Stream.of(persona.getNom(), persona.getLlinatge1(), persona.getLlinatge2())
					.filter(part -> part != null && !part.isEmpty())
					.collect(Collectors.joining(" "));
		}
		return persona.getRaoSocial();
	}

	/**
	 * Informa els enviaments per email de les anotacions de la pàgina amb una sola consulta de logs
	 * (mateix format que la JSP antiga: "dd/MM/yyyy HH:mm:ss destinataris").
	 */
	@Override
	protected void afterConversion(List<VistaMovimentResourceEntity> entities, List<VistaMovimentResource> resources) {
		super.afterConversion(entities, resources);

		List<Long> registreIds = resources.stream()
				.filter(VistaMovimentResource::isEnviatPerEmail)
				.map(VistaMovimentResource::getIdRegistre)
				.distinct()
				.collect(Collectors.toList());
		if (registreIds.isEmpty()) {
			return;
		}
		Map<Long, List<String>> enviamentsPerRegistre = new HashMap<>();
		for (ContingutLogEntity log : contingutLogRepository.findByContingutIdInAndTipusOrderByCreatedDateAsc(
				registreIds,
				LogTipusEnumDto.ENVIAMENT_EMAIL)) {
			if (log.getCreatedDate().isPresent()) {
				enviamentsPerRegistre
						.computeIfAbsent(log.getContingut().getId(), k -> new ArrayList<>())
						.add(ENVIAMENT_EMAIL_FORMAT.format(log.getCreatedDate().get()) + " " + log.getParam2());
			}
		}
		resources.stream()
				.filter(VistaMovimentResource::isEnviatPerEmail)
				.forEach(r -> r.setEnviamentsPerEmail(
						enviamentsPerRegistre.getOrDefault(r.getIdRegistre(), new ArrayList<>())));
	}

	@Override
	protected Specification<VistaMovimentResourceEntity> additionalSpecification(String[] namedQueries) {
		Long entitatActualId = SessioActualUtil.getEntitatId();
		Map<String, String> mapaNamedQueries = Utils.namedQueriesToMap(namedQueries);

		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (entitatActualId != null) {
				predicates.add(cb.equal(root.get("entitatId"), entitatActualId));
			}

			Set<Serializable> bustiesPermesesIds = aclEntryResourceService.findIdsWithAnyPermission(
					ResourceType.BUSTIA,
					List.of(PermissionEnum.READ),
					authenticationHelper.getCurrentUserName(),
					new ArrayList<>(List.of(BaseConfig.ROLE_USER)));
			if (bustiesPermesesIds.isEmpty()) {
				return cb.disjunction();
			}
			int chunkSize = 900; // límit d'elements d'un IN a Oracle
			List<Serializable> bustiesIdsList = new ArrayList<>(bustiesPermesesIds);
			List<Predicate> bustiesPredicates = new ArrayList<>();
			for (int i = 0; i < bustiesIdsList.size(); i += chunkSize) {
				List<Serializable> chunk = bustiesIdsList.subList(i, Math.min(i + chunkSize, bustiesIdsList.size()));
				bustiesPredicates.add(root.get("bustiaOrigen").get("id").in(chunk));
				bustiesPredicates.add(root.get("bustiaDesti").get("id").in(chunk));
			}
			predicates.add(cb.or(bustiesPredicates.toArray(new Predicate[0])));

			// INTERESSAT: cercar per document/nom/llinatges/raó social d'algun dels interessats de
			// l'anotació de registre (mateixos camps que RegistreFilter.tsx, sense concatenar-los).
			if (mapaNamedQueries.containsKey("INTERESSAT")) {
				String interessat = "%" + mapaNamedQueries.get("INTERESSAT").toLowerCase() + "%";

				Subquery<Long> subquery = query.subquery(Long.class);
				Root<RegistreInteressatResourceEntity> interessatRoot = subquery.from(RegistreInteressatResourceEntity.class);
				subquery.select(interessatRoot.get("id"));
				subquery.where(
						cb.equal(interessatRoot.get("registre").get("id"), root.get("idRegistre")),
						cb.or(
								cb.like(cb.lower(interessatRoot.get("documentNum")), interessat),
								cb.like(cb.lower(interessatRoot.get("nom")), interessat),
								cb.like(cb.lower(interessatRoot.get("llinatge1")), interessat),
								cb.like(cb.lower(interessatRoot.get("llinatge2")), interessat),
								cb.like(cb.lower(interessatRoot.get("raoSocial")), interessat)
						)
				);
				predicates.add(cb.exists(subquery));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	/**
	 * Accions "Descarregar ZIP" (versió original / còpia autèntica imprimible) del menú de la fila:
	 * El booleà distingeix quina versió es demana, igual que al controller antic.
	 */
	@RequiredArgsConstructor
	private class DescarregarZipReportGenerator
			implements ReportGenerator<VistaMovimentResourceEntity, Serializable, VistaMovimentResource> {

		private final boolean ambVersioImprimible;

		@Override
		public List<VistaMovimentResource> generateData(String code, VistaMovimentResourceEntity entity, Serializable params) {
			return List.of(objectMappingHelper.newInstanceMap(entity, VistaMovimentResource.class));
		}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			VistaMovimentResource moviment = ((List<VistaMovimentResource>) data).get(0);

			// "Rol actual" equivalent a RolHelper.getRolActual(request) de la JSP: null per a un usuari
			// normal (getZipDocumentacio agafa llavors l'entitat del propi registre), o el rol d'admin amb
			// què s'ha entrat si n'és un, per no excloure els documents interns de la descàrrega.
			String rolActual = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)
					? BaseConfig.ROLE_ADMIN
					: authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA)
							? BaseConfig.ROLE_ADMIN_LECTURA
							: null;
			try {
				FitxerDto fitxer = registreService.getZipDocumentacio(moviment.getIdRegistre(), rolActual, ambVersioImprimible);
				return new DownloadableFile(fitxer.getNom(), fitxer.getContentType(), fitxer.getContingut());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}

	}

	/**
	 * Acció "Enviar via email": Declarada aquí (i no reutilitzant {@code RegistreResource.ACTION_ENVIAR_EMAIL_CODE})
	 * perquè aquella l'executa amb {@code isVistaMoviments=false}: exigiria permís sobre la bústia ACTUAL del registre,
	 * que en aquesta pantalla pot no ser-hi (l'usuari hi arriba per un moviment antic seu).
	 * <p>
	 * Suporta l'execució massiva: quan no arriba id per la URL (l'entity que rep el framework és null),
	 * itera "params.getIds()" sense aturar-se al primer error, i retorna un resum amb els comptadors i,
	 * si n'hi ha hagut, els errors de cada moviment.
	 */
	private class EnviarEmailActionExecutor
			implements ActionExecutor<VistaMovimentResourceEntity, VistaMovimentResource.EnviarEmailForm, HashMap<String, String>> {

		@Override
		public HashMap<String, String> exec(String code, VistaMovimentResourceEntity entity, VistaMovimentResource.EnviarEmailForm params) throws ActionExecutionException {
			String adreces = Arrays.stream(params.getDestinatari()
							.replaceAll("\\s*,\\s*|\\s+", ",").split(","))
					.distinct()
					.collect(Collectors.joining(","));

			if (entity != null) {
				enviarEmail(code, entity, adreces, params.getMotiu());
				HashMap<String, String> resposta = new HashMap<>();
				resposta.put("numero", entity.getNumero());
				return resposta;
			}

			if (params.getIds() == null || params.getIds().isEmpty()) {
				throw new ActionExecutionException(
						VistaMovimentResource.class,
						null, code,
						I18nUtil.getInstance().getI18nMessage("bustia.pendent.contingut.seleccio.moviments.buida"));
			}

			int ok = 0;
			List<String> errors = new ArrayList<>();
			for (String movimentId : params.getIds()) {
				try {
					VistaMovimentResourceEntity moviment = getEntity(movimentId);
					enviarEmail(code, moviment, adreces, params.getMotiu());
					ok++;
				} catch (Exception e) {
					errors.add(errorMovimentMissatge(movimentId, e));
				}
			}
			return resultatMassiu(ok, params.getIds().size(), errors);
		}

		private void enviarEmail(String code, VistaMovimentResourceEntity entity, String adreces, String motiu) throws ActionExecutionException {
			if (RegistreProcesEstatEnum.ARXIU_PENDENT.equals(entity.getProcesEstat())) {
				throw new ActionExecutionException(
						VistaMovimentResource.class,
						null, code,
						I18nUtil.getInstance().getI18nMessage("bustia.controller.pendent.contingut.enviar.email.validacio.estat"));
			}

			Long entitatActualId = SessioActualUtil.getEntitatId();
			try {
				bustiaService.registreAnotacioEnviarPerEmail(
						entitatActualId,
						entity.getIdRegistre(),
						adreces,
						motiu,
						true,
						authenticationHelper.getCurrentUserRoles()[0]);
			} catch (Exception e) {
				throw new ActionExecutionException(
						VistaMovimentResource.class,
						null, code,
						"S'ha produit un error al intentar enviar correu: " + e.getMessage());
			}
		}

		@Override
		public void onChange(Serializable id, VistaMovimentResource.EnviarEmailForm previous, String fieldName, Object fieldValue, Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, VistaMovimentResource.EnviarEmailForm target) {
		}

	}

	/**
	 * Acció "Reenviar": Declarada aquí (i no reutilitzant {@code RegistreResource.ACTION_REENVIAR_CODE})
	 * perquè aquesta pantalla necessita passar {@code destiLogic}, el moviment concret des d'on es reenvia, que RegistreResource no coneix,
	 * i perquè aquell recurs restringeix l'acció a ROLE_ADMIN.
	 * <p>
	 * "Deixar còpia" sempre és {@code true}. Suporta l'execució massiva igual que {@link EnviarEmailActionExecutor}:
	 * cada moviment es reenvia des del seu propi {@code destiLogic}, un darrere l'altre, sense aturar-se al primer error.
	 */
	private class ReenviarActionExecutor
			implements ActionExecutor<VistaMovimentResourceEntity, VistaMovimentResource.ReenviarForm, HashMap<String, String>> {

		private boolean isConeixementActiva() {
			return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.enviar.coneixement"));
		}

		@Override
		public HashMap<String, String> exec(String code, VistaMovimentResourceEntity entity, VistaMovimentResource.ReenviarForm params) throws ActionExecutionException {
			if (params.getBusties() == null || params.getBusties().isEmpty()) {
				throw new ActionExecutionException(
						VistaMovimentResource.class,
						null, code,
						I18nUtil.getInstance().getI18nMessage("bustia.pendent.accio.reenviar.no.desti"));
			}

			if (entity != null) {
				reenviar(code, entity, params);
				HashMap<String, String> resposta = new HashMap<>();
				resposta.put("numero", entity.getNumero());
				return resposta;
			}

			if (params.getIds() == null || params.getIds().isEmpty()) {
				throw new ActionExecutionException(
						VistaMovimentResource.class,
						null, code,
						I18nUtil.getInstance().getI18nMessage("bustia.pendent.contingut.seleccio.moviments.buida"));
			}

			int ok = 0;
			List<String> errors = new ArrayList<>();
			for (String movimentId : params.getIds()) {
				try {
					VistaMovimentResourceEntity moviment = getEntity(movimentId);
					reenviar(code, moviment, params);
					ok++;
				} catch (Exception e) {
					errors.add(errorMovimentMissatge(movimentId, e));
				}
			}
			return resultatMassiu(ok, params.getIds().size(), errors);
		}

		private void reenviar(String code, VistaMovimentResourceEntity entity, VistaMovimentResource.ReenviarForm params) throws ActionExecutionException {
			Long entitatActualId = SessioActualUtil.getEntitatId();
			Long[] perConeixement = params.getConeixement() != null
					? params.getConeixement().toArray(new Long[0])
					: new Long[0];
			try {
				bustiaService.registreReenviar(
						entitatActualId,
						params.getBusties().toArray(new Long[0]),
						entity.getIdRegistre(),
						true, // deixar còpia: sempre activat des de la Vista de moviments
						params.getComentari(),
						perConeixement,
						Collections.emptyMap(),
						entity.getDestiLogic());
			} catch (ActionExecutionException e) {
				throw e;
			} catch (Exception e) {
				throw new ActionExecutionException(
						VistaMovimentResource.class,
						null, code,
						"S'ha produït un error al reenviar l'anotació: " + e.getMessage());
			}
		}

		@Override
		public void onChange(Serializable id, VistaMovimentResource.ReenviarForm previous, String fieldName, Object fieldValue, Map<String, es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, VistaMovimentResource.ReenviarForm target) {
			if (fieldName == null) {
				target.setConeixementActiva(isConeixementActiva());
			}
		}

	}

	/** Missatge d'error d'un moviment concret dins una acció massiva (identificat pel seu id compost "reg_dest"). */
	private String errorMovimentMissatge(String movimentId, Exception e) {
		String missatge = e.getMessage();
		return movimentId + ": " + (missatge != null ? missatge : e.getClass().getSimpleName());
	}

	/** Resum d'una acció massiva: comptadors d'èxit/total i, si n'hi ha hagut, el detall dels errors. */
	private HashMap<String, String> resultatMassiu(int ok, int total, List<String> errors) {
		HashMap<String, String> resposta = new HashMap<>();
		resposta.put("count", String.valueOf(ok));
		resposta.put("total", String.valueOf(total));
		if (!errors.isEmpty()) {
			resposta.put("errors", String.join("\n", errors));
		}
		return resposta;
	}

}
