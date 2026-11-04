/**
 * 
 */
package es.caib.distribucio.logic.intf.registre.v1;

import es.caib.distribucio.logic.intf.registre.RegistreAnotacioBase;
import lombok.Getter;
import lombok.Setter;


/**
 * Classe que representa una anotació de registre (V1).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreAnotacioV1 extends RegistreAnotacioBase {

    private String siaCodi;//19 (Long to String)

}
