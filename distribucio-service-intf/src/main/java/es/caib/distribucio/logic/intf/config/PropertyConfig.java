/**
 * 
 */
package es.caib.distribucio.logic.intf.config;

/**
 * Configuració de les propietats de l'aplicació.
 * 
 * @author Limit Tecnologies
 */
public class PropertyConfig {

	private static final String PROPERTY_PREFIX = "es.caib.distribucio.";

	public static final String APP_NAME = PROPERTY_PREFIX + "app.name";
	public static final String APP_URL = PROPERTY_PREFIX + "app.url";

	public static final String MAIL_FROM = PROPERTY_PREFIX + "mail.from";
	public static final String FILES_PATH = PROPERTY_PREFIX + "files.path";
	public static final String DEFAULT_AUDITOR = PROPERTY_PREFIX + "default.auditor";

	public static final String PERSISTENCE_CONTAINER_TRANSACTIONS_DISABLED = PROPERTY_PREFIX + "persist.container-transactions-disabled";
	public static final String PERSISTENCE_TRANSACTION_MANAGER_ENABLED = PROPERTY_PREFIX + "persist.transaction-manager.enabled";

}
