package es.caib.distribucio.logic.intf.dto;

import java.util.List;

/**
 * Estructura per mostrar el número de còpies d'un registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreCopiesDto {

	private boolean registreTrobat;
	private List<Integer> copies;
	
	public boolean isRegistreTrobat() {
		return registreTrobat;
	}
	public void setRegistreTrobat(boolean registreTrobat) {
		this.registreTrobat = registreTrobat;
	}
	public List<Integer> getCopies() {
		return copies;
	}
	public void setCopies(List<Integer> copies) {
		this.copies = copies;
	}
	
}
