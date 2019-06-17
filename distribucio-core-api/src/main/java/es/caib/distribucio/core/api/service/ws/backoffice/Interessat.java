/**
 * 
 */
package es.caib.distribucio.core.api.service.ws.backoffice;

/**
 * Classe que representa interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Interessat extends InteressatBase {

	private Representant representant;

	public Representant getRepresentant() {
		return representant;
	}
	public void setRepresentant(Representant representant) {
		this.representant = representant;
	}

}
