/**
 * 
 */
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

import es.caib.distribucio.core.api.dto.ContingutTipusEnumDto;

/**
 * Classe del model de dades que representa un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "dis_bustia")
@EntityListeners(AuditingEntityListener.class)
public class BustiaEntity extends ContingutEntity {

	@Column(name = "unitat_codi", length = 9, nullable = false)
	protected String unitatCodi;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "unitat_id")
	@ForeignKey(name = "dis_unitat_organitzativa_bustia_fk")
	protected UnitatOrganitzativaEntity unitatOrganitzativa;
	@Column(name = "per_defecte")
	protected boolean perDefecte;
	@Column(name = "activa")
	protected boolean activa;

	public UnitatOrganitzativaEntity getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public String getUnitatCodi() {
		return unitatCodi;
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
		this.unitatCodi = (unitatOrganitzativa != null) ? unitatOrganitzativa.getCodi()	: null;
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
			built.unitatCodi = unitatCodi;
			built.unitatOrganitzativa = unitatOrganitzativa;
			built.pare = pare;
			built.activa = true;
			built.tipus = ContingutTipusEnumDto.BUSTIA;
		}
		public BustiaEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
