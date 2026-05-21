/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreFiltreDto implements Serializable {

	private String bustia;
	/** Per mostrar el contingut de les bústies innactives */
	private boolean mostrarInnactives;
	private String numero;
	private String titol;
	private String remitent;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private RegistreProcesEstatSimpleEnumDto procesEstatSimple;
	private String numeroOrigen;
	private String interessat;
	private RegistreTipusDocFisicaEnumDto tipusDocFisica;
	private RegistreEnviatPerEmailEnumDto enviatPerEmail;

	private String backCodi;
	
	private ReglaDto regla;

	private RegistreMarcatPerSobreescriureEnumDto sobreescriure;
	
	private RegistreFiltreReintentsEnumDto reintents;
	
	// Filtre per administradors
	/** Estat específic. */
	private RegistreProcesEstatEnum estat;
	/** Per filtrar només les que tinguin error informat. */
	private boolean nomesAmbErrors;
	/** Unitat organitzativa superior. */
	private Long unitatId;
	
	private String bustiaOrigen;
	
	/** Per mostrar el contingut de les bústies innactives */
	private boolean mostrarInnactivesOrigen;
	
	/** Per filtrar només les que tinguin annexos en estat esborrany. */
	private boolean nomesAmbEsborranys;

    private boolean ambAnnexosInterns;
	
	private String procedimentCodi;
	
	private RegistreNombreAnnexesEnumDto nombreAnnexes;

	private String usuariAssignatCodi;
	
	private boolean mostrarSenseAssignar;

	public boolean isMostrarInactives() {
		return mostrarInnactives;
	}
	public void setMostrarInactives(boolean mostrarInactives) {
		this.mostrarInnactives = mostrarInactives;
	}

	public boolean isMostrarInactivesOrigen() {
		return mostrarInnactivesOrigen;
	}
	public void setMostrarInactivesOrigen(boolean mostrarInactivesOrigen) {
		this.mostrarInnactivesOrigen = mostrarInactivesOrigen;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
