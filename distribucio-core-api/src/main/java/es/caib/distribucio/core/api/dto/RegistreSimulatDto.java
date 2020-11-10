/**
 * 
 */
package es.caib.distribucio.core.api.dto;

/**
 * Classe que simula anotacio de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreSimulatDto {

	private Long unitatId;
	private Long bustiaId;
	private String assumpteCodi;
	private String procedimentCodi;


	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
}
