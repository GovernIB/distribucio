package es.caib.distribucio.logic.intf.resourcevalidation;

import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;

import es.caib.distribucio.logic.intf.model.RegistreResource;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi el nom de la bústia per una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class RegistreClassificarValidValidator implements ConstraintValidator<RegistreClassificarValid, RegistreResource.ClassificarForm> {

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
    private void notNullViolation(ConstraintValidatorContext context, String field) {
        addViolation(context, field, "{javax.validation.constraints.NotNull.message}");
    }

    @Override
    public boolean isValid(RegistreResource.ClassificarForm resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (RegistreClassificarTipusEnum.PROCEDIMENT.equals( resource.getTipus() )
                && resource.getProcediment() == null) {
            this.notNullViolation(context, RegistreResource.ClassificarForm.Fields.procediment);
            valid = false;
        }

        if (RegistreClassificarTipusEnum.SERVEI.equals( resource.getTipus() )
                && resource.getServei() == null) {
            this.notNullViolation(context, RegistreResource.ClassificarForm.Fields.procediment);
            valid = false;
        }

        return valid;
    }
}
