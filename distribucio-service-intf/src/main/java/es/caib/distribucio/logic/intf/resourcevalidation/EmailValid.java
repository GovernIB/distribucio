/**
 * 
 */
package es.caib.distribucio.logic.intf.resourcevalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= EmailValidValidator.class)
public @interface EmailValid {

	String message() default "{bustia.controller.pendent.contingut.enviar.email.validacio.adreces}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
