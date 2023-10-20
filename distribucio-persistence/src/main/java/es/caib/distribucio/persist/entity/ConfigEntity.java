package es.caib.distribucio.persist.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.config.BaseConfig;

/**
 * Classe del model de dades de configuracio de properties.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "config")
@EntityListeners(AuditingEntityListener.class)
public class ConfigEntity {

	@Id
	@Column(name = "key", length = 256, nullable = false)
	private String key;

	@Column(name = "value", length = 2048, nullable = true)
	private String value;

	@Column(name = "description", length = 2048, nullable = true)
	private String description;

	@Column(name = "jboss_property", nullable = false)
	private boolean jbossProperty;

	@Column(name = "group_code", length = 2048, nullable = true)
	private String groupCode;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "type_code", insertable = false, updatable = false,
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "config_type_fk"))
	private ConfigTypeEntity type;

	@Column(name="type_code", length = 128, nullable = true)
	private String typeCode;

	@Column(name = "position")
	private int position;

	@LastModifiedBy
	@ManyToOne
	private UsuariEntity lastModifiedBy;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	@Column(name = "entitat_codi")
	private String entitatCodi;

	@Column(name = "configurable")
	private boolean configurable;

	/**
	 * Per a mapejar el Dto de la vista.
	 *
	 * @return El llistat de possibles valors que pot prendre la propietat
	 */
	public void crearConfigNova(String key, String entitatCodi, ConfigEntity entitat) {
		this.key = key;
		this.entitatCodi = entitatCodi;
		this.value = null;
		this.description = entitat.getDescription();
		this.jbossProperty = entitat.isJbossProperty();
		this.groupCode = entitat.getGroupCode();
		this.type = entitat.getType();
		this.configurable = entitat.isConfigurable();
	}

	public List<String> getValidValues() {
		return type.getValidValues();
	}
	public String getTypeCode() {
		return type.getCode();
	}

	public void updateValue(String value) {
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public String getDescription() {
		return description;
	}
	public boolean isJbossProperty() {
		return jbossProperty;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public ConfigTypeEntity getType() {
		return type;
	}
	public int getPosition() {
		return position;
	}

	public ConfigEntity() {
	}

	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}

	public void setKey(String key) {
		this.key = key;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setJbossProperty(boolean jbossProperty) {
		this.jbossProperty = jbossProperty;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public void setType(ConfigTypeEntity type) {
		this.type = type;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public void setLastModifiedBy(UsuariEntity lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public boolean isConfigurable() {
		return configurable;
	}
	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

}
