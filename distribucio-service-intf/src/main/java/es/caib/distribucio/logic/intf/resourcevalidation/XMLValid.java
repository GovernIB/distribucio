package es.caib.distribucio.logic.intf.resourcevalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que la cadena d'un domini sigui correcte.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = XMLValidValidator.class)
public @interface XMLValid {

    String message() default "{domini.form.camp.cadena-validacio}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
