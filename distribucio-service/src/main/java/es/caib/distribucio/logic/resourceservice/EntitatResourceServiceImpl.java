package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.PermisosHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.model.EntitatResource;
import es.caib.distribucio.logic.intf.resourceservice.EntitatResourceService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.repository.AvisRepository;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	private final EntitatResourceRepository entitatResourceRepository;
	private final AvisRepository avisRepository;
	private final CacheHelper cacheHelper;
	private final ConfigHelper configHelper;
	private final PermisosHelper permisosHelper;

	@PostConstruct
	public void init() {
		// Un sol executor per als dos codis: la lògica és la mateixa i el codi de l'acció arriba
		// com a paràmetre d'exec().
		ActivaActionExecutor activaActionExecutor = new ActivaActionExecutor();
		register(EntitatResource.ACTION_ACTIVAR_CODE, activaActionExecutor);
		register(EntitatResource.ACTION_DESACTIVAR_CODE, activaActionExecutor);
		register(EntitatResource.PERSPECTIVE_PERMISOS_COUNT_CODE, new PermisosCountPerspectiveApplicator());
		register(EntitatResource.PERSPECTIVE_PERMISOS_CODE, new PermisosPerspectiveApplicator());
		register(EntitatResource.ACTION_PERMIS_GUARDAR_CODE, new PermisGuardarActionExecutor());
		register(EntitatResource.ACTION_PERMIS_ESBORRAR_CODE, new PermisEsborrarActionExecutor());
	}

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

	@Override
	protected void afterCreateSave(
			EntitatResourceEntity entity,
			EntitatResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean anyOrderChanged) {
		// Mateixes tasques que fa la interfície JSP en crear una entitat (EntitatServiceImpl.create
		// i EntitatController.post): crear-ne les propietats de configuració i buidar la cache
		// d'entitats accessibles perquè el selector d'entitat vegi la nova.
		configHelper.crearConfigsEntitat(entity.getCodi());
		cacheHelper.evictAllEntitatsUsuariCache();
	}

	@Override
	protected void afterUpdateSave(
			EntitatResourceEntity entity,
			EntitatResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean anyOrderChanged) {
		// El codi i el nom de l'entitat es mostren al selector d'entitat, que es serveix de la
		// cache: sense buidar-la el canvi no es veuria fins a reiniciar (EntitatController fa el
		// mateix amb evictEntitatsAccessiblesUsuari).
		cacheHelper.evictAllEntitatsUsuariCache();
	}

	@Override
	protected void beforeDelete(
			EntitatResourceEntity entity,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		// Les mateixes dependències que esborra EntitatServiceImpl.delete abans de treure la
		// fila: sense això quedarien avisos, propietats de configuració i ACLs orfes, que una
		// entitat futura amb el mateix identificador o codi heretaria.
		avisRepository.deleteAllByEntitatId(entity.getId());
		configHelper.deleteConfigEntitat(entity.getCodi());
		permisosHelper.deleteAcl(entity.getId(), EntitatEntity.class);
	}

	@Override
	protected void afterDelete(
			EntitatResourceEntity entity,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		cacheHelper.evictAllEntitatsUsuariCache();
	}

	/**
	 * Activa o desactiva l'entitat segons el codi de l'acció executada, l'equivalent d'entitat/{id}/enable
	 * i entitat/{id}/disable de la interfície JSP (EntitatController.enable/disable).
	 * <p>
	 * Les accions no tenen formulari (no declaren formClass), de manera que {@code params} sempre
	 * és null i el front les executa directament sobre la fila, sense cap diàleg.
	 */
	private class ActivaActionExecutor implements ActionExecutor<EntitatResourceEntity, Serializable, Serializable> {

		@Override
		public void onChange(
				Serializable id,
				Serializable previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				Serializable target) {
			// Sense formulari no hi ha cap camp que pugui canviar.
		}

		@Override
		public Serializable exec(
				String code,
				EntitatResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			entity.setActiva(EntitatResource.ACTION_ACTIVAR_CODE.equals(code));
			entitatResourceRepository.save(entity);
			// findEntitatsAccessiblesUsuari només retorna les entitats actives: sense buidar la
			// cache el selector d'entitat continuaria oferint una entitat desactivada.
			cacheHelper.evictAllEntitatsUsuariCache();
			return null;
		}

	}

	/**
	 * Omple el comptador de permisos de cada entitat, el que la interfície JSP mostra al costat
	 * de la icona de clau del llistat (EntitatDto.getPermisosCount() a entitatList.jsp).
	 * <p>
	 * Consulta els permisos de totes les entitats de la pàgina amb una sola consulta d'ACL, com
	 * fa PermisosEntitatHelper.omplirPermisosPerEntitats per al llistat de la JSP.
	 * <p>
	 * Els permisos de l'entitat només els administra el superusuari (veure EntitatService.
	 * findPermisSuper): per a la resta de rols el comptador es deixa sense valor en comptes de
	 * revelar-lo, ja que la perspectiva, com que no declara restriccions pròpies, només demana
	 * el permís de lectura sobre el recurs -- que tots els rols tenen.
	 */
	private class PermisosCountPerspectiveApplicator
			implements PerspectiveApplicator<EntitatResourceEntity, EntitatResource> {

		@Override
		public boolean applyMultiple(
				String code,
				List<EntitatResourceEntity> entities,
				List<EntitatResource> resources) {
			if (!authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER)) {
				return true;
			}
			List<Long> ids = entities.stream().
					map(EntitatResourceEntity::getId).
					collect(Collectors.toList());
			Map<Long, List<PermisDto>> permisosPerEntitat = permisosHelper.findPermisos(
					ids,
					EntitatEntity.class);
			IntStream.range(0, entities.size()).forEach(i -> {
				List<PermisDto> permisos = permisosPerEntitat.get(entities.get(i).getId());
				resources.get(i).setPermisosCount(permisos != null ? permisos.size() : 0);
			});
			return true;
		}

		@Override
		public void applySingle(
				String code,
				EntitatResourceEntity entity,
				EntitatResource resource) {
			applyMultiple(
					code,
					Collections.singletonList(entity),
					Collections.singletonList(resource));
		}

	}

	/**
	 * Omple el llistat de permisos de l'entitat, el que mostra entitatPermis.jsp, amb el mateix
	 * ordre per defecte que la taula de la JSP (per nom del principal, ascendent).
	 * <p>
	 * Delega en EntitatService.findPermisSuper, que és qui restringeix la consulta al rol
	 * DIS_SUPER: per a qualsevol altre rol la petició acaba amb un accés denegat.
	 */
	private class PermisosPerspectiveApplicator
			implements PerspectiveApplicator<EntitatResourceEntity, EntitatResource> {

		@Override
		public void applySingle(
				String code,
				EntitatResourceEntity entity,
				EntitatResource resource) {
			List<PermisDto> permisos = new ArrayList<>(entitatService.findPermisSuper(entity.getId()));
			permisos.sort(PermisDto.sortByPrincipalNom());
			resource.setPermisos(permisos.stream().
					map(this::toPermis).
					collect(Collectors.toList()));
		}

		private EntitatResource.Permis toPermis(PermisDto permis) {
			EntitatResource.Permis resourcePermis = new EntitatResource.Permis();
			resourcePermis.setId(permis.getId());
			resourcePermis.setPrincipalTipus(permis.getPrincipalTipus());
			resourcePermis.setPrincipalNom(permis.getPrincipalNom());
			resourcePermis.setAdministracio(permis.isAdministration());
			resourcePermis.setAdminLectura(permis.isAdminLectura());
			resourcePermis.setUsuari(permis.isRead());
			return resourcePermis;
		}

	}

	/**
	 * Dona d'alta o modifica un permís de l'entitat, l'equivalent del POST a entitat/{id}/permis
	 * de la interfície JSP (EntitatPermisSuperController.save).
	 * <p>
	 * Com a la JSP no hi ha diferència entre crear i modificar: EntitatService.updatePermisSuper
	 * substitueix tots els permisos que el principal tengui sobre l'entitat pels del formulari,
	 * de manera que desar un permís sense cap casella marcada equival a treure'l.
	 */
	private class PermisGuardarActionExecutor
			implements ActionExecutor<EntitatResourceEntity, EntitatResource.FormPermis, Serializable> {

		@Override
		public void onChange(
				Serializable id,
				EntitatResource.FormPermis previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				EntitatResource.FormPermis target) {
			// El formulari no té cap camp que depengui dels altres.
		}

		@Override
		public Serializable exec(
				String code,
				EntitatResourceEntity entity,
				EntitatResource.FormPermis params) throws ActionExecutionException {
			PermisDto permis = new PermisDto();
			permis.setPrincipalTipus(params.getPrincipalTipus());
			permis.setPrincipalNom(params.getPrincipalNom());
			permis.setAdministration(params.isAdministracio());
			permis.setAdminLectura(params.isAdminLectura());
			permis.setRead(params.isUsuari());
			entitatService.updatePermisSuper(entity.getId(), permis);
			return null;
		}

	}

	/**
	 * Esborra un permís de l'entitat, l'equivalent d'entitat/{id}/permis/{permisId}/delete de la
	 * interfície JSP (EntitatPermisSuperController.delete).
	 */
	private class PermisEsborrarActionExecutor
			implements ActionExecutor<EntitatResourceEntity, EntitatResource.FormPermisEsborrar, Serializable> {

		@Override
		public void onChange(
				Serializable id,
				EntitatResource.FormPermisEsborrar previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				EntitatResource.FormPermisEsborrar target) {
			// El formulari no es mostra mai: la interfície només demana confirmació.
		}

		@Override
		public Serializable exec(
				String code,
				EntitatResourceEntity entity,
				EntitatResource.FormPermisEsborrar params) throws ActionExecutionException {
			entitatService.deletePermisSuper(entity.getId(), params.getPermisId());
			return null;
		}

	}

}
