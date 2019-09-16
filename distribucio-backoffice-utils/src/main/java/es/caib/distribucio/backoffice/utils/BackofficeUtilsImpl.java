
package es.caib.distribucio.backoffice.utils;

import es.caib.plugins.arxiu.api.Expedient;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class BackofficeUtilsImpl implements BackofficeUtils {
	private IArxiuPlugin iArxiuPlugin;
	
	// name of carpeta to which annexos should be moved, if null annexos are moved directly to expedient without creating carpeta
	private String carpetaAnnexos;
	
	
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			Expedient expedient,
			AnotacioRegistreEntrada anotacioRegistreEntrada){
		
		
		// CREATE EXPEDIENT IN ARXIU
		
		// CREATE CARPETA IF NOT NULL IN ARXIU
		
		// MOVE DOCEUMNTS IN ARXIU 
		
		
		
		ArxiuResultat arxiuResultat = new ArxiuResultat();
	
		return arxiuResultat;
	}

	public BackofficeUtilsImpl(
			IArxiuPlugin iArxiuPlugin) {
		super();
		this.iArxiuPlugin = iArxiuPlugin;
	}

	public IArxiuPlugin getIarxiuPlugin() {
		return iArxiuPlugin;
	}

	public void setIarxiuPlugin(IArxiuPlugin iArxiuPlugin) {
		this.iArxiuPlugin = iArxiuPlugin;
	}

	public String getCarpetaAnnexos() {
		return carpetaAnnexos;
	}
	public void setCarpetaAnnexos(String carpetaAnnexos) {
		this.carpetaAnnexos = carpetaAnnexos;
	}
	

}
