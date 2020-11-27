/**
 * 
 */
package es.caib.distribucio.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
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
		
		// Comprova que almenys un camp del firtre esta informat
		if ((command.getAssumpteCodiFiltre() == null || command.getAssumpteCodiFiltre().trim().isEmpty()) && 
			(command.getProcedimentCodiFiltre() == null || command.getProcedimentCodiFiltre().trim().isEmpty()) &&
			 command.getUnitatFiltreId() == null &&
			 command.getBustiaFiltreId() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("assumpteCodiFiltre")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("procedimentCodiFiltre")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("unitatFiltreId")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("bustiaFiltreId")
					.addConstraintViolation();	
			valid = false;
		}
		

		if (command.getTipus() == ReglaTipusEnumDto.UNITAT && command.getUnitatDestiId() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
					.addNode("unitatDestiId")
					.addConstraintViolation();	
			valid = false;
		} else if (command.getTipus() == ReglaTipusEnumDto.BUSTIA && command.getBustiaDestiId() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
					.addNode("bustiaDestiId")
					.addConstraintViolation();	
			valid = false;
		} else if (command.getTipus() == ReglaTipusEnumDto.BACKOFFICE && command.getBackofficeDestiId() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
					.addNode("backofficeDestiId")
					.addConstraintViolation();	
			valid = false;
		}
		
		
		

		if (!valid)
			context.disableDefaultConstraintViolation();
		
        return valid;
	}
}