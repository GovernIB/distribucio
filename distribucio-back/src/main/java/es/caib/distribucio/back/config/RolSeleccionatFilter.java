package es.caib.distribucio.back.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Restringeix, petició a petició, les autoritats de l'usuari al rol amb què declara que està
 * operant a la capçalera de rol seleccionat (per defecte {@code X-App-Role}, veure
 * {@link es.caib.distribucio.logic.intf.config.BaseConfig#PROP_SECURITY_ROLE_HTTP_HEADER}).
 * <p/>
 * Sense això, les restriccions per rol dels recursos ({@code @ResourceAccessConstraint} de tipus
 * ROLE, que es resolen amb les autoritats de la sessió de seguretat) responen "l'usuari té aquest
 * rol" en lloc de "l'usuari està operant amb aquest rol": qui té {@code DIS_ADMIN} i
 * {@code DIS_ADMIN_LECTURA} continuaria podent escriure encara que hagués triat el rol de lectura.
 * <p/>
 * {@link WebSecurityConfig#getAllowedRoles()} ja fa aquesta restricció, però només arriba a
 * aplicar-se al camí de token bearer ({@code BaseWebSecurityConfig.jwtAuthConverter}, que es
 * resol a cada petició). Als camins de sessió OIDC i de preautenticació les autoritats es
 * calculen una sola vegada -- en iniciar sessió-- quan encara no hi ha cap capçalera, de manera
 * que la restricció no s'hi aplicava mai. Aquest filtre la garanteix als tres camins.
 * <p/>
 * Només pot restringir: la llista resultant és la intersecció amb les autoritats que l'usuari ja
 * tenia, així que una capçalera manipulada no pot concedir cap rol (com a molt deixa la petició
 * sense cap rol i, per tant, sense accés). Les peticions sense capçalera -- la interfície JSP
 * clàssica -- no es toquen.
 */
@Slf4j
@RequiredArgsConstructor
public class RolSeleccionatFilter extends OncePerRequestFilter {

	private final String rolHttpHeader;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		SecurityContext contextOriginal = SecurityContextHolder.getContext();
		Authentication authRestringida = getAuthenticationAmbRolSeleccionat(
				contextOriginal.getAuthentication(),
				request.getHeader(rolHttpHeader));
		if (authRestringida == null) {
			filterChain.doFilter(request, response);
			return;
		}
		// El context es restaura sempre: si no, el filtre de persistència del context (que
		// s'executa abans i acaba després) desaria a la sessió les autoritats ja restringides i
		// l'usuari perdria la resta de rols fins a tornar a iniciar sessió.
		SecurityContext contextRestringit = SecurityContextHolder.createEmptyContext();
		contextRestringit.setAuthentication(authRestringida);
		SecurityContextHolder.setContext(contextRestringit);
		try {
			filterChain.doFilter(request, response);
		} finally {
			SecurityContextHolder.setContext(contextOriginal);
		}
	}

	/**
	 * Retorna una còpia de l'autenticació amb les autoritats restringides al rol seleccionat, o
	 * null si no s'ha de restringir res (no hi ha capçalera, l'usuari no està autenticat o les
	 * autoritats ja es limiten al rol seleccionat).
	 */
	private Authentication getAuthenticationAmbRolSeleccionat(Authentication auth, String rolSeleccionat) {
		if (rolSeleccionat == null || auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}
		Set<GrantedAuthority> autoritatsRol = auth.getAuthorities().stream().
				filter(a -> rolSeleccionat.equals(a.getAuthority())).
				collect(Collectors.toSet());
		if (auth.getAuthorities().size() == autoritatsRol.size()) {
			return null;
		}
		Authentication authRestringida = copiaAmbAutoritats(auth, autoritatsRol);
		if (authRestringida == null) {
			// Tipus d'autenticació no previst: no es pot copiar sense perdre'n informació (hi ha
			// codi que en depèn, com ReactController.getAuthToken()), així que es deixa tal com
			// està i s'avisa, en lloc de restringir a cegues.
			log.warn(
					"No es pot restringir l'autenticació al rol seleccionat: tipus no suportat ({})",
					auth.getClass().getName());
		}
		return authRestringida;
	}

	private Authentication copiaAmbAutoritats(Authentication auth, Set<GrantedAuthority> autoritats) {
		if (auth instanceof JwtAuthenticationToken) {
			JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken)auth;
			return new JwtAuthenticationToken(jwtAuth.getToken(), autoritats, jwtAuth.getName());
		} else if (auth instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken)auth;
			return new OAuth2AuthenticationToken(
					(OAuth2User)oauth2Auth.getPrincipal(),
					autoritats,
					oauth2Auth.getAuthorizedClientRegistrationId());
		} else if (auth instanceof PreAuthenticatedAuthenticationToken) {
			PreAuthenticatedAuthenticationToken preauth = (PreAuthenticatedAuthenticationToken)auth;
			PreAuthenticatedAuthenticationToken copia = new PreAuthenticatedAuthenticationToken(
					preauth.getPrincipal(),
					preauth.getCredentials(),
					autoritats);
			copia.setDetails(preauth.getDetails());
			return copia;
		} else {
			return null;
		}
	}

}
