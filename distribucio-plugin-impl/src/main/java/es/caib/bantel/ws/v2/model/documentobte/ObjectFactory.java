
package es.caib.bantel.ws.v2.model.documentobte;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import es.caib.bantel.ws.v2.model.datosdocumentopresencial.DatosDocumentoPresencial;
import es.caib.bantel.ws.v2.model.datosdocumentotelematico.DatosDocumentoTelematico;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.bantel.ws.v2.model.documentobte package. 
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

    private final static QName _DocumentoBTEPresentacionPresencial_QNAME = new QName("", "presentacionPresencial");
    private final static QName _DocumentoBTEPresentacionTelematica_QNAME = new QName("", "presentacionTelematica");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.bantel.ws.v2.model.documentobte
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DocumentosBTE }
     * 
     */
    public DocumentosBTE createDocumentosBTE() {
        return new DocumentosBTE();
    }

    /**
     * Create an instance of {@link DocumentoBTE }
     * 
     */
    public DocumentoBTE createDocumentoBTE() {
        return new DocumentoBTE();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatosDocumentoPresencial }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "presentacionPresencial", scope = DocumentoBTE.class)
    public JAXBElement<DatosDocumentoPresencial> createDocumentoBTEPresentacionPresencial(DatosDocumentoPresencial value) {
        return new JAXBElement<DatosDocumentoPresencial>(_DocumentoBTEPresentacionPresencial_QNAME, DatosDocumentoPresencial.class, DocumentoBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatosDocumentoTelematico }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "presentacionTelematica", scope = DocumentoBTE.class)
    public JAXBElement<DatosDocumentoTelematico> createDocumentoBTEPresentacionTelematica(DatosDocumentoTelematico value) {
        return new JAXBElement<DatosDocumentoTelematico>(_DocumentoBTEPresentacionTelematica_QNAME, DatosDocumentoTelematico.class, DocumentoBTE.class, value);
    }

}
