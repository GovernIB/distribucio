/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.bustia;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;


/**
 * Declaració dels mètodes per al servei d'enviament de contingut a
 * bústies de DISTRIBUCIO v1.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = BustiaV1WsService.SERVICE_NAME,
		targetNamespace = BustiaV1WsService.NAMESPACE_URI)
public interface BustiaV1WsService {

	public static final String SERVICE_NAME = "BustiaV1";
	public static final String NAMESPACE_URI = "http://www.caib.es/distribucio/ws/v1/bustia";

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
			@WebParam(name="entitat") @XmlElement(required=true) String entitat,
			@WebParam(name="unitatAdministrativa") @XmlElement(required=true) String unitatAdministrativa,
			@WebParam(name="registreEntrada") @XmlElement(required=true) RegistreAnotacio registreEntrada);

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
			@WebParam(name="entitat") @XmlElement(required=true) String entitat,
			@WebParam(name="unitatAdministrativa") @XmlElement(required=true) String unitatAdministrativa,
			@WebParam(name="referenciaDocument") @XmlElement(required=true) String referenciaDocument);

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
			@WebParam(name="entitat") @XmlElement(required=true) String entitat,
			@WebParam(name="unitatAdministrativa") @XmlElement(required=true) String unitatAdministrativa,
			@WebParam(name="referenciaExpedient") @XmlElement(required=true) String referenciaExpedient);

}
