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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaEstatEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaFiltreDto;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper.Converter;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class UnitatOrganitzativaServiceImpl implements UnitatOrganitzativaService {

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Resource
	private CacheHelper cacheHelper;

	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId) {
		return unitatOrganitzativaHelper.getObsoletesFromWS(entitatId);		
	}

	@Override
	@Transactional
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId) {
		return unitatOrganitzativaHelper.getNewFromWS(entitatId);		
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
		cacheHelper.evictUnitatsOrganitzativesFindArbreByPare(entitat.getCodiDir3());
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

		String estat = null; 
		if (filtre.getEstat() == UnitatOrganitzativaEstatEnumDto.VIGENTE) {
			estat = "V";
		} else if (filtre.getEstat() == UnitatOrganitzativaEstatEnumDto.EXTINGUIDO) {
			estat = "E";
		} else if (filtre.getEstat() == UnitatOrganitzativaEstatEnumDto.TRANSITORIO) {
			estat = "T";
		} else if (filtre.getEstat() == UnitatOrganitzativaEstatEnumDto.ANULADO) {
			estat = "A";
		}
		
		// Si es filtra per unitat superior llavors es passa la llista d'identificadors possibles de l'arbre.
		List<String> codisUnitatsDescendants = unitatOrganitzativaHelper.getCodisOfUnitatsDescendants(entitat, filtre.getCodiUnitatSuperior()); 
		if (codisUnitatsDescendants.isEmpty())
			codisUnitatsDescendants.add("-"); // per evitar error per llista buida
		
		
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("codiIDenominacioUnitatSuperior", new String[]{"codiUnitatSuperior"});
		PaginaDto<UnitatOrganitzativaDto> resultPagina =  paginacioHelper.toPaginaDto(
				unitatOrganitzativaRepository.findByFiltrePaginat(
						entitat.getCodiDir3(),
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() != null ? filtre.getCodi() : "",
						filtre.getDenominacio() == null || filtre.getDenominacio().isEmpty(), 
						filtre.getDenominacio() != null ? filtre.getDenominacio() : "",
						filtre.getCodiUnitatSuperior() == null || filtre.getCodiUnitatSuperior().isEmpty(),
						codisUnitatsDescendants,
						estat == null,
						estat,
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
		
		
		for (UnitatOrganitzativaDto unitatOrganitzativaDto : resultPagina.getContingut()) {
			UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findByCodi(unitatOrganitzativaDto.getCodiUnitatSuperior());
			if (unitatOrganitzativaEntity != null) {
				unitatOrganitzativaDto.setDenominacioUnitatSuperior(unitatOrganitzativaEntity.getDenominacio());
			}
			List<UnitatOrganitzativaDto> path = unitatOrganitzativaHelper.findPath(
					entitat.getCodiDir3(),
					unitatOrganitzativaDto.getCodi());
			int childArrelIndex;
			if (path.size() != 0) {
				if (path.size() > 1) {
					childArrelIndex = path.size() - 2;
				} else {
					childArrelIndex = 0;
				}
				unitatOrganitzativaDto.setCodiUnitatArrel(path.get(childArrelIndex).getCodi() + " - " + path.get(childArrelIndex).getDenominacio());
			}
		}
		
		return resultPagina;
	}

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
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(
			String entitatCodi, 
			String filtre, 
			boolean ambArrel, 
			boolean nomesAmbBusties) {
		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		
		List<UnitatOrganitzativaEntity> unitats = null;
		
		if (nomesAmbBusties) {
			unitats = unitatOrganitzativaRepository.findByCodiDir3UnitatAndCodiAndDenominacioFiltreNomesAmbBusties(
					entitat.getCodiDir3(),
					filtre == null || filtre.isEmpty(), 
					filtre != null ? filtre : "",
					ambArrel);
			
		} else {
			unitats = unitatOrganitzativaRepository.findByCodiDir3UnitatAndCodiAndDenominacioFiltre(
					entitat.getCodiDir3(),
					filtre == null || filtre.isEmpty(), 
					filtre != null ? filtre : "",
					ambArrel);
		}

		
		
		
		return conversioTipusHelper.convertirList(
				unitats,
				UnitatOrganitzativaDto.class);
	}
	

	@Override
	@Transactional(readOnly = true)
	public UnitatOrganitzativaDto findById(
			Long id) {
		return unitatOrganitzativaHelper.toDto(
				unitatOrganitzativaRepository.findOne(id));
		/*UnitatOrganitzativaEntity unitatEntity = unitatOrganitzativaRepository.findOne(id);
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
		return unitat;*/
	}

	@Override
	@Transactional(readOnly = true)
	public UnitatOrganitzativaDto findByCodi(
			String unitatOrganitzativaCodi) {
		return unitatOrganitzativaHelper.toDto(
				unitatOrganitzativaRepository.findByCodi(unitatOrganitzativaCodi));
		/*UnitatOrganitzativaDto unitat = conversioTipusHelper.convertir(
				,
				UnitatOrganitzativaDto.class);
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
		return unitat;*/
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
	
	private static final Logger logger = LoggerFactory.getLogger(UnitatOrganitzativaServiceImpl.class);
}
