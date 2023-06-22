package es.caib.distribucio.backoffice.utils.sistra;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import es.caib.distribucio.backoffice.utils.sistra.formulario.Formulario;
import es.caib.distribucio.backoffice.utils.sistra.pago.Pago;

/** Implementació de la classe d'utilitats per Sistra2 pels backoffices de Distribucio
 * 
 */
public class BackofficeSistra2UtilsImpl implements BackofficeSistra2Utils {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pago parseXmlPago(byte[] contingut) throws Exception {
		Pago pago;
		try {
	    	JAXBContext jaxbContext = JAXBContext.newInstance(Pago.class);
	    	Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    	pago = (Pago) jaxbUnmarshaller.unmarshal( new StringReader(new String(contingut)) );
		} catch(Exception e) {
			String errMsg = "Error obtenint la informacó del pagamant a partir del contingut de l'annex : " + e.getMessage();
			throw new Exception(errMsg, e);
		}
		return pago;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Formulario parseXmlFormulario(byte[] contingut) throws Exception {
		Formulario formulario;
		try {
	    	JAXBContext jaxbContext = JAXBContext.newInstance(Formulario.class);
	    	Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	    	formulario = (Formulario) jaxbUnmarshaller.unmarshal( new StringReader(new String(contingut)) );
		} catch(Exception e) {
			String errMsg = "Error obtenint la informacó del formulari a partir del contingut de l'annex: " + e.getMessage();
			throw new Exception(errMsg, e);
		}
		return formulario;
	}

}
