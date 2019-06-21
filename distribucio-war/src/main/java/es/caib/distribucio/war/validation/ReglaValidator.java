/**
 * 
 */
package es.caib.distribucio.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.war.command.ReglaCommand;
import es.caib.distribucio.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglaValidator implements ConstraintValidator<Regla, ReglaCommand> {

	private String codiMissatge;

	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(final Regla anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(final ReglaCommand command, final ConstraintValidatorContext context) {
		boolean valid = true;
		
		// Comprova que el codi d'assumpte o el codi de procediement estiguin informats
		if ((command.getAssumpteCodi() == null || "".equals(command.getAssumpteCodi().trim()))
				&& (command.getProcedimentCodi() == null || "".equals(command.getProcedimentCodi().trim()))) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("assumpteCodi")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("procedimentCodi")
					.addConstraintViolation();	
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
        return valid;
	}
}
