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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.api.dto.ContingutTipusEnumDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;

/**
 * Classe del model de dades que representa una anotació al
 * registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "dis_registre",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "dis_reg_mult_uk",
						columnNames = {
								"entitat_codi",
								"llibre_codi",
								"tipus",
								"numero",
								"data"})})
@EntityListeners(AuditingEntityListener.class)
public class RegistreEntity extends ContingutEntity {

	private static final int ERROR_MAX_LENGTH = 1000;

	@Column(name = "tipus", length = 1, nullable = false)
	private String registreTipus;
	@Column(name = "unitat_adm", length = 21, nullable = false)
	private String unitatAdministrativa;
	@Column(name = "unitat_adm_desc", length = 100)
	private String unitatAdministrativaDescripcio;
	@Column(name = "numero", length = 100, nullable = false)
	private String numero;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;
	@Column(name = "identificador", length = 100, nullable = false)
	private String identificador;
	@Column(name = "entitat_codi", length = 21, nullable = false)
	private String entitatCodi;
	@Column(name = "entitat_desc", length = 100)
	private String entitatDescripcio;
	@Column(name = "oficina_codi", length = 21, nullable = false)
	private String oficinaCodi;
	@Column(name = "oficina_desc", length = 100)
	private String oficinaDescripcio;
	@Column(name = "llibre_codi", length = 4, nullable = false)
	private String llibreCodi;
	@Column(name = "llibre_desc", length = 100)
	private String llibreDescripcio;
	@Column(name = "extracte", length = 240)
	private String extracte;
	@Column(name = "assumpte_tipus_codi", length = 16, nullable = false)
	private String assumpteTipusCodi;
	@Column(name = "assumpte_tipus_desc", length = 100)
	private String assumpteTipusDescripcio;
	@Column(name = "assumpte_codi", length = 16)
	private String assumpteCodi;
	@Column(name = "assumpte_desc", length = 100)
	private String assumpteDescripcio;
	@Column(name = "referencia", length = 16)
	private String referencia;
	@Column(name = "expedient_num", length = 80)
	private String expedientNumero;
	@Column(name = "expedient_arxiu_uuid", length = 100)
	private String expedientArxiuUuid;
	@Column(name = "num_orig", length = 80)
	private String numeroOrigen;
	@Column(name = "idioma_codi", length = 2, nullable = false)
	private String idiomaCodi;
	@Column(name = "idioma_desc", length = 100)
	private String idiomaDescripcio;
	@Column(name = "transport_tipus_codi", length = 2)
	private String transportTipusCodi;
	@Column(name = "transport_tipus_desc", length = 100)
	private String transportTipusDescripcio;
	@Column(name = "transport_num", length = 20)
	private String transportNumero;
	@Column(name = "usuari_codi", length = 20)
	private String usuariCodi;
	@Column(name = "usuari_nom", length = 80)
	private String usuariNom;
	@Column(name = "usuari_contacte", length = 160)
	private String usuariContacte;
	@Column(name = "aplicacio_codi", length = 20)
	private String aplicacioCodi;
	@Column(name = "aplicacio_versio", length = 15)
	private String aplicacioVersio;
	@Column(name = "docfis_codi", length = 1)
	private String documentacioFisicaCodi;
	@Column(name = "docfis_desc", length = 100)
	private String documentacioFisicaDescripcio;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Column(name = "exposa", length = 4000)
	private String exposa;
	@Column(name = "solicita", length = 4000)
	private String solicita;
	@Column(name = "motiu_rebuig", length = 1024)
	private String motiuRebuig;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "proces_data")
	private Date procesData;
	@Enumerated(EnumType.STRING)
	@Column(name = "proces_estat", length = 16, nullable = false)
	private RegistreProcesEstatEnum procesEstat;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_orig")
	private Date dataOrigen;
	@Column(name = "oficina_orig_codi", length = 21)
	private String oficinaOrigenCodi;
	@Column(name = "oficina_orig_desc", length = 100)
	private String oficinaOrigenDescripcio;
	@Enumerated(EnumType.STRING)
	@Column(name = "proces_estat_sistra", length = 16)
	private RegistreProcesEstatSistraEnum procesEstatSistra;
	@Column(name = "sistra_id_tram", length = 20)
	private String identificadorTramitSistra;
	@Column(name = "sistra_id_proc", length = 100)
	private String identificadorProcedimentSistra;
	@Column(name = "proces_error", length = ERROR_MAX_LENGTH)
	private String procesError;
	@Column(name = "proces_intents")
	private int procesIntents;
	@OneToMany(
			mappedBy = "registre",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<RegistreInteressatEntity> interessats = new ArrayList<RegistreInteressatEntity>();
	@OneToMany(
			mappedBy = "registre",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<RegistreAnnexEntity> annexos = new ArrayList<RegistreAnnexEntity>();
	@Column(name = "justificant_arxiu_uuid", length = 100)
	private String justificantArxiuUuid;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "regla_id")
	@ForeignKey(name = "dis_regla_registre_fk")
	private ReglaEntity regla;
	@Column(name = "llegida")
	private Boolean llegida;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_tancament")
	private Date dataTancament;
	@Column(name = "arxiu_tancat")
	private Boolean arxiuTancat;
	@Column(name = "arxiu_tancat_error")
	private Boolean arxiuTancatError;
	
	public RegistreTipusEnum getRegistreTipus() {
		return RegistreTipusEnum.valorAsEnum(registreTipus);
	}
	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public String getUnitatAdministrativaDescripcio() {
		return unitatAdministrativaDescripcio;
	}
	public String getNumero() {
		return numero;
	}
	public Date getData() {
		return data;
	}
	public Date getDataOrigen() {
		return dataOrigen;
	}
	public String getIdentificador() {
		return identificador;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}
	public String getOficinaCodi() {
		return oficinaCodi;
	}
	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}
	public String getOficinaOrigenCodi() {
		return oficinaOrigenCodi;
	}
	public String getOficinaOrigenDescripcio() {
		return oficinaOrigenDescripcio;
	}
	public String getLlibreCodi() {
		return llibreCodi;
	}
	public String getLlibreDescripcio() {
		return llibreDescripcio;
	}
	public String getExtracte() {
		return extracte;
	}
	public String getAssumpteTipusCodi() {
		return assumpteTipusCodi;
	}
	public String getAssumpteTipusDescripcio() {
		return assumpteTipusDescripcio;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public String getAssumpteDescripcio() {
		return assumpteDescripcio;
	}
	public String getReferencia() {
		return referencia;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public String getExpedientArxiuUuid() {
		return expedientArxiuUuid;
	}
	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public String getIdiomaDescripcio() {
		return idiomaDescripcio;
	}
	public String getTransportTipusCodi() {
		return transportTipusCodi;
	}
	public String getTransportTipusDescripcio() {
		return transportTipusDescripcio;
	}
	public String getTransportNumero() {
		return transportNumero;
	}
	public String getUsuariNom() {
		return usuariNom;
	}
	public String getUsuariContacte() {
		return usuariContacte;
	}
	public String getAplicacioCodi() {
		return aplicacioCodi;
	}
	public String getAplicacioVersio() {
		return aplicacioVersio;
	}
	public String getDocumentacioFisicaCodi() {
		return documentacioFisicaCodi;
	}
	public String getDocumentacioFisicaDescripcio() {
		return documentacioFisicaDescripcio;
	}
	public String getObservacions() {
		return observacions;
	}
	public String getExposa() {
		return exposa;
	}
	public String getSolicita() {
		return solicita;
	}
	public String getMotiuRebuig() {
		return motiuRebuig;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public Date getProcesData() {
		return procesData;
	}
	public RegistreProcesEstatEnum getProcesEstat() {
		return procesEstat;
	}
	public RegistreProcesEstatSistraEnum getProcesEstatSistra() {
		return procesEstatSistra;
	}
	public String getProcesError() {
		return procesError;
	}
	public Integer getProcesIntents() {
		return procesIntents;
	}
	public List<RegistreInteressatEntity> getInteressats() {
		return interessats;
	}
	public List<RegistreAnnexEntity> getAnnexos() {
		return annexos;
	}
	public String getJustificantArxiuUuid() {
		return justificantArxiuUuid;
	}
	public ReglaEntity getRegla() {
		return regla;
	}
	public Boolean getLlegida() {
		return llegida;
	}

	public void updateMotiuRebuig(
			String motiuRebuig) {
		this.motiuRebuig = motiuRebuig;
	}
	public void updateDataTancament(
			Date dataTancament) {
		this.dataTancament = dataTancament;
		this.arxiuTancat = false;
	}
	public void updateArxiuTancat(Boolean arxiuTancat){
		this.arxiuTancat = arxiuTancat;
	}
	public void updateArxiuTancatError(Boolean arxiuTancatError){
		this.arxiuTancatError = arxiuTancatError;
	}
	public void updateProces(
			RegistreProcesEstatEnum procesEstat,
			Exception exception) {
		this.procesData = new Date();
		if (procesEstat != null) {
			this.procesEstat = procesEstat;
			this.procesIntents = 0;
		}
		this.procesIntents++;
		if (exception != null) {
			String error = exception.getMessage() + ": " + ExceptionUtils.getRootCauseMessage(exception);
			this.procesError = StringUtils.abbreviate(error, ERROR_MAX_LENGTH);
		} else {
			this.procesError = null;
		}
	}
	public void updateProcesSistra(RegistreProcesEstatSistraEnum procesEstatSistra) {
		this.procesEstatSistra = procesEstatSistra;
	}
	public void updateIdentificadorTramitSistra(String identificadorTramit) {
		this.identificadorTramitSistra = identificadorTramit;
	}
	public void updateIdentificadorProcedimentSistra(String identificadorProcediment) {
		this.identificadorProcedimentSistra = identificadorProcediment;
	}
	public void updateLlegida(Boolean llegida) {
		this.llegida = llegida;
	}
	public void updateExpedientArxiuUuid(String expedientArxiuUuid) {
		this.expedientArxiuUuid = expedientArxiuUuid;
	}
	public void updateJustificantArxiuUuid(String justificantArxiuUuid) {
		this.justificantArxiuUuid = justificantArxiuUuid;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			String unitatAdministrativaDescripcio,
			String numero,
			Date data,
			String identificador,
			String extracte,
			String oficinaCodi,
			String llibreCodi,
			String assumpteTipusCodi,
			String idiomaCodi,
			RegistreProcesEstatEnum procesEstat,
			ContingutEntity pare) {
		return new Builder(
				entitat,
				tipus,
				unitatAdministrativa,
				unitatAdministrativaDescripcio,
				numero,
				data,
				identificador,
				extracte,
				oficinaCodi,
				llibreCodi,
				assumpteTipusCodi,
				idiomaCodi,
				procesEstat,
				pare);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		RegistreEntity built;
		Builder(
				EntitatEntity entitat,
				RegistreTipusEnum tipus,
				String unitatAdministrativa,
				String unitatdAministrativaDescripcio,
				String numero,
				Date data,
				String identificador,
				String extracte,
				String oficinaCodi,
				String llibreCodi,
				String assumpteTipusCodi,
				String idiomaCodi,
				RegistreProcesEstatEnum procesEstat,
				ContingutEntity pare) {
			built = new RegistreEntity();
			if (extracte != null) {
				built.nom = numero + " - " + extracte;
			} else {
				built.nom = numero;
			}
			built.entitat = entitat;
			built.registreTipus = tipus.getValor();
			built.unitatAdministrativa = unitatAdministrativa;
			built.unitatAdministrativaDescripcio = unitatdAministrativaDescripcio;
			built.numero = numero;
			built.data = data;
			built.identificador = identificador;
			built.extracte = extracte;
			built.oficinaCodi = oficinaCodi;
			built.llibreCodi = llibreCodi;
			built.assumpteTipusCodi = assumpteTipusCodi;
			built.idiomaCodi = idiomaCodi;
			built.procesEstat = procesEstat;
			built.pare = pare;
			built.tipus = ContingutTipusEnumDto.REGISTRE;
			built.procesData = new Date();
			built.procesIntents = 0;
			built.arxiuTancat = false;
			built.arxiuTancatError = false;
		}
		public Builder entitatCodi(String entitatCodi) {
			built.entitatCodi = entitatCodi;
			return this;
		}
		public Builder entitatDescripcio(String entitatDescripcio) {
			built.entitatDescripcio = entitatDescripcio;
			return this;
		}
		public Builder oficinaDescripcio(String oficinaDescripcio) {
			built.oficinaDescripcio = oficinaDescripcio;
			return this;
		}
		public Builder llibreDescripcio(String llibreDescripcio) {
			built.llibreDescripcio = llibreDescripcio;
			return this;
		}
		public Builder assumpteTipusDescripcio(String assumpteTipusDescripcio) {
			built.assumpteTipusDescripcio = assumpteTipusDescripcio;
			return this;
		}
		public Builder assumpteCodi(String assumpteCodi) {
			built.assumpteCodi = assumpteCodi;
			return this;
		}
		public Builder assumpteDescripcio(String assumpteDescripcio) {
			built.assumpteDescripcio = assumpteDescripcio;
			return this;
		}
		public Builder referencia(String referencia) {
			built.referencia = referencia;
			return this;
		}
		public Builder expedientNumero(String expedientNumero) {
			built.expedientNumero = expedientNumero;
			return this;
		}
		public Builder numeroOrigen(String numeroOrigen) {
			built.numeroOrigen = numeroOrigen;
			return this;
		}
		public Builder idiomaDescripcio(String idiomaDescripcio) {
			built.idiomaDescripcio = idiomaDescripcio;
			return this;
		}
		public Builder transportTipusCodi(String transportTipusCodi) {
			built.transportTipusCodi = transportTipusCodi;
			return this;
		}
		public Builder transportTipusDescripcio(String transportTipusDescripcio) {
			built.transportTipusDescripcio = transportTipusDescripcio;
			return this;
		}
		public Builder transportNumero(String transportNumero) {
			built.transportNumero = transportNumero;
			return this;
		}
		public Builder usuariCodi(String usuariCodi) {
			built.usuariCodi = usuariCodi;
			return this;
		}
		public Builder usuariNom(String usuariNom) {
			built.usuariNom = usuariNom;
			return this;
		}
		public Builder usuariContacte(String usuariContacte) {
			built.usuariContacte = usuariContacte;
			return this;
		}
		public Builder aplicacioCodi(String aplicacioCodi) {
			built.aplicacioCodi = aplicacioCodi;
			return this;
		}
		public Builder aplicacioVersio(String aplicacioVersio) {
			built.aplicacioVersio = aplicacioVersio;
			return this;
		}
		public Builder documentacioFisicaCodi(String documentacioFisicaCodi) {
			built.documentacioFisicaCodi = documentacioFisicaCodi;
			return this;
		}
		public Builder documentacioFisicaDescripcio(String documentacioFisicaDescripcio) {
			built.documentacioFisicaDescripcio = documentacioFisicaDescripcio;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder exposa(String exposa) {
			built.exposa = exposa;
			return this;
		}
		public Builder solicita(String solicita) {
			built.solicita = solicita;
			return this;
		}
		public Builder motiuRebuig(String motiuRebuig) {
			built.motiuRebuig = motiuRebuig;
			return this;
		}
		public Builder procesData(Date procesData) {
			built.procesData = procesData;
			return this;
		}
		public Builder regla(ReglaEntity regla) {
			built.regla = regla;
			built.procesIntents = new Integer(0);
			// Per backoffices tipus Sistra posa l'estat en pendent
			if (regla != null
					&& regla.getBackofficeTipus() != null
					&& BackofficeTipusEnumDto.SISTRA.equals(regla.getBackofficeTipus())) {
				built.procesEstatSistra = RegistreProcesEstatSistraEnum.PENDENT;
			}
			return this;
		}
		public Builder oficinaOrigen(Date dataOrigen,
				String oficinaOrigenCodi,
				String oficinaOrigenDescripcio) {
			built.dataOrigen = dataOrigen;
			built.oficinaOrigenCodi = oficinaOrigenCodi;
			built.oficinaOrigenDescripcio = oficinaOrigenDescripcio;
			return this;
		}
		public Builder llegida(Boolean llegida) {
			built.llegida = llegida;
			return this;
		}
		public Builder justificantArxiuUuid(String justificantArxiuUuid) {
			built.justificantArxiuUuid = justificantArxiuUuid;
			return this;
		}
		public RegistreEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((entitatCodi == null) ? 0 : entitatCodi.hashCode());
		result = prime * result + ((llibreCodi == null) ? 0 : llibreCodi.hashCode());
		result = prime * result + numero.hashCode();
		result = prime * result + ((registreTipus == null) ? 0 : registreTipus.hashCode());
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
		RegistreEntity other = (RegistreEntity) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (entitatCodi == null) {
			if (other.entitatCodi != null)
				return false;
		} else if (!entitatCodi.equals(other.entitatCodi))
			return false;
		if (llibreCodi == null) {
			if (other.llibreCodi != null)
				return false;
		} else if (!llibreCodi.equals(other.llibreCodi))
			return false;
		if (numero != other.numero)
			return false;
		if (registreTipus == null) {
			if (other.registreTipus != null)
				return false;
		} else if (!registreTipus.equals(other.registreTipus))
			return false;
		return true;
	}
	
	@Override
	public String getContingutType() {
		return "Anotació de registre";
	}

	private static final long serialVersionUID = -2299453443943600172L;
}
