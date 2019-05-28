/**
 * 
 */
package es.caib.distribucio.core.api.service.ws.backoffice;

/**
 * Classe que representa una anotació de registre entrada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioRegistreEntrada extends AnotacioRegistreBase {

	private String destiCodi;
	private String destiDescripcio;

	public String getDestiCodi() {
		return destiCodi;
	}
	public void setDestiCodi(String destiCodi) {
		this.destiCodi = destiCodi;
	}
	public String getDestiDescripcio() {
		return destiDescripcio;
	}
	public void setDestiDescripcio(String destiDescripcio) {
		this.destiDescripcio = destiDescripcio;
	}

}
