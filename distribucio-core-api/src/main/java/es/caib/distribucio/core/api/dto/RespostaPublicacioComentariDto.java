/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Objecte que forma la resposta de publicar un comentari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaPublicacioComentariDto implements Serializable {

	private boolean publicat;
	private boolean error;
	private List<String> errorsDescripcio = new ArrayList<String>();
	private List<ContingutComentariDto> comentaris;
	
	public boolean isPublicat() {
		return publicat;
	}
	public void setPublicat(boolean publicat) {
		this.publicat = publicat;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public List<String> getErrorsDescripcio() {
		return errorsDescripcio;
	}
	public void setErrorsDescripcio(List<String> errorsDescripcio) {
		this.errorsDescripcio = errorsDescripcio;
	}
	public List<ContingutComentariDto> getComentaris() {
		return comentaris;
	}
	public void setComentaris(List<ContingutComentariDto> comentaris) {
		this.comentaris = comentaris;
	}

	private static final long serialVersionUID = 4865759534265706278L;
}
