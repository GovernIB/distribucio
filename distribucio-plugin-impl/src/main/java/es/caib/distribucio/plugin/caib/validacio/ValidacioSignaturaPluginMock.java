/**
 * 
 */
package es.caib.distribucio.plugin.caib.validacio;

import java.util.Properties;

import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;

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
	
	public ValidacioSignaturaPluginMock(Properties properties) {
		super(properties);
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

}
