
package es.caib.bantel.ws.v2.model.tramitebte;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import es.caib.bantel.ws.v2.model.documentobte.DocumentosBTE;


/**
 * <p>Clase Java para TramiteBTE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TramiteBTE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroEntrada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoEntrada" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="unidadAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="fecha" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firmadaDigitalmente" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="procesada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificadorTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="versionTramite" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nivelAutenticacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="usuarioSeycon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descripcionTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoReferenciaRDSAsiento" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="claveReferenciaRDSAsiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoReferenciaRDSJustificante" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="claveReferenciaRDSJustificante" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numeroRegistro" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaRegistro" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="numeroPreregistro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaPreregistro" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="usuarioNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usuarioNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="representadoNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="representadoNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="delegadoNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="delegadoNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoConfirmacionPreregistro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="habilitarAvisos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisoSMS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisoEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="habilitarNotificacionTelematica" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tramiteSubsanacion" type="{urn:es:caib:bantel:ws:v2:model:TramiteBTE}TramiteSubsanacion" minOccurs="0"/>
 *         &lt;element name="referenciaGestorDocumentalAsiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoDocumentoCustodiaAsiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referenciaGestorDocumentalJustificante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoDocumentoCustodiaJustificante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentos" type="{urn:es:caib:bantel:ws:v2:model:DocumentoBTE}DocumentosBTE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TramiteBTE", propOrder = {
    "numeroEntrada",
    "codigoEntrada",
    "unidadAdministrativa",
    "fecha",
    "tipo",
    "firmadaDigitalmente",
    "procesada",
    "identificadorTramite",
    "versionTramite",
    "nivelAutenticacion",
    "usuarioSeycon",
    "descripcionTramite",
    "codigoReferenciaRDSAsiento",
    "claveReferenciaRDSAsiento",
    "codigoReferenciaRDSJustificante",
    "claveReferenciaRDSJustificante",
    "numeroRegistro",
    "fechaRegistro",
    "numeroPreregistro",
    "fechaPreregistro",
    "usuarioNif",
    "usuarioNombre",
    "representadoNif",
    "representadoNombre",
    "delegadoNif",
    "delegadoNombre",
    "idioma",
    "tipoConfirmacionPreregistro",
    "habilitarAvisos",
    "avisoSMS",
    "avisoEmail",
    "habilitarNotificacionTelematica",
    "tramiteSubsanacion",
    "referenciaGestorDocumentalAsiento",
    "codigoDocumentoCustodiaAsiento",
    "referenciaGestorDocumentalJustificante",
    "codigoDocumentoCustodiaJustificante",
    "documentos"
})
public class TramiteBTE {

    @XmlElement(required = true)
    protected String numeroEntrada;
    protected long codigoEntrada;
    protected long unidadAdministrativa;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fecha;
    @XmlElement(required = true)
    protected String tipo;
    protected boolean firmadaDigitalmente;
    @XmlElement(required = true)
    protected String procesada;
    @XmlElement(required = true)
    protected String identificadorTramite;
    protected int versionTramite;
    @XmlElement(required = true)
    protected String nivelAutenticacion;
    @XmlElementRef(name = "usuarioSeycon", type = JAXBElement.class, required = false)
    protected JAXBElement<String> usuarioSeycon;
    @XmlElement(required = true)
    protected String descripcionTramite;
    protected long codigoReferenciaRDSAsiento;
    @XmlElement(required = true)
    protected String claveReferenciaRDSAsiento;
    protected long codigoReferenciaRDSJustificante;
    @XmlElement(required = true)
    protected String claveReferenciaRDSJustificante;
    @XmlElement(required = true)
    protected String numeroRegistro;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaRegistro;
    @XmlElementRef(name = "numeroPreregistro", type = JAXBElement.class, required = false)
    protected JAXBElement<String> numeroPreregistro;
    @XmlElementRef(name = "fechaPreregistro", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> fechaPreregistro;
    @XmlElementRef(name = "usuarioNif", type = JAXBElement.class, required = false)
    protected JAXBElement<String> usuarioNif;
    @XmlElementRef(name = "usuarioNombre", type = JAXBElement.class, required = false)
    protected JAXBElement<String> usuarioNombre;
    @XmlElementRef(name = "representadoNif", type = JAXBElement.class, required = false)
    protected JAXBElement<String> representadoNif;
    @XmlElementRef(name = "representadoNombre", type = JAXBElement.class, required = false)
    protected JAXBElement<String> representadoNombre;
    @XmlElementRef(name = "delegadoNif", type = JAXBElement.class, required = false)
    protected JAXBElement<String> delegadoNif;
    @XmlElementRef(name = "delegadoNombre", type = JAXBElement.class, required = false)
    protected JAXBElement<String> delegadoNombre;
    @XmlElement(required = true)
    protected String idioma;
    @XmlElementRef(name = "tipoConfirmacionPreregistro", type = JAXBElement.class, required = false)
    protected JAXBElement<String> tipoConfirmacionPreregistro;
    @XmlElementRef(name = "habilitarAvisos", type = JAXBElement.class, required = false)
    protected JAXBElement<String> habilitarAvisos;
    @XmlElementRef(name = "avisoSMS", type = JAXBElement.class, required = false)
    protected JAXBElement<String> avisoSMS;
    @XmlElementRef(name = "avisoEmail", type = JAXBElement.class, required = false)
    protected JAXBElement<String> avisoEmail;
    @XmlElementRef(name = "habilitarNotificacionTelematica", type = JAXBElement.class, required = false)
    protected JAXBElement<String> habilitarNotificacionTelematica;
    @XmlElementRef(name = "tramiteSubsanacion", type = JAXBElement.class, required = false)
    protected JAXBElement<TramiteSubsanacion> tramiteSubsanacion;
    @XmlElementRef(name = "referenciaGestorDocumentalAsiento", type = JAXBElement.class, required = false)
    protected JAXBElement<String> referenciaGestorDocumentalAsiento;
    @XmlElementRef(name = "codigoDocumentoCustodiaAsiento", type = JAXBElement.class, required = false)
    protected JAXBElement<String> codigoDocumentoCustodiaAsiento;
    @XmlElementRef(name = "referenciaGestorDocumentalJustificante", type = JAXBElement.class, required = false)
    protected JAXBElement<String> referenciaGestorDocumentalJustificante;
    @XmlElementRef(name = "codigoDocumentoCustodiaJustificante", type = JAXBElement.class, required = false)
    protected JAXBElement<String> codigoDocumentoCustodiaJustificante;
    @XmlElement(required = true)
    protected DocumentosBTE documentos;

    /**
     * Obtiene el valor de la propiedad numeroEntrada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroEntrada() {
        return numeroEntrada;
    }

    /**
     * Define el valor de la propiedad numeroEntrada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroEntrada(String value) {
        this.numeroEntrada = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoEntrada.
     * 
     */
    public long getCodigoEntrada() {
        return codigoEntrada;
    }

    /**
     * Define el valor de la propiedad codigoEntrada.
     * 
     */
    public void setCodigoEntrada(long value) {
        this.codigoEntrada = value;
    }

    /**
     * Obtiene el valor de la propiedad unidadAdministrativa.
     * 
     */
    public long getUnidadAdministrativa() {
        return unidadAdministrativa;
    }

    /**
     * Define el valor de la propiedad unidadAdministrativa.
     * 
     */
    public void setUnidadAdministrativa(long value) {
        this.unidadAdministrativa = value;
    }

    /**
     * Obtiene el valor de la propiedad fecha.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFecha() {
        return fecha;
    }

    /**
     * Define el valor de la propiedad fecha.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFecha(XMLGregorianCalendar value) {
        this.fecha = value;
    }

    /**
     * Obtiene el valor de la propiedad tipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define el valor de la propiedad tipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }

    /**
     * Obtiene el valor de la propiedad firmadaDigitalmente.
     * 
     */
    public boolean isFirmadaDigitalmente() {
        return firmadaDigitalmente;
    }

    /**
     * Define el valor de la propiedad firmadaDigitalmente.
     * 
     */
    public void setFirmadaDigitalmente(boolean value) {
        this.firmadaDigitalmente = value;
    }

    /**
     * Obtiene el valor de la propiedad procesada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcesada() {
        return procesada;
    }

    /**
     * Define el valor de la propiedad procesada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcesada(String value) {
        this.procesada = value;
    }

    /**
     * Obtiene el valor de la propiedad identificadorTramite.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorTramite() {
        return identificadorTramite;
    }

    /**
     * Define el valor de la propiedad identificadorTramite.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorTramite(String value) {
        this.identificadorTramite = value;
    }

    /**
     * Obtiene el valor de la propiedad versionTramite.
     * 
     */
    public int getVersionTramite() {
        return versionTramite;
    }

    /**
     * Define el valor de la propiedad versionTramite.
     * 
     */
    public void setVersionTramite(int value) {
        this.versionTramite = value;
    }

    /**
     * Obtiene el valor de la propiedad nivelAutenticacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNivelAutenticacion() {
        return nivelAutenticacion;
    }

    /**
     * Define el valor de la propiedad nivelAutenticacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNivelAutenticacion(String value) {
        this.nivelAutenticacion = value;
    }

    /**
     * Obtiene el valor de la propiedad usuarioSeycon.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioSeycon() {
        return usuarioSeycon;
    }

    /**
     * Define el valor de la propiedad usuarioSeycon.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioSeycon(JAXBElement<String> value) {
        this.usuarioSeycon = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionTramite.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionTramite() {
        return descripcionTramite;
    }

    /**
     * Define el valor de la propiedad descripcionTramite.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionTramite(String value) {
        this.descripcionTramite = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoReferenciaRDSAsiento.
     * 
     */
    public long getCodigoReferenciaRDSAsiento() {
        return codigoReferenciaRDSAsiento;
    }

    /**
     * Define el valor de la propiedad codigoReferenciaRDSAsiento.
     * 
     */
    public void setCodigoReferenciaRDSAsiento(long value) {
        this.codigoReferenciaRDSAsiento = value;
    }

    /**
     * Obtiene el valor de la propiedad claveReferenciaRDSAsiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveReferenciaRDSAsiento() {
        return claveReferenciaRDSAsiento;
    }

    /**
     * Define el valor de la propiedad claveReferenciaRDSAsiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveReferenciaRDSAsiento(String value) {
        this.claveReferenciaRDSAsiento = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoReferenciaRDSJustificante.
     * 
     */
    public long getCodigoReferenciaRDSJustificante() {
        return codigoReferenciaRDSJustificante;
    }

    /**
     * Define el valor de la propiedad codigoReferenciaRDSJustificante.
     * 
     */
    public void setCodigoReferenciaRDSJustificante(long value) {
        this.codigoReferenciaRDSJustificante = value;
    }

    /**
     * Obtiene el valor de la propiedad claveReferenciaRDSJustificante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveReferenciaRDSJustificante() {
        return claveReferenciaRDSJustificante;
    }

    /**
     * Define el valor de la propiedad claveReferenciaRDSJustificante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveReferenciaRDSJustificante(String value) {
        this.claveReferenciaRDSJustificante = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroRegistro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    /**
     * Define el valor de la propiedad numeroRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroRegistro(String value) {
        this.numeroRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaRegistro.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Define el valor de la propiedad fechaRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaRegistro(XMLGregorianCalendar value) {
        this.fechaRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroPreregistro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNumeroPreregistro() {
        return numeroPreregistro;
    }

    /**
     * Define el valor de la propiedad numeroPreregistro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNumeroPreregistro(JAXBElement<String> value) {
        this.numeroPreregistro = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaPreregistro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaPreregistro() {
        return fechaPreregistro;
    }

    /**
     * Define el valor de la propiedad fechaPreregistro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaPreregistro(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaPreregistro = value;
    }

    /**
     * Obtiene el valor de la propiedad usuarioNif.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioNif() {
        return usuarioNif;
    }

    /**
     * Define el valor de la propiedad usuarioNif.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioNif(JAXBElement<String> value) {
        this.usuarioNif = value;
    }

    /**
     * Obtiene el valor de la propiedad usuarioNombre.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioNombre() {
        return usuarioNombre;
    }

    /**
     * Define el valor de la propiedad usuarioNombre.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioNombre(JAXBElement<String> value) {
        this.usuarioNombre = value;
    }

    /**
     * Obtiene el valor de la propiedad representadoNif.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRepresentadoNif() {
        return representadoNif;
    }

    /**
     * Define el valor de la propiedad representadoNif.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRepresentadoNif(JAXBElement<String> value) {
        this.representadoNif = value;
    }

    /**
     * Obtiene el valor de la propiedad representadoNombre.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRepresentadoNombre() {
        return representadoNombre;
    }

    /**
     * Define el valor de la propiedad representadoNombre.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRepresentadoNombre(JAXBElement<String> value) {
        this.representadoNombre = value;
    }

    /**
     * Obtiene el valor de la propiedad delegadoNif.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDelegadoNif() {
        return delegadoNif;
    }

    /**
     * Define el valor de la propiedad delegadoNif.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDelegadoNif(JAXBElement<String> value) {
        this.delegadoNif = value;
    }

    /**
     * Obtiene el valor de la propiedad delegadoNombre.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDelegadoNombre() {
        return delegadoNombre;
    }

    /**
     * Define el valor de la propiedad delegadoNombre.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDelegadoNombre(JAXBElement<String> value) {
        this.delegadoNombre = value;
    }

    /**
     * Obtiene el valor de la propiedad idioma.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Define el valor de la propiedad idioma.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdioma(String value) {
        this.idioma = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoConfirmacionPreregistro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTipoConfirmacionPreregistro() {
        return tipoConfirmacionPreregistro;
    }

    /**
     * Define el valor de la propiedad tipoConfirmacionPreregistro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTipoConfirmacionPreregistro(JAXBElement<String> value) {
        this.tipoConfirmacionPreregistro = value;
    }

    /**
     * Obtiene el valor de la propiedad habilitarAvisos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHabilitarAvisos() {
        return habilitarAvisos;
    }

    /**
     * Define el valor de la propiedad habilitarAvisos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHabilitarAvisos(JAXBElement<String> value) {
        this.habilitarAvisos = value;
    }

    /**
     * Obtiene el valor de la propiedad avisoSMS.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAvisoSMS() {
        return avisoSMS;
    }

    /**
     * Define el valor de la propiedad avisoSMS.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAvisoSMS(JAXBElement<String> value) {
        this.avisoSMS = value;
    }

    /**
     * Obtiene el valor de la propiedad avisoEmail.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAvisoEmail() {
        return avisoEmail;
    }

    /**
     * Define el valor de la propiedad avisoEmail.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAvisoEmail(JAXBElement<String> value) {
        this.avisoEmail = value;
    }

    /**
     * Obtiene el valor de la propiedad habilitarNotificacionTelematica.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHabilitarNotificacionTelematica() {
        return habilitarNotificacionTelematica;
    }

    /**
     * Define el valor de la propiedad habilitarNotificacionTelematica.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHabilitarNotificacionTelematica(JAXBElement<String> value) {
        this.habilitarNotificacionTelematica = value;
    }

    /**
     * Obtiene el valor de la propiedad tramiteSubsanacion.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TramiteSubsanacion }{@code >}
     *     
     */
    public JAXBElement<TramiteSubsanacion> getTramiteSubsanacion() {
        return tramiteSubsanacion;
    }

    /**
     * Define el valor de la propiedad tramiteSubsanacion.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TramiteSubsanacion }{@code >}
     *     
     */
    public void setTramiteSubsanacion(JAXBElement<TramiteSubsanacion> value) {
        this.tramiteSubsanacion = value;
    }

    /**
     * Obtiene el valor de la propiedad referenciaGestorDocumentalAsiento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReferenciaGestorDocumentalAsiento() {
        return referenciaGestorDocumentalAsiento;
    }

    /**
     * Define el valor de la propiedad referenciaGestorDocumentalAsiento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReferenciaGestorDocumentalAsiento(JAXBElement<String> value) {
        this.referenciaGestorDocumentalAsiento = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoDocumentoCustodiaAsiento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoDocumentoCustodiaAsiento() {
        return codigoDocumentoCustodiaAsiento;
    }

    /**
     * Define el valor de la propiedad codigoDocumentoCustodiaAsiento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoDocumentoCustodiaAsiento(JAXBElement<String> value) {
        this.codigoDocumentoCustodiaAsiento = value;
    }

    /**
     * Obtiene el valor de la propiedad referenciaGestorDocumentalJustificante.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReferenciaGestorDocumentalJustificante() {
        return referenciaGestorDocumentalJustificante;
    }

    /**
     * Define el valor de la propiedad referenciaGestorDocumentalJustificante.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReferenciaGestorDocumentalJustificante(JAXBElement<String> value) {
        this.referenciaGestorDocumentalJustificante = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoDocumentoCustodiaJustificante.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoDocumentoCustodiaJustificante() {
        return codigoDocumentoCustodiaJustificante;
    }

    /**
     * Define el valor de la propiedad codigoDocumentoCustodiaJustificante.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoDocumentoCustodiaJustificante(JAXBElement<String> value) {
        this.codigoDocumentoCustodiaJustificante = value;
    }

    /**
     * Obtiene el valor de la propiedad documentos.
     * 
     * @return
     *     possible object is
     *     {@link DocumentosBTE }
     *     
     */
    public DocumentosBTE getDocumentos() {
        return documentos;
    }

    /**
     * Define el valor de la propiedad documentos.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentosBTE }
     *     
     */
    public void setDocumentos(DocumentosBTE value) {
        this.documentos = value;
    }

}
