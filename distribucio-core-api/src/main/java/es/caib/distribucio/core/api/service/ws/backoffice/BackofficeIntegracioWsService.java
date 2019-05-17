/**
 * 
 */
package es.caib.distribucio.core.api.service.ws.backoffice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;


/**
 * Declaració dels mètodes per al servei per a processar anotacions
 * de registre mitjançant una aplicació externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "BackofficeIntegracio",
		targetNamespace = "http://www.caib.es/distribucio/ws/backofficeIntegracio")
public interface BackofficeIntegracioWsService {

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
