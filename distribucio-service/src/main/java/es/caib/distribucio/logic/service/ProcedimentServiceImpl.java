package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.ArbreNodeDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentUpdateProgressDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentUpdateProgressDto.Estat;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
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
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	/** Progrés d'acualització actual.*/
	private static Map<Long, ProcedimentUpdateProgressDto> progressosActualitzacio = new HashMap<Long, ProcedimentUpdateProgressDto>();

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

		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodi(filtre.getUnitatOrganitzativa());
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
		
		ProcedimentUpdateProgressDto progres = null;
		// Comprova si hi ha una altre instància del procés en execució
		if (isUpdatingProcediments(entitatId)) {
			logger.debug("Ja existeix un altre procés que està executant l'actualització de procediments per l'entitat " + entitatId + ".");
			return;	// S'està executant l'actualitzacio
		} else {
			progres = new ProcedimentUpdateProgressDto();
			progressosActualitzacio.put(entitatId, progres);
		}

		// Inicialitza i consulta els procediments		
		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
		ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
		List<UnitatOrganitzativaEntity> llistaUnitatsOrganitzatives = unitatOrganitzativaRepository.findByCodiDir3Entitat(entitat.getCodiDir3());
		logger.debug("Actualitzant els procediments de l'entitat " + entitat.getCodi() + " " + entitat.getNom() + " amb " + llistaUnitatsOrganitzatives.size() + " unitats.");
		progres.setEstat(Estat.INICIALITZANT);

		// Marca'm els procediments de Distribució com a extingits
		List<ProcedimentEntity> llistaProcedimentsDistribucio = procedimentRepository.findByEntitat(entitat);
		for (ProcedimentEntity procedimentDistribucio : llistaProcedimentsDistribucio) {
			procedimentDistribucio.update(
					procedimentDistribucio.getCodi(), 
					procedimentDistribucio.getNom(), 
					procedimentDistribucio.getCodiSia(),
					ProcedimentEstatEnumDto.EXTINGIT,
					procedimentDistribucio.getUnitatOrganitzativa(), 
					entitatRepository.getReferenceById(entitatId));
			
			procedimentRepository.save(procedimentDistribucio);
		}
		
		// Consulta els procediments dins l'arbre
		ArbreDto<UnitatOrganitzativaDto> unitatsArbre = 
				unitatOrganitzativaHelper.unitatsOrganitzativesFindArbreByPare(entitat.getCodiDir3());
		progres.setEstat(Estat.ACTUALITZANT);
		progres.setUnitatsTotal(unitatsArbre.toDadesList().size());
		
		int reintents = 0;
		int max_intents = 5;
		if (unitatsArbre != null) {
			logger.debug("Actualitzant els procediments de l'entitat " + entitat.getCodi() + " " + entitat.getNom() + " amb " + unitatsArbre.toList().size() + " unitats.");
			List<ArbreNodeDto<UnitatOrganitzativaDto>> unitats = new ArrayList<>();
			unitats.add(unitatsArbre.getArrel());
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodesUosAmbError = new ArrayList<>();
			do {
				nodesUosAmbError = new ArrayList<>();
				for(ArbreNodeDto<UnitatOrganitzativaDto> unitat : unitats) {
					updateProcedimentsArbre(
							entitatId, 
							unitat, 
							nodesUosAmbError, 
							reintents, 
							max_intents,
							progres);
				}
				unitats = nodesUosAmbError;
				reintents++;
			} while (reintents <= max_intents 
					&& !nodesUosAmbError.isEmpty());

			if (nodesUosAmbError.size() > 0) {
				// Llença excepció
				StringBuilder errMsg = new StringBuilder("No S'han pogut consultar i actualitzar correctament els procediments per les següents unitats organitzatives després de " + max_intents + " reintents :[");
				for (int i=0; i < nodesUosAmbError.size(); i++) {
					errMsg.append(nodesUosAmbError.get(i).getDades().getCodiAndNom());
					if (i < nodesUosAmbError.size()-1) {
						errMsg.append(", ");
					}
				}
				errMsg.append("]");
				progres.setEstat(ProcedimentUpdateProgressDto.Estat.ERROR);
				progres.setErrorMsg(errMsg.toString());
				throw new Exception(errMsg.toString());
			} else {
				progres.setEstat(ProcedimentUpdateProgressDto.Estat.FINALITZAT);
			}
		}		
	}
	

	private void updateProcedimentsArbre(
			Long entitatId, 
			ArbreNodeDto<UnitatOrganitzativaDto> nodeUo, 
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodesUosAmbError, 
			int reintents, 
			int max_intents,
			ProcedimentUpdateProgressDto progres) {

		if (nodeUo != null) {
			UnitatOrganitzativaDto uoDto = nodeUo.getDades();	
			try {
				List<Procediment> procediments = pluginHelper.procedimentFindByCodiDir3(uoDto.getCodi());
				if (procediments != null && !procediments.isEmpty()) {
					updateProcediments(procediments, entitatId, uoDto.getId());
				} else {
					logger.debug("No hi ha procediments associats al codiDir3 " + uoDto.getCodi());
				}
				// Compta la unitat com a processada en el progrés de l'actualització
				progres.incUnitatsProcessades();
			}catch (Exception e) {
				logger.error("Error consultant els procediments de la UO " + uoDto.getCodiAndNom() + " intent " + reintents + " de " + max_intents + ": " + e.getMessage(), e);
				nodesUosAmbError.add(nodeUo);
			}
			if (nodeUo.getFills() != null) {
				for (ArbreNodeDto<UnitatOrganitzativaDto> fill : nodeUo.getFills()) {
					updateProcedimentsArbre(
							entitatId, 
							fill, 
							nodesUosAmbError, 
							reintents, 
							max_intents,
							progres);
				}
			}
		}
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
		ProcedimentUpdateProgressDto progres = progressosActualitzacio.get(entitatId);
		return progres != null 
				&& progres.getEstat() != ProcedimentUpdateProgressDto.Estat.FINALITZAT
				&& progres.getEstat() != ProcedimentUpdateProgressDto.Estat.ERROR;
	}
	
	@Override
	public ProcedimentUpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return progressosActualitzacio.get(entitatId);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentServiceImpl.class);

}
