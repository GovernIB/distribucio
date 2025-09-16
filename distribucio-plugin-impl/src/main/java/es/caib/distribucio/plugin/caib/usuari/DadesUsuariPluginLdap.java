/**
 * 
 */
package es.caib.distribucio.plugin.caib.usuari;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;
import es.caib.distribucio.plugin.utils.PropertiesHelper;
import lombok.Synchronized;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginLdap extends DistribucioAbstractPluginProperties implements DadesUsuariPlugin {

	public DadesUsuariPluginLdap() {
		super();
	}
	
	public DadesUsuariPluginLdap(Properties properties) {
		super(properties);
	}
	
	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (codi=" + usuariCodi + ")");
		try {
			DadesUsuari dadesUsuari = consultaUsuariUnic(
					getLdapFiltreCodi(),
					usuariCodi);
			
			incrementarOperacioOk();
			return dadesUsuari;
		} catch (SistemaExternException ex) {
			incrementarOperacioError();
			throw ex;
		} catch (NamingException ex) {
			incrementarOperacioError();
			throw new SistemaExternException(
					"Error al consultar l'usuari amb codi (codi=" + usuariCodi + ")",
					ex);
		}
	}

	@Override
	public List<DadesUsuari> findAmbGrup(
			String grupCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			List<DadesUsuari> dadesUsuaris = consultaUsuaris(
					getLdapFiltreGrup(),
					grupCodi);
			
			incrementarOperacioOk();
			return dadesUsuaris;
		} catch (NamingException ex) {
			incrementarOperacioError();
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup (grupCodi=" + grupCodi + ")",
					ex);
		}
	}

	@Override
	public List<String> findRolsPerUsuari(String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		
		try {
		
			List<String> rolsUsuari = new ArrayList<String>();
			Hashtable<String, String> entornLdap = new Hashtable<String, String>();
			entornLdap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
			entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
			entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
			LdapContext ctx = new InitialLdapContext(entornLdap, null);
			
			try {
				String[] atributs = getLdapAtributs().split(",");
				SearchControls searchCtls = new SearchControls();
				searchCtls.setReturningAttributes(atributs);
				searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				NamingEnumeration<SearchResult> answer = ctx.search(
						getLdapSearchBase(),
						getLdapFiltreCodi().replace("XXX", usuariCodi),
						searchCtls);
				while (answer.hasMoreElements()) {
					SearchResult result = answer.next();
						rolsUsuari = obtenirAtributComListString(
								result.getAttributes(),
								atributs[5]);
					
				}
			} finally {
				ctx.close();
			}
			
			return rolsUsuari;
			
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}

	@SuppressWarnings("rawtypes")
	private List<String> obtenirAtributComListString(Attributes atributs, String atributNom) throws NamingException {
		
		Attribute atribut = atributs.get(atributNom);
		List<String> listRols = new ArrayList<String>();		
		NamingEnumeration rols = atribut.getAll();
		
		while (rols.hasMoreElements()) {
			String rol = rols.next().toString();
			int iniciIndexRol = rol.indexOf("CN=") + 3;
			int fiIndexRol = rol.indexOf(",");
			listRols.add(rol.substring(iniciIndexRol, fiIndexRol));
		}
		
		return listRols;
	}

	private DadesUsuari consultaUsuariUnic(
			String filtre,
			String valor) throws SistemaExternException, NamingException {
		List<DadesUsuari> usuaris = consultaUsuaris(filtre, valor);
		if (usuaris.size() == 1) {
			return usuaris.get(0);
		} else if(usuaris.size() > 1){
			throw new SistemaExternException(
					"La consulta d'usuari únic ha retornat més d'un resultat (" +
					"filtre=" + filtre + ", " +
					"valor=" + valor + ")");
		} else if(usuaris.size() == 0){
			throw new SistemaExternException(
					"La consulta d'usuari únic no ha retornat cap resultat (" +
					"filtre=" + filtre + ", " +
					"valor=" + valor + ")");
		} else {
			throw new SistemaExternException("Error desconegut al consultar un usuari únic");
		}
	}
	private List<DadesUsuari> consultaUsuaris(
			String filtre,
			String valor) throws NamingException {
		List<DadesUsuari> usuaris = new ArrayList<DadesUsuari>();
		Hashtable<String, String> entornLdap = new Hashtable<String, String>();
		entornLdap.put(Context.INITIAL_CONTEXT_FACTORY,
				  "com.sun.jndi.ldap.LdapCtxFactory");
		entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
		entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
		entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
		LdapContext ctx = new InitialLdapContext(entornLdap, null);
		try {
			String[] atributs = getLdapAtributs().split(",");
			SearchControls searchCtls = new SearchControls();
			searchCtls.setReturningAttributes(atributs);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> answer = ctx.search(
					getLdapSearchBase(),
					filtre.replace("XXX", valor),
					searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult result = answer.next();
				String grup = obtenirAtributComString(
						result.getAttributes(),
						atributs[4]);
				String memberOf = obtenirAtributComString(
						result.getAttributes(),
						atributs[5]);
				boolean excloure = false;
				String useraccountcontrol = "";
				if (getLdapExcloureGrup() != null) {
					excloure = grup.equals(getLdapExcloureGrup());
					if (excloure && getLdapExcloureMembre() != null) {
						excloure = memberOf.contains(getLdapExcloureMembre());
					}
				}
				if (!excloure) {
					String codi = obtenirAtributComString(
							result.getAttributes(),
							atributs[0]);
					String nom = obtenirAtributComString(
							result.getAttributes(),
							atributs[1]);
					String llinatges = obtenirAtributComString(
							result.getAttributes(),
							atributs[2]);
					String email = obtenirAtributComString(
							result.getAttributes(),
							atributs[3]);
					String nif = obtenirAtributComString(
							result.getAttributes(),
							atributs[4]);
					if (atributs.length > 6) {
	 					useraccountcontrol = obtenirAtributComString(
								result.getAttributes(),
								atributs[6]);
					}
					DadesUsuari dadesUsuari = new DadesUsuari();
					dadesUsuari.setCodi(codi);
					dadesUsuari.setNom(nom);
					dadesUsuari.setLlinatges(llinatges);
					dadesUsuari.setEmail(email);
					dadesUsuari.setNif(nif);
					if (atributs.length > 6) {
						dadesUsuari.setActiu(isUserEnabled(useraccountcontrol, nom));
					}
					usuaris.add(dadesUsuari);
					
				}
			}
		} finally {
			ctx.close();
		}
		return usuaris;
	}
	
	private boolean isUserEnabled(String useraccountcontrol, String nom) {
		List<String> valoresInactividad = new ArrayList<String>();
		valoresInactividad.add("2"); // ACCOUNTDISABLE
		valoresInactividad.add("546"); // Disabled, Don’t Expire Password
		valoresInactividad.add("514"); // Disabled, Don’t Expire Password
		valoresInactividad.add("66050"); // Disabled, Don’t Expire Password
		valoresInactividad.add("66082"); // Disabled, Don’t Expire Password
		
		if (valoresInactividad.contains(useraccountcontrol)) {
			LOGGER.error("El usuario " + nom + " está inactivo en la LDAP!");
			return false;
		} 
		return true;
	}

	private String obtenirAtributComString(
			Attributes atributs,
			String atributNom) throws NamingException {
		Attribute atribut = atributs.get(atributNom);
		return (atribut != null) ? (String)atribut.get() : null;
	}

	private String getLdapServerUrl() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.server.url");
	}
	private String getLdapPrincipal() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.principal");
	}
	private String getLdapCredentials() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.credentials");
	}
	private String getLdapSearchBase() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.search.base");
	}
	private String getLdapAtributs() {
		// Exemple: cn,givenName,sn,mail,departmentNumber,memberOf
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.atributs");
	}
	private String getLdapFiltreCodi() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(cn=XXX))
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.filtre.codi");
	}
	private String getLdapFiltreGrup() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(memberOf=cn=XXX,dc=caib,dc=es))
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.filtre.grup");
	}
	private String getLdapExcloureGrup() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.excloure.grup");
	}
	private String getLdapExcloureMembre() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.dades.usuari.ldap.excloure.membre");
	}
	
	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private boolean configuracioEspecifica = false;
	private int operacionsOk = 0;
	private int operacionsError = 0;

	@Synchronized
	private void incrementarOperacioOk() {
		operacionsOk++;
	}

	@Synchronized
	private void incrementarOperacioError() {
		operacionsError++;
	}

	@Synchronized
	private void resetComptadors() {
		operacionsOk = 0;
		operacionsError = 0;
	}

	@Override
	public boolean teConfiguracioEspecifica() {
		return this.configuracioEspecifica;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		try {
			Instant start = Instant.now();
			findAmbCodi("fakeUser");
			return EstatSalut.builder()
					.latencia((int) Duration.between(start, Instant.now()).toMillis())
					.estat(EstatSalutEnum.UP)
					.build();
		} catch (Exception ex) {
			return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
		}
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
				.totalOk(operacionsOk)
				.totalError(operacionsError)
				.build();
		resetComptadors();
		return integracioPeticions;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginLdap.class);

}
