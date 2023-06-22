/**
 * 
 */
package es.caib.distribucio.rest.client.regla.domini;

import lombok.Getter;
import lombok.Setter;

/** Classe per transformar de l'API REST quan aquesta és un codi HTTP status i un 
 * missatge de confirmació. Els codis de resposta solen ser:
 * 200 Correcte
 * 401 Error per no trobat.
 * 500 Error intern no controlat.
 * A més es disposa de dos mètodes per obtenir un boolea de si el resultat és correcte o no 
 * segons el codi de retorn de status.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Getter @Setter
public class ReglaResponse  {

	private int status = -1;
    private String msg = null;
    
	public ReglaResponse(
			int status,
			String msg) {
		this.status = status;
		this.msg = msg;
	}
	
	public boolean isCorrecte() {
		return this.status == 200;
	}
	
	public boolean isError() {
		return ! this.isCorrecte();
	}
}
