/**
 * 
 */
package es.caib.distribucio.back.config;

import java.util.Locale;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import es.caib.distribucio.back.base.config.BaseMessageSourceConfig;

/**
 * Configuració multiidioma de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Configuration
public class I18nConfig extends BaseMessageSourceConfig {

//	@Bean
//	public LocaleResolver localeResolver() {
//		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//		return localeResolver;
//	}

	/**
	 * Bundle de missatges de l'aplicació (per darrere hi ha encara {@code base-boot-messages},
	 * que hi afegeix la classe base).
	 * <p>
	 * {@code distribucio-service-messages} viu al mòdul distribucio-service-intf i és l'únic
	 * bundle de missatges de l'aplicació: el fan servir tant la interfície JSP com la capa de
	 * negoci, i tant en mode Spring Boot (un sol context, aquest MessageSource) com en mode EAR
	 * (context dels EJBs, {@code EjbContextConfig.messageSource()}, que apunta al mateix
	 * basename). Amb un sol bundle, les dues modalitats i les dues interfícies veuen sempre el
	 * mateix text; quan n'hi havia dos, les claus repetides divergien.
	 * <p>
	 * Es declara a distribucio-service-intf perquè és l'únic mòdul visible des de tots dos
	 * contextos: és dependència directa de distribucio-back i, en mode EAR, el seu jar va a
	 * {@code EAR/lib}. A distribucio-back només hi queden els {@code _prompt} del motor genèric
	 * de recursos ({@code distribucio-back-rest-messages}).
	 */
	@Override
	protected String[] getBasenames() {
		return new String[] {
				"distribucio-service-messages"
		};
	}

	@Override
	protected Locale getDefaultLocale() {
		return Locale.forLanguageTag(BaseConfig.DEFAULT_LOCALE);
	}

}
