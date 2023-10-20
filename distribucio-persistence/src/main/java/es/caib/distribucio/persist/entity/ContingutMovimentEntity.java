/**
 * 
 */
package es.caib.distribucio.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;

/**
 * Classe del model de dades que representa un canvi de lloc
 * d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_mov")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ContingutMovimentEntity extends DistribucioAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "contingut_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_mov_contingut_fk"))
	protected ContingutEntity contingut;
	@Column(name = "origen_id")
	protected Long origenId;
	@Column(name = "desti_id")
	protected Long destiId;
	@Column(name = "origen_nom")
	protected String origenNom;
	@Column(name = "desti_nom")
	protected String destiNom;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "remitent_codi",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "remitent_contmov_fk"))
	protected UsuariEntity remitent;
	@Column(name = "comentari", length = 3940)
	protected String comentari;
	@Column(name = "per_coneixement")
	protected Boolean perConeixement;
	@Column(name = "comentari_destins", length = 256)
	protected String comentariDestins;

	@Column(name = "num_duplicat")
	private Integer numDuplicat;

	public ContingutEntity getContingut() {
		return contingut;
	}
	public Long getOrigenId() {
		return origenId;
	}
	public Long getDestiId() {
		return destiId;
	}
	public String getOrigenNom() {
		return origenNom;
	}
	public String getDestiNom() {
		return destiNom;
	}
	public UsuariEntity getRemitent() {
		return remitent;
	}
	public String getComentari() {
		return comentari;
	}
	public boolean isPerConeixement() {
		return perConeixement != null? perConeixement.booleanValue() : false;
	}
	public String getComentariDestins() {
		return comentariDestins;
	}
	
	public void updateComentariDestins(String comentari) {
		this.comentariDestins = comentari;
	}
	
	public void updatePerConeixement(boolean perConeixement) {
		this.perConeixement = perConeixement;
	}
	
	public void updateComentari(String comentari) {
		this.comentari = comentari;
	}

	public void updateRemitent(UsuariEntity remitent) {
		this.remitent = remitent;
	}
	public Integer getNumDuplicat() {
		return numDuplicat;
	}
	public void incrementNumDuplicat() {
		this.numDuplicat++;
	}
	
	public static Builder getBuilder(
			ContingutEntity contenidor,
			Long origenId,
			String origenNom,
			Long destiId,
			String destiNom,
			UsuariEntity remitent,
			String comentari) {
		return new Builder(
				contenidor,
				origenId,
				origenNom,
				destiId,
				destiNom,
				remitent,
				comentari);
	}
	public static Builder getBuilder(
			ContingutEntity contenidor,
			Long destiId,
			String destiNom,
			UsuariEntity remitent,
			String comentari) {
		return new Builder(
				contenidor,
				null,
				null,
				destiId,
				destiNom,
				remitent,
				comentari);
	}
	public static class Builder {
		ContingutMovimentEntity built;
		Builder(
				ContingutEntity contingut,
				Long origenId,
				String origenNom,
				Long destiId,
				String destiNom,
				UsuariEntity remitent,
				String comentari) {
			built = new ContingutMovimentEntity();
			built.contingut = contingut;
			built.origenId = origenId;
			built.origenNom = origenNom;
			built.destiId = destiId;
			built.destiNom = destiNom;
			built.remitent = remitent;
			built.comentari = comentari;
			built.numDuplicat = 1;
		}
		public ContingutMovimentEntity build() {
			return built;
		}
	}

}
