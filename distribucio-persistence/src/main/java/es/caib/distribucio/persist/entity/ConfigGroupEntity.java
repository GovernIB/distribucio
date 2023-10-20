package es.caib.distribucio.persist.entity;


import javax.persistence.*;

import es.caib.distribucio.logic.intf.config.BaseConfig;

import java.util.Set;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "config_group")
public class ConfigGroupEntity {

	@Id
	@Column(name = "code", length = 128, nullable = false)
	private String key;

	@Column(name = "description", length = 512, nullable = true)
	private String description;

	@Column(name = "position")
	private int position;

	@Column(name = "parent_code")
	private String parentCode;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_code")
	@OrderBy("position asc")
	private Set<ConfigEntity> configs;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_code")
	@OrderBy("position asc")
	private Set<ConfigGroupEntity> innerConfigs;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Set<ConfigEntity> getConfigs() {
		return configs;
	}

	public void setConfigs(Set<ConfigEntity> configs) {
		this.configs = configs;
	}

	public Set<ConfigGroupEntity> getInnerConfigs() {
		return innerConfigs;
	}

	public void setInnerConfigs(Set<ConfigGroupEntity> innerConfigs) {
		this.innerConfigs = innerConfigs;
	}

}
