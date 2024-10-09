/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.UsuariRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;

/**
 * Helper per a operacions amb usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariHelper {
 
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ConfigHelper configHelper;

	/*public Authentication generarUsuariAutenticatEjb(
			SessionContext sessionContext,
			boolean establirComAUsuariActual) {
		if (sessionContext != null && sessionContext.getCallerPrincipal() != null) {
			return generarUsuariAutenticat(
					sessionContext.getCallerPrincipal().getName(),
					establirComAUsuariActual);
		} else {
			return null;
		}
	}*/

	public Authentication generarUsuariAutenticat(
			String usuariCodi,
			boolean establirComAUsuariActual) {
		List<GrantedAuthority> authorities = null;
		Authentication auth = new DadesUsuariAuthenticationToken(
				usuariCodi,
			authorities);
		if (establirComAUsuariActual)
			SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}

	public class DadesUsuariAuthenticationToken extends AbstractAuthenticationToken {
		String principal;
		public DadesUsuariAuthenticationToken(
				String usuariCodi,
				Collection<GrantedAuthority> authorities) {
			super(authorities);
			principal = usuariCodi;
		}
		@Override
		public Object getCredentials() {
			return principal;
		}
		@Override
		public Object getPrincipal() {
			return principal;
		}
		private static final long serialVersionUID = 5974089352023050267L;
	}

	public UsuariEntity getUsuariAutenticat() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			return null;
		UsuariEntity usuari = usuariRepository.findById(auth.getName()).orElse(null);
		if (usuari == null) {
			String idioma = configHelper.getConfig("es.caib.distribucio.default.user.language");
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			// Primer cream l'usuari amb dades fictícies i després l'actualitzam.
			// Així evitam possibles bucles infinits a l'hora de guardar registre
			// de les peticions al plugin d'usuaris.
			usuari = usuariRepository.save(
					UsuariEntity.getBuilder(
							auth.getName(),
							auth.getName(),
							"00000000X",
							auth.getName() + "@" + "caib.es",
							auth.getName() + "@" + "caib.es", 
							idioma).build());
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari != null) {
				usuari.update(
						dadesUsuari.getNom(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
			} else {
				// Pot ser un usuari d'integració que no estigui a LDAP
				usuari.update(
						auth.getName(),
						null,
						null);
			}
			usuari = usuariRepository.save(usuari);
		}
		return usuari;
	}

	public UsuariEntity getUsuariByCodi(String codi) {
		UsuariEntity usuari = usuariRepository.findById(codi).orElse(null);
		if (usuari == null) {
			String idioma = configHelper.getConfig("es.caib.distribucio.default.user.language");
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + codi + ")");
			// Primer cream l'usuari amb dades fictícies i després l'actualitzam.
			// Així evitam possibles bucles infinits a l'hora de guardar registre
			// de les peticions al plugin d'usuaris.
			usuari = usuariRepository.save(
					UsuariEntity.getBuilder(
							codi,
							codi,
							"00000000X",
							codi + "@" + "caib.es",
							codi + "@" + "caib.es", 
							idioma).build());
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(codi);
			if (dadesUsuari != null) {
				usuari.update(
						dadesUsuari.getNom(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
			} else {
				usuari.update(
						codi,
						null,
						null);
			}
			usuariRepository.save(usuari);
		}
		return usuari;
	}

	/** Mètode públic per consultar si l'usuari actual és administrador d'entitat DIS_ADMIN. */
	public boolean isAdmin() {
		return this.hasRole("DIS_ADMIN");
	}

	/** Mètode públic per consultar si l'usuari actual és administrador d'entitat DIS_ADMIN_LECTURA. */
	public boolean isAdminLectura() {
		return this.hasRole("DIS_ADMIN_LECTURA");
	}

	/** Mètode públic per consultar si l'usuari actual té el rol passat per paràmetre. */
	public boolean hasRole(String rol) {
		boolean hasRole = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			for (GrantedAuthority ga : auth.getAuthorities())
				if (ga.getAuthority().equals(rol)) {
					hasRole = true;
					break;
				}
		}
		return hasRole;
	}

	private static final Logger logger = LoggerFactory.getLogger(UsuariHelper.class);

}
