/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;

/**
 * Implementació de AclService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AclServiceBean implements AclService {

	@Autowired
	AclService delegate;



	@Override
	@RolesAllowed("tothom")
	public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
		return delegate.findChildren(parentIdentity);
	}

	@Override
	@RolesAllowed("tothom")
	public Acl readAclById(
			ObjectIdentity object) throws NotFoundException {
		return delegate.readAclById(object);
	}

	@Override
	@RolesAllowed("tothom")
	public Acl readAclById(
			ObjectIdentity object,
			List<Sid> sids) throws NotFoundException {
		return delegate.readAclById(object, sids);
	}

	@Override
	@RolesAllowed("tothom")
	public Map<ObjectIdentity, Acl> readAclsById(
			List<ObjectIdentity> objects) throws NotFoundException {
		return delegate.readAclsById(objects);
	}

	@Override
	@RolesAllowed("tothom")
	public Map<ObjectIdentity, Acl> readAclsById(
			List<ObjectIdentity> objects,
			List<Sid> sids) throws NotFoundException {
		return delegate.readAclsById(objects, sids);
	}

}
