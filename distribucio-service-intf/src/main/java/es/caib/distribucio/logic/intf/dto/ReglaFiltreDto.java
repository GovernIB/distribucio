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
public class ReglaFiltreDto implements Serializable {

	private String unitatCodi;
	private String nom;
	private Long unitatId;
	private ReglaTipusEnumDto tipus;
	private Long backofficeId;
	private String codiSIA;
	private String codiServei;
	private String codiAssumpte;
	private Long bustiaId;
//	private boolean activa = true;
	private ReglaFiltreActivaEnumDto activa;
	private ReglaPresencialEnumDto presencial;

    private Long unitatDestiId;
    private Long bustiaDestiId;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
