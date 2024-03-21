/**
 * 
 */
package es.caib.distribucio.logic.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.exception.PermissionDeniedException;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.permission.ExtendedPermission;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.MetaDadaEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.ContingutRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.MetaDadaRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.persist.repository.ReglaRepository;


/**
 * Helper per a la comprovació de l'existencia d'entitats de base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntityComprovarHelper {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private BustiaRepository bustiaRepository;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private ReglaRepository reglaRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private MetaDadaRepository metaDadaRepository;

	@Transactional
	public EntitatEntity comprovarEntitat(
			Long entitatId,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAdmin,
			boolean comprovarPermisUsuariOrAdmin) throws NotFoundException {
		final Timer comprovarEntitatTimer = metricRegistry.timer(MetricRegistry.name(EntityComprovarHelper.class, "comprovarEntitat"));
		Timer.Context comprovarEntitatContext = comprovarEntitatTimer.time();
		EntitatEntity entitat = entitatRepository.findById(entitatId).orElse(null);
		if (entitat == null) {
			throw new NotFoundException(
					entitatId,
					EntitatEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisUsuari) {
			boolean esLectorEntitat = permisosHelper.isGrantedAny(
					entitatId,
					EntitatEntity.class,
					new Permission[] {
							ExtendedPermission.READ, 
							ExtendedPermission.ADMIN_LECTURA},
					auth);
			if (!esLectorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"READ || ADMIN_LECTURA");
			}
		}
		if (comprovarPermisAdmin) {
			boolean esAdministradorEntitat = permisosHelper.isGrantedAny(
				entitatId,
					EntitatEntity.class,
					new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.ADMIN_LECTURA},
					auth);
			if (!esAdministradorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION");
			}
		}
		if (comprovarPermisUsuariOrAdmin) {
			boolean esAdministradorOLectorEntitat = permisosHelper.isGrantedAny(
					entitatId,
					EntitatEntity.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.ADMIN_LECTURA, 
						ExtendedPermission.READ},
					auth);
			if (!esAdministradorOLectorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION || ADMIN_LECTURA || READ");
			}
		}
		
		comprovarEntitatContext.stop();
		return entitat;
	}

	public ContingutEntity comprovarContingut(
			EntitatEntity entitat,
			Long id,
			BustiaEntity bustiaPare) {
		ContingutEntity contingut = contingutRepository.findById(id).orElse(null);
		if (contingut == null) {
			throw new NotFoundException(
					id,
					ContingutEntity.class);
		}
		if (!contingut.getEntitat().equals(entitat)) {
			throw new ValidationException(
					id,
					ContingutEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat del contingut");
		}
		if (bustiaPare != null) {
			if (contingut.getPare() != null) {
				if (!contingut.getPare().getId().equals(bustiaPare.getId())) {
					throw new ValidationException(
							id,
							ContingutEntity.class,
							"La bústia especificada (id=" + bustiaPare.getId() + ") no coincideix amb la bústia del contingut");
				}
			}
		}
		return contingut;
	}

	@Transactional
	public BustiaEntity comprovarBustia(
			EntitatEntity entitat,
			Long bustiaId,
			boolean comprovarPermisRead) {
		final Timer comprovarBustiaTimer = metricRegistry.timer(MetricRegistry.name(EntityComprovarHelper.class, "comprovarBustia"));
		Timer.Context comprovarBustiaContext = comprovarBustiaTimer.time();
		
		BustiaEntity bustia = bustiaRepository.findById(bustiaId).orElse(null);
		if (bustia == null) {
			throw new NotFoundException(
					bustiaId,
					BustiaEntity.class);
		}
		if (!entitat.getId().equals(bustia.getEntitat().getId())) {
			throw new ValidationException(
					bustiaId,
					BustiaEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de la bústia (id=" + bustia.getEntitat().getId() + ")");
		}
		if (comprovarPermisRead) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			/*cxxxx*/
			boolean esPermisRead = permisosHelper.isGrantedAll(
					bustiaId,
					BustiaEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
			if (!esPermisRead) {
				throw new PermissionDeniedException(
						bustiaId,
						BustiaEntity.class,
						auth.getName(),
						"READ",
						bustia.getNom());
			}
		}
		comprovarBustiaContext.stop();
		return bustia;
	}
	
	
	
	
	
	



	public BustiaEntity comprovarBustia(
			Long bustiaId,
			boolean comprovarPermisRead) {
		final Timer comprovarBustiaTimer = metricRegistry.timer(MetricRegistry.name(EntityComprovarHelper.class, "comprovarBustia"));
		Timer.Context comprovarBustiaContext = comprovarBustiaTimer.time();
		
		BustiaEntity bustia = bustiaRepository.findById(bustiaId).orElse(null);
		if (bustia == null) {
			throw new NotFoundException(
					bustiaId,
					BustiaEntity.class);
		}
		if (comprovarPermisRead) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			/*cxxxx*/
			boolean esPermisRead = permisosHelper.isGrantedAll(
					bustiaId,
					BustiaEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
			if (!esPermisRead) {
				throw new PermissionDeniedException(
						bustiaId,
						BustiaEntity.class,
						auth.getName(),
						"READ",
						bustia.getNom());
			}
		}
		comprovarBustiaContext.stop();
		return bustia;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	public RegistreEntity comprovarRegistre(
			Long id,
			BustiaEntity bustiaPare) {
		RegistreEntity registre = registreRepository.findById(id).orElse(null);
		if (registre == null) {
			throw new NotFoundException(
					id,
					RegistreEntity.class);
		}
		if (bustiaPare != null) {
			if (registre.getPare() != null) {
				if (!registre.getPare().getId().equals(bustiaPare.getId())) {
					throw new ValidationException(
							id,
							RegistreEntity.class,
							"La bústia especificada (id=" + bustiaPare.getId() + ") no coincideix amb la bústia de l'anotació de registre");
				}
			}
		}
		return registre;
	}

	public ReglaEntity comprovarRegla(
			EntitatEntity entitat,
			Long reglaId) {
		ReglaEntity regla = reglaRepository.findById(reglaId).orElse(null);
		if (regla == null) {
			throw new NotFoundException(
					reglaId,
					ReglaEntity.class);
		}
		if (!regla.getEntitat().equals(entitat)) {
			throw new ValidationException(
					reglaId,
					ReglaEntity.class,
					"La regla especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de la regla");
		}
		return regla;
	}
	
	public MetaDadaEntity comprovarMetaDada(
			EntitatEntity entitat,
			Long metaDadaId) {
		MetaDadaEntity metadada = metaDadaRepository.findById(metaDadaId).orElse(null);
		if (metadada == null) {
			throw new NotFoundException(
					metaDadaId,
					MetaDadaEntity.class);
		}
		if (!metadada.getEntitat().equals(entitat)) {
			throw new ValidationException(
					metaDadaId,
					ReglaEntity.class,
					"La metadada especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de la metadada");
		}
		return metadada;
	}

}