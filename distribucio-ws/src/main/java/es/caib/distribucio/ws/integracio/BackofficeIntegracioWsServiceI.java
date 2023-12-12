/**
 * 
 */
package es.caib.distribucio.ws.integracio;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;


/**
 * Declaració dels mètodes per al servei per a processar anotacions
 * de registre mitjançant una aplicació externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = BackofficeIntegracioWsServiceI.SERVICE_NAME,
		targetNamespace = BackofficeIntegracioWsServiceI.NAMESPACE_URI)
public interface BackofficeIntegracioWsServiceI {

	public static final String SERVICE_NAME = "BackofficeIntegracio";
	public static final String NAMESPACE_URI = "http://www.caib.es/distribucio/ws/backofficeIntegracio";

	/**
	 * Processa una anotació de registre d'entrada.
	 * 
	 * @param registreEntrada
	 *            Dades de l'anotació al registre d'entrada.
	 * @return el resultat de processar l'anotació.
	 */
	@WebMethod
	public AnotacioRegistreEntrada consulta(
			@WebParam(name="id") @XmlElement(required=true) AnotacioRegistreId id);

	/**
	 * Processa una anotació de registre d'entrada.
	 * 
	 * @param registreEntrada
	 *            Dades de l'anotació al registre d'entrada.
	 * @return el resultat de processar l'anotació.
	 */
	@WebMethod
	public void canviEstat(
			@WebParam(name="id") @XmlElement(required=true) AnotacioRegistreId id,
			@WebParam(name="estat") @XmlElement(required=true) Estat estat,
			@WebParam(name="observacions") @XmlElement String observacions);
	
}
