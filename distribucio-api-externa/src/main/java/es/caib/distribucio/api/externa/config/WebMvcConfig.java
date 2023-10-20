/**
 * 
 */
package es.caib.distribucio.api.externa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuració de Spring MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration("apiExternaWebMvcConfig")
@DependsOn("apiExternaEjbClientConfig")
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	}

}
