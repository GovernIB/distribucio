/**
 * 
 */
package es.caib.distribucio.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "dis_cont_log_param")
@EntityListeners(AuditingEntityListener.class)
public class ContingutLogParamEntity extends DistribucioAuditable<Long> implements Comparable<ContingutLogParamEntity>{

	/** Llargada màxima del paràmetre */
	private static final int PARAM_MAX_LENGTH = 255;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cont_log_id")
	private ContingutLogEntity contingutLog;
	
	@Column(name = "numero", nullable = false)
	private long numero;
	
	@Column(name = "valor", length = 256, nullable = false)
	private String valor;
	

	public ContingutLogEntity getContingutLog() {
		return contingutLog;
	}
	public long getNumero() {
		return numero;
	}
	public String getValor() {
		return valor;
	}
	public static Builder getBuilder(
			ContingutLogEntity contingutLog,
			long numero,
			String valor) {
		return new Builder(
				contingutLog, 
				numero, 
				valor);
	}
	public static class Builder {
		ContingutLogParamEntity built;
		Builder(ContingutLogEntity contingutLog, long numero, String valor) {
			built = new ContingutLogParamEntity();
			built.contingutLog = contingutLog;
			built.numero = numero;
			built.valor = StringUtils.abbreviate(valor, PARAM_MAX_LENGTH);
		}
		public ContingutLogParamEntity build() {
			return built;
		}
	}

	@Override
	public int compareTo(ContingutLogParamEntity o) {
		return Long.valueOf(numero).compareTo(o.numero);
	}

}
