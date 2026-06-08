/**
 * 
 */
package es.caib.distribucio.back.command;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.RegistreEnviatPerEmailEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreFiltreDto;
import es.caib.distribucio.logic.intf.dto.RegistreFiltreReintentsEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreMarcatPerSobreescriureEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreNombreAnnexesEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreTipusDocFisicaEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreFiltreCommand {

	private String bustia;
	/** Per mostrar el contingut de les bústies innactives */
	private boolean mostrarInactives;
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
	private String backObservacions;
	
	private ReglaDto regla;
	
	private RegistreMarcatPerSobreescriureEnumDto sobreescriure;
	
	private RegistreFiltreReintentsEnumDto reintents;

	// Filtre per administradors
	/** Estat específic. */
	private RegistreProcesEstatEnum estat;
	/** Per filtrar només les que tinguin error informat. */
	private boolean nomesAmbErrors;
    private boolean ambAnnexosInterns;
	/** Unitat organitzativa superior. */
	private Long unitatId;
	
	private String bustiaOrigen;
	
	/** Per mostrar el contingut de les bústies origen innactives */
	public boolean mostrarInactivesOrigen;

	/** Per filtrar només les que tinguin annexos en estat esborrany. */
	private boolean nomesAmbEsborranys;
	
	private String procedimentCodi;
	
	private RegistreNombreAnnexesEnumDto nombreAnnexes;

	private String usuariAssignatCodi;

	/** Per mostrar anotacions sense assignar */
	private boolean mostrarSenseAssignar;

    private boolean advancedSearchActive;
	
	public static RegistreFiltreCommand asCommand(RegistreFiltreDto dto) {
		RegistreFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				RegistreFiltreCommand.class);
		return command;
	}
	public static RegistreFiltreDto asDto(RegistreFiltreCommand command) {
		RegistreFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				RegistreFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
