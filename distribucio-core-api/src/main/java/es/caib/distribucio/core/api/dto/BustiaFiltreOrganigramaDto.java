package es.caib.distribucio.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaFiltreOrganigramaDto implements Serializable {

	private String unitatCodiFiltre;
	private String nomFiltre;


	public String getUnitatCodiFiltre() {
		return unitatCodiFiltre;
	}

	public void setUnitatCodiFiltre(String unitatCodiFiltre) {
		this.unitatCodiFiltre = unitatCodiFiltre;
	}

	public String getNomFiltre() {
		return nomFiltre;
	}

	public void setNomFiltre(String nomFiltre) {
		this.nomFiltre = nomFiltre;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
