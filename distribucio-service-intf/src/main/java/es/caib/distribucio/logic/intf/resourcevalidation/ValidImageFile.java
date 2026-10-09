package es.caib.distribucio.logic.intf.resourcevalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageFile {
    String message() default "El fitxer ha de ser una imatge vàlida (jpg, jpeg, png, gif, bmp, webp)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
