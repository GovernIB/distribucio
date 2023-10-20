/**
 * 
 */
package es.caib.distribucio.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;

/**
 * Classe del model de dades que representa un MonitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "mon_int_param")
@EntityListeners(AuditingEntityListener.class)
public class MonitorIntegracioParamEntity extends DistribucioPersistable<Long> {

	@ManyToOne(optional = false)
	@JoinColumn(
			name = "mon_int_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "mon_int_param_monint_fk"))
	private MonitorIntegracioEntity monitorIntegracio;
	
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	
	@Column(name = "descripcio", length = 1024)
	private String descripcio;

	public MonitorIntegracioEntity getMonitorIntegracio() {
		return monitorIntegracio;
	}

	public void setMonitorIntegracio(MonitorIntegracioEntity monitorIntegracio) {
		this.monitorIntegracio = monitorIntegracio;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public static Builder getBuilder(
			MonitorIntegracioEntity monitorIntegracio,
			String nom,
			String descripcio) {
		return new Builder(
				monitorIntegracio, 
				nom, 
				descripcio);
	}
	public static class Builder {
		MonitorIntegracioParamEntity built;
		Builder(MonitorIntegracioEntity monitorIntegracio,
				String nom,
				String descripcio) {
			built = new MonitorIntegracioParamEntity();
			built.monitorIntegracio = monitorIntegracio;
			built.nom = StringUtils.abbreviate(nom, 256);
			built.descripcio = StringUtils.abbreviate(descripcio, 1024);
		}
		public MonitorIntegracioParamEntity build() {
			return built;
		}
	}

}
