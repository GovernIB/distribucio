
package es.caib.distribucio.backoffice.utils.arxiu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat.ExpedientAccio;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultatAnnex.AnnexAccio;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.rest.client.domini.Annex;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.domini.Interessat;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class BackofficeArxiuUtilsImpl implements BackofficeArxiuUtils {
	
	/** Referència al plugin d'Arxiu per realitzar crides i comprovacions. */
	private IArxiuPlugin iArxiuPlugin = null;
	
	/** Nom de la carpeta on es mouran els annexos. Si no es fixa la propietat llavors els annexos es mouran a la carpeta arrel de l'expedient. */
	private String carpetaAnnexos = null;
	
	/** Referència a la instància per notificar les crides a l'Arxiu. */
	private ArxiuPluginListener arxiuPluginListener = null;
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstatEnumDto ntiEstat,
			String serieDocumental,
			AnotacioRegistreEntrada anotacioRegistreEntrada){
		
		return this.crearExpedientAmbAnotacioRegistre(
				toArxiuExpedient(
					identificador,
					nom,
					ntiIdentificador,
					ntiOrgans,
					ntiDataObertura,
					ntiClassificacio,
					ntiEstat,
					serieDocumental),
				anotacioRegistreEntrada);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			Expedient expedient,
			AnotacioRegistreEntrada anotacioRegistreEntrada){
		String expedientIdentificador = null;
		String carpetaUuid = null;
		boolean isCarpetaActive = carpetaAnnexos != null && !carpetaAnnexos.isEmpty();

		ArxiuResultat arxiuResultat = new ArxiuResultat();
		arxiuResultat.setErrorCodi(DistribucioArxiuError.NO_ERROR);
		try {
			// CREATE EXPEDIENT IN ARXIU
			expedientIdentificador = createExpedientInArxiu(expedient, arxiuResultat);

			// expedientDetalls
			Expedient expedientDetalls;
			long t0 = System.currentTimeMillis();
			Map<String, String> parametres = new HashMap<String, String>();
			parametres.put("expedientIdentificador", expedientIdentificador);
			try {
				expedientDetalls = iArxiuPlugin.expedientDetalls(
						expedientIdentificador,
						null);
				event("expedientDetalls", parametres, true, null, null, System.currentTimeMillis() - t0);
			} catch (Exception e) {
				logger.error("Error obtenint els detalls de l'expedient amb identificador " + expedientIdentificador, e);
				event("expedientDetalls", parametres, false, e.getMessage(), e, System.currentTimeMillis() - t0);
				throw e;
			}
						
			// Crea la carpeta a l'expedient destí si aquesta està fixada
			if (isCarpetaActive) {
				carpetaUuid = createCarpetaInArxiu(expedientDetalls);
			}
			
			// Afegeix els interessats a l'expedient destí
			addInteressats(
					expedientDetalls.getMetadades().getInteressats(),
					anotacioRegistreEntrada.getInteressats(),
					expedient,
					expedientIdentificador);
			
			// Mou els documents cap a l'expedient de l'Arxiu
			moveDocumentsInArxiu(
					isCarpetaActive,
					carpetaUuid,
					anotacioRegistreEntrada,
					expedientDetalls,
					arxiuResultat);

		} catch (Exception e) {
			arxiuResultat.setException(e);
			arxiuResultat.setErrorCodi(DistribucioArxiuError.ARXIU_ERROR);
			arxiuResultat.setErrorMessage(e.getMessage());
			logger.error("Error al processar anotacio al libreria backoffice-arxiu", e);
		} finally {
			arxiuResultat.setIdentificadorExpedient(expedientIdentificador);
		}
		return arxiuResultat;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setArxiuPlugin(IArxiuPlugin iArxiuPlugin) {
		this.iArxiuPlugin = iArxiuPlugin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IArxiuPlugin getArxiuPlugin() {
		return this.iArxiuPlugin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCarpeta(String carpetaAnnexos) {
		this.carpetaAnnexos = carpetaAnnexos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCarpeta() {
		return this.carpetaAnnexos;
	}
	
	@Override
	public void setArxiuPluginListener(ArxiuPluginListener listener) {
		this.arxiuPluginListener = listener;
	}

	@Override
	public ArxiuPluginListener getArxiuPluginListener() {
		return this.arxiuPluginListener;
	}	


	private void moveDocumentsInArxiu(
			boolean isCarpetaActive,
			String carpetaUuid,
			AnotacioRegistreEntrada anotacioRegistreEntrada,
			Expedient expedientDetalls, 
			ArxiuResultat arxiuResultat) {

		// Consulta els continguts de la carpeta o l'expedient per comprovar si existeixen els annexos
		List<ContingutArxiu> fillsArxiu;
		if (isCarpetaActive) {
			Carpeta carpeta;
			// carpetaDetalls
			long t0 = System.currentTimeMillis();
			Map<String, String> parametres = new HashMap<String, String>();
			parametres.put("expedient.identificador", expedientDetalls.getIdentificador());
			parametres.put("expedient.nom", expedientDetalls.getNom());
			parametres.put("carpetaUuid", carpetaUuid);
			try {
				carpeta = iArxiuPlugin.carpetaDetalls(carpetaUuid);
				event("carpetaDetalls", parametres, true, null, null, System.currentTimeMillis() - t0);
			} catch (Exception e) {
				logger.error("Error obtenint els detalls de la carpeta amb uuid " + carpetaUuid + " de l'expedient " + expedientDetalls.getIdentificador() );
				event("carpetaDetalls", parametres, false, e.getMessage(), e, System.currentTimeMillis() - t0);
				throw e;
			}
			fillsArxiu = carpeta.getContinguts();
		} else {
			fillsArxiu = expedientDetalls.getContinguts();
		}

		// Processa els annexos
		ArxiuResultatAnnex resultatAnnex;
		for (Annex annex : anotacioRegistreEntrada.getAnnexos()) {
			resultatAnnex = new ArxiuResultatAnnex();
			resultatAnnex.setAnnex(annex);
			resultatAnnex.setErrorCodi(DistribucioArxiuError.NO_ERROR);
			
			boolean documentExistsInArxiu = false;
			if (fillsArxiu != null) {
				String nom = revisarContingutNom(annex.getNom());
				for (ContingutArxiu fillArxiu : fillsArxiu) {
					if (fillArxiu.getTipus() == ContingutTipus.DOCUMENT && fillArxiu.getNom().equals(nom)) {
						documentExistsInArxiu = true;
						logger.debug("Document amb nom: " + annex.getTitol() + " ja existeix al arxiu");
						resultatAnnex.setAccio(AnnexAccio.EXISTENT);
						resultatAnnex.setIdentificadorAnnex(fillArxiu.getIdentificador());
					}
				}
			}
			if (!documentExistsInArxiu) {
				
				ContingutArxiu nouDocumentDispatched;
				// documentMoure
				long t0 = System.currentTimeMillis();
				Map<String, String> parametres = new HashMap<String, String>();
				parametres.put("expedient.identificador", expedientDetalls.getIdentificador());
				parametres.put("expedient.nom", expedientDetalls.getNom());
				parametres.put("annex.uuid", annex.getUuid());
				parametres.put("annex.nom", annex.getNom());
				try {
					nouDocumentDispatched = iArxiuPlugin.documentMoure(
							annex.getUuid(),
							isCarpetaActive ? carpetaUuid : expedientDetalls.getIdentificador(),
							expedientDetalls.getIdentificador());

					resultatAnnex.setAccio(AnnexAccio.MOGUT);
					if (nouDocumentDispatched == null) {
						resultatAnnex.setIdentificadorAnnex(annex.getUuid());
						logger.debug("Document mogut al arxiu amb id: " + annex.getUuid());
					} else {
						resultatAnnex.setIdentificadorAnnex(nouDocumentDispatched.getIdentificador());
						logger.debug("Document enviat al arxiu amb id: " + nouDocumentDispatched.getIdentificador());
					}
					event("documentMoure", parametres, true, null, null, System.currentTimeMillis() - t0);
				} catch (Exception e) {
					// Informa de l'error a nivell d'annex
					resultatAnnex.setAccio(AnnexAccio.ERROR);
					resultatAnnex.setErrorCodi(DistribucioArxiuError.ANNEX_ERROR);
					resultatAnnex.setErrorMessage("Error movent l'annex :" + e.getMessage());
					resultatAnnex.setException(e);
					// Informa de l'error a nivell global.
					arxiuResultat.setErrorCodi(DistribucioArxiuError.ANNEX_ERROR);
					arxiuResultat.setErrorMessage("Hi ha hagut un error movent annexos.");
					
					logger.error("Error movent l'annex \"" + annex.getNom() + "\" a l'arxiu: " + e.getMessage(), e);
					event("documentMoure", parametres, false, e.getMessage(), e, System.currentTimeMillis() - t0);
				}
			}
			// Afegeix el resultat a la llista de resultat d'annexos
			arxiuResultat.addResultatAnnex(annex, resultatAnnex);
		}
	}
	
	private String createCarpetaInArxiu(Expedient expedientDetalls) {
		
		String carpetaUuid = null;
		// check if carpeta already exists in arxiu
		boolean carpetaExistsInArxiu = false;
		carpetaUuid = null;
		if (expedientDetalls.getContinguts() != null) {
			String carpetaAnnexosNomRevisat = this.revisarContingutNom(carpetaAnnexos);
			for (ContingutArxiu contingutArxiu : expedientDetalls.getContinguts()) {
				if (contingutArxiu.getTipus().equals(ContingutTipus.CARPETA) 
						&& contingutArxiu.getNom().equals(carpetaAnnexosNomRevisat)) {
					carpetaExistsInArxiu = true;
					carpetaUuid = contingutArxiu.getIdentificador();
					logger.debug("La carpeta amb nom: " + carpetaAnnexos + " (" + carpetaAnnexosNomRevisat + ") ja existeix al arxiu");
				}
			}
		}
		// if carpeta doesnt exist create it in arxiu
		if (!carpetaExistsInArxiu){

			
			ContingutArxiu carpetaCreada;
			// carpetaCrear
			long t0 = System.currentTimeMillis();
			Map<String, String> parametres = new HashMap<String, String>();
			parametres.put("expedient.identificador", expedientDetalls.getIdentificador());
			parametres.put("expedient.nom", expedientDetalls.getNom());
			parametres.put("carpetaAnnexos", carpetaAnnexos);
			try {
				carpetaCreada = iArxiuPlugin.carpetaCrear(
						toArxiuCarpeta(
									null,
							carpetaAnnexos),
						expedientDetalls.getIdentificador());
				event("carpetaCrear", parametres, true, null, null, System.currentTimeMillis() - t0);
			} catch (Exception e) {
				logger.error("Error creant la carpeta a l'expedient identificador " + expedientDetalls.getIdentificador(), e);
				event("carpetaCrear", parametres, false, e.getMessage(), e, System.currentTimeMillis() - t0);
				throw e;
			}
			carpetaUuid = carpetaCreada.getIdentificador();
			logger.debug("Carpeta creat al arxiu amb id:  " + carpetaUuid);
		} 
		return carpetaUuid;
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

	private void addInteressats(
			List<String> interessatsArxiu,
			List<Interessat> interessatsRegistre,
			Expedient expedient,
			String expedientIdentificador) {
		expedient.setIdentificador(expedientIdentificador);
		List<String> interessatsToAdd = new ArrayList<String>();
		if (interessatsArxiu == null)
			interessatsArxiu = new ArrayList<>();

		if (interessatsRegistre != null) {
			for (Interessat interessat : interessatsRegistre) {

				if (interessatsArxiu.isEmpty()) {
					interessatsToAdd.add(interessat.getDocumentNumero());
				} else {
					boolean alreadyExists = false;
					for (String interessatArxiu : interessatsArxiu) {
						if (interessat.getDocumentNumero().equals(interessatArxiu)) {
							alreadyExists = true;
						}
					}
					if (!alreadyExists) {
						interessatsToAdd.add(interessat.getDocumentNumero());
					}
				}
			}
			interessatsArxiu.addAll(interessatsToAdd);
		}
		if (interessatsToAdd != null && !interessatsToAdd.isEmpty()) {
			expedient.getMetadades().setInteressats(interessatsArxiu);
			// expedientModificar
			long t0 = System.currentTimeMillis();
			Map<String, String> parametres = new HashMap<String, String>();
			parametres.put("expedient.identificador", expedient.getIdentificador());
			parametres.put("expedient.nom", expedient.getNom());
			try {
				iArxiuPlugin.expedientModificar(expedient);
				event("expedientModificar", parametres, true, null, null, System.currentTimeMillis() - t0);
			} catch (Exception e) {
				logger.error("Error afegint interessats a l'expedient identificador " + expedientIdentificador, e);
				event("expedientModificar", parametres, false, e.getMessage(), e, System.currentTimeMillis() - t0);
				throw e;
			}
		}
	}
	
	/** Comprova si el listener està fixat i l'invoca. */
	private void event(String metode, Map<String, String> parametres, boolean correcte, String error, Exception e, long timeMs) {
		if (this.arxiuPluginListener != null)
			this.arxiuPluginListener.event(metode, parametres, correcte, error, e, timeMs);
	}

	private String createExpedientInArxiu(Expedient expedient, ArxiuResultat arxiuResultat) {
		String expedientIdentificador;
		if (expedient.getIdentificador() == null || expedient.getIdentificador().isEmpty()) {
			ContingutArxiu expedientCreat;
			// expedientCrear
			long t0 = System.currentTimeMillis();
			Map<String, String> parametres = new HashMap<String, String>();
			parametres.put("expedient.nom", expedient.getNom());
			try {
				expedientCreat = iArxiuPlugin.expedientCrear(expedient);
				event("expedientCrear", parametres, true, null, null, System.currentTimeMillis() - t0);
			} catch (Exception e) {
				arxiuResultat.setAccio(ExpedientAccio.ERROR);
				event("expedientCrear", parametres, false, e.getMessage(), e, System.currentTimeMillis() - t0);
				throw e;
			}
			expedientIdentificador = expedientCreat.getIdentificador();
			arxiuResultat.setAccio(ExpedientAccio.MOGUT);
			arxiuResultat.setIdentificadorExpedient(expedientIdentificador);
			logger.debug("Expedient creat al arxiu amb id: " + expedientIdentificador);
		} else {
			expedientIdentificador = expedient.getIdentificador();
			arxiuResultat.setAccio(ExpedientAccio.EXISTENT);
			arxiuResultat.setIdentificadorExpedient(expedient.getIdentificador());
			logger.debug("L'expedient amb id: " + expedientIdentificador + " ja existeix");
		}
		return expedientIdentificador;
	}
	
	
	private Carpeta toArxiuCarpeta(
			String identificador,
			String nom) {
		Carpeta carpeta = new Carpeta();
		carpeta.setIdentificador(identificador);
		carpeta.setNom(nom);
		return carpeta;
	}

	private Expedient toArxiuExpedient(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstatEnumDto ntiEstat,
			String serieDocumental) {
		Expedient expedient = new Expedient();
		expedient.setNom(nom);
		expedient.setIdentificador(identificador);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setIdentificador(ntiIdentificador);
		metadades.setDataObertura(ntiDataObertura);
		metadades.setClassificacio(ntiClassificacio);
		if (ntiEstat != null) {
			switch (ntiEstat) {
			case OBERT:
				metadades.setEstat(ExpedientEstat.OBERT);
				break;
			case TANCAT:
				metadades.setEstat(ExpedientEstat.TANCAT);
				break;
			case INDEX_REMISSIO:
				metadades.setEstat(ExpedientEstat.INDEX_REMISSIO);
				break;
			}
		}
		metadades.setOrgans(ntiOrgans);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setMetadades(metadades);
		return expedient;
	}
	

	/** Constructor on es fixa la referència a l' IArxiuPlugin
	 * 
	 * @param iArxiuPlugin
	 */
	public BackofficeArxiuUtilsImpl(
			IArxiuPlugin iArxiuPlugin) {
		super();
		this.iArxiuPlugin = iArxiuPlugin;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BackofficeArxiuUtilsImpl.class);
}
