package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.logic.intf.model.VistaMovimentResource;
import es.caib.distribucio.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.distribucio.logic.intf.resourceservice.VistaMovimentResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.resourceentity.RegistreInteressatResourceEntity;
import es.caib.distribucio.persist.resourceentity.VistaMovimentResourceEntity;
import es.caib.distribucio.persist.resourcerepository.RegistreInteressatResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

	/**
	 * Completa camps que no es poden obtenir per mapeig directe de l'entitat:
	 * el resum de la columna d'interessats i si les bústies origen/destí encara estan actives.
	 */
	@Override
	protected void afterConversion(VistaMovimentResourceEntity entity, VistaMovimentResource resource) {
		List<RegistreInteressatResourceEntity> interessats = registreInteressatResourceRepository.findByRegistreId(entity.getIdRegistre());
		resource.setInteressatsString(interessats.stream()
				.map(RegistreInteressatResourceEntity::getNomComplet)
				.filter(nom -> nom != null && !nom.isEmpty())
				.collect(Collectors.joining("<br>")));

		resource.setBustiaOrigenActiva(entity.getBustiaOrigen() != null && entity.getBustiaOrigen().isActiva());
		resource.setBustiaDestiActiva(entity.getBustiaDesti() != null && entity.getBustiaDesti().isActiva());
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
