package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model.UsuariResource;
import es.caib.distribucio.logic.intf.resourceservice.UsuariResourceService;
import es.caib.distribucio.persist.entity.BustiaDefaultEntity;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.BustiaDefaultRepository;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.UsuariRepository;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * Implementació del servei de consulta i modificació del perfil de l'usuari autenticat actual.
 * <p>
 * Restringeix sempre l'accés al propi usuari: independentment de l'id sol·licitat, la consulta
 * només pot retornar (o modificar) el registre l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl
		extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity>
		implements UsuariResourceService {

	private static final String ROLE_DISPLAY_PREFIX = "DIS_";

	private final AuthenticationHelper authenticationHelper;
	private final EntitatRepository entitatRepository;
	private final UsuariRepository usuariRepository;
	private final BustiaRepository bustiaRepository;
	private final BustiaDefaultRepository bustiaDefaultRepository;

	@Override
	protected Specification<UsuariResourceEntity> additionalSpecification(String[] namedQueries) {
		String currentUserName = authenticationHelper.getCurrentUserName();
		return (root, query, cb) -> cb.equal(root.get("id"), currentUserName);
	}

	@Override
	protected void completeResource(UsuariResource resource) {
		String[] roles = authenticationHelper.getCurrentUserRoles();
		resource.setRols(Arrays.stream(roles).
				filter(r -> r.startsWith(ROLE_DISPLAY_PREFIX)).
				toArray(String[]::new));
		// bustiaPerDefecte es guarda a una taula apart (dis_bustia_default), per parella
		// entitat+usuari -- s'utilitza entitatPerDefecteId com a "entitat de context" ja que la
		// interfície REACT encara no disposa d'un selector d'entitat independent (veure
		// AplicacioServiceImpl.getBustiaPerDefecte/updateUsuariActual per a l'equivalent JSP).
		if (resource.getEntitatPerDefecteId() != null) {
			EntitatEntity entitat = entitatRepository.getReferenceById(resource.getEntitatPerDefecteId());
			UsuariEntity usuari = usuariRepository.getReferenceById(resource.getId());
			BustiaDefaultEntity bustiaDefault = bustiaDefaultRepository.findByEntitatAndUsuari(entitat, usuari);
			if (bustiaDefault != null) {
				resource.setBustiaPerDefecte(bustiaDefault.getBustia().getId());
			}
		}
	}

	@Override
	protected void afterConversion(UsuariResourceEntity entity, UsuariResource resource) {
		// completeResource() només es crida des de create()/update() (sobre el resource
		// d'entrada, abans de desar) -- getOne() no el crida mai, per tant sense això els camps
		// derivats (rols, bustiaPerDefecte) no es mostraven en obrir el diàleg de perfil, només
		// després de guardar. afterConversion() es crida sempre (getOne() i com a resposta final
		// de create()/update()), per tant és el punt correcte per garantir-ho en tots els casos.
		completeResource(resource);
	}

	@Override
	protected void beforeUpdateSave(
			UsuariResourceEntity entity,
			UsuariResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (resource.getEntitatPerDefecteId() == null) {
			return;
		}
		EntitatEntity entitat = entitatRepository.getReferenceById(resource.getEntitatPerDefecteId());
		UsuariEntity usuari = usuariRepository.getReferenceById(resource.getId());
		BustiaDefaultEntity bustiaDefault = bustiaDefaultRepository.findByEntitatAndUsuari(entitat, usuari);
		if (resource.getBustiaPerDefecte() != null) {
			BustiaEntity bustia = bustiaRepository.getReferenceById(resource.getBustiaPerDefecte());
			if (bustiaDefault != null) {
				bustiaDefault.updateBustiaDefault(bustia);
			} else {
				bustiaDefaultRepository.save(BustiaDefaultEntity.getBuilder(entitat, bustia, usuari).build());
			}
		} else if (bustiaDefault != null) {
			bustiaDefaultRepository.delete(bustiaDefault);
		}
	}

}
