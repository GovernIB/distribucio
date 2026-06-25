/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que simula anotacio de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreSimulatDto {

	private Long unitatId;
	private Long bustiaId;
	private String assumpteCodi;
	private String procedimentCodi;
	private String serveiCodi;
	private String tramitCodi;
	private ReglaPresencialEnumDto presencial;

}
