/**
 * 
 */
package es.caib.distribucio.core.api.dto;


/**
 * Informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentDto implements Comparable<ProcedimentDto>{

	private String codi;
	private String nom;
	private String codiSia;
	private UnitatOrganitzativaDto unitatOrganitzativa;
	private EntitatDto entitat;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodiSia() {
		return codiSia;
	}
	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}
	public UnitatOrganitzativaDto getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public void setUnitatOrganitzativa(UnitatOrganitzativaDto unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public String getCodiNom() {
		return codiSia + " - " + nom;
	}
	
	@Override
	public int compareTo(ProcedimentDto o) {
		int ret;
		try {
			ret = ((Long.valueOf(codiSia)).compareTo(Long.valueOf(o.getCodiSia())));
		} catch( Exception e) {
			ret = codiSia.compareTo(o.getCodiSia());
		}
		return ret;
	}

}
