package es.caib.distribucio.core.api.dto;

import java.util.List;

public class ConfigGroupDto {
    private String key;
    private String description;
    private List<ConfigDto> configs;
    private List<ConfigGroupDto> innerConfigs;
    
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
	public List<ConfigDto> getConfigs() {
		return configs;
	}
	public void setConfigs(List<ConfigDto> configs) {
		this.configs = configs;
	}
	public List<ConfigGroupDto> getInnerConfigs() {
		return innerConfigs;
	}
	public void setInnerConfigs(List<ConfigGroupDto> innerConfigs) {
		this.innerConfigs = innerConfigs;
	}
    
    
}
