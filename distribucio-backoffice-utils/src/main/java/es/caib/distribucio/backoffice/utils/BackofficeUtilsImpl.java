
package es.caib.distribucio.backoffice.utils;

import java.util.Date;
import java.util.List;

import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.service.ws.backoffice.Annex;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class BackofficeUtilsImpl implements BackofficeUtils {
	private IArxiuPlugin iArxiuPlugin;
	
	// name of carpeta to which annexos should be moved, if null annexos are moved directly to expedient without creating carpeta
	private String carpetaAnnexos;
	
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstatEnumDto ntiEstat,
			List<String> ntiInteressats,
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
					ntiInteressats,
					serieDocumental),
				anotacioRegistreEntrada);
	}
	
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			Expedient expedient,
			AnotacioRegistreEntrada anotacioRegistreEntrada){
		
		ArxiuResultat arxiuResultat = new ArxiuResultat();
		String carpetaUuid = null;
		boolean isCarpetaActive = carpetaAnnexos != null && !carpetaAnnexos.isEmpty();
		
		
		try {
			// CREATE EXPEDIENT IN ARXIU
			String expedientCreatIdentificador;
			if (expedient.getIdentificador() == null || expedient.getIdentificador().isEmpty()) {
				ContingutArxiu expedientCreat = iArxiuPlugin.expedientCrear(expedient);
				expedientCreatIdentificador = expedientCreat.getIdentificador();
			} else {
				expedientCreatIdentificador = expedient.getIdentificador();
			}
			
			Expedient expedientDetalls = iArxiuPlugin.expedientDetalls(
					expedientCreatIdentificador,
					null);
			arxiuResultat.setIdentificadorExpedient(expedientCreatIdentificador);
			
			// CREATE CARPETA IN ARXIU
			if (isCarpetaActive) {
				
				// check if already exists in arxiu
				boolean carpetaExistsInArxiu = false;
				carpetaUuid = null;
				if (expedientDetalls.getContinguts() != null) {
					for (ContingutArxiu contingutArxiu : expedientDetalls.getContinguts()) {
						String replacedNom = carpetaAnnexos.replace("/", "_");
						if (contingutArxiu.getTipus() == ContingutTipus.CARPETA && contingutArxiu.getNom().equals(
								replacedNom)) {
							carpetaExistsInArxiu = true;
							carpetaUuid = contingutArxiu.getIdentificador();
						}
					}
				}
				// if doesnt exist create it in arxiu
				if (!carpetaExistsInArxiu) {
					ContingutArxiu carpetaCreada = iArxiuPlugin.carpetaCrear(toArxiuCarpeta(null,
							carpetaAnnexos),
							expedientCreatIdentificador);
					carpetaUuid = carpetaCreada.getIdentificador();
				}
			}
			
			// MOVE DOCUMENTS IN ARXIU 
			for (Annex annex : anotacioRegistreEntrada.getAnnexos()) {
				
				if (isCarpetaActive) {
					Carpeta carpeta = iArxiuPlugin.carpetaDetalls(carpetaUuid);
					boolean documentExistsInArxiu = false;
					if (carpeta.getContinguts() != null) {
						for (ContingutArxiu contingutArxiu : carpeta.getContinguts()) {
							if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT && contingutArxiu.getNom().equals(
									annex.getTitol())) {
								documentExistsInArxiu = true;
							}
						}
					}
					if (!documentExistsInArxiu) {
						ContingutArxiu nouDocumentDispatched = iArxiuPlugin.documentMoure(
								annex.getUuid(),
								carpetaUuid,
								expedientCreatIdentificador);
						
						// if document was dispatched, new docuement will be returned
						if (nouDocumentDispatched != null) {
							annex.setUuid(nouDocumentDispatched.getIdentificador());
						}
					}
				} else {
					
					boolean documentExistsInArxiu = false;
					if (expedientDetalls.getContinguts() != null) {
						for (ContingutArxiu contingutArxiu : expedientDetalls.getContinguts()) {
							if (contingutArxiu.getTipus() == ContingutTipus.DOCUMENT && contingutArxiu.getNom().equals(
									annex.getTitol())) {
								documentExistsInArxiu = true;
							}
						}
					}
					if (!documentExistsInArxiu) {
						ContingutArxiu nouDocumentDispatched = iArxiuPlugin.documentMoure(
								annex.getUuid(),
								expedientCreatIdentificador,
								expedientCreatIdentificador);
					}
				}
			}
		arxiuResultat.setErrorCodi(DistribucioArxiuError.NO_ERROR);	
		
		} catch (Exception e) {
			arxiuResultat.setException(e);
			arxiuResultat.setErrorCodi(DistribucioArxiuError.ARXIU_ERROR);
			arxiuResultat.setErrorMessage(e.getMessage());
		}
		return arxiuResultat;
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
			List<String> ntiInteressats,
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
		metadades.setInteressats(ntiInteressats);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setMetadades(metadades);
		return expedient;
	}
	

	public BackofficeUtilsImpl(
			IArxiuPlugin iArxiuPlugin) {
		super();
		this.iArxiuPlugin = iArxiuPlugin;
	}

	public IArxiuPlugin getIarxiuPlugin() {
		return iArxiuPlugin;
	}

	public void setIarxiuPlugin(IArxiuPlugin iArxiuPlugin) {
		this.iArxiuPlugin = iArxiuPlugin;
	}

	public String getCarpetaAnnexos() {
		return carpetaAnnexos;
	}
	public void setCarpetaAnnexos(String carpetaAnnexos) {
		this.carpetaAnnexos = carpetaAnnexos;
	}
	

}
