package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.EntitatResource;
import es.caib.distribucio.logic.intf.resourceservice.EntitatResourceService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació del servei de consulta i modificació d'entitats via el motor genèric de recursos.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl
		extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity>
		implements EntitatResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final EntitatService entitatService;

	@Override
	protected Specification<EntitatResourceEntity> additionalSpecification(String[] namedQueries) {
		// DIS_SUPER administra totes les entitats (sense filtre); la resta de rols només poden
		// veure les entitats a les que tenen accés (mateixa consulta que utilitza
		// UsuariPreferenciesController/EntitatHelper per al selector d'entitat de la JSP).
		if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER)) {
			return null;
		}
		List<Long> accessibleIds = entitatService.findAccessiblesUsuariActual().stream().
				map(entitat -> entitat.getId()).
				collect(Collectors.toList());
		return (root, query, cb) -> accessibleIds.isEmpty() ? cb.disjunction() : root.get("id").in(accessibleIds);
	}

}
