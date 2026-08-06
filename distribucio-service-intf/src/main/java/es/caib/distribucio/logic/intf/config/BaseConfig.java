/**
 * 
 */
package es.caib.distribucio.logic.intf.config;

/**
 * Propietats de configuració de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
public class BaseConfig {

	public static final String APP_NAME = "distribucio";
	public static final String DB_PREFIX = "dis_";

	public static final String BASE_PACKAGE = "es.caib." + APP_NAME;

	public static final String APP_PROPERTIES = BASE_PACKAGE + ".properties";
	public static final String APP_SYSTEM_PROPERTIES = BASE_PACKAGE + ".system.properties";

	public static final String ROLE_SUPER = "DIS_SUPER";
	public static final String ROLE_ADMIN = "DIS_ADMIN";
	public static final String ROLE_ADMIN_LECTURA = "DIS_ADMIN_LECTURA";
	public static final String ROLE_REGLA = "DIS_REGLA";
	public static final String ROLE_REPORT = "DIS_REPORT";
	public static final String ROLE_BUSTIA_WS = "DIS_BSTWS";
	public static final String ROLE_BACKOFFICE_WS = "DIS_BACKWS";
	public static final String ROLE_COMANDA = "DIS_COM";
	public static final String ROLE_USER = "tothom";

	public static final String API_PATH = "/api";
	public static final String PING_PATH = "/ping";
	public static final String SYSENV_PATH = "/sysenv";
	public static final String MANIFEST_PATH = "/manifest";
	public static final String AUTH_TOKEN_PATH = "/authToken";
	public static final String AUTH_ROLES_PATH = "/authRoles";
	public static final String REACT_APP_PATH = "/reactapp";

}
