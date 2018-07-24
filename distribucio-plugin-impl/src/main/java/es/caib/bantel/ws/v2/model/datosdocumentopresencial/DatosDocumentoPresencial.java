
package es.caib.bantel.ws.v2.model.datosdocumentopresencial;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DatosDocumentoPresencial complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DatosDocumentoPresencial">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipoDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="compulsarDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fotocopia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firma" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosDocumentoPresencial", propOrder = {
    "tipoDocumento",
    "compulsarDocumento",
    "fotocopia",
    "firma"
})
public class DatosDocumentoPresencial {

    @XmlElement(required = true)
    protected String tipoDocumento;
    @XmlElement(required = true)
    protected String compulsarDocumento;
    @XmlElement(required = true)
    protected String fotocopia;
    @XmlElement(required = true)
    protected String firma;

    /**
     * Obtiene el valor de la propiedad tipoDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Define el valor de la propiedad tipoDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumento(String value) {
        this.tipoDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad compulsarDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompulsarDocumento() {
        return compulsarDocumento;
    }

    /**
     * Define el valor de la propiedad compulsarDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompulsarDocumento(String value) {
        this.compulsarDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad fotocopia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFotocopia() {
        return fotocopia;
    }

    /**
     * Define el valor de la propiedad fotocopia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFotocopia(String value) {
        this.fotocopia = value;
    }

    /**
     * Obtiene el valor de la propiedad firma.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirma() {
        return firma;
    }

    /**
     * Define el valor de la propiedad firma.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirma(String value) {
        this.firma = value;
    }

}
