package es.caib.distribucio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Classe principal de distribucio-ws per executar amb SpringBoot.
 * 
 * @author Límit Tecnologies
 */
@ConditionalOnNotWarDeployment
@SpringBootApplication
@ComponentScan
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class DistribucioWsBootApp {

	public static void main(String[] args) {
		SpringApplication.run(DistribucioWsBootApp.class, args);
	}

}
