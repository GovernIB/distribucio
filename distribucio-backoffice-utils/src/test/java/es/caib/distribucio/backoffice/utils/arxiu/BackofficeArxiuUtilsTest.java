package es.caib.distribucio.backoffice.utils.arxiu;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.api.IArxiuPlugin;
import es.caib.pluginsib.arxiu.caib.ArxiuPluginCaib;

/**
 * Test per provar la llibreria de distribucio-backoffice-utils. Amb el client REST consulta una anotació
 * a patir del seu identificador i clau i invoca al mètode BackofficeArxiuUtils.crearExpedientAmbAnotacioRegistre(Expedient, AnotacioRegistreEntrada)
 * per moure tots els annexos. També injecta un listener per capturar dels events i mostrar el resultat. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeArxiuUtilsTest implements ArxiuPluginListener{

	// Dades per consultar l'anotacio
	public static String BASE_URL = "https://se.caib.es/distribucioapi/interna";
	public static String USERNAME = "$helium_distribucio_se";
	public static String PASSWORD = "****";
	// Correctament moguda i resultat OK
	public static String ANOTACIO_IDENTIFICADOR = "GOIBE1780474672650/2026";
	public static String ANOTACIO_CLAU = "0m+RNJwGzOOGbF2JZc5bUUlRWwDuBLotK7O2eWLmBzo=";
	// Error en moure perquè ja està mogut en una altra carpeta
//	public static String ANOTACIO_IDENTIFICADOR = "GOIBE1780473012792/2026";
//	public static String ANOTACIO_CLAU = "1UFsT9SVljBqaiOqrT1R3U1NFDVrhluT2FT/lM0FLHg=";
	
	// Dades per configurar el plugin d'Arxiu.
	public static String ARXIU_URL = "https://esbse.caib.es:4430/esb";
	public static String ARXIU_APP_CODI = "HELIUM";
	public static String ARXIU_USUARI= "helium";
	public static String ARXIU_PASSWORD = "****";
	public static String ARXIU_IMPRIMIBLE_URl = "https://se.caib.es/concsvapi/interna/printable/uuid";
	public static String ARXIU_IMPRIMIBLE_USUARI = "$helium_concsv_se";
	public static String ARXIU_IMPRIMIBLE_PASSWORD = "****";
	
	// Dades per la prova de moure anotació.
	public static String EXPEDIENT_DESTI_UUID = "345f39d4-de3e-4a8e-a590-99e94d4fba9c";
	
	

	/** Primer consulta una anotació i posteriorment invoca al mètode per moure. Cal teni en compte que
	 * les anotacios només es poden moure una vegada per restricció amb l'Arxiu. */
	@Test
	public void consultaIMouAnotacio() throws Exception {
		
		// Configura el client API REST
		BackofficeIntegracioRestClient restClient = 
				BackofficeIntegracioRestClientFactory.getRestClient(BASE_URL, USERNAME, PASSWORD);
		
		// Consulta l'anotació
		AnotacioRegistreId id = new AnotacioRegistreId();
		id.setIdentificador(ANOTACIO_IDENTIFICADOR);
		id.setClauAcces(ANOTACIO_CLAU);
		AnotacioRegistreEntrada anotacio = restClient.consulta(id);

		// Configura el pluginArxiuCaib que s'usarà per consultar i moure annexos.
		Properties properties = new Properties();
		properties.put("pluginsib.arxiu.caib.base.url", ARXIU_URL);
		properties.put("pluginsib.arxiu.caib.aplicacio.codi", ARXIU_APP_CODI);
		properties.put("pluginsib.arxiu.caib.usuari", ARXIU_USUARI);
		properties.put("pluginsib.arxiu.caib.contrasenya", ARXIU_PASSWORD);
		properties.put("pluginsib.arxiu.caib.conversio.imprimible.url", ARXIU_IMPRIMIBLE_URl);
		properties.put("pluginsib.arxiu.caib.conversio.imprimible.usuari", ARXIU_IMPRIMIBLE_USUARI);
		properties.put("pluginsib.arxiu.caib.conversio.imprimible.contrasenya", ARXIU_IMPRIMIBLE_PASSWORD);
		IArxiuPlugin arxiuPlugin = new ArxiuPluginCaib("", properties);
		
		// Consulta l'expedient de destí per fer la crida de moure annexos. Ha d'existir però la crida fallarà perquè els annexos 
		// de l'anotació original ja estan moguts.
		Expedient expedient = arxiuPlugin.expedientDetalls(EXPEDIENT_DESTI_UUID, null);

		// Usa la llibreria per moure tots els annexos a una carpeta de l'expedient destí
		BackofficeArxiuUtils backofficeArxiuUtils = new BackofficeArxiuUtilsImpl(arxiuPlugin);
		// La carpeta tindrá com a nom el número de l'anotació
		backofficeArxiuUtils.setCarpeta(anotacio.getIdentificador());
		// Afegeix la instància de la classe com a escoltador d'events
		backofficeArxiuUtils.setArxiuPluginListener(this);

		// Invoca al mètode que mourà els annexos. Com que ja estan moguts el resultat serà erroni.
		ArxiuResultat arxiuResultat = backofficeArxiuUtils.crearExpedientAmbAnotacioRegistre(expedient, anotacio);
		
		// Imprimeix el resultat per pantalla
		this.logResultat(
				"Resultat de la crida crearExpedientAmbAnotacioRegistre per l'anotació " + anotacio.getIdentificador(),
				arxiuResultat);	
	}

	/**
	 * Mètode que la llibreria del backoffice de distribució crida si es fixa una instància que implementa {@link ArxiuPluginListener}.
	 */
	@Override
	public void event(
			String metode, 
			Map<String, String> parametres, 
			boolean correcte, 
			String error, 
			Exception e,
			long timeMs) {
		StringBuilder str = new StringBuilder()
				.append("S'ha invocat el mètode \"")
				.append(metode)
				.append("\" amb els paràmetres {");
		int i = 0;
		for (String key : parametres.keySet()) {
			str.append(key).append("=").append(parametres.get(key));
			i++;
			if (i<parametres.size())
				str.append(", ");
		}
		str.append("} amb resultat ").append(correcte ? "OK" : "KO");
		if (error != null)
			str.append(": ").append(error);
		if (e != null)
			str.append(" ").append(e.getClass()).append(" ").append(e.getMessage());
		str.append(" ").append(timeMs).append("ms");
		
		System.out.println(str.toString());
	}

	/** Mètode privat per mostrar per pantalla el resultat de la crida. */
	private void logResultat(String descripcio, ArxiuResultat arxiuResultat) {
		System.out.println(descripcio);
		// Resultat a nivell d'expedient
		System.out.println("- uuid: " + arxiuResultat.getIdentificadorExpedient());
		System.out.println("- accio: " + arxiuResultat.getAccio());
		System.out.println("- errorCodi: " + arxiuResultat.getErrorCodi());
		System.out.println("- errorMessage: " + arxiuResultat.getErrorMessage());
		System.out.println("- excepcio: " + (arxiuResultat.getException() != null? arxiuResultat.getException().getClass() + " " + arxiuResultat.getException().getMessage() :  " - "));
		// Resultat pels annexos
		List<ArxiuResultatAnnex> resultatAnnexos = arxiuResultat.getResultatAnnexos(); 
		System.out.println(" - Resultat dels " + resultatAnnexos.size() + " annexos:");
		for (ArxiuResultatAnnex resultatAnnex : resultatAnnexos) {
			System.out.println("\b- uuid: " + resultatAnnex.getIdentificadorAnnex());
			System.out.println("\\b- accio: " + resultatAnnex.getAccio());
			System.out.println("\\b- errorCodi: " + resultatAnnex.getErrorCodi());
			System.out.println("\\b- errorMessage: " + resultatAnnex.getErrorMessage());
			System.out.println("\\b- excepcio: " + (resultatAnnex.getException() != null? resultatAnnex.getException().getClass() + " " + resultatAnnex.getException().getMessage() :  " - "));
		}
	}

	
}
