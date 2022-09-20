package es.caib.distribucio.core.service.ws.backoffice;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import es.caib.distribucio.backoffice.utils.arxiu.DistribucioArxiuError;
import es.caib.distribucio.backoffice.utils.sistra.BackofficeSistra2Utils;
import es.caib.distribucio.backoffice.utils.sistra.BackofficeSistra2UtilsImpl;
import es.caib.distribucio.backoffice.utils.sistra.formulario.Formulario;
import es.caib.distribucio.backoffice.utils.sistra.pago.Pago;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.helper.IntegracioHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.rest.client.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.BackofficeIntegracioRestClientFactory;
import es.caib.distribucio.rest.client.domini.Annex;
import es.caib.distribucio.rest.client.domini.AnnexEstat;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
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
	@Autowired
	private ConfigHelper configHelper;
		
	/** Mètode del WS que rep les comunicacions d'anotacions pendents. */
	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		try {
			// Client de l'API REST de backoffice per consultar anotacions
			BackofficeIntegracioRestClient backofficeClient = getClientRest();			
			es.caib.distribucio.rest.client.domini.AnotacioRegistreId idWs;
			for (AnotacioRegistreId id : ids) {
				try {
					// Construeix l'identificador pel WS del backoffice de DISTRIBUCIO
					idWs = new es.caib.distribucio.rest.client.domini.AnotacioRegistreId();
					idWs.setClauAcces(id.getClauAcces());
					idWs.setIndetificador(id.getIndetificador());
		
					// Consulta l'anotació
					AnotacioRegistreEntrada anotacio = backofficeClient.consulta(idWs);
					// Canvia l'estat a Rebuda
					backofficeClient.canviEstat(
							idWs,
							es.caib.distribucio.rest.client.domini.Estat.REBUDA,
							"Canviar l'estat a rebuda");
					
					// Prepara la cria a la llibreria d'utilitats pel backoffice de DISTRIBUCIO
					// Constructor amb la referència al plugin d'Arxiu
					BackofficeArxiuUtils backofficeArxiuUtils = new BackofficeArxiuUtilsImpl(getArxiuPlugin());
					// Afegeix la instància de la classe com a escoltador d'events
					backofficeArxiuUtils.setArxiuPluginListener(this);
					// Estableix la carpeta on guardar els annexos de l'anotació
					backofficeArxiuUtils.setCarpeta(anotacio.getIdentificador());
		
					// Crida a la creació de l'expedient
					String SERIE_DOCUMENTAL = "S0001"; 
					String CLASSIFICACIO = "000000";
					ArxiuResultat arxiuResultat;
					int intent = 0;
					String expedientUuid = null;
					do {
						// Crida al mètode de creació de la llibreria
						arxiuResultat = backofficeArxiuUtils.crearExpedientAmbAnotacioRegistre(
								expedientUuid,
								anotacio.getIdentificador() + "_" + new Date().getTime(),
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
					// Processament dels annexos de documents tècnics segons el títol
					String titol;
					for (Annex annex : anotacio.getAnnexos()) {
						titol = revisarContingutNom(annex.getNom());
						
						// Copmprovar si està en estat esborrany o és invàlid
						if (annex.getEstat() == AnnexEstat.ESBORRANY) {
							logger.warn("L'annex \"" + annex.getTitol() + "\" està en estat esborrany i es podria perdre a l'hora de tancar l'expedient.");
						}
						if (!annex.isDocumentValid()) {
							logger.warn("L'annex \"" + annex.getTitol() + "\" està marcat com invàlid: " + annex.getDocumentError());
						}
						
						if (titol != null 
								&& ("FORMULARIO".equals(titol) 
										|| "PAGO".equals(titol))) {
							// Recupera el contingut de l'annex
							Document document = getArxiuPlugin().documentDetalls(annex.getUuid(), null, true);							
							byte[] contingut = document.getContingut().getContingut();
							// Interpreta el contingut amb la classe BackofficeSistra2Utils
							BackofficeSistra2Utils sistra2Utils = new BackofficeSistra2UtilsImpl();
							logger.trace("  Document tècnic \"" + titol + "\". Dades:");
							try {
								if ("FORMULARIO".equals(titol)) {
									Formulario formulario = sistra2Utils.parseXmlFormulario(contingut);
									logger.trace("  formulario: " + ToStringBuilder.reflectionToString(formulario));								
								} else if ("PAGO".equals(titol)) {
									Pago pago = sistra2Utils.parseXmlPago(contingut);
									logger.trace("  pago: " + ToStringBuilder.reflectionToString(pago));								
								}
							} catch(Exception e) {								
								logger.error("Error obtenint la informació del document tècnic " + titol + ":" + e.getMessage(), e);
							}
						}

					}
					// Es comunica el resultat a DISTRIBUCIO
					switch(arxiuResultat.getErrorCodi()) {
						case 0:
							backofficeClient.canviEstat(idWs, es.caib.distribucio.rest.client.domini.Estat.PROCESSADA, "Processada");
							break;
						default:
							backofficeClient.canviEstat(idWs, es.caib.distribucio.rest.client.domini.Estat.ERROR, arxiuResultat.getErrorCodi() + " " + arxiuResultat.getErrorMessage());
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
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.distribucio.");
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
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	
	private BackofficeIntegracioRestClient getClientRest() {
		BackofficeIntegracioRestClient client = null;
		
		String url_base = configHelper.getConfig("es.caib.distribucio.backoffice.test.backofficeIntegracio.url");
		String usuari = configHelper.getConfig("es.caib.distribucio.backoffice.test.backofficeIntegracio.usuari");
		String contrasenya = configHelper.getConfig("es.caib.distribucio.backoffice.test.backofficeIntegracio.contrasenya");
		if (url_base != null && usuari != null && contrasenya != null) {
			logger.trace(">>> Creant el client BackofficeIntegracioRestClient API REST");
			client = BackofficeIntegracioRestClientFactory.getRestClient(
					url_base, 
					usuari, 
					contrasenya);
		} else {
			throw new RuntimeException("Falta configurar les propietats pel client de Backoffice de DISTRIBUCIO  es.caib.distribucio.backoffice.test.backofficeIntegracio.*");
		}
		return client;
	}
	
	
	/** Mètode privat per revisar el nom del contingut de la mateixa manera que ho fa el 
	 * plugin d'Arxiu abans de guardar el contingut.
	 * @param nom Nomm del contingut
	 * @return Retorna el nom substituïnt els caràcters no permesos o null si el nom és null.
	 */
	private String revisarContingutNom(String nom) {
		if (nom == null) {
			return null;
		}
		//return nom.replace("&", "&amp;").replaceAll("[\\\\/:*?\"<>|]", "_");
		nom = nom.replaceAll("[\\s\\']", " ").replaceAll("[^\\wçñàáèéíïòóúüÇÑÀÁÈÉÍÏÒÓÚÜ()\\-,\\.·\\s]", "").trim();
		if (nom.endsWith(".")) {
			nom = nom.substring(0, nom.length()-1);
		}
		return nom;
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
		
		logger.trace(str.toString());
	}
}
