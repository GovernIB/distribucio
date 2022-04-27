/**
 * 
 */
package es.caib.distribucio.war.rest;

/**
 * Test per al client REST de l'API REST de creació de regles automàtiques de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglesRestTest {

	private static final String URL = "http://10.35.3.232:8080/distribucio";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	
	/** Mètode de prova de creació d'una regla. */
	public static void main(String[] args) {
		
		// Creació del client
		ReglesRestClient client = new ReglesRestClient(
				URL,
				USERNAME,
				PASSWORD,
				false);
		
		// Creació de la regla
		try {
			String entitat = "A04019281";
			String sia = "BACK_HELIUM_X";
			String backoffice = "HELIUM";
			boolean ret = client.add(entitat, sia, backoffice);
			System.out.println("Creació finalitzada correctament amb resultat " + ret);
		} catch (Exception e) {
			System.err.println("Error creant la regla: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
}