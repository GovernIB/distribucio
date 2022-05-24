package es.caib.distribucio.war.command;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

public class ConfigCommand {
    private String key;
    private String value;
    
    private String entitatCodi;
    private String entitatKey;

    public ConfigCommand() {

	}
    
    public ConfigCommand(
    		String key,
			String value) {
		super();
		this.key = key;
		this.value = value;
	}


	public boolean isBooleanValue() {
        return value!=null && value.equals("true");
    }

    public void setBooleanValue(boolean booleanValue) {
        this.value = booleanValue ? "true" : "false";
    }
    
    
    public boolean isEntitatBooleanValue() {
        return entitatKey!=null && entitatKey.equals("true");
    }

    public void setEntitatBooleanValue(boolean booleanValue) {
        this.entitatKey = booleanValue ? "true" : "false";
    }

    
	public static ConfigCommand asCommand(ConfigDto dto) {
		ConfigCommand command = ConversioTipusHelper.convertir(
				dto, 
				ConfigCommand.class);
		return command;
	}
	
	public static ConfigDto asDto(ConfigCommand command) {
		ConfigDto dto = ConversioTipusHelper.convertir(
				command,
				ConfigDto.class);
		return dto;
	}
    
    
    

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getEntitatKey() {
		return entitatKey;
	}

	public void setEntitatKey(String entitatKey) {
		this.entitatKey = entitatKey;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getEntitatCodi() {
		return entitatCodi;
	}

	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
    
    
}
