package es.caib.distribucio.persist.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import es.caib.distribucio.logic.intf.config.BaseConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe del model de dades que representa un del tipus de dades possibles per a una propietat de configuració.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "config_type")
public class ConfigTypeEntity {

	@Id
	@Column(name = "code", length = 128, nullable = false)
	private String code;

	@Column(name = "value", length = 2048, nullable = false)
	private String value;

	public List<String> getValidValues() {
		if (value == null || value.isEmpty()) {
			return Collections.emptyList();
		}
		String[] values = value.split(",");
		return Arrays.asList(values);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
