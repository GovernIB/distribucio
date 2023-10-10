/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatDto extends AuditoriaDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String cif;
	private String codiDir3;
	private boolean activa;

	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualAdministration;
	private boolean usuariActualAdminLectura;

	private byte[] logoCapBytes;
	private String logoExtension;
	private boolean eliminarLogoCap;
	private String colorFons;
	private String colorLletra;
	
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public String getCodiDir3() {
		return codiDir3;
	}
	public void setCodiDir3(String codiDir3) {
		this.codiDir3 = codiDir3;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
	}
	public boolean isUsuariActualAdministration() {
		return usuariActualAdministration;
	}
	public void setUsuariActualAdministration(boolean usuariActualAdministration) {
		this.usuariActualAdministration = usuariActualAdministration;
	}
	public boolean isUsuariActualAdminLectura() {
		return usuariActualAdminLectura;
	}
	public void setUsuariActualAdminLectura(boolean usuariActualAdminLectura) {
		this.usuariActualAdminLectura = usuariActualAdminLectura;
	}
	public byte[] getLogoCapBytes() {
		return logoCapBytes;
	}
	public void setLogoCapBytes(byte[] logoCapBytes) {
		this.logoCapBytes = logoCapBytes;
	}
	public String getLogoExtension() {
		return logoExtension;
	}
	public void setLogoExtension(String logoExtension) {
		this.logoExtension = logoExtension;
	}
	public boolean isEliminarLogoCap() {
		return eliminarLogoCap;
	}
	public void setEliminarLogoCap(boolean eliminarLogoCap) {
		this.eliminarLogoCap = eliminarLogoCap;
	}
	public String getColorFons() {
		return colorFons;
	}
	public void setColorFons(String colorFons) {
		this.colorFons = colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public void setColorLletra(String colorLletra) {
		this.colorLletra = colorLletra;
	}
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
