
package es.caib.bantel.ws.v2.model.documentobte;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import es.caib.bantel.ws.v2.model.datosdocumentopresencial.DatosDocumentoPresencial;
import es.caib.bantel.ws.v2.model.datosdocumentotelematico.DatosDocumentoTelematico;


/**
 * <p>Clase Java para DocumentoBTE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DocumentoBTE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numeroInstancia" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="presentacionTelematica" type="{urn:es:caib:bantel:ws:v2:model:DatosDocumentoTelematico}DatosDocumentoTelematico" minOccurs="0"/>
 *         &lt;element name="presentacionPresencial" type="{urn:es:caib:bantel:ws:v2:model:DatosDocumentoPresencial}DatosDocumentoPresencial" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoBTE", propOrder = {
    "nombre",
    "identificador",
    "numeroInstancia",
    "presentacionTelematica",
    "presentacionPresencial"
})
public class DocumentoBTE {

    @XmlElement(required = true)
    protected String nombre;
    @XmlElement(required = true)
    protected String identificador;
    protected int numeroInstancia;
    @XmlElementRef(name = "presentacionTelematica", type = JAXBElement.class, required = false)
    protected JAXBElement<DatosDocumentoTelematico> presentacionTelematica;
    @XmlElementRef(name = "presentacionPresencial", type = JAXBElement.class, required = false)
    protected JAXBElement<DatosDocumentoPresencial> presentacionPresencial;

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
     * Obtiene el valor de la propiedad identificador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Define el valor de la propiedad identificador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificador(String value) {
        this.identificador = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroInstancia.
     * 
     */
    public int getNumeroInstancia() {
        return numeroInstancia;
    }

    /**
     * Define el valor de la propiedad numeroInstancia.
     * 
     */
    public void setNumeroInstancia(int value) {
        this.numeroInstancia = value;
    }

    /**
     * Obtiene el valor de la propiedad presentacionTelematica.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoTelematico }{@code >}
     *     
     */
    public JAXBElement<DatosDocumentoTelematico> getPresentacionTelematica() {
        return presentacionTelematica;
    }

    /**
     * Define el valor de la propiedad presentacionTelematica.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoTelematico }{@code >}
     *     
     */
    public void setPresentacionTelematica(JAXBElement<DatosDocumentoTelematico> value) {
        this.presentacionTelematica = value;
    }

    /**
     * Obtiene el valor de la propiedad presentacionPresencial.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoPresencial }{@code >}
     *     
     */
    public JAXBElement<DatosDocumentoPresencial> getPresentacionPresencial() {
        return presentacionPresencial;
    }

    /**
     * Define el valor de la propiedad presentacionPresencial.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoPresencial }{@code >}
     *     
     */
    public void setPresentacionPresencial(JAXBElement<DatosDocumentoPresencial> value) {
        this.presentacionPresencial = value;
    }

}
