package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.ProcedimentHelper;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto.Estat;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ProcedimentEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.ProcedimentRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.procediment.Procediment;

/**
 * Implementació del servei de gestió de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class ProcedimentServiceImpl implements ProcedimentService{
	
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ProcedimentHelper procedimentHelper;
	
	/** Progrés d'acualització actual.*/
	private static Map<Long, UpdateProgressDto> progressosActualitzacio = new ConcurrentHashMap<Long, UpdateProgressDto>();

	@Override
	@Transactional(readOnly = true) 
	public PaginaDto<ProcedimentDto> findAmbFiltre(
			Long entitatId, 
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.trace("Cercant els procediments segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		
		PaginaDto<ProcedimentDto> llistaProcediments = null;
        EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);

		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(entitat.getCodiDir3(), filtre.getUnitatOrganitzativa());
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("codiProcediment", new String[]{"codi"});
		llistaProcediments = paginacioHelper.toPaginaDto(
				procedimentRepository.findAmbFiltrePaginat(
						entitatId, 
						unitatOrganitzativa == null, 
						unitatOrganitzativa != null ? unitatOrganitzativa : null, 
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() != null ? filtre.getCodi() : "", 
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom() != null ? filtre.getNom() : "", 
						filtre.getCodiSia() == null || filtre.getCodiSia().isEmpty(), 
						filtre.getCodiSia() != null ? filtre.getCodiSia() : "", 
						filtre.getEstat() == null, 
						filtre.getEstat(),						
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio)), 
				ProcedimentDto.class);

		
		return llistaProcediments;
	}

	/** Mètode per trobar i actualitzar els procediments. Es pot fer manualment o des de la tasca
	 * programada.
	 */
	@Override
	@Transactional	
	public void findAndUpdateProcediments(Long entitatId) throws Exception {
		
		String msgInfo;
		UpdateProgressDto progres = null;
		// Comprova si hi ha una altre instància del procés en execució
		if (isUpdatingProcediments(entitatId)) {
			logger.debug("Ja existeix un altre procés que està executant l'actualització de procediments per l'entitat " + entitatId + ".");
			return;	// S'està executant l'actualitzacio
		} else {
			progres = new UpdateProgressDto();
			progressosActualitzacio.put(entitatId, progres);
		}
		
		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
		msgInfo = "Inici del procés d'actualització de procediments de l'entitat " + entitat.getCodi() + " " + entitat.getNom();
		progres.setEstat(Estat.INICIALITZANT);
		logger.info(msgInfo);
		
		List<Procediment> procedimentList = null;
		int reintents = 1;
		boolean errorConsultaProcediments = false;
		Exception exConsultaProcediments = null;
		String errMsg = "-";
		do {
			try {
				msgInfo = "Obtenint el llistat de procediments per a l'entitat " + entitat.getCodiDir3();
				logger.info(msgInfo);
				procedimentList = pluginHelper.procedimentFindByCodiDir3(entitat.getCodiDir3());				
			} catch (Exception e) {
				exConsultaProcediments = e;
				errMsg = "Error consultant els procediments per l'entitat: " + entitat.getCodiDir3();
				errorConsultaProcediments = reintents++ >= 3;				
			}
		} 
		while (procedimentList == null && !errorConsultaProcediments);
		
		// Comprova si hi ha hagut errors consultant els procediments
		if (errorConsultaProcediments) {
			progres.setEstat(UpdateProgressDto.Estat.ERROR);
			String errorMessage = exConsultaProcediments.getMessage();
			if (errorMessage != null)
				progres.setErrorMsg(errorMessage);
			else
				progres.setErrorMsg(errMsg);
			throw new Exception(errMsg, exConsultaProcediments);
		}
		if (procedimentList == null || procedimentList.isEmpty()) {
			throw new Exception(
					"No s'ha obtingut cap llista o resultat per la consulta de procediments: (llista " + (procedimentList == null? "nul·la" :  "buida") + ")"
			);
		}
		
		// Processa els procediments consultats
		msgInfo="S'han obtingut " + procedimentList.size() + " procediments vigents a Distribucio.";
		logger.info(msgInfo);
		progres.setEstat(Estat.ACTUALITZANT);
		progres.setTotal(procedimentList.size());
		
		// Crea un Map amb els procediments de Distribucio per codi
		Map<String, Procediment> procedimentMap = new HashMap<String, Procediment>();
		for (Procediment procediment : procedimentList) {
			procedimentMap.put(procediment.getCodigo(), procediment);
		}
		
		// Deshabilita els procediments que no hagi retornat Distribucio
		procedimentHelper.actualtizarProcedimentsNoVigents(entitat, procedimentMap);
		
		// Processa tots els procediments, actualitza-ne la informació, donant-los d'alta i revisant la seva UO		
		msgInfo = "Es procedeix a processar els " + procedimentList.size() + " procediments consultats a Distribucio.";
		logger.info(msgInfo);
		
		// Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
		Map<String, UnitatOrganitzativaEntity> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativaEntity>();
		for (Procediment procediment : procedimentList) {
			// Tracta el procediment en una transacció a part.
			procedimentHelper.actualitzaProcediment(procediment, unitatsOrganitzatives, entitat);
			progres.incProcessats();
		}
		progres.setEstat(UpdateProgressDto.Estat.FINALITZAT);
	}

	@Transactional
	private void updateProcediments(
			List<Procediment> procediments,
			Long entitatId,
			Long unitatOrganitzativaId) {
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findById(unitatOrganitzativaId).orElse(null);
		for (Procediment procediment: procediments) {
			if (procediment.getCodigo() != null && !procediment.getCodigo().isEmpty()) {
				ProcedimentEntity procedimentEntity = procedimentRepository.findByCodi(
						entitatId,
						procediment.getCodigo());
				EntitatEntity entitat = entitatRepository.findById(entitatId).orElse(null);
				if (procedimentEntity == null) {
					procedimentEntity = ProcedimentEntity.getBuilder(
							procediment.getCodigo(),
							procediment.getNombre(),
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat).built();
					procedimentRepository.save(procedimentEntity);
				} else {
					procedimentEntity.update(
							procediment.getCodigo(),
							procediment.getNombre(),
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat);
					procedimentRepository.save(procedimentEntity);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ProcedimentDto findByCodiSia(Long entitatId, String codiSia) {
		return conversioTipusHelper.convertir(
				procedimentRepository.findByCodiSia(
						entitatId, 
						codiSia), 
				ProcedimentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findByNomOrCodiSia(Long entitatId, String search) {
		if (search == null || search.isEmpty()) {
			return new ArrayList<>();
		}			
		return conversioTipusHelper.convertirList(
				procedimentRepository.findByNomOrCodiSia(
						entitatId, 
						search != null ? search : ""), 
				ProcedimentDto.class);
	}

	@Override
	public boolean isUpdatingProcediments(Long entitatId) {
		UpdateProgressDto progres = progressosActualitzacio.get(entitatId);
		return progres != null 
				&& progres.getEstat() != UpdateProgressDto.Estat.FINALITZAT
				&& progres.getEstat() != UpdateProgressDto.Estat.ERROR;
	}
	
	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return progressosActualitzacio.get(entitatId);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentServiceImpl.class);

}
