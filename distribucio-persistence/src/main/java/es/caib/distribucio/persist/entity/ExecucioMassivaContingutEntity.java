package es.caib.distribucio.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ElementTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@Builder(builderMethodName = "hiddenBuilder")
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = BaseConfig.DB_PREFIX + "execucio_massiva_cont")
@EntityListeners(AuditingEntityListener.class)
public class ExecucioMassivaContingutEntity extends DistribucioAuditable<Long> {
	
	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	private ExecucioMassivaContingutEstatDto estat;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_creacio", nullable = false)
	private Date dataCreacio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inici")
	private Date dataInici;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_final")
	private Date dataFi;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "execucio_massiva_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "execucio_massiva_contingut_massiva_fk"))
	private ExecucioMassivaEntity execucioMassiva;
	
	@Column(name = "element_id", nullable = false)
	private Long elementId;
	
	@Column(name = "element_nom")
	private String elementNom;
	
	@Column(name = "element_tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	private ElementTipusEnumDto elementTipus;
	
	@Column(name = "error")
	private String error;
	
	@Column(name = "missatge")
	private String missatge;
	
	@Column(name = "ordre", nullable = false)
	private int ordre;
	
	public void updateCancelat() {
		this.estat = ExecucioMassivaContingutEstatDto.CANCELADA;
	}

	public void updatePausat() {
		this.estat = ExecucioMassivaContingutEstatDto.PAUSADA;
	}

	public void updatePendent() {
		this.estat = ExecucioMassivaContingutEstatDto.PENDENT;
	}
	
	public void updateMissatge(String missatge) {
		this.missatge = missatge;
	}
	
	public void updateError(String error) {
		this.estat = ExecucioMassivaContingutEstatDto.ERROR;
		this.error = error;
	}
	
	public void updateProcessant(Date dataInici) {
		this.estat = ExecucioMassivaContingutEstatDto.PROCESSANT;
		this.dataInici = dataInici;
	}
	
	public void updateFinalitzat(Date dataFi) {
		this.estat = ExecucioMassivaContingutEstatDto.FINALITZADA;
		this.dataFi = dataFi;
	}

	public boolean isCancelat() {
		return this.getEstat().equals(ExecucioMassivaContingutEstatDto.CANCELADA);
	}
	
	public boolean isPausat() {
		return this.getEstat().equals(ExecucioMassivaContingutEstatDto.PAUSADA);
	}
	
}
