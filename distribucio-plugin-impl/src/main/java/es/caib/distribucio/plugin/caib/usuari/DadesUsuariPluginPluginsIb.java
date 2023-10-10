/**
 * 
 */
package es.caib.distribucio.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.IUserInformationPlugin;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.keycloak.KeyCloakUserInformationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginPluginsIb extends DistribucioAbstractPluginProperties implements DadesUsuariPlugin {

	private static final String PLUGIN_PROPS_BASE = "es.caib.distribucio.plugin.";
	private static final String PROPS_BASE = PLUGIN_PROPS_BASE + "dades.usuari.pluginsib.";

	private IUserInformationPlugin userInformationPlugin;
	
	public DadesUsuariPluginPluginsIb() {
		super();
	}
	
	public DadesUsuariPluginPluginsIb(Properties properties) {
		super(properties);
	}
	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			UserInfo userInfo = getUserInformationPlugin().getUserInfoByUserName(usuariCodi);
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
			String[] userNames = getUserInformationPlugin().getUsernamesByRol(grupCodi);
			List<DadesUsuari> resposta = new ArrayList<DadesUsuari>();
			for (String userName: userNames) {
				UserInfo userInfo = getUserInformationPlugin().getUserInfoByUserName(userName);
				resposta.add(toDadesUsuari(userInfo));
			}
			return resposta;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup " + grupCodi,
					ex);
		}
	}

	private static final String PLUGIN_PROPS_PREFIX = PLUGIN_PROPS_BASE + "pluginsib.userinformation.keycloak.";
	private IUserInformationPlugin getUserInformationPlugin() {
		if (userInformationPlugin == null) {
			Properties props = new Properties();
			props.put(PLUGIN_PROPS_PREFIX + "serverurl", getDadesUsuariPluginProperty("service.url"));
			props.put(PLUGIN_PROPS_PREFIX + "realm", getDadesUsuariPluginProperty("service.realm"));
			props.put(PLUGIN_PROPS_PREFIX + "client_id", getDadesUsuariPluginProperty("service.client_id"));
			props.put(PLUGIN_PROPS_PREFIX + "password_secret", getDadesUsuariPluginProperty("service.password_secret"));
			props.put(PLUGIN_PROPS_PREFIX + "mapping.administrationID", "nif");
			userInformationPlugin = new KeyCloakUserInformationPlugin(PLUGIN_PROPS_BASE, props);
		}
		return userInformationPlugin;
	}

	private String getDadesUsuariPluginProperty(String propertySuffix) {
		return getProperty(PROPS_BASE + propertySuffix);
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

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginPluginsIb.class);

}
