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
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;

/**
 * Classe del model de dades que representa un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "bustia")
@EntityListeners(AuditingEntityListener.class)
public class BustiaEntity extends ContingutEntity {


	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "unitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "bustia_unitat_org_fk"))
	protected UnitatOrganitzativaEntity unitatOrganitzativa;
	@Column(name = "per_defecte")
	protected boolean perDefecte;
	@Column(name = "activa")
	protected boolean activa;

	public UnitatOrganitzativaEntity getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}

	public boolean isPerDefecte() {
		return perDefecte;
	}
	public boolean isActiva() {
		return activa;
	}

	public void update(
			String nom,
			UnitatOrganitzativaEntity unitatOrganitzativa) {
		this.nom = nom;
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	public void updatePerDefecte(boolean perDefecte) {
		this.perDefecte = perDefecte;
	}
	public void updateActiva(boolean activa) {
		this.activa = activa;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			String nom,
			String unitatCodi,
			UnitatOrganitzativaEntity unitatOrganitzativa,
			BustiaEntity pare) {
		return new Builder(
				entitat,
				nom,
				unitatCodi,
				unitatOrganitzativa,
				pare);
	}
	public static class Builder {
		BustiaEntity built;
		Builder(
				EntitatEntity entitat,
				String nom,
				String unitatCodi,
				UnitatOrganitzativaEntity unitatOrganitzativa,
				BustiaEntity pare) {
			built = new BustiaEntity();
			built.entitat = entitat;
			built.nom = nom;
			built.unitatOrganitzativa = unitatOrganitzativa;
			built.pare = pare;
			built.activa = true;
			built.tipus = ContingutTipusEnumDto.BUSTIA;
		}
		public BustiaEntity build() {
			return built;
		}
	}

}