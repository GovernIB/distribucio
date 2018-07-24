
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
    "numeroEntrada"
})
@XmlRootElement(name = "obtenerEntrada")
public class ObtenerEntrada {

    @XmlElement(required = true)
    protected ReferenciaEntrada numeroEntrada;

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

}
