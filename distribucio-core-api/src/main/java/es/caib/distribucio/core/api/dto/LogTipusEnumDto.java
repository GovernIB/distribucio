/**
 * 
 */
package es.caib.distribucio.core.api.dto;


/**
 * Enumeració amb els possibles tipus d'accions de log.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum LogTipusEnumDto {
	CREACIO, 
	MODIFICACIO, 
	ACTIVACIO, 
	DESACTIVACIO, 
	MOVIMENT,
	REENVIAMENT,
	PER_DEFECTE, 
	ENVIAMENT_EMAIL,
	MARCAMENT_PROCESSAT,
	MARCAMENT_PENDENT,
	DISTRIBUCIO, 
	REGLA_APLICAR, 
	BACK_REBUDA, 
	BACK_PROCESSADA, 
	BACK_REBUTJADA, 
	BACK_ERROR,
	AGAFAR,
	ALLIBERAR,
	DUPLICITAT,
	SOBREESCRIURE
	
}
