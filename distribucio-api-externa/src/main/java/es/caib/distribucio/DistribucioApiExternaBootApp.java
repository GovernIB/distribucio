package es.caib.distribucio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe principal de l'api interna de distribucio per executar amb SpringBoot.
 * 
 * @author Límit Tecnologies
 */
@ConditionalOnNotWarDeployment
@SpringBootApplication/*(exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class
})*/
@ComponentScan
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class DistribucioApiExternaBootApp {

	public static void main(String[] args) {
		SpringApplication.run(DistribucioApiExternaBootApp.class, args);
	}

}
