/**
 * 
 */
package es.caib.distribucio.persist.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;

/**
 * Classe del model de dades que representa una bústia favorit d'un usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = BaseConfig.DB_PREFIX + "bustia_favorit",
		uniqueConstraints = {
				@UniqueConstraint(
						name = BaseConfig.DB_PREFIX + "bustia_fav_mult_uk",
						columnNames = { "bustia_id", "usuari_codi" })
		}
)
@EntityListeners(AuditingEntityListener.class)
public class UsuariBustiaFavoritEntity extends DistribucioAuditable<Long> {


	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "bustia_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "bustia_fav_bustia_fk"))
	protected BustiaEntity bustia;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "usuari_codi",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "bustia_fav_usuari_fk"))
	protected UsuariEntity usuari;
	
	
	
	public BustiaEntity getBustia() {
		return bustia;
	}

	public UsuariEntity getUsuari() {
		return usuari;
	}

	public static Builder getBuilder(
			BustiaEntity bustia,
			UsuariEntity usuari) {
		return new Builder(
				bustia,
				usuari);
	}
	public static class Builder {
		UsuariBustiaFavoritEntity built;
		Builder(
				BustiaEntity bustia,
				UsuariEntity usuari) {
			built = new UsuariBustiaFavoritEntity();
			built.bustia = bustia;
			built.usuari = usuari;
		}
		public UsuariBustiaFavoritEntity build() {
			return built;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bustia == null && usuari == null) ? 0 : bustia.hashCode());
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
		UsuariBustiaFavoritEntity other = (UsuariBustiaFavoritEntity) obj;
		if (bustia == null) {
			if (other.bustia != null)
				return false;
		} else if (usuari == null) {
			if (other.usuari != null)
				return false;
		} else if (!bustia.equals(other.bustia) && !usuari.equals(other.usuari))
			return false;
		return true;
	}

}
