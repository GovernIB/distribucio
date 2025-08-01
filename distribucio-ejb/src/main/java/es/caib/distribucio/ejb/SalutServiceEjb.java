package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.springframework.context.annotation.Primary;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.SalutService;
import lombok.experimental.Delegate;

@Primary
@Stateless
public class SalutServiceEjb extends AbstractServiceEjb<SalutService> implements SalutService {

	@Delegate
	private SalutService delegateService = null;
	
    @Override
    public List<IntegracioInfo> getIntegracions() {
        return delegateService.getIntegracions();
    }

    @Override
    public List<AppInfo> getSubsistemes() {
        return delegateService.getSubsistemes();
    }

    @Override
    public SalutInfo checkSalut(String versio, String performanceUrl) {
        return delegateService.checkSalut(versio, performanceUrl);
    }
    
	protected void setDelegateService(SalutService delegateService) {
		this.delegateService = delegateService;
	}

}