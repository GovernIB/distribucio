package es.caib.distribucio.back.config;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Costat client del broker JMS intern: connecta els {@code @JmsListener} de
 * {@code SseResourceController} amb el broker que crea
 * {@link es.caib.distribucio.logic.config.JmsConfig} a la capa de negoci.
 * <p/>
 * Tots els beans són {@code @ConditionalOnWarDeployment} perquè només calen en mode EAR, on el
 * WAR i la capa de negoci són contexts de Spring separats. En mode Spring Boot tot viu al mateix
 * context i els beans equivalents ja els aporta {@code JmsConfig}.
 *
 * @author Límit Tecnologies
 */
@Configuration
@EnableJms
public class JmsClientConfig {

	@Value("${es.caib.distribucio.jms.broker.url:vm://0}")
	private String brokerUrl;

	@Bean
	@ConditionalOnWarDeployment
	public ConnectionFactory connectionFactory() throws JMSException {
		return new ActiveMQConnectionFactory(brokerUrl);
	}

	/**
	 * Factory dels contenidors dels {@code @JmsListener}.
	 * <p/>
	 * Es declara explícitament per a no heretar la que configura Spring Boot per defecte: sobre
	 * JBoss el context té un gestor de transaccions JTA i Spring Boot l'injectaria al factory;
	 * llavors el contenidor degrada la caché de CACHE_AUTO a CACHE_NONE i crea i tanca connexió,
	 * sessió i consumidor a cada cicle de sondeig, amb el reguitzell de traces d'Artemis que això
	 * suposa. Amb CACHE_CONSUMER el consumidor es crea una sola vegada en desplegar.
	 * <p/>
	 * No s'hi configura cap gestor de transaccions: els listeners són {@code @Async} (retornen tot
	 * d'una i la feina es fa en un altre fil), de manera que la transacció ja es confirmava abans
	 * de fer res i no aportava cap garantia.
	 */
	@Bean
	@ConditionalOnWarDeployment
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
		return factory;
	}

}
