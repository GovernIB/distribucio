package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.ArbreNodeDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.ProcedimentFiltreDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.service.ProcedimentService;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.ProcedimentEntity;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.ProcedimentRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
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

		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("codiProcediment", new String[]{"codi"});
		llistaProcediments = paginacioHelper.toPaginaDto(
				procedimentRepository.findAmbFiltrePaginat(
						entitatId, 
						filtre.getUnitatOrganitzativa().getCodi() == null || filtre.getUnitatOrganitzativa().getCodi().isEmpty(), 
						filtre.getUnitatOrganitzativa().getCodi() != null ? filtre.getUnitatOrganitzativa().getCodi() : "", 
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() != null ? filtre.getCodi() : "", 
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom() != null ? filtre.getNom() : "", 
						filtre.getCodiSia() == null || filtre.getCodiSia().isEmpty(), 
						filtre.getCodiSia() != null ? filtre.getCodiSia() : "", 
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio)), 
				ProcedimentDto.class);

		
		return llistaProcediments;
	}

	@Override
	@Transactional
	public void findAndUpdateProcediments(Long entitatId) throws Exception {

		
		int max_intents = 5;
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		
		// Consulta l'arbre
		ArbreDto<UnitatOrganitzativaDto> unitatsArbre = 
				unitatOrganitzativaHelper.unitatsOrganitzativesFindArbreByPare(entitat.getCodiDir3());
		int reintents = 0;
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
							max_intents);
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
				throw new Exception(errMsg.toString());
			}	
		}	
	}
	

	private void updateProcedimentsArbre(
			Long entitatId, 
			ArbreNodeDto<UnitatOrganitzativaDto> nodeUo, 
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodesUosAmbError, 
			int reintents, 
			int max_intents) {

		if (nodeUo != null) {
			UnitatOrganitzativaDto uoDto = nodeUo.getDades();	
			try {
				List<Procediment> procediments = pluginHelper.procedimentFindByCodiDir3(uoDto.getCodi());
				if (procediments != null && !procediments.isEmpty()) {
					updateProcediments(procediments, entitatId, uoDto.getId());
				} else {
					logger.debug("No hi ha procediments associats al codiDir3 " + uoDto.getCodi());
				}
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
							max_intents);
				}
			}
		}
	}

	@Transactional
	private void updateProcediments(
			List<Procediment> procediments, 
			Long entitatId, 
			Long unitatOrganitzativaId) {
		for (Procediment procediment: procediments) {
			if (procediment.getCodigo() != null 
					&& !procediment.getCodigo().isEmpty()) {
				ProcedimentEntity procedimentEntity = procedimentRepository.findByCodi(
																entitatId, 
																procediment.getCodigo());
				if (procedimentEntity == null) {
					procedimentEntity = ProcedimentEntity.getBuilder(
							procediment.getCodigo(), 
							procediment.getNombre(), 
							procediment.getCodigoSIA(),
							unitatOrganitzativaRepository.findOne(unitatOrganitzativaId), 
							entitatRepository.findOne(entitatId)).built();
					
					procedimentRepository.save(procedimentEntity);
				
				}else {
					procedimentEntity.update(
							procediment.getCodigo(), 
							procediment.getNombre(), 
							procediment.getCodigoSIA(),
							unitatOrganitzativaRepository.findOne(unitatOrganitzativaId), 
							entitatRepository.findOne(entitatId));
					
					procedimentRepository.save(procedimentEntity);
				}
			}
		}
		procedimentRepository.flush();
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
	

	private static final Logger logger = LoggerFactory.getLogger(ProcedimentServiceImpl.class);

}
