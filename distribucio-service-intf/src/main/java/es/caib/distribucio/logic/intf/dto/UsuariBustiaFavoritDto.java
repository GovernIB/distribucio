/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Bústia favorit d'un usuari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariBustiaFavoritDto extends AuditoriaDto {

	protected Long id;
	private BustiaDto bustia;
	private UsuariDto usuari;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BustiaDto getBustia() {
		return bustia;
	}
	public void setBustia(BustiaDto bustia) {
		this.bustia = bustia;
	}
	public UsuariDto getUsuari() {
		return usuari;
	}
	public void setUsuari(UsuariDto usuari) {
		this.usuari = usuari;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
