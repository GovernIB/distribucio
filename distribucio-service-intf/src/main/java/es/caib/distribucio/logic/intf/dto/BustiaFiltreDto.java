/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class BustiaFiltreDto implements Serializable {

	private String unitatCodi;
	private String nom;
	private String codiUnitatSuperior;
	// if the obsolete is true we look for the busties of extinguished or anulated unitats  
	private Boolean unitatObsoleta;
	private Long unitatId;
	private String numeroOrigen;
	private Boolean perDefecte;
	private Boolean activa;
    private Boolean permis;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
