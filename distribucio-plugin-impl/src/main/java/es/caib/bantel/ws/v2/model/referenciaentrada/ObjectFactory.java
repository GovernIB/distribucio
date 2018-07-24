
package es.caib.bantel.ws.v2.model.referenciaentrada;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.bantel.ws.v2.model.referenciaentrada package. 
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

    private final static QName _ReferenciaEntradaClaveAcceso_QNAME = new QName("", "claveAcceso");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.bantel.ws.v2.model.referenciaentrada
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReferenciasEntrada }
     * 
     */
    public ReferenciasEntrada createReferenciasEntrada() {
        return new ReferenciasEntrada();
    }

    /**
     * Create an instance of {@link ReferenciaEntrada }
     * 
     */
    public ReferenciaEntrada createReferenciaEntrada() {
        return new ReferenciaEntrada();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "claveAcceso", scope = ReferenciaEntrada.class)
    public JAXBElement<String> createReferenciaEntradaClaveAcceso(String value) {
        return new JAXBElement<String>(_ReferenciaEntradaClaveAcceso_QNAME, String.class, ReferenciaEntrada.class, value);
    }

}
