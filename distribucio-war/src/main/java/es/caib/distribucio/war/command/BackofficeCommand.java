package es.caib.distribucio.war.command;


import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.distribucio.logic.intf.dto.BackofficeDto;
import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;
import es.caib.distribucio.war.validation.Backoffice;

@Backoffice()
public class BackofficeCommand {

    private Long id;
    @NotEmpty @Size(max = 20)
    private String codi;
    @NotEmpty @Size(max = 64)
    private String nom;
    @NotEmpty @Size(max = 256)
    private String url;
    @Size(max = 255)
    private String usuari;
    @Size(max = 255)
    private String contrasenya;
    private Integer intents;
    // Deixem aquest camp que era usat per Bantel perquè potser el reutilitzem en el futur
    private Integer tempsEntreIntents;
    
    private Long entitatId;
    
    private BackofficeTipusEnumDto tipus;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCodi() {
        return codi;
    }
    public void setCodi(String codi) {
        this.codi = codi;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUsuari() {
        return usuari;
    }
    public void setUsuari(String usuari) {
        this.usuari = usuari;
    }
    public String getContrasenya() {
        return contrasenya;
    }
    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }
    public Integer getIntents() {
        return intents;
    }
    public void setIntents(Integer intents) {
        this.intents = intents;
    }
    public Integer getTempsEntreIntents() {
        return tempsEntreIntents;
    }
    public void setTempsEntreIntents(Integer tempsEntreIntents) {
        this.tempsEntreIntents = tempsEntreIntents;
    }

    public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public BackofficeTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(BackofficeTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public static BackofficeCommand asCommand(BackofficeDto dto) {
        return ConversioTipusHelper.convertir(dto, BackofficeCommand.class);
    }
    public static BackofficeDto asDto(BackofficeCommand command) {
        return ConversioTipusHelper.convertir(command, BackofficeDto.class);
    }
}
