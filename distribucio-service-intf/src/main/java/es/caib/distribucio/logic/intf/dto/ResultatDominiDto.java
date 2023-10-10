/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ResultatDominiDto {
	
	private int totalElements;
	private List<ResultatConsultaDto> resultat;
}
