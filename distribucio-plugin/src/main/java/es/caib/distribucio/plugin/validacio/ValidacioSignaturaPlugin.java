package es.caib.distribucio.plugin.validacio;

import es.caib.distribucio.plugin.IntegracioPlugin;

/**
 * Interfície del plugin per a la validació de firmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ValidacioSignaturaPlugin extends IntegracioPlugin{

	public ValidaSignaturaResposta validaSignatura(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut
			) throws es.caib.distribucio.plugin.SistemaExternException;

}
