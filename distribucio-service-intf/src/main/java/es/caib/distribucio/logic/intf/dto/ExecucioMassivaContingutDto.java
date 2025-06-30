package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO amb informació del contingut d'una execució massiva
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ExecucioMassivaContingutDto extends AuditoriaDto implements Serializable {

	private Long id;
	private Date dataCreacio;
	private Date dataInici;
	private Date dataFi;
	private ExecucioMassivaContingutEstatDto estat;
	private String error;
	private int ordre;
	private ExecucioMassivaDto execucioMassiva;
	private String missatge;
	private Long elementId;
	private String elementNom;
	private ElementTipusEnumDto elementTipus;

	public String getDataCreacioAmbFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dataCreacio != null ? sdf.format(dataCreacio) : "";
	}
	
	public String getDataIniciAmbFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dataInici != null ? sdf.format(dataInici) : "";
	}
	
	public String getDataFiAmbFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dataFi != null ? sdf.format(dataFi) : "";
	}
	
	private static final long serialVersionUID = 6061543051665185101L;
	
}
