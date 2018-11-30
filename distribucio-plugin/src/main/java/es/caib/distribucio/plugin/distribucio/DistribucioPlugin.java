package es.caib.distribucio.plugin.distribucio;

import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.plugins.arxiu.api.Document;

/**
 * Plugin per a la distribució de contingut contra sistemes externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DistribucioPlugin {

	/**
	 * Crea un contenidor de contingut, per exemple un expedient
	 * al gestor d'arxius remot
	 * 
	 * @param anotacio
	 *            L'assentament registral a partir del qual es crea el contenidor
	 * @param unitatArrelCodi
	 *            codi DIR3 de la unitat organtizativa del contenidor
	 * @return L'identificador UUID del contenidor creat
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String contenidorCrear(
			DistribucioRegistreAnotacio anotacio,
			String unitatArrelCodi) throws SistemaExternException;

	/**
	 * Crea (envia) els documents d'un assentament registral
	 * al gestor d'arxius remot dins el un contenidor creat prèviament
	 * 
	 * @param anotacio
	 *            L'assentament registral a partir del qual s'obtenen els documents a enviar
	 * @param unitatArrelCodi
	 *            codi DIR3 de la unitat organtizativa de la qual pertanyen els documents
	 * @param contenidorArxiuUuid
	 *            identificador arxiu uuid del contenidor creat
	 * @return L'identificador UUID del darrer document creat o enviat al gestor d'arxius remot
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String documentCrear(
			DistribucioRegistreAnnex annex,
			String unitatArrelCodi,
			String contenidorArxiuUuid) throws SistemaExternException;

	/**
	 * Obté el document enviat prèviament al gestor d'arxius remot
	 * 
	 * @param arxiuUuid
	 *            identificador UUID del document creat o enviat previament
	 * @param versio
	 *            versió del document a obtenir
	 * @param ambContingut
	 *            indica si es vol obtenir el contingut (byte[]) del document en qüestió
	 * @param ambVersioImprimible
	 *            indica si es vol obtenir la versió PDF imprimible del document sol·licitat
	 * @return Document sol·licitat
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public Document documentDescarregar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException;

	/**
	 * Implementa la lògica de marcar com a processada per l'usuari 
	 * un assentametn registral i el subsegüent tracte que els seus annexos tendran. 
	 * 
	 * @param anotacio
	 *            assentament registral que es marca com a processat
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public void contenidorMarcarProcessat(
			DistribucioRegistreAnotacio anotacio) throws SistemaExternException;

	/**
	 * Elimina un contenidor creat prèviament a partir del seu UUID
	 * 
	 * @param uuid
	 *            identificador UUID del contenidor creat o enviat previament
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public void contenidorEliminar(
			String uuid) throws SistemaExternException;

}
