package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.model.AclEntryResource;
import es.caib.distribucio.logic.intf.model.SubjectType;
import es.caib.distribucio.persist.base.entity.ResourceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.Id;

/**
 * Mapping lleuger per mantenir un identificador estable d'API per a AclEntry.
 * Les dades reals d'autorització es desen en les taules Spring ACL (com_acl_*).
 */
@Getter
@Setter
@NoArgsConstructor
public class AclEntryResourceEntity implements ResourceEntity<AclEntryResource, String> {

	@Id
	private @Nullable String id;
	private AclEntryResource resource;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return null == getId();
	}

	public SubjectType getSubjectType() {
		if (getResource() != null) {
			return getResource().getSubjectType();
		} else {
			return null;
		}
	}
	public String getSubjectValue() {
		if (getResource() != null) {
			return getResource().getSubjectValue();
		} else {
			return null;
		}
	}

	@Builder
	public AclEntryResourceEntity(
			String id,
			AclEntryResource resource) {
		this.id = id;
		this.resource = resource;
	}

}
