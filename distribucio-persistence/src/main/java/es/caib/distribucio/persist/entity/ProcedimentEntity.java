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

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;

/**
 * Classe del model de dades que representa 
 * els procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
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

    @Column(name = "comu")
    private Boolean comu;

	public void update(
			String codi, 
			String nom, 
			String codiSia, 
			ProcedimentEstatEnumDto estat, 
			UnitatOrganitzativaEntity unitatOrganitzativa, 
			EntitatEntity entitat,
            Boolean comu) {
		this.codi = codi;
		this.nom = nom;
		this.codiSia = codiSia;
		this.estat = estat;
		this.unitatOrganitzativa = unitatOrganitzativa;
		this.entitat = entitat;
		this.comu = comu;
	}

	public static Builder getBuilder(
			String codi, 
			String nom, 
			String codiSia, 
			ProcedimentEstatEnumDto estat, 
			UnitatOrganitzativaEntity unitatOrganitzativa, 
			EntitatEntity entitat,
            Boolean comu) {
		return new Builder(
				codi, 
				nom, 
				codiSia, 
				estat, 
				unitatOrganitzativa, 
				entitat,
                comu);
	}

	public static class Builder {
		ProcedimentEntity built;
		Builder(
				String codi, 
				String nom, 
				String codiSia, 
				ProcedimentEstatEnumDto estat, 
				UnitatOrganitzativaEntity unitatOrganitzativa, 
				EntitatEntity entitat,
                Boolean comu) {
			built = new ProcedimentEntity();
			built.codi = codi;
			built.nom = nom;
			built.codiSia = codiSia;
			built.estat = estat;
			built.unitatOrganitzativa = unitatOrganitzativa;
			built.entitat = entitat;
			built.comu = comu;
		}
		
		public ProcedimentEntity built() {
			return built;
		}
	}

}
