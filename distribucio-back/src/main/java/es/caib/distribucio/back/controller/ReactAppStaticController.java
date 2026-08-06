package es.caib.distribucio.back.controller;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Objects;

/**
 * Serveix la interfície React (build de Vite copiat a {@code /reactapp}) amb fallback a
 * {@code index.html} per a les rutes internes del SPA (mode "history").
 * <p>
 * Inactiu quan el perfil {@code devProxy} està actiu -- en aquest mode {@link DevProxyController}
 * ocupa el mateix path ({@code /reactapp/**}) per reenviar les peticions al servidor de
 * desenvolupament de Vite ({@code npm run dev}) en lloc de servir el build estàtic.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@Profile("!devProxy")
public class ReactAppStaticController {

	private final ServletContext servletContext;

	@RequestMapping({BaseConfig.REACT_APP_PATH, BaseConfig.REACT_APP_PATH + "/**"})
	public ResponseEntity<?> serveReact(HttpServletRequest request) {
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		try {
			InputStream resource = servletContext.getResourceAsStream(path);
			if (resource != null) {
				String mimeType = servletContext.getMimeType(path);
				MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
				return ResponseEntity.ok().contentType(mediaType).body(new InputStreamResource(resource));
			}
			String uri = request.getRequestURI();
			if (uri.matches(".*\\.(js|css|ico|png|jpg|svg|woff2?|map)$") || uri.endsWith("index.html")) {
				return ResponseEntity.notFound().build();
			}
			InputStream indexHtml = servletContext.getResourceAsStream(BaseConfig.REACT_APP_PATH + "/index.html");
			return ResponseEntity
					.ok()
					.contentType(MediaType.TEXT_HTML)
					.body(new InputStreamResource(Objects.requireNonNull(indexHtml)));
		} catch (Exception ex) {
			log.error("Error carregant recurs de reactapp", ex);
			return ResponseEntity.internalServerError().body("Error carregant recurs");
		}
	}

}
