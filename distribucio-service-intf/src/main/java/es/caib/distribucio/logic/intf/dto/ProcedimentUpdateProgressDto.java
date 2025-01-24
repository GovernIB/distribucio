package es.caib.distribucio.logic.intf.dto;


import lombok.Getter;
import lombok.Setter;

/** Classe per anar responent les línies d'informació en la consulta del progrés d'actualització de procediments. Permet tractar 
 * les actualitzacions en transaccions separades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ProcedimentUpdateProgressDto {

	/** Estat de consulta o actualització per unitats. */
	public enum Estat {
		INICIALITZANT,
		ACTUALITZANT,
		FINALITZAT,
		ERROR
	}
	Estat estat;
	
	/** Número total de procediments que s'han de processar. */
	int procedimentsTotal = 0;
	int procedimentsProcessats = 0;
	
	/** Comptador de progrés de 0 a 100 per la barra de progrés. */
	int progres = 0;
	/** Missatge d'error. */
	String errorMsg;
	
	/// Comptadors pel resum
	public void incUnitatsProcessades() { 
		procedimentsProcessats++;
		if (procedimentsTotal > 0) {
			progres = procedimentsProcessats *100 / procedimentsTotal;
		}
	}
}
