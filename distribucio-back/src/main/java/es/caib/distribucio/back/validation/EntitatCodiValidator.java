/**
 * 
 */
package es.caib.distribucio.back.validation;

import es.caib.distribucio.back.command.EntitatCommand;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.EntitatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de backoffice i altres validacions per al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatCodiValidator implements ConstraintValidator<EntitatCodi, EntitatCommand> {

	@Autowired
	private EntitatService entitatService;

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }

	@Override
	public void initialize(final EntitatCodi constraintAnnotation) {
	}

	@Override
	public boolean isValid(final EntitatCommand value, final ConstraintValidatorContext context) {
        AtomicBoolean valid = new AtomicBoolean(true);

        if (value.getId() != null) {
            EntitatDto entity = entitatService.findById(value.getId());
            if (entity != null && entity.getCodi() != null && value.getCodi() != null) {
                if (!Objects.equals(entity.getCodi(), value.getCodi())) {
                    this.addViolation(context, "codi", "{entitat.validator.canvi.codi}");
                    valid.set(false);
                }
            }
        }

        Optional.ofNullable(entitatService.findByCodi(value.getCodi()))
                .ifPresent((entity) -> {
                    if (!Objects.equals(value.getId(), entity.getId())) {
                        this.addViolation(context, "codi", "{javax.validation.constraints.Unique.message}");
                        valid.set(false);
                    }
                });

        Optional.ofNullable(entitatService.findByCodiDir3(value.getCodiDir3()))
                .ifPresent((entity) -> {
                    if (!Objects.equals(value.getId(), entity.getId())) {
                        this.addViolation(context, "codiDir3", "{javax.validation.constraints.Unique.message}");
                        valid.set(false);
                    }
                });

        return valid.get();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatCodiValidator.class);

}
