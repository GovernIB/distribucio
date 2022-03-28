package es.caib.distribucio.core.api.dto.dadesobertes;

import java.io.Serializable;
import java.text.ParseException;

public class LogsDadesObertesDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String data;
	private String tipus;
	private String tipusDesc;
	private String usuari;
	private Long anotacioId;
	private String anotacioEstat;
	private String anotacioEstatDesc;
	private String anotacioError;
	private String anotacioErrorDesc;
	private boolean pendent;
	private Long nAnnexos;
	private Long bustiaOrigenId;
	private String bustiaOrigenNom;
	private Long bustiaDestiId;
	private String bustiaDestiNom;
	private String uoOrigenCodi;
	private String uoOrigenNom;
	private String uoSuperirOrigenCodi;
	private String uoSuperiorOrigenNom;
	private String uoDestiCodi;
	private String uoDestiNom;
	private String uoSuperiorDestiCodi;
	private String uoSuperiorDestiNom;
	
	
	public String getData() {
		return data;
	}
	public void setData(String strDate) throws ParseException {
		this.data = strDate;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getTipusDesc() {
		return tipusDesc;
	}
	public void setTipusDesc(String tipusDesc) {
		this.tipusDesc = tipusDesc;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public Long getAnotacioId() {
		return anotacioId;
	}
	public void setAnotacioId(Long anotacioId) {
		this.anotacioId = anotacioId;
	}
	public String getAnotacioEstat() {
		return anotacioEstat;
	}
	public void setAnotacioEstat(String anotacioEstat) {
		this.anotacioEstat = anotacioEstat;
	}
	public String getAnotacioEstatDesc() {
		return anotacioEstatDesc;
	}
	public void setAnotacioEstatDesc(String anotacioEstatDesc) {
		this.anotacioEstatDesc = anotacioEstatDesc;
	}
	public String getAnotacioError() {
		return anotacioError;
	}
	public void setAnotacioError(String anotacioError) {
		this.anotacioError = anotacioError;
	}
	public String getAnotacioErrorDesc() {
		return anotacioErrorDesc;
	}
	public void setAnotacioErrorDesc(String anotacioErrorDesc) {
		this.anotacioErrorDesc = anotacioErrorDesc;
	}
	public boolean isPendent() {
		return pendent;
	}
	public void setPendent(boolean pendent) {
		this.pendent = pendent;
	}
	public Long getnAnnexos() {
		return nAnnexos;
	}
	public void setnAnnexos(Long nAnnexos) {
		this.nAnnexos = nAnnexos;
	}
	public Long getBustiaOrigenId() {
		return bustiaOrigenId;
	}
	public void setBustiaOrigenId(Long bustiaOrigenId) {
		this.bustiaOrigenId = bustiaOrigenId;
	}
	public String getBustiaOrigenNom() {
		return bustiaOrigenNom;
	}
	public void setBustiaOrigenNom(String bustiaOrigenNom) {
		this.bustiaOrigenNom = bustiaOrigenNom;
	}
	public Long getBustiaDestiId() {
		return bustiaDestiId;
	}
	public void setBustiaDestiId(Long bustiaDestiId) {
		this.bustiaDestiId = bustiaDestiId;
	}
	public String getBustiaDestiNom() {
		return bustiaDestiNom;
	}
	public void setBustiaDestiNom(String bustiaDestiNom) {
		this.bustiaDestiNom = bustiaDestiNom;
	}
	public String getUoOrigenCodi() {
		return uoOrigenCodi;
	}
	public void setUoOrigenCodi(String uoOrigenCodi) {
		this.uoOrigenCodi = uoOrigenCodi;
	}
	public String getUoOrigenNom() {
		return uoOrigenNom;
	}
	public void setUoOrigenNom(String uoOrigenNom) {
		this.uoOrigenNom = uoOrigenNom;
	}
	public String getUoSuperirOrigenCodi() {
		return uoSuperirOrigenCodi;
	}
	public void setUoSuperirOrigenCodi(String uoSuperirOrigenCodi) {
		this.uoSuperirOrigenCodi = uoSuperirOrigenCodi;
	}
	public String getUoSuperiorOrigenNom() {
		return uoSuperiorOrigenNom;
	}
	public void setUoSuperiorOrigenNom(String uoSuperiorOrigenNom) {
		this.uoSuperiorOrigenNom = uoSuperiorOrigenNom;
	}
	public String getUoDestiCodi() {
		return uoDestiCodi;
	}
	public void setUoDestiCodi(String uoDestiCodi) {
		this.uoDestiCodi = uoDestiCodi;
	}
	public String getUoDestiNom() {
		return uoDestiNom;
	}
	public void setUoDestiNom(String uoDestiNom) {
		this.uoDestiNom = uoDestiNom;
	}
	public String getUoSuperiorDestiCodi() {
		return uoSuperiorDestiCodi;
	}
	public void setUoSuperiorDestiCodi(String uoSuperiorDestiCodi) {
		this.uoSuperiorDestiCodi = uoSuperiorDestiCodi;
	}
	public String getUoSuperiorDestiNom() {
		return uoSuperiorDestiNom;
	}
	public void setUoSuperiorDestiNom(String uoSuperiorDestiNom) {
		this.uoSuperiorDestiNom = uoSuperiorDestiNom;
	}	

}
