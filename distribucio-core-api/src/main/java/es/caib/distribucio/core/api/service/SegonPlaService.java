/**
 * 
 */
package es.caib.distribucio.core.api.service;

/**
 * Declaració dels mètodes per a gestionar continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SegonPlaService {

	/**
	 * Comprovar si hi ha execucions massives pendents 
	 * d'executar-se
	 */

	public void comprovarEnviamentEmailsNoAgrupatsBustia();

	public void comprovarEnviamentEmailsAgrupatsBustia();
	
}
