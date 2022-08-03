package es.caib.distribucio.war.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@Configuration
@EnableSwagger
@ComponentScan(basePackages = "es.caib.distribucio.war.rest")
public class SwaggerConfig {

	private SpringSwaggerConfig springSwaggerConfig;
	 
    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }
 
    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .apiVersion("1.0")
                .ignoredParameterTypes(ResponseEntity.class)
                .includePatterns(".*/api/rest/regles/add", ".*/api/rest/regles/canviEstat", ".*/api/rest/regles/consultarRegla");
    }
 
	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(
				"API REST Distribucio",
				"API REST de Distribucio per a la creació automàtica de regles de distribucio a backoffices",
				"", 	// URL de temes de servei
				"limit@limit.es",
				"",		// Llicència
				""); 	// URL Llicència
				
		return apiInfo;
	}
	
	/**
	 * Class that provides your applications url context path
	 */
	@Bean
	public ApiPathProvider apiPathProvider() {
		ApiPathProvider apiPathProvider = new ApiPathProvider(); //"http://localhost:8080");
		apiPathProvider.setDefaultSwaggerPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
		return apiPathProvider;
	}
}
