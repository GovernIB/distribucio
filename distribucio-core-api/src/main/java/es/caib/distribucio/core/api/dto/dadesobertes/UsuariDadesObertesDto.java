package es.caib.distribucio.core.api.dto.dadesobertes;

import java.io.Serializable;

/** Classe per retornar les dades d'usuaris per bústies a l'API REST de dades obertes.
 *
 */
public class UsuariDadesObertesDto implements Serializable{
	
	private static final long serialVersionUID = -2771550393162310506L;

	private String usuari;
	private String usuariNom;
	private Long bustiaId;
	private String bustiaNom;
	private String uo;
	private String uoNom;
	private String uoSuperior;
	private String uoSuperiorNom;
	private boolean rol;
	private boolean permis;
	
	
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public String getUsuariNom() {
		return usuariNom;
	}
	public void setUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}
	public String getBustiaNom() {
		return bustiaNom;
	}
	public void setBustiaNom(String bustiaNom) {
		this.bustiaNom = bustiaNom;
	}
	public String getUo() {
		return uo;
	}
	public void setUo(String uo) {
		this.uo = uo;
	}
	public String getUoNom() {
		return uoNom;
	}
	public void setUoNom(String uoNom) {
		this.uoNom = uoNom;
	}
	public String getUoSuperior() {
		return uoSuperior;
	}
	public void setUoSuperior(String uoSuperior) {
		this.uoSuperior = uoSuperior;
	}
	public String getUoSuperiorNom() {
		return uoSuperiorNom;
	}
	public void setUoSuperiorNom(String uoSuperiorNom) {
		this.uoSuperiorNom = uoSuperiorNom;
	}
	public boolean isRol() {
		return rol;
	}
	public void setRol(boolean rol) {
		this.rol = rol;
	}
	public boolean isPermis() {
		return permis;
	}
	public void setPermis(boolean permis) {
		this.permis = permis;
	}
	
	

}
