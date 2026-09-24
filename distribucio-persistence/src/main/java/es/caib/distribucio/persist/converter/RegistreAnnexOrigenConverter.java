package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.registre.RegistreAnnexOrigenEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RegistreAnnexOrigenConverter implements AttributeConverter<RegistreAnnexOrigenEnum, String> {

    @Override
    public String convertToDatabaseColumn(RegistreAnnexOrigenEnum atribut) {
        return atribut != null ? atribut.getValor() : null;
    }

    @Override
    public RegistreAnnexOrigenEnum convertToEntityAttribute(String valorBD) {
        return RegistreAnnexOrigenEnum.valorAsEnum(valorBD);
    }
}