/**
 * 
 */
package es.caib.distribucio.core.api.registre;

/**
 * Enumeració amb els possibles valors de l'estat de processament
 * d'una anotació de registre:
 *   ARXIU_PENDENT: Pendent de guardar annexos a dins l'arxiu
 *   REGLA_PENDENT: Pendent d'aplicar regla
 *   BUSTIA_PENDENT: Pendent a bústia
 *   DISTRIBUIT_PROCESSAT: Distribuït marcant com a processat
 *   DISTRIBUIT_BACKOFFICE: Distribuït amb backoffice
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreProcesEstatEnum {
	ARXIU_PENDENT,
	REGLA_PENDENT,
	BUSTIA_PENDENT,
	DISTRIBUIT_PROCESSAT,
	DISTRIBUIT_BACKOFFICE
}
