package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.registre.RegistreAnnexElaboracioEstatEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RegistreAnnexElaboracioEstatConverter implements AttributeConverter<RegistreAnnexElaboracioEstatEnum, String> {

    @Override
    public String convertToDatabaseColumn(RegistreAnnexElaboracioEstatEnum atribut) {
        return atribut != null ? atribut.getValor() : null;
    }

    @Override
    public RegistreAnnexElaboracioEstatEnum convertToEntityAttribute(String valorBD) {
        return RegistreAnnexElaboracioEstatEnum.valorAsEnum(valorBD);
    }
}