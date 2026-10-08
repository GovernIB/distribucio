package es.caib.distribucio.logic.intf.model.auth;

/**
 * Interfície amb els detalls de l'autenticació.
 *
 * @author Límit Tecnologies
 */
public interface AuthenticationDetails {

	String getJwtToken();
	String getPreferredUsername();
	String getName();
	String getEmail();
	String getNif();
	String[] getOriginalRoles();

}
