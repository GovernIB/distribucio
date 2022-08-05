package es.caib.distribucio.core.helper;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import es.caib.distribucio.core.api.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.repository.RegistreRepository;

@Component
public class GestioDocumentalHelper {
	
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private RegistreRepository registreRepository;
	
	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP = "anotacions_registre_doc_tmp";
	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP = "anotacions_registre_fir_tmp";
	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";	
	
	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut) {
		
		pluginHelper.gestioDocumentalGet(
				id, 
				agrupacio, 
				contingutOut);
	}	
	
	public String gestioDocumentalCreate(
			String agrupacio,
			byte[] contingut) {
		
		return pluginHelper.gestioDocumentalCreate(
				agrupacio, 
				contingut);	
	}
	
	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		pluginHelper.gestioDocumentalDelete(
				id, 
				agrupacio);
	}
	
	
	/** Esborra els documents temporals. Programa un esborrat en el cas que el commit vagi bé, si no els temporals no s'han d'esborrar. 
	 * També posa a null l'id del gestor documental.
	 */
	@Transactional
	public void esborrarDocsTemporals(long anotacioId) {
		
		RegistreEntity anotacioEntity = registreRepository.findOne(anotacioId);

		logger.debug("Programant l'esborrat de temporals després del commit per l'anotació de registre " + anotacioEntity.getNumero());

		EsborrarDocsTemporalsHandler esborrarDocsTemporalsHandler = new EsborrarDocsTemporalsHandler();
		
		if (anotacioEntity.getAnnexos() != null && anotacioEntity.getAnnexos().size() > 0) {
			for (RegistreAnnexEntity annex : anotacioEntity.getAnnexos()) {
				// No s'esborren els documents originals pels documents que queden com esborranys
				if (annex.getArxiuEstat() != AnnexEstat.ESBORRANY) {
					if (annex.getGesdocDocumentId() != null) {
						esborrarDocsTemporalsHandler.putIdentificador(GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, annex.getGesdocDocumentId());
						annex.updateGesdocDocumentId(null);
					}
					for (RegistreAnnexFirmaEntity firma : annex.getFirmes()) {
						if (firma.getGesdocFirmaId() != null) {
							esborrarDocsTemporalsHandler.putIdentificador(GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, firma.getGesdocFirmaId());
							firma.updateGesdocFirmaId(null);
						}
					}
				}
			}
		}
		
		TransactionSynchronizationManager.registerSynchronization(esborrarDocsTemporalsHandler);
	}	
	
	/** Classe que implementa la sincronització de transacció pes esborrar els temporals només en el cas que la transacció
	 * hagi finalitzat correctament. D'aquesta forma no s'esborren els temporals si no s'han guardat correctament a l'arxiu
	 * amb la informació a BBDD.
	 */
	public class EsborrarDocsTemporalsHandler implements TransactionSynchronization {
		
		/** Map amb el nom de l'agrupació i la llista d'identficadors a esborrar. 
		 * Map<agrupacio, List<identificadors> */
		private Map<String,List<String>> identificadors = new HashMap<>();
		
		/** Afegeix un identificador a una agrupació
		 * 
		 * @param agrupacio
		 * @param identificadorId
		 */
		public void putIdentificador(String agrupacio, String identificadorId) {
			if (!identificadors.containsKey(agrupacio))
				identificadors.put(agrupacio, new ArrayList<String>());
			identificadors.get(agrupacio).add(identificadorId);
		}

		/** Mètode que s'executa després que s'hagi guardat correctament a BBDD i per tants els temporals es poden guardar correctament. */
		@Override
		@Transactional
		public void afterCommit() {
			logger.debug("Esborrant els arxius temporals");
			for (String agrupacio : identificadors.keySet())
				for (String identificador : identificadors.get(agrupacio)) {
					logger.debug("Esborrar arxiu temporal agrupacio=" + agrupacio + ", identificador=" + identificador);
					try {
						gestioDocumentalDelete(identificador, agrupacio);
					} catch(Exception e) {
						logger.error("Error esborrant l'annex amb id " + identificador + " i agrupacio " + agrupacio + " del gestor documental: " + e.getMessage(), e);
					}
				}
		}

		@Override
		public void suspend() {}
		@Override
		public void resume() {}
		@Override
		public void flush() {}
		@Override
		public void beforeCommit(boolean readOnly) {}
		@Override
		public void beforeCompletion() {}
		@Override
		public void afterCompletion(int status) {}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(GestioDocumentalHelper.class);
}
