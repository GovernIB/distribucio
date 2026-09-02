/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

/**
 * Interfície d'usuari amb la que s'entra a l'aplicació.
 * <p>
 * Es tria per usuari al seu perfil (columna {@code dis_usuari.interficie_usuari}); si l'usuari
 * no n'ha triat cap mana la propietat {@code es.caib.distribucio.interface.defecte} i, si
 * aquesta tampoc té valor, {@link #REACT} (veure {@code DistribucioController.get()}).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum InterficieUsuariEnumDto {
	/** Interfície clàssica: JSP + Spring MVC. */
	JSP,
	/** Interfície moderna: aplicació REACT servida a /reactapp. */
	REACT
}
