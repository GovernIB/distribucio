package es.caib.distribucio.backoffice.utils.sistra;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import es.caib.distribucio.backoffice.utils.sistra.formulario.Campo;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Formulario;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Valor;
import es.caib.distribucio.backoffice.utils.sistra.pago.ConfirmacionPago;
import es.caib.distribucio.backoffice.utils.sistra.pago.Contribuyente;
import es.caib.distribucio.backoffice.utils.sistra.pago.DatosPago;
import es.caib.distribucio.backoffice.utils.sistra.pago.Pago;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeSistra2UtilsTest {

	private BackofficeSistra2Utils sistraUtils = new BackofficeSistra2UtilsImpl();

	/** Prova d'extreure les dades del pagament a partir del congingut xml de l'annex.*/
	@Test
	public void parseXmlPago() throws Exception {
		// Given
		byte[] contingut = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("es/caib/distribucio/backoffice/utils/sistra/pago.xml").toURI()));
		// When
		Pago pago = null;
		try {
			pago = sistraUtils.parseXmlPago(contingut);
		} catch (Exception e) {
			fail("Error capturat : " + e.getMessage());
		}
    	// Then
    	assertTrue(pago != null);
	}
	
	/** Prova de convertir el pagament a XML */
	@Test
	public void pagoToXml() throws Exception {
		// Given
		// Pago
		Pago pago = new Pago();
		pago.setIdentificador("identificador");
		// Pago.datosPago
		DatosPago datosPago = new DatosPago();
		datosPago.setConcepto("concepto");
		pago.setDatosPago(datosPago);
		// Pago.datosPago.contribuyente
		Contribuyente contribuyente = new Contribuyente();
		contribuyente.setNif("00000000R");
		contribuyente.setNombre("Contribuyente");
		datosPago.setContribuyente(contribuyente);
		// Pago.confirmacionPago
		ConfirmacionPago confirmacionPago = new ConfirmacionPago();
		confirmacionPago.setFechaPago("01/01/1970");
		confirmacionPago.setLocalizador("localitzador");
		pago.setConfirmacionPago(confirmacionPago);
		// When
    	JAXBContext jaxbContext = JAXBContext.newInstance(Pago.class);
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	StringWriter writer = new StringWriter();
    	jaxbMarshaller.marshal(pago, writer);
    	String xml = writer.toString();
    	// Then
    	assertTrue(xml != null && xml.contains("PAGO"));
	}
	
	/** Prova d'extreure les dades del formulari a partir del congingut xml de l'annex.*/
	@Test
	public void parseXmlFormulario() throws Exception {
		// Given
		byte[] contingut = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("es/caib/distribucio/backoffice/utils/sistra/formulario.xml").toURI()));
		// When
		Formulario formulario = null;
		try {
			formulario = sistraUtils.parseXmlFormulario(contingut);
		} catch (Exception e) {
			fail("Error capturat : " + e.getMessage());
		}
    	// Then
    	assertTrue(formulario != null);
	}
	
	/** Prova de convertir el pagament a XML */
	@Test
	public void formularioToXml() throws Exception {
		// Given
		// Formulario
		Formulario formulario = new Formulario();
		formulario.setAccion("accion");
		Campo campo;
		Valor valor;
		for (int i = 0; i<2; i++) {
			// Pago.campo
			campo = new Campo();
			campo.setId("campo_" + i);
			for (int j = 0; j<2; j++) {
				// pago.campo.valor
				valor = new Valor();
				valor.setCodigo("cod_" + i);
				valor.setValue("valor_i");
				campo.getValores().add(valor);
			}
			formulario.getCampos().add(campo);
		}
		// When
    	JAXBContext jaxbContext = JAXBContext.newInstance(Formulario.class);
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	StringWriter writer = new StringWriter();
    	jaxbMarshaller.marshal(formulario, writer);
    	String xml = writer.toString();
    	// Then
    	assertTrue(xml != null && xml.contains("FORMULARIO"));
	}

}
