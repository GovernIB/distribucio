package es.caib.distribucio.backoffice.utils.sistra.pago;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pel tipus complex DATOS_PAGO amb les dades del pagament..
 * 
 * <pre>
 * &lt;complexType name="DATOS_PAGO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ENTIDAD_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PASARELA_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ORGANISMO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IDIOMA" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SIMULADO" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PRESENTACION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CONTRIBUYENTE" type="{urn:es:caib:sistra2:xml:pago:v1:model}CONTRIBUYENTE"/>
 *         &lt;element name="MODELO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CONCEPTO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TASA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IMPORTE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DETALLE_PAGO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATOS_PAGO", propOrder = {
    "entidadId",
    "pasarelaId",
    "organismo",
    "idioma",
    "simulado",
    "presentacion",
    "contribuyente",
    "modelo",
    "concepto",
    "tasa",
    "importe",
    "detallePago"
})
public class DatosPago {

    @XmlElement(name = "ENTIDAD_ID", required = true)
    protected String entidadId;
    @XmlElement(name = "PASARELA_ID", required = true)
    protected String pasarelaId;
    @XmlElement(name = "ORGANISMO", required = false)
    protected String organismo;
    @XmlElement(name = "IDIOMA", required = true)
    protected String idioma;
    @XmlElement(name = "SIMULADO")
    protected boolean simulado;
    @XmlElement(name = "PRESENTACION", required = true)
    protected String presentacion;
    @XmlElement(name = "CONTRIBUYENTE", required = true)
    protected Contribuyente contribuyente;
    @XmlElement(name = "MODELO", required = true)
    protected String modelo;
    @XmlElement(name = "CONCEPTO", required = true)
    protected String concepto;
    @XmlElement(name = "TASA", required = false)
    protected String tasa;
    @XmlElement(name = "IMPORTE")
    protected int importe;
    @XmlElement(name = "DETALLE_PAGO", required = false)
    protected String detallePago;

    public String getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(String value) {
        this.entidadId = value;
    }

    public String getPasarelaId() {
        return pasarelaId;
    }

    public void setPasarelaid(String value) {
        this.pasarelaId = value;
    }

    public String getOrganismo() {
        return organismo;
    }

    public void setOrganismo(String value) {
        this.organismo = value;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String value) {
        this.idioma = value;
    }

    public boolean isSimulado() {
        return simulado;
    }

    public void setSimulado(boolean value) {
        this.simulado = value;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String value) {
        this.presentacion = value;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

   public void setContribuyente(Contribuyente value) {
        this.contribuyente = value;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String value) {
        this.modelo = value;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String value) {
        this.concepto = value;
    }

    public String getTasa() {
        return tasa;
    }

    public void setTasa(String value) {
        this.tasa = value;
    }

    public int getImporte() {
        return importe;
    }

    public void setImporte(int value) {
        this.importe = value;
    }

    public String getDetallePago() {
        return detallePago;
    }

    public void setDetallePago(String value) {
        this.detallePago = value;
    }

}
