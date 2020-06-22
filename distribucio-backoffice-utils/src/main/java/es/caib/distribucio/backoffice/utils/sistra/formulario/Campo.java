package es.caib.distribucio.backoffice.utils.sistra.formulario;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pel tipus complex CAMPO corresponent al camp d'un formulari.
 * 
 * <pre>
 * &lt;complexType name="CAMPO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VALOR" type="{urn:es:caib:sistra2:xml:formulario:v1:model}VALOR" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tipo" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="simple"/>
 *             &lt;enumeration value="compuesto"/>
 *             &lt;enumeration value="multivaluado"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAMPO", propOrder = {
    "valores"
})
public class Campo {

    @XmlElement(name = "VALOR", nillable = true)
    protected List<Valor> valores;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "tipo", required = true)
    protected String tipo;

    public List<Valor> getValores() {
        if (valores == null) {
            valores = new ArrayList<Valor>();
        }
        return this.valores;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String value) {
        this.tipo = value;
    }
}
