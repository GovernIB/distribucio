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
	
	
	// ------------- FILRE ----------------------
	@Column(name = "assumpte_codi", length = 16)
	protected String assumpteCodiFiltre;
	
	@Column(name = "procediment_codi", length = 64, nullable = false)
	private String procedimentCodiFiltre;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "unitat_id")
	@ForeignKey(name = "dis_unitat_regla_fk")
	protected UnitatOrganitzativaEntity unitatOrganitzativaFiltre;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "bustia_filtre_id")
	protected BustiaEntity bustiaFiltre;
	
	
	// ------------- ACCIO  ----------------------
	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	protected ReglaTipusEnumDto tipus;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "backoffice_desti_id")
	protected BackofficeEntity backofficeDesti;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "bustia_id")
	@ForeignKey(name = "dis_bustia_regla_fk")
	protected BustiaEntity bustiaDesti;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "unitat_desti_id")
	protected UnitatOrganitzativaEntity unitatDesti;
	

	
	

	
	
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

	public UnitatOrganitzativaEntity getUnitatOrganitzativaFiltre() {
		return unitatOrganitzativaFiltre;
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
	public String getAssumpteCodiFiltre() {
		return assumpteCodiFiltre;
	}
	public String getProcedimentCodiFiltre() {
		return procedimentCodiFiltre;
	}
	public BustiaEntity getBustiaDesti() {
		return bustiaDesti;
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
	public BustiaEntity getBustiaFiltre() {
		return bustiaFiltre;
	}
	public BackofficeEntity getBackofficeDesti() {
		return backofficeDesti;
	}
	
	public UnitatOrganitzativaEntity getUnitatDesti() {
		return unitatDesti;
	}
	public void update(
			String nom,
			String descripcio,
			ReglaTipusEnumDto tipus,
			String assumpteCodiFiltre,
			String procedimentCodiFiltre,
			UnitatOrganitzativaEntity unitatOrganitzativaFiltre,
			BustiaEntity bustiaFiltre) {
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.assumpteCodiFiltre = assumpteCodiFiltre;
		this.procedimentCodiFiltre = procedimentCodiFiltre;
		this.unitatOrganitzativaFiltre = unitatOrganitzativaFiltre;
		this.bustiaFiltre = bustiaFiltre;
	}
	public void updatePerTipusBustia(
			BustiaEntity bustiaDesti) {
		this.bustiaDesti = bustiaDesti;
	}
	public void updatePerTipusBackoffice(
			BackofficeEntity backofficeEntity) {
		this.backofficeDesti = backofficeEntity;
	}
	public void updatePerTipusUnitat(
			UnitatOrganitzativaEntity unitatOrganitzativaEntity) {
		this.unitatDesti = unitatOrganitzativaEntity;
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
			String assumpteCodiFiltre,
			String procedimentCodiFiltre,
			UnitatOrganitzativaEntity unitatOrganitzativaFiltre,
			BustiaEntity bustiaFiltre,
			int ordre) {
		return new Builder(
				entitat,
				nom,
				tipus,
				assumpteCodiFiltre,
				procedimentCodiFiltre,
				unitatOrganitzativaFiltre,
				bustiaFiltre,
				ordre);
	}
	public static class Builder {
		ReglaEntity built;
		Builder(
				EntitatEntity entitat,
				String nom,
				ReglaTipusEnumDto tipus,
				String assumpteCodiFiltre,
				String procedimentCodiFiltre,
				UnitatOrganitzativaEntity unitatOrganitzativaFiltre,
				BustiaEntity bustiaFiltre,
				int ordre) {
			built = new ReglaEntity();
			built.entitat = entitat;
			built.nom = nom;
			built.tipus = tipus;
			built.assumpteCodiFiltre = assumpteCodiFiltre;
			built.procedimentCodiFiltre = procedimentCodiFiltre;
			built.unitatOrganitzativaFiltre = unitatOrganitzativaFiltre;
			built.bustiaFiltre = bustiaFiltre;
			built.ordre = ordre;
			built.activa = true;
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
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
		result = prime * result + ((assumpteCodiFiltre == null) ? 0 : assumpteCodiFiltre.hashCode());
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
		if (assumpteCodiFiltre == null) {
			if (other.assumpteCodiFiltre != null)
				return false;
		} else if (!assumpteCodiFiltre.equals(other.assumpteCodiFiltre))
			return false;
		if (procedimentCodiFiltre == null) {
			if (other.procedimentCodiFiltre != null)
				return false;
		} else if (!procedimentCodiFiltre.equals(other.procedimentCodiFiltre))
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
