/**
 * 
 */
package es.caib.distribucio.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
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
@Table(name = BaseConfig.DB_PREFIX + "cont_mov_email")
@EntityListeners(AuditingEntityListener.class)
public class ContingutMovimentEmailEntity extends DistribucioAuditable<Long> {

	@Column(name = "destinatari_codi", length = 64)
	protected String destinatari;
	
	@Column(name = "destinatari_email", length = 256)
	protected String email;
	
	@Column(name = "enviament_agrupat")
	protected boolean enviamentAgrupat;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "bustia_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_mov_email_bustia_fk"))
	protected BustiaEntity bustia;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "contingut_moviment_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_mov_email_contmov_fk"))
	protected ContingutMovimentEntity contingutMoviment;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "contingut_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_mov_email_cont_fk"))
	protected ContingutEntity contingut;
	
	@Column(name = "unitat_organitzativa", length = 256)
	protected String unitatOrganitzativa;
	
	public String getDestinatari() {
		return destinatari;
	}
	public String getEmail() {
		return email;
	}
	public boolean isEnviamentAgrupat() {
		return enviamentAgrupat;
	}
	public BustiaEntity getBustia() {
		return bustia;
	}
	public ContingutMovimentEntity getContingutMoviment() {
		return contingutMoviment;
	}
	public ContingutEntity getContingut() {
		return contingut;
	}
	public String getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}

	public static Builder getBuilder(
			String destinatari,
			String email,
			boolean enviamentAgrupat,
			BustiaEntity bustia,
			ContingutMovimentEntity contingutMoviment,
			ContingutEntity contingut,
			String unitatOrganitzativa) {
		return new Builder(
				destinatari,
				email,
				enviamentAgrupat,
				bustia,
				contingutMoviment,
				contingut,
				unitatOrganitzativa);
	}
	public static class Builder {
		ContingutMovimentEmailEntity built;
		Builder(
				String destinatari,
				String email,
				boolean enviamentAgrupat,
				BustiaEntity bustia,
				ContingutMovimentEntity contingutMoviment,
				ContingutEntity contingut,
				String unitatOrganitzativa) {
			built = new ContingutMovimentEmailEntity();
			built.destinatari = destinatari;
			built.email = email;
			built.enviamentAgrupat = enviamentAgrupat;
			built.bustia = bustia;
			built.contingutMoviment = contingutMoviment;
			built.contingut = contingut;
			built.unitatOrganitzativa = unitatOrganitzativa;
		}
		public ContingutMovimentEmailEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((contingutMoviment == null) ? 0 : contingutMoviment.hashCode());
		result = prime * result + ((destinatari == null) ? 0 : destinatari.hashCode());
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
		ContingutMovimentEmailEntity other = (ContingutMovimentEmailEntity) obj;
		if (contingutMoviment == null) {
			if (other.contingutMoviment != null)
				return false;
		} else if (!contingutMoviment.equals(other.contingutMoviment))
			return false;
		if (destinatari == null) {
			if (other.destinatari != null)
				return false;
		} else if (!destinatari.equals(other.destinatari))
			return false;
		return true;
	}

}
