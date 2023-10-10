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
 * Constraint de validació per les regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ReglaValidator.class)
public @interface Regla {

	String message() default "regla.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
