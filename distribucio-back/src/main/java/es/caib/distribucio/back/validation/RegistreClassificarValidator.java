/**
 * 
 */
package es.caib.distribucio.back.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.distribucio.back.command.RegistreClassificarCommand;
import es.caib.distribucio.back.helper.MessageHelper;

/**
 * Validador de la comanda de classificar un registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreClassificarValidator implements ConstraintValidator<RegistreClassificar, RegistreClassificarCommand>{

	private String codiMissatge;

	@Override
	public void initialize(RegistreClassificar registreClassificar) {
		codiMissatge = registreClassificar.message();
	}

	@Override
	public boolean isValid(RegistreClassificarCommand command, ConstraintValidatorContext context) {

		boolean valid = true;

		// Valida que només s'informi el procediment o serveis, però no tots dos
		if (command.getCodiProcediment() != null && command.getCodiServei() != null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(
							this.codiMissatge + ".codis.servei.procediment"))
			.addNode("codiProcediment")
			.addConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(
							this.codiMissatge + ".codis.servei.procediment"))
			.addNode("codiServei")
			.addConstraintViolation();
			valid = false;	
		}
		
		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}
}
