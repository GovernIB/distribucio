/**
 *
 */
package es.caib.distribucio.back.config;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * En mode standalone (Spring Boot embedded, sense WAR real desplegat -- {@code DistribucioBackBootApp})
 * {@code src/main/webapp} no forma part del classpath/artefacte executat, així que per defecte
 * Tomcat no en resol cap recurs: ni {@code /WEB-INF/decorators.xml} (SiteMesh) ni les pròpies
 * vistes {@code /WEB-INF/jsp/**}. Fixar el "document root" de Tomcat al directori font és el
 * truc habitual per poder executar JSPs en mode Spring Boot embedded sense empaquetar abans el
 * WAR. Sota JBoss/WAR real ({@code DistribucioBackApp}) no cal -- ja hi són com a arrel del
 * desplegament -- d'aquí el {@code @ConditionalOnNotWarDeployment}.
 * <p>
 * Classe deliberadament mínima i sense cap altra dependència: Spring Boot instancia totes les
 * beans {@link WebServerFactoryCustomizer} molt aviat (durant la creació del propi
 * {@code TomcatServletWebServerFactory}, abans que existeixi el {@code ServletContext}). Si
 * aquesta bean visqués en una classe de configuració amb altres dependències (com
 * {@link WebMvcConfig}), instanciar-la arrossegaria tota la cadena de dependències d'aquella
 * classe -- incloent-hi, en aquest projecte, tota la capa de serveis fins a
 * {@code WebSecurityConfig} -- massa aviat, abans que el {@code ServletContext} estigui
 * disponible.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
public class TomcatWebappDocumentRootConfig {

	@Bean
	@ConditionalOnNotWarDeployment
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatWebappDocumentRootCustomizer() {
		return factory -> {
			File webappDir = findWebappDir();
			if (webappDir != null) {
				log.info("Fixant el document root de Tomcat a {}", webappDir);
				factory.setDocumentRoot(webappDir);
			} else {
				log.warn("No s'ha trobat el directori src/main/webapp -- SiteMesh i les vistes JSP fallaran en mode standalone");
			}
		};
	}

	/**
	 * Deriva la ubicació de {@code src/main/webapp} a partir d'on s'han carregat les classes
	 * d'aquest mateix mòdul ({@code <mòdul>/target/classes/}), en lloc del directori de treball
	 * del procés (que depèn de com/des d'on s'arrenqui l'aplicació i, per tant, no és fiable).
	 *
	 * @return el directori {@code src/main/webapp} si s'ha pogut localitzar i existeix, o null en
	 *         cas contrari (p.ex. si les classes es carreguen des d'un JAR/WAR empaquetat).
	 */
	private File findWebappDir() {
		CodeSource codeSource = TomcatWebappDocumentRootConfig.class.getProtectionDomain().getCodeSource();
		if (codeSource == null) {
			return null;
		}
		File classesDir;
		try {
			classesDir = new File(codeSource.getLocation().toURI());
		} catch (URISyntaxException ex) {
			log.warn("No s'ha pogut resoldre la ubicació de les classes per localitzar src/main/webapp", ex);
			return null;
		}
		// classesDir apunta a <mòdul>/target/classes -- només és fiable si és un directori real
		// (no un JAR/WAR empaquetat, cas en el qual no cal aplicar aquest ajust).
		if (!classesDir.isDirectory()) {
			return null;
		}
		File moduleDir = classesDir.getParentFile() != null ? classesDir.getParentFile().getParentFile() : null;
		if (moduleDir == null) {
			return null;
		}
		File webappDir = new File(moduleDir, "src/main/webapp");
		return webappDir.isDirectory() ? webappDir : null;
	}

}
