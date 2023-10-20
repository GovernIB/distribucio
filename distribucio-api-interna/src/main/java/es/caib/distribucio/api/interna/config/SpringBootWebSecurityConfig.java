/**
 * 
 */
package es.caib.distribucio.api.interna.config;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration("apiInternaSpringBootWebSecurityConfig")
@EnableWebSecurity
@ConditionalOnNotWarDeployment
public class SpringBootWebSecurityConfig extends BaseWebSecurityConfig {

	@Bean
	//@Order(1)
	public SecurityFilterChain clientFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().
			requestMatchers(publicRequestMatchers()).permitAll().
			anyRequest().authenticated();
		http.oauth2Login().
			userInfoEndpoint().userService(oidcUserService());
		http.logout().
			addLogoutHandler(keycloakLogoutHandler()).
			logoutUrl(LOGOUT_URL).
			logoutSuccessUrl("/");
		return http.build();
	}

	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oidcUserService() {
		final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
		return (userRequest) -> {
			OAuth2User oauth2User = delegate.loadUser(userRequest);
			OAuth2AccessToken accessToken = userRequest.getAccessToken();
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			try {
				JWT parsedJwt = JWTParser.parse(accessToken.getTokenValue());
				JSONObject realmAccess = (JSONObject)parsedJwt.getJWTClaimsSet().getClaim("realm_access");
				if (realmAccess != null) {
					JSONArray roles = (JSONArray)realmAccess.get("roles");
					if (roles != null) {
						roles.stream().
						map(r -> new SimpleGrantedAuthority((String)r)).
						forEach(mappedAuthorities::add);
					}
				}
			} catch (ParseException ex) {
				log.warn("No s'han pogut obtenir els rols del token JWT", ex);
			}
			return new DefaultOAuth2User(
					mappedAuthorities,
					oauth2User.getAttributes(),
					userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
		};
	}

	private LogoutHandler keycloakLogoutHandler() {
		return new LogoutHandler() {
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
		};
	}

	/*@Bean
	@Order(2)
	public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().
			requestMatchers(publicRequestMatchers()).
			permitAll().
			anyRequest().
			authenticated();
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		return http.build();
	}*/

}
