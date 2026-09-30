package es.caib.distribucio.persist.converter;

import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MetaDataValueConverter {

    public static String formatToEntity(MetaDadaTipusEnumDto tipus, Object value) {
        switch (tipus) {
            case DATA:
                return MetaDataValueConverter.formatDate( value );
            default:
                return value.toString();
        }
    }

    public static Object formatToResource(MetaDadaTipusEnumDto tipus, String value) {
        switch (tipus) {
            case DATA:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(value, formatter);
            case BOOLEA:
                return Boolean.valueOf(value);
            default:
                return value;
        }
    }

    private static String formatDate(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }

        String str = value.toString().trim();

        if (str.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            try {
                LocalDate date = LocalDate.parse(str.substring(0, 10));
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                return str;
            }
        }

        return str;
    }
}
