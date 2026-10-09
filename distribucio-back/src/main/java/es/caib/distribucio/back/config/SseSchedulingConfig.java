package es.caib.distribucio.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Activa la planificació i l'execució asíncrona al context del WAR.
 * <p/>
 * En mode EAR el WAR exclou els packages {@code logic} i {@code persist} del seu escaneig, de
 * manera que no hi arriben ni {@code SegonPlaConfig} ni {@code EventConfig}: sense això no
 * s'executaria el ping de manteniment de les connexions SSE
 * ({@code SseResourceController.pingClientsSse}) ni els {@code @JmsListener} anotats amb
 * {@code @Async}. En mode Spring Boot les dues anotacions ja hi són i repetir-les no té cap efecte.
 *
 * @author Límit Tecnologies
 */
@Configuration
@EnableAsync
@EnableScheduling
public class SseSchedulingConfig {

}
