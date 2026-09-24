package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.registre.RegistreInteressatCanalEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RegistreInteressatCanalConverter implements AttributeConverter<RegistreInteressatCanalEnum, String> {

    @Override
    public String convertToDatabaseColumn(RegistreInteressatCanalEnum atribut) {
        return atribut != null ? atribut.getValor() : null;
    }

    @Override
    public RegistreInteressatCanalEnum convertToEntityAttribute(String valorBD) {
        return RegistreInteressatCanalEnum.valorAsEnum(valorBD);
    }
}