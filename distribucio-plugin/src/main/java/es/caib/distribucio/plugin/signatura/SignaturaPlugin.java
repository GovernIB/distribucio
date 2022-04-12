package es.caib.distribucio.plugin.signatura;

import es.caib.distribucio.plugin.IntegracioPlugin;
import es.caib.distribucio.plugin.SistemaExternException;

/**
 * Plugin permetre la signatura de documents des del servidor de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SignaturaPlugin extends IntegracioPlugin{

	public SignaturaResposta signar(
			String id, 
			String nom, 
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental) throws SistemaExternException;

}
