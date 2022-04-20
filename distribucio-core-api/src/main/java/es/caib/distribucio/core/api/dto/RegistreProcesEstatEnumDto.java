/**
 * 
 */
package es.caib.distribucio.core.api.dto;

/**
 * Enumeració amb els possibles valors de l'estat de processament
 * d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreProcesEstatEnumDto {
	ARXIU_PENDENT,
	REGLA_PENDENT,
	BUSTIA_PENDENT,
	BUSTIA_PROCESSADA,
	BACK_PENDENT,
	BACK_COMUNICADA,
	BACK_REBUDA,
	BACK_PROCESSADA,
	BACK_REBUTJADA,
	BACK_ERROR
}
