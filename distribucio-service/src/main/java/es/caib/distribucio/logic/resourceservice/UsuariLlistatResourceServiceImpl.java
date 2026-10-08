package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.model.Resource;
import es.caib.distribucio.logic.intf.model.UsuariLlistatResource;
import es.caib.distribucio.logic.intf.resourceservice.UsuariLlistatResourceService;
import es.caib.distribucio.persist.resourceentity.UsuariLlistatResourceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de consulta del llistat d'usuaris de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Service
public class UsuariLlistatResourceServiceImpl extends BaseMutableResourceService<UsuariLlistatResource, String, UsuariLlistatResourceEntity> implements UsuariLlistatResourceService {

	/**
	 * Exclou els usuaris de sistema amb el mateix criteri que RIPEA ({@code UsuariResourceServiceImpl
	 * .additionalSpringFilter}): codis que contenen "SYSTEM" o que comencen per "$". La interfície
	 * JSP no fa aquesta exclusió; s'aplica aquí per si en el futur se'n creen.
	 */
	@Override
	protected Specification<UsuariLlistatResourceEntity> additionalSpecification(String[] namedQueries) {
		return (root, query, cb) -> cb.and(
				cb.notLike(root.<String>get("id"), "%SYSTEM%"),
				cb.notLike(root.<String>get("id"), "$%"));
	}

	/**
	 * Afegeix el NIF als camps del filtre ràpid. No es pot declarar a {@code quickFilterFields}
	 * perquè el motor genèric només hi accepta camps del recurs, i el NIF no es publica.
	 */
	@Override
	protected String buildSpringFilterForQuickFilter(
			Class<? extends Resource<?>> resourceClass,
			String prefix,
			String quickFilter) {
		String springFilter = super.buildSpringFilterForQuickFilter(resourceClass, prefix, quickFilter);
		if (springFilter == null || prefix != null || !UsuariLlistatResource.class.equals(resourceClass)) {
			return springFilter;
		}
		String filtreNif = "lower(nif)~lower('%" + quickFilter.replace("'", "\\'") + "%')";
		return springFilter.isEmpty() ? filtreNif : springFilter + " or (" + filtreNif + ")";
	}

}
