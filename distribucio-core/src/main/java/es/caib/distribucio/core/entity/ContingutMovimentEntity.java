/**
 * 
 */
package es.caib.distribucio.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.audit.DistribucioAuditable;

/**
 * Classe del model de dades que representa un canvi de lloc
 * d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "dis_cont_mov")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ContingutMovimentEntity extends DistribucioAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "contingut_id")
	protected ContingutEntity contingut;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "origen_id")
	protected ContingutEntity origen;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "desti_id")
	protected ContingutEntity desti;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "remitent_codi")
	@ForeignKey(name = "dis_remitent_contmov_fk")
	protected UsuariEntity remitent;
	@Column(name = "comentari", length = 3940)
	protected String comentari;
	@Column(name = "per_coneixement")
	protected Boolean perConeixement;
	@Column(name = "comentari_destins", length = 256)
	protected String comentariDestins;

	public ContingutEntity getContingut() {
		return contingut;
	}
	public ContingutEntity getOrigen() {
		return origen;
	}
	public ContingutEntity getDesti() {
		return desti;
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
	
	public static Builder getBuilder(
			ContingutEntity contenidor,
			ContingutEntity origen,
			ContingutEntity desti,
			UsuariEntity remitent,
			String comentari) {
		return new Builder(
				contenidor,
				origen,
				desti,
				remitent,
				comentari);
	}
	public static Builder getBuilder(
			ContingutEntity contenidor,
			ContingutEntity desti,
			UsuariEntity remitent,
			String comentari) {
		return new Builder(
				contenidor,
				null,
				desti,
				remitent,
				comentari);
	}
	public static class Builder {
		ContingutMovimentEntity built;
		Builder(
				ContingutEntity contingut,
				ContingutEntity origen,
				ContingutEntity desti,
				UsuariEntity remitent,
				String comentari) {
			built = new ContingutMovimentEntity();
			built.contingut = contingut;
			built.origen = origen;
			built.desti = desti;
			built.remitent = remitent;
			built.comentari = comentari;
		}
		public ContingutMovimentEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
