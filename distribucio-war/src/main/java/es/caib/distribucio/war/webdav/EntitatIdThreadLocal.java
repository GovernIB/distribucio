/**
 * 
 */
package es.caib.distribucio.war.webdav;

/**
 * Emmagatzema l'entitat actual a dins una variable de tipus
 * ThreadLocal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatIdThreadLocal {

	public static final ThreadLocal<Long> entitatIdThreadLocal = new ThreadLocal<Long>();

	public static void set(Long entitatId) {
		entitatIdThreadLocal.set(entitatId);
	}

	public static void unset() {
		entitatIdThreadLocal.remove();
	}

	public static Long get() {
		return entitatIdThreadLocal.get();
	}

}
