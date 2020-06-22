package es.caib.distribucio.backoffice.utils.sistra.pago;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java CONFIRMACION_PAGO pel tipus complex de la confirmació del pagament.
 * 
 * <pre>
 * &lt;complexType name="CONFIRMACION_PAGO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FECHA_PAGO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LOCALIZADOR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CONFIRMACION_PAGO", propOrder = {
    "fechaPago",
    "localizador"
})
public class ConfirmacionPago {

    @XmlElement(name = "FECHA_PAGO", required = true)
    protected String fechaPago;
    @XmlElement(name = "LOCALIZADOR", required = true)
    protected String localizador;

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String value) {
        this.fechaPago = value;
    }

    public String getLocalizador() {
        return localizador;
    }

    public void setLocalizador(String value) {
        this.localizador = value;
    }

}
