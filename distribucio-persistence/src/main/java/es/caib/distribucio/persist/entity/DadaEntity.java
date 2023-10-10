/**
 * 
 */
package es.caib.distribucio.persist.entity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;

/**
 * Classe del model de dades que representa una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "dis_dada",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						"metadada_id",
						"registre_id",
						"ordre"})})
@EntityListeners(AuditingEntityListener.class)
public class DadaEntity extends DistribucioAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "metadada_id",
			foreignKey = @ForeignKey(name = "ipa_metadada_dada_fk"))
	protected MetaDadaEntity metaDada;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "registre_id",
			foreignKey = @ForeignKey(name = "ipa_registre_dada_fk"))
	protected RegistreEntity registre;
	@Column(name = "valor", length = 256, nullable = false)
	protected String valor;
	@Column(name = "ordre")
	protected int ordre;
	@Version
	private long version = 0;

	public MetaDadaEntity getMetaDada() {
		return metaDada;
	}
	public RegistreEntity getNode() {
		return registre;
	}
	public Object getValor() {
		return getDadaValorPerRetornar(metaDada, valor);
	}
	public String getValorComString() {
		return valor;
	}
	public int getOrdre() {
		return ordre;
	}

	public void update(
			Object valor,
			int ordre) {
		this.valor = getDadaValorPerEmmagatzemar(metaDada.getTipus(), valor);
		this.ordre = ordre;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus meta-dada.
	 * 
	 * @param metaDada
	 *            La meta-dada a la qual pertany aquesta dada.
	 * @param node
	 *            El node al qual pertany aquesta dada.
	 * @param valor
	 *           El valor de l'atribut valor.
	 * @param ordre
	 *            El valor de l'atribut ordre.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			MetaDadaEntity metaDada,
			RegistreEntity node,
			Object valor,
			int ordre) {
		return new Builder(
				metaDada,
				node,
				valor,
				ordre);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		DadaEntity built;
		Builder(
				MetaDadaEntity metaDada,
				RegistreEntity registre,
				Object valor,
				int ordre) {
			built = new DadaEntity();
			built.metaDada = metaDada;
			built.registre = registre;
			built.valor = getDadaValorPerEmmagatzemar(metaDada.getTipus(), valor);
			built.ordre = ordre;
		}
		public DadaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metaDada == null) ? 0 : metaDada.hashCode());
		result = prime * result + ((registre == null) ? 0 : registre.hashCode());
		result = prime * result + ordre;
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
		DadaEntity other = (DadaEntity) obj;
		if (metaDada == null) {
			if (other.metaDada != null)
				return false;
		} else if (!metaDada.equals(other.metaDada))
			return false;
		if (registre == null) {
			if (other.registre != null)
				return false;
		} else if (!registre.equals(other.registre))
			return false;
		if (ordre != other.ordre)
			return false;
		return true;
	}



	public static String getDadaValorPerEmmagatzemar(
			MetaDadaTipusEnumDto tipus,
			Object valor) {
		if (valor == null)
			return null;
		if (tipus.equals(MetaDadaTipusEnumDto.TEXT) || tipus.equals(MetaDadaTipusEnumDto.DOMINI)) {
			if (valor instanceof String) {
				return (String)valor;
			} else {
				throw new RuntimeException();
			}
		} else if (tipus.equals(MetaDadaTipusEnumDto.DATA)) {
			if (valor instanceof Date) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				return sdf.format((Date)valor);
			} else {
				throw new RuntimeException();
			}
		} else if (tipus.equals(MetaDadaTipusEnumDto.SENCER)) {
			if (valor instanceof Long) {
				return ((Long)valor).toString();
			} else {
				throw new RuntimeException();
			}
		} else if (tipus.equals(MetaDadaTipusEnumDto.FLOTANT)) {
			if (valor instanceof Double) {
				return ((Double)valor).toString();
			} else {
				throw new RuntimeException();
			}
		} else if (tipus.equals(MetaDadaTipusEnumDto.IMPORT)) {
			if (valor instanceof BigDecimal) {
				return ((BigDecimal)valor).toString();
			} else {
				throw new RuntimeException();
			}
		} else if (tipus.equals(MetaDadaTipusEnumDto.BOOLEA)) {
			if (valor instanceof Boolean) {
				return ((Boolean)valor).toString();
			} else {
				throw new RuntimeException();
			}
		}
		throw new RuntimeException();
	}
	public static Object getDadaValorPerRetornar(
			MetaDadaEntity metaDada,
			String valor) {
		if (valor == null) {
			return null;
		} else {
			if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.TEXT) || metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
				return valor;
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DATA)) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					return sdf.parse(valor);
				} catch (ParseException ex) {
					throw new RuntimeException(ex);
				}
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.SENCER)) {
				return Long.valueOf(valor);
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.FLOTANT)) {
				return Double.valueOf(valor);
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.IMPORT)) {
				return new BigDecimal(valor);
			} else if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.BOOLEA)) {
				return Boolean.valueOf(valor);
			}
			throw new RuntimeException();
		}
	}

}
