/**
 * 
 */
package es.caib.distribucio.api.externa.controller;

import java.util.List;

import es.caib.distribucio.logic.intf.dto.dadesobertes.BustiaDadesObertesDto;

/**
 * Test per al client REST de l'API REST de consulta de dades obertes de Distribicio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesObertesRestTest {

	private static final String URL = "http://localhost:8080/distribucioapi";
	// Usuari amb només el rol de DIS_REPORT
	private static final String USERNAME = "disreport";
	private static final String PASSWORD = "disreport";

	
	/** Mètode de prova d'execució per les diferents consultes */
	public static void main(String[] args) {
		
		// Creació del client
		DadesObertesRestClient client = new DadesObertesRestClient(
				URL,
				USERNAME,
				PASSWORD,
				false);
		
		// Consulta de bústies
		try {
			Long bustiaId = null;
			String uo = null;
			String uoSuperior = null; // A04019281 - Govern
			List<BustiaDadesObertesDto> busties = client.busties(bustiaId, uo, uoSuperior);
			System.out.println("Consulta correcta amb " + busties.size() + " resultats.");
		} catch (Exception ex) {
			System.err.println("Error consultant les bústies: " + ex.getMessage());
			ex.printStackTrace();
		}
		
	}
}