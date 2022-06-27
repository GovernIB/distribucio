/**
 * 
 */
package es.caib.distribucio.plugin.caib.signatura;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.signatura.SignaturaPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaResposta;
import es.caib.plugins.arxiu.api.FirmaPerfil;

/**
 * Implementació mock del plugin de signatura. Retorna una signatura falsa 
 * quan se signa. Si l'id és igual a "e" llavors retorna una excepció de sistema
 * estern, si no retorna una firma falsa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaPluginMock extends DistribucioAbstractPluginProperties implements SignaturaPlugin {	  
		  
	
	@Override
	public SignaturaResposta signar(
			String id,
			String nom,
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental) throws SistemaExternException {
		
		SignaturaResposta resposta = new SignaturaResposta();
		
		if (id != null && "e".equals(id)) {
			// Cas per provocar una excepció
			String errMsg = "Excepció provocada per paràmetre a SignaturaPluginMock";
			Logger.getLogger(SignaturaPluginMock.class.getName()).log(Level.SEVERE, errMsg);
			throw new SistemaExternException(errMsg);
		}

		// Retorna una firma falsa
		byte[] firmaContingut = null;
		try {
			firmaContingut = IOUtils.toByteArray(this.getClass().getResourceAsStream("/es/caib/distribucio/plugin/signatura/firma_document_mock.xml"));
			resposta.setContingut(firmaContingut);
			resposta.setMime("application/octet-stream");
			resposta.setNom("firma_document_mock.xml");
			resposta.setTipusFirma("CADES");
			resposta.setTipusFirmaEni("TF04");
			resposta.setPerfilFirmaEni(FirmaPerfil.BES.toString());
		} catch (IOException e) {
			String errMsg = "Error llegint el fitxer mock de firma XAdES: " + e.getMessage();
			Logger.getLogger(SignaturaPluginMock.class.getName()).log(Level.SEVERE, errMsg, e);
			e.printStackTrace();
		}		
		return resposta;
	}  
	
	@Override
	public String getUsuariIntegracio() {
		return "Mock";
	}
}
