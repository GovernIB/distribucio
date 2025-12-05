package es.caib.distribucio.plugin.caib.validacio;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.distribucio.plugin.utils.PropertiesHelper;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;

public class ValidacioFirmaAgilTest {

	private static final String API_ENDPOINT_ADDRESS = "https://dev.caib.es/evidenciesibapi/externa";
	private static final String API_USERNAME = "$distribucio_evidenciesib_dev";
	private static final String API_PASSWORD = "**************";
	
	private ValidacioSignaturaPlugin plugin;
	
	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugins.validatesignature.api.evidenciesib.endpoint",
				API_ENDPOINT_ADDRESS);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugins.validatesignature.api.evidenciesib.username",
				API_USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugins.validatesignature.api.evidenciesib.password",
				API_PASSWORD);
		plugin = new ValidacioFirmaPluginApiEvidenciesIB(PropertiesHelper.getProperties(), false);
	}
	
	@Test
	public void test() {
		try {
			String documentNom = "annex_firma_agil.pdf";
			String documentMime = "application/pdf";
			byte[] documentContingut = this.getContingut("/" + documentNom);
			ValidaSignaturaResposta resposta = plugin.validaSignatura(documentNom, documentMime, documentContingut, null);
			
			System.out.println("Resposta: " + resposta);
			
		} catch(Exception e) {
			System.err.println("Error executant el test: " + e.getClass() + " " + e.getMessage());
			e.printStackTrace(System.err);
			fail("Error capturat: " + e.getClass() + ": " + e.getMessage());
		}
	}
	
	private byte[] getContingut(String fitxerNom) {
		byte[] contingut;
		try {
			InputStream arxiuContingut = getClass().getResourceAsStream(fitxerNom);
			contingut = IOUtils.toByteArray(arxiuContingut);
		} catch(Exception e) {
			throw new RuntimeException("Error obtenint el contingut de " + fitxerNom + ": " + e.getClass() + ": " + e.getMessage(), e);
		}
		return contingut;
	}
	
}
