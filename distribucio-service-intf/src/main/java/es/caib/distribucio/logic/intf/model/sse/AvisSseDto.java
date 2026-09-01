package es.caib.distribucio.logic.intf.model.sse;

import java.io.Serializable;
import java.util.Date;

import es.caib.distribucio.logic.intf.dto.AvisNivellEnumDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dades d'un avís tal com viatgen cap al client per SSE.
 * <p/>
 * No es reutilitza {@link es.caib.distribucio.logic.intf.dto.AvisDto} a posta: aquell duu
 * l'entitat sencera ({@code EntitatDto}, amb el logo en un {@code byte[]}), que no fa cap falta
 * al client i multiplicaria la mida de cada missatge enviat a totes les connexions obertes.
 * Aquí només hi ha el que pinta el banner d'avisos.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvisSseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String assumpte;
	private String missatge;
	private AvisNivellEnumDto avisNivell;
	private Date dataInici;
	private Date dataFinal;

}
