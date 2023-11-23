package es.caib.distribucio;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Classe principal de l'aplicació distribucio per a executar amb el WAR.
 * 
 * @author Límit Tecnologies
 */
@Slf4j
@ConditionalOnWarDeployment
@SpringBootApplication(exclude = {
		/*SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class,*/
		DataSourceAutoConfiguration.class, 
		DataSourceTransactionManagerAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		LiquibaseAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		WebSocketServletAutoConfiguration.class,
		JerseyServerMetricsAutoConfiguration.class,
})
@ComponentScan(
		basePackages = { BaseConfig.BASE_PACKAGE },
		excludeFilters = {
				@ComponentScan.Filter(
						type = FilterType.REGEX,
						pattern = {
								"es\\.caib\\." + BaseConfig.APP_NAME + "\\.logic\\..*",
								"es\\.caib\\." + BaseConfig.APP_NAME + "\\.persist\\..*",
								"es\\.caib\\." + BaseConfig.APP_NAME + "\\.ejb\\..*" })
		})
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties",
				"file://${" + BaseConfig.APP_PROPERTIES + "}",
				"file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}" })
public class DistribucioApiExternaApp extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DistribucioApiExternaApp.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		try {
			Manifest manifest = new Manifest(servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"));
			Attributes attributes = manifest.getMainAttributes();
			String version = attributes.getValue("Implementation-Version");
			String buildTimestamp = attributes.getValue("Build-Timestamp");
			log.info("Carregant l'aplicació " + BaseConfig.APP_NAME + "-api-externa versió " + version + " generada en data " + buildTimestamp);
		} catch (IOException ex) {
			throw new ServletException("Couldn't read MANIFEST.MF", ex);
		}
		super.onStartup(servletContext);
	}

}
