
package es.caib.distribucio.backoffice.utils;

import es.caib.plugins.arxiu.api.Expedient;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;


public interface BackofficeUtils {
	
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			Expedient expedient,
			AnotacioRegistreEntrada anotacioRegistreEntrada);


}
