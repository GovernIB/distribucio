
package es.caib.bantel.ws.v2.model.tramitebte;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.bantel.ws.v2.model.tramitebte package. 
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

    private final static QName _TramiteBTEUsuarioNif_QNAME = new QName("", "usuarioNif");
    private final static QName _TramiteBTEDelegadoNombre_QNAME = new QName("", "delegadoNombre");
    private final static QName _TramiteBTENumeroPreregistro_QNAME = new QName("", "numeroPreregistro");
    private final static QName _TramiteBTEHabilitarAvisos_QNAME = new QName("", "habilitarAvisos");
    private final static QName _TramiteBTEHabilitarNotificacionTelematica_QNAME = new QName("", "habilitarNotificacionTelematica");
    private final static QName _TramiteBTEReferenciaGestorDocumentalAsiento_QNAME = new QName("", "referenciaGestorDocumentalAsiento");
    private final static QName _TramiteBTERepresentadoNif_QNAME = new QName("", "representadoNif");
    private final static QName _TramiteBTECodigoDocumentoCustodiaJustificante_QNAME = new QName("", "codigoDocumentoCustodiaJustificante");
    private final static QName _TramiteBTEUsuarioNombre_QNAME = new QName("", "usuarioNombre");
    private final static QName _TramiteBTERepresentadoNombre_QNAME = new QName("", "representadoNombre");
    private final static QName _TramiteBTEDelegadoNif_QNAME = new QName("", "delegadoNif");
    private final static QName _TramiteBTEAvisoSMS_QNAME = new QName("", "avisoSMS");
    private final static QName _TramiteBTECodigoDocumentoCustodiaAsiento_QNAME = new QName("", "codigoDocumentoCustodiaAsiento");
    private final static QName _TramiteBTEUsuarioSeycon_QNAME = new QName("", "usuarioSeycon");
    private final static QName _TramiteBTETramiteSubsanacion_QNAME = new QName("", "tramiteSubsanacion");
    private final static QName _TramiteBTETipoConfirmacionPreregistro_QNAME = new QName("", "tipoConfirmacionPreregistro");
    private final static QName _TramiteBTEAvisoEmail_QNAME = new QName("", "avisoEmail");
    private final static QName _TramiteBTEReferenciaGestorDocumentalJustificante_QNAME = new QName("", "referenciaGestorDocumentalJustificante");
    private final static QName _TramiteBTEFechaPreregistro_QNAME = new QName("", "fechaPreregistro");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.bantel.ws.v2.model.tramitebte
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TramiteBTE }
     * 
     */
    public TramiteBTE createTramiteBTE() {
        return new TramiteBTE();
    }

    /**
     * Create an instance of {@link TramiteSubsanacion }
     * 
     */
    public TramiteSubsanacion createTramiteSubsanacion() {
        return new TramiteSubsanacion();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "usuarioNif", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEUsuarioNif(String value) {
        return new JAXBElement<String>(_TramiteBTEUsuarioNif_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "delegadoNombre", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEDelegadoNombre(String value) {
        return new JAXBElement<String>(_TramiteBTEDelegadoNombre_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "numeroPreregistro", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTENumeroPreregistro(String value) {
        return new JAXBElement<String>(_TramiteBTENumeroPreregistro_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "habilitarAvisos", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEHabilitarAvisos(String value) {
        return new JAXBElement<String>(_TramiteBTEHabilitarAvisos_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "habilitarNotificacionTelematica", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEHabilitarNotificacionTelematica(String value) {
        return new JAXBElement<String>(_TramiteBTEHabilitarNotificacionTelematica_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "referenciaGestorDocumentalAsiento", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEReferenciaGestorDocumentalAsiento(String value) {
        return new JAXBElement<String>(_TramiteBTEReferenciaGestorDocumentalAsiento_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "representadoNif", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTERepresentadoNif(String value) {
        return new JAXBElement<String>(_TramiteBTERepresentadoNif_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoDocumentoCustodiaJustificante", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTECodigoDocumentoCustodiaJustificante(String value) {
        return new JAXBElement<String>(_TramiteBTECodigoDocumentoCustodiaJustificante_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "usuarioNombre", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEUsuarioNombre(String value) {
        return new JAXBElement<String>(_TramiteBTEUsuarioNombre_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "representadoNombre", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTERepresentadoNombre(String value) {
        return new JAXBElement<String>(_TramiteBTERepresentadoNombre_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "delegadoNif", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEDelegadoNif(String value) {
        return new JAXBElement<String>(_TramiteBTEDelegadoNif_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "avisoSMS", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEAvisoSMS(String value) {
        return new JAXBElement<String>(_TramiteBTEAvisoSMS_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoDocumentoCustodiaAsiento", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTECodigoDocumentoCustodiaAsiento(String value) {
        return new JAXBElement<String>(_TramiteBTECodigoDocumentoCustodiaAsiento_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "usuarioSeycon", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEUsuarioSeycon(String value) {
        return new JAXBElement<String>(_TramiteBTEUsuarioSeycon_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TramiteSubsanacion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tramiteSubsanacion", scope = TramiteBTE.class)
    public JAXBElement<TramiteSubsanacion> createTramiteBTETramiteSubsanacion(TramiteSubsanacion value) {
        return new JAXBElement<TramiteSubsanacion>(_TramiteBTETramiteSubsanacion_QNAME, TramiteSubsanacion.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tipoConfirmacionPreregistro", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTETipoConfirmacionPreregistro(String value) {
        return new JAXBElement<String>(_TramiteBTETipoConfirmacionPreregistro_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "avisoEmail", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEAvisoEmail(String value) {
        return new JAXBElement<String>(_TramiteBTEAvisoEmail_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "referenciaGestorDocumentalJustificante", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEReferenciaGestorDocumentalJustificante(String value) {
        return new JAXBElement<String>(_TramiteBTEReferenciaGestorDocumentalJustificante_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fechaPreregistro", scope = TramiteBTE.class)
    public JAXBElement<XMLGregorianCalendar> createTramiteBTEFechaPreregistro(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_TramiteBTEFechaPreregistro_QNAME, XMLGregorianCalendar.class, TramiteBTE.class, value);
    }

}
