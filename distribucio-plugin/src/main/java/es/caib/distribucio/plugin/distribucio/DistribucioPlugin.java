package es.caib.distribucio.plugin.distribucio;

import java.util.Map;

import es.caib.distribucio.core.api.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.plugin.IntegracioPlugin;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.plugins.arxiu.api.Document;

/**
 * Plugin per a la distribució de contingut contra sistemes externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DistribucioPlugin extends IntegracioPlugin {

	/**
	 * Crea un contenidor de contingut, per exemple un expedient
	 * al gestor d'arxius remot
	 * 
	 * @param expedientNumero
	 *            Numero de epxiedentque s’utilitzarà per crear expedient
	 * @param unitatArrelCodi
	 *            codi DIR3 de la unitat organtizativa del contenidor
	 * @return L'identificador UUID del contenidor creat
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public String expedientCrear(
			String expedientNumero,
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
	public String saveAnnexAsDocumentInArxiu(
			DistribucioRegistreAnnex annex,
			String unitatArrelCodi,
			String contenidorArxiuUuid,
			DocumentEniRegistrableDto documentEniRegistrableDto) throws SistemaExternException;

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
	 * Elimina un contenidor creat prèviament a partir del seu UUID
	 * 
	 * @param uuid
	 *            identificador UUID del contenidor creat o enviat previament
	 * @throws SistemaExternException
	 *            Si hi ha hagut algun problema per dur a terme l'acció.
	 */
	public void contenidorEliminar(
			String uuid) throws SistemaExternException;

	/**
	 * Configura el gestor d'integracions.
	 * 
	 * @param integracioManager
	 *            la instància del gestor d'integracions
	 */
	public void configurar(
			IntegracioManager integracioManager,
			String itegracioGesdocCodi,
			String integracioArxiuCodi,
			String integracioSignaturaCodi,
			String gesdocAgrupacioAnnexos,
			String gesdocAgrupacioFirmes);

	public static interface IntegracioManager {
		public void addAccioOk(
				String integracioCodi,
				String descripcio,
				String usuariIntegracio,
				Map<String, String> parametres,
				long tempsResposta);
		public void addAccioError(
				String integracioCodi,
				String descripcio,
				String usuariIntegracio,
				Map<String, String> parametres,
				long tempsResposta,
				String errorDescripcio,
				Throwable throwable);
	}

}
