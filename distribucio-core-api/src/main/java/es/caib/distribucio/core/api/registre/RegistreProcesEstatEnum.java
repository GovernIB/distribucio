/**
 * 
 */
package es.caib.distribucio.core.api.registre;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeració amb els possibles valors de l'estat de processament
 * d'una anotació de registre:
 *   ARXIU_PENDENT: Pendent de guardar annexos a dins l'arxiu
 *   ARXIU_PENDENT_AMB_INTENTS_PENDENTS: Pendent de guardar annexos a dins l'arxiu amb intents pendents
 *   REGLA_PENDENT: Pendent d'aplicar regla
 *   BUSTIA_PENDENT: Pendent a bústia
 *   BUSTIA_PROCESSADA: Distribuït marcant com a processat
 *   BACK_PENDENT: Anotació pendent d’enviar al backoffice
 *   BACK_COMUNICADA: Anotació comunicada al backoffice
 *	 BACK_REBUDA: Anotació rebuda al backoffice
 *	 BACK_PROCESSADA: Anotació processada correctament pel backoffice
 *	 BACK_REBUTJADA: Anotació rebutjada pel backoffice
 *	 BACK_ERROR: Anotació processada al backoffice amb errors
 * 
 *  També conté mètodes estàtics per determinar si un estat és pendent o processat.
 *  
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreProcesEstatEnum {
	ARXIU_PENDENT,
	REGLA_PENDENT,
	BUSTIA_PENDENT,
	BUSTIA_PROCESSADA,
	BACK_PENDENT,
	BACK_COMUNICADA,
	BACK_REBUDA,
	BACK_PROCESSADA,
	BACK_REBUTJADA,
	BACK_ERROR;
	
	/** Llista d'estats pendents */
	public static final List<RegistreProcesEstatEnum> estatsPendents = new ArrayList<RegistreProcesEstatEnum>();	
	static {
		estatsPendents.add(BUSTIA_PENDENT);
		estatsPendents.add(ARXIU_PENDENT);
		estatsPendents.add(REGLA_PENDENT);
		estatsPendents.add(BACK_REBUTJADA);
		estatsPendents.add(BACK_ERROR);
	}
	
	/** Llista d'estats processats. */
	public static final List<RegistreProcesEstatEnum> estatsProcessats = new ArrayList<RegistreProcesEstatEnum>();	
	static {
		estatsProcessats.add(BUSTIA_PROCESSADA);
		estatsProcessats.add(BACK_PENDENT);
		estatsProcessats.add(BACK_COMUNICADA);
		estatsProcessats.add(BACK_REBUDA);
		estatsProcessats.add(BACK_PROCESSADA);

	}

	/** Mètode per consultar si un estat és pendent. */
	public static boolean isPendent(RegistreProcesEstatEnum estat) {
		return estatsPendents.contains(estat);
	}
	
	/** Mètode per consultar si un estat és pendent. */
	public static boolean isProcessat(RegistreProcesEstatEnum estat) {
		return 	estatsProcessats.contains(estat);
	}
	
	
	
	
}
