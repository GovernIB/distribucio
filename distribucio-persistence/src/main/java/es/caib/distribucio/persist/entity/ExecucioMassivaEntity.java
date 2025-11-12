package es.caib.distribucio.persist.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaEstatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaTipusDto;

@Getter
@Entity
@Builder(builderMethodName = "hiddenBuilder")
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = BaseConfig.DB_PREFIX + "execucio_massiva")
@EntityListeners(AuditingEntityListener.class)
public class ExecucioMassivaEntity extends DistribucioAuditable<Long> {

	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	private ExecucioMassivaTipusDto tipus;

    @Setter
	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	private ExecucioMassivaEstatDto estat;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_creacio", nullable = false)
	private Date dataCreacio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inici")
	private Date dataInici;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_final")
	private Date dataFi;

	@ManyToOne(optional = false)
	@JoinColumn(name = "entitat_id")
	private EntitatEntity entitat;

	@ManyToOne(optional = false)
	@JoinColumn(name = "usuari_codi")
	private UsuariEntity usuari;
	
	@Builder.Default
	@OneToMany(
			mappedBy = "execucioMassiva",
			cascade = {CascadeType.ALL},
			fetch = FetchType.EAGER)
	private List<ExecucioMassivaContingutEntity> continguts = new ArrayList<ExecucioMassivaContingutEntity>();
	
	@Column(name = "parametres")
	private String parametres;

	public void addContingut(ExecucioMassivaContingutEntity execucioMassivaContingut) {
		getContinguts().add(execucioMassivaContingut);
	}

	public void updateFinalitzat(Date dataFi) {
		this.estat = ExecucioMassivaEstatDto.FINALITZADA;
		this.dataFi = dataFi;
	}

	public void updateCancelat() {
        if (!ExecucioMassivaEstatDto.FINALITZADA.equals(estat)) {
            this.estat = ExecucioMassivaEstatDto.CANCELADA;

            for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : continguts) {
                if (execucioMassivaContingutEntity.getError() == null
                        && !ExecucioMassivaContingutEstatDto.FINALITZADA.equals(execucioMassivaContingutEntity.getEstat()))
                    execucioMassivaContingutEntity.updateCancelat();
            }
        }
	}

	public void updateProcessant(Date dataInici) {
		this.estat = ExecucioMassivaEstatDto.PROCESSANT;
		this.dataInici = dataInici;
	}

	public void updatePausat() {
        if (!ExecucioMassivaEstatDto.FINALITZADA.equals(estat)) {
            this.estat = ExecucioMassivaEstatDto.PAUSADA;

            for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : continguts) {
                if (execucioMassivaContingutEntity.getError() == null
                        && !ExecucioMassivaContingutEstatDto.FINALITZADA.equals(execucioMassivaContingutEntity.getEstat()))
                    execucioMassivaContingutEntity.updatePausat();
            }
        }
	}

	public void updatePendent() {
        if (!ExecucioMassivaEstatDto.FINALITZADA.equals(estat)) {
            this.estat = ExecucioMassivaEstatDto.PENDENT;

            for (ExecucioMassivaContingutEntity execucioMassivaContingutEntity : continguts) {
                if (execucioMassivaContingutEntity.getError() == null
                        && !ExecucioMassivaContingutEstatDto.FINALITZADA.equals(execucioMassivaContingutEntity.getEstat()))
                    execucioMassivaContingutEntity.updatePendent();
            }
        }
	}
	
}