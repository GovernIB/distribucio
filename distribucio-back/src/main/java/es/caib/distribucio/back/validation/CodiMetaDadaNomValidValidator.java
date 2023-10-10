/**
 * 
 */
package es.caib.distribucio.back.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Constraint de validació que controla que el nom del codi de meta-dada es valid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiMetaDadaNomValidValidator implements ConstraintValidator<CodiMetaDadaNomValid, String> {

	@Override
	public void initialize(CodiMetaDadaNomValid constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(
			String value,
			ConstraintValidatorContext context) {
		if (value == null || value.isEmpty())
			return true;
		return checkIfNameIsValidPartJava(value);
	}
	
	
	/**
	 * Checks if the @param nameToCheck consists of characters that may be part of a Java identifier 
	 * 
	 * @param nameToCheck
	 * @return
	 */
	private static boolean checkIfNameIsValidPartJava(String nameToCheck) {
		
		boolean nameValid = true;
		for (int i = 0; i < nameToCheck.length(); i++) {
			int codePoint = nameToCheck.codePointAt(i);
			
			if (i == 0 && Character.isUpperCase(codePoint)) {
				nameValid = false;
				break;
			}
			
			if (!Character.isJavaIdentifierPart(codePoint)) {
				nameValid = false;
				break;
			}
		}
		return nameValid;
	}






}
