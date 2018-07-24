
package es.caib.bantel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.bantel.ws.v2.model.tramitebte.TramiteBTE;


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
 *         &lt;element name="obtenerEntradaReturn" type="{urn:es:caib:bantel:ws:v2:model:TramiteBTE}TramiteBTE"/>
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
    "obtenerEntradaReturn"
})
@XmlRootElement(name = "obtenerEntradaResponse")
public class ObtenerEntradaResponse {

    @XmlElement(required = true)
    protected TramiteBTE obtenerEntradaReturn;

    /**
     * Obtiene el valor de la propiedad obtenerEntradaReturn.
     * 
     * @return
     *     possible object is
     *     {@link TramiteBTE }
     *     
     */
    public TramiteBTE getObtenerEntradaReturn() {
        return obtenerEntradaReturn;
    }

    /**
     * Define el valor de la propiedad obtenerEntradaReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link TramiteBTE }
     *     
     */
    public void setObtenerEntradaReturn(TramiteBTE value) {
        this.obtenerEntradaReturn = value;
    }

}
