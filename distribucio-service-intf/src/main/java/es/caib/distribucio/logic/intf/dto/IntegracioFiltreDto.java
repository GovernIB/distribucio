package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del filtre del 
 * monitor d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class IntegracioFiltreDto implements Serializable {

    private String codi;
    private String entitat;
    private IntegracioAccioEstatEnumDto estat;
    private IntegracioAccioTipusEnumDto tipus;
    private String descripcio;
    private Date dataInici;
    private Date dataFi;
    private String usuari;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	private static final long serialVersionUID = -248365773192710830L;

}
