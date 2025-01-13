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
import javax.persistence.Lob;
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
@Table(name = BaseConfig.DB_PREFIX + "cont_comment")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ContingutComentariEntity extends DistribucioAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "contingut_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_comment_cont_fk"))
	protected ContingutEntity contingut;
	
	@Lob
	@Column(name = "text")
	protected String text;

	public ContingutEntity getContingut() {
		return contingut;
	}
	public String getText() {
		return text;
	}
	
	public static Builder getBuilder(
			ContingutEntity contingut,
			String text) {
		return new Builder(
				contingut,
				text);
	}
	public static class Builder {
		ContingutComentariEntity built;
		Builder(
				ContingutEntity contingut,
				String text) {
			built = new ContingutComentariEntity();
			built.contingut = contingut;
			built.text = text;
		}
		public ContingutComentariEntity build() {
			return built;
		}
	}

}
