/**
 *
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * Excepció guardada al log d'excepcions (taula dis_excepcio_log).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExcepcioLogDto implements Serializable {

	private Long id;
	private Date data;
	private String tipus;
	private String objectId;
	private String objectClass;
	private String uri;
	private String origen;
	private String param1;
	private String param2;
	private String message;
	private String stacktrace;
	private String entitatCodi;

	private static final long serialVersionUID = -139254994389509932L;

}
