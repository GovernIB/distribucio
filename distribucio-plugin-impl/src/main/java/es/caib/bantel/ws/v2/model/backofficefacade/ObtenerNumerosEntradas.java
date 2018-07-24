
package es.caib.bantel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificadorProcedimiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificadorTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="procesada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="desde" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="hasta" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identificadorProcedimiento",
    "identificadorTramite",
    "procesada",
    "desde",
    "hasta"
})
@XmlRootElement(name = "obtenerNumerosEntradas")
public class ObtenerNumerosEntradas {

    @XmlElement(required = true)
    protected String identificadorProcedimiento;
    @XmlElement(required = true)
    protected String identificadorTramite;
    @XmlElement(required = true)
    protected String procesada;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar desde;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar hasta;

    /**
     * Obtiene el valor de la propiedad identificadorProcedimiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorProcedimiento() {
        return identificadorProcedimiento;
    }

    /**
     * Define el valor de la propiedad identificadorProcedimiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorProcedimiento(String value) {
        this.identificadorProcedimiento = value;
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
     * Obtiene el valor de la propiedad desde.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDesde() {
        return desde;
    }

    /**
     * Define el valor de la propiedad desde.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDesde(XMLGregorianCalendar value) {
        this.desde = value;
    }

    /**
     * Obtiene el valor de la propiedad hasta.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHasta() {
        return hasta;
    }

    /**
     * Define el valor de la propiedad hasta.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHasta(XMLGregorianCalendar value) {
        this.hasta = value;
    }

}
