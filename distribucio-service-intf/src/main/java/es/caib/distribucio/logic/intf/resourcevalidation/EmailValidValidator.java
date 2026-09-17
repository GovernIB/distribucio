package es.caib.distribucio.logic.intf.resourcevalidation;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * Constraint de validació que controla que no es repeteixi el nom de la bústia per una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class EmailValidValidator implements ConstraintValidator<EmailValid, String> {

    @Override
    public boolean isValid(String resource, ConstraintValidatorContext context) {
        boolean valid = true;

        Set<String> adrecesRevisades = new HashSet<>();
        Set<String> adrecesErronies = new HashSet<>();
        if (resource != null && !resource.isEmpty() ) {
            // substitueix els espais per comes
            String adreces = resource.replaceAll("\\s*,\\s*|\\s+", ",");
            for(String adr : adreces.split(",")) {
                if (!adrecesRevisades.contains(adr) && !adrecesErronies.contains(adr)) {
                    if (adr.matches("\\S+@\\S+[.\\S+]+")) {
                        adrecesRevisades.add(adr);
                    } else {
                        adrecesErronies.add(adr);
                    }
                }
            }
            if (!adrecesErronies.isEmpty()) {
                String emailsErronis = String.join(", ", adrecesErronies);
                context.disableDefaultConstraintViolation();

                context.unwrap(HibernateConstraintValidatorContext.class)
                        .addMessageParameter("0", emailsErronis)
                        .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
