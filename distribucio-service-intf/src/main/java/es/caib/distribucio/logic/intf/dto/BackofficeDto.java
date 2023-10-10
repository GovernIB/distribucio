package es.caib.distribucio.logic.intf.dto;

public class BackofficeDto {

    private Long id;
    private String codi;
    private String nom;
    private String url;
    private String usuari;
    private String contrasenya;
    private Integer intents;
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

}
