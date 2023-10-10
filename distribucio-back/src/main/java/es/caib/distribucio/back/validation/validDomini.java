/**
 * 
 */
package es.caib.distribucio.back.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint de validació que controla que la cadena d'un domini sigui correcte.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=validDominiValidator.class)
public @interface validDomini {

	String message() default "La cadena que s'ha introduit no és vàlida. Revisa el xml.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
