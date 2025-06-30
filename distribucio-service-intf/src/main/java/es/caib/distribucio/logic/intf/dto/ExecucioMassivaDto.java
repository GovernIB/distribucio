package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO amb informació d'una execució massiva
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExecucioMassivaDto extends AuditoriaDto implements Serializable {

	private Long id;
	private ExecucioMassivaEstatDto estat;
	private ExecucioMassivaTipusDto tipus;
	private Date dataCreacio;
	private Date dataInici;
	private Date dataFi;
	private UsuariDto usuari;
	private List<ExecucioMassivaContingutDto> continguts;
	private List<Long> contingutIds = new ArrayList<Long>();
	private String parametres;
	private double executades;
	private int errors;
	private int cancelats;
	private boolean emcPausat;

	private static final long serialVersionUID = 7633840731951346114L;

}
