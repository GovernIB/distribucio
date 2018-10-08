package es.caib.distribucio.plugin.distribucio;

import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.plugins.arxiu.api.Document;

/**
 * Plugin per a la distribució de contingut contra sistemes externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DistribucioPlugin {
	
	public String contenidorCrear(DistribucioRegistreAnotacio anotacio, String unitatArrelCodi) throws SistemaExternException;
	
	public String documentCrear(DistribucioRegistreAnotacio anotacio, String unitatArrelCodi, String identificadorRetorn);
	
	public Document documentDescarregar(String arxiuUuid, String versio, boolean ambContingut, boolean ambVersioImprimible) throws SistemaExternException ;
	
	public void contenidorEliminar(String uuid) throws SistemaExternException;
	
	public void marcarProcessat(DistribucioRegistreAnotacio anotacio) throws SistemaExternException;

}
