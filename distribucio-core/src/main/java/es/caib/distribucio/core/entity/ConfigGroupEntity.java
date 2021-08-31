package es.caib.distribucio.core.entity;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(	name = "DIS_CONFIG_GROUP")
public class ConfigGroupEntity {

    @Id
    @Column(name = "CODE", length = 128, nullable = false)
    private String key;

    @Column(name = "DESCRIPTION", length = 512, nullable = true)
    private String description;

    @Column(name = "POSITION")
    private int position;

    @Column(name = "PARENT_CODE")
    private String parentCode;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_CODE")
    @OrderBy("position ASC")
    private Set<ConfigEntity> configs;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CODE")
    @OrderBy("position ASC")
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
