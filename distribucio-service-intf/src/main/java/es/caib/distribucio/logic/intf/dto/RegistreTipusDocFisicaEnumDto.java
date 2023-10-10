/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

/**
 * Enumeració amb els possibles estats del tipus de documentació adjunta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreTipusDocFisicaEnumDto {
	PAPER, // Documentació adjunta en suport PAPER (o altres suports)
	DIGIT_PAPER, // Documentació adjunta digitalitzada i complementàriament en paper
	DIGIT; // Documentació adjunta digitalitzada
	
	public int getValue() {
	    return ordinal() + 1;
	}
}

