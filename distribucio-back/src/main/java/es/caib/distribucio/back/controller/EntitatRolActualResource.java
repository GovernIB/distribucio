package es.caib.distribucio.back.controller;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Estat actual del selector d'entitat/rol per a l'usuari autenticat (equivalent al que la
 * interfície JSP calcula a cada petició via {@code EntitatHelper}/{@code RolHelper} i mostra al
 * decorador de pàgina).
 *
 * @author Límit Tecnologies
 */
@Getter
@AllArgsConstructor
public class EntitatRolActualResource {

	private final Long entitatActualId;
	private final String rolActual;
	private final List<String> rolsDisponibles;

}
