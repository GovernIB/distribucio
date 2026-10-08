package es.caib.distribucio.logic.intf.model;

/**
 * Estil visual del menú lateral de la interfície REACT.
 *
 * @author Límit Tecnologies
 */
public enum MenuEstilEnum {
	/** El menú reutilitza la paleta de colors activa (la del tema de l'aplicació). */
	TEMA,
	/** El menú utilitza una paleta invertida respecte del tema actiu (clar si el tema és fosc i viceversa). */
	TEMA_INVERTIT,
	/** El menú utilitza una paleta fixa (gris), independent del tema actiu. */
	PEU
}
