package es.caib.distribucio.back.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * Reenvia les peticions sota {@code /reactapp/**} al servidor de desenvolupament de Vite
 * ({@code npm run dev}), preservant capçaleres (cookies de sessió incloses) perquè el SPA
 * es pugui navegar a través del mateix origen que el backend real ({@code
 * http://localhost:8080/distribucio/reactapp/}) amb recàrrega en calent, sense els
 * problemes de CORS/sessió que apareixen en obrir directament el port de Vite (5173).
 * <p>
 * Només actiu amb el perfil Spring {@code devProxy} (mai en producció). Substitueix
 * {@link ReactController}, que resta inactiu mentre aquest perfil ho està.
 *
 * @author Límit Tecnologies
 */
@Controller
@Profile("devProxy")
@RequestMapping("/reactapp")
public class DevProxyController {

	@Value("${es.caib.distribucio.development.proxyUrl:http://localhost:5173}")
	private String proxyUrl;

	@RequestMapping("/**")
	public ResponseEntity<byte[]> proxy(HttpServletRequest request) {
		String path = request.getRequestURI();
		String query = request.getQueryString();
		String targetUrl = proxyUrl + path + (query != null ? "?" + query : "");

		HttpHeaders headers = new HttpHeaders();
		Collections.list(request.getHeaderNames()).forEach(name -> headers.add(name, request.getHeader(name)));

		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, byte[].class);

		HttpHeaders responseHeaders = new HttpHeaders();
		response.getHeaders().forEach((key, value) -> responseHeaders.put(key, value));

		return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
	}

}
