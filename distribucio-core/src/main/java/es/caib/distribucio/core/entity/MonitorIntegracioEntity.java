/**
 * 
 */
package es.caib.distribucio.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.api.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.core.api.dto.IntegracioAccioTipusEnumDto;

/**
 * Classe del model de dades que representa un MonitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="dis_mon_int")
@EntityListeners(AuditingEntityListener.class)
public class MonitorIntegracioEntity extends AbstractPersistable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;
	
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	
	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	protected IntegracioAccioTipusEnumDto tipus;
	
	@Column(name = "temps_resposta")
	private Long tempsResposta;
	
	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private IntegracioAccioEstatEnumDto estat = IntegracioAccioEstatEnumDto.OK;
	
	@Column(name = "codi_usuari", length = 64, nullable = false)
	private String codiUsuari;
	
	@Column(name = "error_descripcio", length = 1024)
	private String errorDescripcio;
	
	@Column(name = "excepcio_msg", length = 1024)
	private String excepcioMessage;
	
	@Column(name = "excepcio_stacktrace", length = 2048)
	private String excepcioStacktrace;
	
	@OneToMany(
			mappedBy = "monitorIntegracio",
			orphanRemoval = true, 
			cascade={CascadeType.ALL})
	private List<MonitorIntegracioParamEntity> parametres = new ArrayList<MonitorIntegracioParamEntity>();


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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void update(
			String codi,
			Date data,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			IntegracioAccioEstatEnumDto estat,
			String codiUsuari) {
		this.codi = codi;
		this.data = data;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.tempsResposta = tempsResposta;
		this.estat = estat;
		this.codiUsuari = codiUsuari;
		
	}

	public void updateEstat(
			IntegracioAccioEstatEnumDto estat) {
		this.estat = estat;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus MonitorIntegracio.
	 * 
	 * @param codi
	 *            El valor de l'atribut codi.	
	 * @param descripcio
	 *            El valor de l'atribut descripcio.
	 * @param tipus
	 *            El valor de l'atribut tipus.
	 * @param tempsResposta
	 *            El valor de l'atribut tempsResposta.
 	 * @param estat
	 *            El valor de l'atribut estat.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,	
			Date data,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			IntegracioAccioEstatEnumDto estat,
			String codiUsuari,
			String errorDescripcio,
			String excepcioMessage,
			String excepcioStacktrace) {
		return new Builder(
				codi,		
				data,
				descripcio,
				tipus,
				tempsResposta,
				estat,
				codiUsuari,
				errorDescripcio,
				excepcioMessage,
				excepcioStacktrace);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit tecnologies
	 */
	public static class Builder {
		MonitorIntegracioEntity built;
		Builder(
				String codi,	
				Date data,
				String descripcio,
				IntegracioAccioTipusEnumDto tipus,
				long tempsResposta,
				IntegracioAccioEstatEnumDto estat,
				String codiUsuari,
				String errorDescripcio,
				String excepcioMessage,
				String excepcioStacktrace) {
			built = new MonitorIntegracioEntity();
	        built.codi = StringUtils.abbreviate(codi, 64);
			built.data = data;
	        built.descripcio = StringUtils.abbreviate(descripcio, 1024);
			built.tipus = tipus;
			built.tempsResposta = tempsResposta;
			built.estat = estat;
	        built.codiUsuari = StringUtils.abbreviate(codiUsuari, 64);
	        built.errorDescripcio = StringUtils.abbreviate(errorDescripcio, 1024);
	        built.excepcioMessage = StringUtils.abbreviate(excepcioMessage, 1024);
	        built.excepcioStacktrace = StringUtils.abbreviate(excepcioStacktrace, 2048);
		}
		public MonitorIntegracioEntity build() {
			return built;
		}
	}
	
	public List<MonitorIntegracioParamEntity> getParametres() {
		return parametres;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MonitorIntegracioEntity other = (MonitorIntegracioEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
