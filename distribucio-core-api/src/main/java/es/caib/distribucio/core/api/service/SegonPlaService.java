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
	
	/**
	 * Aplica les regles pendents a les anotacions de registre.
	 */
	public void aplicarReglesPendentsBackoffice();

	/**
	 * Tanca els contenidors de l'arxiu (expedients) de les anotacions de registre
	 * ja processades.
	 */
	public void tancarContenidorsArxiuPendents();
	

	public void enviarEmailsPendentsNoAgrupats();

	public void enviarEmailsPendentsAgrupats();

	/**
	 * Guarda els annexos pendents de les anotacions de registre que han
	 * arribat a DISTRIBUCIO.
	 */
	public void guardarAnotacionsPendentsEnArxiu();

	/**
	 * Envia anotacions pendents al backoffice de distribucio
	 */
	void enviarIdsAnotacionsPendentsBackoffice();


	void addNewEntryToHistogram();

	
}
