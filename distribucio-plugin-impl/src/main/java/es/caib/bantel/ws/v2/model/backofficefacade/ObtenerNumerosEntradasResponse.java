
package es.caib.bantel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.bantel.ws.v2.model.referenciaentrada.ReferenciasEntrada;


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
 *         &lt;element name="obtenerNumerosEntradasReturn" type="{urn:es:caib:bantel:ws:v2:model:ReferenciaEntrada}ReferenciasEntrada"/>
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
    "obtenerNumerosEntradasReturn"
})
@XmlRootElement(name = "obtenerNumerosEntradasResponse")
public class ObtenerNumerosEntradasResponse {

    @XmlElement(required = true)
    protected ReferenciasEntrada obtenerNumerosEntradasReturn;

    /**
     * Obtiene el valor de la propiedad obtenerNumerosEntradasReturn.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciasEntrada }
     *     
     */
    public ReferenciasEntrada getObtenerNumerosEntradasReturn() {
        return obtenerNumerosEntradasReturn;
    }

    /**
     * Define el valor de la propiedad obtenerNumerosEntradasReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciasEntrada }
     *     
     */
    public void setObtenerNumerosEntradasReturn(ReferenciasEntrada value) {
        this.obtenerNumerosEntradasReturn = value;
    }

}
