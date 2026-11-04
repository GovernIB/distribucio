/**
 * 
 */
package es.caib.distribucio.persist.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;

/**
 * Classe del model de dades que representa una anotació al
 * registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre",
		uniqueConstraints = {
				@UniqueConstraint(
						name = BaseConfig.DB_PREFIX + "registre_mult_uk",
						columnNames = { "entitat_codi", "llibre_codi", "tipus", "numero", "data", "numero_copia"})
		}
)
@EntityListeners(AuditingEntityListener.class)
public class RegistreEntity extends ContingutEntity {


	@Column(name = "tipus", length = 1, nullable = false)
	private String registreTipus;
	@Column(name = "unitat_adm", length = 21, nullable = false)
	private String unitatAdministrativa;
	@Column(name = "unitat_adm_desc", length = 300)
	private String unitatAdministrativaDescripcio;
	@Column(name = "numero", length = 255, nullable = false)
	private String numero;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;
	@Column(name = "identificador", length = 100, nullable = false)
	private String identificador;
	@Column(name = "entitat_codi", length = 255, nullable = false)
	private String entitatCodi;
	@Column(name = "entitat_desc", length = 255)
	private String entitatDescripcio;
	@Column(name = "oficina_codi", length = 21, nullable = false)
	private String oficinaCodi;
	@Column(name = "oficina_desc", length = 300)
	private String oficinaDescripcio;
	@Column(name = "llibre_codi", length = 4, nullable = false)
	private String llibreCodi;
	@Column(name = "llibre_desc", length = 255)
	private String llibreDescripcio;
	@Column(name = "extracte", length = 240)
	private String extracte;
	@Column(name = "assumpte_tipus_codi", length = 16)
	private String assumpteTipusCodi;
	@Column(name = "assumpte_tipus_desc", length = 100)
	private String assumpteTipusDescripcio;
	@Column(name = "assumpte_codi", length = 16)
	private String assumpteCodi;
	@Column(name = "assumpte_desc", length = 255)
	private String assumpteDescripcio;
//	@Column(name = "procediment_codi", length = 64)
//	private String procedimentCodi;
//	@Column(name = "servei_codi", length = 64)
//	private String serveiCodi;
	@Column(name = "sia_codi", length = 64)
	private String siaCodi;
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
	@Column(name = "transport_tipus_codi", length = 20)
	private String transportTipusCodi;
	@Column(name = "transport_tipus_desc", length = 100)
	private String transportTipusDescripcio;
	@Column(name = "transport_num", length = 20)
	private String transportNumero;
	@Column(name = "usuari_codi", length = 20)
	private String usuariCodi;
	@Column(name = "usuari_nom", length = 767)
	private String usuariNom;
	@Column(name = "usuari_contacte", length = 255)
	private String usuariContacte;
	@Column(name = "aplicacio_codi", length = 255)
	private String aplicacioCodi;
	@Column(name = "aplicacio_versio", length = 255)
	private String aplicacioVersio;
	@Column(name = "docfis_codi", length = 19)
	private String documentacioFisicaCodi;
	@Column(name = "docfis_desc", length = 100)
	private String documentacioFisicaDescripcio;
	@Column(name = "observacions", length = 50)
	private String observacions;
    @Lob
    @Column(name = "exposa", nullable = true)
	private String exposa;
    @Lob
    @Column(name = "solicita", nullable = true)
	private String solicita;
    @Lob
    @Column(name = "motiu_rebuig", nullable = true)
	private String motiuRebuig;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "proces_data")
	private Date procesData;
	@Enumerated(EnumType.STRING)
	@Column(name = "proces_estat", length = 64, nullable = false)
	private RegistreProcesEstatEnum procesEstat;
	/** Indica si l'anotació de registre està pendent de processament (true) o processada (false). */
	@Column(name = "pendent")
	private Boolean pendent;
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
	@Lob
	@Column(name = "proces_error")
	private String procesError;
	@Column(name = "proces_intents")
	private int procesIntents;
	/** Codi del backoffice que ha processat l'anotació, s'informa a partir de la Regla.codiBackoffice */
	@Column(name = "back_codi", length = 20)
	private String backCodi;
	// Date when regla change state of anotacio to RegistreProcesEstatEnum.BACK_PENDENT
	@Column(name = "back_pendent_data")
	private Date backPendentData;
	@Column(name = "back_rebuda_data")
	// Date when backoffice called BackofficeIntegracioWsService.canviEstat(RegistreProcesEstatEnum.BACK_REBUDA) method 
	private Date backRebudaData;
	@Column(name = "back_comunicada_data")
    // Date when change state of anotacio to RegistreProcesEstatEnum.BACK_COMUNICADA
	private Date backComunicadaData;
	@Column(name = "back_proces_rebutj_error_data")
	// Date when backoffice called BackofficeIntegracioWsService.canviEstat(RegistreProcesEstatEnum.BACK_PROCESADA) or (RegistreProcesEstatEnum.BACK_REBUTJADA) or (RegistreProcesEstatEnum.BACK_ERROR) method 
	private Date backProcesRebutjErrorData;
	@Column(name = "back_observacions")
	private String backObservacions;
	// Date when distribucio will retry to send anotacio to backoffice
	@Column(name = "back_retry_enviar_data")
	private Date backRetryEnviarData;
	
	@Column(name = "presencial")
	private Boolean presencial;
	
	@Column(name = "justificant_descarregat")
	private boolean justificantDescarregat;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "justificant_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_justificant_fk"))
	private RegistreAnnexEntity justificant;
	
	
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
	@Column(name = "justificant_arxiu_uuid", length = 256)
	private String justificantArxiuUuid;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "regla_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_regla_fk"))
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
	/** Com que es pot reenviar un registre a una altra bústia amb el mateix número de registre es posa el número de còpia per distingir-los. */
	@Column(name = "numero_copia")
	private Integer numeroCopia;
	@Column(name = "enviat_per_email")
	private boolean enviatPerEmail;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "agafat_per",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_agafatper_fk"))
	protected UsuariEntity agafatPer;
	

	@Column(name = "reactivat")
	private boolean reactivat;

	@Column(name = "sobreescriure")
	private boolean sobreescriure;

	/** Conté el recompte del número d'annexos en estat esborrany */
	@Column(name = "annexos_estat_esborrany")
	private int annexosEstatEsborrany;

	@OneToMany(mappedBy = "registre", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	protected Set<DadaEntity> dades;
	
	@Column(name = "tramit_codi", length = 64)
	private String tramitCodi;
	@Column(name = "tramit_nom", length = 255)
	private String tramitNom;

	public void updateEnviatPerEmail(boolean enviatPerEmail) {
		this.enviatPerEmail = enviatPerEmail;
	}
	public void updateJustificant(RegistreAnnexEntity justificant) {
		this.justificant = justificant;
	}
	public void updateJustificantDescarregat(boolean justificantDescarregat) {
		this.justificantDescarregat = justificantDescarregat;
	}
	public RegistreTipusEnum getRegistreTipus() {
		return RegistreTipusEnum.valorAsEnum(registreTipus);
	}
	public Integer getProcesIntents() {
		return procesIntents;
	}
	public Integer getNumeroCopia() {
		return numeroCopia != null? numeroCopia : 0;
	}
	public void updateAgafatPer(UsuariEntity usuari) {
		this.agafatPer = usuari;
	}
	public boolean getArxiuTancat() {
		return this.arxiuTancat != null ? this.arxiuTancat.booleanValue() : false;
	}
	public boolean getArxiuTancatError() {
		return this.arxiuTancatError != null ? this.arxiuTancatError.booleanValue() : false;
	}
	public void updateReactivat(boolean reactivat) {
		this.reactivat = reactivat;
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
	public void updatePendent(boolean pendent) {
		this.pendent = pendent;
	}
	/** Forces updating process state without deleting error information. */
	public void setProces(
			RegistreProcesEstatEnum procesEstat) {
		this.procesData = new Date();
		if (procesEstat != null) {
			this.procesEstat = procesEstat;
		}
		this.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);
	}
	
	public void setNewProcesEstat(
			RegistreProcesEstatEnum procesEstat) {
		this.procesData = new Date();
		this.procesEstat = procesEstat;
		this.procesIntents = 0;
		this.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);
		this.procesError = null;
	}
	
	
	public void updateProcesMultipleExcepcions(
			RegistreProcesEstatEnum procesEstat,
			List<Throwable> exceptions) {
		this.procesData = new Date();
		if (procesEstat != null) {
			this.procesEstat = procesEstat;
			this.procesIntents = 0;
			this.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);
		}
		this.procesIntents++;
		if (exceptions != null && !exceptions.isEmpty() && exceptions.get(0) != null) {
			String error = "";
			for (Throwable throwable : exceptions) {
				error += StringUtils.abbreviate(throwable.getMessage() + ": " + ExceptionUtils.getRootCauseMessage(throwable), (2000)) + "\r\n";
			}
			
			this.procesError = error;
		} else {
			this.procesError = null;
		}
	}
	

	
	
	public void updateProces(
			RegistreProcesEstatEnum procesEstat,
			Throwable exception) {
		updateProcesMultipleExcepcions(procesEstat, Arrays.asList(exception));
	}
	
	public void updateProcesBackPendent() {
		this.procesData = null;
		this.procesIntents = 0;
		this.procesError = null;
        this.backRetryEnviarData = null;
		this.procesEstat = RegistreProcesEstatEnum.BACK_PENDENT;
		this.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);

	}
	public void updateRegla(ReglaEntity regla) {
		this.regla = regla;
		this.procesData = null;
		this.procesIntents = 0;
		this.procesError = null;
		this.procesEstat = RegistreProcesEstatEnum.REGLA_PENDENT;
		this.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);
	}
	public void removeRegla() {
		this.regla = null;
	}
	public void updateBackCodi(String backCodi) {
		this.backCodi = backCodi;
	}
	public void updateBackRetryEnviarData(Date backRetryEnviarData) {
		this.backRetryEnviarData = backRetryEnviarData;
	}
	public void updateBackPendentData(Date backPendentData) {
		this.backPendentData = backPendentData;
	}
	public void updateBackRebudaData(Date backRebudaData) {
		this.backRebudaData = backRebudaData;
		this.procesError = null;
	}
	public void updateBackProcesRebutjErrorData(Date backProcesRebutjErrorData) {
		this.backProcesRebutjErrorData = backProcesRebutjErrorData;
	}
	public void updateBackEstat(RegistreProcesEstatEnum procesEstat, String backObservacions) {
//		if (procesEstat.equals(RegistreProcesEstatEnum.BACK_ERROR)) {
//			this.procesIntents = 0;
//		}
        if (RegistreProcesEstatEnum.BACK_COMUNICADA.equals(procesEstat)) {
            this.backComunicadaData = new Date();
        }
		this.procesEstat = procesEstat;
		this.backObservacions = backObservacions;
		this.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);
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
	public void updateTitol(String titol) {
		this.extracte = titol;
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
	public void updatePresencial(Boolean presencial) {
		this.presencial = presencial;
	}
	public void updateSobreescriure(boolean sobreescriure) {
		this.sobreescriure = sobreescriure;
	}
	public static Builder getBuilder(
			EntitatEntity entitat,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			String unitatAdministrativaDescripcio,
			String numero,
			Date data,
			Integer numeroCopia,
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
				numeroCopia,
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
				Integer numeroCopia,
				String identificador,
				String extracte,
				String oficinaCodi,
				String llibreCodi,
				String assumpteTipusCodi,
				String idiomaCodi,
				RegistreProcesEstatEnum procesEstat,
				ContingutEntity pare) {
			built = new RegistreEntity();
			
			// Nom del contingut
			built.nom = numero;
			if (extracte != null) {
				built.nom += " - " + extracte;
			}
			if (numeroCopia != null && numeroCopia > 0)
				built.nom += " (" + numeroCopia + ")";			
			built.entitat = entitat;
			built.registreTipus = tipus.getValor();
			built.unitatAdministrativa = unitatAdministrativa;
			built.unitatAdministrativaDescripcio = unitatdAministrativaDescripcio;
			built.numero = numero;
			built.data = data;
			built.numeroCopia = numeroCopia;
			built.identificador = identificador;
			built.extracte = extracte;
			built.oficinaCodi = oficinaCodi;
			built.llibreCodi = llibreCodi;
			built.assumpteTipusCodi = assumpteTipusCodi;
			built.idiomaCodi = idiomaCodi;
			built.procesEstat = procesEstat;
			built.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);
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
		public Builder siaCodi(String siaCodi) {
			built.siaCodi = siaCodi;
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
			built.procesIntents = Integer.valueOf(0);
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
		public Builder presencial(Boolean presencial) {
			built.presencial = presencial;
			return this;
		}
		public RegistreEntity build() {
			return built;
		}
		public Builder tramitCodi(String tramitCodi) {
			built.tramitCodi = tramitCodi;
			return this;
		}
		public Builder tramitNom(String tramitNom) {
			built.tramitNom = tramitNom;
			return this;
		}
	}
	
	public void override(
			EntitatEntity entitat,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			String unitatdAministrativaDescripcio,
			String numero,
			Date data,
			Integer numeroCopia,
			String identificador,
			String extracte,
			String oficinaCodi,
			String llibreCodi,
			String assumpteTipusCodi,
			String idiomaCodi,
			RegistreProcesEstatEnum procesEstat,
			ContingutEntity pare,
			String entitatCodi,
			String entitatDescripcio,
			String oficinaDescripcio,
			String llibreDescripcio,
			String assumpteTipusDescripcio,
			String assumpteCodi,
			String assumpteDescripcio,
//			String procedimentCodi,
//			String serveiCodi,
			String siaCodi,
			String referencia,
			String expedientNumero,
			String numeroOrigen,
			String idiomaDescripcio,
			String transportTipusCodi,
			String transportTipusDescripcio,
			String transportNumero,
			String usuariCodi,
			String usuariNom,
			String usuariContacte,
			String aplicacioCodi,
			String aplicacioVersio,
			String documentacioFisicaCodi,
			String documentacioFisicaDescripcio,
			String observacions,
			String exposa,
			Date dataOrigen,
			String oficinaOrigenCodi,
			String oficinaOrigenDescripcio,
			String justificantArxiuUuid,
			String tramitCodi,
			String tramitNom) {
		// Nom del contingut
		this.nom = numero;
		if (extracte != null) {
			this.nom += " - " + extracte;
		}
		if (numeroCopia != null && numeroCopia > 0)
			this.nom += " (" + numeroCopia + ")";
		this.entitat = entitat;
		this.registreTipus = tipus.getValor();
		this.unitatAdministrativa = unitatAdministrativa;
		this.unitatAdministrativaDescripcio = unitatdAministrativaDescripcio;
		this.numero = numero;
		this.data = data;
		this.numeroCopia = numeroCopia;
		this.identificador = identificador;
		this.extracte = extracte;
		this.oficinaCodi = oficinaCodi;
		this.llibreCodi = llibreCodi;
		this.assumpteTipusCodi = assumpteTipusCodi;
		this.idiomaCodi = idiomaCodi;
		this.procesEstat = procesEstat;
		this.pendent = RegistreProcesEstatEnum.isPendent(procesEstat);
		this.procesIntents = 0;
		this.pare = pare;
		this.tipus = ContingutTipusEnumDto.REGISTRE;
		this.procesData = new Date();
		this.procesIntents = 0;
		this.arxiuTancat = false;
		this.arxiuTancatError = false;
		this.entitatCodi = entitatCodi;
		this.entitatDescripcio = entitatDescripcio;
		this.oficinaDescripcio = oficinaDescripcio;
		this.llibreDescripcio = llibreDescripcio;
		this.assumpteTipusDescripcio = assumpteTipusDescripcio;
		this.assumpteCodi = assumpteCodi;
		this.assumpteDescripcio = assumpteDescripcio;
//		this.procedimentCodi = procedimentCodi;
//		this.serveiCodi = serveiCodi;
		this.siaCodi = siaCodi;
		this.referencia = referencia;
		this.expedientNumero = expedientNumero;
		this.numeroOrigen = numeroOrigen;
		this.idiomaDescripcio = idiomaDescripcio;
		this.transportTipusCodi = transportTipusCodi;
		this.transportTipusDescripcio = transportTipusDescripcio;
		this.transportNumero = transportNumero;
		this.usuariCodi = usuariCodi;
		this.usuariNom = usuariNom;
		this.usuariContacte = usuariContacte;
		this.aplicacioCodi = aplicacioCodi;
		this.aplicacioVersio = aplicacioVersio;
		this.documentacioFisicaCodi = documentacioFisicaCodi;
		this.documentacioFisicaDescripcio = documentacioFisicaDescripcio;
		this.observacions = observacions;
		this.exposa = exposa;
		this.dataOrigen = dataOrigen;
		this.oficinaOrigenCodi = oficinaOrigenCodi;
		this.oficinaOrigenDescripcio = oficinaOrigenDescripcio;
		this.justificantArxiuUuid = justificantArxiuUuid;
		this.justificant = null;
		this.procesError = null;
		this.sobreescriure = false;
		this.tramitCodi = tramitCodi;
		this.tramitNom = tramitNom;
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
		result = prime * result + ((numeroCopia == null) ? 0 : numeroCopia.hashCode());
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
		if (numeroCopia == null) {
			if (other.numeroCopia != null)
				return false;
		} else if (!numeroCopia.equals(other.numeroCopia))
			return false;
		return true;
	}

	@Override
	public String getContingutType() {
		return "Anotació de registre";
	}

}
