package es.caib.distribucio.logic.intf.resourcevalidation;

import es.caib.distribucio.logic.intf.model.EntitatResource;

import es.caib.distribucio.logic.intf.resourceservice.EntitatResourceService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Constraint de validació que controla que no es repeteixi el codiDir3 per una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class EntitatValidValidator implements ConstraintValidator<EntitatValid, EntitatResource> {

//    private final EntitatResourceRepository entitatResourceReporitory;
    private final EntitatResourceService entitatResourceService;

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }

    @Override
    public boolean isValid(EntitatResource resource, ConstraintValidatorContext context) {
        AtomicBoolean valid = new AtomicBoolean(true);

        entitatResourceService.findByCodi(resource.getCodi())
                .ifPresent((entity) -> {
                    if (!Objects.equals(resource.getId(), entity.getId())) {
                        this.addViolation(context, EntitatResource.Fields.codi,
                                "{javax.validation.constraints.Unique.message}");
                        valid.set(false);
                    }
                });

        entitatResourceService.findByCodiDir3(resource.getCodiDir3())
                .ifPresent((entity) -> {
                    if (!Objects.equals(resource.getId(), entity.getId())) {
                        this.addViolation(context, EntitatResource.Fields.codiDir3,
                                "{javax.validation.constraints.Unique.message}");
                        valid.set(false);
                    }
                });

        return valid.get();
    }
}
