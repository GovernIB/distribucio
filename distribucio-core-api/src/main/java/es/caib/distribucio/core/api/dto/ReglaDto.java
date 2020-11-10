/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una regla per a gestionar anotacions de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglaDto extends AuditoriaDto {

	private Long id;
	
	private String nom;
	private String descripcio;
	
	
	// ------------- FILRE ----------------------
	private String assumpteCodiFiltre;
	private String procedimentCodiFiltre;
	private UnitatOrganitzativaDto unitatOrganitzativaFiltre;
	private Long bustiaFiltreId;

	// ------------- ACCIO  ----------------------
	private ReglaTipusEnumDto tipus;
	private Long bustiaDestiId;
	private Long backofficeDestiId;
	private String backofficeDestiNom;
	private Long unitatDestiId;
	
	
	private int ordre;
	private boolean activa;


	
	public Long getBustiaFiltreId() {
		return bustiaFiltreId;
	}
	public void setBustiaFiltreId(Long bustiaFiltreId) {
		this.bustiaFiltreId = bustiaFiltreId;
	}
	public Long getBackofficeDestiId() {
		return backofficeDestiId;
	}
	public void setBackofficeDestiId(Long backofficeDestiId) {
		this.backofficeDestiId = backofficeDestiId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public ReglaTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ReglaTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getAssumpteCodiFiltre() {
		return assumpteCodiFiltre;
	}
	public void setAssumpteCodiFiltre(String assumpteCodiFiltre) {
		this.assumpteCodiFiltre = assumpteCodiFiltre;
	}
	public String getProcedimentCodiFiltre() {
		return procedimentCodiFiltre;
	}
	public void setProcedimentCodiFiltre(String procedimentCodiFiltre) {
		this.procedimentCodiFiltre = procedimentCodiFiltre;
	}
	public UnitatOrganitzativaDto getUnitatOrganitzativaFiltre() {
		return unitatOrganitzativaFiltre;
	}
	public void setUnitatOrganitzativaFiltre(UnitatOrganitzativaDto unitatOrganitzativaFiltre) {
		this.unitatOrganitzativaFiltre = unitatOrganitzativaFiltre;
	}
	public Long getBustiaDestiId() {
		return bustiaDestiId;
	}
	public void setBustiaDestiId(Long bustiaDestiId) {
		this.bustiaDestiId = bustiaDestiId;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public String getBackofficeDestiNom() {
		return backofficeDestiNom;
	}
	public void setBackofficeDestiNom(String backofficeDestiNom) {
		this.backofficeDestiNom = backofficeDestiNom;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public Long getUnitatDestiId() {
		return unitatDestiId;
	}
	public void setUnitatDestiId(Long unitatDestiId) {
		this.unitatDestiId = unitatDestiId;
	}

}
