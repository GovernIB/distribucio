package es.caib.distribucio.logic.config;

import java.util.Collections;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

/**
 * Broker JMS intern (Artemis encastat) que fa de pont entre la capa de negoci i les connexions
 * SSE obertes.
 * <p/>
 * Cal perquè les dues bandes no comparteixen context de Spring quan es desplega l'EAR: els
 * canvis d'avisos passen als beans de {@code logic}, que viuen al context que crea
 * {@code EjbContextConfig}, mentre que els {@code SseEmitter} viuen al context del WAR
 * {@code distribucio-back}. Amb un broker {@code vm://0} -- dins la mateixa JVM, sense
 * persistència ni seguretat-- el primer hi publica i el segon hi escolta amb {@code @JmsListener}.
 * <p/>
 * Aquesta classe és al package {@code logic}, de manera que només s'instancia una vegada: en mode
 * EAR el creen els EJBs (els WARs exclouen {@code logic} del seu escaneig) i en mode Spring Boot
 * viu al context únic de l'aplicació. Els beans equivalents del costat client són a
 * {@code es.caib.distribucio.back.config.JmsClientConfig}.
 *
 * @author Límit Tecnologies
 */
@Configuration
@EnableJms
public class JmsConfig {

	@Value("${es.caib.distribucio.jms.broker.url:vm://0}")
	private String brokerUrl;

	@Bean(initMethod = "start", destroyMethod = "stop")
	public EmbeddedActiveMQ createEmbeddedBroker() throws Exception {
		EmbeddedActiveMQ embeddedBroker = new EmbeddedActiveMQ();
		org.apache.activemq.artemis.core.config.Configuration configuration = new ConfigurationImpl().
				setPersistenceEnabled(false).
				setSecurityEnabled(false).
				addAcceptorConfiguration("invm", brokerUrl);
		// Sense adreça de missatges morts ni d'expiració Artemis avisa a cada arrencada (AMQ222165 i
		// AMQ222166). La mateixa configuració per a totes les cues ("#").
		AddressSettings addressSettings = new AddressSettings().
				setDeadLetterAddress(SimpleString.toSimpleString("DLQ.DISTRIBUCIO")).
				setExpiryAddress(SimpleString.toSimpleString("Expiry.DISTRIBUCIO")).
				setMaxDeliveryAttempts(5);
		Map<String, AddressSettings> addressesSettings = Collections.singletonMap("#", addressSettings);
		configuration.setAddressesSettings(addressesSettings);
		embeddedBroker.setConfiguration(configuration);
		return embeddedBroker;
	}

	@Bean
	public ConnectionFactory connectionFactory() throws JMSException {
		return new ActiveMQConnectionFactory(brokerUrl);
	}

	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		return new JmsTemplate(connectionFactory);
	}

}
