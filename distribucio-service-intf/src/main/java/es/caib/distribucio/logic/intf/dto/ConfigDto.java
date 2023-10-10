package es.caib.distribucio.logic.intf.dto;

import java.util.List;

import es.caib.distribucio.logic.intf.dto.config.EntitatConfig;

public class ConfigDto {
    private String key;
    private String value;
    private String description;
    private boolean jbossProperty;
    private List<EntitatConfig> entitatsConfig;
    private String entitatCodi;
    private String groupCode;
    private boolean configurable;

    private String typeCode;
    private List<String> validValues;
    
    private static final String prefix = "es.caib.distribucio";    
	
    public String addEntitatKey(EntitatDto entitat) {
    	String[] splitKey = key.split(prefix);
    	if (entitat == null || entitat.getCodi() == null || entitat.getCodi() == "" || splitKey == null || splitKey.length == 0 || splitKey.length != 2) {
            return null;
        }
    	EntitatConfig config = new EntitatConfig();
    	config.setCodi(entitat.getCodi());
    	config.setConfigKey(prefix + "." + entitat.getCodi() + splitKey[1]);
    	entitatsConfig.add(config);
    	return config.getConfigKey();
    }
    
    public String crearEntitatKey() {
    	if (entitatCodi == null || entitatCodi == "" || key == null || key == "") {
    		return null;
    	}
    	String[] splitKey = key.split(prefix);
    	return (prefix + "." + entitatCodi + splitKey[1]);
    }
    
    
	public static String getPrefix() {
		return prefix;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isJbossProperty() {
		return jbossProperty;
	}
	public void setJbossProperty(boolean jbossProperty) {
		this.jbossProperty = jbossProperty;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public List<String> getValidValues() {
		return validValues;
	}
	public void setValidValues(List<String> validValues) {
		this.validValues = validValues;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public boolean isConfigurable() {
		return configurable;
	}

	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}
    
    
    
}
