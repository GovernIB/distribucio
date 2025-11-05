/**
 * 
 */
package es.caib.distribucio.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que no es repeteixi una bústia amb el mateix nom per a una unitat organitzativa
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=EntitatCodiValidator.class)
public @interface EntitatCodi {

	String message() default "El codi de l'entitat no es pot canviar";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
