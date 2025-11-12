/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

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

	void canviEstatComunicatAPendent();


	void addNewEntryToHistogram();
	
	/** 
	 * Consulta i guarda les dades històriques del dia
	 */
	public void calcularDadesHistoriques();

	/** 
	 * Esborra les dades antigues del monitor d'integracions
	 */
	public void esborrarDadesAntigesMonitorIntegracio();
	
	/**
	 * Reintenta el processament al backoffice 
	 **/
	public void reintentarProcessamentBackoffice();
	
	/**
	 * Actualitza els procediments 
	 * @throws Exception 
	 **/
	public void actualitzarProcediments() throws Exception;

	/**
	 * Actualitza els serveis 
	 * @throws Exception 
	 **/
	public void actualitzarServeis() throws Exception;
	
	/**
	 * Executa les execucions massives en cua
	 * @throws Exception 
	 **/
	public void executeNextMassiveScheduledTask() throws Exception;
	
	/** Reinicia una tasca concreta o "totes". */
	public void restartSchedulledTasks(String taskCodi);
}
