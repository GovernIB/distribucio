/**
 * 
 */
package es.caib.distribucio.back.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració de Springdoc OpenAPI.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		String version = "Unknown";
		try {
			Manifest manifest = new Manifest(getClass().getResourceAsStream("/META-INF/MANIFEST.MF"));
			Attributes attributes = manifest.getMainAttributes();
			version = attributes.getValue("Implementation-Version");
		} catch (IOException ex) {
			log.error("No s'ha pogut obtenir la versió del fitxer MANIFEST.MF", ex);
		}
		OpenAPI openapi = new OpenAPI().info(
				new Info().
				title("API REST Distribucio").
				description("API REST de Distribucio per a la creació automàtica de regles de distribucio a backoffices").
				contact(new Contact().email("limit@limit.es")).
				version(version));
		if (enableAuthComponent()) {
			return openapi.
					components(
							new Components().addSecuritySchemes(
									"Bearer token",
									new SecurityScheme().
									type(SecurityScheme.Type.HTTP).
									scheme("bearer").
									bearerFormat("JWT").
									in(SecurityScheme.In.HEADER).
									name("Authorization"))).
					addSecurityItem(
							new SecurityRequirement().addList(
									"Bearer token",
									Arrays.asList("read", "write")));
		} else {
			return openapi;
		}
	}

	protected boolean enableAuthComponent() {
		return false;
	}

}
