package es.caib.distribucio.logic.resourceservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.ContingutResource;
import es.caib.distribucio.logic.intf.resourceservice.ContingutResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourceentity.ContingutResourceEntity;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;
import es.caib.distribucio.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de recurs per a la consulta de continguts.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutResourceServiceImpl
		extends BaseMutableResourceService<ContingutResource, Long, ContingutResourceEntity<ContingutResource>>
		implements ContingutResourceService {

	private final UsuariResourceRepository usuariResourceRepository;

	/**
	 * Restringeix totes les consultes (llistat, exportació i lectura d'un sol registre) a l'entitat
	 * actualment seleccionada per l'usuari -- mateix patró que {@code ServeiResourceServiceImpl}/
	 * {@code BustiaResourceServiceImpl}. Sense entitat seleccionada (p.ex. DIS_SUPER) no restringeix
	 * res.
	 */
	@Override
	protected Specification<ContingutResourceEntity<ContingutResource>> additionalSpecification(String[] namedQueries) {
		Long entitatId = SessioActualUtil.getEntitatId();
		if (entitatId != null) {
			return (root, query, cb) -> cb.equal(root.get("entitat").get("id"), entitatId);
		}
		return null;
	}

	/**
	 * Completa els camps que el llistat legacy "Contingut" mostra i que no es poden obtenir per
	 * mapeig genèric per reflexió (veure {@code ContingutHelper.toContingutDto} del manteniment
	 * legacy): el nom mostrat, el nom complet del remitent i el breadcrumb de la bústia.
	 */
	@Override
	protected void afterConversion(ContingutResourceEntity<ContingutResource> entity, ContingutResource resource) {
		resource.setNom(displayNom(entity));

		if (entity.getCreatedBy() != null) {
			UsuariResourceEntity usuari = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
			if (usuari != null && usuari.getNom() != null) {
				resource.setCreatedByFullName(usuari.getNom());
			}
		}

		resource.setPath(buildPath(entity));
	}

	/**
	 * Puja per "pare" fins a la bústia arrel i inverteix el resultat, perquè el primer element sigui
	 * sempre la unitat organitzativa arrel i l'últim la bústia pare directa -- mateix algorisme que
	 * {@code ContingutHelper.getPathContingut}/{@code getPathContingutComDto} del manteniment legacy.
	 */
	private List<String> buildPath(ContingutResourceEntity<?> entity) {
		List<ContingutResourceEntity<?>> ancestors = new ArrayList<>();
		ContingutResourceEntity<?> current = entity;
		while (current.getPare() != null) {
			ContingutResourceEntity<?> pare = current.getPare();
			ancestors.add(pare);
			current = pare;
		}
		Collections.reverse(ancestors);
		List<String> path = new ArrayList<>(ancestors.size());
		for (ContingutResourceEntity<?> ancestor : ancestors) {
			path.add(displayNom(ancestor));
		}
		return path;
	}

	/**
	 * Una bústia arrel (sense pare) es mostra amb la denominació de la unitat organitzativa, no amb
	 * el seu propi nom -- mateixa regla que {@code ContingutHelper.toContingutDto} del manteniment
	 * legacy.
	 */
	private String displayNom(ContingutResourceEntity<?> entity) {
		if (entity instanceof BustiaResourceEntity) {
			BustiaResourceEntity bustia = (BustiaResourceEntity) entity;
			if (bustia.getPare() == null && bustia.getUnitatOrganitzativa() != null) {
				return bustia.getUnitatOrganitzativa().getDenominacio();
			}
		}
		return entity.getNom();
	}

}
