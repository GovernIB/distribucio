/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntitatHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PermisosEntitatHelper;
import es.caib.distribucio.logic.helper.PermisosHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.logic.permission.ExtendedPermission;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EntitatServiceImpl implements EntitatService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private BustiaRepository bustiaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private PermisosEntitatHelper permisosEntitatHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private EntitatHelper entitatHelper;



	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto create(EntitatDto entitat) {
		logger.debug("Creant una nova entitat (" +
				"entitat=" + entitat + ")");
		if (entitat.getLogoCapBytes() != null && entitat.getLogoCapBytes().length != 0) {
			entitatHelper.removeLogos(entitat.getCodiDir3());
			entitatHelper.createLogo(
					entitat.getCodiDir3(),
					entitat.getLogoExtension(),
					entitat.getLogoCapBytes());
		}
		EntitatEntity entity = EntitatEntity.getBuilder(
				entitat.getCodi(),
				entitat.getNom(),
				entitat.getDescripcio(),
				entitat.getCif(),
				entitat.getCodiDir3(),
				entitat.getColorFons(),
				entitat.getColorLletra()).build();
		return conversioTipusHelper.convertir(
				entitatRepository.save(entity),
				EntitatDto.class);
	}

	@Transactional
	@Override
	public EntitatDto update(
			EntitatDto entitat) {
		logger.debug("Actualitzant entitat existent (" +
				"entitat=" + entitat + ")");
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(
				entitat.getId(),
				false,
				false,
				false);
		boolean eliminarLogoActual = entitat.isEliminarLogoCap();
		if (!eliminarLogoActual) {
			if (entitat.getLogoCapBytes() != null && entitat.getLogoCapBytes().length != 0) {
				entitatHelper.createLogo(
						entitat.getCodiDir3(),
						entitat.getLogoExtension(),
						entitat.getLogoCapBytes());
			}
		} else if (eliminarLogoActual){
			entitatHelper.removeLogos(entitat.getCodiDir3());
		}
		
		entitatEntity.update(
				entitat.getCodi(),
				entitat.getNom(),
				entitat.getDescripcio(),
				entitat.getCif(),
				entitat.getCodiDir3(),
				entitat.getColorFons(),
				entitat.getColorLletra());
		return conversioTipusHelper.convertir(
				entitatEntity,
				EntitatDto.class);
	}

	@Transactional
	@Override
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		logger.debug("Actualitzant propietat activa d'una entitat existent (" +
				"id=" + id + ", " +
				"activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		entitat.updateActiva(activa);
		return conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto delete(
			Long id) {
		logger.debug("Esborrant entitat (" +
				"id=" + id +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		entitatRepository.delete(entitat);
		permisosHelper.deleteAcl(
				entitat.getId(),
				EntitatEntity.class);
		return conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findById(Long id) {
		logger.debug("Consulta de l'entitat (" +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		EntitatDto dto = conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
		permisosEntitatHelper.omplirPermisosPerEntitat(dto);
		return dto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByIdWithLogo(Long id) {
		logger.debug("Consulta de l'entitat (" +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		EntitatDto dto = conversioTipusHelper.convertir(
				entitat,
				EntitatDto.class);
		try {
			dto.setLogoCapBytes(entitatHelper.getLogo(dto.getCodiDir3()));
		} catch (Exception ex) {
			logger.error("No s'ha definit cap logo per l'entitat (entitatCodi=" + dto.getCodiDir3() + ")", ex);
		}
		permisosEntitatHelper.omplirPermisosPerEntitat(dto);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodi(String codi) {
		logger.debug("Consulta de l'entitat amb codi (" +
				"codi=" + codi + ")");
		EntitatDto entitat = conversioTipusHelper.convertir(
				entitatRepository.findByCodi(codi),
				EntitatDto.class);
		if (entitat != null)
			permisosEntitatHelper.omplirPermisosPerEntitat(entitat);
		return entitat;
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodiDir3(String codiDir3) {
		logger.debug("Consulta de l'entitat amb codi DIR3 (" +
				"codiDir3=" + codiDir3 + ")");
		EntitatDto entitat = conversioTipusHelper.convertir(
				entitatRepository.findByCodiDir3(codiDir3),
				EntitatDto.class);
		if (entitat != null)
			permisosEntitatHelper.omplirPermisosPerEntitat(entitat);
		return entitat;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<EntitatDto> findPaginat(PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de totes les entitats paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<EntitatDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					entitatRepository.findByFiltrePaginat(
							paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
							paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					EntitatDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					entitatRepository.findByFiltrePaginat(
							paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
							paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
							paginacioHelper.toSpringDataSort(paginacioParams)),
					EntitatDto.class);
		}
		permisosEntitatHelper.omplirPermisosPerEntitats(
				resposta.getContingut(),
				true);
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAccessiblesUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<EntitatDto> entitats;
		if (auth != null) {
			String authUserName = auth.getName();
			logger.trace("Consulta les entitats accessibles per l'usuari actual (" +
					"usuari=" + authUserName + ")");
			entitats = cacheHelper.findEntitatsAccessiblesUsuari(authUserName);
		} else {
			logger.trace("Consulta de les entitats per l'usuari actual sense usuari autenticat.");
			entitats = new ArrayList<EntitatDto>();
		}
		return entitats;
	}

	@Transactional(readOnly = true)
	@Override
	public void evictEntitatsAccessiblesUsuari() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		cacheHelper.evictEntitatsAccessiblesUsuari(auth.getName());
	}

	@Transactional
	@Override
	public List<PermisDto> findPermisSuper(
			Long id) {
		logger.debug("Consulta com a superusuari dels permisos de l'entitat (" +
				"id=" + id + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		return permisosHelper.findPermisos(
				id,
				EntitatEntity.class);
	}
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void updatePermisSuper(
			Long id,
			PermisDto permis) {
		logger.debug("Modificació com a superusuari del permis de l'entitat (" +
				"id=" + id + ", " +
				"permis=" + permis + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		permisosHelper.updatePermis(
				id,
				EntitatEntity.class,
				permis);
	}
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void deletePermisSuper(
			Long id,
			Long permisId) {
		logger.debug("Eliminació com a superusuari del permis de l'entitat (" +
				"id=" + id + ", " +
				"permisId=" + permisId + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		permisosHelper.deletePermis(
				id,
				EntitatEntity.class,
				permisId);
	}

	@Transactional
	@Override
	public List<PermisDto> findPermisAdmin(
			Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta com a administrador del permis de l'entitat (" +
				"id=" + id + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		//boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
		boolean esAdministradorEntitat = permisosHelper.isGrantedAny(
				id,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADMIN_LECTURA },
				auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		return permisosHelper.findPermisos(
				id,
				EntitatEntity.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void updatePermisAdmin(
			Long id,
			PermisDto permis) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Modificació com a administrador del permis de l'entitat (" +
				"id=" + id + ", " +
				"permis=" + permis + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		//boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
		boolean esAdministradorEntitat = permisosHelper.isGrantedAny(
				id,
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADMIN_LECTURA},
				auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		permisosHelper.updatePermis(
				id,
				EntitatEntity.class,
				permis);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void deletePermisAdmin(
			Long id,
			Long permisId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Eliminació com a administrador del permis de l'entitat (" +
				"id=" + id + ", " +
				"permisId=" + permisId + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		//boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
		boolean esAdministradorEntitat = permisosHelper.isGrantedAny(
				id,
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.ADMINISTRATION},
				auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (" +
					"id=" + id + ", " +
					"usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		permisosHelper.deletePermis(
				id,
				EntitatEntity.class,
				permisId);
	}
	
	
	

	@Override
	public void setConfigEntitat(EntitatDto entitatDto) {
		ConfigHelper.setEntitat(entitatDto);		
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
