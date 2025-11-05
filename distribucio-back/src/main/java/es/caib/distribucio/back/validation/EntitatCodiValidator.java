/**
 * 
 */
package es.caib.distribucio.back.validation;

import es.caib.distribucio.back.command.EntitatCommand;
import es.caib.distribucio.back.helper.MessageHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.EntitatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de backoffice i altres validacions per al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatCodiValidator implements ConstraintValidator<EntitatCodi, EntitatCommand> {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private EntitatService entitatService;

	@Override
	public void initialize(final EntitatCodi constraintAnnotation) {
	}

	@Override
	public boolean isValid(final EntitatCommand value, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (value.getId() != null) {
            EntitatDto entity = entitatService.findById(value.getId());
            if (entity != null && entity.getCodi() != null && value.getCodi() != null) {
                if (!Objects.equals(entity.getCodi(), value.getCodi())) {
                    valid = false;
                    context.buildConstraintViolationWithTemplate(
                                    MessageHelper.getInstance().getMessage("entitat.validator.canvi.codi", null, new RequestContext(request).getLocale()))
                            .addNode("codi")
                            .addConstraintViolation()
                            .disableDefaultConstraintViolation();
                }
            }
        }
        return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatCodiValidator.class);

}
