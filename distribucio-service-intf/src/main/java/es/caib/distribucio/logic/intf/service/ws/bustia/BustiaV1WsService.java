/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.bustia;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;


/**
 * Declaració dels mètodes per al servei d'enviament de contingut a
 * bústies de DISTRIBUCIO v1.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BustiaV1WsService {
	
	public enum Origen {
		SOAP,
		REST
	}

	/**
	 * Envia una anotació de registre d'entrada a la bústia per defecte
	 * associada amb la unitat administrativa.
	 * 
	 * @param entitat
	 *            Codi de l'entitat.
	 * @param unitatAdministrativa
	 *            Codi de la unitat administrativa.
	 * @param registreEntrada
	 *            Dades de l'anotació al registre d'entrada.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_BUSTIA_WS + "')")
	public void enviarAnotacioRegistreEntrada(
			String entitat,
			String unitatAdministrativa,
			RegistreAnotacio registreEntrada,
			Origen origen);

	/**
	 * Envia una anotació de registre d'entrada a la bústia per defecte
	 * associada amb la unitat administrativa.
	 * 
	 * @param entitat
	 *            Codi de l'entitat.
	 * @param unitatAdministrativa
	 *            Codi de la unitat administrativa.
	 * @param referenciaDocument
	 *            Referència per a consultar el document.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_BUSTIA_WS + "')")
	public void enviarDocument(
			String entitat,
			String unitatAdministrativa,
			String referenciaDocument);

	/**
	 * Envia una anotació de registre d'entrada a la bústia per defecte
	 * associada amb la unitat administrativa.
	 * 
	 * @param entitat
	 *            Codi de l'entitat.
	 * @param unitatAdministrativa
	 *            Codi de la unitat administrativa.
	 * @param referenciaDocument
	 *            Referència per a consultar l'expedient.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_BUSTIA_WS + "')")
	public void enviarExpedient(
			String entitat,
			String unitatAdministrativa,
			String referenciaExpedient);

}
