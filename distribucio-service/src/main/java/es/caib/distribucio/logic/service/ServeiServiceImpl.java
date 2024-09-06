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
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.service.ServeiService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ServeiEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.ServeiRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.servei.Servei;

/**
 * Implementació del servei de gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class ServeiServiceImpl implements ServeiService{
	
	@Autowired
	private ServeiRepository serveiRepository;
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
	public PaginaDto<ServeiDto> findAmbFiltre(
			Long entitatId, 
			ServeiFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.trace("Cercant els serveis segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		
		PaginaDto<ServeiDto> llistaServeis = null;

		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodi(filtre.getUnitatOrganitzativa());
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("codiServei", new String[]{"codi"});
		llistaServeis = paginacioHelper.toPaginaDto(
				serveiRepository.findAmbFiltrePaginat(
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
				ServeiDto.class);

		
		return llistaServeis;
	}

	@Override
	@Transactional	
	public void findAndUpdateServeis(Long entitatId) throws Exception {
		int max_intents = 5;
		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
		ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
		List<UnitatOrganitzativaEntity> llistaUnitatsOrganitzatives = unitatOrganitzativaRepository.findByCodiDir3Entitat(entitat.getCodiDir3());
		logger.debug("Actualitzant els serveis de l'entitat " + entitat.getCodi() + " " + entitat.getNom() + " amb " + llistaUnitatsOrganitzatives.size() + " unitats.");
		
		// Marca'm els serveis de Distribució com a extingits
		List<ServeiEntity> llistaServeisDistribucio = serveiRepository.findByEntitat(entitat);
		for (ServeiEntity serveiDistribucio : llistaServeisDistribucio) {
			serveiDistribucio.update(
					serveiDistribucio.getCodi(), 
					serveiDistribucio.getNom(), 
					serveiDistribucio.getCodiSia(),
					ServeiEstatEnumDto.EXTINGIT,
					serveiDistribucio.getUnitatOrganitzativa(), 
					entitatRepository.getReferenceById(entitatId));
			
			serveiRepository.save(serveiDistribucio);
		}
		
		// Consulta l'arbre
		ArbreDto<UnitatOrganitzativaDto> unitatsArbre = 
				unitatOrganitzativaHelper.unitatsOrganitzativesFindArbreByPare(entitat.getCodiDir3());
		int reintents = 0;
		if (unitatsArbre != null) {
			logger.debug("Actualitzant els serveis de l'entitat " + entitat.getCodi() + " " + entitat.getNom() + " amb " + unitatsArbre.toList().size() + " unitats.");
			List<ArbreNodeDto<UnitatOrganitzativaDto>> unitats = new ArrayList<>();
			unitats.add(unitatsArbre.getArrel());
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodesUosAmbError = new ArrayList<>();
			do {
				nodesUosAmbError = new ArrayList<>();
				for(ArbreNodeDto<UnitatOrganitzativaDto> unitat : unitats) {
					updateServeisArbre(
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
				StringBuilder errMsg = new StringBuilder("No S'han pogut consultar i actualitzar correctament els serveis per les següents unitats organitzatives després de " + max_intents + " reintents :[");
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
	

	private void updateServeisArbre(
			Long entitatId, 
			ArbreNodeDto<UnitatOrganitzativaDto> nodeUo, 
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodesUosAmbError, 
			int reintents, 
			int max_intents) {

		if (nodeUo != null) {
			UnitatOrganitzativaDto uoDto = nodeUo.getDades();	
			try {
				List<Servei> serveis = pluginHelper.serveiFindByCodiDir3(uoDto.getCodi());
				if (serveis != null && !serveis.isEmpty()) {
					updateServeis(serveis, entitatId, uoDto.getId());
				} else {
					logger.debug("No hi ha serveis associats al codiDir3 " + uoDto.getCodi());
				}
			}catch (Exception e) {
				logger.error("Error consultant els serveis de la UO " + uoDto.getCodiAndNom() + " intent " + reintents + " de " + max_intents + ": " + e.getMessage(), e);
				nodesUosAmbError.add(nodeUo);
			}
			if (nodeUo.getFills() != null) {
				for (ArbreNodeDto<UnitatOrganitzativaDto> fill : nodeUo.getFills()) {
					updateServeisArbre(
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
	private void updateServeis(
			List<Servei> serveis,
			Long entitatId,
			Long unitatOrganitzativaId) {
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findById(unitatOrganitzativaId).orElse(null);
		for (Servei servei: serveis) {
			if (servei.getCodigo() != null && !servei.getCodigo().isEmpty()) {
				ServeiEntity serveiEntity = serveiRepository.findByCodi(
						entitatId,
						servei.getCodigo());
				EntitatEntity entitat = entitatRepository.findById(entitatId).orElse(null);
				if (serveiEntity == null) {
					serveiEntity = ServeiEntity.getBuilder(
							servei.getCodigo(),
							servei.getNombre(),
							servei.getCodigoSIA(),
							ServeiEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat).built();
					serveiRepository.save(serveiEntity);
				} else {
					serveiEntity.update(
							servei.getCodigo(),
							servei.getNombre(),
							servei.getCodigoSIA(),
							ServeiEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat);
					serveiRepository.save(serveiEntity);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ServeiDto findByCodiSia(Long entitatId, String codiSia) {
		return conversioTipusHelper.convertir(
				serveiRepository.findByCodiSia(
						entitatId, 
						codiSia), 
				ServeiDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServeiDto> findByNomOrCodiSia(Long entitatId, String search) {
		if (search == null || search.isEmpty()) {
			return new ArrayList<>();
		}			
		return conversioTipusHelper.convertirList(
				serveiRepository.findByNomOrCodiSia(
						entitatId, 
						search != null ? search : ""), 
				ServeiDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(ServeiServiceImpl.class);

}
