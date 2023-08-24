package es.caib.distribucio.backoffice.utils.sistra.formulario;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _FORMULARIO_QNAME = new QName("urn:es:caib:sistra2:xml:formulario:v1:model", "FORMULARIO");

    public ObjectFactory() {
    }

    public Formulario createFormulario() {
        return new Formulario();
    }

    public Elemento createElemento() {
        return new Elemento();
    }

    public Valor createValor() {
        return new Valor();
    }

    public Campo createCampo() {
        return new Campo();
    }

    @XmlElementDecl(namespace = "urn:es:caib:sistra2:xml:formulario:v1:model", name = "FORMULARIO")
    public JAXBElement<Formulario> createFormulario(Formulario value) {
        return new JAXBElement<Formulario>(_FORMULARIO_QNAME, Formulario.class, null, value);
    }

}
