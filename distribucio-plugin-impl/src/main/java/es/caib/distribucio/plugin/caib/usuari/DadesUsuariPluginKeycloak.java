/**
 * 
 */
package es.caib.distribucio.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant el plugin de Keycloak. Les propietats necessàries són les següents a partir
 * de es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak. :
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
public class DadesUsuariPluginKeycloak extends KeyCloakUserInformationPlugin implements DadesUsuariPlugin {

	public DadesUsuariPluginKeycloak(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	public DadesUsuariPluginKeycloak(String propertyKeyBase) {
		super(propertyKeyBase);
	}
	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
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
		LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			var usuariCodis = getUsernamesByRol(grupCodi);
//			var usuariCodis = getUsuarisByRol(grupCodi);
			if (usuariCodis == null || usuariCodis.length == 0) {
				return new ArrayList<>();
			}
			return Arrays.stream(usuariCodis).map(u -> DadesUsuari.builder().codi(u).build()).collect(Collectors.toList());
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup " + grupCodi,
					ex);
		}
	}
	
	
	@Override
	public String[] getUsernamesByRol(String rol) throws Exception {

		Set<String> usernamesClientApp = null;
		Set<String> usernamesClientPersons = null;
		Set<String> usersRealm = null;
		try {
			String appClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id");
			usernamesClientApp = this.getUsernamesByRolOfClient(rol, appClient);
		} catch (Exception ex) {
			log.error("No s'han obtingut usuaris per client d'aplicació", ex);
		}
		try {
			String personsClient = this.getPropertyRequired("pluginsib.userinformation.keycloak.client_id_for_user_autentication");
			usernamesClientPersons = this.getUsernamesByRolOfClient(rol, personsClient);
		} catch (Exception ex) {
			log.error("No s'han obtingut usuaris per client de persones", ex);
		}
		try {
			usersRealm = this.getUsernamesByRolOfRealm(rol);
		} catch (Exception ex) {
			log.error("No s'han obtingut usuaris per realm", ex);
		}
		if (usernamesClientApp == null && usernamesClientPersons == null && usersRealm == null) {
			return null;
		}
		Set<String> users = new TreeSet();
		if (usernamesClientApp != null) {
			users.addAll(usernamesClientApp);
		}

		if (usernamesClientPersons != null) {
			users.addAll(usernamesClientPersons);
		}

		if (usersRealm != null) {
			users.addAll(usersRealm);
		}

		return users.toArray(new String[users.size()]);
	}

	private Set<String> getUsernamesByRolOfRealm(String rol) throws Exception {

		RolesResource roleres = this.getKeyCloakConnectionForRoles();
		try {
			Set<UserRepresentation> userRep = roleres.get(rol).getRoleUserMembers();
			Set<String> users = new HashSet();
			Iterator var5 = userRep.iterator();

			while(var5.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var5.next();
				users.add(ur.getUsername());
			}

			return users;
		} catch (NotFoundException var7) {
			return null;
		}
	}

	private Set<String> getUsernamesByRolOfClient(String rol, String client) throws Exception {

		Keycloak keycloak = this.getKeyCloakConnection();
		ClientsResource clientsApi = keycloak.realm(this.getPropertyRequired("pluginsib.userinformation.keycloak.realm")).clients();
		List<ClientRepresentation> crList = clientsApi.findByClientId(client);
		if (crList == null || crList.isEmpty()) {
			return null;
		}
		ClientResource c = clientsApi.get((crList.get(0)).getId());
		RolesResource rrs = c.roles();

		try {
			Set<String> users = new HashSet();
			RoleResource rr = rrs.get(rol);
			Set<UserRepresentation> userRep = rr.getRoleUserMembers();
			Iterator var11 = userRep.iterator();

			while(var11.hasNext()) {
				UserRepresentation ur = (UserRepresentation)var11.next();
				users.add(ur.getUsername());
			}

			return users;
		} catch (NotFoundException var13) {
			return null;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginKeycloak.class);

}
