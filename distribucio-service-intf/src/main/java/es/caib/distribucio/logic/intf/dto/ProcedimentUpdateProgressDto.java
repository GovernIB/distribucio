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
	
	/** Número total d'unitats en l'arbre d'unitats. */
	int unitatsTotal = 0;
	int unitatsProcessades = 0;
	
	/** Comptador de progrés de 0 a 100 per la barra de progrés. */
	int progres = 0;
	/** Missatge d'error. */
	String errorMsg;
	
	/// Comptadors pel resum
	public void incUnitatsProcessades() { 
		unitatsProcessades++;
		if (unitatsTotal > 0) {
			progres = unitatsProcessades *100 / unitatsTotal;
		}
	}
}
