/**
 * 
 */
package es.caib.distribucio.back.config;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

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
@Configuration
@ConditionalOnNotWarDeployment
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class SpringBootWebSecurityConfig extends BaseWebSecurityConfig {

	@Bean
	public SecurityFilterChain oauth2LoginSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests().
			requestMatchers(publicRequestMatchers()).permitAll().
			anyRequest().authenticated();
		http.oauth2Login().
			userInfoEndpoint().userService(oauth2UserService());
		http.logout().
			invalidateHttpSession(true).
			clearAuthentication(true).
			deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
			//addLogoutHandler(oauth2LogoutHandler()).
			//logoutUrl(LOGOUT_URL).
			logoutSuccessUrl("/");
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable();
		http.cors();
		return http.build();
	}

	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
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

	/*// TODO no funciona perquè aquest handler suposa que li arribarà un OidcUser d'on
	// podrà obtenir el idToken però realment li arriba un OAuth2User sense idToken.
	private LogoutHandler oauth2LogoutHandler() {
		return new LogoutHandler() {
			private final RestTemplate restTemplate = new RestTemplate();
			@Override
			public void logout(
					HttpServletRequest request,
					HttpServletResponse response,
					Authentication auth) {
				if (auth != null) {
					logoutFromKeycloak((OidcUser)auth.getPrincipal());
				}
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
	}*/

}