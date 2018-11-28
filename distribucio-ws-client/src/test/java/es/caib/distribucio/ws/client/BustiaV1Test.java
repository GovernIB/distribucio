/**
 * 
 */
package es.caib.distribucio.ws.client;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Random;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import es.caib.distribucio.ws.v1.bustia.BustiaV1;
import es.caib.distribucio.ws.v1.bustia.RegistreAnnex;
import es.caib.distribucio.ws.v1.bustia.RegistreAnotacio;
import es.caib.distribucio.ws.v1.bustia.RegistreInteressat;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaV1Test {

	private static final String ENTITAT_DIST_CODI = "E00003601";
	private static final String UNITAT_ADM_CODI = "E03029603";
	private static final String APLICACIO_CODI = "CLIENT_TEST";
	private static final String APLICACIO_VERSIO = "2";
	private static final String ASSUMPTE_CODI = "A1";
	private static final String ASSUMPTE_DESC = "Descripcio CodA";
	private static final String ASSUMPTE_TIPUS_CODI = "A1";
	private static final String ASSUMPTE_TIPUS_DESC = "Assumpte de proves";
	private static final String USUARI_CODI = "u104848";
	private static final String USUARI_NOM = "VHZ";
	private static final String EXTRACTE = "Anotació amb annexos sense firma per provar autofirma servidor";
	private static final String ENTITAT_CODI = "codientitat";
	private static final String ENTITAT_DESC = "Descripció entitat";
	private static final String OFICINA_CODI = "10";
	private static final String OFICINA_DESC = "Oficina de proves";
	private static final String LLIBRE_CODI = "10";
	private static final String LLIBRE_DESC = "Oficina de proves";
	private static final String IDIOMA_CODI = "1";
	private static final String IDIOMA_DESC = "Català";
	private static final String IDENTIFICADOR = "15/10/2015";
	private static final String EXPEDIENT_NUM = "12345678";

	@Test
	public void test() throws DatatypeConfigurationException, IOException {
		Random generator = new Random();
		int randomNumber = generator.nextInt(9999) + 1;
		RegistreAnotacio anotacio = new RegistreAnotacio(); 
		anotacio.setAplicacioCodi(APLICACIO_CODI);
		anotacio.setAplicacioVersio(APLICACIO_VERSIO);
		anotacio.setAssumpteCodi(ASSUMPTE_CODI);
        anotacio.setAssumpteDescripcio(ASSUMPTE_DESC);
        anotacio.setAssumpteTipusCodi(ASSUMPTE_TIPUS_CODI);
        anotacio.setAssumpteDescripcio(ASSUMPTE_TIPUS_DESC);
        anotacio.setUsuariCodi(USUARI_CODI);
        anotacio.setUsuariNom(USUARI_NOM);
        anotacio.setData(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        anotacio.setExtracte(EXTRACTE);
        anotacio.setEntitatCodi(ENTITAT_CODI);
        anotacio.setEntitatDescripcio(ENTITAT_DESC);
        anotacio.setOficinaCodi(OFICINA_CODI);
        anotacio.setOficinaDescripcio(OFICINA_DESC);
        anotacio.setLlibreCodi(LLIBRE_CODI);
        anotacio.setLlibreDescripcio(LLIBRE_DESC);
        anotacio.setNumero(String.valueOf(randomNumber));
        anotacio.setIdiomaCodi(IDIOMA_CODI);
        anotacio.setIdiomaDescripcio(IDIOMA_DESC);
        anotacio.setIdentificador(IDENTIFICADOR);
        anotacio.setExpedientNumero(EXPEDIENT_NUM);
        RegistreAnnex annex1 = crearAnnex(
        		"Annex1",
        		"annex.pdf",
        		"application/pdf",
        		null,
        		getContingutAnnexSenseFirma(),
        		"0",
        		"EE01",
        		"TD01",
        		"01");
        anotacio.getAnnexos().add(annex1);
        RegistreAnnex justificant = crearAnnex(
        		"justificant",
        		"justificant.pdf",
        		"application/pdf",
        		"9f33c5c7-7d0f-4d70-9082-c541a42cc041",
        		null, //getContingutJustificant(),
        		"1",
        		"EE01",
        		"TD02",
        		"02");
        anotacio.setJustificant(justificant);
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

	private RegistreAnnex crearAnnex(
			String titol,
			String arxiuNom,
			String arxiuTipusMime,
			String arxiuUuid,
			InputStream arxiuContingut,
			String eniOrigen,
			String eniEstatElaboracio,
			String eniTipusDocumental,
			String sicresTipusDocument) throws IOException, DatatypeConfigurationException {
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

	/*@SuppressWarnings("unused")
	private void afegirFirmes(RegistreAnnex annex) throws IOException {
		Firma firma2 = new Firma();
		File firmaFile2 = new File("c:/Feina/RIPEA/annexos/2018-01-24_CAdES_Detached_foto_jpg.csig");
        byte[] firmaContingut2 = FileUtils.readFileToByteArray(firmaFile2);
		firma2.setTipus("TF04");
		firma2.setPerfil("BES");
		firma2.setContingut(firmaContingut2);
		firma2.setFitxerNom("2018-01-24_CAdES_Detached_foto_jpg.csig");
		firma2.setTipusMime(Files.probeContentType(firmaFile2.toPath()));
		firma2.setCsvRegulacio("Regulació CSV 2");
		annex.getFirmes().add(firma2);
	}
	@SuppressWarnings("unused")
	private void afegirFirmes2(RegistreAnnex annex) throws IOException {
		Firma firma = new Firma();
		File firmaFile = new File("c:/Feina/RIPEA/annexos/firmes cert Toni/annex1_signed.pdf");
        byte[] firmaContingut = FileUtils.readFileToByteArray(firmaFile);
        firma.setTipus("TF06");
        firma.setPerfil("EPES");
        firma.setContingut(firmaContingut);
        firma.setFitxerNom("annex1_signed.pdf");
        firma.setTipusMime(Files.probeContentType(firmaFile.toPath()));
        firma.setCsvRegulacio("Regulació CSV 1");
		annex.getFirmes().add(firma);
	}
	private void afegirFirmesJpg(RegistreAnnex annex) throws IOException {
		Firma firma2 = new Firma();
		File firmaFile2 = new File("c:/Feina/RIPEA/annexos/firmes cert Toni/Koala.jpg_signed.csig");
        byte[] firmaContingut2 = FileUtils.readFileToByteArray(firmaFile2);
		firma2.setTipus("TF04");
		firma2.setPerfil("BES");
		firma2.setContingut(firmaContingut2);
		firma2.setFitxerNom("Koala.jpg_signed.csig");
		firma2.setTipusMime(Files.probeContentType(firmaFile2.toPath()));
		firma2.setCsvRegulacio("Regulació CSV 2");
		annex.getFirmes().add(firma2);
	}*/

	private BustiaV1 getBustiaServicePort() throws IOException {
		Properties testProperties = getTestProperties();
		return BustiaV1WsClientFactory.getWsClient(
				testProperties.getProperty("bustia.test.service.url"),
				testProperties.getProperty("bustia.test.service.username"),
				testProperties.getProperty("bustia.test.service.password"));
	}

	private InputStream getContingutAnnexSenseFirma() {
		InputStream is = getClass().getResourceAsStream(
        		"/annex_sense_firma.pdf");
		return is;
	}
	@SuppressWarnings("unused")
	private InputStream getContingutJustificant() {
		InputStream is = getClass().getResourceAsStream(
        		"/justificant.pdf");
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
