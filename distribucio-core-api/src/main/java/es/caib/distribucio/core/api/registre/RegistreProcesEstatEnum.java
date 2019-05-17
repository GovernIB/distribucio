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
 *   BUSTIA_PROCESSADA: Distribuït marcant com a processat
 *   BACK_PENDENT: Anotació pendent d’enviar al backoffice
 *	 BACK_REBUDA: Anotació rebuda al backoffice
 *	 BACK_PROCESSADA: Anotació processada correctament pel backoffice
 *	 BACK_REBUTJADA: Anotació rebutjada pel backoffice
 *	 BACK_ERROR: Anotació processada al backoffice amb errors
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreProcesEstatEnum {
	ARXIU_PENDENT,
	REGLA_PENDENT,
	BUSTIA_PENDENT,
	BUSTIA_PROCESSADA,
	BACK_PENDENT,
	BACK_REBUDA,
	BACK_PROCESSADA,
	BACK_REBUTJADA,
	BACK_ERROR
}
