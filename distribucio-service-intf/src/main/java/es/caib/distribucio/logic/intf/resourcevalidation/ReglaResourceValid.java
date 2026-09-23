package es.caib.distribucio.logic.intf.resourcevalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació bàsica del recurs de regles: com a mínim un filtre informat (llevat de les
 * regles de tipus BACKOFFICE) i el destí obligatori segons el tipus de regla.
 * <p>
 * Equivalent simplificat de l'antic {@code es.caib.distribucio.back.validation.Regla}: no inclou les
 * comprovacions creuades contra altres regles (unicitat de codis SIA, unicitat nom+tipus+assumpte), que
 * queden pendents per quan es migri l'acció de guardar de debò.
 *
 * @author Límit Tecnologies
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReglaResourceValidValidator.class)
public @interface ReglaResourceValid {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
