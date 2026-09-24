package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.registre.RegistreAnnexNtiTipusDocumentEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RegistreAnnexNtiTipusDocumentConverter implements AttributeConverter<RegistreAnnexNtiTipusDocumentEnum, String> {

    @Override
    public String convertToDatabaseColumn(RegistreAnnexNtiTipusDocumentEnum atribut) {
        return atribut != null ? atribut.getValor() : null;
    }

    @Override
    public RegistreAnnexNtiTipusDocumentEnum convertToEntityAttribute(String valorBD) {
        return RegistreAnnexNtiTipusDocumentEnum.valorAsEnum(valorBD);
    }
}