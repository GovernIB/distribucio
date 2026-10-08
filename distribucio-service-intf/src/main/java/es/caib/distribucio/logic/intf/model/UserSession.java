package es.caib.distribucio.logic.intf.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Informació de la sessió d'usuari (entitat seleccionada), enviada pel front en cada petició
 * mitjançant la capçalera {@code X-App-Session}.
 *
 * @author Límit Tecnologies
 */
@Getter
@AllArgsConstructor
public class UserSession implements Serializable {

	private Long entitatId;

}
