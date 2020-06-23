/**
 * 
 */
package es.caib.distribucio.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.audit.DistribucioAuditable;

/**
 * Classe del model de dades que representa el registre d'una acció
 * feta sobre un contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "dis_cont_log")
@EntityListeners(AuditingEntityListener.class)
public class ContingutLogEntity extends DistribucioAuditable<Long> {

	/** Llargada màxima del paràmetre */
	private static final int PARAM_MAX_LENGTH = 255;

	@Column(name = "tipus", length = 30, nullable = false)
	@Enumerated(EnumType.STRING)
	private LogTipusEnumDto tipus;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "contingut_id")
	protected ContingutEntity contingut;
	@Column(name = "objecte_id", length = 64)
	private String objecteId;
	@Column(name = "objecte_tipus", length = 12)
	@Enumerated(EnumType.STRING)
	private LogObjecteTipusEnumDto objecteTipus;
	@Column(name = "objecte_log_tipus", length = 30)
	@Enumerated(EnumType.STRING)
	private LogTipusEnumDto objecteLogTipus;
	@Column(name = "param1", length = 256)
	private String param1;
	@Column(name = "param2", length = 256)
	private String param2;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "contmov_id")
	@ForeignKey(name = "dis_contmov_contlog_fk")
	protected ContingutMovimentEntity contingutMoviment;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = "dis_pare_contlog_fk")
	protected ContingutLogEntity pare;



	public LogTipusEnumDto getTipus() {
		return tipus;
	}
	public ContingutEntity getContingut() {
		return contingut;
	}
	public ContingutMovimentEntity getContingutMoviment() {
		return contingutMoviment;
	}
	public String getObjecteId() {
		return objecteId;
	}
	public LogObjecteTipusEnumDto getObjecteTipus() {
		return objecteTipus;
	}
	public LogTipusEnumDto getObjecteLogTipus() {
		return objecteLogTipus;
	}
	public String getParam1() {
		return param1;
	}
	public String getParam2() {
		return param2;
	}
	public ContingutLogEntity getPare() {
		return pare;
	}

	public void updateParams(
			String param1,
			String param2) {
		this.param1 = StringUtils.abbreviate(param1, PARAM_MAX_LENGTH);
		this.param2 = StringUtils.abbreviate(param2, PARAM_MAX_LENGTH);
	}

	public static Builder getBuilder(
			LogTipusEnumDto tipus,
			ContingutEntity contingut) {
		return new Builder(
				tipus,
				contingut);
	}
	public static class Builder {
		ContingutLogEntity built;
		Builder(
				LogTipusEnumDto tipus,
				ContingutEntity contingut) {
			built = new ContingutLogEntity();
			built.tipus = tipus;
			built.contingut = contingut;
		}
		public Builder objecte(Persistable<? extends Serializable> objecte) {
			built.objecteId = objecte.getId().toString();
			return this;
		}
		public Builder objecteTipus(LogObjecteTipusEnumDto objecteTipus) {
			built.objecteTipus = objecteTipus;
			return this;
		}
		public Builder objecteLogTipus(LogTipusEnumDto objecteLogTipus) {
			built.objecteLogTipus = objecteLogTipus;
			return this;
		}
		public Builder param1(String param1) {
			built.param1 = StringUtils.abbreviate(param1, PARAM_MAX_LENGTH);
			return this;
		}
		public Builder param2(String param2) {
			built.param2 = StringUtils.abbreviate(param2, PARAM_MAX_LENGTH);
			return this;
		}
		public Builder pare(ContingutLogEntity pare) {
			built.pare = pare;
			return this;
		}
		public Builder contingutMoviment(ContingutMovimentEntity contingutMoviment) {
			built.contingutMoviment = contingutMoviment;
			return this;
		}
		public ContingutLogEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
