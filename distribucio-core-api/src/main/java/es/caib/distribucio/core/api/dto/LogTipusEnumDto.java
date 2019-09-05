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
	CREACIO, // used in anotacio
	CONSULTA,
	MODIFICACIO, 
	ELIMINACIO,
	RECUPERACIO,
	ELIMINACIODEF,
	ACTIVACIO,
	DESACTIVACIO,
	AGAFAR,
	ALLIBERAR,
	COPIA,
	MOVIMENT,// used in anotacio
	ENVIAMENT,
	REENVIAMENT,// used in anotacio
	PROCESSAMENT,
	TANCAMENT,
	REOBERTURA,
	ACUMULACIO,
	DISGREGACIO,
	PER_DEFECTE,
	PFIRMA_ENVIAMENT,
	PFIRMA_CANCELACIO,
	PFIRMA_CALLBACK,
	PFIRMA_FIRMA,
	PFIRMA_REBUIG,
	PFIRMA_REINTENT,
	ARXIU_CSV,
	ARXIU_CUSTODIAT,
	CUSTODIA_CANCELACIO,
	FIRMA_CLIENT,
	NOTIFICACIO_ENTREGADA,
	NOTIFICACIO_REBUTJADA,
	NOTIFICACIO_REINTENT,
	ENVIAMENT_EMAIL,// used
	MARCAMENT_PROCESSAT,// used
	DISTRIBUCIO, // used
	REGLA_APLICAR, // newly added
	BACK_REBUDA, // newly added
	BACK_PROCESSADA, // newly added
	BACK_REBUTJADA, // newly added
	BACK_ERROR // newly added
	
}
