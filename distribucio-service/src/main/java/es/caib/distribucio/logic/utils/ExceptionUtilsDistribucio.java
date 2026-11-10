package es.caib.distribucio.logic.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Classe amb mètodes comuns per tractar excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExceptionUtilsDistribucio {

	/** Comprova si l'excepció està causada per un tipus d'excepció.
	 * 
	 * @param exception Excepció a comprovar.
	 * @param exceptionClasses Classe per veure si extén o implementa.
	 * 
	 * @return True si l'excepció implementa, extén o està causada.
	 */
	public static boolean isCausedBy(Throwable exception, Class<? extends Throwable> exceptionClass) {
		return isCausedBy(exception, Arrays.asList(exceptionClass));
	}

	/** Comprova si l'excepció està causada per un tipus d'excepció.
	 * 
	 * @param exception Excepció a comprovar.
	 * @param exceptionClasses Classe per veure si extén o implementa.
	 * 
	 * @return True si l'excepció implementa, extén o està causada.
	 */
	public static boolean isCausedBy(Throwable exception, List<Class<? extends Throwable>> exceptionClasses) {
		Throwable current = exception;
		while (current != null) {
			for (Class<? extends Throwable> exceptionClass : exceptionClasses) {
				if (exceptionClass.isAssignableFrom(current.getClass())) {
					return true;
				}
			}
			current = current.getCause();
		}
		return false;
	}

}
