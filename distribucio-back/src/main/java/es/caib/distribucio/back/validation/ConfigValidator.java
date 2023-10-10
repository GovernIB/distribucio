/**
 * 
 */
package es.caib.distribucio.back.validation;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.back.command.ConfigCommand;
import es.caib.distribucio.back.helper.MessageHelper;
import es.caib.distribucio.logic.intf.dto.ConfigDto;
import es.caib.distribucio.logic.intf.service.ConfigService;

/**
 * Constraint de validació a l'hora de guardar la configuració que controla 
 * que el valor sigui correcte per a la propietat que es vol modificar.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConfigValidator implements ConstraintValidator<Config, Object> {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private ConfigService configService;

	@Override
	public void initialize(final Config constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean valid = true;
		ConfigCommand command = (ConfigCommand) value;
		try {
			
			// Recupera la propietat 
			ConfigDto configDto = configService.findByKey(command.getKey());
			if (configDto != null) {
				if ("CRON".equals(configDto.getTypeCode())) {
					try {
						if (command.getValue() != null) {
							CronSequenceGenerator cronGen = new CronSequenceGenerator(command.getValue());
							cronGen.next(new Date());
						}
					} catch(Exception e) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("config.validator.error.cron.no.valid", null, new RequestContext(request).getLocale()))
						.addNode("value")
						.addConstraintViolation();							
					}
				}
			} else {
				valid = false;
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("config.validator.error.clau.no.trobada", null, new RequestContext(request).getLocale()))
				.addNode("codi")
				.addConstraintViolation();	
			}

		} catch (final Exception ex) {
			LOGGER.error("Error validant la comanda de configuracó: " + ex.getMessage(), ex);
		}
					
		if (!valid)
			context.disableDefaultConstraintViolation();
        return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValidator.class);

}
