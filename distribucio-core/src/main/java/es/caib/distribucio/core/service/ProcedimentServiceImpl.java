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

import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.core.api.dto.ProcedimentFiltreDto;
import es.caib.distribucio.core.api.service.ProcedimentService;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.ProcedimentEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PluginHelper;
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
						filtre.getUnitatOrganitzativa() == null || filtre.getUnitatOrganitzativa().isEmpty(), 
						filtre.getUnitatOrganitzativa() != null ? filtre.getUnitatOrganitzativa() : "", 
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

	@Override
	@Transactional	
	public void findAndUpdateProcediments(Long entitatId) throws Exception {
		int max_intents = 5;
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		List<UnitatOrganitzativaEntity> llistaUnitatsOrganitzatives = unitatOrganitzativaRepository.findByCodiDir3Entitat(entitat.getCodiDir3());
		logger.debug("Actualitzant els procediments de l'entitat " + entitat.getCodi() + " " + entitat.getNom() + " amb " + llistaUnitatsOrganitzatives.size() + " unitats.");
		
		// Marca'm els procediments de Distribució com a extingits
		List<ProcedimentEntity> llistaProcedimentsDistribucio = procedimentRepository.findAll();
		for (ProcedimentEntity procedimentDistribucio : llistaProcedimentsDistribucio) {
			procedimentDistribucio.update(
					procedimentDistribucio.getCodi(), 
					procedimentDistribucio.getNom(), 
					procedimentDistribucio.getCodiSia(),
					ProcedimentEstatEnumDto.EXTINGIT,
					procedimentDistribucio.getUnitatOrganitzativa(), 
					entitatRepository.findOne(entitatId));
			
			procedimentRepository.save(procedimentDistribucio);
		}
		
		List<UnitatOrganitzativaEntity> unitatsAmbErrors = new ArrayList<>();
		int reintents = 0;
		do {
			unitatsAmbErrors = new ArrayList<>();
			for (UnitatOrganitzativaEntity unitatOrganitzativa : llistaUnitatsOrganitzatives) {
				try {
					List<Procediment> procediments = pluginHelper.procedimentFindByCodiDir3(unitatOrganitzativa.getCodi());
					if (procediments != null && !procediments.isEmpty()) { 
						updateProcediments(procediments, entitatId, unitatOrganitzativa.getId());
					} else {
						logger.debug("No hi ha procediments associats al codiDir3 " + unitatOrganitzativa.getCodi());
					}
				}catch (Exception e) {
					logger.error("Error consultant els procediments de la UO " + unitatOrganitzativa.getCodiAndNom() + " intent " + reintents + " de " + max_intents + ": " + e.getMessage(), e);
					unitatsAmbErrors.add(unitatOrganitzativa);
				}
			}
			llistaUnitatsOrganitzatives = unitatsAmbErrors;
			reintents++;
		} while (reintents <= max_intents 
				&& !llistaUnitatsOrganitzatives.isEmpty());	
		
		if (llistaUnitatsOrganitzatives.size() > 0) {
			// Llença excepció
			StringBuilder errMsg = new StringBuilder("No S'han pogut consultar i actualitzar correctament els procediments per les següents unitats organitzatives després de " + max_intents + " reintents :[");
			for (int i=0; i < llistaUnitatsOrganitzatives.size(); i++) {
				errMsg.append(llistaUnitatsOrganitzatives.get(i).getCodiAndNom());
				if (i < llistaUnitatsOrganitzatives.size()-1) {
					errMsg.append(", ");
				}
			}
			errMsg.append("]");
			throw new Exception(errMsg.toString());
		}		
	}
	

	@Transactional
	private void updateProcediments(
			List<Procediment> procediments, 
			Long entitatId, 
			Long unitatOrganitzativaId) {
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findOne(unitatOrganitzativaId);
		
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
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitatRepository.findOne(entitatId)).built();
					
					procedimentRepository.save(procedimentEntity);
				
				}else {		
					procedimentEntity.update(
							procediment.getCodigo(), 
							procediment.getNombre(), 
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitatRepository.findOne(entitatId));
					
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
						codiSia == null, 
						codiSia != null ? codiSia : ""), 
				ProcedimentDto.class);						
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findByNom(Long entitatId, String nom) {
		return conversioTipusHelper.convertirList(
				procedimentRepository.findByNom(
						entitatId, 
						nom == null,
						nom != null ? nom : ""), 
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
