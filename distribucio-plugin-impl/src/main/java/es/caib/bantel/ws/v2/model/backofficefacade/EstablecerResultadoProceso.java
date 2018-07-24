
package es.caib.bantel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciaEntrada;


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
 *         &lt;element name="numeroEntrada" type="{urn:es:caib:bantel:ws:v2:model:ReferenciaEntrada}ReferenciaEntrada"/>
 *         &lt;element name="resultado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resultadoProcesamiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "numeroEntrada",
    "resultado",
    "resultadoProcesamiento"
})
@XmlRootElement(name = "establecerResultadoProceso")
public class EstablecerResultadoProceso {

    @XmlElement(required = true)
    protected ReferenciaEntrada numeroEntrada;
    @XmlElement(required = true)
    protected String resultado;
    @XmlElement(required = true)
    protected String resultadoProcesamiento;

    /**
     * Obtiene el valor de la propiedad numeroEntrada.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciaEntrada }
     *     
     */
    public ReferenciaEntrada getNumeroEntrada() {
        return numeroEntrada;
    }

    /**
     * Define el valor de la propiedad numeroEntrada.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciaEntrada }
     *     
     */
    public void setNumeroEntrada(ReferenciaEntrada value) {
        this.numeroEntrada = value;
    }

    /**
     * Obtiene el valor de la propiedad resultado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Define el valor de la propiedad resultado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultado(String value) {
        this.resultado = value;
    }

    /**
     * Obtiene el valor de la propiedad resultadoProcesamiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultadoProcesamiento() {
        return resultadoProcesamiento;
    }

    /**
     * Define el valor de la propiedad resultadoProcesamiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultadoProcesamiento(String value) {
        this.resultadoProcesamiento = value;
    }

}
