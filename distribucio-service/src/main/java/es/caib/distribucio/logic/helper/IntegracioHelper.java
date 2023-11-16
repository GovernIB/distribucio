/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDto;
import es.caib.distribucio.logic.intf.dto.MonitorIntegracioDto;
import es.caib.distribucio.logic.intf.dto.MonitorIntegracioParamDto;
import es.caib.distribucio.logic.intf.service.MonitorIntegracioService;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class IntegracioHelper {

	public static final String INTCODI_USUARIS = "USUARIS";
	public static final String INTCODI_UNITATS = "UNITATS";
	public static final String INTCODI_ARXIU = "ARXIU";
	public static final String INTCODI_DADESEXT = "DADESEXT";
	public static final String INTCODI_SIGNATURA = "SIGNATURA";
	public static final String INTCODI_VALIDASIG = "VALIDASIG";
	public static final String INTCODI_GESDOC = "GESDOC";
	public static final String INTCODI_BUSTIAWS = "BUSTIAWS";
	public static final String INTCODI_PROCEDIMENT = "PROCEDIMENT";
	public static final String INTCODI_DISTRIBUCIO = "DISTRIBUCIO";
	public static final String INTCODI_BACKOFFICE = "BACKOFFICE";

	@Autowired 
	private MonitorIntegracioService monitorIntegracioService;

//	@Autowired
//	private MonitorIntegracioRepository monitorIntegracioRepository;
//	@Autowired
//	private MonitorIntegracioParamRepository monitorIntegracioParamRepository;

	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(
				novaIntegracio(
						INTCODI_UNITATS));
		integracions.add(
				novaIntegracio(
						INTCODI_ARXIU));
		integracions.add(
				novaIntegracio(
						INTCODI_DADESEXT));
		integracions.add(
				novaIntegracio(
						INTCODI_SIGNATURA));
		integracions.add(
				novaIntegracio(
						INTCODI_VALIDASIG));
		integracions.add(
				novaIntegracio(
						INTCODI_GESDOC));
		integracions.add(
				novaIntegracio(
						INTCODI_BUSTIAWS));
		integracions.add(
				novaIntegracio(
						INTCODI_PROCEDIMENT));
		integracions.add(
				novaIntegracio(
						INTCODI_DISTRIBUCIO));
		integracions.add(
				novaIntegracio(
						INTCODI_BACKOFFICE));
				
		return integracions;
	}

	public List<IntegracioDto> findPerDiagnostic() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(
				novaIntegracio(
						INTCODI_USUARIS));
		integracions.add(
				novaIntegracio(
						INTCODI_UNITATS));
		integracions.add(
				novaIntegracio(
						INTCODI_ARXIU));
		integracions.add(
				novaIntegracio(
						INTCODI_DADESEXT));
		integracions.add(
				novaIntegracio(
						INTCODI_SIGNATURA));
		integracions.add(
				novaIntegracio(
						INTCODI_VALIDASIG));
		integracions.add(
				novaIntegracio(
						INTCODI_GESDOC));
		integracions.add(
				novaIntegracio(
						INTCODI_PROCEDIMENT));
				
		return integracions;
	}

	public void addAccioOk(
			String integracioCodi,
			String descripcio,
			String usuariIntegracio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {
		MonitorIntegracioDto accio = new MonitorIntegracioDto();
		accio.setCodi(integracioCodi);
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setCodiUsuari(usuariIntegracio);
		accio.setCodiEntitat(ConfigHelper.getEntitatActualCodi());
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.OK);
//		accio.setParametres(this.buildParams(parametres));
		monitorIntegracioService.create(accio);
//		monitorIntegracioCreate(accio);
		logger.debug(descripcio + ", Parametres: " + parametres + ", Temps resposta: " + tempsResposta);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			String usuariIntegracio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio) {
		addAccioError(
				integracioCodi,
				descripcio,
				usuariIntegracio,
				parametres,
				tipus,
				tempsResposta,
				errorDescripcio,
				null);
	}

	public void addAccioError(
			String integracioCodi,
			String descripcio,
			String usuariIntegracio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		MonitorIntegracioDto accio = new MonitorIntegracioDto();
		accio.setCodi(integracioCodi);
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setCodiUsuari(usuariIntegracio);
		accio.setCodiEntitat(ConfigHelper.getEntitatActualCodi());
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);		
		if (throwable != null){
			accio.setExcepcioMessage(
					ExceptionUtils.getMessage(throwable));
			accio.setExcepcioStacktrace(
					ExceptionUtils.getStackTrace(throwable));
		}
		accio.setParametres(this.buildParams(parametres));
		monitorIntegracioService.create(accio);
		logger.error("Error d'integracio " + descripcio + ": " + errorDescripcio + "("
				+ "integracioCodi=" + integracioCodi + ", "
				+ "parametres=" + parametres + ", "
				+ "tipus=" + tipus + ", "
				+ "usuariIntegracio=" + usuariIntegracio + ", "
				+ "tempsResposta=" + tempsResposta + ")",
				throwable);
	}


	private List<MonitorIntegracioParamDto> buildParams(Map<String, String> parametres) {
		List<MonitorIntegracioParamDto> parametresDto = new ArrayList<>();
		if (parametres != null && !parametres.isEmpty()) {
			for (String nom : parametres.keySet()) {
				MonitorIntegracioParamDto paramDto = new MonitorIntegracioParamDto();
				paramDto.setNom(nom);
				paramDto.setDescripcio(parametres.get(nom));
				parametresDto.add(paramDto);
			}
		}
		return parametresDto;
	}

	private IntegracioDto novaIntegracio(
			String codi) {
		IntegracioDto integracio = new IntegracioDto();
		integracio.setCodi(codi);
		if (INTCODI_USUARIS.equals(codi)) {
			integracio.setNom("Usuaris");
		} else if (INTCODI_UNITATS.equals(codi)) {
			integracio.setNom("Unitats admin.");
		} else if (INTCODI_ARXIU.equals(codi)) {
			integracio.setNom("Arxiu digital");
		} else if (INTCODI_DADESEXT.equals(codi)) {
			integracio.setNom("Dades ext.");
		} else if (INTCODI_SIGNATURA.equals(codi)) {
			integracio.setNom("Signatura");
		} else if (INTCODI_VALIDASIG.equals(codi)) {
			integracio.setNom("Valida sig.");
		} else if (INTCODI_GESDOC.equals(codi)) {
			integracio.setNom("Gestió doc.");
		} else if (INTCODI_BUSTIAWS.equals(codi)) {
			integracio.setNom("Bústia WS");
		} else if (INTCODI_PROCEDIMENT.equals(codi)) {
			integracio.setNom("Procediments");
		} else if (INTCODI_DISTRIBUCIO.equals(codi)) {
			integracio.setNom("Distribució");
		} else if (INTCODI_BACKOFFICE.equals(codi)) {
			integracio.setNom("Backoffice");
		}
		return integracio;
	}

	private static final Logger logger = LoggerFactory.getLogger(IntegracioHelper.class);

}
