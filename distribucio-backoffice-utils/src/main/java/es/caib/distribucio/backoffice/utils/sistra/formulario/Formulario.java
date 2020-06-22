package es.caib.distribucio.backoffice.utils.sistra.formulario;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pel tipus complex FORMULARIO del document tècnic de formularis.
 * 
 * <p>Aquesta classe es corrrespon al següent fragment d'esquema XML:
 * 
 * <pre>
 * &lt;complexType name="FORMULARIO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CAMPO" type="{urn:es:caib:sistra2:xml:formulario:v1:model}CAMPO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="accion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "FORMULARIO")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FORMULARIO", propOrder = {
    "campos"
})
public class Formulario {

    @XmlElement(name = "CAMPO")
    protected List<Campo> campos;
    @XmlAttribute(name = "accion")
    protected String accion;

    public List<Campo> getCampos() {
        if (campos == null) {
            campos = new ArrayList<Campo>();
        }
        return this.campos;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String value) {
        this.accion = value;
    }
}
