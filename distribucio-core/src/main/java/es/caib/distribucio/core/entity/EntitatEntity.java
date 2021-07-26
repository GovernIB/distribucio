/**
 * 
 */
package es.caib.distribucio.core.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.audit.DistribucioAuditable;

/**
 * Classe del model de dades que representa una Entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="dis_entitat")
@EntityListeners(AuditingEntityListener.class)
public class EntitatEntity extends DistribucioAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "cif", length = 9, nullable = false)
	private String cif;
	@Column(name = "codi_dir3", length = 9, nullable = false)
	private String codiDir3;
	@Column(name = "fecha_actualizacion")
	Timestamp fechaActualizacion;
	@Column(name = "fecha_sincronizacion")
	Timestamp fechaSincronizacion;
	
	@Column(name = "color_fons", length = 32)
	private String colorFons;
	@Column(name = "color_lletra", length = 32)
	private String colorLletra;
	
	@Column(name = "activa")
	private boolean activa = true;
	@Version
	private long version = 0;
	
	public Timestamp getFechaActualizacion() {
		return fechaActualizacion;
	}
	public void updateFechaActualizacion(Timestamp fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	public Timestamp getFechaSincronizacion() {
		return fechaSincronizacion;
	}
	public void updateFechaSincronizacion(Timestamp fechaSincronizacion) {
		this.fechaSincronizacion = fechaSincronizacion;
	}
	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getCif() {
		return cif;
	}
	public String getCodiDir3() {
		return codiDir3;
	}
	public boolean isActiva() {
		return activa;
	}
	public String getColorFons() {
		return colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public void update(
			String codi,
			String nom,
			String descripcio,
			String cif,
			String codiDir3,
			String colorFons,
			String colorLletra) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.cif = cif;
		this.codiDir3 = codiDir3;
		this.colorFons = colorFons;
		this.colorLletra = colorLletra;
	}

	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus Entitat.
	 * 
	 * @param codi
	 *            El valor de l'atribut codi.
	 * @param nom
	 *            El valor de l'atribut nom.
	 * @param descripcio
	 *            El valor de l'atribut descripcio.
	 * @param cif
	 *            El valor de l'atribut cif.
	 * @param codiDir3
	 *            El valor de l'atribut codiDir3.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String cif,
			String codiDir3,
			String colorFons,
			String colorLletra) {
		return new Builder(
				codi,
				nom,
				descripcio,
				cif,
				codiDir3,
				colorFons,
				colorLletra);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Josep Gayà
	 */
	public static class Builder {
		EntitatEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String cif,
				String codiDir3,
				String colorFons,
				String colorLletra) {
			built = new EntitatEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.cif = cif;
			built.codiDir3 = codiDir3;
			built.colorFons = colorFons;
			built.colorLletra = colorLletra;
		}
		public EntitatEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		EntitatEntity other = (EntitatEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
