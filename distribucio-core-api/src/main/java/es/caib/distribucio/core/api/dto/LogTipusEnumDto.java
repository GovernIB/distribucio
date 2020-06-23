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
	CONSULTA,  //to delete
	MODIFICACIO, //used
	ELIMINACIO, //to delete
	RECUPERACIO, //to delete
	ELIMINACIODEF, // to delete
	ACTIVACIO, // used
	DESACTIVACIO, // used
	AGAFAR,// to delete
	ALLIBERAR, // to delete
	COPIA, // to delete
	MOVIMENT,// used in anotacio
	ENVIAMENT, // to delete
	REENVIAMENT,// used in anotacio
	PROCESSAMENT, //to delete
	TANCAMENT, // to delete
	REOBERTURA, // to delete
	ACUMULACIO,// to delete
	DISGREGACIO, // to delete
	PER_DEFECTE, // used
	PFIRMA_ENVIAMENT, // to delete
	PFIRMA_CANCELACIO, // to delete
	PFIRMA_CALLBACK, // to delete
	PFIRMA_FIRMA, //to delete
	PFIRMA_REBUIG,// to delete
	PFIRMA_REINTENT,// to delete
	ARXIU_CSV,// to delete
	ARXIU_CUSTODIAT, // to delete
	CUSTODIA_CANCELACIO, //to delete
	FIRMA_CLIENT, // to delete
	NOTIFICACIO_ENTREGADA,// to delete
	NOTIFICACIO_REBUTJADA,// to delete
	NOTIFICACIO_REINTENT,// to delete
	ENVIAMENT_EMAIL,// used
	MARCAMENT_PROCESSAT,// used
	DISTRIBUCIO, // used
	REGLA_APLICAR, // newly added
	BACK_REBUDA, // newly added
	BACK_PROCESSADA, // newly added
	BACK_REBUTJADA, // newly added
	BACK_ERROR // newly added
	
}
