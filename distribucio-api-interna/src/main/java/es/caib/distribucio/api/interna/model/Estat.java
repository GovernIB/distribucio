package es.caib.distribucio.api.interna.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Enumeració amb els possibles valors del estat d’una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public enum Estat {
	PENDENT,
	REBUDA,
	PROCESSADA,
	REBUTJADA,
	ERROR
}
