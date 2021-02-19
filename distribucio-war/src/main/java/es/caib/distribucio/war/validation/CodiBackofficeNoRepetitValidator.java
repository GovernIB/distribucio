/**
 * 
 */
package es.caib.distribucio.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.service.BackofficeService;
import es.caib.distribucio.war.command.BackofficeCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiBackofficeNoRepetitValidator implements ConstraintValidator<CodiBackofficeNoRepetit, Object> {

	@Autowired
	private BackofficeService backofficeService;

	@Override
	public void initialize(final CodiBackofficeNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			BackofficeCommand command = (BackofficeCommand) value;
			
			BackofficeDto entitat = backofficeService.findByCodi(command.getEntitatId(), command.getCodi());
			if (entitat != null) {
				return false;
			} else {
				return true;
			}
		} catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi de backoffice és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiBackofficeNoRepetitValidator.class);

}
