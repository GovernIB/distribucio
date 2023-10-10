/**
 * 
 */
package es.caib.distribucio.back.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuració de Spring Security.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final KeycloakLogoutHandler keycloakLogoutHandler;

	WebSecurityConfig(KeycloakLogoutHandler keycloakLogoutHandler) {
		this.keycloakLogoutHandler = keycloakLogoutHandler;
	}

	@Bean
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Order(1)
	@Bean
	public SecurityFilterChain clientFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().
			requestMatchers(publicRequestMatchers()).
			permitAll().
			anyRequest().
			authenticated();
		http.oauth2Login().
			and().
			logout().
			addLogoutHandler(keycloakLogoutHandler).
			logoutSuccessUrl("/");
		return http.build();
	}

	@Order(2)
	@Bean
	public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().
			requestMatchers(publicRequestMatchers()).
			permitAll().
			anyRequest().
			authenticated();
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		return http.build();
	}

	private RequestMatcher[] publicRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/public/**/*"),
				new AntPathRequestMatcher("/ws/v0/bustia"),
				new AntPathRequestMatcher("/ws/v1/bustia"),
				new AntPathRequestMatcher("/ws/MCGDws"),
				new AntPathRequestMatcher("/ws/portafibCallback"),
				new AntPathRequestMatcher("/api-docs"),
				new AntPathRequestMatcher("/api-docs/**/*"),
				new AntPathRequestMatcher("/css/**/*"),
				new AntPathRequestMatcher("/fonts/**/*"),
				new AntPathRequestMatcher("/img/**/*"),
				new AntPathRequestMatcher("/js/**/*"),
				new AntPathRequestMatcher("/webjars/**/*"),
		};
	}

	@Slf4j
	@Configuration
	public static class KeycloakLogoutHandler implements LogoutHandler {
		private final RestTemplate restTemplate = new RestTemplate();
		@Override
		public void logout(
				HttpServletRequest request,
				HttpServletResponse response,
				Authentication auth) {
			logoutFromKeycloak((OidcUser)auth.getPrincipal());
		}
		private void logoutFromKeycloak(OidcUser user) {
			String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";
			UriComponentsBuilder builder = UriComponentsBuilder.
					fromUriString(endSessionEndpoint).
					queryParam("id_token_hint", user.getIdToken().getTokenValue());
			ResponseEntity<String> logoutResponse = restTemplate.getForEntity(
			builder.toUriString(), String.class);
			if (logoutResponse.getStatusCode().is2xxSuccessful()) {
				log.debug("Successfully logged out from Keycloak");
			} else {
				log.error("Could not propagate logout to Keycloak");
			}
		}
	}

}
