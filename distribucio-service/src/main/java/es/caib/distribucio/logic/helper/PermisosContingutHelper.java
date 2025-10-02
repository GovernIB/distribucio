package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.ContingutDto;
import es.caib.distribucio.logic.permission.ExtendedPermission;
import es.caib.distribucio.persist.entity.BustiaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermisosContingutHelper {

    @Autowired
    private PermisosHelper permisosHelper;

    public void omplirPermisosPerContingut(ContingutDto contingut, Long bustiaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        contingut.setPotModificar(
                permisosHelper.isGrantedAll(
                        bustiaId,
                        BustiaEntity.class,
                        new Permission[] {ExtendedPermission.WRITE},
                        auth));
    }
}
