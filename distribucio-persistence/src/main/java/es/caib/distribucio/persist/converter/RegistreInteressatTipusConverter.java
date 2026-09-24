package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.registre.RegistreInteressatTipusEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RegistreInteressatTipusConverter implements AttributeConverter<RegistreInteressatTipusEnum, String> {

    @Override
    public String convertToDatabaseColumn(RegistreInteressatTipusEnum atribut) {
        // Si es null, guarda null. Si no, guarda el valor personalizado ("1", "2", etc.)
        return atribut != null ? atribut.getValor() : null;
    }

    @Override
    public RegistreInteressatTipusEnum convertToEntityAttribute(String valorBD) {
        return RegistreInteressatTipusEnum.valorAsEnum(valorBD);
    }
}