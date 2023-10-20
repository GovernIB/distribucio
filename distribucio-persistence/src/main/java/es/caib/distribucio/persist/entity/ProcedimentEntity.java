package es.caib.distribucio.persist.entity;

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

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;

/**
 * Classe del model de dades que representa 
 * els procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "procediment")
@EntityListeners(AuditingEntityListener.class)
public class ProcedimentEntity extends DistribucioAuditable<Long>{

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;

	@Column(name = "nom", length = 256)
	private String nom;

	@Column(name = "codisia", length = 64)
	private String codiSia;

	@Column(name = "estat", length = 20) 
	@Enumerated(EnumType.STRING)
	private ProcedimentEstatEnumDto estat; // Vigente, Extinguido

	@ManyToOne(optional = false, fetch = FetchType.LAZY) 
	@JoinColumn(
			name = "id_unitat_organitzativa",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procediment_unitat_fk"))
	private UnitatOrganitzativaEntity unitatOrganitzativa;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procediment_entitat_fk"))
	private EntitatEntity entitat;

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCodiSia() {
		return codiSia;
	}

	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}

	public ProcedimentEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(ProcedimentEstatEnumDto estat) {
		this.estat = estat;
	}

	public UnitatOrganitzativaEntity getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}

	public void setUnitatOrganitzativa(UnitatOrganitzativaEntity unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public void setEntitat(EntitatEntity entitat) {
		this.entitat = entitat;
	}

	public void update(
			String codi, 
			String nom, 
			String codiSia, 
			ProcedimentEstatEnumDto estat, 
			UnitatOrganitzativaEntity unitatOrganitzativa, 
			EntitatEntity entitat) {
		this.codi = codi;
		this.nom = nom;
		this.codiSia = codiSia;
		this.estat = estat;
		this.unitatOrganitzativa = unitatOrganitzativa;
		this.entitat = entitat;
	}

	public static Builder getBuilder(
			String codi, 
			String nom, 
			String codiSia, 
			ProcedimentEstatEnumDto estat, 
			UnitatOrganitzativaEntity unitatOrganitzativa, 
			EntitatEntity entitat) {
		return new Builder(
				codi, 
				nom, 
				codiSia, 
				estat, 
				unitatOrganitzativa, 
				entitat);
	}

	public static class Builder {
		ProcedimentEntity built;
		Builder(
				String codi, 
				String nom, 
				String codiSia, 
				ProcedimentEstatEnumDto estat, 
				UnitatOrganitzativaEntity unitatOrganitzativa, 
				EntitatEntity entitat) {
			built = new ProcedimentEntity();
			built.codi = codi;
			built.nom = nom;
			built.codiSia = codiSia;
			built.estat = estat;
			built.unitatOrganitzativa = unitatOrganitzativa;
			built.entitat = entitat;
		}
		
		public ProcedimentEntity built() {
			return built;
		}
	}

}
