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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.api.registre.RegistreAnnexElaboracioEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.core.audit.DistribucioAuditable;

/**
 * Classe del model de dades que representa un document
 * d'una anotació al registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "dis_registre_annex")
@EntityListeners(AuditingEntityListener.class)
public class RegistreAnnexEntity extends DistribucioAuditable<Long> {

	@Column(name = "titol", length = 200, nullable = false)
	private String titol;
	@Column(name = "fitxer_nom", length = 256, nullable = false)
	private String fitxerNom;
	@Column(name = "fitxer_tamany", nullable = false)
	private int fitxerTamany;
	@Column(name = "fitxer_mime", length = 30)
	private String fitxerTipusMime;
	@Column(name = "fitxer_arxiu_uuid", length = 256)
	private String fitxerArxiuUuid;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_captura", nullable = false)
	private Date dataCaptura;
	@Column(name = "localitzacio", length = 80)
	private String localitzacio;
	@Column(name = "origen_ciuadm", length = 1, nullable = false)
	private String origenCiutadaAdmin;
	@Column(name = "nti_tipus_doc", length = 4, nullable = false)
	private String ntiTipusDocument;
	@Column(name = "sicres_tipus_doc", length = 2)
	private String sicresTipusDocument;
	@Column(name = "nti_elaboracio_estat", length = 4)
	private String ntiElaboracioEstat;
	@Column(name = "observacions", length = 50)
	private String observacions;
	@Column(name = "firma_mode")
	private Integer firmaMode;
	@Column(name = "timestamp", length = 100)
	private String timestamp;
	@Column(name = "validacio_ocsp", length = 100)
	private String validacioOCSP;
	@Column(name = "gesdoc_doc_id")
	private String gesdocDocumentId;
	
	@Column(name = "sign_detalls_descarregat")
	private boolean signaturaDetallsDescarregat;
	@Column(name = "firma_csv", length = 256)
	private String firmaCsv;
	
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "registre_id")
	@ForeignKey(name = "dis_annex_registre_fk")
	private RegistreEntity registre;
	@OneToMany(
			mappedBy = "annex",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<RegistreAnnexFirmaEntity> firmes;
	@Version
	private long version = 0;

	
	public String getFirmaCsv() {
		return firmaCsv;
	}
	public void updateFirmaCsv(String firmaCsv) {
		this.firmaCsv = firmaCsv;
	}
	public String getTitol() {
		return titol;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public int getFitxerTamany() {
		return fitxerTamany;
	}
	public String getFitxerTipusMime() {
		return fitxerTipusMime;
	}
	public String getFitxerArxiuUuid() {
		return fitxerArxiuUuid;
	}
	public Date getDataCaptura() {
		return dataCaptura;
	}
	public String getLocalitzacio() {
		return localitzacio;
	}
	public RegistreAnnexOrigenEnum getOrigenCiutadaAdmin() {
		return RegistreAnnexOrigenEnum.valorAsEnum(origenCiutadaAdmin);
	}
	public RegistreAnnexNtiTipusDocumentEnum getNtiTipusDocument() {
		return RegistreAnnexNtiTipusDocumentEnum.valorAsEnum(ntiTipusDocument);
	}
	public RegistreAnnexSicresTipusDocumentEnum getSicresTipusDocument() {
		return RegistreAnnexSicresTipusDocumentEnum.valorAsEnum(sicresTipusDocument);
	}
	public RegistreAnnexElaboracioEstatEnum getNtiElaboracioEstat() {
		return RegistreAnnexElaboracioEstatEnum.valorAsEnum(ntiElaboracioEstat);
	}
	public String getObservacions() {
		return observacions;
	}
	public Integer getFirmaMode() {
		return firmaMode;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public String getValidacioOCSP() {
		return validacioOCSP;
	}
	public String getGesdocDocumentId() {
		return gesdocDocumentId;
	}
	public RegistreEntity getRegistre() {
		return registre;
	}
	public void updateRegistre(RegistreEntity registre) {
		this.registre = registre;
	}
	public List<RegistreAnnexFirmaEntity> getFirmes() {
		return firmes;
	}
	public void updateSicresTipusDocument(String sicresTipusDocument) {
		this.sicresTipusDocument = sicresTipusDocument;
	}
	public void updateTitol(String titol) {
		this.titol = titol;
	}
	public void updateFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public void updateFitxerTipusMime(String fitxerTipusMime) {
		this.fitxerTipusMime = fitxerTipusMime;
	}
	public void updateNtiTipusDocument(String ntiTipusDocument) {
		this.ntiTipusDocument = ntiTipusDocument;
	}
	public void updateNtiElaboracioEstat(String ntiElaboracioEstat) {
		this.ntiElaboracioEstat = ntiElaboracioEstat;
	}
	public void updateDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}
	public void updateOrigenCiutadaAdmin(String origenCiutadaAdmin) {
		this.origenCiutadaAdmin = origenCiutadaAdmin;
	}
	public boolean isSignaturaDetallsDescarregat() {
		return signaturaDetallsDescarregat;
	}
	public void updateSignaturaDetallsDescarregat(boolean signaturaDetallsDescarregat) {
		this.signaturaDetallsDescarregat = signaturaDetallsDescarregat;
	}
	public void updateFitxerArxiuUuid(String fitxerArxiuUuid) {
		this.fitxerArxiuUuid = fitxerArxiuUuid;
	}
	public void updateFitxerTamany(int fitxerTamany) {
		this.fitxerTamany = fitxerTamany;
	}
	public void updateGesdocDocumentId(String gesdocDocumentId) {
		this.gesdocDocumentId = gesdocDocumentId;
	}

	public static Builder getBuilder(
			String titol,
			String fitxerNom,
			int fitxerTamany,
			String fitxerArxiuUuid,
			Date dataCaptura,
			RegistreAnnexOrigenEnum origenCiutadaAdmin,
			RegistreAnnexNtiTipusDocumentEnum ntiTipusDocument,
			RegistreAnnexSicresTipusDocumentEnum sicresTipusDocument,
			RegistreEntity registre) {
		return new Builder(
				titol,
				fitxerNom,
				fitxerTamany,
				fitxerArxiuUuid,
				dataCaptura,
				origenCiutadaAdmin,
				ntiTipusDocument,
				sicresTipusDocument,
				registre);
	}
	public static class Builder {
		RegistreAnnexEntity built;
		Builder(
				String titol,
				String fitxerNom,
				int fitxerTamany,
				String fitxerArxiuUuid,
				Date dataCaptura,
				RegistreAnnexOrigenEnum origenCiutadaAdmin,
				RegistreAnnexNtiTipusDocumentEnum ntiTipusDocument,
				RegistreAnnexSicresTipusDocumentEnum sicresTipusDocument,
				RegistreEntity registre) {
			built = new RegistreAnnexEntity();
			built.titol = titol;
			built.fitxerNom = fitxerNom;
			built.fitxerTamany = fitxerTamany;
			built.fitxerArxiuUuid = fitxerArxiuUuid;
			built.dataCaptura = dataCaptura;
			if (origenCiutadaAdmin != null)
				built.origenCiutadaAdmin = origenCiutadaAdmin.getValor();
			if (ntiTipusDocument != null)
				built.ntiTipusDocument = ntiTipusDocument.getValor();
			if (sicresTipusDocument != null)
				built.sicresTipusDocument = sicresTipusDocument.getValor();
			built.registre = registre;
			built.firmes = new ArrayList<RegistreAnnexFirmaEntity>();
		}
		public Builder fitxerTipusMime(String fitxerTipusMime) {
			built.fitxerTipusMime = fitxerTipusMime;
			return this;
		}
		public Builder localitzacio(String localitzacio) {
			built.localitzacio = localitzacio;
			return this;
		}
		public Builder ntiElaboracioEstat(RegistreAnnexElaboracioEstatEnum ntiElaboracioEstat) {
			if (ntiElaboracioEstat != null)
				built.ntiElaboracioEstat = ntiElaboracioEstat.getValor();
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder firmaMode(Integer firmaMode) {
			built.firmaMode = firmaMode;
			return this;
		}
		public Builder timestamp(String timestamp) {
			built.timestamp = timestamp;
			return this;
		}
		public Builder validacioOCSP(String validacioOCSP) {
			built.validacioOCSP = validacioOCSP;
			return this;
		}
		public Builder gesdocDocumentId(String gesdocDocumentId) {
			built.gesdocDocumentId = gesdocDocumentId;
			return this;
		}
		public RegistreAnnexEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataCaptura == null) ? 0 : dataCaptura.hashCode());
		result = prime * result + ((fitxerArxiuUuid == null) ? 0 : fitxerArxiuUuid.hashCode());
		result = prime * result + ((fitxerNom == null) ? 0 : fitxerNom.hashCode());
		result = prime * result + fitxerTamany;
		result = prime * result + ((ntiTipusDocument == null) ? 0 : ntiTipusDocument.hashCode());
		result = prime * result + ((origenCiutadaAdmin == null) ? 0 : origenCiutadaAdmin.hashCode());
		result = prime * result + ((registre == null) ? 0 : registre.hashCode());
		result = prime * result + ((sicresTipusDocument == null) ? 0 : sicresTipusDocument.hashCode());
		result = prime * result + ((titol == null) ? 0 : titol.hashCode());
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
		RegistreAnnexEntity other = (RegistreAnnexEntity) obj;
		if (dataCaptura == null) {
			if (other.dataCaptura != null)
				return false;
		} else if (!dataCaptura.equals(other.dataCaptura))
			return false;
		if (fitxerArxiuUuid == null) {
			if (other.fitxerArxiuUuid != null)
				return false;
		} else if (!fitxerArxiuUuid.equals(other.fitxerArxiuUuid))
			return false;
		if (fitxerNom == null) {
			if (other.fitxerNom != null)
				return false;
		} else if (!fitxerNom.equals(other.fitxerNom))
			return false;
		if (fitxerTamany != other.fitxerTamany)
			return false;
		if (ntiTipusDocument == null) {
			if (other.ntiTipusDocument != null)
				return false;
		} else if (!ntiTipusDocument.equals(other.ntiTipusDocument))
			return false;
		if (origenCiutadaAdmin == null) {
			if (other.origenCiutadaAdmin != null)
				return false;
		} else if (!origenCiutadaAdmin.equals(other.origenCiutadaAdmin))
			return false;
		if (registre == null) {
			if (other.registre != null)
				return false;
		} else if (!registre.equals(other.registre))
			return false;
		if (sicresTipusDocument == null) {
			if (other.sicresTipusDocument != null)
				return false;
		} else if (!sicresTipusDocument.equals(other.sicresTipusDocument))
			return false;
		if (titol == null) {
			if (other.titol != null)
				return false;
		} else if (!titol.equals(other.titol))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
