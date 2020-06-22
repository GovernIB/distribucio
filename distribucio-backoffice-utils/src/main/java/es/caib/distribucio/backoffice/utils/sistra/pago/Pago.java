package es.caib.distribucio.backoffice.utils.sistra.pago;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pel tipus complex PAGO del document tècnic de pagaments.
 * 
 * <p>Aquesta classe es corrrespon al següent fragment d'esquema XML:
 * 
 * <pre>
 * &lt;complexType name="PAGO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDENTIFICADOR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATOS_PAGO" type="{urn:es:caib:sistra2:xml:pago:v1:model}DATOS_PAGO"/>
 *         &lt;element name="CONFIRMACION_PAGO" type="{urn:es:caib:sistra2:xml:pago:v1:model}CONFIRMACION_PAGO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "PAGO")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PAGO", propOrder = {
    "identificador",
    "datosPago",
    "confirmacionPago"
})
public class Pago {

    @XmlElement(name = "IDENTIFICADOR", required = true)
    protected String identificador;
    @XmlElement(name = "DATOS_PAGO", required = true)
    protected DatosPago datosPago;
    @XmlElement(name = "CONFIRMACION_PAGO", required = false)
    protected ConfirmacionPago confirmacionPago;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String value) {
        this.identificador = value;
    }

    public DatosPago getDatosPago() {
        return datosPago;
    }

    public void setDatosPago(DatosPago value) {
        this.datosPago = value;
    }

    public ConfirmacionPago getConfirmacionPago() {
        return confirmacionPago;
    }

    public void setConfirmacionPago(ConfirmacionPago value) {
        this.confirmacionPago = value;
    }

}
