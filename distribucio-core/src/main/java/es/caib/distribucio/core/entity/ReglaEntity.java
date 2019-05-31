/**
 * 
 */
package es.caib.distribucio.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.audit.DistribucioAuditable;

/**
 * Classe del model de dades que representa una regla per al
 * processament automàtic d'anotacions de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "dis_regla",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						"entitat_id",
						"nom",
						"tipus",
						"assumpte_codi"})})
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ReglaEntity extends DistribucioAuditable<Long> {


	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	@Column(name = "descripcio", length = 1024)
	protected String descripcio;
	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	protected ReglaTipusEnumDto tipus;
	@Column(name = "assumpte_codi", length = 16)
	protected String assumpteCodi;
	@Column(name = "procediment_codi", length = 64, nullable = false)
	private String procedimentCodi;
	@Column(name = "unitat_codi", length = 9)
	protected String unitatCodi;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "unitat_id")
	@ForeignKey(name = "dis_unitat_regla_fk")
	protected UnitatOrganitzativaEntity unitatOrganitzativa;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "bustia_id")
	@ForeignKey(name = "dis_bustia_regla_fk")
	protected BustiaEntity bustia;
	@Column(name = "tipus_backoffice", length = 256)
	@Enumerated(EnumType.STRING)
	protected BackofficeTipusEnumDto backofficeTipus;
	@Column(name = "url", length = 256)
	protected String backofficeUrl;
	@Column(name = "usuari", length = 64)
	protected String backofficeUsuari;
	@Column(name = "contrasenya", length = 64)
	protected String backofficeContrasenya;
	@Column(name = "intents")
	protected Integer backofficeIntents;
	@Column(name = "temps_entre_intents")
	protected Integer backofficeTempsEntreIntents;
	@Column(name = "ordre", nullable = false)
	protected int ordre;
	@Column(name = "activa")
	protected boolean activa = true;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "dis_entitat_regla_fk")
	protected EntitatEntity entitat;
	@Version
	private long version = 0;


	public UnitatOrganitzativaEntity getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public String getNom() {
		return nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public ReglaTipusEnumDto getTipus() {
		return tipus;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public BustiaEntity getBustia() {
		return bustia;
	}
	public BackofficeTipusEnumDto getBackofficeTipus() {
		return backofficeTipus;
	}
	public String getBackofficeUrl() {
		return backofficeUrl;
	}
	public String getBackofficeUsuari() {
		return backofficeUsuari;
	}
	public String getBackofficeContrasenya() {
		return backofficeContrasenya;
	}
	public Integer getBackofficeIntents() {
		return backofficeIntents;
	}
	public Integer getBackofficeTempsEntreIntents() {
		return backofficeTempsEntreIntents;
	}
	public int getOrdre() {
		return ordre;
	}
	public boolean isActiva() {
		return activa;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}

	public void update(
			String nom,
			String descripcio,
			ReglaTipusEnumDto tipus,
			String procedimentCodi,
			UnitatOrganitzativaEntity unitatOrganitzativa) {
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.procedimentCodi = procedimentCodi;
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	public void updatePerTipusBustia(
			BustiaEntity bustia) {
		this.bustia = bustia;
	}
	public void updatePerTipusBackoffice(
			BackofficeTipusEnumDto backofficeTipus,
			String backofficeUrl,
			String backofficeUsuari,
			String backofficeContrasenya,
			Integer backofficeIntents,
			Integer backofficeTempsEntreIntents) {
		this.backofficeTipus = backofficeTipus;
		this.backofficeUrl = backofficeUrl;
		this.backofficeUsuari = backofficeUsuari;
		this.backofficeContrasenya = backofficeContrasenya;
		this.backofficeIntents = backofficeIntents;
		this.backofficeTempsEntreIntents = backofficeTempsEntreIntents;
	}
	public void updateOrdre(
			int ordre) {
		this.ordre = ordre;
	}
	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			String nom,
			ReglaTipusEnumDto tipus,
			String procedimentCodi,
			UnitatOrganitzativaEntity unitatOrganitzativa,
			int ordre) {
		return new Builder(
				entitat,
				nom,
				tipus,
				procedimentCodi,
				unitatOrganitzativa,
				ordre);
	}
	public static class Builder {
		ReglaEntity built;
		Builder(
				EntitatEntity entitat,
				String nom,
				ReglaTipusEnumDto tipus,
				String procedimentCodi,
				UnitatOrganitzativaEntity unitatOrganitzativa,
				int ordre) {
			built = new ReglaEntity();
			built.entitat = entitat;
			built.nom = nom;
			built.tipus = tipus;
			built.procedimentCodi = procedimentCodi;
			built.unitatOrganitzativa = unitatOrganitzativa;
			built.ordre = ordre;
			built.activa = true;
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public Builder backofficeUrl(String backofficeUrl) {
			built.backofficeUrl = backofficeUrl;
			return this;
		}
		public Builder backofficeUsuari(String backofficeUsuari) {
			built.backofficeUsuari = backofficeUsuari;
			return this;
		}
		public Builder backofficeContrasenya(String backofficeContrasenya) {
			built.backofficeContrasenya = backofficeContrasenya;
			return this;
		}
		public ReglaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((assumpteCodi == null) ? 0 : assumpteCodi.hashCode());
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
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
		ReglaEntity other = (ReglaEntity) obj;
		if (assumpteCodi == null) {
			if (other.assumpteCodi != null)
				return false;
		} else if (!assumpteCodi.equals(other.assumpteCodi))
			return false;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (tipus != other.tipus)
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
