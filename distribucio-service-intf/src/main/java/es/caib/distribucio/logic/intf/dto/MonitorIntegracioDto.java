/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entrada al monitor d'integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class MonitorIntegracioDto extends AuditoriaDto {

	private Long id;
	private String codi;	
	private Date data;
	private String descripcio;
	private IntegracioAccioTipusEnumDto tipus;
	private Long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private String codiUsuari;
	private String codiEntitat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;
	private String numeroRegistre;

	private List<MonitorIntegracioParamDto> parametres = new ArrayList<MonitorIntegracioParamDto>();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
