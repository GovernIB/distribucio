
package es.caib.bantel.ws.v2.model.datosdocumentotelematico;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import es.caib.bantel.ws.v2.model.firmaws.FirmasWS;


/**
 * <p>Clase Java para DatosDocumentoTelematico complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DatosDocumentoTelematico">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoReferenciaRds" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="claveReferenciaRds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="referenciaGestorDocumental" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="firmas" type="{urn:es:caib:bantel:ws:v2:model:FirmaWS}FirmasWS"/>
 *         &lt;element name="estructurado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="codigoDocumentoCustodia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosDocumentoTelematico", propOrder = {
    "codigoReferenciaRds",
    "claveReferenciaRds",
    "referenciaGestorDocumental",
    "nombre",
    "extension",
    "content",
    "firmas",
    "estructurado",
    "codigoDocumentoCustodia"
})
public class DatosDocumentoTelematico {

    protected long codigoReferenciaRds;
    @XmlElement(required = true)
    protected String claveReferenciaRds;
    @XmlElementRef(name = "referenciaGestorDocumental", type = JAXBElement.class, required = false)
    protected JAXBElement<String> referenciaGestorDocumental;
    @XmlElement(required = true)
    protected String nombre;
    @XmlElement(required = true)
    protected String extension;
    @XmlElement(required = true)
    protected byte[] content;
    @XmlElement(required = true)
    protected FirmasWS firmas;
    protected boolean estructurado;
    @XmlElementRef(name = "codigoDocumentoCustodia", type = JAXBElement.class, required = false)
    protected JAXBElement<String> codigoDocumentoCustodia;

    /**
     * Obtiene el valor de la propiedad codigoReferenciaRds.
     * 
     */
    public long getCodigoReferenciaRds() {
        return codigoReferenciaRds;
    }

    /**
     * Define el valor de la propiedad codigoReferenciaRds.
     * 
     */
    public void setCodigoReferenciaRds(long value) {
        this.codigoReferenciaRds = value;
    }

    /**
     * Obtiene el valor de la propiedad claveReferenciaRds.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveReferenciaRds() {
        return claveReferenciaRds;
    }

    /**
     * Define el valor de la propiedad claveReferenciaRds.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveReferenciaRds(String value) {
        this.claveReferenciaRds = value;
    }

    /**
     * Obtiene el valor de la propiedad referenciaGestorDocumental.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReferenciaGestorDocumental() {
        return referenciaGestorDocumental;
    }

    /**
     * Define el valor de la propiedad referenciaGestorDocumental.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReferenciaGestorDocumental(JAXBElement<String> value) {
        this.referenciaGestorDocumental = value;
    }

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad extension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Define el valor de la propiedad extension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Obtiene el valor de la propiedad content.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Define el valor de la propiedad content.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContent(byte[] value) {
        this.content = value;
    }

    /**
     * Obtiene el valor de la propiedad firmas.
     * 
     * @return
     *     possible object is
     *     {@link FirmasWS }
     *     
     */
    public FirmasWS getFirmas() {
        return firmas;
    }

    /**
     * Define el valor de la propiedad firmas.
     * 
     * @param value
     *     allowed object is
     *     {@link FirmasWS }
     *     
     */
    public void setFirmas(FirmasWS value) {
        this.firmas = value;
    }

    /**
     * Obtiene el valor de la propiedad estructurado.
     * 
     */
    public boolean isEstructurado() {
        return estructurado;
    }

    /**
     * Define el valor de la propiedad estructurado.
     * 
     */
    public void setEstructurado(boolean value) {
        this.estructurado = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoDocumentoCustodia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoDocumentoCustodia() {
        return codigoDocumentoCustodia;
    }

    /**
     * Define el valor de la propiedad codigoDocumentoCustodia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoDocumentoCustodia(JAXBElement<String> value) {
        this.codigoDocumentoCustodia = value;
    }

}
