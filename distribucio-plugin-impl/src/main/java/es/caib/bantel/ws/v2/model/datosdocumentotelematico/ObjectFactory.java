
package es.caib.bantel.ws.v2.model.datosdocumentotelematico;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.bantel.ws.v2.model.datosdocumentotelematico package. 
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

    private final static QName _DatosDocumentoTelematicoReferenciaGestorDocumental_QNAME = new QName("", "referenciaGestorDocumental");
    private final static QName _DatosDocumentoTelematicoCodigoDocumentoCustodia_QNAME = new QName("", "codigoDocumentoCustodia");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.bantel.ws.v2.model.datosdocumentotelematico
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DatosDocumentoTelematico }
     * 
     */
    public DatosDocumentoTelematico createDatosDocumentoTelematico() {
        return new DatosDocumentoTelematico();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "referenciaGestorDocumental", scope = DatosDocumentoTelematico.class)
    public JAXBElement<String> createDatosDocumentoTelematicoReferenciaGestorDocumental(String value) {
        return new JAXBElement<String>(_DatosDocumentoTelematicoReferenciaGestorDocumental_QNAME, String.class, DatosDocumentoTelematico.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoDocumentoCustodia", scope = DatosDocumentoTelematico.class)
    public JAXBElement<String> createDatosDocumentoTelematicoCodigoDocumentoCustodia(String value) {
        return new JAXBElement<String>(_DatosDocumentoTelematicoCodigoDocumentoCustodia_QNAME, String.class, DatosDocumentoTelematico.class, value);
    }

}
