/**
 * 
 */
package es.caib.distribucio.apiexterna.controller;

import java.util.List;

import es.caib.distribucio.core.api.dto.dadesobertes.BustiaDadesObertesDto;

/**
 * Test per al client REST de l'API REST de consulta de dades obertes de Distribicio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesObertesRestTest {

	private static final String URL = "http://localhost:8080/distribucioapi";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	
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
			String uo = "A04019281";
			String uoSuperior = null; // A04019281 - Govern
			List<BustiaDadesObertesDto> busties = client.busties(bustiaId, uo, uoSuperior);
			System.out.println("Consulta correcta amb " + busties.size() + " resultats.");
		} catch (Exception e) {
			System.err.println("Error consultant les bústies: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
}