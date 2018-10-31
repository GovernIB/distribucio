/**
 * 
 */
package es.caib.distribucio.ws.client;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import es.caib.distribucio.ws.v1.bustia.BustiaV1;
import es.caib.distribucio.ws.v1.bustia.BustiaV1Service;
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

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/distribucio/ws/v1/bustia";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin15";

	@Test
	public void test() throws DatatypeConfigurationException, IOException {
		
		Random generator = new Random(); 
		int randomNumber = generator.nextInt(9999) + 1;
		
		RegistreAnotacio anotacio = new RegistreAnotacio(); 
		anotacio.setAplicacioCodi("CLIENT_TEST");
		anotacio.setAplicacioVersio("2");
		anotacio.setAssumpteCodi("A1");
        anotacio.setAssumpteDescripcio("Descripcio CodA");
        anotacio.setAssumpteTipusCodi("A1");
        anotacio.setAssumpteDescripcio("Assumpte de proves");
        anotacio.setUsuariCodi("u104848");
        anotacio.setUsuariNom("VHZ");
        anotacio.setData(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        anotacio.setExtracte("Anotació amb annexos sense firma per provar autofirma servidor");
        anotacio.setOficinaCodi("10");
        anotacio.setOficinaDescripcio("Oficina de proves");
        anotacio.setEntitatCodi("codientitat");
        anotacio.setEntitatDescripcio("Descripció entitat");
        anotacio.setLlibreCodi("L01");
        anotacio.setLlibreDescripcio("llibre descripció");
        anotacio.setNumero(String.valueOf(randomNumber));
        anotacio.setIdiomaCodi("1");
        anotacio.setIdiomaDescripcio("Català");
        anotacio.setIdentificador("15/10/2015");
        anotacio.setExpedientNumero(String.valueOf(randomNumber));
        
        File file = new File("c:/Feina/RIPEA/annexos/firmes cert Toni/Koala.jpg");
        byte[] encodedContingut = FileUtils.readFileToByteArray(file);
        RegistreAnnex annex1 = new RegistreAnnex();
        annex1.setTitol("Koala imatge");
        annex1.setFitxerNom(file.getName());
        annex1.setFitxerTipusMime(Files.probeContentType(file.toPath()));
        annex1.setFitxerContingut(encodedContingut);
        annex1.setFitxerTamany((int)(file.length()));
        annex1.setEniDataCaptura(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        annex1.setEniOrigen("0");
        annex1.setEniEstatElaboracio("EE01");
        annex1.setEniTipusDocumental("TD01");
        annex1.setSicresTipusDocument("01");
        afegirFirmesJpg(annex1);

        anotacio.getAnnexos().add(annex1);


//        File file2 = new File("c:/Feina/RIPEA/annexos/firmes cert Toni/annex1.pdf");
//        byte[] encodedContingut2 = FileUtils.readFileToByteArray(file2);
//        RegistreAnnex annex2 = new RegistreAnnex();
//        annex2.setTitol("annexproves2");
//        annex2.setFitxerNom(file2.getName());
////        annex2.setFitxerTipusMime(Files.probeContentType(file2.toPath()));
//        annex2.setFitxerContingut(encodedContingut2);
////        annex2.setFitxerTamany((int)(file2.length()));
////        annex2.setFitxerArxiuUuid("0d7465ae-ac35-4835-9100-911487fba14f");
//        annex2.setEniDataCaptura(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
//        annex2.setEniOrigen("1");
//        annex2.setEniEstatElaboracio("EE01");
//        annex2.setEniTipusDocumental("TD02");
//        annex2.setSicresTipusDocument("02");
//        afegirFirmes2(annex2);
//
//        anotacio.getAnnexos().add(annex2);
        
        
        File file3 = new File("c:/Feina/RIPEA/annexos/justificant.pdf");
        byte[] encodedContingut3 = FileUtils.readFileToByteArray(file3);
        RegistreAnnex justificant = new RegistreAnnex();
        justificant.setTitol("justificant");
        justificant.setFitxerNom(file3.getName());
        justificant.setFitxerTipusMime(Files.probeContentType(file3.toPath()));
        justificant.setFitxerContingut(encodedContingut3);
        justificant.setFitxerTamany((int)(file3.length()));
        justificant.setEniDataCaptura(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        justificant.setEniOrigen("1");
        justificant.setEniEstatElaboracio("EE01");
        justificant.setEniTipusDocumental("TD02");
        justificant.setSicresTipusDocument("02");
        justificant.setFitxerArxiuUuid("9f33c5c7-7d0f-4d70-9082-c541a42cc041");
        
//        anotacio.setJustificant(justificant);
        
//        afegirInteressats(anotacio);
        
        try {
    		getBustiaServicePort().enviarAnotacioRegistreEntrada(
    				"E00003601", // "entitatCodi",
    				"E03029603", // "unitatAdministrativaCodi",
    				anotacio);        	
        } catch (Exception e) {
        	System.err.println("Error invocant el WS: " + e.getMessage());
        	e.printStackTrace();
        	fail();
        }
	}
	
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
	}

	private BustiaV1 getBustiaServicePort() throws MalformedURLException {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		BustiaV1 bustia = new BustiaV1Service(url).getBustiaV1ServicePort();
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		if (USERNAME != null) {
			BindingProvider bp = (BindingProvider)bustia;
			bp.getBinding().setHandlerChain(handlerChain);
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					USERNAME);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					PASSWORD);
		}
		return bustia;
	}

}
