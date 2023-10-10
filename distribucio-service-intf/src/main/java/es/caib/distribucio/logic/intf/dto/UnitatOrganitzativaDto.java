/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;




/**
 * Informació d'una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatOrganitzativaDto implements Serializable {

	private Long id;
	private String codi;
	private String denominacio;
	private String nifCif;
	private String codiUnitatSuperior;
	private String codiUnitatArrel;
	private Date dataCreacioOficial;
	private Date dataSupressioOficial;
	private Date dataExtincioFuncional;
	private Date dataAnulacio;
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio

	private String codiPais;
	private String codiComunitat;
	private String codiProvincia;
	private String codiPostal;
	private String nomLocalitat;
	private String localitat;

	private String adressa;
	private Long tipusVia;
	private String nomVia;
	private String numVia; 
	
	private TipusTransicioEnumDto tipusTransicio;
	
	private List<UnitatOrganitzativaDto> noves;
	private Map<String, String> altresUnitatsFusionades;

	private List<UnitatOrganitzativaDto> lastHistoricosUnitats;
	
	private String denominacioUnitatSuperior;
	
	
	public boolean isObsoleta() {
		if (estat.equals("E") || estat.equals("A") || estat.equals("T")) {
			return true;
		} else {
			return false;
		}
	}
	public String getCodiIDenominacioUnitatSuperior() {
		return codiUnitatSuperior  + (denominacioUnitatSuperior != null ? " - " + denominacioUnitatSuperior : "");
	}

	public String getDenominacioUnitatSuperior() {
		return denominacioUnitatSuperior;
	}
	public void setDenominacioUnitatSuperior(String denominacioUnitatSuperior) {
		this.denominacioUnitatSuperior = denominacioUnitatSuperior;
	}
	public List<UnitatOrganitzativaDto> getLastHistoricosUnitats() {
		return lastHistoricosUnitats;
	}

	public void setLastHistoricosUnitats(List<UnitatOrganitzativaDto> lastHistoricosUnitats) {
		this.lastHistoricosUnitats = lastHistoricosUnitats;
	}
	
	public Map<String, String> getAltresUnitatsFusionades() {
		return altresUnitatsFusionades;
	}
	public void setAltresUnitatsFusionades(Map<String, String> altresUnitatsFusionades) {
		this.altresUnitatsFusionades = altresUnitatsFusionades;
	}
	public TipusTransicioEnumDto getTipusTransicio() {
		return tipusTransicio;
	}
	public void setTipusTransicio(TipusTransicioEnumDto tipusTransicio) {
		this.tipusTransicio = tipusTransicio;
	}
	public List<UnitatOrganitzativaDto> getNoves() {
		return noves;
	}
	public void setNoves(List<UnitatOrganitzativaDto> noves) {
		this.noves = noves;
	}
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
	public String getDenominacio() {
		return denominacio;
	}
	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}
	public String getNifCif() {
		return nifCif;
	}
	public void setNifCif(String nifCif) {
		this.nifCif = nifCif;
	}
	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}
	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}
	public String getCodiUnitatArrel() {
		return codiUnitatArrel;
	}
	public void setCodiUnitatArrel(String codiUnitatArrel) {
		this.codiUnitatArrel = codiUnitatArrel;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public Date getDataCreacioOficial() {
		return dataCreacioOficial;
	}
	public void setDataCreacioOficial(Date dataCreacioOficial) {
		this.dataCreacioOficial = dataCreacioOficial;
	}
	public Date getDataSupressioOficial() {
		return dataSupressioOficial;
	}
	public void setDataSupressioOficial(Date dataSupressioOficial) {
		this.dataSupressioOficial = dataSupressioOficial;
	}
	public Date getDataExtincioFuncional() {
		return dataExtincioFuncional;
	}
	public void setDataExtincioFuncional(Date dataExtincioFuncional) {
		this.dataExtincioFuncional = dataExtincioFuncional;
	}
	public Date getDataAnulacio() {
		return dataAnulacio;
	}
	public void setDataAnulacio(Date dataAnulacio) {
		this.dataAnulacio = dataAnulacio;
	}
	public String getCodiPais() {
		return codiPais;
	}
	public void setCodiPais(String codiPais) {
		this.codiPais = codiPais;
	}
	public String getCodiComunitat() {
		return codiComunitat;
	}
	public void setCodiComunitat(String codiComunitat) {
		this.codiComunitat = codiComunitat;
	}
	public String getCodiProvincia() {
		return codiProvincia;
	}
	public void setCodiProvincia(String codiProvincia) {
		this.codiProvincia = codiProvincia;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}
	public String getNomLocalitat() {
		return nomLocalitat;
	}
	public void setNomLocalitat(String nomLocalitat) {
		this.nomLocalitat = nomLocalitat;
	}
	public String getLocalitat() {
		return localitat;
	}
	public void setLocalitat(String localitat) {
		this.localitat = localitat;
	}
	public String getAdressa() {
		return adressa;
	}
	public void setAdressa(String adressa) {
		this.adressa = adressa;
	}
	public Long getTipusVia() {
		return tipusVia;
	}
	public void setTipusVia(Long tipusVia) {
		this.tipusVia = tipusVia;
	}
	public String getNomVia() {
		return nomVia;
	}
	public void setNomVia(String nomVia) {
		this.nomVia = nomVia;
	}
	public String getNumVia() {
		return numVia;
	}
	public void setNumVia(String numVia) {
		this.numVia = numVia;
	}

	public String getNom() {
		return this.denominacio + " (" + this.codi + ")";
	}
	
	public String getCodiAndNom() {
		return this.codi + " - " + this.denominacio + "";
	}
	
	private static final long serialVersionUID = -5602898182576627524L;

}
