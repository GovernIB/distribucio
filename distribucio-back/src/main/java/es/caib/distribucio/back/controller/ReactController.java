package es.caib.distribucio.back.controller;

import es.caib.distribucio.back.base.controller.BaseUtilsController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.base.config.PropertyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;

/**
 * Exposa els endpoints de {@link BaseUtilsController} ({@code /ping}, {@code /sysenv},
 * {@code /manifest}, {@code /authToken}, {@code /authRoles}) que el SPA llegeix com a
 * {@code window.__RUNTIME_CONFIG__}/{@code __MANIFEST__}/{@code __AUTH_TOKEN__}/
 * {@code __AUTH_ROLES__} a l'{@code index.html}.
 * <p>
 * Sempre actiu (independent del perfil {@code devProxy}): aquests endpoints viuen fora de
 * {@code /reactapp}, tant si el build estàtic el serveix {@link ReactAppStaticController} com
 * si en mode desenvolupament ({@code npm run dev}) es demanen via proxy des de Vite (mateix
 * origen des del punt de vista del navegador -- veure {@code vite.config.ts}).
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Controller
public class ReactController extends BaseUtilsController {

	@Autowired(required = false)
	private OAuth2AuthorizedClientService authorizedClientService;

	@Override
	protected String getAuthToken() {
		// El SPA reutilitza la sessió ja establerta per Spring Security (ContainerAuthProvider al
		// front, sense client OIDC propi), així que no cal exposar cap bearer token com a tal: només
		// cal un JWT vàlid perquè el front en llegeixi el "exp" i sàpiga quan verificar de nou.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		// Mode JBoss (pre-auth): el principal ja és un OidcUser amb l'idToken del Keycloak adapter.
		if (authentication.getPrincipal() instanceof OidcUser) {
			return ((OidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();
		}
		// Mode standalone (WebSecurityConfig, isOauth2ClientActive/oauth2Login): el principal és un
		// DefaultOAuth2User sense el JWT -- cal anar a buscar l'access token real via el client
		// OAuth2 autoritzat associat a aquesta autenticació.
		if (authentication instanceof OAuth2AuthenticationToken && authorizedClientService != null) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
			OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
					oauth2Token.getAuthorizedClientRegistrationId(),
					oauth2Token.getName());
			if (authorizedClient != null) {
				return authorizedClient.getAccessToken().getTokenValue();
			}
		}
		return null;
	}

	@Override
	protected String[] getAuthRoles() {
		return new String[] {
				BaseConfig.ROLE_SUPER,
				BaseConfig.ROLE_ADMIN,
				BaseConfig.ROLE_ADMIN_LECTURA,
				BaseConfig.ROLE_REGLA,
				BaseConfig.ROLE_REPORT,
				BaseConfig.ROLE_BUSTIA_WS,
				BaseConfig.ROLE_BACKOFFICE_WS,
				BaseConfig.ROLE_COMANDA,
				BaseConfig.ROLE_USER
		};
	}

	@Override
	protected boolean isReactAppMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT)
				&& PropertyConfig.REACT_APP_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getReactAppMappedFrontProperty(String propertyName) {
		return PropertyConfig.REACT_APP_PROPS_MAP.get(propertyName);
	}

	@Override
	protected boolean isViteMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT)
				&& PropertyConfig.VITE_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getViteMappedFrontProperty(String propertyName) {
		return PropertyConfig.VITE_PROPS_MAP.get(propertyName);
	}

}
