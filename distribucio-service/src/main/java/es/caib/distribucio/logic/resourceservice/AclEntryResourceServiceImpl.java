package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.AclHelper;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.permission.ExtendedPermission;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.model.AclEntryResource;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.logic.intf.model.SubjectType;
import es.caib.distribucio.logic.intf.resourceservice.AclEntryResourceService;
import es.caib.distribucio.persist.resourceentity.AclEntryResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AclEntryResourceServiceImpl extends BaseMutableResourceService<AclEntryResource, String, AclEntryResourceEntity> implements AclEntryResourceService {

	private final AclHelper aclHelper;

    @Override
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

	@Override
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

    @Override
    public Integer countSidsWithPermission(ResourceType resourceType, Serializable resourceId) {
        return aclHelper.countSidsWithPermission(getClassFromResourceType(resourceType), resourceId);
    }

    @Override
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

	@Override
	protected boolean isEntityRepositoryOptional() {
		return true;
	}

	@Override
	protected Optional<AclEntryResourceEntity> entityRepositoryFindOne(String id) {
		AclEntryResource.AclEntryPk pk = AclEntryResource.AclEntryPk.deserializeFromString(id);
		Class<?> resourceClass = getClassFromResourceType(pk.getResourceType());
		if (resourceClass != null) {
			Acl acl = aclHelper.get(
					resourceClass,
					pk.getResourceId(),
					null);
			if (acl != null) {
				List<AclEntryResourceEntity> entries = toAclEntries(acl).stream().
						filter(e -> {
							AclEntryResource.AclEntryPk epk = AclEntryResource.AclEntryPk.deserializeFromString(e.getId());
							return epk.getSidName().equals(pk.getSidName()) && epk.isSidPrincipal() == pk.isSidPrincipal();
						}).
						collect(Collectors.toList());
				if (!entries.isEmpty()) {
					return Optional.of(entries.get(0));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	protected Page<AclEntryResourceEntity> entityRepositoryFindEntities(
			String quickFilter,
			String filter,
			String[] namedQueries,
			Pageable pageable) {
		boolean filterContainsOr = filter != null && filter.contains(" or ");
		List<String[]> filterTriplets = extractFilterTriplets(filter);
		String filterResourceType = filterTriplets.stream().
				filter(t -> t[0].equals("resourceType") && t[1].equals(":")).
				findFirst().
				map(t-> t[2]).
				orElse(null);
		String filterResourceId = filterTriplets.stream().
				filter(t -> t[0].equals("resourceId") && t[1].equals(":")).
				findFirst().
				map(t-> t[2]).
				orElse(null);
		boolean filterOk = filter != null && !filterContainsOr && filterResourceType != null && filterResourceId != null;
		if (filterOk) {
			Class<?> resourceClass = getClassFromResourceType(ResourceType.valueOf(filterResourceType));
			Long resourceId = Long.parseLong(filterResourceId);
			Acl acl = aclHelper.get(
					resourceClass,
					resourceId,
					null);
			if (acl != null) {
				List<AclEntryResourceEntity> resultList = toAclEntries(acl);
				return new PageImpl<>(
						pageable.getSort().isUnsorted() ? resultList : resultList.stream().
								sorted(createGetterBasedComparator(pageable.getSort())).
								collect(Collectors.toList()),
						pageable,
						resultList.size());
			} else {
				return Page.empty();
			}
		} else {
			throw new RuntimeException("Filtre no suportat");
		}
	}

	@Override
	protected AclEntryResourceEntity entitySaveFlushAndRefresh(AclEntryResourceEntity entity) {
		AclEntryResource resource = entity.getResource();
		String newId = buildAclEntryPk(resource).serializeToString();
		String previousId = entity.getId();
		if (previousId != null && !previousId.equals(newId)) {
			AclEntryResource.AclEntryPk previousPk = AclEntryResource.AclEntryPk.deserializeFromString(previousId);
			aclHelper.delete(
					getClassFromResourceType(previousPk.getResourceType()),
					previousPk.getResourceId(),
					previousPk.getSidName(),
					!previousPk.isSidPrincipal());
		}
		List<PermissionEnum> permissionsGranted = new ArrayList<>();
		if (resource.isReadAllowed()) permissionsGranted.add(PermissionEnum.READ);
		if (resource.isWriteAllowed()) permissionsGranted.add(PermissionEnum.WRITE);
		if (resource.isCreateAllowed()) permissionsGranted.add(PermissionEnum.CREATE);
		if (resource.isDeleteAllowed()) permissionsGranted.add(PermissionEnum.DELETE);
		if (resource.isAdminAllowed()) permissionsGranted.add(PermissionEnum.ADMINISTRATION);
		if (resource.isPerm0Allowed()) permissionsGranted.add(PermissionEnum.PERM0);
		if (resource.isPerm1Allowed()) permissionsGranted.add(PermissionEnum.PERM1);
		if (resource.isPerm2Allowed()) permissionsGranted.add(PermissionEnum.PERM2);
		if (resource.isPerm3Allowed()) permissionsGranted.add(PermissionEnum.PERM3);
		if (resource.isPerm4Allowed()) permissionsGranted.add(PermissionEnum.PERM4);
		if (resource.isPerm5Allowed()) permissionsGranted.add(PermissionEnum.PERM5);
		if (resource.isPerm6Allowed()) permissionsGranted.add(PermissionEnum.PERM6);
		if (resource.isPerm7Allowed()) permissionsGranted.add(PermissionEnum.PERM7);
		if (resource.isPerm8Allowed()) permissionsGranted.add(PermissionEnum.PERM8);
		if (resource.isPerm9Allowed()) permissionsGranted.add(PermissionEnum.PERM9);
		aclHelper.set(
				getClassFromResourceType(resource.getResourceType()),
				resource.getResourceId(),
				resource.getSubjectValue(),
				resource.getSubjectType().equals(SubjectType.ROLE),
				permissionsGranted);
		entity.setId(newId);
		resource.setId(newId);
		return entity;
	}

	@Override
	protected AclEntryResource entityDetachConvertAndMerge(
			AclEntryResourceEntity entity,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean create) {
		AclEntryResource response = entityToResource(entity);
		entityAfterMergeLogic(response, entity, answers, create);
		return response;
	}

	@Override
	protected void entityRepositoryDelete(AclEntryResourceEntity entity) {
		AclEntryResource resource = entity.getResource();
		aclHelper.delete(
				getClassFromResourceType(resource.getResourceType()),
				resource.getResourceId(),
				resource.getSubjectValue(),
				resource.getSubjectType().equals(SubjectType.ROLE));
	}

	@Override
	protected void entityRepositoryFlush() {
	}

	@Override
	protected AclEntryResource entityToResource(AclEntryResourceEntity entity) {
		return entity.getResource();
	}

	@Override
	protected AclEntryResourceEntity resourceToEntity(
			AclEntryResource resource,
			String pk,
			Map<String, Persistable<?>> referencedEntities) {
		return AclEntryResourceEntity.builder().
				id(pk).
				resource(resource).
				build();
	}

	@Override
	protected void updateEntityWithResource(
			AclEntryResourceEntity entity,
			AclEntryResource resource,
			Map<String, Persistable<?>> referencedEntities) {
		entity.setResource(resource);
	}

	private List<AclEntryResourceEntity> toAclEntries(Acl acl) {
		List<AccessControlEntry> accessControlEntries = acl.getEntries();
		if (accessControlEntries == null) {
			return Collections.emptyList();
		}
		Map<Sid, List<AccessControlEntry>> entriesBySid = accessControlEntries.stream().
				collect(Collectors.groupingBy(AccessControlEntry::getSid));
		String resourceClassName = acl.getObjectIdentity().getType();
		Serializable resourceId = acl.getObjectIdentity().getIdentifier();
		return entriesBySid.entrySet().stream().
				map(entry -> formAccessControlEntryToAclEntryResourceEntity(
						resourceClassName,
						resourceId,
						entry.getKey(),
						entry.getValue())).
				collect(Collectors.toList());
	}

	private AclEntryResourceEntity formAccessControlEntryToAclEntryResourceEntity(
			String resourceClassName,
			Serializable resourceId,
			Sid sid,
			List<AccessControlEntry> aces) {
		AclEntryResource aclEntry = new AclEntryResource();
		if (sid instanceof PrincipalSid) {
			aclEntry.setSubjectType(SubjectType.USER);
			aclEntry.setSubjectValue(((PrincipalSid)sid).getPrincipal());
		} else if (sid instanceof GrantedAuthoritySid) {
			aclEntry.setSubjectType(SubjectType.ROLE);
			aclEntry.setSubjectValue(((GrantedAuthoritySid)sid).getGrantedAuthority());
		}
		ResourceType resourceType = getResourceTypeFromClassName(resourceClassName);
		aclEntry.setResourceType(resourceType);
		aclEntry.setResourceId(resourceId);
		AclEntryResource.AclEntryPk pk = buildAclEntryPk(aclEntry);
		aclEntry.setId(pk.serializeToString());
		aces.forEach(a -> {
			int mask = a.getPermission().getMask();
			if ((mask & BasePermission.READ.getMask()) != 0) aclEntry.setReadAllowed(true);
			if ((mask & BasePermission.WRITE.getMask()) != 0) aclEntry.setWriteAllowed(true);
			if ((mask & BasePermission.CREATE.getMask()) != 0) aclEntry.setCreateAllowed(true);
			if ((mask & BasePermission.DELETE.getMask()) != 0) aclEntry.setDeleteAllowed(true);
			if ((mask & BasePermission.ADMINISTRATION.getMask()) != 0) aclEntry.setAdminAllowed(true);
			if ((mask & ExtendedPermission.PERM0.getMask()) != 0) aclEntry.setPerm0Allowed(true);
			if ((mask & ExtendedPermission.PERM1.getMask()) != 0) aclEntry.setPerm1Allowed(true);
			if ((mask & ExtendedPermission.PERM2.getMask()) != 0) aclEntry.setPerm2Allowed(true);
			if ((mask & ExtendedPermission.PERM3.getMask()) != 0) aclEntry.setPerm3Allowed(true);
			if ((mask & ExtendedPermission.PERM4.getMask()) != 0) aclEntry.setPerm4Allowed(true);
			if ((mask & ExtendedPermission.PERM5.getMask()) != 0) aclEntry.setPerm5Allowed(true);
			if ((mask & ExtendedPermission.PERM6.getMask()) != 0) aclEntry.setPerm6Allowed(true);
			if ((mask & ExtendedPermission.PERM7.getMask()) != 0) aclEntry.setPerm7Allowed(true);
			if ((mask & ExtendedPermission.PERM8.getMask()) != 0) aclEntry.setPerm8Allowed(true);
			if ((mask & ExtendedPermission.PERM9.getMask()) != 0) aclEntry.setPerm9Allowed(true);
		});
		return resourceToEntity(
				aclEntry,
				pk.serializeToString(),
				null);
	}

	private AclEntryResource.AclEntryPk buildAclEntryPk(AclEntryResource aclEntry) {
		return new AclEntryResource.AclEntryPk(
				aclEntry.getResourceType(),
				aclEntry.getResourceId(),
				aclEntry.getSubjectType().equals(SubjectType.USER),
				aclEntry.getSubjectValue());
	}

	private static final Pattern TRIPLET_PATTERN = Pattern.compile(
			"([a-zA-Z_][a-zA-Z0-9_\\.]*)" +
					"\\s*" +
					"(:|=|!=|>=|<=|>|<|~|!~|\\bin\\b)" +
					"\\s*" +
					"(\\[[^\\]]*\\]|'[^']*'|\"[^\"]*\"|\\d+\\.?\\d*|[^\\s\\)]+)",
			Pattern.CASE_INSENSITIVE);

	private List<String[]> extractFilterTriplets(String filter) {
		Matcher matcher = TRIPLET_PATTERN.matcher(filter);
		List<String[]> triplets = new ArrayList<>();
		while (matcher.find()) {
			String field = matcher.group(1);
			String op = matcher.group(2);
			String value = matcher.group(3);
			if (value.startsWith("'") && value.endsWith("'")) {
				value = value.substring(1, value.length() - 1);
			}
			triplets.add(new String[]{field, op, value});
		}
		return triplets;
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
			} catch (Exception ex) {
				log.error("Couldn't find class for resource type " + resourceType);
			}
		}
		return null;
	}

	private ResourceType getResourceTypeFromClassName(String className) {
		if (className != null) {
			if (className.equals("es.caib.distribucio.persist.entity.BustiaEntity")) {
				return ResourceType.BUSTIA;
			} else if (className.equals("es.caib.distribucio.persist.entity.EntitatEntity")) {
				return ResourceType.ENTITAT;
			} else {
				log.error("Couldn't find ResourceType for className " + className);
			}
		}
		return null;
	}

	private <T> Comparator<T> createGetterBasedComparator(Sort sort) {
		return sort.stream().
				map(this::<T>createComparatorForOrder).
				reduce(Comparator::thenComparing).
				orElse((a, b) -> 0);
	}
	@SuppressWarnings("unchecked")
	private <T> Comparator<T> createComparatorForOrder(Sort.Order order) {
		switch (order.getProperty()) {
			case "subjectType":
				return (Comparator<T>)createComparator(AclEntryResourceEntity::getSubjectType, order.getDirection());
			case "subjectValue":
				return (Comparator<T>)createComparator(AclEntryResourceEntity::getSubjectValue, order.getDirection());
			default:
				return (a, b) -> 0;
		}
	}
	private <T, U extends Comparable<U>> Comparator<T> createComparator(
			Function<T, U> extractor, Sort.Direction direction) {
		return (a, b) -> {
			U valueA = extractor.apply(a);
			U valueB = extractor.apply(b);
			if (valueA == null && valueB == null) return 0;
			if (valueA == null) return direction == Sort.Direction.ASC ? -1 : 1;
			if (valueB == null) return direction == Sort.Direction.ASC ? 1 : -1;
			int result = valueA.compareTo(valueB);
			return direction == Sort.Direction.ASC ? result : -result;
		};
	}

}
