
package es.caib.bantel.ws.v2.model.backofficefacade;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.bantel.ws.v2.model.backofficefacade package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Fault_QNAME = new QName("urn:es:caib:bantel:ws:v2:model:BackofficeFacade", "fault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.bantel.ws.v2.model.backofficefacade
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ObtenerEntradaResponse }
     * 
     */
    public ObtenerEntradaResponse createObtenerEntradaResponse() {
        return new ObtenerEntradaResponse();
    }

    /**
     * Create an instance of {@link ObtenerNumerosEntradas }
     * 
     */
    public ObtenerNumerosEntradas createObtenerNumerosEntradas() {
        return new ObtenerNumerosEntradas();
    }

    /**
     * Create an instance of {@link BackofficeFacadeException }
     * 
     */
    public BackofficeFacadeException createBackofficeFacadeException() {
        return new BackofficeFacadeException();
    }

    /**
     * Create an instance of {@link EstablecerResultadoProcesoResponse }
     * 
     */
    public EstablecerResultadoProcesoResponse createEstablecerResultadoProcesoResponse() {
        return new EstablecerResultadoProcesoResponse();
    }

    /**
     * Create an instance of {@link ObtenerNumerosEntradasResponse }
     * 
     */
    public ObtenerNumerosEntradasResponse createObtenerNumerosEntradasResponse() {
        return new ObtenerNumerosEntradasResponse();
    }

    /**
     * Create an instance of {@link ObtenerEntrada }
     * 
     */
    public ObtenerEntrada createObtenerEntrada() {
        return new ObtenerEntrada();
    }

    /**
     * Create an instance of {@link EstablecerResultadoProceso }
     * 
     */
    public EstablecerResultadoProceso createEstablecerResultadoProceso() {
        return new EstablecerResultadoProceso();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BackofficeFacadeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:bantel:ws:v2:model:BackofficeFacade", name = "fault")
    public JAXBElement<BackofficeFacadeException> createFault(BackofficeFacadeException value) {
        return new JAXBElement<BackofficeFacadeException>(_Fault_QNAME, BackofficeFacadeException.class, null, value);
    }

}
