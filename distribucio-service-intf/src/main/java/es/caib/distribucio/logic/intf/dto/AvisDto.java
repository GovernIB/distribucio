/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class AvisDto {

	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;
    private EntitatDto entitat;
    private Long entitatId;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
