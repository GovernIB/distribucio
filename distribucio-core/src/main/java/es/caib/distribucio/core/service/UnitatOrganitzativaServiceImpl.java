/**
 * 
 */
package es.caib.distribucio.core.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.MunicipiDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProvinciaDto;
import es.caib.distribucio.core.api.dto.TipusViaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaFiltreDto;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper.Converter;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class UnitatOrganitzativaServiceImpl implements UnitatOrganitzativaService {

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	
	
	
	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> predictSynchronization(Long entitatId) {

		return unitatOrganitzativaHelper.predictSynchronization(entitatId);		
	}
	
	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entitatId) {

		return unitatOrganitzativaHelper.predictFirstSynchronization(entitatId);		
	}
	
	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId) {

		return unitatOrganitzativaHelper.getVigentsFromWebService(entidadId);
	}
	
	@Override
	@Transactional
	public boolean isFirstSincronization(Long entidadId) {

		EntitatEntity entitat = entitatRepository.getOne(entidadId);

		if (entitat.getFechaSincronizacion() == null) {
			return true;
		} else {
			return false;
		}

	}
	
	@Override
	@Transactional
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		return unitatOrganitzativaHelper.getLastHistoricos(uo);
	}
	

	@Override
	@Transactional
	public void synchronize(Long entitatId) {
		EntitatEntity entitat = entitatRepository.getOne(entitatId);
		
		unitatOrganitzativaHelper.sincronizarOActualizar(entitat);		
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		// if this is first synchronization set current date as a date of first
		// sinchronization and the last actualization
		if (entitat.getFechaSincronizacion() == null) {
			entitat.updateFechaActualizacion(timestamp);
			entitat.updateFechaSincronizacion(timestamp);
		// if this is not the first synchronization only change date of actualization
		} else {
			entitat.updateFechaActualizacion(timestamp);
		}
		
		
//		List<UnitatOrganitzativaDto> unitatsOrganitzativesPerEntitat = new ArrayList<UnitatOrganitzativaDto>();
//		
//			unitatsOrganitzativesPerEntitat = unitatOrganitzativaHelper.findUnitatsOrganitzativesPerEntitatFromPlugin(entitatActual.getCodi());
//	
//		for (UnitatOrganitzativaDto unitatOrganitzativa : unitatsOrganitzativesPerEntitat) {
//			create(entitatActual.getId(), unitatOrganitzativa);
//		}
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltre(
			Long entitatId,
			UnitatOrganitzativaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Cercant les unitats organitzatives segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();

		
		PaginaDto<UnitatOrganitzativaDto> resultPagina =  paginacioHelper.toPaginaDto(
				unitatOrganitzativaRepository.findByCodiDir3AndUnitatDenominacioFiltrePaginat(
						entitat.getCodiDir3(),
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi(),
						filtre.getDenominacio() == null || filtre.getDenominacio().isEmpty(), 
						filtre.getDenominacio(),
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio)),
				UnitatOrganitzativaDto.class,
				new Converter<UnitatOrganitzativaEntity, UnitatOrganitzativaDto>() {
					@Override
					public UnitatOrganitzativaDto convert(UnitatOrganitzativaEntity source) {
						UnitatOrganitzativaDto unitatDto = conversioTipusHelper.convertir(
								source,
								UnitatOrganitzativaDto.class);
						return unitatDto;
					}
				});
		return resultPagina;
	}
	
	
//	@Transactional
//	public void create(
//			Long entitatId,
//			UnitatOrganitzativaDto unitatOrganitzativa) {
//		logger.debug("Creant una nova unitat organitzativa ("
//				+ "entitatId=" + entitatId + ", "
//				+ "unitatOrganitzativa=" + unitatOrganitzativa + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				false,
//				true,
//				false);
//		
//		UnitatOrganitzativaEntity entity = UnitatOrganitzativaEntity.getBuilder(
//				unitatOrganitzativa.getCodi(),
//				unitatOrganitzativa.getDenominacio(),
//				unitatOrganitzativa.getNifCif(),
//				unitatOrganitzativa.getCodiUnitatSuperior(),
//				unitatOrganitzativa.getCodiUnitatArrel(),
//				unitatOrganitzativa.getDataCreacioOficial(),
//				unitatOrganitzativa.getDataSupressioOficial(),
//				unitatOrganitzativa.getDataExtincioFuncional(),
//				unitatOrganitzativa.getDataAnulacio(),
//				unitatOrganitzativa.getEstat(), 
//				unitatOrganitzativa.getCodiPais(),
//				unitatOrganitzativa.getCodiComunitat(),
//				unitatOrganitzativa.getCodiProvincia(),
//				unitatOrganitzativa.getCodiPostal(),
//				unitatOrganitzativa.getNomLocalitat(),
//				unitatOrganitzativa.getLocalitat(),
//				unitatOrganitzativa.getAdressa(),
//				unitatOrganitzativa.getTipusVia(),
//				unitatOrganitzativa.getNomVia(),
//				unitatOrganitzativa.getNumVia(),
//				false).build();
//		unitatOrganitzativaRepository.save(entity);
//		
//	}
//	
	@Override
	@Transactional(readOnly = true)
	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id){
		
		EntitatEntity entitat = entitatRepository.findOne(id);
		return unitatOrganitzativaHelper.unitatsOrganitzativesFindArbreByPareAndEstatVigent(entitat.getCodiDir3());
	}
	

	
	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) { 
		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		return conversioTipusHelper.convertirList(
				unitatOrganitzativaRepository.findByCodiDir3Entitat(entitat.getCodiDir3()),
				UnitatOrganitzativaDto.class);
//		return cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi).toDadesList();
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(
			String entitatCodi, String filtre) { 
		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		return conversioTipusHelper.convertirList(
				unitatOrganitzativaRepository.findByCodiDir3UnitatAndCodiAndDenominacioFiltre(
						entitat.getCodiDir3(),
						filtre == null || filtre.isEmpty(), 
						filtre),
				UnitatOrganitzativaDto.class);
	}
	
	


	
	@Override
	@Transactional(readOnly = true)
	public UnitatOrganitzativaDto findById(
			Long id) {
		UnitatOrganitzativaEntity unitatEntity = unitatOrganitzativaRepository.findOne(id);
		UnitatOrganitzativaDto unitat = conversioTipusHelper.convertir(
				unitatOrganitzativaRepository.findOne(id),
				UnitatOrganitzativaDto.class);
		
		unitat = UnitatOrganitzativaHelper.assignAltresUnitatsFusionades(unitatEntity, unitat);
		
		if (unitat != null) {
			unitat.setAdressa(
					getAdressa(
							unitat.getTipusVia(), 
							unitat.getNomVia(), 
							unitat.getNumVia()));
			if (unitat.getCodiPais() != null && !"".equals(unitat.getCodiPais())) {
				unitat.setCodiPais(("000" + unitat.getCodiPais()).substring(unitat.getCodiPais().length()));
			}
			if (unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				unitat.setCodiComunitat(("00" + unitat.getCodiComunitat()).substring(unitat.getCodiComunitat().length()));
			}
			if ((unitat.getCodiProvincia() == null || "".equals(unitat.getCodiProvincia())) && 
					unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				List<ProvinciaDto> provincies = cacheHelper.findProvinciesPerComunitat(unitat.getCodiComunitat());
				if (provincies != null && provincies.size() == 1) {
					unitat.setCodiProvincia(provincies.get(0).getCodi());
				}		
			}
			if (unitat.getCodiProvincia() != null && !"".equals(unitat.getCodiProvincia())) {
				unitat.setCodiProvincia(("00" + unitat.getCodiProvincia()).substring(unitat.getCodiProvincia().length()));
				if (unitat.getLocalitat() == null && unitat.getNomLocalitat() != null) {
					MunicipiDto municipi = findMunicipiAmbNom(
							unitat.getCodiProvincia(), 
							unitat.getNomLocalitat());
					if (municipi != null)
						unitat.setLocalitat(municipi.getCodi());
					else
						logger.error("UNITAT ORGANITZATIVA. No s'ha trobat la localitat amb el nom: '" + unitat.getNomLocalitat() + "'");
				}
			}
		}
		return unitat;
	}
	
	
	
	

	@Override
	@Transactional(readOnly = true)
	public UnitatOrganitzativaDto findByCodi(
			String unitatOrganitzativaCodi) {
		UnitatOrganitzativaDto unitat = conversioTipusHelper.convertir(
				unitatOrganitzativaRepository.findByCodi(unitatOrganitzativaCodi),
				UnitatOrganitzativaDto.class);
		
//		UnitatOrganitzativaDto unitat = pluginHelper.unitatsOrganitzativesFindByCodi(
//				unitatOrganitzativaCodi);
		if (unitat != null) {
			unitat.setAdressa(
					getAdressa(
							unitat.getTipusVia(), 
							unitat.getNomVia(), 
							unitat.getNumVia()));
			if (unitat.getCodiPais() != null && !"".equals(unitat.getCodiPais())) {
				unitat.setCodiPais(("000" + unitat.getCodiPais()).substring(unitat.getCodiPais().length()));
			}
			if (unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				unitat.setCodiComunitat(("00" + unitat.getCodiComunitat()).substring(unitat.getCodiComunitat().length()));
			}
			if ((unitat.getCodiProvincia() == null || "".equals(unitat.getCodiProvincia())) && 
					unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				List<ProvinciaDto> provincies = cacheHelper.findProvinciesPerComunitat(unitat.getCodiComunitat());
				if (provincies != null && provincies.size() == 1) {
					unitat.setCodiProvincia(provincies.get(0).getCodi());
				}		
			}
			if (unitat.getCodiProvincia() != null && !"".equals(unitat.getCodiProvincia())) {
				unitat.setCodiProvincia(("00" + unitat.getCodiProvincia()).substring(unitat.getCodiProvincia().length()));
				if (unitat.getLocalitat() == null && unitat.getNomLocalitat() != null) {
					MunicipiDto municipi = findMunicipiAmbNom(
							unitat.getCodiProvincia(), 
							unitat.getNomLocalitat());
					if (municipi != null)
						unitat.setLocalitat(municipi.getCodi());
					else
						logger.error("UNITAT ORGANITZATIVA. No s'ha trobat la localitat amb el nom: '" + unitat.getNomLocalitat() + "'");
				}
			}
		}
		return unitat;
	}

	@Override
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio,
			String nivellAdm, 
			String comunitat, 
			String provincia, 
			String localitat, 
			Boolean arrel) {
		return pluginHelper.unitatsOrganitzativesFindByFiltre(
				codiDir3, 
				denominacio,
				nivellAdm, 
				comunitat, 
				provincia, 
				localitat, 
				arrel);
	}



	private String getAdressa(
			Long tipusVia,
			String nomVia,
			String numVia) {
		String adressa = "";
		if (tipusVia != null) {
			List<TipusViaDto> tipus = cacheHelper.findTipusVia();
			for (TipusViaDto tvia: tipus) {
				if (tvia.getCodi().equals(tipusVia)) {
					adressa = tvia.getDescripcio() + " ";
					break;
				}
			}
		}
		adressa += nomVia;
		if (numVia != null) {
			adressa += ", " + numVia;
		}
		return adressa;
	}

	private MunicipiDto findMunicipiAmbNom(
			String provinciaCodi,
			String municipiNom) {
		MunicipiDto municipi = null;
		List<MunicipiDto> municipis = cacheHelper.findMunicipisPerProvincia(provinciaCodi);
		if (municipis != null) {
			for (MunicipiDto mun: municipis) {
				if (mun.getNom().equalsIgnoreCase(municipiNom)) { 
					municipi = mun;
					break;
				}
			}
		}
		return municipi;
	}

	private static final Logger logger = LoggerFactory.getLogger(UnitatOrganitzativaServiceImpl.class);

}
