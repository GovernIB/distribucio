package es.caib.distribucio.backoffice.utils.sistra.pago;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pel tipus complex CONTRIBUYENTE amb les dades del contribuent.
 * 
 * <pre>
 * &lt;complexType name="CONTRIBUYENTE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NIF" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NOMBRE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CONTRIBUYENTE", propOrder = {
    "nif",
    "nombre"
})
public class Contribuyente {

    @XmlElement(name = "NIF", required = true)
    protected String nif;
    @XmlElement(name = "NOMBRE", required = true)
    protected String nombre;

    public String getNif() {
        return nif;
    }

    public void setNif(String value) {
        this.nif = value;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        this.nombre = value;
    }

}
