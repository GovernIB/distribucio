/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper.Converter;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaFiltreDto;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;

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
		EntitatEntity entitat = entitatRepository.getReferenceById(entidadId);
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
        EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
        synchronize(entitat);
	}

	@Override
	@Transactional
	public void forcedSynchronize(Long entitatId) {
		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
        entitat.updateFechaSincronizacion(null);
        synchronize(entitat);
	}

    private void synchronize(EntitatEntity entitat) {
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
		logger.trace("Cercant les unitats organitzatives segons el filtre ("
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
		
		 
		// Es crean diferents llistes de 1000 com a màxim per evitar error a la consulta
		List<String> llistaCodisUnitats1 = new ArrayList<>();
		List<String> llistaCodisUnitats2 = new ArrayList<>();
		List<String> llistaCodisUnitats3 = new ArrayList<>();
		List<String> llistaCodisUnitats4 = new ArrayList<>();
		List<String> llistaCodisUnitats5 = new ArrayList<>();
		for(int i=0; i<codisUnitatsDescendants.size(); i++) {
			if (i>=0 && i<1000) {
				llistaCodisUnitats1.add(codisUnitatsDescendants.get(i));
			} else  if (i>=1000 && i<2000) {
				llistaCodisUnitats2.add(codisUnitatsDescendants.get(i));
			} else  if (i>=2000 && i<3000) {
				llistaCodisUnitats3.add(codisUnitatsDescendants.get(i));
			} else  if (i>=3000 && i<4000) {
				llistaCodisUnitats4.add(codisUnitatsDescendants.get(i));
			} else  if (i>=4000 && i<5000) {
				llistaCodisUnitats5.add(codisUnitatsDescendants.get(i));
			}
		}
		
		
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("codiIDenominacioUnitatSuperior", new String[]{"codiUnitatSuperior"});
		PaginaDto<UnitatOrganitzativaDto> resultPagina =  paginacioHelper.toPaginaDto(
				unitatOrganitzativaRepository.findByFiltrePaginatAmbLlistes(
						entitat.getCodiDir3(),
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() != null ? filtre.getCodi() : "",
						filtre.getDenominacio() == null || filtre.getDenominacio().isEmpty(), 
						filtre.getDenominacio() != null ? filtre.getDenominacio() : "",
						filtre.getCodiUnitatSuperior() == null || filtre.getCodiUnitatSuperior().isEmpty(),
						//codisUnitatsDescendants,
						!llistaCodisUnitats1.isEmpty() ? llistaCodisUnitats1 : null,
						!llistaCodisUnitats2.isEmpty() ? llistaCodisUnitats2 : llistaCodisUnitats1,
						!llistaCodisUnitats3.isEmpty() ? llistaCodisUnitats3 : llistaCodisUnitats1,
						!llistaCodisUnitats4.isEmpty() ? llistaCodisUnitats4 : llistaCodisUnitats1,
						!llistaCodisUnitats5.isEmpty() ? llistaCodisUnitats5 : llistaCodisUnitats1,
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
			UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(entitat.getCodiDir3(), unitatOrganitzativaDto.getCodiUnitatSuperior());
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
		EntitatEntity entitat = entitatRepository.getReferenceById(id);
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
			boolean nomesAmbBusties,
			boolean isUsuari) {
		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		
		List<UnitatOrganitzativaEntity> unitats = null;
		
		if (isUsuari) {
			unitats = unitatOrganitzativaHelper.findByCodiDir3UnitatAndCodiAndDenominacioFiltreNomesAmbBustiesUsuariActual(
					entitat, 
					filtre, 
					ambArrel);
		} else {
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
		}
		
		return conversioTipusHelper.convertirList(
				unitats,
				UnitatOrganitzativaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(
			String filtre) {
		
		List<UnitatOrganitzativaEntity> unitats = null;
		
		unitats = unitatOrganitzativaRepository.findByCodiAndDenominacioFiltre(
				filtre == null || filtre.isEmpty(), 
				filtre != null ? filtre : "");		
		
		return conversioTipusHelper.convertirList(
				unitats,
				UnitatOrganitzativaDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(
			String entitatCodi, 
			String codiUnitatSuperior, 
			String filtre, 
			boolean ambArrel, 
			boolean nomesAmbBusties) {
		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		
		List<UnitatOrganitzativaEntity> unitats = null;
		
		if (nomesAmbBusties) {
			unitats = unitatOrganitzativaRepository.findByCodiDir3UnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltreNomesAmbBusties(
					entitat.getCodiDir3(),
					codiUnitatSuperior,
					filtre == null || filtre.isEmpty(), 
					filtre != null ? filtre : "",
					ambArrel);
			
		} else {
			unitats = unitatOrganitzativaRepository.findByCodiDir3UnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltre(
					entitat.getCodiDir3(),
					codiUnitatSuperior,
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
				unitatOrganitzativaRepository.getReferenceById(id));
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
	public UnitatOrganitzativaDto findByCodiDir3EntitatAndCodi(
            String codiDir3Entitat,
			String unitatOrganitzativaCodi) {
		return unitatOrganitzativaHelper.toDto(
				unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(codiDir3Entitat, unitatOrganitzativaCodi));
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
