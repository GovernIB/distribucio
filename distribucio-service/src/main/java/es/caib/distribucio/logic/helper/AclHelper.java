package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.config.AclConfig;
import es.caib.distribucio.logic.intf.base.permission.ExtendedPermission;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper encarregat de gestionar les ACLs.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AclHelper {

    private final AclConfig aclConfig;
	private final DataSource dataSource;
	private final MutableAclService mutableAclService;

	private NamedParameterJdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initJdbcTemplate() {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Obté la informació de l'ACL que correspon al recurs especificat.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param resourceId
	 *            l'id del recurs.
	 * @param sids
	 *            els SIDs per a filtrar les ACEs que retornarà l'ACL. Si es passa null es retornaran totes les ACEs.
	 * @return la informació de l'ACL.
	 */
	public Acl get(
			Class<?> resourceClass,
			Serializable resourceId,
			List<Sid> sids) {
		return getMutableAcl(
				resourceClass,
				resourceId,
				sids,
				false);
	}

	/**
	 * Modifica la informació de l'ACL del recurs especificat.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param resourceId
	 *            l'id del recurs.
	 * @param sidName
	 *            el nom del SID a modificar.
	 * @param sidGrantedAuthority
	 *            true si el nom del sid correspon a una GrantedAuthority (rol) false en cas contrari.
	 * @param permissionsGranted
	 *            la llista de permisos que te el SID especificat sobre el recurs.
	 */
	public void set(
			Class<?> resourceClass,
			Serializable resourceId,
			String sidName,
			boolean sidGrantedAuthority,
			List<PermissionEnum> permissionsGranted) {
		Sid sid = getSid(sidName, sidGrantedAuthority);
		MutableAcl acl = getMutableAcl(
				resourceClass,
				resourceId,
				List.of(sid),
				true);
		// Es recorren els permisos de l'ACL i s'esborren els que no
		// hi han de ser. Els permisos de permissionList que ja hi son
		// S'esborren de la llista.
		// Es recorren girats perque cada vegada que s'esborra un ace
		// es reorganitzen els índexos
		List<Permission> permissionList = permissionsGranted.stream().
				map(ExtendedPermission::fromEnumValue).
				collect(Collectors.toList());
		for (int i = acl.getEntries().size() - 1; i >= 0; i--) {
			AccessControlEntry ace = acl.getEntries().get(i);
			if (ace.getSid().equals(sid)) {
				if (permissionList.contains(ace.getPermission())) {
					permissionList.remove(ace.getPermission());
				} else {
					acl.deleteAce(i);
				}
			}
		}
		// S'afegeixen els permisos que queden a la llista
		for (Permission permissionItem: permissionList) {
			acl.insertAce(
					acl.getEntries().size(),
					permissionItem,
					sid,
					true);
		}
		mutableAclService.updateAcl(acl);
	}

	/**
	 * Esborra tots els permisos del SID sobre el recurs especificat.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param resourceId
	 *            l'id del recurs.
	 * @param sidName
	 *            el nom del SID a modificar.
	 * @param sidGrantedAuthority
	 *            true si el nom del sid correspon a una GrantedAuthority (rol) false en cas contrari.
	 */
	public void delete(
			Class<?> resourceClass,
			Serializable resourceId,
			String sidName,
			boolean sidGrantedAuthority) {
		set(
				resourceClass,
				resourceId,
				sidName,
				sidGrantedAuthority,
				new ArrayList<>());
	}

	/**
	 * Indica si es tenen permisos sobre el recurs especificat.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param resourceId
	 *            l'id del recurs.
	 * @param permissions
	 *            els permísos a comprovar. Es retornarà true si el recurs te permès ALGUN dels permisos.
	 * @param sids
	 *            la llista de SIDs. Si no es passa cap valor s'utilitzaran els SIDs de l'usuari actual.
	 * @return true si es tenen permisos o false en cas contrari.
	 */
	public boolean anyPermissionGranted(
			Class<?> resourceClass,
			Serializable resourceId,
			List<Permission> permissions,
			Sid... sids) {
		MutableAcl acl = getMutableAcl(
				resourceClass,
				resourceId,
				Arrays.asList(sids),
				false);
		if (acl != null) {
			try {
				return acl.isGranted(
						permissions,
						sids.length > 0 ? Arrays.asList(sids) : getCurrentUserSids(),
						true);
			} catch (NotFoundException ex) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Retorna les ids sobre les quals els SIDs especificats tenen algun permís.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param permissions
	 *            els permísos a comprovar. Es retornaran les ids que tenen permès ALGUN dels permisos.
	 * @param sids
	 *            la llista de SIDs. Si no es passa cap valor s'utilitzaran els SIDs de l'usuari actual.
	 * @return la llista d'ids.
	 */
	public Set<Serializable> findIdsWithAnyPermission(
			Class<?> resourceClass,
			List<Permission> permissions,
			Sid... sids) {
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("isTrue", true);
		paramsMap.put("isFalse", false);
		paramsMap.put("className", resourceClass.getName());
		List<Sid> sidsList = sids.length > 0 ? Arrays.asList(sids) : getCurrentUserSids();
		Optional<String> principal = sidsList.stream().
				filter(s -> s instanceof PrincipalSid).
				map(s -> ((PrincipalSid)s).getPrincipal()).
				findFirst();
		principal.ifPresent(principalSid -> paramsMap.put("principal", principal.get()));
		List<String> grantedAuthorities = sidsList.stream().
				filter(s -> s instanceof GrantedAuthoritySid).
				map(s -> ((GrantedAuthoritySid)s).getGrantedAuthority()).
				collect(Collectors.toList());
		if (!grantedAuthorities.isEmpty()) {
			paramsMap.put("grantedAuthorities", grantedAuthorities);
		}
		boolean anyPermission = permissions != null && !permissions.isEmpty();
		if (anyPermission) {
			paramsMap.put(
					"masks",
					permissions.stream().map(Permission::getMask).collect(Collectors.toSet()));
		}
		String query = aclConfig.getIdsWithPermissionQuery(
				anyPermission,
				principal.isPresent(),
				!grantedAuthorities.isEmpty());
		return jdbcTemplate.query(
				query,
				paramsMap,
				rs -> {
					Set<Serializable> ids1 = new HashSet<>();
					while (rs.next()) {
						Serializable s = (Serializable) rs.getObject(1);
						if (s != null) ids1.add(s);
					}
					return ids1;
				});
	}

    public Integer countSidsWithPermission(Class<?> resourceClass, Serializable resourceId) {
        Map<Serializable, Integer> map = this.countAllSidsWithPermission(resourceClass, List.of(resourceId));
        return map.get(resourceId);
    }

    public Map<Serializable, Integer> countAllSidsWithPermission(Class<?> resourceClass, List<? extends Serializable> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("className", resourceClass.getName());
        paramsMap.put("resourceIds", resourceIds.stream().map(Object::toString).collect(Collectors.toList()));
        paramsMap.put("isTrue", true);

        String sql = " SELECT o.object_id_identity, COUNT(DISTINCT e.sid)" +
        " FROM com_acl_class c" +
        " JOIN com_acl_object_identity o ON c.id = o.object_id_class" +
        " JOIN com_acl_entry e ON o.id = e.acl_object_identity" +
        " WHERE c.class = :className" +
        "   AND o.object_id_identity IN (:resourceIds)" +
        "   AND e.granting = :isTrue" +
        " GROUP BY o.object_id_identity";

        return jdbcTemplate.query(sql, paramsMap, rs -> {
            Map<Serializable, Integer> result = new HashMap<>();
            while (rs.next()) {
                Serializable oid = (Serializable) rs.getObject(1);
                int count = rs.getInt(2);
                result.put(oid, count);
            }
            return result;
        });
    }

	/**
	 * Retorna la llista de SIDs de l'usuari autenticat.
	 *
	 * @return la llista de SIDs (principal + grantedAuthorities).
	 */
	public List<Sid> getCurrentUserSids() {
		List<Sid> sids = new ArrayList<>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			sids.add(new PrincipalSid(auth.getName()));
			for (GrantedAuthority ga: auth.getAuthorities()) {
				sids.add(new GrantedAuthoritySid(ga.getAuthority()));
			}
		}
		return sids;
	}

	private MutableAcl getMutableAcl(
			Class<?> resourceClass,
			Serializable resourceId,
			List<Sid> sids,
			boolean createIfNotExists) {
		ObjectIdentity objectIdentity = new ObjectIdentityImpl(resourceClass.getName(), resourceId);
		MutableAcl acl;
		try {
			acl = (sids != null) ? (MutableAcl)mutableAclService.readAclById(objectIdentity, sids) : (MutableAcl)mutableAclService.readAclById(objectIdentity);
		} catch (NotFoundException ex) {
			if (createIfNotExists) {
				acl = mutableAclService.createAcl(objectIdentity);
			} else {
				acl = null;
			}
		}
		return acl;
	}

	private boolean isPermissionGranted(
			Acl acl,
			Permission permission,
			Sid... sids) {
		try {
			return acl.isGranted(
					Collections.singletonList(permission),
					Arrays.asList(sids),
					true);
		} catch (NotFoundException ex) {
			return false;
		}
	}

	private Sid getSid(
			String name,
			boolean grantedAuthority) {
		if (!grantedAuthority) {
			return new PrincipalSid(name);
		} else {
			return new GrantedAuthoritySid(name);
		}
	}

}
