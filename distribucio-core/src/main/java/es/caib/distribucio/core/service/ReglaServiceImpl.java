/**
 * 
 */
package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.core.api.dto.RegistreSimulatAccionEnumDto;
import es.caib.distribucio.core.api.dto.RegistreSimulatDto;
import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.ReglaFiltreActivaEnumDto;
import es.caib.distribucio.core.api.dto.ReglaFiltreDto;
import es.caib.distribucio.core.api.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.service.ReglaService;
import es.caib.distribucio.core.entity.BackofficeEntity;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.core.repository.BackofficeRepository;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.ReglaRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;

/**
 * Implementació dels mètodes per a gestionar regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ReglaServiceImpl implements ReglaService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private ReglaRepository reglaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private BackofficeRepository backofficeRepository;
	@Resource
	private ReglaHelper reglaHelper;
	@Resource
	private BustiaHelper bustiaHelper;
	@Resource
	private BustiaRepository bustiaRepository;

	@Override
	@Transactional
	public ReglaDto create(
			Long entitatId,
			ReglaDto reglaDto) {
		logger.debug("Creant una nova regla ("
				+ "entitatId=" + entitatId + ", "
				+ "regla=" + reglaDto + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		int ordre = reglaRepository.countByEntitat(entitat);

		ReglaEntity reglaEntity = ReglaEntity.getBuilder(
				entitat,
				reglaDto.getNom(),
				reglaDto.getTipus(),
				reglaDto.getPresencial(),
				reglaDto.getAssumpteCodiFiltre(),
				reglaDto.getProcedimentCodiFiltre(),
				reglaDto.getUnitatOrganitzativaFiltre() != null ? unitatOrganitzativaRepository.findOne(reglaDto.getUnitatOrganitzativaFiltre().getId()) : null,
				reglaDto.getBustiaFiltreId() != null ? bustiaRepository.findOne(reglaDto.getBustiaFiltreId()) : null,
				ordre).
				descripcio(reglaDto.getDescripcio()).
				build();
		switch(reglaDto.getTipus()) {
		case BACKOFFICE:
			BackofficeEntity backofficeEntity = backofficeRepository.findOne(reglaDto.getBackofficeDestiId());
			reglaEntity.updatePerTipusBackoffice(
					backofficeEntity);
			break;
		case BUSTIA:
			BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					reglaDto.getBustiaDestiId(),
					false);
			reglaEntity.updatePerTipusBustia(
					bustia);
			break;
		case UNITAT:
			UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findOne(reglaDto.getUnitatDestiId());
			reglaEntity.updatePerTipusUnitat(
					unitatOrganitzativaEntity);
			break;
		}

		return toReglaDto(reglaRepository.save(reglaEntity));
	}

	@Override
	@Transactional
	public ReglaDto update(
			Long entitatId,
			ReglaDto reglaDto) throws NotFoundException {
		logger.debug("Modificant la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "regla=" + reglaDto + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity reglaEntity = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaDto.getId());
		BackofficeEntity backofficeDestiId;
		if (reglaDto.getTipus().equals(ReglaTipusEnumDto.BUSTIA) || 
			reglaDto.getTipus().equals(ReglaTipusEnumDto.UNITAT) ) {
			backofficeDestiId = null;
		}else {
			backofficeDestiId = backofficeRepository.findOne(reglaDto.getBackofficeDestiId());
		}
		reglaEntity.update(
				backofficeDestiId, 
				reglaDto.getNom(),
				reglaDto.getDescripcio(),
				reglaDto.getTipus(),
				reglaDto.getPresencial(),
				reglaDto.getAssumpteCodiFiltre(),
				reglaDto.getProcedimentCodiFiltre(),
				reglaDto.getUnitatOrganitzativaFiltre() != null ? unitatOrganitzativaRepository.findOne(reglaDto.getUnitatOrganitzativaFiltre().getId()) : null,
				reglaDto.getBustiaFiltreId() != null ? bustiaRepository.findOne(reglaDto.getBustiaFiltreId()) : null);
		switch(reglaDto.getTipus()) {
		case BACKOFFICE:
			BackofficeEntity backofficeEntity = backofficeRepository.findOne(reglaDto.getBackofficeDestiId());
			reglaEntity.updatePerTipusBackoffice(
					backofficeEntity);
			break;
		case BUSTIA:
			BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					reglaDto.getBustiaDestiId(),
					false);
			reglaEntity.updatePerTipusBustia(
					bustia);
			break;
		case UNITAT:
			UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findOne(reglaDto.getUnitatDestiId());
			reglaEntity.updatePerTipusUnitat(
					unitatOrganitzativaEntity);
			break;
		}
		return toReglaDto(reglaRepository.save(reglaEntity));
	}

	@Override
	@Transactional
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			boolean activa) throws NotFoundException {
		logger.debug("Modificant propietat activa de la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ", "
				+ "activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		regla.updateActiva(activa);
		return toReglaDto(reglaRepository.save(regla));
	}
	
	@Override
	@Transactional
	public ReglaDto updatePresencial(Long entitatId, Long reglaId, boolean activa, ReglaPresencialEnumDto presencial)
			throws NotFoundException {
			logger.debug("Modificant propietats activa i presencial de la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ", "
				+ "activa=" + activa + ")");		
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					true,
					false);
			ReglaEntity regla = entityComprovarHelper.comprovarRegla(
					entitat,
					reglaId);
			regla.updateActiva(activa);
			regla.updatePresencial(presencial);
			return toReglaDto(reglaRepository.save(regla));
	}

	@Override
	@Transactional
	public ReglaDto delete(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		logger.debug("Esborrant la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		
		
		List<ReglaEntity> regles = reglaRepository.findByEntitatOrderByOrdreAsc(regla.getEntitat());
		regles.remove(regla);
		int i = 0;
		for (ReglaEntity r : regles) {
			r.updateOrdre(i++);
		}
		
		
		// cannot remove busties containing any anotacions
		if (registreRepository.findByRegla(regla) != null && !registreRepository.findByRegla(regla).isEmpty()) {
			String missatgeError = "No es pot esborrar la regla amb anotacions connectat (" + 
					"reglaId=" + reglaId + ")";
			logger.error(missatgeError);
			throw new ValidationException(
					reglaId,
					ReglaEntity.class,
					missatgeError);
		}
		
		
		
		reglaRepository.delete(regla);
		return toReglaDto(regla);
	}

	@Override
	@Transactional
	public ReglaDto moveUp(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		logger.debug("Movent la regla per amunt ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		canviPosicio(
				regla,
				regla.getOrdre() - 1);
		return toReglaDto(regla);
	}

	@Override
	@Transactional
	public ReglaDto moveDown(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		logger.debug("Movent la regla per avall ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		canviPosicio(
				regla,
				regla.getOrdre() + 1);
		return toReglaDto(regla);
	}

	@Override
	@Transactional
	public ReglaDto moveTo(
			Long entitatId,
			Long reglaId,
			int posicio) throws NotFoundException {
		logger.debug("Movent la regla a la posició especificada ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ", "
				+ "posicio=" + posicio + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		canviPosicio(
				regla,
				posicio);
		return toReglaDto(regla);
	}
	
	@Override
	@Transactional
	public List<String> aplicarManualment(
			Long entitatId,
			Long reglaId) {
		logger.debug("Aplicant la regla manualment ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		
		List<String> numerosRegistres = new ArrayList<>();

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		
		List<String> codisProcediments;
		if(regla.getProcedimentCodiFiltre() != null && !regla.getProcedimentCodiFiltre().trim().isEmpty()) {
			codisProcediments = Arrays.asList(regla.getProcedimentCodiFiltre().split(" "));
		} else {
			codisProcediments = new ArrayList<>();
			codisProcediments.add("-");
		}
		
		List<Long> bustiesUnitatOrganitzativaIds = new ArrayList<>();
		if (regla.getUnitatOrganitzativaFiltre() != null) {			
			for (BustiaEntity bustia : bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNotNull(entitat, regla.getUnitatOrganitzativaFiltre())) {
				bustiesUnitatOrganitzativaIds.add(bustia.getId());
			}
		}
		if (bustiesUnitatOrganitzativaIds.isEmpty()) {
			bustiesUnitatOrganitzativaIds.add(0L);
		}
		
		Boolean registrePresencial = null;
		if (regla.getPresencial() != null) {
			registrePresencial = ReglaPresencialEnumDto.SI.equals(regla.getPresencial());
		}
		
		for(RegistreEntity registre : reglaRepository.findRegistres(
				entitat, 
				regla.getUnitatOrganitzativaFiltre() == null, 
				bustiesUnitatOrganitzativaIds,
				registrePresencial == null, 
				registrePresencial != null ? registrePresencial.booleanValue() : false,
				regla.getBustiaFiltre() == null, 
				regla.getBustiaFiltre() != null ? regla.getBustiaFiltre().getId() : 0L,
				regla.getProcedimentCodiFiltre() == null || regla.getProcedimentCodiFiltre().trim().isEmpty(), 
				codisProcediments, 
				regla.getAssumpteCodiFiltre() == null || regla.getAssumpteCodiFiltre().trim().isEmpty(), 
				regla.getAssumpteCodiFiltre() != null && !regla.getAssumpteCodiFiltre().trim().isEmpty() ?
						regla.getAssumpteCodiFiltre() : "-")) {
			
			// S'assigna la regla per a que es processi en segon pla
			registre.updateRegla(regla);

			numerosRegistres.add( registre.getNumero());
			logger.debug("Regla " + regla.getId() + " \"" + regla.getNom() + "\" aplicada manualment a l'anotació " + registre.getNumero());
		}

		return numerosRegistres;
	}

	@Override
	@Transactional(readOnly = true)
	public ReglaDto findOne(
			Long entitatId,
			Long reglaId) {
		logger.debug("Cercant la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		return toReglaDto(regla);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ReglaDto> findAmbEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de regles amb paginació ("
				+ "entitatId=" + entitatId + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return paginacioHelper.toPaginaDto(
				reglaRepository.findByEntitatAndFiltrePaginat(
						entitat,
						paginacioParams.getFiltre() == null,
						paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ReglaDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ReglaDto> findAmbFiltrePaginat(
			Long entitatId,
			ReglaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Cercant les regles segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());
		
		BustiaEntity bustia = filtre.getBustiaId() == null ? null : bustiaRepository.findOne(filtre.getBustiaId());

		BackofficeEntity backoffice = filtre.getBackofficeId() == null ? null : backofficeRepository.findOne(filtre.getBackofficeId());
		
		boolean totes = false;
		boolean activa = false;
		if (filtre.getActiva() == null) {
			totes = true;
		}else if (filtre.getActiva().equals(ReglaFiltreActivaEnumDto.ACTIVES)) {
			activa = true;
		}

		PaginaDto<ReglaDto> resultPagina =  paginacioHelper.toPaginaDto(
				reglaRepository.findByFiltrePaginat(
						entitat,
						filtre.getUnitatId() == null, 
						unitat,
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom() != null ? filtre.getNom() : "",
						filtre.getCodiAssumpte() == null, 
						filtre.getCodiAssumpte() != null ? filtre.getCodiAssumpte() : "", 
						filtre.getCodiSIA() == null || filtre.getCodiSIA().isEmpty(), 
						filtre.getCodiSIA() != null ? filtre.getCodiSIA() : "",
						filtre.getTipus() == null , 
						filtre.getTipus(),
						filtre.getPresencial() == null,
						filtre.getPresencial(),
						bustia == null, 
						bustia, 
						backoffice == null ,
						backoffice,
//						filtre.isActiva(),
						totes,
						activa, 
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ReglaDto.class);
		
		return resultPagina;
	}	
	
	/**
	 * Consulta les regles per codi de procediment.
	 * @return Map<codiProcediment, List<ReglasExistents>>
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, List<ReglaDto>> findReglesByCodiProcediment(List<String> procediments) {
		
		Map<String, List<ReglaDto>> result = new HashMap<String, List<ReglaDto>>();
		for (String procediment : procediments) {
			List<ReglaEntity> reglasExistents = reglaRepository.findReglaBackofficeByCodiProcediment(procediment);
			for (ReglaEntity regla : reglasExistents) {
				if (regla.getProcedimentCodiFiltre() != null) {
					List<String> procedimentsExistents = Arrays.asList(regla.getProcedimentCodiFiltre().split(" "));
					if (procedimentsExistents.contains(procediment)) {
						if (!result.containsKey(procediment)) {
							result.put(procediment, new ArrayList<ReglaDto>());
						}
						result.get(procediment).add(conversioTipusHelper.convertir(
								regla,
								ReglaDto.class));	
					}
				}
			}
		}
		return result;
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ReglaDto> findByEntitatAndUnitatCodi(
			Long entitatId, 
			String unitatCodi) {
		logger.debug("Cercant las regles de la unitat (" 
				+ "entitatId=" + entitatId 
				+ ", " + "unitatCodi=" + unitatCodi
				+ ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);

		List<ReglaEntity> regles = reglaRepository.findByEntitatAndUnitatOrganitzativaFiltreCodi(entitat, unitatCodi);
		List<ReglaDto> resposta = new ArrayList<ReglaDto>();
		for (ReglaEntity regla : regles) {
			resposta.add(toReglaDto(regla));
		}
		return resposta;
	}


	
	@Override
	@Transactional(readOnly = true)
	public List<RegistreSimulatAccionDto> simularReglaAplicacio(
			RegistreSimulatDto registreSimulatDto) {

		logger.debug("Simulant regla aplicacio ("
				+ "unitatId=" + registreSimulatDto.getUnitatId() + ", "
				+ "bustiaId=" + registreSimulatDto.getBustiaId() + ", "
				+ "codiProcedmient=" + registreSimulatDto.getProcedimentCodi() + ", "
				+ "presencial=" + registreSimulatDto.getPresencial() + ", "
				+ "codiAssumpte=" + registreSimulatDto.getAssumpteCodi() + ")");
		
		
		List<RegistreSimulatAccionDto> simulatAccions = new ArrayList<>();
		
		UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findOne(
				registreSimulatDto.getUnitatId());
		
		EntitatEntity entitatEntity = entitatRepository.findByCodiDir3(unitatOrganitzativaEntity.getCodiDir3Entitat());
		
		BustiaEntity bustiaDesti = null;
		if (registreSimulatDto.getBustiaId() == null) {
			bustiaDesti = bustiaHelper.findBustiaDesti(
					entitatEntity,
					unitatOrganitzativaEntity.getCodi());
			simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.BUSTIA_PER_DEFECTE, bustiaDesti.getNom(), null));

		} else { 
			bustiaDesti = bustiaRepository.findOne(registreSimulatDto.getBustiaId());
		}
		Boolean presencial = null;
		if (registreSimulatDto.getPresencial() != null) {
			presencial = registreSimulatDto.getPresencial().equals(ReglaPresencialEnumDto.SI) ? true : false;
		}
		ReglaEntity reglaAplicable = reglaHelper.findAplicable(
				entitatEntity,
				unitatOrganitzativaEntity.getId(),
				bustiaDesti.getId(),
				registreSimulatDto.getProcedimentCodi(),
				registreSimulatDto.getAssumpteCodi(), 
				presencial);
	
		registreSimulatDto.setBustiaId(bustiaDesti.getId());
		
		if (reglaAplicable != null) {
			reglaHelper.aplicarSimulation(
					entitatEntity,
					registreSimulatDto,
					reglaAplicable,
					new ArrayList<ReglaEntity>(),
					simulatAccions, 
					presencial);
		}

		
		

		
		
		return simulatAccions;

	}
	
	
	private void canviPosicio(
			ReglaEntity regla,
			int posicio) {
		List<ReglaEntity> regles = reglaRepository.findByEntitatOrderByOrdreAsc(
				regla.getEntitat());
		
		if (posicio != regles.indexOf(regla)) {
			regles.remove(regla);
			regles.add(posicio, regla);
			int i = 0;
			for (ReglaEntity r : regles) {
				r.updateOrdre(i++);
			}
		}

	}

	private ReglaDto toReglaDto(ReglaEntity regla) {
		ReglaDto dto = conversioTipusHelper.convertir(
				regla,
				ReglaDto.class);
		if (regla.getBustiaDesti() != null)
			dto.setBustiaDestiId(regla.getBustiaDesti().getId());
		
		UnitatOrganitzativaEntity unitatFiltreEntity = regla.getUnitatOrganitzativaFiltre();
		if (unitatFiltreEntity != null) {
			UnitatOrganitzativaDto unitatFiltreDto = conversioTipusHelper.convertir(
					unitatFiltreEntity,
					UnitatOrganitzativaDto.class);
			unitatFiltreDto = UnitatOrganitzativaHelper.assignAltresUnitatsFusionades(unitatFiltreEntity, unitatFiltreDto);
			dto.setUnitatOrganitzativaFiltre(unitatFiltreDto);
		}
		
		UnitatOrganitzativaEntity unitatDesti = regla.getUnitatDesti();
		if (unitatDesti != null) {
			dto.setUnitatDestiId(unitatDesti.getId());
		}
		
		BustiaEntity busitaFiltre = regla.getBustiaFiltre();
		if (busitaFiltre != null) {
			dto.setBustiaFiltreId(busitaFiltre.getId());
		}
	
		
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	public List<ReglaDto> findReglaBackofficeByProcediment (String procedimentCodi) {
		List<ReglaEntity> reglesPerSia = reglaRepository.findReglaBackofficeByCodiProcediment(procedimentCodi);
		return conversioTipusHelper.convertirList(reglesPerSia, ReglaDto.class);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<ReglaDto> findReglaByProcediment (String procedimentCodi) {
		List<ReglaEntity> reglesPerSia = reglaRepository.findReglaByCodiProcediment(procedimentCodi);
		return conversioTipusHelper.convertirList(reglesPerSia, ReglaDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(ReglaServiceImpl.class);

	
	
	



}
