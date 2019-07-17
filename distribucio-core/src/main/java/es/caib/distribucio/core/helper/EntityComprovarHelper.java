/**
 * 
 */
package es.caib.distribucio.core.helper;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.PermissionDeniedException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.ReglaRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
import es.caib.distribucio.core.service.BustiaServiceImpl;


/**
 * Helper per a la comprovació de l'existencia d'entitats de base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntityComprovarHelper {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private ContingutRepository contingutRepository;
	@Resource
	private BustiaRepository bustiaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private ReglaRepository reglaRepository;
	@Resource
	private PermisosHelper permisosHelper;
	@Autowired
	private MetricRegistry metricRegistry;


	public EntitatEntity comprovarEntitat(
			Long entitatId,
			boolean comprovarPermisUsuari,
			boolean comprovarPermisAdmin,
			boolean comprovarPermisUsuariOrAdmin) throws NotFoundException {
		final Timer comprovarEntitatTimer = metricRegistry.timer(MetricRegistry.name(EntityComprovarHelper.class, "comprovarEntitat"));
		Timer.Context comprovarEntitatContext = comprovarEntitatTimer.time();
		
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			throw new NotFoundException(
					entitatId,
					EntitatEntity.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (comprovarPermisUsuari) {
			boolean esLectorEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.READ},
					auth);
			if (!esLectorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"READ");
			}
		}
		if (comprovarPermisAdmin) {
			boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.ADMINISTRATION},
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
						ExtendedPermission.READ},
					auth);
			if (!esAdministradorOLectorEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION || READ");
			}
		}
		
		comprovarEntitatContext.stop();
		return entitat;
	}

	public ContingutEntity comprovarContingut(
			EntitatEntity entitat,
			Long id,
			BustiaEntity bustiaPare) {
		ContingutEntity contingut = contingutRepository.findOne(id);
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

	public BustiaEntity comprovarBustia(
			EntitatEntity entitat,
			Long bustiaId,
			boolean comprovarPermisRead) {
		final Timer comprovarBustiaTimer = metricRegistry.timer(MetricRegistry.name(EntityComprovarHelper.class, "comprovarBustia"));
		Timer.Context comprovarBustiaContext = comprovarBustiaTimer.time();
		
		BustiaEntity bustia = bustiaRepository.findOne(bustiaId);
		if (bustia == null) {
			throw new NotFoundException(
					bustiaId,
					BustiaEntity.class);
		}
		if (!entitat.equals(bustia.getEntitat())) {
			throw new ValidationException(
					bustiaId,
					BustiaEntity.class,
					"L'entitat especificada (id=" + entitat.getId() + ") no coincideix amb l'entitat de la bústia (id=" + bustia.getEntitat().getId() + ")");
		}
		if (comprovarPermisRead) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
						"READ");
			}
		}
		
		comprovarBustiaContext.stop();
		return bustia;
	}

	public RegistreEntity comprovarRegistre(
			Long id,
			BustiaEntity bustiaPare) {
		RegistreEntity registre = registreRepository.findOne(id);
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
		ReglaEntity regla = reglaRepository.findOne(reglaId);
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

}
