/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;


/**
 * Informació d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiDto implements Comparable<ServeiDto>{

	private Long id;
	private String codi;
	private String nom;
	private String codiSia;
	private ServeiEstatEnumDto estat;
	private UnitatOrganitzativaDto unitatOrganitzativa;
	private EntitatDto entitat;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public ServeiEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(ServeiEstatEnumDto estat) {
		this.estat = estat;
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
	public String getCodiNomEstat() {
		return codiSia + " - " + nom + " => " + estat;
	}
	
	@Override
	public int compareTo(ServeiDto o) {
		int ret;
		try {
			ret = ((Long.valueOf(codiSia)).compareTo(Long.valueOf(o.getCodiSia())));
		} catch( Exception e) {
			ret = codiSia.compareTo(o.getCodiSia());
		}
		return ret;
	}

}
