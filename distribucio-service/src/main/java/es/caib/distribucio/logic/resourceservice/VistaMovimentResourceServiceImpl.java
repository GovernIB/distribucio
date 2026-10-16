package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
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

	private static final DateTimeFormatter ENVIAMENT_EMAIL_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	@PostConstruct
	public void init() {
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

}
