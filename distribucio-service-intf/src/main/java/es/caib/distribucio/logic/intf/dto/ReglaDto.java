/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació d'una regla per a gestionar anotacions de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ReglaDto extends AuditoriaDto {

	private Long id;
	
	private String nom;
	private String descripcio;

//	private Date createdDate;
//	private Date lastModifiedDate;

	// ------------- FILRE ----------------------
	private String assumpteCodiFiltre;
	private String procedimentCodiFiltre;
	private String serveiCodiFiltre;
	private UnitatOrganitzativaDto unitatOrganitzativaFiltre;
	private String codiAndNomUnitatOrganitzativa;
	private String estatUnitatOrganitzativa;
	private Long bustiaFiltreId;
	private String bustiaFiltreNom;
	private ReglaPresencialEnumDto presencial;

	// ------------- ACCIO  ----------------------
	private ReglaTipusEnumDto tipus;
	private Long bustiaDestiId;
	private String bustiaDestiNom;
	private Long backofficeDestiId;
	private String backofficeDestiNom;
	private Long unitatDestiId;
	private String unitatDestiNom;
	
	private int ordre;
	private boolean activa;
	
	private Long entitatId;
	private String entitatNom;
	

	private UnitatOrganitzativaDto unitatDesti;

	private boolean aturarAvaluacio;

	public String getCodiAndNomUnitatOrganitzativa() {
		if (unitatOrganitzativaFiltre!=null) {
			return unitatOrganitzativaFiltre.getCodiAndNom();
		} else {
			return "";
		}
	}
	public String getEstatUnitatOrganitzativa() {
		if (unitatOrganitzativaFiltre!=null) {
			return unitatOrganitzativaFiltre.getEstat();
		} else {
			return "";
		}
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ReglaDto other = (ReglaDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
