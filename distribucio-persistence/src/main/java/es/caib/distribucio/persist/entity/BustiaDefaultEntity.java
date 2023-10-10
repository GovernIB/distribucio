/**
 * 
 */
package es.caib.distribucio.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe del model de dades que representa les bústies default dels usuaris
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "DIS_BUSTIA_DEFAULT")
@EntityListeners(AuditingEntityListener.class)
public class BustiaDefaultEntity {

	@Id @GeneratedValue
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "ENTITAT",
			foreignKey = @ForeignKey(name = "DIS_BUSTIA_DEF_ENT_FK"))
	private EntitatEntity entitat;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "BUSTIA",
			foreignKey = @ForeignKey(name = "DIS_BUSTIA_DEF_BST_FK"))
	private BustiaEntity bustia;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "USUARI",
			foreignKey = @ForeignKey(name = "DIS_BUSTIA_DEF_USR_FK"))
	private UsuariEntity usuari;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public BustiaEntity getBustia() {
		return bustia;
	}

	public UsuariEntity getUsuari() {
		return usuari;
	}
	
	public void updateBustiaDefault(BustiaEntity bustia) {
		this.bustia = bustia;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus BustiaDefault.
	 * 
	 * @param entitat
	 *            L'entitat a la qual pertany la bústia.
	 * @param bustia
	 *            La bústia per definir com per defecte.
	 * @param usuari
	 *            L'usuari per relacionar amb la bústia.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			EntitatEntity entitat,
			BustiaEntity bustia,
			UsuariEntity usuari) {
		return new Builder(
				entitat,
				bustia,
				usuari);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta entitat.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		BustiaDefaultEntity built;
		Builder(EntitatEntity entitat,
				BustiaEntity bustia,
				UsuariEntity usuari) {
			built = new BustiaDefaultEntity();
			built.entitat = entitat;
			built.bustia = bustia;
			built.usuari = usuari;
		}
		public BustiaDefaultEntity build() {
			return built;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result + ((bustia == null) ? 0 : bustia.hashCode());
		result = prime * result + ((usuari == null) ? 0 : usuari.hashCode());
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
		BustiaDefaultEntity other = (BustiaDefaultEntity) obj;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (bustia == null) {
			if (other.bustia != null)
				return false;
		} else if (bustia != other.bustia)
			return false;
		if (usuari == null) {
			if (other.usuari != null)
				return false;
		} else if (!usuari.equals(other.usuari))
			return false;
		return true;
	}
	
}
