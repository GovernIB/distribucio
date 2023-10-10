package es.caib.distribucio.ejb;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.ConfigService;
import lombok.experimental.Delegate;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ConfigServiceEjb extends AbstractServiceEjb<ConfigService> implements ConfigService {

	@Delegate
	private ConfigService delegateService = null;

	protected void setDelegateService(ConfigService delegateService) {
		this.delegateService = delegateService;
	}

}
