package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model.UsuariResource;
import es.caib.distribucio.logic.intf.resourceservice.UsuariResourceService;
import es.caib.distribucio.persist.resourceentity.BustiaDefaultResourceEntity;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;
import es.caib.distribucio.persist.resourcerepository.BustiaDefaultResourceRepository;
import es.caib.distribucio.persist.resourcerepository.BustiaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UsuariResourceRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
	private final CacheHelper cacheHelper;
	private final EntitatResourceRepository entitatResourceRepository;
	private final UsuariResourceRepository usuariResourceRepository;

	private final BustiaResourceRepository bustiaResourceRepository;
	private final BustiaDefaultResourceRepository bustiaDefaultResourceRepository;

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
			EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(resource.getEntitatPerDefecteId());
			UsuariResourceEntity usuari = usuariResourceRepository.getReferenceById(resource.getId());
			BustiaDefaultResourceEntity bustiaDefault = bustiaDefaultResourceRepository.findByEntitatAndUsuari(entitat, usuari);
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
		EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(resource.getEntitatPerDefecteId());
		UsuariResourceEntity usuari = usuariResourceRepository.getReferenceById(resource.getId());
		BustiaDefaultResourceEntity bustiaDefault = bustiaDefaultResourceRepository.findByEntitatAndUsuari(entitat, usuari);
		if (resource.getBustiaPerDefecte() != null) {
			BustiaResourceEntity bustia = bustiaResourceRepository.getReferenceById(resource.getBustiaPerDefecte());
			if (bustiaDefault != null) {
				bustiaDefault.updateBustiaDefault(bustia);
			} else {
				bustiaDefaultResourceRepository.save(BustiaDefaultResourceEntity.getBuilder(entitat, bustia, usuari).build());
			}
		} else if (bustiaDefault != null) {
			bustiaDefaultResourceRepository.delete(bustiaDefault);
		}
	}

    @Override
    public void refresh() {
		UsuariResource usuariFromAuth = getUsuariResourceFromAuth();
		if (usuariFromAuth != null) {
			Optional<UsuariResourceEntity> usuariOptional = usuariResourceRepository.findById(authenticationHelper.getCurrentUserName());
			if (usuariOptional.isPresent()) {
				UsuariResourceEntity usuariFromDb = usuariOptional.get();
				if (hasToUpdateUsuari(usuariFromDb, usuariFromAuth)) {
					usuariFromDb.setNom(usuariFromAuth.getNom());
					usuariFromDb.setNif(usuariFromAuth.getNif());
					usuariFromDb.setEmail(usuariFromAuth.getEmail());
					usuariResourceRepository.save(usuariFromDb);
				}
			} else {
				UsuariResourceEntity usuari = new UsuariResourceEntity();
				usuari.setId(usuariFromAuth.getId());
				usuari.setNom(usuariFromAuth.getNom());
				usuari.setNif(usuariFromAuth.getNif());
				usuari.setEmail(usuariFromAuth.getEmail());
				usuari.setEstilMenu(usuariFromAuth.getEstilMenu());
				usuariResourceRepository.save(usuari);
			}
		}
    }

	private UsuariResource getUsuariResourceFromAuth() {
		String codi = authenticationHelper.getCurrentUserName();
		DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(codi);
		if (dadesUsuari == null) {
			return null;
		}
		UsuariResource usuari = new UsuariResource();
		usuari.setId(codi);
		usuari.setNom(dadesUsuari.getNomSencer());
		usuari.setNif(dadesUsuari.getNif());
		usuari.setEmail(dadesUsuari.getEmail());
		return usuari;
	}

	private boolean hasToUpdateUsuari(UsuariResourceEntity usuariFromDb, UsuariResource usuariFromAuth) {
		return !Objects.equals(usuariFromDb.getNom(), usuariFromAuth.getNom()) ||
				!Objects.equals(usuariFromDb.getNif(), usuariFromAuth.getNif()) ||
				!Objects.equals(usuariFromDb.getEmail(), usuariFromAuth.getEmail());
	}
}
