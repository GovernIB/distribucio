/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;
import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;

/**
 * Informació del filtre d'annexos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class AnnexosFiltreDto implements Serializable {
	
	private String numero;
	private AnnexEstat arxiuEstat;
	private ArxiuFirmaTipusEnumDto tipusFirma;
	private String titol;
	private String fitxerNom;
	private String fitxerTipusMime;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private Integer numeroCopia;

    private Float midaMinima;
    private Float midaMaxima;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
