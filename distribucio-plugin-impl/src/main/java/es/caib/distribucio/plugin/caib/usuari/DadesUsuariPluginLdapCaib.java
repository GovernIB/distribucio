/**
 * 
 */
package es.caib.distribucio.plugin.caib.usuari;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.ldap.LdapUserInformationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;
import io.micrometer.core.instrument.MeterRegistry;

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
			long start = System.currentTimeMillis();
			UserInfo userInfo = getUserInfoByUserName(usuariCodi);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			return toDadesUsuari(userInfo);
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
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
			long start = System.currentTimeMillis();
			List<DadesUsuari> dadesUsuaris = new ArrayList<>();
			UserInfo[] usersInfo = this.getUserInfoByRol(grupCodi);
			if (usersInfo != null) {
				for (int i = 0; i < usersInfo.length; i++) {
					dadesUsuaris.add(toDadesUsuari(usersInfo[i]));
				}
			}
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			return dadesUsuaris;
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup LDAP CAIB " + grupCodi,
					ex);
		}
	}

	@Override
	public List<String> findRolsPerUsuari(String usuariCodi) throws SistemaExternException {
		try {
			var info = getRolesByUsername(usuariCodi);
			return info != null && info.getRoles() != null ? eliminarDuplicados(List.of(info.getRoles())) : new ArrayList<String>();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}
	
	private List<String> eliminarDuplicados(List<String> lista) {
        return new ArrayList<>(new LinkedHashSet<>(lista)); // Set elimina duplicados y mantiene el orden
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

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }
    
    @Override
	public boolean teConfiguracioEspecifica() {
		return salutPluginComponent.teConfiguracioEspecifica();
	}

	@Override
	public EstatSalut getEstatPlugin() {
		return salutPluginComponent.getEstatPlugin();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		return salutPluginComponent.getPeticionsPlugin();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginLdapCaib.class);

}
