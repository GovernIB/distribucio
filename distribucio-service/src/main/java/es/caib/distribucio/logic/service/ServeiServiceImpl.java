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

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.ServeiHelper;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto.Estat;
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
	private EntitatRepository entitatRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ServeiHelper serveiHelper;

	/** Progrés d'acualització actual.*/
	private static Map<Long, UpdateProgressDto> serveisActualitzacio = new HashMap<Long, UpdateProgressDto>();

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

	/** Mètode per trobar i actualitzar els serveis. Es pot fer manualment o des de la tasca
	 * programada.
	 */
	@Override
	@Transactional	
	public void findAndUpdateServeis(Long entitatId) throws Exception {
		
		String msgInfo;
		UpdateProgressDto progres = null;
		// Comprova si hi ha una altre instància del procés en execució
		if (isUpdatingServeis(entitatId)) {
			logger.debug("Ja existeix un altre procés que està executant l'actualització de serveis per l'entitat " + entitatId + ".");
			return;	// S'està executant l'actualitzacio
		} else {
			progres = new UpdateProgressDto();
			serveisActualitzacio.put(entitatId, progres);
		}
		
		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
		msgInfo = "Inici del procés d'actualització de serveis de l'entitat " + entitat.getCodi() + " " + entitat.getNom();
		progres.setEstat(Estat.INICIALITZANT);
		logger.info(msgInfo);
		
		List<Servei> serveiList = null;
		int reintents = 1;
		boolean errorConsultaServeis = false;
		Exception exConsultaServeis = null;
		String errMsg = "-";
		do {
			try {
				msgInfo = "Obtenint el llistat de serveis per a l'entitat " + entitat.getCodiDir3();
				logger.info(msgInfo);
				serveiList = pluginHelper.serveiFindByCodiDir3(entitat.getCodiDir3());				
			} catch (Exception e) {
				exConsultaServeis = e;
				errMsg = "Error consultant els procediments per l'entitat: " + entitat.getCodiDir3();
				errorConsultaServeis = reintents++ >= 3;				
			}
		} 
		while (serveiList == null && !errorConsultaServeis);
		
		// Comprova si hi ha hagut errors consultant els serveis
		if (errorConsultaServeis) {
			progres.setEstat(UpdateProgressDto.Estat.ERROR);
			throw new Exception(errMsg, exConsultaServeis);
		}
		if (serveiList == null || serveiList.isEmpty()) {
			throw new Exception(
					"No s'ha obtingut cap llista o resultat per la consulta de serveis: (llista " + (serveiList == null? "nul·la" :  "buida") + ")"
			);
		}
		
		// Processa els serveis consultats
		msgInfo="S'han obtingut " + serveiList.size() + " serveis vigents a Rolsac.";
		logger.info(msgInfo);
		progres.setEstat(Estat.ACTUALITZANT);
		progres.setTotal(serveiList.size());
		
		// Crea un Map amb els serveis de Distribucio per codi
		Map<String, Servei> serveiMap = new HashMap<String, Servei>();
		for (Servei servei : serveiList) {
			serveiMap.put(servei.getCodigo(), servei);
		}
		
		// Deshabilita els serveis que no hagi retornat Distribucio
		serveiHelper.actualtizarServeisNoVigents(entitat, serveiMap);
		
		// Processa tots els serveis, actualitza-ne la informació, donant-los d'alta i revisant la seva UO		
		msgInfo = "Es procedeix a processar els " + serveiList.size() + " serveis consultats a Rolsac.";
		logger.info(msgInfo);
		
		// Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
		Map<String, UnitatOrganitzativaEntity> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativaEntity>();
		for (Servei servei : serveiList) {
			// Tracta el servei en una transacció a part.
			serveiHelper.actualitzaServei(servei, unitatsOrganitzatives, entitat);
			progres.incProcessats();
		}
		progres.setEstat(UpdateProgressDto.Estat.FINALITZAT);
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

	@Override
	public boolean isUpdatingServeis(Long entitatId) {
		UpdateProgressDto progres = serveisActualitzacio.get(entitatId);
		return progres != null 
				&& progres.getEstat() != UpdateProgressDto.Estat.FINALITZAT
				&& progres.getEstat() != UpdateProgressDto.Estat.ERROR;
	}
	
	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return serveisActualitzacio.get(entitatId);
	}

	private static final Logger logger = LoggerFactory.getLogger(ServeiServiceImpl.class);

}
