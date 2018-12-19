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
	DISTRIBUIT_PROCESSAT,
	DISTRIBUIT_BACKOFFICE
}
