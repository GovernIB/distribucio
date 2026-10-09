package es.caib.distribucio.logic.helper;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.distribucio.logic.intf.model.MetriquesResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Helper per a obtenir les mètriques de l'aplicació (timers, comptadors...) del registre de Dropwizard.
 *
 * @author Límit Tecnologies
 */
@Component
public class MetriquesHelper {

	@Autowired
	private MetricRegistry metricRegistry;

	/**
	 * Retorna les mètriques de l'aplicació en el format JSON de Dropwizard.
	 *
	 * @throws JsonProcessingException si no es poden serialitzar.
	 */
	public String getMetriquesJson() throws JsonProcessingException {
		return getMapper().writeValueAsString(metricRegistry);
	}

	/**
	 * Retorna les mètriques de l'aplicació amb la mateixa estructura (i noms de camp) que {@link #getMetriquesJson()}.
	 *
	 * @throws JsonProcessingException si no es poden serialitzar.
	 */
	public MetriquesResource.Metriques getMetriques() throws JsonProcessingException {
		ObjectMapper mapper = getMapper();
		return mapper.readValue(
				mapper.writeValueAsString(metricRegistry),
				MetriquesResource.Metriques.class);
	}

	private ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(
				new MetricsModule(
						TimeUnit.SECONDS,
						TimeUnit.MILLISECONDS,
						false));
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

}
