/**
 *
 */
package es.caib.distribucio.back.config;

import es.caib.distribucio.back.base.config.BaseWebSecurityConfig;
import es.caib.distribucio.back.base.config.MethodSecurityConfig;
import es.caib.distribucio.logic.intf.base.util.HttpRequestUtil;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.auth.AuthenticationDetails;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Configuració de Spring Security. Suporta dos modes d'autenticació segons el tipus de
 * desplegament, seleccionats en temps d'execució mitjançant {@link #isJboss()}:
 * <ul>
 *     <li>Desplegat sobre JBoss (dins l'EAR): autenticació delegada al contenidor web
 *     (adaptador Keycloak de JBoss) i llegida via pre-autenticació J2EE.</li>
 *     <li>Standalone (Spring Boot): OAuth2 login (sessió) combinat amb OAuth2 resource
 *     server (Bearer JWT, usat pel SPA React quan es corre amb {@code npm run dev}).</li>
 * </ul>
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
public class WebSecurityConfig extends BaseWebSecurityConfig {

	public static final String LOGOUT_URL = "/usuari/logout";

	@Value("${es.caib.distribucio.security.mappableRoles:" +
			BaseConfig.ROLE_SUPER + "," +
			BaseConfig.ROLE_ADMIN + "," +
			BaseConfig.ROLE_ADMIN_LECTURA + "," +
			BaseConfig.ROLE_REGLA + "," +
			BaseConfig.ROLE_BUSTIA_WS + "," +
			BaseConfig.ROLE_COMANDA + "," +
			BaseConfig.ROLE_USER + "}")
	private String mappableRoles;
	@Value("${" + BaseConfig.PROP_SECURITY_ROLE_HTTP_HEADER + ":X-App-Role}")
	private String selectedRoleHttpHeader;
	@Value("${es.caib.distribucio.security.nameAttributeKey:preferred_username}")
	private String nameAttributeKey;
	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;

	@Autowired(required = false)
	private ClientRegistrationRepository clientRegistrationRepository;

	@Override
	protected void customHttpSecurityConfiguration(HttpSecurity http) throws Exception {
		if (!isJboss()) {
			OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
					clientRegistrationRepository);
			oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
			http.logout(lo -> lo.
					addLogoutHandler(getDeleteCookiesLogoutHandler()).
					logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
					invalidateHttpSession(true).
					clearAuthentication(true).
					deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
					logoutSuccessHandler(oidcLogoutSuccessHandler).
					logoutSuccessUrl("/"));
		}
		http.authorizeHttpRequests().
				requestMatchers(publicRequestMatchers()).permitAll();
		// La interfície REACT indica a cada petició amb quin rol està operant l'usuari; el filtre
		// hi restringeix les autoritats perquè les comprovacions per rol responguin "opera amb
		// aquest rol" i no "té aquest rol" (veure RolSeleccionatFilter).
		http.addFilterBefore(
				new RolSeleccionatFilter(selectedRoleHttpHeader),
				AuthorizationFilter.class);
		if (!isJboss()) {
			http.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
					.invalidSessionUrl("/"));
			// Les autoritats es creen sense prefix (veure BaseWebSecurityConfig.oidcUserService i
			// MethodSecurityConfig.DEFAULT_ROLE_PREFIX), però el wrapper de Spring Security que
			// respon HttpServletRequest.isUserInRole() hi anteposa "ROLE_" per defecte i no troba
			// mai cap coincidència. Sense això RolHelper no ofereix DIS_SUPER a la capçalera
			// (getRolsUsuariActual és l'únic lloc que el consulta amb isUserInRole) i el canvi de
			// rol falla en silenci, perquè processarCanviRols també hi passa.
			// Només en mode Boot standalone: sobre JBoss els rols arriben del contenidor per
			// pre-autenticació i aquest camí encara no s'ha provat.
			http.servletApi(servletApi -> servletApi.rolePrefix(MethodSecurityConfig.DEFAULT_ROLE_PREFIX));
		}
		super.customHttpSecurityConfiguration(http);
	}

	/**
	 * Matchers públics addicionals als de {@link #internalPublicRequestMatchers()}: recursos
	 * estàtics de la webapp JSP i documentació de l'API.
	 */
	protected RequestMatcher[] publicRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/swagger-resources/**"),
				new AntPathRequestMatcher("/swagger-ui/**"),
				new AntPathRequestMatcher("/public/**"),
				new AntPathRequestMatcher("/api-docs"),
				new AntPathRequestMatcher("/api-docs/**/*"),
				new AntPathRequestMatcher("/css/**/*"),
				new AntPathRequestMatcher("/fonts/**/*"),
				new AntPathRequestMatcher("/img/**/*"),
				new AntPathRequestMatcher("/js/**/*"),
				new AntPathRequestMatcher("/webjars/**/*"),
		};
	}

	@Bean
	public LogoutHandler getDeleteCookiesLogoutHandler() {
		return (request, response, authentication) -> {
			try {
				log.info("Logout called");
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						Cookie deletedCookie = new Cookie(cookie.getName(), "");
						deletedCookie.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
						deletedCookie.setMaxAge(0);
						deletedCookie.setHttpOnly(cookie.isHttpOnly());
						deletedCookie.setSecure(cookie.getSecure());
						response.addCookie(deletedCookie);
					}
				}
				request.logout();
			} catch (ServletException ex) {
				log.error("Error en el logout", ex);
			}
		};
	}

	@Override
	protected boolean isWebContainerAuthActive() {
		return isJboss();
	}
	@Override
	protected boolean isOauth2ResourceServerActive() {
		return !isJboss();
	}
	@Override
	protected boolean isOidcClientActive() {
		return !isJboss();
	}

	@Override
	protected Set<String> getAllowedRoles() {

		Optional<HttpServletRequest> optionalRequest = HttpRequestUtil.getCurrentHttpRequest();
		Set<String> allowedRoles = Set.of(mappableRoles.split(","));
		if (optionalRequest.isPresent()) {
			// Si la petició HTTP conté la capçalera amb el rol seleccionat retorna únicament aquest rol en la llista de rols permesos.
			HttpServletRequest request = optionalRequest.get();
			String selectedRole = request.getHeader(selectedRoleHttpHeader);
			if (selectedRole != null) {
				HashSet<String> editableAllowedRoles = new HashSet<>(allowedRoles);
				editableAllowedRoles.removeIf(s -> !s.equals(selectedRole));
				return editableAllowedRoles;
			}
		}
		return allowedRoles;
	}

	/**
	 * Concedeix el rol "tothom" ({@link BaseConfig#ROLE_USER}) a qualsevol usuari autenticat, abans
	 * que s'apliqui el filtre de rols permesos. No és un rol de Keycloak ni està declarat al
	 * web.xml: és el rol base amb què opera qualsevol usuari de l'aplicació, i la interfície JSP
	 * ja l'ofereix al selector sempre que l'usuari tengui permís de lectura sobre l'entitat actual
	 * (veure {@code RolHelper.getRolsUsuariActual}). Sense això el selector de rols de la interfície
	 * REACT no el pot oferir mai, i seleccionar-lo deixava l'usuari sense cap authority perquè
	 * {@link #getAllowedRoles()} no el considerava mapejable.
	 * <p/>
	 * RIPEA fa el mateix a {@code SpringBootWebSecurityConfig.java:104} (camí Spring Boot) i a
	 * {@code JBossWebSecurityConfig.java:141} (camí JBoss, que aquí es cobreix a
	 * {@link #getPreauthFilterAuthenticationDetailsSource()}).
	 */
	@Override
	protected void filterAllowedGrantedAuthorities(Set<GrantedAuthority> grantedAuthorities) {
		grantedAuthorities.add(new SimpleGrantedAuthority(BaseConfig.ROLE_USER));
		super.filterAllowedGrantedAuthorities(grantedAuthorities);
	}

	private boolean isJboss() {
		return jbossHomeDir != null;
	}

	@Override
	protected AuthenticationDetailsSource<HttpServletRequest, ?> getPreauthFilterAuthenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				// El contenidor no coneix el rol "tothom" (no està declarat al web.xml), així que
				// s'afegeix aquí als rols J2EE perquè tot usuari autenticat el tengui també en
				// mode EAR -- veure filterAllowedGrantedAuthorities.
				Collection<String> j2eeUserRoles = new HashSet<>(getUserRoles(context));
				j2eeUserRoles.add(BaseConfig.ROLE_USER);
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					Set<String> roles = new HashSet<>(j2eeUserRoles);
					AccessToken.Access realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						realmAccess.getRoles().stream().
								map(r -> MethodSecurityConfig.DEFAULT_ROLE_PREFIX + r).
								forEach(roles::add);
					}
					IDToken idToken = keycloakPrincipal.getKeycloakSecurityContext().getIdToken();
					// Les autoritats es construeixen senceres a posta: la restricció al rol
					// seleccionat es fa per petició a RolSeleccionatFilter, no aquí, perquè
					// aquest detall es calcula un sol cop i deixaria els rols congelats al que
					// hi hagués a la capçalera de la primera petició de la sessió.
					result = new PreauthWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							keycloakPrincipal.getKeycloakSecurityContext().getIdTokenString(),
							nameAttributeKey.equals("preferred_username") ?
									idToken.getPreferredUsername() :
									(String)idToken.getOtherClaims().get(nameAttributeKey),
							idToken.getName(),
							idToken.getEmail(),
							(String)idToken.getOtherClaims().get("nif"),
							roles.toArray(new String[0]));
				} else {
					// Veure el comentari del bloc anterior sobre la restricció per rol.
					Collection<? extends GrantedAuthority> grantedAuthorities = j2eeUserRoles2GrantedAuthoritiesMapper.
							getGrantedAuthorities(j2eeUserRoles);
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
							context,
							grantedAuthorities);
				}
				log.debug("Created WebAuthenticationDetails for {} with roles {}",
						context.getUserPrincipal().getName(),
						result.getGrantedAuthorities());
				return result;
			}
		};
		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(getAllowedRoles());
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(MethodSecurityConfig.DEFAULT_ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Getter
	public static class PreauthWebAuthenticationDetails
			extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails
			implements AuthenticationDetails {
		private final String jwtToken;
		private final String preferredUsername;
		private final String name;
		private final String email;
		private final String nif;
		private final String[] originalRoles;
		public PreauthWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				String jwtToken,
				String preferredUsername,
				String name,
				String email,
				String nif,
				String[] originalRoles) {
			super(request, authorities);
			this.jwtToken = jwtToken;
			this.preferredUsername = preferredUsername;
			this.name = name;
			this.email = email;
			this.nif = nif;
			this.originalRoles = originalRoles;
		}
	}
}
