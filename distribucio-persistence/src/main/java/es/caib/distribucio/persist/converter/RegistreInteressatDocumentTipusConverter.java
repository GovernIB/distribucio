package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatTipusEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RegistreInteressatDocumentTipusConverter implements AttributeConverter<RegistreInteressatDocumentTipusEnum, String> {

    @Override
    public String convertToDatabaseColumn(RegistreInteressatDocumentTipusEnum atribut) {
        return atribut != null ? atribut.getValor() : null;
    }

    @Override
    public RegistreInteressatDocumentTipusEnum convertToEntityAttribute(String valorBD) {
        return RegistreInteressatDocumentTipusEnum.valorAsEnum(valorBD);
    }
}