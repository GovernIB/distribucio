/**
 * 
 */
package es.caib.distribucio.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.BackofficeService;
import es.caib.distribucio.war.command.BackofficeCommand;
import es.caib.distribucio.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de backoffice i altres validacions per al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeValidator implements ConstraintValidator<Backoffice, Object> {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private BackofficeService backofficeService;
	@Autowired
	private AplicacioService aplicacioService;	

	@Override
	public void initialize(final Backoffice constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean valid = true;
		BackofficeCommand command = (BackofficeCommand) value;
		try {


			BackofficeDto existent = backofficeService.findByCodi(command.getEntitatId(), command.getCodi());
			if (existent != null && (command.getId() == null || !command.getId().equals(existent.getId()))) {
				valid = false;
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("backoffice.validator.ja.existeix.codi", null, new RequestContext(request).getLocale()))
				.addNode("codi")
				.addConstraintViolation();	
			} 

		} catch (final Exception ex) {
			LOGGER.error("Error al validar si el codi de backoffice és únic", ex);
		}
		
		if (command.getUsuari() != null && !command.getUsuari().isEmpty()) {
			String usuari = aplicacioService.propertyFindByNom(command.getUsuari());
			if (usuari == null) {
				valid = false;
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("backoffice.validator.no.existeix.property", null, new RequestContext(request).getLocale()))
				.addNode("usuari")
				.addConstraintViolation();	
			}
		}

		if (command.getContrasenya() != null && !command.getContrasenya().isEmpty()) {
			String contrasenya = aplicacioService.propertyFindByNom(command.getContrasenya());
			if (contrasenya == null) {
				valid = false;
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("backoffice.validator.no.existeix.property", null, new RequestContext(request).getLocale()))
				.addNode("contrasenya")
				.addConstraintViolation();	
			}
		}

			
		if (!valid)
			context.disableDefaultConstraintViolation();
        return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(BackofficeValidator.class);

}
