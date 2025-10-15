/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class EntitatDto extends AuditoriaDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String cif;
	private String codiDir3;
	private boolean activa;

	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualAdministration;
	private boolean usuariActualAdminLectura;

	private byte[] logoCapBytes;
	private String logoExtension;
	private boolean eliminarLogoCap;
	private String colorFons;
	private String colorLletra;

    private Timestamp fechaActualizacion;
    private Timestamp fechaSincronizacion;

	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
