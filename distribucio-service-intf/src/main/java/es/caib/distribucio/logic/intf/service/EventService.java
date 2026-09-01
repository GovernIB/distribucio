package es.caib.distribucio.logic.intf.service;

import es.caib.distribucio.logic.intf.model.sse.AvisosActiusEvent;

/**
 * Declaració dels mètodes per a la gestió d'esdeveniments SSE (Server-Sent Events).
 * <p/>
 * De moment només s'hi publiquen els avisos: la capa de negoci avisa amb
 * {@link #notifyAvisosActius()} que la taula {@code dis_avis} ha canviat, i
 * {@code SseResourceController} consulta amb {@link #getAvisosActius(String, Long)} què ha de
 * veure cada connexió oberta.
 * <p/>
 * Cap mètode no du {@code @PreAuthorize}: {@link #getAvisosActius(String, Long)} es crida des del
 * fil del {@code @JmsListener}, que no ve de cap petició web i per tant no té cap usuari
 * autenticat al context. Qui pot subscriure's -- i amb quin rol i entitat-- es comprova una sola
 * vegada, al controlador, quan s'obre la connexió (que sí que és una petició autenticada).
 *
 * @author Límit Tecnologies
 */
public interface EventService {

	/**
	 * Notifica a totes les connexions obertes que els avisos actius han canviat.
	 */
	void notifyAvisosActius();

	/**
	 * Retorna els avisos actius que ha de veure un usuari que opera amb el rol i l'entitat
	 * indicats, amb el mateix criteri que la interfície JSP ({@code AvisHelper.findAvisos}):
	 * amb el rol de superusuari tots els avisos actius, i amb qualsevol altre rol només els
	 * globals (sense entitat) i els de l'entitat de treball.
	 *
	 * @param rol rol amb què opera l'usuari.
	 * @param entitatId identificador de l'entitat de treball, o null si no n'hi ha cap.
	 * @return els avisos actius filtrats.
	 */
	AvisosActiusEvent getAvisosActius(String rol, Long entitatId);

}
