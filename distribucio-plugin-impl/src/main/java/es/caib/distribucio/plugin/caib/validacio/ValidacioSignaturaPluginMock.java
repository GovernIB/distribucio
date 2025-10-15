/**
 * 
 */
package es.caib.distribucio.plugin.caib.validacio;

import java.util.Properties;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Implementació mock del plugin de signatura. Retorna una signatura falsa 
 * quan se signa. Si l'id és igual a "e" llavors retorna una excepció de sistema
 * estern, si no retorna una firma falsa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioSignaturaPluginMock extends DistribucioAbstractPluginProperties implements ValidacioSignaturaPlugin {	  
		  
	public ValidacioSignaturaPluginMock() {
		super();
	}
	
	public ValidacioSignaturaPluginMock(Properties properties, boolean configuracioEspecifica) {
		super(properties);
		salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
	}

	
	@Override
	public ValidaSignaturaResposta validaSignatura(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut
			) throws es.caib.distribucio.plugin.SistemaExternException {
		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
		boolean firmaCorrecta = true;
		if (firmaCorrecta == true) {
			
		} else {
			
		}
		return resposta;
	}

	
	@Override
	public String getUsuariIntegracio() {
		return "Mock";
	}

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }
    
	@Override
	public boolean teConfiguracioEspecifica() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

}
