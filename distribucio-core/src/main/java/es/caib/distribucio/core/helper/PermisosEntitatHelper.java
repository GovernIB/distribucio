/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.core.security.ExtendedPermission;

/**
 * Utilitat per omplir els permisos de les entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PermisosEntitatHelper {

	@Resource
	private PermisosHelper permisosHelper;



	public void omplirPermisosPerEntitats(
			List<EntitatDto> entitats,
			boolean ambLlistaPermisos) {
		// Filtra les entitats per saber els permisos per a l'usuari actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ObjectIdentifierExtractor<EntitatDto> oie = new ObjectIdentifierExtractor<EntitatDto>() {
			public Long getObjectIdentifier(EntitatDto entitat) {
				return entitat.getId();
			}
		};
		List<EntitatDto> entitatsRead = new ArrayList<EntitatDto>();
		entitatsRead.addAll(entitats);
		permisosHelper.filterGrantedAll(
				entitatsRead,
				oie,
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		List<EntitatDto> entitatsAdministracio = new ArrayList<EntitatDto>();
		entitatsAdministracio.addAll(entitats);
		//permisosHelper.filterGrantedAll(
		permisosHelper.filterGrantedAny(
				entitatsAdministracio,
				oie,
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.ADMINISTRATION, ExtendedPermission.ADMIN_LECTURA},
				auth);
		for (EntitatDto entitat: entitats) {
			entitat.setUsuariActualRead(
					entitatsRead.contains(entitat));
			entitat.setUsuariActualAdministration(
					entitatsAdministracio.contains(entitat));
			entitat.setUsuariActualAdminLectura(
					entitatsAdministracio.contains(entitat));
		}
		// Obté els permisos per a totes les entitats només amb una consulta
		if (ambLlistaPermisos) {
			List<Long> ids = new ArrayList<Long>();
			for (EntitatDto entitat: entitats)
				ids.add(entitat.getId());
			Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
					ids,
					EntitatEntity.class);
			for (EntitatDto entitat: entitats)
				entitat.setPermisos(permisos.get(entitat.getId()));
		}
	}

	public void omplirPermisosPerEntitat(EntitatDto entitat) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		entitat.setUsuariActualRead(
				permisosHelper.isGrantedAll(
						entitat.getId(),
						EntitatEntity.class,
						new Permission[] {ExtendedPermission.READ},
						auth));
		entitat.setUsuariActualAdministration(
				permisosHelper.isGrantedAll(
						entitat.getId(),
						EntitatEntity.class,
						new Permission[] {ExtendedPermission.ADMINISTRATION},
						auth));

		entitat.setUsuariActualAdminLectura(
				permisosHelper.isGrantedAll(
						entitat.getId(),
						EntitatEntity.class,
						new Permission[] {ExtendedPermission.ADMIN_LECTURA},
						auth));
	}

}
