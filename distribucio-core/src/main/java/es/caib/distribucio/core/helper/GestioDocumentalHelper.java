package es.caib.distribucio.core.helper;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import es.caib.distribucio.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;

@Component
public class GestioDocumentalHelper {
	

	private GestioDocumentalPlugin gestioDocumentalPlugin;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private ConfigHelper configHelper;
	
	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP = "anotacions_registre_doc_tmp";
	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP = "anotacions_registre_fir_tmp";
	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	
	
	
	
	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut) {
		String accioDescripcio = "Consultant document a dins la gestió documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		long t0 = System.currentTimeMillis();
		try {
			if (getGestioDocumentalPlugin() != null) {
				getGestioDocumentalPlugin().get(
						id,
						agrupacio,
						contingutOut);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al consultar document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}	
	
	public String gestioDocumentalCreate(
			String agrupacio,
			byte[] contingut) {
		String accioDescripcio = "Creant nou document a dins la gestió documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("agrupacio", agrupacio);
		int contingutLength = contingut != null ? contingut.length : 0;
		accioParams.put("numBytes", Integer.toString(contingutLength));
		long t0 = System.currentTimeMillis();
		try {
			String gestioDocumentalId = null;
			if (getGestioDocumentalPlugin() != null) {
				gestioDocumentalId = getGestioDocumentalPlugin().create(
						agrupacio,
						new ByteArrayInputStream(contingut));
			}
			accioParams.put("idRetornat", gestioDocumentalId);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	
	
	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		String accioDescripcio = "Esborrant document a dins la gestió documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		long t0 = System.currentTimeMillis();
		try {
			if (getGestioDocumentalPlugin() != null) {
				getGestioDocumentalPlugin().delete(
						id,
						agrupacio);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al esborrar document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	
	/** Esborra els documents temporals. Programa un esborrat en el cas que el commit vagi bé, si no els temporals no s'han d'esborrar. 
	 * També posa a null l'id del gestor documental.
	 */
	@Transactional
	public void esborrarDocsTemporals(RegistreEntity anotacioEntity) {

		logger.debug("Programant l'esborrat de temporals després del commit per l'anotació de registre " + anotacioEntity.getNumero());

		EsborrarDocsTemporalsHandler esborrarDocsTemporalsHandler = new EsborrarDocsTemporalsHandler();
		
		if (anotacioEntity.getAnnexos() != null && anotacioEntity.getAnnexos().size() > 0) {
			for (RegistreAnnexEntity annex : anotacioEntity.getAnnexos()) {
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
	
	
	

	private boolean gestioDocumentalPluginConfiguracioProvada = false;
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (gestioDocumentalPlugin == null && !gestioDocumentalPluginConfiguracioProvada) {
			gestioDocumentalPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de gestió documental",
							ex);
				}
			}
			/*else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de gestió documental no està configurada");
			}*/
		}
		return gestioDocumentalPlugin;
	}
	
	
	
	private String getPropertyPluginGestioDocumental() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.gesdoc.class");
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(GestioDocumentalHelper.class);

}
