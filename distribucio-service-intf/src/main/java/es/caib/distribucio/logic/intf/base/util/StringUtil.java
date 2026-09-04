package es.caib.distribucio.logic.intf.base.util;

/**
 * Utilitats per strings.
 * 
 * @author Límit Tecnologies
 */
public class StringUtil {

	public static String capitalize(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String decapitalize(String str) {
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

	public static String removeLeadingAndTrailingChars(String str, Integer numChars) {
		if (str.length() > 2 * numChars) {
			return str.substring(numChars, str.length() - numChars);
		} else {
			return "";
		}
	}

	/**
	 * <p>Comprova si un string té un valor valid.</p>
	 *
	 * <pre>
	 * Utils.hasValue(null)      = false
	 * Utils.hasValue("")        = false
	 * Utils.hasValue(" ")       = false
	 * Utils.hasValue("bob")     = true
	 * Utils.hasValue("  bob  ") = true
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null
	 */
	public static boolean hasValue(String str) {
		if (str==null || "".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}

}
