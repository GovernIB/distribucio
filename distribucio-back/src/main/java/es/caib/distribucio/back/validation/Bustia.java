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
 * Constraint de validació que controla que no es repeteixi una bústia amb el mateix nom per a una unitat organitzativa
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=BustiaValidator.class)
public @interface Bustia {

	String message() default "Ja existeix una bústia amb aquest nom a la unitat orgànica seleccionada";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
