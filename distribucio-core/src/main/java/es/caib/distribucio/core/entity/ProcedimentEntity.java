package es.caib.distribucio.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.audit.DistribucioAuditable;

/**
 * Classe del model de dades que representa 
 * els procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "dis_procediment")
@EntityListeners(AuditingEntityListener.class)
public class ProcedimentEntity extends DistribucioAuditable<Long>{

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	
	@Column(name = "nom", length = 256)
	private String nom;
	
	@Column(name = "codisia", length = 64)
	private String codiSia;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY) 
	@JoinColumn(name = "id_unitat_organitzativa")
	@ForeignKey(name = "dis_procediment_unitat_fk")
	private UnitatOrganitzativaEntity unitatOrganitzativa;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat")
	@ForeignKey(name = "dis_procediment_entitat_fk")
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
			UnitatOrganitzativaEntity unitatOrganitzativa, 
			EntitatEntity entitat) {
		this.codi = codi;
		this.nom = nom;
		this.codiSia = codiSia;
		this.unitatOrganitzativa = unitatOrganitzativa;
		this.entitat = entitat;
	}
	


	public static Builder getBuilder(
			String codi, 
			String nom, 
			String codiSia, 
			UnitatOrganitzativaEntity unitatOrganitzativa, 
			EntitatEntity entitat) {
		return new Builder(
				codi, 
				nom, 
				codiSia, 
				unitatOrganitzativa, 
				entitat);
	}
	
	public static class Builder {
		ProcedimentEntity built;
		Builder(
				String codi, 
				String nom, 
				String codiSia, 
				UnitatOrganitzativaEntity unitatOrganitzativa, 
				EntitatEntity entitat) {
			built = new ProcedimentEntity();
			built.codi = codi;
			built.nom = nom;
			built.codiSia = codiSia;
			built.unitatOrganitzativa = unitatOrganitzativa;
			built.entitat = entitat;
		}
		
		public ProcedimentEntity built() {
			return built;
		}
	}


	private static final long serialVersionUID = 5429596550889089134L;

}
