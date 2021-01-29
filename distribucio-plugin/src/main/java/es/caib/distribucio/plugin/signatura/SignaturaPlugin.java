package es.caib.distribucio.plugin.signatura;

import es.caib.distribucio.plugin.SistemaExternException;

/**
 * Plugin permetre la signatura de documents des del servidor de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SignaturaPlugin {

	public byte[] signar(
			String id, 
			String nom, 
			String motiu,
			String tipusFirma,
			byte[] contingut, 
			String tipusDocumental) throws SistemaExternException;

}
