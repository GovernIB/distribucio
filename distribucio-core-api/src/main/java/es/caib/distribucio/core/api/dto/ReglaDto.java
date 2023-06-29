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
	private String bustiaFiltreNom;
	private ReglaPresencialEnumDto presencial;

	// ------------- ACCIO  ----------------------
	private ReglaTipusEnumDto tipus;
	private Long bustiaDestiId;
	private String bustiaDestiNom;
	private Long backofficeDestiId;
	private String backofficeDestiNom;
	private Long unitatDestiId;
	private String unitatDestiNom;
	
	private int ordre;
	private boolean activa;
	
	private Long entitatId;
	private String entitatNom;
	

	private UnitatOrganitzativaDto unitatDesti;

	
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
	public ReglaPresencialEnumDto getPresencial() {
		return presencial;
	}
	public void setPresencial(ReglaPresencialEnumDto presencial) {
		this.presencial = presencial;
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
	public UnitatOrganitzativaDto getUnitatDesti() {
		return unitatDesti;
	}
	public void setUnitatDesti(UnitatOrganitzativaDto unitatDesti) {
		this.unitatDesti = unitatDesti;
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
	public String getBustiaFiltreNom() {
		return bustiaFiltreNom;
	}
	public void setBustiaFiltreNom(String bustiaFiltreNom) {
		this.bustiaFiltreNom = bustiaFiltreNom;
	}
	public String getBustiaDestiNom() {
		return bustiaDestiNom;
	}
	public void setBustiaDestiNom(String bustiaDestiNom) {
		this.bustiaDestiNom = bustiaDestiNom;
	}
	public String getUnitatDestiNom() {
		return unitatDestiNom;
	}
	public void setUnitatDestiNom(String unitatDestiNom) {
		this.unitatDestiNom = unitatDestiNom;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	
}
