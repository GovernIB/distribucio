package es.caib.distribucio.logic.intf.model.sse;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Esdeveniment amb els avisos actius que ha de veure un usuari.
 * <p/>
 * Fa dos papers, igual que a RIPEA:
 * <ul>
 *     <li>Com a <b>missatge JMS</b> viatja buit: només és el senyal que els avisos han canviat.
 *     Qui el rep ({@code SseResourceController}) torna a consultar els avisos de cada usuari
 *     connectat, perquè el resultat depèn del rol i de l'entitat de cada connexió.</li>
 *     <li>Com a <b>event SSE</b> viatja amb la llista ja filtrada per a aquella connexió.</li>
 * </ul>
 *
 * @author Límit Tecnologies
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvisosActiusEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AvisSseDto> avisos;

}
