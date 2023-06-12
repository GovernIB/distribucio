/**
 * 
 */
package es.caib.distribucio.core.entity;

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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
								"data",
								"numero_copia"})})
@EntityListeners(AuditingEntityListener.class)
public class RegistreEntity extends ContingutEntity {


	@Column(name = "tipus", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
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
	@Column(name = "procediment_codi", length = 64)
	private String procedimentCodi;
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
	@JoinColumn(name = "justificant_id")
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
	/** Com que es pot reenviar un registre a una altra bústia amb el mateix número de registre es posa el número de còpia per distingir-los. */
	@Column(name = "numero_copia")
	private Integer numeroCopia;
	@Column(name = "enviat_per_email")
	private boolean enviatPerEmail;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "agafat_per")
	@ForeignKey(name = "dis_agafatper_registre_fk")
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
	
	public boolean isEnviatPerEmail() {
		return enviatPerEmail;
	}
	public void updateEnviatPerEmail(boolean enviatPerEmail) {
		this.enviatPerEmail = enviatPerEmail;
	}
	public RegistreAnnexEntity getJustificant() {
		return justificant;
	}
	public void updateJustificant(RegistreAnnexEntity justificant) {
		this.justificant = justificant;
	}
	public boolean isJustificantDescarregat() {
		return justificantDescarregat;
	}
	public void updateJustificantDescarregat(boolean justificantDescarregat) {
		this.justificantDescarregat = justificantDescarregat;
	}
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
	public Boolean getPresencial() {
		return presencial;
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
	public String getProcedimentCodi() {
		return procedimentCodi;
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
	/** Retorna true si està pendent de processament. */
	public Boolean getPendent() {
		return pendent;
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
	public Integer getNumeroCopia() {
		return numeroCopia != null? numeroCopia : 0;
	}
	public String getBackCodi() {
		return backCodi;
	}
	public Date getBackRetryEnviarData() {
		return backRetryEnviarData;
	}
	public Date getBackPendentData() {
		return backPendentData;
	}
	public Date getBackRebudaData() {
		return backRebudaData;
	}
	public String getBackObservacions() {
		return backObservacions;
	}
	public UsuariEntity getAgafatPer() {
		return agafatPer;
	}
	public void updateAgafatPer(UsuariEntity usuari) {
		this.agafatPer = usuari;
	}
	// Informació sobre el tancament del registre
	public Date getDataTancament() {
		return this.dataTancament;
	}
	public boolean getArxiuTancat() {
		return this.arxiuTancat != null ? this.arxiuTancat.booleanValue() : false;
	}
	public boolean getArxiuTancatError() {
		return this.arxiuTancatError != null ? this.arxiuTancatError.booleanValue() : false;
	}
	public boolean isReactivat() {
		return reactivat;
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
			List<Exception> exceptions) {
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
			Exception exception) {
		updateProcesMultipleExcepcions(procesEstat, Arrays.asList(exception));
	}
	
	public void updateProcesBackPendent() {
		this.procesData = null;
		this.procesIntents = 0;
		this.procesError = null;
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
	public Date getBackProcesRebutjErrorData() {
		return backProcesRebutjErrorData;
	}
	public void updateBackProcesRebutjErrorData(Date backProcesRebutjErrorData) {
		this.backProcesRebutjErrorData = backProcesRebutjErrorData;
	}
	public void updateBackEstat(RegistreProcesEstatEnum procesEstat, String backObservacions) {
//		if (procesEstat.equals(RegistreProcesEstatEnum.BACK_ERROR)) {
//			this.procesIntents = 0;
//		}
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
	public void updateProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
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
	public boolean isSobreescriure() {
		return sobreescriure;
	}
	public void updateSobreescriure(boolean sobreescriure) {
		this.sobreescriure = sobreescriure;
	}
	public int getAnnexosEstatEsborrany() {
		return annexosEstatEsborrany;
	}
	public void setAnnexosEstatEsborrany(int annexosEstatEsborrany) {
		this.annexosEstatEsborrany = annexosEstatEsborrany;
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
		public Builder procedimentCodi(String procedimentCodi) {
			built.procedimentCodi = procedimentCodi;
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
			String procedimentCodi,
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
			String justificantArxiuUuid) {

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
		this.procedimentCodi = procedimentCodi;
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

	private static final long serialVersionUID = -2299453443943600172L;
}
