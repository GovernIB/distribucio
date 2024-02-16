/**
 * 
 */
package es.caib.distribucio.ws.client;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import es.caib.distribucio.ws.v1.bustia.RegistreAnnex.MetaDades;
import es.caib.distribucio.ws.v1.bustia.RegistreAnnex.MetaDades.Entry;
import es.caib.distribucio.ws.v1.bustia.RegistreAnotacio;
import es.caib.distribucio.ws.v1.bustia.RegistreInteressat;
/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaV1Test {

	private static final String REGISTRE_TIPUS = "E";//"S"
	private static final String ENTITAT_DIST_CODI = "A04003003"; //A04003003 DEV | A04019281 DES
	private static final String UNITAT_ADM_CODI = "A04003003"; //A04026923 DEV | A04032369 DES
	private static final String APLICACIO_CODI = "CLIENT_TEST";
	private static final String APLICACIO_VERSIO = "2";
	private static final String ASSUMPTE_CODI = null;
	private static final String ASSUMPTE_DESC = "Descripcio Codi";
	private static final String ASSUMPTE_TIPUS_CODI = ""; //"A1";
	private static final String ASSUMPTE_TIPUS_DESC = "Assumpte de proves"; //"Assumpte de proves";
	private static final String PROCEDIMENT_CODI = "BACK_HELIUM_DP"; //"BACK_DIST_232" //"1234" //PRE	//"208133" //DEV // "208002" prova regles //DES "BACK_HELIUM" backoffice 
	private static final boolean PRESENCIAL = false;
	private static final String USUARI_CODI = "u104848";
	private static final String USUARI_NOM = "VHZ";
	private static final String EXTRACTE = "Prova regles backoffice " + new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(new Date()) ;
	private static final String ENTITAT_CODI = ENTITAT_DIST_CODI;
	private static final String ENTITAT_DESC = "Descripció entitat";
	private static final String OFICINA_CODI = "10";
	private static final String OFICINA_DESC = "Oficina de proves";
	private static final String LLIBRE_CODI = "GOIB";
	private static final String LLIBRE_DESC = "Llibre de proves";
	private static final String IDIOMA_CODI = "1";
	private static final String IDIOMA_DESC = "Català";
	private static final String IDENTIFICADOR = "15/10/2015";
	private static final String EXPEDIENT_NUM =  System.currentTimeMillis() + "/2023";
	

	private static final int N_ANOTACIONS = 1;
	private static final int N_ANNEXOS = 1;
	private static final boolean TEST_ANNEX_FIRMAT = false;
	private static final boolean TEST_ANNEX_FIRMAT_XADES_INTERNALLY_DETACHED = false; //TF02 - XAdES internally detached signature
	private static final boolean TEST_ANNEX_FIRMAT_XADES_ENVELOPED = false; //TF03 - XAdES enveloped signature  
	private static final boolean TEST_ANNEX_FIRMA_CADES_DETACHED = false; //TF04 - CAdES detached/explicit signature
	private static final boolean TEST_ANNEX_FIRMA_CADES_ATTACHED = false; //TF05 - CAdES attached
	private static final boolean TEST_ANNEX_PDF = true;
	private static final boolean TEST_ANNEX_DOC_TECNIC = true; // Indica si adjuntar els documents tècnics de sistra2 com annexos
	
	
	private static final  Map<String, String> metaDadesMap = new HashMap<String, String>() {{
	    put("eni:idioma", "ca");
	    put("eni:descripcion", "Descripció de l'annex");
	    put("eni:resolucion", "12");
	    put("eni:profundidad_color", "6400");
//	    put("eni:id_tramite", PROCEDIMENT_CODI);
//		put("cm:title", "Títol de l'annex");
//	    put("eni:app_tramite_doc", "app tramite doc valor");
//	    put("eni:organo", "Òrgan valor");
//	    put("eni:origen", "Origen valor");
//		put("eni:estado_elaboracion", "Estat elaboració valor");
//		put("eni:tipo_doc_ENI", "Tipus doc ENI valor");
//		put("eni:cod_clasificacion", "Codi classificació valor");
//		put("eni:csv", "Csv valor");
//		put("eni:def_csv", "Def csv valor");
//		put("eni:id", "id valor");
//		put("eni:id_origen", "id origen");
//		put("eni:fecha_inicio", "20/11/2021");
//		put("eni:nombre_formato", "Nom formato valor");
//		put("eni:extension_formato", "Extensió format valor");
//		put("eni:tamano_logico", "Mida lógica valor");
//		put("eni:termino_punto_acceso", "Terme punt accés valor");
//		put("eni:id_punto_acceso", "Id punt accés valor");
//		put("eni:esquema_punto_acceso", "Esquema punt accés valor");
//		put("eni:soporte", "Soporte valor");
//		put("eni:loc_archivo_central", "Loc arxiu central valor");
//		put("eni:loc_archivo_general", "Loc arxiu general valor");
//		put("eni:unidades", "Unitats valor");
//		put("eni:subtipo_doc", "Subtipus doc valor");
//		put("eni:tipo_asiento_registral", "Tipus asiento registral valor");
//		put("eni:codigo_oficina_registro", "Codi oficina registre valor");
//		put("eni:fecha_asiento_registral", "20/11/2021");
//		put("eni:numero_asiento_registral", "Nombre asiento registral");
//		put("eni:tipoFirma", "TD04");
//		put("eni:perfil_firma", "PADES");
//		put("eni:fecha_sellado", "20/11/2021");
	}};

	
	/** Accepta els certificats i afegeix el protocol TLSv1.2.
	 * @throws Exception */
	@Before
	public void init() throws Exception {
		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
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
	public void test() throws Exception {
		RegistreAnotacio anotacio; 
		int nAnotacions = N_ANOTACIONS;
		for (int i=1; i<=nAnotacions; i++) {
			System.out.println("Enviant l'anotació " + i + " de " + N_ANOTACIONS);
			anotacio = new RegistreAnotacio(); 
			anotacio.setTipusES(REGISTRE_TIPUS);
			anotacio.setAplicacioCodi(APLICACIO_CODI);
			anotacio.setAplicacioVersio(APLICACIO_VERSIO);
			anotacio.setAssumpteCodi(ASSUMPTE_CODI);
			anotacio.setProcedimentCodi(PROCEDIMENT_CODI);
	        anotacio.setAssumpteDescripcio(ASSUMPTE_DESC);
	        anotacio.setAssumpteTipusCodi(ASSUMPTE_TIPUS_CODI);
	        anotacio.setAssumpteDescripcio(ASSUMPTE_TIPUS_DESC);
	        anotacio.setUsuariCodi(USUARI_CODI);
	        anotacio.setUsuariNom(USUARI_NOM);
	        //anotacio.setData(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2021, 11, 28, 16, 31, 00))); //per sobreescriure
	        GregorianCalendar calendar = new GregorianCalendar();
	        //calendar.setTime(new SimpleDateFormat("dd-MM-yyyy").parse("29-11-2021"));
	        calendar.setTime(new Date());
	        anotacio.setData(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
	        anotacio.setExtracte(EXTRACTE + " " + i + " de " + N_ANOTACIONS);
	        anotacio.setEntitatCodi(ENTITAT_CODI);
	        anotacio.setEntitatDescripcio(ENTITAT_DESC);
	        anotacio.setOficinaCodi(OFICINA_CODI);
	        anotacio.setOficinaDescripcio(OFICINA_DESC);
	        anotacio.setLlibreCodi(LLIBRE_CODI);
	        anotacio.setLlibreDescripcio(LLIBRE_DESC);
	        anotacio.setNumero(LLIBRE_CODI + "E" + System.currentTimeMillis() + "/" + Calendar.getInstance().get(Calendar.YEAR));
	        anotacio.setIdiomaCodi(IDIOMA_CODI);
	        anotacio.setIdiomaDescripcio(IDIOMA_DESC);
	        anotacio.setIdentificador(IDENTIFICADOR);
	        anotacio.setExpedientNumero(EXPEDIENT_NUM);
	        anotacio.setPresencial(PRESENCIAL);
	        anotacio.setObservacions("Anotacio Observacions....");
	        
//	        anotacio.setDocumentacioFisicaCodi("1");
//	        anotacio.setDocumentacioFisicaDescripcio("Documentació adjunta en suport PAPER (o altres suports)");
//	        anotacio.setDocumentacioFisicaCodi("2");
//	        anotacio.setDocumentacioFisicaDescripcio("Documentació adjunta digitalitzada i complementàriament en paper");
	        anotacio.setDocumentacioFisicaCodi("3");
	        anotacio.setDocumentacioFisicaDescripcio("Documentació adjunta digitalitzada");
	        
	        
	        int nCaracters = 5000;
	        StringBuilder textGran = new StringBuilder("Text gran " + i + ": ");
	        while (textGran.length() < nCaracters)
	        	textGran.append("0123456789 ");
	        anotacio.setExposa(textGran.toString());
	        anotacio.setSolicita("Text sol·licita " + i);
	        List<Firma> firmes = null;
	        RegistreAnnex annex;
	        int nAnnexos = N_ANNEXOS;
	        for (int j=1; j<=nAnnexos; j++) {
		        if (TEST_ANNEX_FIRMAT ) {
		        	if (TEST_ANNEX_FIRMAT_XADES_INTERNALLY_DETACHED) {
		        		
		        		firmes = new ArrayList<Firma>();
			            Firma firma = new Firma();
			            firma.setTipusMime("application/xml");
			            firma.setContingut(
			            		IOUtils.toByteArray(getContingutWithFirmaXadesDettached()));
			            firma.setTipus("TF02");
			            firma.setPerfil("BES");
			            firmes.add(firma);
			            annex = crearAnnex(
				        		"Annex signat XADES dettached" + j,
				        		"formulario.xml_xades_detached.xsig",
				        		"application/xml",
				        		null,
				        		null,
				        		"0",
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);
		        		
		        	} else if (TEST_ANNEX_FIRMAT_XADES_ENVELOPED) {
		        		
			        	firmes = new ArrayList<Firma>();
			            Firma firma = new Firma();
			            firma.setTipusMime("application/xsig");
			            firma.setContingut(null);
			            firma.setTipus("TF03");
			            firma.setPerfil("BES");
			            firmes.add(firma);
			            annex = crearAnnex(
				        		"Annex signat XADES enveloped" + j,
				        		"formulari.xml_xades_enveloped.xsig",
				        		"application/xml",
				        		null,
				        		getContingutWithFirmaXadesEnveloped(),
				        		"0",
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);
		        		
		        	} else if (TEST_ANNEX_FIRMA_CADES_DETACHED) {
		        		
			        	firmes = new ArrayList<Firma>();
			            Firma firma = new Firma();
			            firma.setFitxerNom("firma_cades_detached.csig");
			            firma.setTipusMime("application/csig");
			            firma.setContingut(
			            		IOUtils.toByteArray(getContingutFirmaCadesDetached()));
			            firma.setTipus("TF04");
			            firma.setPerfil("BES");
			            firmes.add(firma);
						
				        annex = crearAnnex(
				        		"Annex signat CAdES TF04 " + j,
				        		"annex.pdf",
				        		"application/pdf",
				        		null,
				        		getContingutAnnexSenseFirmaPdf(),
				        		"0",
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);

		        	} else if (TEST_ANNEX_FIRMA_CADES_ATTACHED) {
		        		
			        	firmes = new ArrayList<Firma>();
			            Firma firma = new Firma();
			            firma.setFitxerNom("firma_cades_atttached.csig");
			            firma.setTipusMime("application/csig");
			            firma.setContingut(
			            		IOUtils.toByteArray(getContingutFirmaCadesAttached()));
			            firma.setTipus("TF05");
			            firma.setPerfil("BES");
			            firmes.add(firma);
						
				        annex = crearAnnex(
				        		"Annex" + j,
				        		"annex signat CADES TF05.pdf",
				        		"application/pdf",
				        		null,
				        		null,
				        		"0",
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);
				    } else {
						
			        	firmes = new ArrayList<Firma>();
			            Firma firma = new Firma();
			            firma.setFitxerNom("annex_firmat.pdf");
			            firma.setTipusMime("application/pdf");
			            firma.setContingut(
			            		IOUtils.toByteArray(getContingutAnnexFirmat()));
//			            firma.setContingut(null);
			            firma.setTipus("TF06");
			            firma.setPerfil("EPES");
			            firmes.add(firma);
			            annex = crearAnnex(
				        		"Annex signat PAdES " + j,
				        		"annex_firmat.pdf",
				        		"application/pdf",
				        		null,
				        		null,
				        		//getContingutAnnexFirmat(),
				        		"0",
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);
					}
		        } else {
			        if (TEST_ANNEX_PDF) {
				        annex = crearAnnex(
				        		"Annex " + j,
				        		"ànnex.pdf",				        		
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
				        		"Annex DOC " + j,
				        		"annex.doc",
				        		"application/msword",
				        		null,
				        		getContingutAnnexSenseFirmaDoc(),
				        		"0",	
				        		"EE01",
				        		"TD01",
				        		"01",
				        		firmes);
//			        	annex = crearAnnex(
//				        		"Annex XML " + j,
//				        		"annex.xml",
//				        		"application/xml",
//				        		null,
//				        		getContingutAltre("formulario.xml"),
//				        		"0",
//				        		"EE01",
//				        		"TD01",
//				        		"01",
//				        		firmes);
			        }
		        }
		        anotacio.getAnnexos().add(annex);
		    }
	        if (TEST_ANNEX_DOC_TECNIC) {
	        	// FORMULARIO
		        anotacio.getAnnexos().add(		        
		        		crearAnnex(
			        		"FORMULARIO",
			        		"formulario.xml",
			        		"application/xml",
			        		null,
			        		getContingutAltre("formulario.xml"),
			        		"0",
			        		"EE01",
			        		"TD99",
			        		"03",
			        		null));
	        	// PAGO
		        anotacio.getAnnexos().add(		        
		        		crearAnnex(
			        		"PAGO",
			        		"pago.xml",
			        		"application/xml",
			        		null,
			        		getContingutAltre("pago.xml"),
			        		"0",
			        		"EE01",
			        		"TD99",
			        		"03",
			        		null));
	        }
	        	        
	        RegistreAnnex justificant = crearAnnex(
	        		"justificant",
	        		"justificant.pdf",
	        		"application/pdf",
	        		"f1dc28d2-5641-4d26-b2d8-7f3417ae3831",
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
	        	//fail();
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
        annex.setObservacions("Annex observacions...");
        
		MetaDades metaDades = new MetaDades();
		for (String key : metaDadesMap.keySet()) {

			Entry entry = new Entry();
			entry.setKey(key);
			entry.setValue(metaDadesMap.get(key));
			metaDades.getEntry().add(entry);
		}
        annex.setMetaDades(metaDades);
        
        
        return annex;
	}

	private void afegirInteressats(RegistreAnotacio anotacio) {
		
		// PERSONA FISICA
		RegistreInteressat representantPersonaFisica = new RegistreInteressat();
		representantPersonaFisica.setAdresa("Carrer companys");
		representantPersonaFisica.setCanalPreferent("02");
		representantPersonaFisica.setCodiPostal("07200");
		representantPersonaFisica.setDocumentNum("77788899N");
		representantPersonaFisica.setDocumentTipus("N");
		representantPersonaFisica.setEmail("representant@limit.es");
		representantPersonaFisica.setEmailHabilitat("true");
		representantPersonaFisica.setLlinatge1("LlinatgeRep1");
		representantPersonaFisica.setLlinatge2("LlinatgeRep2");
		representantPersonaFisica.setMunicipi("Felanitx");
		representantPersonaFisica.setMunicipiCodi("162");
		representantPersonaFisica.setNom("NomRep");
		representantPersonaFisica.setObservacions(null);
		representantPersonaFisica.setPais("Espanya");
		representantPersonaFisica.setPaisCodi("724");
		representantPersonaFisica.setProvincia("Illes Balears");
		representantPersonaFisica.setProvinciaCodi("01");
		representantPersonaFisica.setRaoSocial(null);
		representantPersonaFisica.setRepresentant(null);
		representantPersonaFisica.setTelefon("666555444");
		representantPersonaFisica.setTipus("2");
		representantPersonaFisica.setCodiDire("r4444444");
		
		RegistreInteressat interessatPersonaFisica = new RegistreInteressat();
		interessatPersonaFisica.setAdresa("Carrer del moix 2");
		interessatPersonaFisica.setCanalPreferent("02");
		interessatPersonaFisica.setCodiPostal("07500");
		interessatPersonaFisica.setDocumentNum("12312312N");
		interessatPersonaFisica.setDocumentTipus("N");
		interessatPersonaFisica.setEmail("interessat@limit.es");
		interessatPersonaFisica.setEmailHabilitat("true");
		interessatPersonaFisica.setLlinatge1("LlinatgeInt1");
		interessatPersonaFisica.setLlinatge2("LlinatgeInt2");
		interessatPersonaFisica.setMunicipi("Manacor");
		interessatPersonaFisica.setMunicipiCodi("162");
		interessatPersonaFisica.setNom("NomInt1");
		interessatPersonaFisica.setObservacions(null);
		interessatPersonaFisica.setPais("Espanya");
		interessatPersonaFisica.setPaisCodi("724");
		interessatPersonaFisica.setProvincia("Illes Balears");
		interessatPersonaFisica.setProvinciaCodi("01");
		interessatPersonaFisica.setRaoSocial(null);
		interessatPersonaFisica.setTelefon("999888777");
		interessatPersonaFisica.setTipus("2");
		interessatPersonaFisica.setCodiDire("i3333333");
		interessatPersonaFisica.setRepresentant(representantPersonaFisica);
		anotacio.getInteressats().add(interessatPersonaFisica);
		
		// PERSONA JURIDICA
		RegistreInteressat interessatPersonaJuridica = new RegistreInteressat();
		interessatPersonaJuridica.setTipus("3");
		interessatPersonaJuridica.setDocumentNum("11111111T");
		interessatPersonaJuridica.setDocumentTipus("O");
		interessatPersonaJuridica.setRaoSocial("raoSocialJuridica");
		anotacio.getInteressats().add(interessatPersonaJuridica);
		
		//ADMINISTRACIO
		RegistreInteressat interessatAdministracio = new RegistreInteressat();
		interessatAdministracio.setTipus("1");
		interessatAdministracio.setDocumentTipus("O");
		interessatAdministracio.setDocumentNum(ENTITAT_DIST_CODI);
		interessatAdministracio.setRaoSocial("raoSocialAdministracio");
		anotacio.getInteressats().add(interessatAdministracio);
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
		InputStream is = getClass().getResourceAsStream("/justificant.pdf");
		return is;
	}
	private InputStream getContingutAnnexSenseFirmaPdf() {
		InputStream is = getClass().getResourceAsStream("/annex_sense_firma.pdf");
		return is;
	}
	private InputStream getContingutFirmaCadesDetached() {
		InputStream is = getClass().getResourceAsStream("/firma_cades_detached.csig");
		return is;
	}
	private InputStream getContingutFirmaCadesAttached() {
		InputStream is = getClass().getResourceAsStream("/firma_cades_attached.csig");
		return is;
	}
	/** TF02 - XAdES internally detached signature */
	private InputStream getContingutWithFirmaXadesDettached() {
		InputStream is = getClass().getResourceAsStream("/formulario.xml_xades_detached.xsig");
		return is;
	}
	private InputStream getContingutWithFirmaXadesEnveloped() {
		InputStream is = getClass().getResourceAsStream("/formulari.xml_xades_enveloped.xsig");
		return is;
	}
	@SuppressWarnings("unused")
	private InputStream getContingutAnnexSenseFirmaDocx() {
		InputStream is = getClass().getResourceAsStream("/annex_sense_firma.docx");
		return is;
	}
	private InputStream getContingutAnnexSenseFirmaDoc() {
		InputStream is = getClass().getResourceAsStream("/annex.doc");
		return is;
	}
	private InputStream getContingutAnnexFirmat() {
		InputStream is = getClass().getResourceAsStream("/annex_firmat.pdf");
		return is;
	}
	private InputStream getContingutAltre(String arxiuNom) {
		InputStream is = getClass().getResourceAsStream("/" + arxiuNom);
		return is;
	}

	private Properties getTestProperties() throws IOException {
		Properties props = new Properties();
		InputStream is = getClass().getResourceAsStream("/bustia_test.properties");
		props.load(is);
		return props;
	}
}
