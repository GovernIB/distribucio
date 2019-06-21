/**
 * 
 */
package es.caib.distribucio.ws.client;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.distribucio.ws.v1.bustia.BustiaV1;
import es.caib.distribucio.ws.v1.bustia.Firma;
import es.caib.distribucio.ws.v1.bustia.RegistreAnnex;
import es.caib.distribucio.ws.v1.bustia.RegistreAnotacio;
import es.caib.distribucio.ws.v1.bustia.RegistreInteressat;
/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaV1Test {

	private static final String ENTITAT_DIST_CODI = "A04019281";
	private static final String UNITAT_ADM_CODI = "A04015411";
	private static final String APLICACIO_CODI = "CLIENT_TEST";
	private static final String APLICACIO_VERSIO = "2";
	private static final String ASSUMPTE_CODI = "BACK";
	private static final String ASSUMPTE_DESC = "Descripcio CodA";
	private static final String ASSUMPTE_TIPUS_CODI = "A1";
	private static final String ASSUMPTE_TIPUS_DESC = "Assumpte de proves";
	private static final String USUARI_CODI = "u104848";
	private static final String USUARI_NOM = "VHZ";
	private static final String EXTRACTE = "Anotació provinent de JUnit (" + System.currentTimeMillis() + ")";
	private static final String ENTITAT_CODI = "A04019281";
	private static final String ENTITAT_DESC = "Descripció entitat";
	private static final String OFICINA_CODI = "10";
	private static final String OFICINA_DESC = "Oficina de proves";
	private static final String LLIBRE_CODI = "11";
	private static final String LLIBRE_DESC = "Llibre de proves";
	private static final String IDIOMA_CODI = "1";
	private static final String IDIOMA_DESC = "Català";
	private static final String IDENTIFICADOR = "15/10/2015";
	private static final String EXPEDIENT_NUM = "BACK/76/2019";

	private static final boolean TEST_ANNEX_FIRMAT = true;
	private static final boolean TEST_ANNEX_PDF = true;
	
	/** Accepta els certificats i afegeix el protocol TLSv1.2.
	 * @throws Exception */
	@Before
	public void init() throws Exception {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		    System.err.println("Error ingorant certificats: " + e.getMessage());
		    e.printStackTrace();
		}		
		// Afegeix el protocol TLSv1.2
    	SSLContext context = SSLContext.getInstance("TLSv1.2");
    	context.init(null,null,null);
    	SSLContext.setDefault(context); 
	}

	@Test
	public void test() throws DatatypeConfigurationException, IOException {
		RegistreAnotacio anotacio; 
		int nAnotacions = 1;
		for (int i=1; i<=nAnotacions; i++) {
			System.out.println("Enviant l'anotació " + i);
			anotacio = new RegistreAnotacio(); 
			anotacio.setAplicacioCodi(APLICACIO_CODI);
			anotacio.setAplicacioVersio(APLICACIO_VERSIO);
			anotacio.setAssumpteCodi(ASSUMPTE_CODI);
	        anotacio.setAssumpteDescripcio(ASSUMPTE_DESC);
	        anotacio.setAssumpteTipusCodi(ASSUMPTE_TIPUS_CODI);
	        anotacio.setAssumpteDescripcio(ASSUMPTE_TIPUS_DESC);
	        anotacio.setUsuariCodi(USUARI_CODI);
	        anotacio.setUsuariNom(USUARI_NOM);
	        anotacio.setData(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
	        anotacio.setExtracte(EXTRACTE + " " + i);
	        anotacio.setEntitatCodi(ENTITAT_CODI);
	        anotacio.setEntitatDescripcio(ENTITAT_DESC);
	        anotacio.setOficinaCodi(OFICINA_CODI);
	        anotacio.setOficinaDescripcio(OFICINA_DESC);
	        anotacio.setLlibreCodi(LLIBRE_CODI);
	        anotacio.setLlibreDescripcio(LLIBRE_DESC);
	        anotacio.setNumero("L" + LLIBRE_CODI + "E" + System.currentTimeMillis() + "/" + Calendar.getInstance().get(Calendar.YEAR));
	        anotacio.setIdiomaCodi(IDIOMA_CODI);
	        anotacio.setIdiomaDescripcio(IDIOMA_DESC);
	        anotacio.setIdentificador(IDENTIFICADOR);
	        anotacio.setExpedientNumero(EXPEDIENT_NUM);
	        List<Firma> firmes = null;
	        RegistreAnnex annex;
	        int nAnnexos = 1;
	        for (int j=1; j<=nAnnexos; j++) {
		        if (TEST_ANNEX_FIRMAT) {
		        	firmes = new ArrayList<Firma>();
		            Firma firma = new Firma();
		            firma.setFitxerNom("annex_firmat.pdf");
		            firma.setTipusMime("application/pdf");
		            firma.setContingut(
		            		IOUtils.toByteArray(getContingutAnnexFirmat()));
		            firma.setTipus("TF06");
		            firma.setPerfil("EPES");
		            firmes.add(firma);
		            annex = crearAnnex(
			        		"Annex" + j,
			        		//"annex.pdf",
			        		"C23 Renovació autorització 121193 & 121194_s.pdf",
			        		"application/pdf",
			        		null,
			        		null,
			        		"0",
			        		"EE01",
			        		"TD01",
			        		"01",
			        		firmes);
		        } else {
			        if (TEST_ANNEX_PDF) {
				        annex = crearAnnex(
				        		"Annex" + j,
				        		"annex.pdf",
				        		"application/pdf",
				        		null,
				        		getContingutAnnexSenseFirmaPdf(),
				        		"0",
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);
			        } else {
			        	annex = crearAnnex(
				        		"Annex" + j,
				        		"annex.docx",
				        		"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				        		null,
				        		getContingutAnnexSenseFirmaDocx(),
				        		"0",
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);
			        }
		        }
		        anotacio.getAnnexos().add(annex);
		    }

	        	        
	        RegistreAnnex justificant = crearAnnex(
	        		"justificant",
	        		"justificant.pdf",
	        		"application/pdf",
	        		"9f33c5c7-7d0f-4d70-9082-c541a42cc041",
	        		null, //getContingutJustificant(),
	        		"1",
	        		"EE01",
	        		"TD02",
	        		"02",
	        		null);
	        anotacio.setJustificant(justificant);
	        
	        
	        
	        afegirInteressats(anotacio);
	        
	        try {
	    		getBustiaServicePort().enviarAnotacioRegistreEntrada(
	    				ENTITAT_DIST_CODI,
	    				UNITAT_ADM_CODI,
	    				anotacio);        	
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	fail();
	        }			
		}
	}

	private RegistreAnnex crearAnnex(
			String titol,
			String arxiuNom,
			String arxiuTipusMime,
			String arxiuUuid,
			InputStream arxiuContingut,
			String eniOrigen,
			String eniEstatElaboracio,
			String eniTipusDocumental,
			String sicresTipusDocument,
			List<Firma> firmes) throws IOException, DatatypeConfigurationException {
		RegistreAnnex annex = new RegistreAnnex();
		annex.setTitol(titol);
		annex.setFitxerNom(arxiuNom);
        annex.setFitxerTipusMime(arxiuTipusMime);
        if (arxiuContingut != null) {
        	annex.setFitxerContingut(IOUtils.toByteArray(arxiuContingut));
        	annex.setFitxerTamany(
        			annex.getFitxerContingut().length);
        }
        annex.setFitxerArxiuUuid(arxiuUuid);
        annex.setEniDataCaptura(
        		DatatypeFactory.newInstance().newXMLGregorianCalendar(
        				new GregorianCalendar()));
        annex.setEniOrigen(eniOrigen);
        annex.setEniEstatElaboracio(eniEstatElaboracio);
        annex.setEniTipusDocumental(eniTipusDocumental);
        annex.setSicresTipusDocument(sicresTipusDocument);
        if (firmes != null) {
        	annex.getFirmes().addAll(firmes);
        }
        return annex;
	}

	@SuppressWarnings("unused")
	private void afegirInteressats(RegistreAnotacio anotacio) {
		RegistreInteressat representant = new RegistreInteressat();
		representant.setAdresa("Carrer companys");
		representant.setCanalPreferent("02");
		representant.setCodiPostal("07200");
		representant.setDocumentNum("77788899P");
		representant.setDocumentTipus("N");
		representant.setEmail("representant@limit.es");
		representant.setEmailHabilitat("true");
		representant.setLlinatge1("LlinatgeRep1");
		representant.setLlinatge2("LlinatgeRep2");
		representant.setMunicipi("Felanitx");
		representant.setNom("NomRep");
		representant.setObservacions(null);
		representant.setPais("ES");
		representant.setProvincia("Illes Balears");
		representant.setRaoSocial(null);
		representant.setRepresentant(null);
		representant.setTelefon("666555444");
		representant.setTipus("2");
		RegistreInteressat interessat = new RegistreInteressat();
		interessat.setAdresa("Carrer del moix 2");
		interessat.setCanalPreferent("02");
		interessat.setCodiPostal("07500");
		interessat.setDocumentNum("12312312P");
		interessat.setDocumentTipus("N");
		interessat.setEmail("interessat@limit.es");
		interessat.setEmailHabilitat("true");
		interessat.setLlinatge1("LlinatgeInt1");
		interessat.setLlinatge2("LlinatgeInt2");
		interessat.setMunicipi("Manacor");
		interessat.setNom("NomInt1");
		interessat.setObservacions(null);
		interessat.setPais("ES");
		interessat.setProvincia("Illes Balears");
		interessat.setRaoSocial(null);
		interessat.setRepresentant(representant);
		interessat.setTelefon("999888777");
		interessat.setTipus("2");
		anotacio.getInteressats().add(interessat);
	}

	private BustiaV1 getBustiaServicePort() throws IOException {
		Properties testProperties = getTestProperties();
		return BustiaV1WsClientFactory.getWsClient(
				testProperties.getProperty("bustia.test.service.url"),
				testProperties.getProperty("bustia.test.service.username"),
				testProperties.getProperty("bustia.test.service.password"));
	}

	@SuppressWarnings("unused")
	private InputStream getContingutJustificant() {
		InputStream is = getClass().getResourceAsStream(
        		"/justificant.pdf");
		return is;
	}
	private InputStream getContingutAnnexSenseFirmaPdf() {
		InputStream is = getClass().getResourceAsStream(
        		"/annex_sense_firma.pdf");
		return is;
	}
	private InputStream getContingutAnnexSenseFirmaDocx() {
		InputStream is = getClass().getResourceAsStream(
        		"/annex_sense_firma.docx");
		return is;
	}
	private InputStream getContingutAnnexFirmat() {
		InputStream is = getClass().getResourceAsStream(
        		"/annex_firmat.pdf");
		return is;
	}

	private Properties getTestProperties() throws IOException {
		Properties props = new Properties();
		InputStream is = getClass().getResourceAsStream(
        		"/bustia_test.properties");
		props.load(is);
		return props;
	}

}
