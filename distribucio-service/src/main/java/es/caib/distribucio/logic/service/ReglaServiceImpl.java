/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.BustiaHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.ReglaHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper.SubsistemesEnum;
import es.caib.distribucio.logic.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.ReglaFiltreActivaEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaFiltreDto;
import es.caib.distribucio.logic.intf.dto.ReglaGestioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.service.ReglaService;
import es.caib.distribucio.persist.entity.BackofficeEntity;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.BackofficeRepository;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.persist.repository.ReglaRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;

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
    @Autowired
    private IntegracioHelper integracioHelper;

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
				reglaDto.getServeiCodiFiltre(),
				reglaDto.getUnitatOrganitzativaFiltre() != null ? unitatOrganitzativaRepository.findById(reglaDto.getUnitatOrganitzativaFiltre().getId()).orElse(null) : null,
				reglaDto.getBustiaFiltreId() != null ? bustiaRepository.findById(reglaDto.getBustiaFiltreId()).orElse(null) : null,
				ordre).
				descripcio(reglaDto.getDescripcio()).
				build();
		switch(reglaDto.getTipus()) {
		case BACKOFFICE:
			BackofficeEntity backofficeEntity = backofficeRepository.findById(reglaDto.getBackofficeDestiId()).orElse(null);
			reglaEntity.updatePerTipusBackoffice(backofficeEntity);
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
			UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findById(reglaDto.getUnitatDestiId()).orElse(null);
			reglaEntity.updatePerTipusUnitat(unitatOrganitzativaEntity);
			break;
		}
		reglaEntity.setAturarAvaluacio(reglaDto.isAturarAvaluacio());
		ReglaDto novaReglaDto = toReglaDto(reglaRepository.save(reglaEntity));
		this.monitoritzarRegla(
				ReglaGestioTipusEnumDto.Alta,
				entitat.getNom(),
				reglaDto.getProcedimentCodiFiltre(),
				reglaDto.getBackofficeDestiNom());				
		return novaReglaDto;
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
				false,
				false);
		ReglaEntity reglaEntity = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaDto.getId());
		BackofficeEntity backofficeDestiId;
		if (reglaDto.getTipus().equals(ReglaTipusEnumDto.BUSTIA) || 
			reglaDto.getTipus().equals(ReglaTipusEnumDto.UNITAT) ) {
			backofficeDestiId = null;
		}else {
			backofficeDestiId = backofficeRepository.findById(reglaDto.getBackofficeDestiId()).orElse(null);
		}
		reglaEntity.update(
				backofficeDestiId, 
				reglaDto.getNom(),
				reglaDto.getDescripcio(),
				reglaDto.getTipus(),
				reglaDto.getPresencial(),
				reglaDto.getAssumpteCodiFiltre(),
				reglaDto.getProcedimentCodiFiltre(),
				reglaDto.getServeiCodiFiltre(),
				reglaDto.getUnitatOrganitzativaFiltre() != null ? unitatOrganitzativaRepository.findById(reglaDto.getUnitatOrganitzativaFiltre().getId()).orElse(null) : null,
				reglaDto.getBustiaFiltreId() != null ? bustiaRepository.findById(reglaDto.getBustiaFiltreId()).orElse(null) : null,
				reglaDto.isAturarAvaluacio());
		switch(reglaDto.getTipus()) {
		case BACKOFFICE:
			BackofficeEntity backofficeEntity = backofficeRepository.findById(reglaDto.getBackofficeDestiId()).orElse(null);
			reglaEntity.updatePerTipusBackoffice(backofficeEntity);
			break;
		case BUSTIA:
			BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					reglaDto.getBustiaDestiId(),
					false);
			reglaEntity.updatePerTipusBustia(bustia);
			break;
		case UNITAT:
			UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findById(reglaDto.getUnitatDestiId()).orElse(null);
			reglaEntity.updatePerTipusUnitat(unitatOrganitzativaEntity);
			break;
		}		
		ReglaDto novaReglaDto = toReglaDto(reglaRepository.save(reglaEntity));
		this.monitoritzarRegla(
				ReglaGestioTipusEnumDto.Modificacio,
				entitat.getNom(),
				reglaDto.getProcedimentCodiFiltre(),
				reglaDto.getBackofficeDestiNom());				
		return novaReglaDto;
	}

	@Override
	@Transactional
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			String sia,
			boolean activa) throws NotFoundException {
		logger.debug("Modificant propietat activa de la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ", "
				+ "activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		regla.updateActiva(activa);
		
		ReglaDto novaReglaDto = toReglaDto(reglaRepository.save(regla));
		this.monitoritzarRegla(
				activa?ReglaGestioTipusEnumDto.Activacio:ReglaGestioTipusEnumDto.Desactivacio,
				entitat.getNom(),
				sia,
				null);				
		return novaReglaDto;
	}
	
	@Override
	@Transactional
	public ReglaDto updateActivaPresencial(Long entitatId, Long reglaId, boolean activa, ReglaPresencialEnumDto presencial, String sia)
			throws NotFoundException {
			logger.debug("Modificant propietats activa i presencial de la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ", "
				+ "activa=" + activa + ")");		
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					false,
					false);
			ReglaEntity regla = entityComprovarHelper.comprovarRegla(
					entitat,
					reglaId);
			regla.updateActiva(activa);
			regla.updatePresencial(presencial);
			
			ReglaDto novaReglaDto = toReglaDto(reglaRepository.save(regla));
			this.monitoritzarRegla(
					ReglaGestioTipusEnumDto.Modificacio,
					entitat.getNom(),
					sia,
					null);				
			return novaReglaDto;
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
		// Actualitza l'ordre de les regles
		List<ReglaEntity> regles = reglaRepository.findByEntitatOrderByOrdreAsc(regla.getEntitat());
		int i = 0;
		for (ReglaEntity r : regles) {
			if (regla.getId() != r.getId()) {
				r.updateOrdre(i++);
			}
		}
		// Comprova que no hi hagi registres pendents de processar aquesta regla
		List<String> registresPendents = new ArrayList<String>();
		for (RegistreEntity registre : registreRepository.findByRegla(regla)) {
			if (RegistreProcesEstatEnum.REGLA_PENDENT.equals(registre.getProcesEstat())) {
				registresPendents.add(registre.getNumero());
			} else {
				registre.removeRegla();
			}
		}
		if (!registresPendents.isEmpty()) {
			String missatgeError = "No es pot esborrar la regla perquè hi ha " + registresPendents.size() + 
					" registres pendents de processar-la: " + registresPendents;
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

		List<String> codisServei = new ArrayList<>();
		if(regla.getServeiCodiFiltre() != null && !regla.getServeiCodiFiltre().trim().isEmpty()) {
			codisServei.addAll(Arrays.asList(regla.getServeiCodiFiltre().split(" ")));
		} else {
			codisServei.add("-");
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
				codisProcediments,
                codisServei,
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
		
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findById(filtre.getUnitatId()).orElse(null);
		UnitatOrganitzativaEntity unitatDesti = filtre.getUnitatDestiId() == null ? null : unitatOrganitzativaRepository.findById(filtre.getUnitatDestiId()).orElse(null);

		BustiaEntity bustia = filtre.getBustiaId() == null ? null : bustiaRepository.findById(filtre.getBustiaId()).orElse(null);
		BustiaEntity bustiaDesti = filtre.getBustiaDestiId() == null ? null : bustiaRepository.findById(filtre.getBustiaDestiId()).orElse(null);

		BackofficeEntity backoffice = filtre.getBackofficeId() == null ? null : backofficeRepository.findById(filtre.getBackofficeId()).orElse(null);
		
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
						filtre.getCodiServei() == null || filtre.getCodiServei().isEmpty(), 
						filtre.getCodiServei() != null ? filtre.getCodiServei() : "",
						filtre.getTipus() == null , 
						filtre.getTipus(),
						filtre.getPresencial() == null,
						filtre.getPresencial(),
						bustia == null, 
						bustia,
                        !ReglaTipusEnumDto.BACKOFFICE.equals(filtre.getTipus()) || backoffice == null,
						backoffice,
                        !ReglaTipusEnumDto.UNITAT.equals(filtre.getTipus()) || filtre.getUnitatDestiId() == null,
                        unitatDesti,
                        !ReglaTipusEnumDto.BUSTIA.equals(filtre.getTipus()) || filtre.getBustiaDestiId() == null,
                        bustiaDesti,
//						filtre.isActiva(),
						totes,
						activa, 
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ReglaDto.class);
		
		return resultPagina;
	}	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Long> findReglaIds(Long entitatId,
			ReglaFiltreDto filtre) {
		logger.debug("Cercant les regles segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findById(filtre.getUnitatId()).orElse(null);
		
		BustiaEntity bustia = filtre.getBustiaId() == null ? null : bustiaRepository.findById(filtre.getBustiaId()).orElse(null);

		BackofficeEntity backoffice = filtre.getBackofficeId() == null ? null : backofficeRepository.findById(filtre.getBackofficeId()).orElse(null);
		
		boolean totes = false;
		boolean activa = false;
		if (filtre.getActiva() == null) {
			totes = true;
		}else if (filtre.getActiva().equals(ReglaFiltreActivaEnumDto.ACTIVES)) {
			activa = true;
		}

		List<Long> ids = reglaRepository.findIdsByFiltre(
						entitat,
						filtre.getUnitatId() == null, 
						unitat,
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom() != null ? filtre.getNom() : "",
						filtre.getCodiAssumpte() == null, 
						filtre.getCodiAssumpte() != null ? filtre.getCodiAssumpte() : "", 
						filtre.getCodiSIA() == null || filtre.getCodiSIA().isEmpty(), 
						filtre.getCodiSIA() != null ? filtre.getCodiSIA() : "",
						filtre.getCodiServei() == null || filtre.getCodiServei().isEmpty(), 
						filtre.getCodiServei() != null ? filtre.getCodiServei() : "",
						filtre.getTipus() == null , 
						filtre.getTipus(),
						filtre.getPresencial() == null,
						filtre.getPresencial(),
						bustia == null, 
						bustia, 
						backoffice == null ,
						backoffice,
						totes,
						activa);
		
		return ids;
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
	public List<ReglaDto> findByEntitatAndUnitatFiltreCodi(
			Long entitatId, 
			String unitatCodi) {
		logger.debug("Cercant las regles de la unitat  filtre (" 
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
	public List<ReglaDto> findByEntitatAndUnitatDestiCodi(
			Long entitatId, 
			String unitatCodi) {
		logger.debug("Cercant las regles de la unitat destí (" 
				+ "entitatId=" + entitatId 
				+ ", " + "unitatCodi=" + unitatCodi
				+ ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);

		List<ReglaEntity> regles = reglaRepository.findByEntitatAndUnitatDestiCodi(entitat, unitatCodi);
		List<ReglaDto> resposta = new ArrayList<ReglaDto>();
		for (ReglaEntity regla : regles) {
			resposta.add(toReglaDto(regla));
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReglaDto> findByEntitatAndBackofficeDestiId(
			Long entitatId, 
			Long backofficeDestiId) {
		logger.debug("Cercant las regles del backoffice destí (" 
				+ "entitatId=" + entitatId 
				+ ", " + "unitatCodi=" + backofficeDestiId
				+ ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);

		List<ReglaEntity> regles = reglaRepository.findByEntitatAndBackofficeDestiId(entitat, backofficeDestiId);
		return conversioTipusHelper.convertirList(
				regles, 
				ReglaDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<RegistreSimulatAccionDto> simularReglaAplicacio(
			RegistreSimulatDto registreSimulatDto) {

		logger.debug("Simulant regla aplicacio ("
				+ "unitatId=" + registreSimulatDto.getUnitatId() + ", "
				+ "bustiaId=" + registreSimulatDto.getBustiaId() + ", "
				+ "codiProcedmient=" + registreSimulatDto.getProcedimentCodi() + ", "
				+ "codiServei=" + registreSimulatDto.getServeiCodi() + ", "
				+ "presencial=" + registreSimulatDto.getPresencial() + ", "
				+ "codiAssumpte=" + registreSimulatDto.getAssumpteCodi() + ")");
		
		
		List<RegistreSimulatAccionDto> simulatAccions = new ArrayList<>();
		
		UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.getReferenceById(
				registreSimulatDto.getUnitatId());
		
		EntitatEntity entitatEntity = entitatRepository.findByCodiDir3(unitatOrganitzativaEntity.getCodiDir3Entitat());
		
		BustiaEntity bustiaDesti = null;
		if (registreSimulatDto.getBustiaId() == null) {
			bustiaDesti = bustiaHelper.findBustiaDesti(
					entitatEntity,
					unitatOrganitzativaEntity.getCodi());
			simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.BUSTIA_PER_DEFECTE, bustiaDesti.getNom(), null));

		} else { 
			bustiaDesti = bustiaRepository.findById(registreSimulatDto.getBustiaId()).orElse(null);
		}
		Boolean presencial = null;
		if (registreSimulatDto.getPresencial() != null) {
			presencial = registreSimulatDto.getPresencial().equals(ReglaPresencialEnumDto.SI) ? true : false;
		}
		registreSimulatDto.setUnitatId(unitatOrganitzativaEntity.getId());
		registreSimulatDto.setBustiaId(bustiaDesti.getId());
		
		reglaHelper.aplicarSimulation(
				entitatEntity,
				registreSimulatDto,
				new ArrayList<ReglaEntity>(),
				simulatAccions, 
				presencial);
		
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

        if (regla.getCreatedBy().isPresent())  {
            dto.setCreatedBy(conversioTipusHelper.convertir(regla.getCreatedBy().get(), UsuariDto.class));
        }

        if (regla.getLastModifiedBy().isPresent())  {
            dto.setLastModifiedBy(conversioTipusHelper.convertir(regla.getLastModifiedBy().get(), UsuariDto.class));
        }
        
		regla.getCreatedDate()
				.ifPresent(ldt -> 
					dto.setCreatedDate(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())));

		regla.getLastModifiedDate()
				.ifPresent(ldt -> 
					dto.setLastModifiedDate(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())));
		
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	public List<ReglaDto> findReglaBackofficeByProcediment (String procedimentCodi) {
		List<ReglaEntity> reglesPerSia = reglaRepository.findReglaBackofficeByCodiProcediment(procedimentCodi);
		this.monitoritzarRegla(
				ReglaGestioTipusEnumDto.Consulta,
				null,
				procedimentCodi,
				null);				
		return conversioTipusHelper.convertirList(reglesPerSia, ReglaDto.class);
	}

	@Transactional(readOnly = true)
	public List<ReglaDto> findReglaBackofficeByServei (String serveiCodi) {
		List<ReglaEntity> reglesPerSia = reglaRepository.findReglaBackofficeByCodiServei(serveiCodi);
		this.monitoritzarRegla(
				ReglaGestioTipusEnumDto.Consulta,
				null,
				serveiCodi,
				null);
		return conversioTipusHelper.convertirList(reglesPerSia, ReglaDto.class);
	}

	@Transactional(readOnly = true)
	public List<ReglaDto> findReglaBackofficeByCodiSia (String siaCodi) {
		List<ReglaEntity> reglesPerSia = reglaRepository.findReglaBackofficeByCodiSia(siaCodi);
		this.monitoritzarRegla(
				ReglaGestioTipusEnumDto.Consulta,
				null,
				siaCodi,
				null);
		return conversioTipusHelper.convertirList(reglesPerSia, ReglaDto.class);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<ReglaDto> findReglaByProcediment (String procedimentCodi) {
		List<ReglaEntity> reglesPerSia = reglaRepository.findReglaByCodiProcediment(procedimentCodi);
		return conversioTipusHelper.convertirList(reglesPerSia, ReglaDto.class);
	}
	
//  @Override
	public void monitoritzarRegla(
			ReglaGestioTipusEnumDto reglaGestioTipusEnumDto,
			String entitat,
			String sia,
			String backoffice
			) {
		long start = System.currentTimeMillis();
		String accioDescripcio = reglaGestioTipusEnumDto.name() + " de la regla amb codi SIA " + sia;
		String usuariIntegracio = this.getUsuariIntegracio();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("Accio sobre la regla", reglaGestioTipusEnumDto.name());
		accioParams.put("Codi SIA", sia);
		accioParams.put("Backoffice", backoffice);		
      
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth!=null) {
			String usuariCodi = auth.getName();
			accioParams.put("Usuari", usuariCodi);
		}
		long t0 = System.currentTimeMillis();
		try {
			logger.trace(">>> Abans de cridar el servei de la gestió de la regla");
			integracioHelper.addAccioOk (
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0
			);
			SubsistemesHelper.addSuccessOperation(SubsistemesEnum.BKE, System.currentTimeMillis() - start);
			logger.trace(">>> Despres de cridar el servei de la gestió de la regla");
		} catch (Exception ex) {
			logger.error("Error al gestionar la regla en el servei web de backoffice integració (" + "sia="
					+ sia + ex);
			String errorDescripcio = "Error al gestionar la regla en el servei web de backoffice integració";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			SubsistemesHelper.addErrorOperation(SubsistemesEnum.BKE);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_BACKOFFICE,
					errorDescripcio,
					ex);
		}
		logger.debug("");
	}
  
	private String getUsuariIntegracio() {
		String usuari = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			usuari = auth.getName();
		}
		return usuari;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReglaServiceImpl.class);



}
