/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Resutat de la classificació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ClassificacioResultatDto {

	private ClassificacioResultatEnumDto resultat;
	private String bustiaNom;
	private UnitatOrganitzativaDto bustiaUnitatOrganitzativa;
    private String backofficeDesti;

	public static enum ClassificacioResultatEnumDto {
		SENSE_CANVIS,
		REGLA_BUSTIA,
		REGLA_BACKOFFICE,
		REGLA_ERROR,
		REGLA_UNITAT,
		TITOL_MODIFICAT
	}

}
