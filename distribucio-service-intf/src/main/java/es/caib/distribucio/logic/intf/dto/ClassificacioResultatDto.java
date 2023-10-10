/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

/**
 * Resutat de la classificació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClassificacioResultatDto {

	private ClassificacioResultatEnumDto resultat;
	private String bustiaNom;
	private UnitatOrganitzativaDto bustiaUnitatOrganitzativa;

	public ClassificacioResultatEnumDto getResultat() {
		return resultat;
	}
	public void setResultat(ClassificacioResultatEnumDto resultat) {
		this.resultat = resultat;
	}
	public String getBustiaNom() {
		return bustiaNom;
	}
	public void setBustiaNom(String bustiaNom) {
		this.bustiaNom = bustiaNom;
	}
	public UnitatOrganitzativaDto getBustiaUnitatOrganitzativa() {
		return bustiaUnitatOrganitzativa;
	}
	public void setBustiaUnitatOrganitzativa(UnitatOrganitzativaDto bustiaUnitatOrganitzativa) {
		this.bustiaUnitatOrganitzativa = bustiaUnitatOrganitzativa;
	}

	public static enum ClassificacioResultatEnumDto {
		SENSE_CANVIS,
		REGLA_BUSTIA,
		REGLA_BACKOFFICE,
		REGLA_ERROR,
		REGLA_UNITAT,
		TITOL_MODIFICAT
	}

}
