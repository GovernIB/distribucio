
package es.caib.bantel.ws.v2.model.tramitebte;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TramiteSubsanacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TramiteSubsanacion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="expedienteCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expedienteUnidadAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TramiteSubsanacion", propOrder = {
    "expedienteCodigo",
    "expedienteUnidadAdministrativa"
})
public class TramiteSubsanacion {

    @XmlElement(required = true)
    protected String expedienteCodigo;
    protected long expedienteUnidadAdministrativa;

    /**
     * Obtiene el valor de la propiedad expedienteCodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedienteCodigo() {
        return expedienteCodigo;
    }

    /**
     * Define el valor de la propiedad expedienteCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedienteCodigo(String value) {
        this.expedienteCodigo = value;
    }

    /**
     * Obtiene el valor de la propiedad expedienteUnidadAdministrativa.
     * 
     */
    public long getExpedienteUnidadAdministrativa() {
        return expedienteUnidadAdministrativa;
    }

    /**
     * Define el valor de la propiedad expedienteUnidadAdministrativa.
     * 
     */
    public void setExpedienteUnidadAdministrativa(long value) {
        this.expedienteUnidadAdministrativa = value;
    }

}
