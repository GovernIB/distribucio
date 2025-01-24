package es.caib.distribucio.logic.intf.dto;


import lombok.Getter;
import lombok.Setter;

/** Classe tenir l'estat d'actualització. Permet tractar 
 * les actualitzacions de procediments i serveis de les diferents entitats en transaccions separades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UpdateProgressDto {

	/** Estat de consulta o actualització per unitats. */
	public enum Estat {
		INICIALITZANT,
		ACTUALITZANT,
		FINALITZAT,
		ERROR
	}
	Estat estat;
	
	/** Número total d'elements que s'han de processar. */
	int total = 0;
	int processats = 0;
	
	/** Comptador de progrés de 0 a 100 per la barra de progrés. */
	int progres = 0;
	/** Missatge d'error. */
	String errorMsg;
	
	/// Comptadors pel resum
	public void incProcessats() { 
		processats++;
		if (total > 0) {
			progres = processats *100 / total;
		}
	}
}
