package es.caib.distribucio.logic.intf.resourcevalidation;

import es.caib.distribucio.logic.intf.model.BackofficeResource;

import es.caib.distribucio.logic.intf.resourceservice.BackofficeResourceService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class BackofficeValidValidator implements ConstraintValidator<BackofficeValid, BackofficeResource> {

    private final BackofficeResourceService backofficeResourceService;

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }

    @Override
    public boolean isValid(BackofficeResource resource, ConstraintValidatorContext context) {
        AtomicBoolean valid = new AtomicBoolean(true);

        backofficeResourceService.findByCodi(resource.getCodi())
                .ifPresent((backoffice) -> {
                    if (!Objects.equals(resource.getId(), backoffice.getId())) {
                        this.addViolation(context, BackofficeResource.Fields.codi,
                                "{javax.validation.constraints.Unique.message}");
                        valid.set(false);
                    }
                });

        return valid.get();
    }
}
