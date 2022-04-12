/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entrada al monitor d'integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MonitorIntegracioDto extends AuditoriaDto {

	private Long id;
	private String codi;	
	private Date data;
	private String descripcio;
	private IntegracioAccioTipusEnumDto tipus;
	private Long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private String codiUsuari;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;
	
	private List<MonitorIntegracioParamDto> parametres = new ArrayList<MonitorIntegracioParamDto>();

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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public IntegracioAccioTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(IntegracioAccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public Long getTempsResposta() {
		return tempsResposta;
	}

	public void setTempsResposta(Long tempsResposta) {
		this.tempsResposta = tempsResposta;
	}

	public IntegracioAccioEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(IntegracioAccioEstatEnumDto estat) {
		this.estat = estat;
	}

	public String getCodiUsuari() {
		return codiUsuari;
	}

	public void setCodiUsuari(String codiUsuari) {
		this.codiUsuari = codiUsuari;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}

	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}

	public String getExcepcioMessage() {
		return excepcioMessage;
	}

	public void setExcepcioMessage(String excepcioMessage) {
		this.excepcioMessage = excepcioMessage;
	}

	public String getExcepcioStacktrace() {
		return excepcioStacktrace;
	}

	public void setExcepcioStacktrace(String excepcioStacktrace) {
		this.excepcioStacktrace = excepcioStacktrace;
	}

	public List<MonitorIntegracioParamDto> getParametres() {
		return parametres;
	}

	public void setParametres(List<MonitorIntegracioParamDto> parametres) {
		this.parametres = parametres;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
