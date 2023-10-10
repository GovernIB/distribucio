package es.caib.distribucio.back.command;

import javax.validation.constraints.NotEmpty;

public class RegistreAssignarCommand {
	
	@NotEmpty
	private String usuariCodi;
	private String comentari;
	
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	
}

