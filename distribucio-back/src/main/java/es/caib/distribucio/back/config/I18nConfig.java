/**
 * 
 */
package es.caib.distribucio.back.config;

import java.util.Locale;

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

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		return localeResolver;
	}

	@Override
	protected String[] getBasenames() {
		return new String[] {
				"messages"
		};
	}

	@Override
	protected Locale getDefaultLocale() {
		return Locale.forLanguageTag("ca");
	}

}
