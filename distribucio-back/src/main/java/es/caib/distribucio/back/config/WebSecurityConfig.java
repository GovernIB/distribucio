/**
 *
 */
package es.caib.distribucio.back.config;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import es.caib.distribucio.back.base.config.BaseWebSecurityConfig;
import es.caib.distribucio.back.base.config.MethodSecurityConfig;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;

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

	@Value("${es.caib.distribucio.security.mappableRoles:" + BaseConfig.ROLE_SUPER + "," + BaseConfig.ROLE_ADMIN + "," + BaseConfig.ROLE_ADMIN_LECTURA + "," + BaseConfig.ROLE_REGLA + "," + BaseConfig.ROLE_BUSTIA_WS + "," + BaseConfig.ROLE_COMANDA + "}")
	private String mappableRoles;
	@Value("${es.caib.distribucio.security.nameAttributeKey:preferred_username}")
	private String nameAttributeKey;
	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(MethodSecurityConfig.DEFAULT_ROLE_PREFIX);
	}

	@Bean
	public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	private boolean isJboss() {
		return jbossHomeDir != null;
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
	protected boolean isOauth2ClientActive() {
		return !isJboss();
	}

	@Override
	protected RequestMatcher[] internalPublicRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher(BaseConfig.API_PATH),
				new AntPathRequestMatcher(BaseConfig.PING_PATH),
				new AntPathRequestMatcher(BaseConfig.AUTH_TOKEN_PATH),
				new AntPathRequestMatcher(BaseConfig.AUTH_ROLES_PATH),
				new AntPathRequestMatcher(BaseConfig.SYSENV_PATH),
				new AntPathRequestMatcher(BaseConfig.MANIFEST_PATH),
		};
	}

	@Override
	protected void customHttpSecurityConfiguration(HttpSecurity http) throws Exception {
		if (isJboss()) {
			http.logout(lo -> lo.
					addLogoutHandler(getLogoutHandler()).
					logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
					invalidateHttpSession(true).
					logoutSuccessUrl("/").
					permitAll(false));
		} else {
			http.logout(lo -> lo.
					logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
					invalidateHttpSession(true).
					clearAuthentication(true).
					deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
					logoutSuccessUrl("/"));
		}
		http.authorizeHttpRequests().
				requestMatchers(publicRequestMatchers()).permitAll();
		super.customHttpSecurityConfiguration(http);
	}

	/**
	 * Matchers públics addicionals als de {@link #internalPublicRequestMatchers()}: recursos
	 * estàtics de la webapp JSP i documentació de l'API.
	 */
	protected RequestMatcher[] publicRequestMatchers() {
		return new RequestMatcher[] {
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
	public LogoutHandler getLogoutHandler() {
		return (request, response, authentication) -> {
			try {
				request.logout();
			} catch (ServletException ex) {
				log.error("Error al sortir de l'aplicació", ex);
			}
		};
	}

	/**
	 * Totes les autoritats concedides -- independentment del mode d'autenticació -- reben
	 * addicionalment el rol "tothom", usat per a regles d'accés vàlides per a qualsevol usuari
	 * autenticat.
	 */
	@Override
	protected void filterAllowedGrantedAuthorities(Set<GrantedAuthority> grantedAuthorities) {
		super.filterAllowedGrantedAuthorities(grantedAuthorities);
		grantedAuthorities.add(new SimpleGrantedAuthority("tothom"));
	}

	@Override
	protected AuthenticationDetailsSource<HttpServletRequest, ?> getPreauthFilterAuthenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				Collection<String> j2eeUserRoles = getUserRoles(context);
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				Set<String> roles = new HashSet<>(j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<?>) context.getUserPrincipal();
					Access realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						roles.addAll(realmAccess.getRoles());
					}
					logger.debug("Creating WebAuthenticationDetails for " + keycloakPrincipal.getName() + " with roles " + roles);
					Set<GrantedAuthority> grantedAuthorities = new HashSet<>(j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles));
					filterAllowedGrantedAuthorities(grantedAuthorities);
					result = new PreauthOidcWebAuthenticationDetails(
							context,
							grantedAuthorities,
							keycloakPrincipal.getKeycloakSecurityContext().getIdTokenString());
				} else {
					logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + j2eeUserRoles);
					Set<GrantedAuthority> grantedAuthorities = new HashSet<>(j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles));
					filterAllowedGrantedAuthorities(grantedAuthorities);
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
							context,
							grantedAuthorities);
				}
				return result;
			}
		};
		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(new HashSet<>(Arrays.asList(mappableRoles.split(","))));
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(MethodSecurityConfig.DEFAULT_ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Override
	protected AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> getPreauthAuthenticationUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
			@SneakyThrows
			protected UserDetails createUserDetails(
					Authentication token,
					Collection<? extends GrantedAuthority> authorities) {
				if (token.getDetails() instanceof PreauthOidcWebAuthenticationDetails) {
					PreauthOidcWebAuthenticationDetails tokenDetails = (PreauthOidcWebAuthenticationDetails)token.getDetails();
					String jwtIdToken = tokenDetails.getJwtIdToken();
					if (jwtIdToken != null) {
						JWT jwt = JWTParser.parse(jwtIdToken);
						return new PreauthOidcUserDetails(
								jwtIdToken,
								token.getName(),
								jwt.getJWTClaimsSet().getIssueTime().toInstant(),
								jwt.getJWTClaimsSet().getExpirationTime().toInstant(),
								jwt.getJWTClaimsSet().getClaims(),
								nameAttributeKey,
								authorities);
					}
				}
				return new User(token.getName(), "N/A", true, true, true, true, authorities);
			}
		};
	}

	@Getter
	public static class PreauthOidcWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
		private final String jwtIdToken;
		public PreauthOidcWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				String jwtIdToken) {
			super(request, authorities);
			this.jwtIdToken = jwtIdToken;
		}
	}

	@Getter
	public static class PreauthOidcUserDetails extends User implements OidcUser {
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
		private final OidcIdToken idToken;
		private final OidcUserInfo userInfo;
		private final Map<String, Object> attributes;
		private final Map<String, Object> claims;
		private final String nameAttributeKey;
		public PreauthOidcUserDetails(
				String jwtIdToken,
				String username,
				Instant issueTime,
				Instant expirationTime,
				Map<String, Object> claims,
				String nameAttributeKey,
				Collection<? extends GrantedAuthority> authorities) {
			super(username, "N/A", true, true, true, true, authorities);
			this.idToken = new OidcIdToken(
					jwtIdToken,
					issueTime,
					expirationTime,
					claims);
			this.userInfo = new OidcUserInfo(claims);
			this.attributes = claims;
			this.claims = claims;
			this.nameAttributeKey = nameAttributeKey;
		}
		public String getName() {
			return getUsername();
		}
	}

}
