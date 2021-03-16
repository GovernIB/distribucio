/**
 * 
 */
package es.caib.distribucio.core.service;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.ArbreNodeDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PermisDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariPermisDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutComentariEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ContingutLogHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.MessageHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper.Converter;
import es.caib.distribucio.core.helper.PermisosHelper;
import es.caib.distribucio.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.ContingutComentariRepository;
import es.caib.distribucio.core.repository.ContingutMovimentEmailRepository;
import es.caib.distribucio.core.repository.ContingutMovimentRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.ReglaRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
import es.caib.distribucio.plugin.usuari.DadesUsuari;



/**
 * Implementació dels mètodes per a gestionar bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class BustiaServiceImpl implements BustiaService {

	@Autowired
	private BustiaRepository bustiaRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private ReglaRepository reglaRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private BustiaHelper bustiaHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private ReglaHelper reglaHelper;
	@Autowired
	private RegistreHelper registreHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private MessageHelper messageHelper;

	@Autowired
	private RegistreService registreService;
	@Autowired
	private JavaMailSender mailSender;	
	
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private ContingutComentariRepository contingutComentariRepository;
	@Autowired
	private ContingutMovimentRepository contingutMovimentRepository;

	@Override
	@Transactional
	public BustiaDto create(
			Long entitatId,
			BustiaDto bustia) {
		logger.debug("Creant una nova bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustia=" + bustia + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		UnitatOrganitzativaEntity unitat = unitatOrganitzativaRepository.findOne(bustia.getUnitatOrganitzativa().getId());
		if (unitat == null) {
			logger.error("No s'ha trobat la unitat administrativa (codi=" + bustia.getUnitatCodi() + ")");
			throw new NotFoundException(
					bustia.getUnitatOrganitzativa().getId(),
					UnitatOrganitzativaEntity.class);
		}
		// Cerca la bústia superior
		BustiaEntity bustiaPare = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNull(
				entitat,
				unitat);
		// Si la bústia superior no existeix la crea
		if (bustiaPare == null) {
			bustiaPare = bustiaRepository.save(
					BustiaEntity.getBuilder(
							entitat,
							unitat.getDenominacio(),
							unitat.getCodi(),
							unitat,
							null).build());
		}
		// Crea la nova bústia
		BustiaEntity entity = BustiaEntity.getBuilder(
				entitat,
				bustia.getNom(),
				unitat.getCodi(),
				unitat,
				bustiaPare).build();
		bustiaRepository.save(entity);
		// Registra al log la creació de la bústia
		contingutLogHelper.logCreacio(
				entity,
				false);
		// Si no hi ha cap bústia per defecte a dins l'unitat configura
		// la bústia actual com a bústia per defecte
		BustiaEntity bustiaPerDefecte = bustiaHelper.findBustiaPerDefecte(entitat, unitat.getCodi());
		
		if (bustiaPerDefecte == null) {
			entity.updatePerDefecte(true);
			
			List<String> params = new ArrayList<>();
			params.add("true");
			params.add(null);
			
			contingutLogHelper.log(
					entity,
					LogTipusEnumDto.PER_DEFECTE,
					params,
					false);
		}
		return bustiaHelper.toBustiaDto(
				entity,
				false,
				false,
				true);
	}

	@Override
	@Transactional
	public BustiaDto update(
			Long entitatId,
			BustiaDto bustiaModifications) {
		logger.debug("Modificant la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustia=" + bustiaModifications + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity bustiaOriginal = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaModifications.getId(),
				false);
		
		String nomOriginalBustia = bustiaOriginal.getNom();
		UnitatOrganitzativaEntity unitatOrganitzativaDesti = unitatOrganitzativaRepository.findOne(bustiaModifications.getUnitatOrganitzativa().getId());
		boolean bustiaPerDefecteInUnitatDesti = bustiaHelper.findBustiaPerDefecte(entitat, unitatOrganitzativaDesti.getCodi()) != null;
		boolean unitatChanged = !bustiaOriginal.getUnitatOrganitzativa().getId().equals(unitatOrganitzativaDesti.getId());

		if (unitatChanged && bustiaOriginal.isPerDefecte()) {
			
			UnitatOrganitzativaEntity unitatOrganitzativaSource = bustiaOriginal.getUnitatOrganitzativa();
			boolean unitatSourceObsoleta = unitatOrganitzativaSource.getEstat().equals("E") || unitatOrganitzativaSource.getEstat().equals("A") || unitatOrganitzativaSource.getEstat().equals("T");
			if (!unitatSourceObsoleta) {
				// Comprova que si es mou la bústia per defecte a una altra unitat encara quedi una bústia per defecte per a la unitat organitzativa anterior en path
				BustiaEntity alternativaBustiaPerDefecteInPath = this.findBustiaPerDefecteAlternativa(entitat, bustiaOriginal); 
				if (alternativaBustiaPerDefecteInPath == null) {
					String missatgeError = "No es pot moure la bústia per defecte si no n'hi ha cap altra superior definida per defecte (" +
							"bustiaId=" + bustiaModifications.getId() + ", " +
							"unitatOrganitzativaCodi=" + bustiaOriginal.getUnitatOrganitzativa().getCodi() + ")";
					logger.error(missatgeError);
					throw new ValidationException(
							bustiaModifications.getId() ,
							BustiaEntity.class,
							missatgeError);
				}		
			}
			
			// comprova si a la unitat orgánica destí ja hi ha alguna bústia per defecte, si ja hi ha desmarca la que estem movent
			if (bustiaPerDefecteInUnitatDesti) {
				bustiaOriginal.updatePerDefecte(false);
			}
		
		// Si a la bústia destí no n'hi ha cap per defecte llavors la marca com a defecte
		} else if (!bustiaPerDefecteInUnitatDesti) {
			bustiaOriginal.updatePerDefecte(true);
		}
		// Actualitza la bústia
		bustiaOriginal.update(
				bustiaModifications.getNom(),
				unitatOrganitzativaDesti);
		
		// Registra al log la modificació de la bústia
		List<String> params = new ArrayList<>();
		params.add((!nomOriginalBustia.equals(bustiaOriginal.getNom())) ? bustiaOriginal.getNom() : null);
		params.add(null);
		contingutLogHelper.log(
				bustiaOriginal,
				LogTipusEnumDto.MODIFICACIO,
				params,
				false);
		return bustiaHelper.toBustiaDto(
				bustiaOriginal,
				false,
				false,
				true);
	}
	
	
	@Override
	public List<UsuariPermisDto> getUsersPermittedForBustia(Long bustiaId){
		BustiaEntity bustiaEntity =  bustiaRepository.findOne(bustiaId);
		return contingutHelper.findUsuarisAmbPermisReadPerContenidor(bustiaEntity);
	}
			
	

	/** Mètode per trobar una bústia per defecte alternativa a la bústia pasada com a paràmetre.
	 *  Aquest mètode s'utilitza per validar a l'hora d'esborrar o moure una bústia en una unitat
	 *  administratvia.
	 * @param entitat
	 * @param bustia
	 * @return
	 */
	private BustiaEntity findBustiaPerDefecteAlternativa(
			EntitatEntity entitat, 
			BustiaEntity bustia) 
	{
		BustiaEntity bustiaPerDefecteAlternativa = null;
		List<UnitatOrganitzativaDto> path = unitatOrganitzativaHelper.findPath(
				entitat.getCodiDir3(),
				bustia.getUnitatOrganitzativa().getCodi());
		if (path != null && !path.isEmpty()) {
			BustiaEntity bustiaAux;
			for (UnitatOrganitzativaDto unitat: path) {
				bustiaAux = bustiaHelper.findBustiaPerDefecte(
						entitat,
						unitat.getCodi());
				if (bustiaAux != null && ! bustiaAux.getId().equals(bustia.getId())) {
					bustiaPerDefecteAlternativa = bustiaAux;
					break;
				}
			}
		}
		return bustiaPerDefecteAlternativa;
	}

	@Override
	@Transactional
	public BustiaDto updateActiva(
			Long entitatId,
			Long id,
			boolean activa) {
		logger.debug("Modificant propietat activa de la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity entity = entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		entity.updateActiva(activa);
		// Registra al log la modificació de la bústia
		

		contingutLogHelper.logMoviment(
				entity,
				activa ? LogTipusEnumDto.ACTIVACIO : LogTipusEnumDto.DESACTIVACIO,
				null,
				false);
		return bustiaHelper.toBustiaDto(
				entity,
				false,
				false,
				true);
	}

	@Override
	@Transactional
	public BustiaDto delete(
			Long entitatId,
			Long id) {
		logger.debug("Esborrant la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		if (bustia.isPerDefecte()) {
			// Valida que si s'esborra encara hi hagi una altra per defecte d'alternativa
			BustiaEntity bustiaPerDefecteAlternativa = this.findBustiaPerDefecteAlternativa(entitat, bustia);
			if (bustiaPerDefecteAlternativa == null) {
				String missatgeError = "No es pot esborrar la bústia per defecte si no n'hi ha cap altra superior definida per defecte (" +
						"bustiaId=" + id + ", " +
						"unitatOrganitzativaCodi=" + bustia.getUnitatOrganitzativa().getCodi() + ")";
				logger.error(missatgeError);
				throw new ValidationException(
						id,
						BustiaEntity.class,
						missatgeError);
			}			
		}
		
		// cannot remove busties containing any anotacions
		if (contingutRepository.findByPare(bustia) != null && !contingutRepository.findByPare(bustia).isEmpty()) {
			String missatgeError = "No es pot esborrar la bústia amb anotacions a dins (" + 
					"bustiaId=" + id + ")";
			logger.error(missatgeError);
			throw new ValidationException(
					id,
					BustiaEntity.class,
					missatgeError);
		}
		
		
		// cannot remove busties with regles
		if ((reglaRepository.findByBustiaDesti(bustia) != null && !reglaRepository.findByBustiaDesti(bustia).isEmpty()) || (reglaRepository.findByBustiaFiltre(bustia) != null && !reglaRepository.findByBustiaFiltre(bustia).isEmpty())) {
			String missatgeError = messageHelper.getMessage("bustia.service.esborrat.error.regles", new Object[] {id});
			logger.error(missatgeError);
			throw new ValidationException(
					id,
					BustiaEntity.class,
					missatgeError);
		}
		
		
		bustiaRepository.delete(bustia);
		return bustiaHelper.toBustiaDto(
				bustia,
				false,
				false,
				true);
	}

	@Override
	@Transactional
	public BustiaDto marcarPerDefecte(
			Long entitatId,
			Long id) {
		logger.debug("Marcant la bústia com a bústia per defecte("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		List<BustiaEntity> bustiesMateixaUnitat = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNotNull(
				entitat,
				bustia.getUnitatOrganitzativa());
		for (BustiaEntity bu: bustiesMateixaUnitat) {
			if (bu.isPerDefecte()) {
				// Registra al log la modificació de la bústia
				List<String> params = new ArrayList<>();
				params.add("false");
				params.add(null);
				contingutLogHelper.log(
						bu,
						LogTipusEnumDto.PER_DEFECTE,
						params,
						false);
			}
			bu.updatePerDefecte(false);
		}
		// Registra al log la modificació de la bústia
		List<String> params = new ArrayList<>();
		params.add("true");
		params.add(null);
		contingutLogHelper.log(
				bustia,
				LogTipusEnumDto.PER_DEFECTE,
				params,
				false);
		bustia.updatePerDefecte(true);
		return bustiaHelper.toBustiaDto(
				bustia,
				false,
				false,
				true);
	}

	@Override
	@Transactional(readOnly = true)
	public BustiaDto findById(
			Long entitatId,
			Long id) {
		logger.debug("Cercant la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		BustiaDto resposta = bustiaHelper.toBustiaDto(
				bustia,
				false,
				false,
				true);
		// Ompl els permisos
		List<BustiaDto> llista = new ArrayList<BustiaDto>();
		llista.add(resposta);
		omplirPermisosPerBusties(llista, true);
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findAmbUnitatCodiAdmin(
			Long entitatId,
			String unitatCodi) {
		logger.debug("Cercant les bústies de la unitat per admins ("
				+ "entitatId=" + entitatId + ", "
				+ "unitatCodi=" + unitatCodi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodi(unitatCodi);
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNotNull(entitat, unitatOrganitzativa);
		List<BustiaDto> resposta = bustiaHelper.toBustiaDto(
				busties,
				false,
				false,
				true);
		omplirPermisosPerBusties(resposta, false);
		return resposta;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findAmbUnitatId(
			Long entitatId,
			Long unitatId) {
		logger.debug("Cercant les bústies de la unitat per admins ("
				+ "entitatId=" + entitatId + ", "
				+ "unitatId=" + unitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findOne(unitatId);
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNotNull(entitat, unitatOrganitzativa);
		List<BustiaDto> resposta = bustiaHelper.toBustiaDto(
				busties,
				false,
				false,
				true);
		omplirPermisosPerBusties(resposta, false);
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<BustiaDto> findAmbFiltreAdmin(
			Long entitatId,
			BustiaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Cercant les bústies segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("unitat", new String[]{"unitatId"});
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId()==null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId()) ;
		
		// Si es filtra per unitat superior llavors es passa la llista d'identificadors possibles de l'arbre.
		List<String> codisUnitatsSuperiors = this.getCodisUnitatsSuperiors(entitat, filtre.getCodiUnitatSuperior()); 
		if (codisUnitatsSuperiors.isEmpty())
			codisUnitatsSuperiors.add("-"); // per evitar error per llista buida

		PaginaDto<BustiaDto> resultPagina =  paginacioHelper.toPaginaDto(
				bustiaRepository.findByEntitatAndUnitatAndBustiaNomAndPareNotNullFiltrePaginat(
						entitat,
						filtre.getUnitatId() == null, 
						unitat,
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom() != null ? filtre.getNom().trim() : "",
						filtre.getCodiUnitatSuperior() == null || filtre.getCodiUnitatSuperior().isEmpty(),
						codisUnitatsSuperiors,
						filtre.getUnitatObsoleta() == null || filtre.getUnitatObsoleta() == false,
						filtre.getPerDefecte() != null && filtre.getPerDefecte(),
						filtre.getActiva() != null && filtre.getActiva(),
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio)),
				BustiaDto.class,
				new Converter<BustiaEntity, BustiaDto>() {
					@Override
					public BustiaDto convert(BustiaEntity source) {
						return bustiaHelper.toBustiaDto(
								source,
								false,
								true,
								true);
					}
				});
		omplirPermisosPerBusties(resultPagina.getContingut(), true);
		return resultPagina;
	}

	/** Mètode per obtenir els codis d'unitats orgàniques de l'arbre que penja a partir de l'unitat
	 * orgànica superior per filtrar per unitat orgànica superior. 
	 * 
	 * @param entitat
	 * @param codiUnitatSuperior
	 * @return Els codis de les UO de l'arbe a partir del node amb codi igual a codiUnitatSuperior.
	 */
	private List<String> getCodisUnitatsSuperiors(EntitatEntity entitat, String codiUnitatSuperior) {
		List<String> codisUnitatsSuperiors = new ArrayList<String>();
		if (codiUnitatSuperior != null) {
			
			ArbreDto<UnitatOrganitzativaDto> arbre = this.getArbreUnitatsSuperiors(entitat, null, codiUnitatSuperior);
			// Agafa tots els identificadors
			for (UnitatOrganitzativaDto uo : arbre.toDadesList()) {
				codisUnitatsSuperiors.add(uo.getCodi());
			}			
		}
		return codisUnitatsSuperiors;
	}

	@Override
	@Transactional
	public List<BustiaDto> findActivesAmbEntitat(
			Long entitatId) {
		
		
		final Timer timerfindActivesAmbEntitat = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findActivesAmbEntitat"));
		Timer.Context contextfindActivesAmbEntitat = timerfindActivesAmbEntitat.time();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Cercant bústies actives de l'entitat ("
				+ "entitatId=" + entitatId + ", "
				+ "usuariCodi=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false, 
				false);
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNullOrderByNomAsc(entitat);
		
		final Timer timerfindActivesAmbEntitattoBustiaDto = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findActivesAmbEntitat.toBustiaDto"));
		Timer.Context contextfindActivesAmbEntitattoBustiaDto = timerfindActivesAmbEntitattoBustiaDto.time();
		List<BustiaDto> bustiesDto = bustiaHelper.toBustiaDto(
				busties,
				false,
				false,
				false);
		contextfindActivesAmbEntitattoBustiaDto.stop();
		
		contextfindActivesAmbEntitat.stop();
		return bustiesDto;
	}

	@Override
	@Transactional
	public List<BustiaDto> findAmbEntitat(
			Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Cercant bústies de l'entitat ("
				+ "entitatId=" + entitatId + ", "
				+ "usuariCodi=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndPareNotNullOrderByNomAsc(entitat);
		return bustiaHelper.toBustiaDto(
				busties,
				false,
				false,
				true);
	}

	@Override
	@Transactional
	public List<BustiaDto> findAmbEntitatAndFiltre(
			Long entitatId, 
			BustiaFiltreOrganigramaDto filtre) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Cercant bústies de l'entitat ("
				+ "entitatId=" + entitatId + ", "
				+ "usuariCodi=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		UnitatOrganitzativaEntity unitat = filtre.getUnitatIdFiltre() != null ? unitatOrganitzativaRepository.findOne(filtre.getUnitatIdFiltre()): null;
		
		// Si es filtra per unitat superior llavors es passa la llista d'identificadors possibles de l'arbre.
		List<String> codisUnitatsSuperiors = this.getCodisUnitatsSuperiors(entitat, filtre.getCodiUnitatSuperior()); 
		if (codisUnitatsSuperiors.isEmpty())
			codisUnitatsSuperiors.add("-"); // per evitar error per llista buida
				
		final Timer timerfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre"));
		Timer.Context contextfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre = timerfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre.time();
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre(
				entitat,
				filtre.getUnitatIdFiltre() == null, 
				unitat,
				filtre.getNomFiltre() == null || filtre.getNomFiltre().isEmpty(), 
				filtre.getNomFiltre() != null ? filtre.getNomFiltre().trim() : "",
				filtre.getCodiUnitatSuperior() == null || filtre.getCodiUnitatSuperior().isEmpty(),
				codisUnitatsSuperiors,
				filtre.getUnitatObsoleta() == null || filtre.getUnitatObsoleta() == false,
				filtre.getPerDefecte() == null || filtre.getPerDefecte() == false,
				filtre.getActiva() == null || filtre.getActiva() == false);
		contextfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre.stop();
		
		final Timer timertoBustiaDto = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "toBustiaDto"));
		Timer.Context contexttoBustiaDto = timertoBustiaDto.time();
		List<BustiaDto> bustiesDto = bustiaHelper.toBustiaDto(
				busties,
				false,
				false,
				true);
		contexttoBustiaDto.stop();
		return bustiesDto;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findBusties(
			Long entitatId,
			boolean mostrarInactives) {
		
		logger.debug("Consulta de busties(" + "entitatId=" + entitatId +  ", mostrarInactives=" + mostrarInactives + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		List<BustiaEntity> busties;		
		if (mostrarInactives) {
			busties = bustiaRepository.findByEntitatAndPareNotNullOrderByNomAsc(entitat);
		} else {
			busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNullOrderByNomAsc(entitat);
		}
		
		List<BustiaDto> bustiesRetorn = bustiaHelper.toBustiaDto(
				busties,
				false,
				true,
				false);
		
		return bustiesRetorn;
	}
	
	

	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findBustiesPermesesPerUsuari(
			Long entitatId,
			boolean mostrarInactives) {
		logger.debug("Consulta de busties permeses per un usuari ("
				+ "entitatId=" + entitatId + ")");
		final Timer findPermesesPerUsuariTimer = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findPermesesPerUsuari"));
		Timer.Context findPermesesPerUsuariContext = findPermesesPerUsuariTimer.time();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Obté la llista d'id's amb permisos per a l'usuari
		List<BustiaEntity> busties;		
		if (mostrarInactives) {
			busties = bustiaRepository.findByEntitatAndPareNotNullOrderByNomAsc(entitat);
		} else {
			busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNullOrderByNomAsc(entitat);
		}
		
		final Timer findPermesesPerUsuariTimerfilterGrantedAll = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findPermesesPerUsuari.filterGrantedAll"));
		Timer.Context findPermesesPerUsuariContextfilterGrantedAll = findPermesesPerUsuariTimerfilterGrantedAll.time();
		// Filtra la llista de bústies segons els permisos
		permisosHelper.filterGrantedAll(
				busties,
				new ObjectIdentifierExtractor<BustiaEntity>() {
					@Override
					public Long getObjectIdentifier(BustiaEntity bustia) {
						return bustia.getId();
					}
				},
				BustiaEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		findPermesesPerUsuariContextfilterGrantedAll.stop();
		
		final Timer findPermesesPerUsuariTimerToBustiaDto = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findPermesesPerUsuari.ToBustiaDto"));
		Timer.Context findPermesesPerUsuariContextToBustiaDto = findPermesesPerUsuariTimerToBustiaDto.time();
		List<BustiaDto> bustiesRetorn = bustiaHelper.toBustiaDto(busties, false, true,
				false);
		findPermesesPerUsuariContextToBustiaDto.stop();
		
		findPermesesPerUsuariContext.stop();
		return bustiesRetorn;
	}


	
	
	
	private EntitatEntity validateRegistre(
			String entitatCodi,
			RegistreAnotacio anotacio){
		
		EntitatEntity entitatPerUnitat = entitatRepository.findByCodiDir3(entitatCodi);
		if (entitatPerUnitat == null) {
			throw new NotFoundException(
					entitatCodi, 
					EntitatEntity.class);
		}
		entityComprovarHelper.comprovarEntitat(
				entitatPerUnitat.getId(),
				false,
				false,
				false);
		RegistreEntity registreRepetit = registreRepository.findByEntitatCodiAndLlibreCodiAndRegistreTipusAndNumeroAndData(
				anotacio.getEntitatCodi(),
				anotacio.getLlibreCodi(),
				RegistreTipusEnum.ENTRADA.getValor(),
				anotacio.getNumero(),
				anotacio.getData());
		if (registreRepetit != null) {
			throw new ValidationException(
					"Aquesta anotació ja ha estat donada d'alta a l'aplicació (" +
					"entitatCodi=" + anotacio.getEntitatCodi() + ", " +
					"llibreCodi=" + anotacio.getLlibreCodi() + ", " +
					"tipus=" + RegistreTipusEnum.ENTRADA.getValor() + ", " +
					"numero=" + anotacio.getNumero() + ", " +
					"data=" + anotacio.getData() + ")");
		}
		
		return entitatPerUnitat;
	}
	
	
	private void moveAnotacioToBustiaPerDefecte(
			EntitatEntity entitat,
			String unitatOrganitzativa,
			RegistreEntity anotacioEntity) {
		
		// find bustia per defecte
		BustiaEntity bustia = bustiaHelper.findBustiaDesti(
				entitat,
				unitatOrganitzativa);
		// move anotacio to bustia per defecte
		ContingutMovimentEntity contingutMovimentEntity = contingutHelper.ferIEnregistrarMoviment(
				anotacioEntity,
				bustia,
				null,
				false);
		bustiaHelper.evictCountElementsPendentsBustiesUsuari(
				bustia.getEntitat(),
				bustia);
		logger.debug("Bústia per defecte de l'anotació (" +
				"entitatUnitatCodi=" + entitat.getCodiDir3() + ", " +
				"unitatOrganitzativa=" + unitatOrganitzativa + ", " +
				"anotacioNumero=" + anotacioEntity.getNumero() + ", "  +
				"bustia=" + bustia + ")");
		
		List<String> params = new ArrayList<>();
		params.add(anotacioEntity.getNom());
		params.add(null);
		
		contingutLogHelper.logAccioWithMovimentAndParams(
				anotacioEntity,
				LogTipusEnumDto.CREACIO,
				contingutMovimentEntity,
				false,
				params);
		
	}
	

	@Transactional
	@Override
	public Exception registreAnotacioCrearIProcessar(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatOrganitzativaCodi,
			RegistreAnotacio registreAnotacio) {
		
		Timer.Context context = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar")).time();
		
		logger.debug("Creant anotació provinent del servei d'enviament a bústia ("
				+ "entitatCodi=" + entitatCodi + ", "
				+ "tipus=" + tipus + ", "
				+ "unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ","
				+ "anotacio=" + registreAnotacio.getNumero() + ")");
		
		//---- validate anotacio -----
		Timer.Context contextvalidateRegistre = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.validateRegistre")).time();
		EntitatEntity entitat = validateRegistre(entitatCodi, registreAnotacio);
		contextvalidateRegistre.stop();
		
		
		//---- find estat of anotacio -----
		Timer.Context contextfindEstat = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.findEstat")).time();
		BustiaEntity bustia = bustiaHelper.findBustiaDesti(
				entitat,
				unitatOrganitzativaCodi);
		UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findPerEntitatAndCodi(
				entitat.getCodi(),
				unitatOrganitzativaCodi);
		ReglaEntity reglaAplicable = reglaHelper.findAplicable(
				entitat,
				unitat.getId(),
				bustia.getId(),
				registreAnotacio.getProcedimentCodi(),
				registreAnotacio.getAssumpteCodi());
		RegistreProcesEstatEnum estat;
		if (registreAnotacio.getAnnexos() != null && !registreAnotacio.getAnnexos().isEmpty()) {
			estat = RegistreProcesEstatEnum.ARXIU_PENDENT;
		} else if (reglaAplicable != null) {
			estat = RegistreProcesEstatEnum.REGLA_PENDENT;
		} else {
			estat = RegistreProcesEstatEnum.BUSTIA_PENDENT;
		}
		contextfindEstat.stop();
		
		
		//-- save anotacio and interessats in db --
		//-- and save annexos and firmes in db and their byte content in the folder in local file system --
		Timer.Context contextcrearRegistreEntity = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.crearRegistreEntity")).time();
		RegistreEntity anotacioEntity = registreHelper.crearRegistreEntity(
				entitat,
				tipus,
				unitatOrganitzativaCodi,
				registreAnotacio,
				reglaAplicable,
				estat);
		contextcrearRegistreEntity.stop();
		
		//-- create emails ---
		Timer.Context contextmoveAnotacioToBustiaPerDefecte = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.moveAnotacioToBustiaPerDefecte")).time();
		moveAnotacioToBustiaPerDefecte(
				entitat,
				unitatOrganitzativaCodi,
				anotacioEntity);
		if (reglaAplicable == null) {
			emailHelper.createEmailsPendingToSend(
					(BustiaEntity) anotacioEntity.getPare(),
					anotacioEntity,
					anotacioEntity.getDarrerMoviment());
		}
		contextmoveAnotacioToBustiaPerDefecte.stop();
		
		
		//-- apply rules of type bustia or unitat ---
		Timer.Context contextprocessarAnotacioPendentRegla = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.processarAnotacioPendentRegla")).time();
		Exception exceptionProcessant = null;
		if (reglaAplicable != null && (reglaAplicable.getTipus() == ReglaTipusEnumDto.BUSTIA || reglaAplicable.getTipus() == ReglaTipusEnumDto.UNITAT)) {
			exceptionProcessant = registreHelper.processarAnotacioPendentRegla(anotacioEntity.getId());
		}
		contextprocessarAnotacioPendentRegla.stop();
		
		//------- process --------
		Timer.Context contextprocess = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.process")).time();
		// if asynchronous processing is turned off save anotacio in arxiu and apply reglas immediately
		if (!bustiaHelper.isProcessamentAsincronProperty()) {
			logger.debug("L'anotació es processarà inmediatament (" +
					"entitatUnitatCodi=" + entitatCodi + ", " +
					"tipus=" + tipus + ", " +
					"unitatOrganitzativa=" + unitatOrganitzativaCodi + ", " +
					"anotacio=" + registreAnotacio.getNumero() + ")");
			exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(anotacioEntity.getId());
			if (exceptionProcessant == null) {
				exceptionProcessant = registreHelper.processarAnotacioPendentRegla(anotacioEntity.getId());
			}
		// if asynchronous processing is turned on there are two @Scheduled methods that will periodically process anotacions pending to save in arxiu or to apply regla to			
		} else {
			logger.debug("L'anotació es processarà de forma asíncrona (" +
					"entitatUnitatCodi=" + entitatCodi + ", " +
					"tipus=" + tipus + ", " +
					"unitatOrganitzativa=" + unitatOrganitzativaCodi + ", " +
					"anotacio=" + registreAnotacio.getNumero() + ")");
		}
		contextprocess.stop();
		
		context.stop();
		return exceptionProcessant;
	}

	




	@Transactional
	@Override
	public void registreAnotacioEnviarPerEmail(
			Long entitatId,
			Long bustiaId,
			Long registreId, 
			String adresses, 
			String motiu) throws MessagingException {
		
		final Timer timerregistreAnotacioEnviarPerEmail = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioEnviarPerEmail"));
		Timer.Context contextregistreAnotacioEnviarPerEmail = timerregistreAnotacioEnviarPerEmail.time();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		RegistreDto registre = registreService.findOne(
				entitatId,
				bustiaId,
				registreId);
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity registreEntity = registreRepository.findByPareAndId(
				bustia,
				registreId);
		RegistreAnnexDto justificant = null;
		if (registre.getJustificantArxiuUuid() != null && !registre.getJustificantArxiuUuid().isEmpty()) {
			justificant = registreService.getRegistreJustificant(
					entitatId,
					bustiaId, 
					registreId);
		}
		List<RegistreAnnexDto> anexos = registreHelper.getAnnexosAmbFirmes(
				entitatId,
				bustiaId,
				registreId);
		
		String appBaseUrl = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.app.base.url");
		String concsvBaseUrl = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.concsv.base.url");
		String message18nRegistreTipus = "";
		if (registre.getRegistreTipus() == RegistreTipusEnum.ENTRADA) {
			message18nRegistreTipus = messageHelper.getMessage("registre.detalls.camp.desti");
		} else if (registre.getRegistreTipus() == RegistreTipusEnum.SORTIDA) {
			messageHelper.getMessage("registre.detalls.camp.origen");
		}
		Object registreDataOrigen = registre.getDataOrigen() == null ? "" : sdf.format(registre.getDataOrigen());
		Object registreData = registre.getData() == null ? "" : sdf.format(registre.getData());
		Object registreCreatedDate = registre.getCreatedDate() == null ? "" : sdf.format(registre.getCreatedDate());
		Object justificantDataCaptura = null;
		if (justificant != null) {
			justificantDataCaptura = justificant.getDataCaptura() == null ? "" : sdf.format(
					justificant.getDataCaptura());
		}
		
		// ################## HTML ########################
		String htmlJustificant = "";
		if (registre.getJustificantArxiuUuid() != null && !registre.getJustificantArxiuUuid().isEmpty()) {
			htmlJustificant = getHtmlJustificant(
					justificant,
					justificantDataCaptura,
					concsvBaseUrl,
					appBaseUrl,
					bustiaId,
					registreId);
		}
		String htmlAnnexosTable = "";
		if (!registre.getAnnexos().isEmpty()) {
			htmlAnnexosTable = getHtmlAnnexosTable(
					anexos,
					sdf,
					appBaseUrl,
					concsvBaseUrl,
					bustiaId,
					registreId);
		}
		String htmlInteressatsTable = "";
		if (!registre.getInteressats().isEmpty()) {
			htmlInteressatsTable = getHtmlInteressatsTable(registre);
		}
		// ### Usuari que reenvia l'anotació ###
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		DadesUsuari dadesUsuariActual = null;
		if (auth != null)
			dadesUsuariActual = cacheHelper.findUsuariAmbCodi(auth.getName());
		
		String html = getHtml(
				registre,
				registreData,
				message18nRegistreTipus,
				registreDataOrigen,
				registreCreatedDate,
				htmlJustificant,
				htmlAnnexosTable,
				htmlInteressatsTable,
				dadesUsuariActual, motiu);
		
		
		// ################## PLAIN TEXT ###################
		String plainTextInteressats = "";
		if (!registre.getInteressats().isEmpty()) {
			plainTextInteressats = getPlainTextInteressats(registre);
		}
		String plainTextJustificant = "";
		if (registre.getJustificantArxiuUuid() != null && !registre.getJustificantArxiuUuid().isEmpty()) {
			plainTextJustificant = getPlainTextJustificant(
					registre,
					justificantDataCaptura,
					justificant);
		}
		String plainTextAnnexos = "";
		if (!registre.getAnnexos().isEmpty()) {
			plainTextAnnexos = getPlaintTextAnnexos(
					registre,
					anexos,
					sdf);
		}
		String plainText = getPlainText(
				registre,
				registreData,
				registreDataOrigen,
				registreCreatedDate,
				message18nRegistreTipus,
				plainTextInteressats,
				plainTextJustificant,
				plainTextAnnexos);
		
		
		
		MimeMessage missatge = mailSender.createMimeMessage();
		missatge.setHeader("Content-Type","text/html charset=UTF-8");
		MimeMessageHelper helper;
		helper = new MimeMessageHelper(missatge, true);
		
		adresses = 
			       Normalizer
			           .normalize(adresses, Normalizer.Form.NFD)
			           .replaceAll("[^\\p{ASCII}]", "");
		
		missatge.addRecipients(RecipientType.TO,
				InternetAddress.parse(adresses));
		helper.setFrom(emailHelper.getRemitent());
		helper.setSubject("Distribució: " + registre.getNom());
		messageHelper.getMessage("registre.detalls.camp.tipus");
		
		helper.setText(plainText, html);
		
		mailSender.send(missatge);
		
		List<String> params = new ArrayList<>();
		params.add(registreEntity.getNom());
		params.add(adresses);
		//String logTo = "Destinataris: " + adresses;
		contingutLogHelper.log(
				registreEntity,
				LogTipusEnumDto.ENVIAMENT_EMAIL,
				params,
				false);
		
		registreEntity.updateEnviatPerEmail(true);
		
		contextregistreAnotacioEnviarPerEmail.stop();
	}



	@Transactional(readOnly = true)
	@Override
	public String getApplictionMetrics() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(
				new MetricsModule(
						TimeUnit.SECONDS,
						TimeUnit.MILLISECONDS,
						false));
		
		logger.debug("Consultant les mètriques de l'aplicació");
		try {
			return mapper.writeValueAsString(metricRegistry);
		} catch (Exception ex) {
			logger.error("Error al generar les mètriques de l'aplicació", ex);
			return "ERR";
		}
	}



	@Transactional(readOnly = true)
	@Override
	public long contingutPendentBustiesAllCount(
			Long entitatId) {
		logger.debug("Consultant els elements pendents a totes les busties ("
				+ "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return cacheHelper.countElementsPendentsBustiesUsuari(
				entitat,
				auth.getName());
	}

	@Transactional
	@Override
	public void registreReenviar(
			Long entitatId,
			Long bustiaOrigenId,
			Long[] bustiaDestiIds,
			Long registreId,
			boolean opcioDeixarCopiaSelectada,
			String comentari,
			Long[] perConeixement) throws NotFoundException {
		
		logger.debug("Reenviant contingut pendent de la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaOrigenId=" + bustiaOrigenId + ", "
				+ "bustiaDestiIds=" + bustiaDestiIds + ", "
				+ "registreId=" + registreId + ", "
				+ "comentari=" + comentari + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		BustiaEntity bustiaOrigen = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaOrigenId,
				true);
		List<BustiaEntity> bustiesDesti = new ArrayList<BustiaEntity>();
		for (int i = 0; i < bustiaDestiIds.length; i++) {
			BustiaEntity bustiaDesti = entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaDestiIds[i],
					false);
			bustiesDesti.add(bustiaDesti);
		}
		
		List<BustiaEntity> bustiesPerConeixement = new ArrayList<BustiaEntity>();
		for (int i = 0; i < perConeixement.length; i++) {
			BustiaEntity bustiaDestiPerConeixement = entityComprovarHelper.comprovarBustia(
					entitat,
					perConeixement[i],
					false);
			bustiesPerConeixement.add(bustiaDestiPerConeixement);
		}
		
		RegistreEntity reg = registreRepository.findByPareAndId(
				bustiaOrigen,
 				registreId);
		if (reg == null) {
			throw new NotFoundException(registreId, RegistreEntity.class);
		}
		
		ContingutEntity registreOriginal = entityComprovarHelper.comprovarContingut(
				entitat,
				registreId,
				bustiaOrigen);
		List<String> assentamentsPerTramitar = new ArrayList<String>(), assentamentsPerConeixement = new ArrayList<String>();
		List<ContingutEntity> nousContinguts = new ArrayList<ContingutEntity>();
		
		for (int i = 0; i < bustiesDesti.size(); i++) {
			BustiaEntity bustia = bustiesDesti.get(i);
			boolean assentamentPerConeixement = bustiesPerConeixement.contains(bustia);
			ContingutEntity registrePerReenviar = null;
			boolean ferCopia = opcioDeixarCopiaSelectada || !isLastIteration(i, bustiesDesti);
			if (ferCopia) {
				registrePerReenviar = contingutHelper.ferCopiaRegistre(
						registreOriginal,
						bustia.getEntitat().getCodiDir3());
			} else {
				registrePerReenviar = registreOriginal;
			}
			
			ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
					registrePerReenviar,
					bustia,
					comentari,
					assentamentPerConeixement);
			
			// when anotacio processed by bustia user is resent to another bustia in new bustia it should be again pending 
			if (registrePerReenviar.getClass() == RegistreEntity.class) {
				RegistreEntity registre = (RegistreEntity) registrePerReenviar;
				if (registre.getProcesEstat() == RegistreProcesEstatEnum.BUSTIA_PROCESSADA) {
					registre.updateProces(
							RegistreProcesEstatEnum.BUSTIA_PENDENT,
							null);
				}
			}	
			if (opcioDeixarCopiaSelectada) {
				contingutLogHelper.logMoviment(
						registreOriginal,
						LogTipusEnumDto.REENVIAMENT,
						contingutMoviment,
						true);
			}
			contingutLogHelper.logMoviment(
					registrePerReenviar,
					LogTipusEnumDto.REENVIAMENT,
					contingutMoviment,
					true);
			emailHelper.createEmailsPendingToSend(
					bustia,
					registrePerReenviar,
					contingutMoviment);
			bustiaHelper.evictCountElementsPendentsBustiesUsuari(
					entitat,
					bustia);
			
			if (assentamentPerConeixement) {
				assentamentsPerConeixement.add(bustia.getNom());
			} else {
				assentamentsPerTramitar.add(bustia.getNom());
			}
			nousContinguts.add(registrePerReenviar);
		}
		
		boolean crearComentariInformatiu = Boolean.parseBoolean(PropertiesHelper.getProperties().getProperty("es.caib.distribucio.contingut.enviar.crear.comentari"));
		
		if (crearComentariInformatiu) {
			StringBuilder comentariContingut = generarTextDestins(
					assentamentsPerTramitar, 
					assentamentsPerConeixement);
			if (comentariContingut.length() > 0) {
				for (ContingutEntity contingut: nousContinguts) {
					ContingutComentariEntity comentariAmbBustiesDesti = ContingutComentariEntity.getBuilder(
							contingut, 
							comentariContingut.toString()).build();
					contingutComentariRepository.save(comentariAmbBustiesDesti);
					
					List<ContingutMovimentEntity> contingutMoviment= contingutMovimentRepository.findByContingutOrderByCreatedDateAsc(contingut);
					for (ContingutMovimentEntity moviment: contingutMoviment) {
						moviment.updateComentariDestins(comentariContingut.toString());
					}
				}
			}
		}
		// Refrescam cache d'elements pendents de les bústies
		bustiaHelper.evictCountElementsPendentsBustiesUsuari(
				entitat,
				bustiaOrigen);
	}
	
	@Transactional(readOnly = true)
	@Override
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzatives(
			Long entitatId,
			boolean nomesBusties,
			boolean nomesBustiesPermeses,
			boolean comptarElementsPendents) {
		logger.debug("Consulta de l'arbre d'unitats organitzatives ("
				+ "entitatId=" + entitatId + ", "
				+ "nomesBusties=" + nomesBusties + ", "
				+ "nomesBustiesPermeses=" + nomesBustiesPermeses + ", "
				+ "comptarElementsPendents=" + comptarElementsPendents + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		return bustiaHelper.findArbreUnitatsOrganitzatives(
				entitat,
				nomesBusties,
				nomesBustiesPermeses,
				comptarElementsPendents);
	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public boolean isBustiaReadPermitted(
			Long bustiaId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return permisosHelper.isGrantedAll(
				bustiaId,
				BustiaEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
	}
	

	@Override
	@Transactional
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(
			Long entitatId,
			List<BustiaDto> busties) {
		logger.debug("Consulta de l'arbre d'unitats organitzatives (" +
				"entitatId=" + entitatId +")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		return bustiaHelper.findArbreUnitatsOrganitzativesAmbFiltre(
				entitat,
				busties);
	}



	@Override
	@Transactional
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) {
		logger.debug("Esborrant permis per a la bústia (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ", " +
				"permisId=" + permisId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		permisosHelper.deletePermis(
				id,
				BustiaEntity.class,
				permisId);
		bustiaHelper.evictCountElementsPendentsBustiesUsuari(
				entitat,
				bustia);
	}

	@Override
	@Transactional
	public int moureAnotacions(
			long entitatId, 
			long bustiaId, 
			long destiId, 
			String comentari) {
		logger.debug("Movent les anotacions de registre entre bústies (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"destiId=" + destiId + ", " +
				"comentari=" + comentari + ")");
		int ret = 0;
		// Comprova permisos
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity bustiaOrigen = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				false);
		BustiaEntity bustiaDesti = entityComprovarHelper.comprovarBustia(
				entitat,
				destiId,
				false);
		// Recupera totes les anotacions de registre
		for (RegistreEntity registre : registreRepository.findByPareId(bustiaId)) {
			if (RegistreProcesEstatEnum.ARXIU_PENDENT == registre.getProcesEstat()) {
				throw new ValidationException(
						registre.getNumero(),
						RegistreEntity.class,
						"Registre amb núm: " + registre.getNumero() + " no es pot reenviar perquè està pendent d'enviar al arxiu");
			} else if ( RegistreProcesEstatEnum.REGLA_PENDENT == registre.getProcesEstat()) {
				throw new ValidationException(
						registre.getNumero(),
						RegistreEntity.class,
						"Registre amb núm: " + registre.getNumero() + " no es pot reenviar perquè te activat el processament automàtic mitjançant una regla (" +
						"reglaId=" + registre.getRegla().getId() + ")");
			}
			ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
					registre,
					bustiaDesti,
					comentari,
					false);
			// Registra al log l'enviament del contingut
			contingutLogHelper.logMoviment(
					registre,
					LogTipusEnumDto.MOVIMENT,
					contingutMoviment,
					true);
			ret++;
		}
		logger.debug(
				"Moviment entre bústies finalitzat correctament. " + ret + " anotacions mogudes de la bustia \"" +
				bustiaOrigen.getId() + " " + bustiaOrigen.getNom() + "\" a la bustia \""+ bustiaDesti.getId() + 
				" " + bustiaDesti.getNom() + "\"");
		return ret;
	}

	private StringBuilder generarTextDestins(
			List<String> assentamentsPerTramitar, 
			List<String> assentamentsPerConeixement) {
		StringBuilder comentariContingut = new StringBuilder();
		if (!assentamentsPerTramitar.isEmpty()) {
			String bustiesTramitacio = messageHelper.getMessage("registre.anotacio.enviat.pertramitar");
			comentariContingut.append(bustiesTramitacio);
			int i = 0;
			for (String perTramitarNom: assentamentsPerTramitar) {
				comentariContingut.append(perTramitarNom);
				if(i++ != assentamentsPerTramitar.size() - 1){
					comentariContingut.append(", ");
				}
			}
		}
		
		if (!assentamentsPerConeixement.isEmpty()) {
			if (!assentamentsPerTramitar.isEmpty())
				comentariContingut.append("\n\r");
			
			String bustiesConeixment = messageHelper.getMessage("registre.anotacio.enviat.perconeixment");
			comentariContingut.append(bustiesConeixment);
			int j = 0;
			for (String perConeixementNom: assentamentsPerConeixement) {
				comentariContingut.append(perConeixementNom);
				if(j++ != assentamentsPerConeixement.size() - 1){
					comentariContingut.append(", ");
				}
			}
		}
		return comentariContingut;
	}

	private void omplirPermisosPerBusties(
			List<? extends BustiaDto> busties,
			boolean ambLlistaPermisos) {
		// Filtra les entitats per saber els permisos per a l'usuari actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<BustiaDto> bustiesRead = new ArrayList<BustiaDto>();
		bustiesRead.addAll(busties);
		permisosHelper.filterGrantedAll(
				bustiesRead,
				new ObjectIdentifierExtractor<BustiaDto>() {
					public Long getObjectIdentifier(BustiaDto bustia) {
						return bustia.getId();
					}
				},
				BustiaEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		for (BustiaDto bustia: busties) {
			bustia.setUsuariActualRead(
					bustiesRead.contains(bustia));
		}
		// Obté els permisos per a totes les bústies només amb una consulta
		if (ambLlistaPermisos) {
			List<Long> ids = new ArrayList<Long>();
			for (BustiaDto bustia: busties)
				ids.add(bustia.getId());
			Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(
					ids,
					BustiaEntity.class);
			for (BustiaDto bustia: busties)
				bustia.setPermisos(permisos.get(bustia.getId()));
		}
	}
	
	private String getHtmlJustificant(RegistreAnnexDto justificant, Object justificantDataCaptura, String concsvBaseUrl, String appBaseUrl, Long bustiaId, Long registreId) {
		String htmlJustificant = 
				"		<table>"+
				"			<tr>"+
				"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.titol.justificant") + "</th>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.data.captura") +"</th>"+
				"				<td>"+ justificantDataCaptura + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.origen") +"</th>"+
				"				<td>"+ Objects.toString(justificant.getOrigenCiutadaAdmin(), "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.estat.elaboracio") + "</th>"+
				"				<td>" + (justificant.getNtiElaboracioEstat()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiElaboracioEstat." + Objects.toString(justificant.getNtiElaboracioEstat(), ""))) + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.tipus.documental") + "</th>"+
				"				<td>" + (justificant.getNtiTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiTipusDocument." + Objects.toString(justificant.getNtiTipusDocument(), ""))) + "</td>"+
				"			</tr>"+			
				(justificant.getLocalitzacio() == null ? "": 
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.localitzacio") + "</th>"+
					"				<td>"  + justificant.getLocalitzacio() +
					"			</tr>"
						)+ 
				(justificant.getObservacions() == null ? "": 
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.observacions") + "</th>"+
					"				<td>"  + justificant.getObservacions() +
					"			</tr>"
						)+ 					
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.fitxer") + "</th>"+
				"				<td>"  + Objects.toString(justificant.getFitxerNom(), "") + "("+Objects.toString(justificant.getFitxerTamany(), "")+" bytes)" +
				(concsvBaseUrl != null && justificant.getFirmaCsv() != null?
						"<a href=\""+concsvBaseUrl+"/view.xhtml?hash="+justificant.getFirmaCsv()+"\"> Descarregar </a>"
						:"<a href=\""+appBaseUrl+"/modal/contingut/"+bustiaId+"/registre/"+registreId+"/justificant\"> Descarregar </a>") +
				"</td>"+
				"			</tr>"+								
				"		</table>";
		
		return htmlJustificant;
	}	
	
	private String getHtmlAnnexosTable(List<RegistreAnnexDto> anexos, SimpleDateFormat sdf, String appBaseUrl, String concsvBaseUrl, Long bustiaId, Long registreId) {
		
		String htmlAnnexos = "";
		for (RegistreAnnexDto annex: anexos) {
			String htmlFirmes="";
			if (annex.getFirmes() != null && !annex.getFirmes().isEmpty()){
				htmlFirmes+=
				"			<tr>"+
				"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.annex.detalls.camp.firmes") + "</th>"+
				"			</tr>";
				int i=1;
				for (ArxiuFirmaDto firma: annex.getFirmes()){
					String htmlDetalls="";
					for(ArxiuFirmaDetallDto detall: firma.getDetalls()){
						htmlDetalls+=
								"							<tr>"+
								"								<td>" +(detall.getData() == null ? messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.data.nd") : sdf.format(detall.getData())) + "</td>"+
								"								<td>"+ Objects.toString(detall.getResponsableNif(), "") + "</td>"+
								"								<td>"+ Objects.toString(detall.getResponsableNom(), "") + "</td>"+
								"								<td>"+ Objects.toString(detall.getEmissorCertificat(), "") + "</td>"+
								"							</tr>";
					}
					htmlFirmes+=

							"			<tr>"+
							"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.annex.detalls.camp.firma") +" "+i+ 
												(!firma.isAutofirma()? "": 
													"("+messageHelper.getMessage("registre.annex.detalls.camp.firma.autoFirma")+")"
												)+ 	
											"</th>"+
							"			</tr>"+
							
							
							"			<tr>"+
							"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaTipus") +"</th>"+
							"				<td>" + (firma.getTipus()==null ? "": messageHelper.getMessage("document.nti.tipfir.enum." + Objects.toString(firma.getTipus(), ""))) + "</td>"+
							"			</tr>"+
							"			<tr>"+
							"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaPerfil") +"</th>"+
							"				<td>"+ Objects.toString(firma.getPerfil(), "") + "</td>"+
							"			</tr>"+
							
							(firma.getTipus() == ArxiuFirmaTipusEnumDto.PADES && firma.getTipus() == ArxiuFirmaTipusEnumDto.CADES_ATT && firma.getTipus() == ArxiuFirmaTipusEnumDto.XADES_ENV ? "": 
								"			<tr>"+
								"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.fitxer") + "</th>"+
								"				<td>"+ Objects.toString(firma.getFitxerNom(), "") + "</td>"+
								"			</tr>"
									)+ 	
							
							(firma.getCsvRegulacio() == null || firma.getCsvRegulacio().isEmpty() ? "": 
								"			<tr>"+
								"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaCsvRegulacio") + "</th>"+
								"				<td>"+ Objects.toString(firma.getCsvRegulacio(), "") + "</td>"+
								"			</tr>"
									)+ 	
					
							(firma.getDetalls().isEmpty() ? "": 
								"				<tr>"+
								"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls") + "</th>"+	
								
								"					<td>"+
								"						<table class=\"table teble-striped table-bordered\">"+
								"						<thead>"+
								"							<tr>"+
								"								<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.data") + "</th>"+		
								"								<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.nif") + "</th>"+	
								"								<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.nom") + "</th>"+	
								"								<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.emissor") + "</th>"+	
								"							</tr>"+
								"						<tbody>"+
								htmlDetalls +
								
								"						</tbody>"+
								"						</table>"+
								"					</td>"+
								"				</tr>");
					i++;
				}
			}
			htmlAnnexos += 
					"			<tr>"+
					"				<th class=\"tableHeader\" colspan=\"2\">" + annex.getTitol() + "</th>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.data.captura") +"</th>"+
					"				<td>"+ (annex.getDataCaptura() == null ? "" : sdf.format(annex.getDataCaptura())) + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.origen") +"</th>"+
					"				<td>"+ Objects.toString(annex.getOrigenCiutadaAdmin(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.estat.elaboracio") + "</th>"+
					"				<td>" + (annex.getNtiElaboracioEstat()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiElaboracioEstat." + Objects.toString(annex.getNtiElaboracioEstat(), ""))) + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.tipus.documental") + "</th>"+
					"				<td>" + (annex.getNtiTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiTipusDocument." + Objects.toString(annex.getNtiTipusDocument(), ""))) + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.sicres.tipus.document") + "</th>"+
					"				<td>"  + (annex.getSicresTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.sicresTipusDocument."+annex.getSicresTipusDocument())) + "</td>"+
					"			</tr>"+				
					(annex.getLocalitzacio() == null ? "": 
						"			<tr>"+
						"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.localitzacio") + "</th>"+
						"				<td>"  + annex.getLocalitzacio() +
						"			</tr>"
							)+ 
					(annex.getObservacions() == null ? "": 
						"			<tr>"+
						"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.observacions") + "</th>"+
						"				<td>"  + annex.getObservacions() +
						"			</tr>"
							)+ 					
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.fitxer") + "</th>"+
					"				<td>"  + Objects.toString(annex.getFitxerNom(), "") + "("+Objects.toString(annex.getFitxerTamany(), "")+" bytes)"+
					(concsvBaseUrl != null && annex.getFirmaCsv()!=null?
							"<a href=\""+concsvBaseUrl+"/view.xhtml?hash="+annex.getFirmaCsv()+"\"> Descarregar </a>"
							:"<a href=\""+appBaseUrl+"/modal/contingut/"+bustiaId+"/registre/"+registreId+"/annex/"+annex.getId()+"/arxiu/DOCUMENT\"> Descarregar </a>") +
					"</td>"+
					"			</tr>"+
					htmlFirmes+"";
		}
		String htmlAnnexosTable=
				
				"		<table>"+
				"			<tr>"+
				"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.pipella.annexos") + "</th>"+
				"			</tr>"+
				htmlAnnexos +
				"		</table>";
		
		
		
		
		return htmlAnnexosTable;
		
	}	
	
	
	private String getHtmlInteressatsTable(RegistreDto registre) {
		String htmlInteressats = "";
		for (RegistreInteressat interessat: registre.getInteressats()) {
			RegistreInteressat representant = interessat.getRepresentant();
			String htmlRepresentant = "";
			if (representant!=null){
				
				String representantTitle="";
				if(representant.getTipus().equals("PERSONA_FIS")){
					String representatLlinatge2 = representant.getLlinatge2()!=null ? representant.getLlinatge2():"";
					representantTitle = representant.getNom()+" "+representant.getLlinatge1()+" "+representatLlinatge2;
				} else {
					representantTitle = representant.getRaoSocial();
				}
				
				htmlRepresentant =
					"			<tr>"+
					"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.interessat.detalls.camp.representant") + "</th>"+
					"			</tr>"+						
					"			<tr>"+
					"				<th class=\"tableHeader\" colspan=\"2\">" + representantTitle + "</th>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.detalls.camp.interessat.tipus") +"</th>"+
					"				<td>"+ (representant.getTipus()==null ? "": messageHelper.getMessage("registre.interessat.tipus.enum." + Objects.toString(representant.getTipus(), "")))+ "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.detalls.camp.interessat.document") +"</th>"+
					"				<td>"+ Objects.toString(representant.getDocumentTipus(), "")+": "+representant.getDocumentNum() + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.pais") + "</th>"+
					"				<td>" + Objects.toString(representant.getPais(), "") + (representant.getPaisCodi() == null ? "" : " (" + representant.getPaisCodi() + ")") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.provincia") + "</th>"+
					"				<td>" + Objects.toString(representant.getProvincia(), "") + (representant.getProvinciaCodi() == null ? "" : " (" + representant.getProvinciaCodi() + ")") +  "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.municipi") + "</th>"+
					"				<td>"  + Objects.toString(representant.getMunicipi(), "") + (representant.getMunicipiCodi() == null ? "" : " (" + representant.getMunicipiCodi() + ")") + "</td>"+
					"			</tr>"+	
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.adresa") + "</th>"+
					"				<td>" + Objects.toString(representant.getAdresa(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.codiPostal") + "</th>"+
					"				<td>"  + Objects.toString(representant.getCodiPostal(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.email") +"</th>"+
					"				<td>"+ Objects.toString(representant.getEmail(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.telefon") + "</th>"+
					"				<td>" + Objects.toString(representant.getTelefon(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.interessat.detalls.camp.emailHabilitat") + "</th>"+
					"				<td>" + Objects.toString(representant.getEmailHabilitat(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent") +"</th>"+
					"				<td>"+ (representant.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(representant.getCanalPreferent(), "")))+ "</td>"+
					"			</tr>"+					
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.observacions") + "</th>"+
					"				<td>"  + Objects.toString(representant.getObservacions(), "") + "</td>"+
					"			</tr>";											
			}
			String interesatTitle="";
			if (interessat.getTipus().equals("PERSONA_FIS")) {
				String interessatLlinatge1 = interessat.getLlinatge2()!=null ? interessat.getLlinatge2() : "";
				interesatTitle = interessat.getNom()+" "+interessat.getLlinatge1()+" "+interessatLlinatge1;
			} else {
				interesatTitle = interessat.getRaoSocial();
			}
			htmlInteressats += 
					"			<tr>"+
					"				<th class=\"tableHeader\" colspan=\"2\">" + interesatTitle + "</th>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.detalls.camp.interessat.tipus") +"</th>"+
					"				<td>"+ (interessat.getTipus()==null ? "": messageHelper.getMessage("registre.interessat.tipus.enum." + Objects.toString(interessat.getTipus(), "")))+ "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.detalls.camp.interessat.document") +"</th>"+
					"				<td>"+ Objects.toString(interessat.getDocumentTipus(), "")+": "+interessat.getDocumentNum() + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.pais") + "</th>"+
					"				<td>" + Objects.toString(interessat.getPais(), "") + (interessat.getPaisCodi() == null ? "" : " (" + interessat.getPaisCodi() + ")")  + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.provincia") + "</th>"+
					"				<td>" + Objects.toString(interessat.getProvincia(), "") + (interessat.getProvinciaCodi() == null ? "" : " (" + interessat.getProvinciaCodi() + ")")  + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.municipi") + "</th>"+
					"				<td>"  + Objects.toString(interessat.getMunicipi(), "") + (interessat.getMunicipiCodi() == null ? "" : " (" + interessat.getMunicipiCodi() + ")")  + "</td>"+
					"			</tr>"+	
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.adresa") + "</th>"+
					"				<td>" + Objects.toString(interessat.getAdresa(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.codiPostal") + "</th>"+
					"				<td>"  + Objects.toString(interessat.getCodiPostal(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.email") +"</th>"+
					"				<td>"+ Objects.toString(interessat.getEmail(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.telefon") + "</th>"+
					"				<td>" + Objects.toString(interessat.getTelefon(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.interessat.detalls.camp.emailHabilitat") + "</th>"+
					"				<td>" + Objects.toString(interessat.getEmailHabilitat(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent") +"</th>"+
					"				<td>"+ (interessat.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(interessat.getCanalPreferent(), "")))+ "</td>"+
					"			</tr>"+					
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.observacions") + "</th>"+
					"				<td>"  + Objects.toString(interessat.getObservacions(), "") + "</td>"+
					"			</tr>"+
					htmlRepresentant;
		}
		
		
		String htmlInteressatsTable = 
							"		<table>"+
							"			<tr>"+
							"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.pipella.interessats") + "</th>"+
							"			</tr>"+
							htmlInteressats +
							"		</table>";
	
		return htmlInteressatsTable;
	}	

	private  String getHtml(
			RegistreDto registre, 
			Object registreData, 
			String message18nRegistreTipus, 
			Object registreDataOrigen, 
			Object registreCreatedDate, 
			String htmlJustificant, 
			String htmlAnnexosTable, 
			String htmlInteressatsTable, 
			DadesUsuari usuariActual, 
			String motiu) {
		
		String html = 
				"<!DOCTYPE html>"+
				"<html>"+
				"<head>"+
				"<style>"+
				"body {"+
				"	margin: 0px;"+
				"	font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;"+
				"	font-size: 14px;"+
				"	color: #333;"+
				"}"+
				"table {"+
				"	"+
				"	border-radius: 4px;"+
				"	width: 100%;"+
				"	border-collapse: collapse;"+
				"	margin-bottom: 10px;"+
				"}"+
				
				"td, th {"+
				"	border-bottom: solid 0.5px #ddd;"+
				"	height: 38px;"+
				"	border: 1px solid #ddd;"+
				"	padding-left: 8px;"+
				"	padding-right: 8px;"+
				"}"+
				
				".tableHeader {"+
				"	background-color: #f5f5f5;"+
				"	border-top-left-radius: 4px;"+
				"	border-top-righ-radius: 4px;"+
				"}"+
				
				".header {"+
				"	height: 30px;"+
				"	background-color: #ff9523;"+
				"	height: 90px;"+
				"	text-align: center;"+
				"	line-height: 100px;"+
				"}"+
				".content {"+
				"	margin: auto;"+
				"	width: 70%;"+
				"	padding: 10px;"+
				"}"+
				
				".footer {"+
				"	height: 30px;"+
				"	background-color: #ff9523;"+
				"	text-align: center;"+
				
				"}"+
				
				".headerText {"+
				"    font-weight: bold;"+
				"    font-family: \"Trebuchet MS\", Helvetica, sans-serif;"+
				"    color: white;"+
				"    font-size: 30px;"+
				"	display: inline-block;"+
				"	vertical-align: middle;"+
				"	line-height: normal; "+
				"}"+
				
				".footerText {"+
				"    font-weight: bold;"+
				"    font-family: \"Trebuchet MS\", Helvetica, sans-serif;"+
				"    color: white;"+
				"    font-size: 13px;"+
				"	display: inline-block;"+
				"	vertical-align: middle;"+
				"	line-height: normal; "+
				"}"+
				
				"</style>"+
				"</head>"+
				"<body>"+
				"	<div class=\"header\">"+
				"	<span class=\"headerText\">"+ messageHelper.getMessage("registre.titol").toUpperCase()+"</span> "+
				"	</div>"+
				"	<div class=\"content\">" +
				(usuariActual == null ? "" :
				"		<table>"+
				"			<tr>"+
				"				<th class=\"tableHeader\" colspan=\"2\">"+messageHelper.getMessage("registre.remitent.titol")+"</th>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.remitent.id") + "</th>"+
				"				<td>" + usuariActual.getCodi() + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.remitent.nom") +"</th>"+
				"				<td>"+ usuariActual.getNom() + " " + (usuariActual.getLlinatges() != null ? usuariActual.getLlinatges() : "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.remitent.email") + "</th>"+
				"				<td>" + usuariActual.getEmail() + "</td>"+
				"			</tr>"+				
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.bustia") + "</th>"+
				"				<td>" + registre.getPare().getNom() + "</td>"+
				"			</tr>"+				
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.motiu") + "</th>"+
				"				<td>" + motiu + "</td>"+
				"			</tr>"+						
				"		</table>"
				) +
				"		<table>"+
				"			<tr>"+
				"				<th class=\"tableHeader\" colspan=\"2\">"+messageHelper.getMessage("registre.titol")+"</th>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.detalls.camp.tipus") +"</th>"+
				"				<td>"+ messageHelper.getMessage("registre.anotacio.tipus.enum." + Objects.toString(registre.getRegistreTipus(), "")) + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.detalls.camp.numero") +"</th>"+
				"				<td>"+ Objects.toString(registre.getNumero(), "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.detalls.camp.data") + "</th>"+
				"				<td>" + registreData + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.detalls.camp.proces.estat") + "</th>"+
				"				<td>" + messageHelper.getMessage("registre.proces.estat.enum." + registre.getProcesEstat()) + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.detalls.camp.proces.presencial") + "</th>"+
				"				<td>" + messageHelper.getMessage("boolean." + Objects.toString(registre.getPresencial(), "")) + "</td>"+
				"			</tr>"+
				"		</table>"+

				"		<table>"+
				"			<tr>"+
				"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.titol.obligatories") + "</th>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>"+ messageHelper.getMessage("registre.detalls.camp.oficina") + "</th>"+
				"				<td>" + Objects.toString(registre.getOficinaDescripcio(), "") + " (" + Objects.toString(registre.getOficinaCodi(), "") + ")" + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.llibre") + "</th>"+
				"				<td>" + Objects.toString(registre.getLlibreDescripcio(), "") + " (" + Objects.toString(registre.getLlibreCodi(), "") + ")" + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.extracte") + "</th>"+
				"				<td>" + Objects.toString(registre.getExtracte(), "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.docfis") + "</th>"+
				"				<td>" + Objects.toString(registre.getDocumentacioFisicaCodi(), "")  + "-" + Objects.toString(registre.getDocumentacioFisicaDescripcio(), "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + message18nRegistreTipus +"</th>"+
				"				<td>" + Objects.toString(registre.getUnitatAdministrativaDescripcio(), "") + " (" + Objects.toString(registre.getUnitatAdministrativa(), "") + ")" + "</td>"+
				"			</tr>		"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.assumpte.tipus") + "</th>"+
				"				<td>" + Objects.toString(registre.getAssumpteTipusDescripcio(), "") + " (" + Objects.toString(registre.getAssumpteTipusCodi(), "") + ")" + "</td>"+
				"			</tr>	"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.idioma") + "</th>"+
				"				<td>" + Objects.toString(registre.getIdiomaDescripcio(), "") + " (" + Objects.toString(registre.getIdiomaCodi(), "") + ")" + "</td>"+
				"			</tr>		"+
				"		</table>"+

				"		<table>"+
				"			<tr>"+
				"				<th colspan=\"4\" class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.titol.opcionals") + "</th>"+
				"			</tr>			"+
				"			<tr>"+
				"				<th colspan=\"2\">" + messageHelper.getMessage("registre.detalls.camp.assumpte.codi") + "</th>"+
				"				<td colspan=\"2\">" + Objects.toString(registre.getAssumpteCodi(), "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th colspan=\"2\">" + messageHelper.getMessage("registre.detalls.camp.procediment") + "</th>"+
				"				<td colspan=\"2\">" + Objects.toString(registre.getProcedimentCodi(), "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.refext") + "</th>"+
				"				<td>" + Objects.toString(registre.getReferencia(), "") + "</td>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.numexp") + "</th>"+
				"				<td>" + Objects.toString(registre.getExpedientNumero(), "") + "</td>	"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.transport.tipus") + "</th>"+
				"				<td>" + Objects.toString(registre.getTransportTipusDescripcio(), "") + " (" + Objects.toString(registre.getTransportTipusCodi(), "") + ")" + "</td>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.transport.num") + "</th>"+
				"				<td>" + Objects.toString(registre.getTransportNumero(), "") + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th colspan=\"2\">" + messageHelper.getMessage("registre.detalls.camp.origen.oficina") + "</th>"+
				"				<td colspan=\"2\">" + Objects.toString(registre.getOficinaOrigenDescripcio(), "") + " (" + Objects.toString(registre.getOficinaOrigenCodi(), "") + ")" + "</td>"+
				"			</tr>		"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.origen.num") + "</th>"+
				"				<td>" + Objects.toString(registre.getNumeroOrigen(), "") + "</td>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.origen.data") + "</th>"+
				"				<td>" + registreDataOrigen + "</td>"+
				"			</tr>	"+
				"			<tr>"+
				"				<th colspan=\"2\">" + messageHelper.getMessage("registre.detalls.camp.observacions") + "</th>"+
				"				<td colspan=\"2\">" + Objects.toString(registre.getObservacions(), "") + "</td>"+
				"			</tr>			"+
				"		</table>"+
				
				"		<table>"+
				"			<tr>"+
				"				<th colspan=\"4\" class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.titol.seguiment") + "</th>"+
				"			</tr>			"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.entitat") + "</th>"+
				"				<td>" + Objects.toString(registre.getEntitatDescripcio(), "") + " (" + Objects.toString(registre.getEntitatCodi(), "") + ")" + "</td>"+
				"			</tr>"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.aplicacio") +"</th>"+
				"				<td>" + Objects.toString(registre.getAplicacioCodi(), "") + Objects.toString(registre.getAplicacioVersio(), "")  + "</td>"+
				"			</tr>		"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.usuari") + "</th>"+
				"				<td>" + Objects.toString(registre.getUsuariNom(), "") + " (" + Objects.toString(registre.getUsuariCodi(), "") + ")" + "</td>"+
				"			</tr>	"+
				"			<tr>"+
				"				<th>" + messageHelper.getMessage("registre.detalls.camp.distribucio.alta") + "</th>"+
				"				<td>" + registreCreatedDate + "</td>"+
				"			</tr>"+		
				"		</table>"+					
				htmlJustificant +
				htmlInteressatsTable + 
				htmlAnnexosTable + 
				"	</div>"+
				"	<div class=\"footer\">"+
					"	<span class=\"footerText\">"+
					"		Distribució - Govern Illes Baleares"+
				"	</span>"+

				"	</div>"+
				"</body>"+
				"</html>";
		
		return html;
	}
	
	private String getPlainTextInteressats(RegistreDto registre) {
		
		
		String plainTextInteressats = "";
		if (!registre.getInteressats().isEmpty()) {
			plainTextInteressats =
				"\n" +
				messageHelper.getMessage("registre.detalls.pipella.interessats").toUpperCase()+"\n"+
				"================================================================================\n";
		}
		for (RegistreInteressat interessat: registre.getInteressats()) {
			RegistreInteressat representant = interessat.getRepresentant();
			String plainTextRepresentant = "";
			if (representant!=null) {
				String representantTitle="";
				if (representant.getTipus().equals("PERSONA_FIS")) {
					String representantLlinatge = representant.getLlinatge2() == null ? " " : representant.getLlinatge2();
					representantTitle = representant.getNom() + " " + representant.getLlinatge1() + " " + representantLlinatge;
				} else {
					representantTitle = representant.getRaoSocial();
				}
				plainTextRepresentant =
					"\n"+												
					messageHelper.getMessage("registre.interessat.detalls.camp.representant").toUpperCase()+"\n"+
					"---------------------------------------------------------\n"+
					representantTitle.toUpperCase()+"\n"+
					"---------------------------------------\n"+
					"\t"+ messageHelper.getMessage("registre.detalls.camp.interessat.tipus") +
					"\t\t\t\t"+ (representant.getTipus()==null ? "": messageHelper.getMessage("registre.interessat.tipus.enum." + Objects.toString(representant.getTipus(), "")))+ "\n"+
					"\t"+ messageHelper.getMessage("registre.detalls.camp.interessat.document") +
					"\t\t\t"+ Objects.toString(representant.getDocumentTipus(), "")+": "+representant.getDocumentNum() + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.pais") + 
					"\t\t\t\t" + Objects.toString(representant.getPais(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.provincia") + 
					"\t\t\t" + Objects.toString(representant.getProvincia(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.municipi") + 
					"\t\t\t"  + Objects.toString(representant.getMunicipi(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.adresa") + 
					"\t\t\t\t" + Objects.toString(representant.getAdresa(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.codiPostal") + 
					"\t\t\t"  + Objects.toString(representant.getCodiPostal(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.email") +
					"\t\t"+ Objects.toString(representant.getEmail(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.telefon") + 
					"\t\t\t\t" + Objects.toString(representant.getTelefon(), "") + "\n"+
					"\t"+ messageHelper.getMessage("registre.interessat.detalls.camp.emailHabilitat") + 
					"\t" + Objects.toString(representant.getEmailHabilitat(), "") + "\n"+
					"\t"+ messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent") +
					"\t\t\t"+ (representant.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(representant.getCanalPreferent(), "")))+ "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.observacions") + 
					"\t\t\t"  + Objects.toString(representant.getObservacions(), "") + "\n";											
			}
			String interesatTitle = "";
			if (interessat.getTipus().equals("PERSONA_FIS")) {
				String interessatLlinatge2 = interessat.getLlinatge2() != null ? interessat.getLlinatge2() : "";
				interesatTitle = interessat.getNom()+" "+interessat.getLlinatge1()+" "+ interessatLlinatge2;
			} else {
				interesatTitle = interessat.getRaoSocial();
			}
			plainTextInteressats += 
					interesatTitle.toUpperCase()+"\n"+
					"--------------------------------------------------------------------------------\n"+
					"\t"+ messageHelper.getMessage("registre.detalls.camp.interessat.tipus") +
					"\t\t\t\t"+ (interessat.getTipus()==null ? "": messageHelper.getMessage("registre.interessat.tipus.enum." + Objects.toString(interessat.getTipus(), "")))+ "\n"+
					"\t"+ messageHelper.getMessage("registre.detalls.camp.interessat.document") +
					"\t\t\t"+ Objects.toString(interessat.getDocumentTipus(), "")+": "+interessat.getDocumentNum() + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.pais") + 
					"\t\t\t\t" + Objects.toString(interessat.getPais(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.provincia") + 
					"\t\t\t" + Objects.toString(interessat.getProvincia(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.municipi") + 
					"\t\t\t"  + Objects.toString(interessat.getMunicipi(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.adresa") + 
					"\t\t\t\t" + Objects.toString(interessat.getAdresa(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.codiPostal") + 
					"\t\t\t"  + Objects.toString(interessat.getCodiPostal(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.email") +
					"\t\t"+ Objects.toString(interessat.getEmail(), "") + "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.telefon") + 
					"\t\t\t\t" + Objects.toString(interessat.getTelefon(), "") + "\n"+
					"\t"+ messageHelper.getMessage("registre.interessat.detalls.camp.emailHabilitat") + 
					"\t" + Objects.toString(interessat.getEmailHabilitat(), "") + "\n"+
					"\t"+ messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent") +
					"\t\t\t"+ (interessat.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(interessat.getCanalPreferent(), "")))+ "\n"+
					"\t"+ messageHelper.getMessage("interessat.form.camp.observacions") + 
					"\t\t\t"  + Objects.toString(interessat.getObservacions(), "") + "\n"+					
					plainTextRepresentant;
		}
		
		return plainTextInteressats;
	}	
	

private String getPlainTextJustificant(RegistreDto registre, Object justificantDataCaptura, RegistreAnnexDto justificant) {
	String plainTextJustificant=""; 
	if (registre.getJustificantArxiuUuid()!=null && !registre.getJustificantArxiuUuid().isEmpty()) {
		plainTextJustificant = 
					"\n"+
					messageHelper.getMessage("registre.detalls.titol.justificant").toUpperCase()+"\n"+
					"================================================================================\n"+			
					"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.data.captura") +
					"\t\t"+ justificantDataCaptura + "\n"+
					"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.origen") +
					"\t\t\t"+ Objects.toString(justificant.getOrigenCiutadaAdmin(), "") + "\n"+
					"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.estat.elaboracio") + 
					"\t" + (justificant.getNtiElaboracioEstat()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiElaboracioEstat." + Objects.toString(justificant.getNtiElaboracioEstat(), ""))) + "\n"+
					"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.tipus.documental") + 
					"\t\t" + (justificant.getNtiTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiTipusDocument." + Objects.toString(justificant.getNtiTipusDocument(), ""))) + "\n"+
					"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.sicres.tipus.document") + 
					"\t\t\t"  + (justificant.getSicresTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.sicresTipusDocument."+justificant.getSicresTipusDocument())) + "\n"+
					(justificant.getLocalitzacio() == null ? "": 
						"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.localitzacio") + 
						"\t\t\t"  + justificant.getLocalitzacio())+ 
					(justificant.getObservacions() == null ? "": 
						"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.observacions") + 
						"\t\t\t"  + justificant.getObservacions())+ 					
					"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.fitxer") + 
					"\t\t\t\t"  + Objects.toString(justificant.getFitxerNom(), "") + "("+Objects.toString(justificant.getFitxerTamany(), "")+" bytes)"+"\n";
	}
	return plainTextJustificant;
}




private String getPlaintTextAnnexos(RegistreDto registre, List<RegistreAnnexDto> anexos, SimpleDateFormat sdf) {
	
	

	String plainTextAnnexos = "";
	if (!registre.getAnnexos().isEmpty()) {
		plainTextAnnexos += 
		"\n"+
		messageHelper.getMessage("registre.detalls.pipella.annexos").toUpperCase()+"\n"+
		"================================================================================\n";
	}
	for (RegistreAnnexDto annex: anexos) {
		String plainTextFirmes = "";
		if (annex.getFirmes() != null && !annex.getFirmes().isEmpty()) {
			plainTextFirmes += 
					"\n" + 
					messageHelper.getMessage("registre.annex.detalls.camp.firmes").toUpperCase()
					+ "\n" + "---------------------------------------------------------\n";
			int i = 1;
			for (ArxiuFirmaDto firma : annex.getFirmes()) {
				String plainTextDetalls="";
				if (!firma.getDetalls().isEmpty()) {
					plainTextDetalls+=
							"\n"+
							messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls") + "\n"+							
							"----------------------------\n" +
							messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.data") + "\t\t"+ 	
							messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.nif") + "\t\t"+ 
							messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.emissor")+ "\t\t"+
							messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.nom")  
							; 
				}
				for (ArxiuFirmaDetallDto detall: firma.getDetalls()) {
					plainTextDetalls+=
							(detall.getData() == null ? messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.data.nd") : sdf.format(detall.getData())) + "\n"+"\t\t" +
							Objects.toString(detall.getResponsableNif(), "") + "\t\t"+ 
							Objects.toString(detall.getEmissorCertificat(), "") + "\t\t"+ 
							Objects.toString(detall.getResponsableNom(), "")+"\n";
				plainTextFirmes+=
						 messageHelper.getMessage("registre.annex.detalls.camp.firma") +" "+i+
								 (!firma.isAutofirma()? "": 
										"("+messageHelper.getMessage("registre.annex.detalls.camp.firma.autoFirma")+")"
									)+"\n"+ 	
						"---------------------------------------\n"+
						"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaTipus") +
						"\t\t\t" + (firma.getTipus()==null ? "": messageHelper.getMessage("document.nti.tipfir.enum." + Objects.toString(firma.getTipus(), ""))) + "\n"+
						"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaPerfil") +
						"\t\t\t"+ Objects.toString(firma.getPerfil(), "") + "\n"+
						(firma.getTipus() == ArxiuFirmaTipusEnumDto.PADES && firma.getTipus() == ArxiuFirmaTipusEnumDto.CADES_ATT && firma.getTipus() == ArxiuFirmaTipusEnumDto.XADES_ENV ? "": 
							"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.fitxer") + 
							"\t\t\t\t"+ Objects.toString(firma.getFitxerNom(), "") + "\n"
								)+ 	
						(firma.getCsvRegulacio() == null || firma.getCsvRegulacio().isEmpty() ? "": 
							"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.firmaCsvRegulacio") + 
							"\t\t\t"+ Objects.toString(firma.getCsvRegulacio(), "") + "\n"
								)+ 	
						plainTextDetalls;
					i++;
				}
			}
		}

		plainTextAnnexos += 
			annex.getTitol().toUpperCase()+"\n"+
			"--------------------------------------------------------------------------------\n"+		
			"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.data.captura") +
			"\t\t"+ (annex.getDataCaptura() == null ? "" : sdf.format(annex.getDataCaptura())) + "\n"+
			"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.origen") +
			"\t\t\t"+ Objects.toString(annex.getOrigenCiutadaAdmin(), "") + "\n"+
			"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.estat.elaboracio") + 
			"\t" + (annex.getNtiElaboracioEstat()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiElaboracioEstat." + Objects.toString(annex.getNtiElaboracioEstat(), ""))) + "\n"+
			"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.eni.tipus.documental") + 
			"\t\t" + (annex.getNtiTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiTipusDocument." + Objects.toString(annex.getNtiTipusDocument(), ""))) + "\n"+
			"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.sicres.tipus.document") + 
			"\t"  + (annex.getSicresTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.sicresTipusDocument."+annex.getSicresTipusDocument())) + "\n"+
			(annex.getLocalitzacio() == null ? "": 
				"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.localitzacio") + 
				"\t\t\t"  + annex.getLocalitzacio()
					)+ 
			(annex.getObservacions() == null ? "": 
				"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.observacions") + 
				"\t\t\t"  + annex.getObservacions()
					)+ 					
			"\t"+ messageHelper.getMessage("registre.annex.detalls.camp.fitxer") + 
			"\t\t\t\t"  + Objects.toString(annex.getFitxerNom(), "") + "("+Objects.toString(annex.getFitxerTamany(), "")+" bytes)"+"\n"+
				plainTextFirmes+
			"";
	}
	
	return plainTextAnnexos;
	
}



private String getPlainText(RegistreDto registre, Object registreData, Object registreDataOrigen, Object registreCreatedDate, String message18nRegistreTipus, String plainTextInteressats, String plainTextJustificant, String plainTextAnnexos) {
	
	String plainText = 
			"ANOTACIÓ DE REGISTRE \n"+
			"================================================================================\n"+
			"\n"+
			"\t"+messageHelper.getMessage("registre.detalls.camp.tipus")+
			"\t\t\t\t"+ messageHelper.getMessage("registre.anotacio.tipus.enum." + Objects.toString(registre.getRegistreTipus(), ""))+"\n"+
			"\t"+messageHelper.getMessage("registre.detalls.camp.numero")+
			"\t\t\t\t"+Objects.toString(registre.getNumero(), "")+"\n"+
			"\t"+messageHelper.getMessage("registre.detalls.camp.data")+
			"\t\t\t\t"+registreData+"\n"+
			"\t"+messageHelper.getMessage("registre.detalls.camp.oficina") + 
			"\t\t\t\t"+Objects.toString(registre.getOficinaDescripcio(), "") + " (" + Objects.toString(registre.getOficinaCodi(), "") + ")" +"\n"+			
			"\n"+
			messageHelper.getMessage("registre.detalls.titol.obligatories").toUpperCase()+"\n"+
			"================================================================================\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.llibre") + 
			"\t\t\t\t" + Objects.toString(registre.getLlibreDescripcio(), "") + " (" + Objects.toString(registre.getLlibreCodi(), "") + ")" + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.extracte") + 
			"\t\t\t" + Objects.toString(registre.getExtracte(), "") + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.docfis") + 
			"\t\t" + Objects.toString(registre.getDocumentacioFisicaCodi(), "")  + "-" + Objects.toString(registre.getDocumentacioFisicaDescripcio(), "") + "\n"+
			"\t" + message18nRegistreTipus +
			"\t\t\t" + Objects.toString(registre.getUnitatAdministrativaDescripcio(), "") + " (" + Objects.toString(registre.getUnitatAdministrativa(), "") + ")" + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.assumpte.tipus") + 
			"\t\t" + Objects.toString(registre.getAssumpteTipusDescripcio(), "") + " (" + Objects.toString(registre.getAssumpteTipusCodi(), "") + ")" + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.idioma") + 
			"\t\t\t\t" + Objects.toString(registre.getIdiomaDescripcio(), "") + " (" + Objects.toString(registre.getIdiomaCodi(), "") + ")" +"\n"+
			"\n"+
			messageHelper.getMessage("registre.detalls.titol.opcionals").toUpperCase()+"\n"+
			"================================================================================\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.assumpte.codi") + 
			"\t\t\t" + Objects.toString(registre.getAssumpteCodi(), "") + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.refext") + 
			"\t\t\t" + Objects.toString(registre.getReferencia(), "") + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.numexp") + 
			"\t\t\t" + Objects.toString(registre.getExpedientNumero(), "") + "\n	"+
			"" + messageHelper.getMessage("registre.detalls.camp.transport.tipus") + 
			"\t\t\t" + Objects.toString(registre.getTransportTipusDescripcio(), "") + " (" + Objects.toString(registre.getTransportTipusCodi(), "") + ")" + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.transport.num") + 
			"\t\t\t" + Objects.toString(registre.getTransportNumero(), "") + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.origen.oficina") + 
			"\t\t\t" + Objects.toString(registre.getOficinaOrigenDescripcio(), "") + " (" + Objects.toString(registre.getOficinaOrigenCodi(), "") + ")" + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.origen.num") + 
			"\t\t\t" + Objects.toString(registre.getNumeroOrigen(), "") + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.origen.data") + 
			"\t\t\t" + registreDataOrigen + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.observacions") + 
			"\t\t\t" + Objects.toString(registre.getObservacions(), "") + "\n"+	
			"\n"+
			messageHelper.getMessage("registre.detalls.titol.seguiment").toUpperCase()+"\n"+
			"================================================================================\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.entitat") + 
			"\t\t\t\t" + Objects.toString(registre.getEntitatDescripcio(), "") + " (" + Objects.toString(registre.getEntitatCodi(), "") + ")" + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.aplicacio") +
			"\t\t\t" + Objects.toString(registre.getAplicacioCodi(), "") + Objects.toString(registre.getAplicacioVersio(), "")  + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.usuari") + 
			"\t\t\t\t" + Objects.toString(registre.getUsuariNom(), "") + " (" + Objects.toString(registre.getUsuariCodi(), "") + ")" + "\n"+
			"\t" + messageHelper.getMessage("registre.detalls.camp.distribucio.alta") + 
			"\t\t" + registreCreatedDate + "\n"+
			plainTextJustificant +
			plainTextInteressats +	
			plainTextAnnexos +
			"";

	return plainText;
	}







	@Override
	@Transactional
	public void updatePermis(
			Long entitatId,
			Long id,
			PermisDto permis) {
		logger.debug("Actualitzant permis per a la bústia (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ", " +
				"permis=" + permis + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		permisosHelper.updatePermis(
				id,
				BustiaEntity.class,
				permis);
		bustiaHelper.evictCountElementsPendentsBustiesUsuari(
				entitat,
				bustia);
	}



	
	
//	private BustiaContingutDto toBustiaContingutDto(
//			ContingutEntity contingut) {
//
//		Object deproxied = HibernateHelper.deproxy(contingut);
//		BustiaContingutDto bustiaContingut = new BustiaContingutDto();
//		bustiaContingut.setId(contingut.getId());
//		bustiaContingut.setNom(contingut.getNom());
//		List<ContingutDto> path = contingutHelper.getPathContingutComDto(
//				contingut,
//				false,
//				false);
//		bustiaContingut.setPath(path);
//
//		BustiaDto pare = bustiaHelper.toBustiaDto((BustiaEntity)(contingut.getPare()), false, false);
//		bustiaContingut.setPareId(pare.getId());
//		bustiaContingut.setBustiaActiva(pare.isActiva());
//
//
//		RegistreEntity registre = null;
//		if (ContingutTipusEnumDto.REGISTRE == contingut.getTipus()) {
//			registre = (RegistreEntity)contingut;

//		
//			if(registre.getProcesEstat()==RegistreProcesEstatEnum.BUSTIA_PENDENT){
//				bustiaContingut.setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto.PENDENT);
//			} else if (registre.getProcesEstat()==RegistreProcesEstatEnum.BUSTIA_PROCESSADA) { 
//				bustiaContingut.setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto.PROCESSAT);
//		}
//		}
//		if (deproxied instanceof RegistreEntity) {
//			RegistreEntity anotacio = (RegistreEntity)contingut;
//			bustiaContingut.setTipus(BustiaContingutPendentTipusEnumDto.REGISTRE);
//			bustiaContingut.setRecepcioData(anotacio.getCreatedDate().toDate());
//			if (anotacio.getProcesError() != null) {
//				bustiaContingut.setError(true);
//		}
//			bustiaContingut.setProcesAutomatic(
//					RegistreProcesEstatEnum.ARXIU_PENDENT == anotacio.getProcesEstat() || RegistreProcesEstatEnum.REGLA_PENDENT == anotacio.getProcesEstat());
//			bustiaContingut.setNumeroOrigen(anotacio.getNumeroOrigen());
//		}
//		if (contingut.getDarrerMoviment() != null) {
//			if (contingut.getDarrerMoviment().getRemitent() != null)
//				bustiaContingut.setRemitent(contingut.getDarrerMoviment().getRemitent().getNom());
//			if (contingut.getDarrerMoviment().getCreatedDate() != null)
//				bustiaContingut.setRecepcioData(contingut.getDarrerMoviment().getCreatedDate().toDate());
//			bustiaContingut.setComentari(contingut.getDarrerMoviment().getComentari());
//		}
//		bustiaContingut.setNumComentaris(contingutComentariRepository.countByContingut(contingut));
//
//		bustiaContingut.setAlerta(alertaRepository.countByLlegidaAndContingutId(
//				false,
//				contingut.getId()) > 0);
//
//		
//		return bustiaContingut;
// }
	
	private boolean isLastIteration(
			int i,
			List<BustiaEntity> bustiesDesti) {

		boolean lastIteration = false;
		if (i == bustiesDesti.size() -
				1) {
			lastIteration = true;
		}
		return lastIteration;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UnitatOrganitzativaDto> findUnitatsSuperiors(Long entitatId, String filtre) {
		
		EntitatEntity entitat = entitatRepository.findOne(entitatId);
		
		ArbreDto<UnitatOrganitzativaDto> arbre = this.getArbreUnitatsSuperiors(entitat, filtre, null);
				
		return arbre.toDadesList();
	}
	
	/** Mètode privat per retornnar un arbre amb les unitats organitzatives superiors
	 * a les bústies de l'entitat. D'aquesta forma s'obté només l'arbre amb bústies.
	 * 
	 * @param entitat Entitat amb les bústies per buscar les unititats orgàniques.
	 * @param filtre Filtre per codi o nom de les unitats orgàniques superiors de les bústies.
	 * @param codiUnitatOrganitzativa Codi del node superior. Només es retornarà l'arbre a partir
	 * del node que coincideixi amb aquest codi.
	 * @return
	 */
	private ArbreDto<UnitatOrganitzativaDto> getArbreUnitatsSuperiors(
			EntitatEntity entitat, 
			String filtre,
			String codiUnitatOrganitzativa) {
		// Recupera les diferents unitats organitzatives de les bústies de l'entorn
		List<UnitatOrganitzativaEntity> unitatsSuperiors = 
				unitatOrganitzativaRepository.findUnitatsSuperiors(
						entitat.getId(),
						filtre == null || filtre.isEmpty(),
						filtre != null ? filtre : "");
		
		// Crea una llista de codis d'UO amb bústia
		Set<String> bustiaUnitatCodis = new HashSet<String>();
		for (UnitatOrganitzativaEntity us : unitatsSuperiors) {
			bustiaUnitatCodis.add(us.getCodi());
		}
		// Consulta tot l'arbre de l'entitat filtrant per codis permesos
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatOrganitzativaHelper.findPerCodiDir3EntitatAmbCodisPermesos(
				entitat.getCodiDir3(),
				bustiaUnitatCodis);
		// Si s'ha passat un codi d'unitat orgànica superior llavors retorna l'arbre a partir del node amb codi igual
		if (codiUnitatOrganitzativa != null && !codiUnitatOrganitzativa.isEmpty()) {
			// Busca el node amb el codi seleccionat
			for (ArbreNodeDto<UnitatOrganitzativaDto> node : arbre.toList()) {
				if (node.getDades().getCodi().equals(codiUnitatOrganitzativa)) {
					arbre = new ArbreDto<UnitatOrganitzativaDto>(false);
					arbre.setArrel(node);
					break;
				}
			}
		}
		return arbre;
	}

	private static final Logger logger = LoggerFactory.getLogger(BustiaServiceImpl.class);
}
