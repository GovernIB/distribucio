/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;




/**
 * Informació d'una bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaDto extends ContingutDto {

	private String unitatCodi;
	private String unitatConselleriaCodi;
	private boolean activa;
	private boolean perDefecte;
	private UnitatOrganitzativaDto unitatOrganitzativa;

	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private String pareNom;
	
	public boolean isUnitatObsoleta() {
		if (unitatOrganitzativa.getEstat().equals("E") || unitatOrganitzativa.getEstat().equals("A") || unitatOrganitzativa.getEstat().equals("T")) {
			return true;
		} else {
			return false;
		}
	}
	public UnitatOrganitzativaDto getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public void setUnitatOrganitzativa(UnitatOrganitzativaDto unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
	}
	public String getUnitatConselleriaCodi() {
		return unitatConselleriaCodi;
	}
	public void setUnitatConselleriaCodi(String unitatConselleriaCodi) {
		this.unitatConselleriaCodi = unitatConselleriaCodi;
	}
	public boolean isActiva() {
		return activa;
	}
	public boolean isInactiva() {
		return !activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public boolean isPerDefecte() {
		return perDefecte;
	}
	public void setPerDefecte(boolean perDefecte) {
		this.perDefecte = perDefecte;
	}
	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
	}


	public String getPareNom() {
		return pareNom;
	}
	public void setPareNom(String pareNom) {
		this.pareNom = pareNom;
	}
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
