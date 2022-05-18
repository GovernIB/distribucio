package es.caib.distribucio.core.api.dto;


import java.util.List;

public class ConfigDto {
    private String key;
    private String value;
    private String description;
    private boolean jbossProperty;
    private String entitatCodi;
    private String groupCode;

    private String typeCode;
    private List<String> validValues;
    
    
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
    
    
    
}
