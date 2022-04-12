/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MonitorIntegracioDto extends AuditoriaDto {

	private Long id;
	private String codi;	
	private Date dataEntrada;
	private String descripcio;
	private IntegracioAccioTipusEnumDto tipus;
	private long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private String codiUsuari;

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

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
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

	public long getTempsResposta() {
		return tempsResposta;
	}

	public void setTempsResposta(long tempsResposta) {
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
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
