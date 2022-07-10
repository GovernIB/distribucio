package es.caib.distribucio.api.interna.model;

import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;

public class InfoCanviEstat {

    private AnotacioRegistreId id;
    private Estat estat;
    private String observacions;
    
	public AnotacioRegistreId getId() {
		return id;
	}
	public void setId(AnotacioRegistreId id) {
		this.id = id;
	}
	public Estat getEstat() {
		return estat;
	}
	public void setEstat(Estat estat) {
		this.estat = estat;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
}
