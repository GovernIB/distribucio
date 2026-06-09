package es.caib.distribucio.logic.intf.util;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

/** Classe amb mètodes d'utilitats comunes.
 * 
 */
public class DatesUtils {


    public static Date toLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? Date.from(offsetDateTime.toInstant()) : null;
    }

    public static OffsetDateTime toOffsetDateTime(Date data) {
        if (data == null) {
            return null;
        }

        if (data instanceof java.sql.Date) {
            return ((java.sql.Date) data).toLocalDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toOffsetDateTime();
        }

        if (data instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) data).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime();
        }

        return data.toInstant()
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }
}
