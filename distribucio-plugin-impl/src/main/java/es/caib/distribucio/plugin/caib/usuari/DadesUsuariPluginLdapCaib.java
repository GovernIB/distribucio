/**
 * 
 */
package es.caib.distribucio.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.ldap.LdapUserInformationPlugin;
import org.fundaciobit.pluginsib.utils.ldap.LDAPUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de LDAP. Les propietats necessàries són les següents a partir
 * de es.caib.distribucio.pluginib.dades.usuari.pluginsib.userinformation.ldap. :
 * 
 * - serverurl: Url del servidor de keycloak
 * - realm: Realm del keycloak.7
 * - client_id: Client ID del keycloak.
 * - client_id_for_user_autentication: Client ID per autenticació del keycloak.
 * - password_secret: Secret del client de keycloak.
 * - mapping.administrationID: Mapeig del administrationID de keycloak.
 * - debug: Activar el debug del plugin de keycloak.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginLdapCaib extends LdapUserInformationPlugin implements DadesUsuariPlugin {

	public DadesUsuariPluginLdapCaib(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	public DadesUsuariPluginLdapCaib(String propertyKeyBase) {
		super(propertyKeyBase);
	}
	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari LDAP CAIB (usuariCodi=" + usuariCodi + ")");
		try {
			UserInfo userInfo = getUserInfoByUserName(usuariCodi);
			return toDadesUsuari(userInfo);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar l'usuari amb codi " + usuariCodi,
					ex);
		}
	}

	@Override
	public List<DadesUsuari> findAmbGrup(
			String grupCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels usuaris del grup LDAP CAIB (grupCodi=" + grupCodi + ")");
		try {
			List<DadesUsuari> dadesUsuaris = new ArrayList<>();
			List<LDAPUser> ldapUsers = this.getLDAPUserManager().getUsersByRol(grupCodi);
			if (ldapUsers != null) {
				for (LDAPUser ldapUser : ldapUsers) {
					// Mateixa transformació
					UserInfo info = new UserInfo();
			        info.setLanguage("ca");
			        info.setName(ldapUser.getName());
			        if (ldapUser.getSurname1() == null) {
			            info.setSurname1(ldapUser.getSurnames());
			        } else {
			            info.setSurname1(ldapUser.getSurname1());
			        }
			        info.setSurname2(ldapUser.getSurname2());

			        info.setAdministrationID(ldapUser.getAdministrationID());
			        info.setUsername(ldapUser.getUserName());
			        info.setEmail(ldapUser.getEmail());
			        info.setPhoneNumber(ldapUser.getTelephoneNumber());

			        dadesUsuaris.add(this.toDadesUsuari(info));
				}
			}
			return dadesUsuaris;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup LDAP CAIB " + grupCodi,
					ex);
		}
	}

	private DadesUsuari toDadesUsuari(UserInfo userInfo) {
		if (userInfo != null) {
			DadesUsuari dadesUsuari = new DadesUsuari();
			dadesUsuari.setCodi(userInfo.getUsername());
			dadesUsuari.setNomSencer(userInfo.getFullName());
			dadesUsuari.setNom(userInfo.getName());
			dadesUsuari.setLlinatges(userInfo.getSurname1() + (userInfo.getSurname2() != null ? " " + userInfo.getSurname2() : ""));
			dadesUsuari.setNif(userInfo.getAdministrationID());
			dadesUsuari.setEmail(userInfo.getEmail());
			dadesUsuari.setActiu(true);
			return dadesUsuari;
		} else {
			return null;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginLdapCaib.class);

}
