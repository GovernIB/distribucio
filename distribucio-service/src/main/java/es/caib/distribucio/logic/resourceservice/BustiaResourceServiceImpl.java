package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.BustiaResource;
import es.caib.distribucio.logic.intf.resourceservice.BustiaResourceService;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BustiaResourceServiceImpl extends BaseMutableResourceService<BustiaResource, Long, BustiaResourceEntity> implements BustiaResourceService {

	@Override
	protected Specification<BustiaResourceEntity> additionalSpecification(String[] namedQueries) {
		// S'exclou la bústia arrel de cada unitat organitzativa (pare_id null), que és un
		// element merament tècnic i mai s'ha de gestionar directament (veure BustiaRepository,
		// que aplica sempre el mateix filtre a l'equivalent JSP).
		return (root, query, cb) -> cb.isNotNull(root.get("pareId"));
	}
}