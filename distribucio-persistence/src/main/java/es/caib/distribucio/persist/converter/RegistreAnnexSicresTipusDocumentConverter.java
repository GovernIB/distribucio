package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RegistreAnnexSicresTipusDocumentConverter implements AttributeConverter<RegistreAnnexSicresTipusDocumentEnum, String> {

    @Override
    public String convertToDatabaseColumn(RegistreAnnexSicresTipusDocumentEnum atribut) {
        return atribut != null ? atribut.getValor() : null;
    }

    @Override
    public RegistreAnnexSicresTipusDocumentEnum convertToEntityAttribute(String valorBD) {
        return RegistreAnnexSicresTipusDocumentEnum.valorAsEnum(valorBD);
    }
}