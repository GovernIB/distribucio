/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ServeiDto implements Comparable<ServeiDto>{

	private Long id;
	private String codi;
	private String nom;
	private String codiSia;
	private ServeiEstatEnumDto estat;
	private UnitatOrganitzativaDto unitatOrganitzativa;
	private EntitatDto entitat;
    private boolean comu;

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
