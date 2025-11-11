/**
 * 
 */
package es.caib.distribucio.api.interna.config;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració de Springdoc OpenAPI.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration("apiInternaOpenApiConfig")
@SecurityScheme(
		type = SecuritySchemeType.HTTP,
		name = "basicAuth",
		scheme = "basic")
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
		OpenAPI openapi = new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
				.info(
				new Info().
				title("API interna de Distribució").
				description("API REST interna de Distribució").
				contact(new Contact().email("limit@limit.es")).
				version(version));
		return openapi;
	}

}
