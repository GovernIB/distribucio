package es.caib.distribucio.core.service.ws.backoffice;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.distribucio.backoffice.utils.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.ArxiuResultatAnnex;
import es.caib.distribucio.backoffice.utils.BackofficeUtils;
import es.caib.distribucio.backoffice.utils.BackofficeUtilsImpl;
import es.caib.distribucio.backoffice.utils.DistribucioArxiuError;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.core.helper.IntegracioHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.BackofficeIntegracio;
import es.caib.distribucio.ws.client.BackofficeIntegracioWsClientFactory;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

/**
 * Exemple de WS Backoffice Distribucio per rebre peticions d'anotacions pendents
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "Backoffice",
		serviceName = "BackofficeService",
		portName = "BackofficeServicePort",
		endpointInterface = "es.caib.ripea.core.api.service.ws.BackofficeWsServiceBean",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
public class BackofficeWsServiceImpl implements BackofficeWsService, 
												ArxiuPluginListener {
	
	/** Instància del plugin d'Arxiu */
	private IArxiuPlugin arxiuPlugin = null;
		
	/** Mètode del WS que rep les comunicacions d'anotacions pendents. */
	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {

		try {
			// Client dels serveis web de backoffice per consultar anotacions
			BackofficeIntegracio backofficeClient = getBackofficeIntegracioServicePort();
			es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId idWs;
			for (AnotacioRegistreId id : ids) {
				try {
					// Construeix l'identificador pel WS del backoffice de DISTRIBUCIO
					idWs = new es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId();
					idWs.setClauAcces(id.getClauAcces());
					idWs.setIndetificador(id.getIndetificador());
		
					// Consulta l'anotació
					AnotacioRegistreEntrada anotacio = backofficeClient.consulta(idWs);
					// Canvia l'estat a Rebuda
					backofficeClient.canviEstat(
							idWs,
							es.caib.distribucio.ws.backofficeintegracio.Estat.REBUDA,
							"Canviar l'estat a rebuda");
					
					// Prepara la cria a la llibreria d'utilitats pel backoffice de DISTRIBUCIO
					// Constructor amb la referència al plugin d'Arxiu
					BackofficeUtils backofficeUtils = new BackofficeUtilsImpl(getArxiuPlugin());
					// Afegeix la instància de la classe com a escoltador d'events
					backofficeUtils.setArxiuPluginListener(this);
					// Estableix la carpeta on guardar els annexos de l'anotació
					backofficeUtils.setCarpeta(anotacio.getIdentificador());
		
					// Crida a la creació de l'expedient
					String SERIE_DOCUMENTAL = "S0002"; 
					String CLASSIFICACIO = "000000";
					ArxiuResultat arxiuResultat;
					int intent = 0;
					String expedientUuid = null;
					do {
						// Crida al mètode de creació de la llibreria
						arxiuResultat = backofficeUtils.crearExpedientAmbAnotacioRegistre(
								expedientUuid,
								anotacio.getIdentificador(),
								null,
								Arrays.asList("A04019281"),
								new Date(),
								CLASSIFICACIO,
								ExpedientEstatEnumDto.OBERT,
								SERIE_DOCUMENTAL,
								anotacio);
						expedientUuid = arxiuResultat.getIdentificadorExpedient();
						// Imprimeix el resultat per pantalla
						this.logResultat(
								"Resultat de la crida " + intent++ + " crearExpedientAmbAnotacioRegistre per l'expedient" + anotacio.getExpedientNumero(),
								arxiuResultat);					
					} while(arxiuResultat.getErrorCodi() != DistribucioArxiuError.NO_ERROR && intent < 10);	

					// Consultem el nou expedient a l'Arxiu
					if (expedientUuid != null) {
						Expedient expedientDetalls = getArxiuPlugin().expedientDetalls(arxiuResultat.getIdentificadorExpedient(), null);
						logger.debug("S'ha creat l'expedient \"" + expedientDetalls.getNom() + "\" amb id=" + expedientDetalls.getIdentificador() + " per l'anotació " + anotacio.getIdentificador() + " amb els següents continguts:");
						for (ContingutArxiu contingut : expedientDetalls.getContinguts()) {
							logger.debug("- " + contingut.getIdentificador() + " " + contingut.getNom() + " amb " + (contingut.getFirmes() != null? contingut.getFirmes().size() : 0) + " firmes.");
						}
					} else {
						logger.warn("L'expedient no s'ha pogut crear per l'anotació " + anotacio.getIdentificador());
					}
					// Es comunica el resultat a DISTRIBUCIO
					switch(arxiuResultat.getErrorCodi()) {
						case 0:
							backofficeClient.canviEstat(idWs, es.caib.distribucio.ws.backofficeintegracio.Estat.PROCESSADA, "Processada");
							break;
						default:
							backofficeClient.canviEstat(idWs, es.caib.distribucio.ws.backofficeintegracio.Estat.ERROR, arxiuResultat.getErrorCodi() + " " + arxiuResultat.getErrorMessage());
							break;
					}
				} catch (Throwable ex) {
					logger.error("Error al processant la petició d'anotació amb id " + id.getIndetificador(), ex);
				}
			}			
		} catch (Exception e) {
			logger.error("Error no controlat en el mètode de comunicació d'anotacions pendents: " + e.getMessage(), e);
		}
	}


	
	
	private void logResultat(String descripcio, ArxiuResultat arxiuResultat) {
		logger.debug(descripcio);
		// Resultat a nivell d'expedient
		logger.debug("- uuid: " + arxiuResultat.getIdentificadorExpedient());
		logger.debug("- accio: " + arxiuResultat.getAccio());
		logger.debug("- errorCodi: " + arxiuResultat.getErrorCodi());
		logger.debug("- errorMessage: " + arxiuResultat.getErrorMessage());
		logger.debug("- excepcio: " + (arxiuResultat.getException() != null? arxiuResultat.getException().getClass() + " " + arxiuResultat.getException().getMessage() :  " - "));

		// Resultat pels annexos
		List<ArxiuResultatAnnex> resultatAnnexos = arxiuResultat.getResultatAnnexos(); 
		logger.debug(" - Resultat dels " + resultatAnnexos.size() + " annexos:");
		for (ArxiuResultatAnnex resultatAnnex : resultatAnnexos) {
			logger.debug("\b- uuid: " + resultatAnnex.getIdentificadorAnnex());
			logger.debug("\\b- accio: " + resultatAnnex.getAccio());
			logger.debug("\\b- errorCodi: " + resultatAnnex.getErrorCodi());
			logger.debug("\\b- errorMessage: " + resultatAnnex.getErrorMessage());
			logger.debug("\\b- excepcio: " + (resultatAnnex.getException() != null? resultatAnnex.getException().getClass() + " " + resultatAnnex.getException().getMessage() :  " - "));
		}
	}

	private IArxiuPlugin getArxiuPlugin() throws SistemaExternException {
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (PropertiesHelper.getProperties().isLlegirSystem()) {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.distribucio.");
					} else {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"es.caib.distribucio.",
								PropertiesHelper.getProperties().findAll());
					}
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_ARXIU,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_ARXIU,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}
	private String getPropertyPluginArxiu() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	
	private BackofficeIntegracio getBackofficeIntegracioServicePort() throws Exception {
		BackofficeIntegracio wsClient = null;
		String url = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.backoffice.test.backofficeIntegracio.url");
		String usuari = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.backoffice.test.backofficeIntegracio.usuari");
		String contrasenya = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.backoffice.test.backofficeIntegracio.contrasenya");
		if (url != null && usuari != null && contrasenya != null) {
			logger.debug(">>> Creant el client BackofficeIntegracio WS");
			wsClient = BackofficeIntegracioWsClientFactory.getWsClient(
					url,
					usuari,
					contrasenya);
		} else {
			throw new RuntimeException("Falta configurar les propietats pel client de Backoffice de DISTRIBUCIO  es.caib.distribucio.backoffice.test.backofficeIntegracio.*");
		}
		return wsClient;
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);

	/** Mètode que la llibreria del backoffice de distribució crida si es fixa una instància que implementa {@link ArxiuPluginListener}.
	 * 
	 */
	@Override
	public void event(
			String metode, 
			Map<String, String> parametres, 
			boolean correcte, 
			String error, 
			Exception e,
			long timeMs) {
		StringBuilder str = new StringBuilder()
				.append("S'ha invocat el mètode \"")
				.append(metode)
				.append("\" amb els paràmetres {");
		int i = 0;
		for (String key : parametres.keySet()) {
			str.append(key).append("=").append(parametres.get(key));
			i++;
			if (i<parametres.size())
				str.append(", ");
		}
		str.append("} amb resultat ").append(correcte ? "OK" : "KO");
		if (error != null)
			str.append(": ").append(error);
		if (e != null)
			str.append(" ").append(e.getClass()).append(" ").append(e.getMessage());
		str.append(" ").append(timeMs).append("ms");
		
		logger.debug(str.toString());
	}
}
