package es.caib.distribucio.back.config;

import es.caib.distribucio.back.base.config.BaseHateoasMessageResolverConfig;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * Configuració del MessageResolver per a spring-hateoas: resol els "_prompt"/"_placeholder" de
 * les propietats HAL-FORMS (el motor genèric de recursos) contra
 * {@code distribucio-back-rest-messages[_ca|_es].properties}.
 * <p>
 * Sense cap subclasse concreta d'aquesta classe base registrada com a bean, els labels dels
 * camps de formulari no es resolien mai (la classe abstracta mai s'instancia).
 *
 * @author Límit Tecnologies
 */
@Configuration
public class HateoasMessageResolverConfig extends BaseHateoasMessageResolverConfig {

	@Override
	protected String getBasename() {
		return "distribucio-back-rest-messages";
	}

	@Override
	protected Locale getDefaultLocale() {
		return Locale.forLanguageTag("ca");
	}

}
