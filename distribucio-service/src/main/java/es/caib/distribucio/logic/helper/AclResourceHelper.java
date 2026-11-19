package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AclResourceHelper {

    private final AclHelper aclHelper;

    /**
     * Indica si l'usuari autenticat té algun dels permisos sobre el recurs, avaluant tots els seus
     * rols i no només el rol amb què opera (veure {@link AclHelper#getRolsAcl(Authentication)}).
     */
    public boolean anyPermissionGranted(
            ResourceType resourceType,
            Serializable resourceId,
            List<PermissionEnum> permissions) {
        return anyPermissionGranted(
                resourceType,
                resourceId,
                permissions,
                SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Indica si l'usuari de l'autenticació té algun dels permisos sobre el recurs, avaluant tots els
     * seus rols i no només el rol amb què opera (veure {@link AclHelper#getRolsAcl(Authentication)}).
     */
    public boolean anyPermissionGranted(
            ResourceType resourceType,
            Serializable resourceId,
            List<PermissionEnum> permissions,
            Authentication auth) {
        if (auth == null) {
            return false;
        }
        return anyPermissionGranted(
                resourceType,
                resourceId,
                permissions,
                auth.getName(),
                AclHelper.getRolsAcl(auth));
    }

    /**
     * Retorna les ids dels recursos sobre els quals l'usuari autenticat té algun dels permisos,
     * avaluant tots els seus rols i no només el rol amb què opera (veure
     * {@link AclHelper#getRolsAcl(Authentication)}).
     */
    public Set<Serializable> findIdsWithAnyPermission(
            ResourceType resourceType,
            List<PermissionEnum> permissions) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            // Sense SIDs la consulta retornaria tots els recursos amb algun permís
            return new HashSet<>();
        }
        return findIdsWithAnyPermission(
                resourceType,
                permissions,
                auth.getName(),
                AclHelper.getRolsAcl(auth));
    }

    public boolean anyPermissionGranted(
            ResourceType resourceType,
            Serializable resourceId,
            List<PermissionEnum> permissions,
            String user,
            List<String> roles) {
        List<Permission> aclPermissions = Optional.ofNullable(permissions).
                orElseGet(List::of).stream().
                map(PermissionEnum::toPermission).
                collect(Collectors.toList());
        return aclHelper.anyPermissionGranted(
                getClassFromResourceType(resourceType),
                resourceId,
                aclPermissions,
                toSids(user, roles).toArray(new Sid[0]));
    }

    public Set<Serializable> findIdsWithAnyPermission(
            ResourceType resourceType,
            List<PermissionEnum> permissions,
            String user,
            List<String> roles) {
        List<Permission> aclPermissions = Optional.ofNullable(permissions).
                orElseGet(List::of).stream().
                map(PermissionEnum::toPermission).
                collect(Collectors.toList());
        return aclHelper.findIdsWithAnyPermission(
                getClassFromResourceType(resourceType),
                aclPermissions,
                toSids(user, roles).toArray(new Sid[0]));
    }

    public Integer countSidsWithPermission(ResourceType resourceType, Serializable resourceId) {
        return aclHelper.countSidsWithPermission(getClassFromResourceType(resourceType), resourceId);
    }

    public Map<Serializable, Integer> countAllSidsWithPermission(ResourceType resourceType, List<Serializable> resourcedIds) {
        return aclHelper.countAllSidsWithPermission(getClassFromResourceType(resourceType), resourcedIds);
    }

    private List<Sid> toSids(String user, List<String> roles) {
        List<Sid> sids = new ArrayList<>();
        if (user != null && !user.isBlank()) {
            sids.add(new PrincipalSid(user));
        }
        Optional.ofNullable(roles).orElseGet(List::of).stream()
                .filter(Objects::nonNull)
                .filter(role -> !role.isBlank())
                .map(GrantedAuthoritySid::new)
                .forEach(sids::add);
        return sids;
    }

    private Class<?> getClassFromResourceType(ResourceType resourceType) {
        if (resourceType != null) {
            try {
                switch (resourceType) {
                    case BUSTIA:
                        return Class.forName("es.caib.distribucio.persist.entity.BustiaEntity");
                    case ENTITAT:
                        return Class.forName("es.caib.distribucio.persist.entity.EntitatEntity");
                }
            } catch (Exception ignore) {}
        }
        return null;
    }
}
