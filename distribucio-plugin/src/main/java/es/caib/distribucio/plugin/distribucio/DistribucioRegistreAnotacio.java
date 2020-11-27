/**
 * 
 */
package es.caib.distribucio.plugin.distribucio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DistribucioRegistreAnotacio {

	private String arxiuUuid;
	private Date arxiuDataActualitzacio;
	private String entitatCodi;
	private String entitatDescripcio;
	private String numero;
	private String extracte;
	private String expedientArxiuUuid;
	
	
	private List<DistribucioRegistreAnnex> annexos = new ArrayList<DistribucioRegistreAnnex>();
	

	public String getArxiuUuid() {
		return arxiuUuid;
	}

	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}

	public Date getArxiuDataActualitzacio() {
		return arxiuDataActualitzacio;
	}

	public void setArxiuDataActualitzacio(Date arxiuDataActualitzacio) {
		this.arxiuDataActualitzacio = arxiuDataActualitzacio;
	}

	public String getEntitatCodi() {
		return entitatCodi;
	}

	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}

	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}

	public void setEntitatDescripcio(String entitatDescripcio) {
		this.entitatDescripcio = entitatDescripcio;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getExtracte() {
		return extracte;
	}

	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}

	public String getExpedientArxiuUuid() {
		return expedientArxiuUuid;
	}

	public void setExpedientArxiuUuid(String expedientArxiuUuid) {
		this.expedientArxiuUuid = expedientArxiuUuid;
	}

	public List<DistribucioRegistreAnnex> getAnnexos() {
		return annexos;
	}

	public void setAnnexos(List<DistribucioRegistreAnnex> annexos) {
		this.annexos = annexos;
	}

}