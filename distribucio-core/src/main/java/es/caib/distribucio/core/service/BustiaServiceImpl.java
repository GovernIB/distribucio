/**
 * 
 */
package es.caib.distribucio.core.service;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.BustiaContingutDto;
import es.caib.distribucio.core.api.dto.BustiaContingutFiltreEstatEnumDto;
import es.caib.distribucio.core.api.dto.BustiaContingutPendentTipusEnumDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.BustiaUserFiltreDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PermisDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDetallDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.Firma;
import es.caib.distribucio.core.api.registre.RegistreAnnex;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ContingutLogHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.HibernateHelper;
import es.caib.distribucio.core.helper.MessageHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper.Converter;
import es.caib.distribucio.core.helper.PermisosHelper;
import es.caib.distribucio.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.core.helper.UsuariHelper;
import es.caib.distribucio.core.repository.AlertaRepository;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.ContingutComentariRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.ReglaRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
import es.caib.distribucio.plugin.registre.RegistreAnotacioResposta;


/**
 * Implementació dels mètodes per a gestionar bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class BustiaServiceImpl implements BustiaService {

	@Resource
	private BustiaRepository bustiaRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private ReglaRepository reglaRepository;
	@Resource
	private ContingutRepository contingutRepository;
	@Resource
	private ContingutComentariRepository contingutComentariRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private ContingutLogHelper contingutLogHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private BustiaHelper bustiaHelper;
	@Resource
	private EmailHelper emailHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private ReglaHelper reglaHelper;
	@Resource
	private RegistreHelper registreHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MessageHelper messageHelper;
	
	@Autowired
	private RegistreService registreService;
	@Resource
	private JavaMailSender mailSender;	

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
				false,
				false);
		// Si no hi ha cap bústia per defecte a dins l'unitat configura
		// la bústia actual com a bústia per defecte
		BustiaEntity bustiaPerDefecte = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPerDefecteTrue(
				entitat,
				unitat);
		if (bustiaPerDefecte == null) {
			entity.updatePerDefecte(true);
			contingutLogHelper.log(
					entity,
					LogTipusEnumDto.PER_DEFECTE,
					"true",
					null,
					false,
					false);
		}
		return toBustiaDto(
				entity,
				false,
				false);
	}

	@Override
	@Transactional
	public BustiaDto update(
			Long entitatId,
			BustiaDto bustia) {
		logger.debug("Modificant la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustia=" + bustia + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		BustiaEntity entity = entityComprovarHelper.comprovarBustia(
				entitat,
				bustia.getId(),
				false);
		String nomOriginal = entity.getNom();
		
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findOne(bustia.getUnitatOrganitzativa().getId());
		entity.update(
				bustia.getNom(),
				unitatOrganitzativa);

		
		// Registra al log la modificació de la bústia
		contingutLogHelper.log(
				entity,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(entity.getNom())) ? entity.getNom() : null,
				null,
				false,
				false);
		return toBustiaDto(
				entity,
				false,
				false);
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
		contingutLogHelper.log(
				entity,
				activa ? LogTipusEnumDto.ACTIVACIO : LogTipusEnumDto.DESACTIVACIO,
				null,
				false,
				false);
		return toBustiaDto(
				entity,
				false,
				false);
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
			BustiaEntity bustiaPerDefecteAlternativa = null;
			List<UnitatOrganitzativaDto> path = unitatOrganitzativaHelper.findPath(
					entitat.getUnitatArrel(),
					bustia.getUnitatCodi());
			if (path != null && !path.isEmpty()) {
				BustiaEntity bustiaAux;
				for (UnitatOrganitzativaDto unitat: path) {
					bustiaAux = bustiaRepository.findByEntitatAndUnitatCodiAndPerDefecteTrue(
							entitat,
							unitat.getCodi());
					if (bustiaAux != null && ! bustiaAux.getId().equals(id)) {
						bustiaPerDefecteAlternativa = bustia;
						break;
					}
				}
				if (bustiaPerDefecteAlternativa == null) {
					String missatgeError = "No es pot esborrar la bústia per defecte si no n'hi ha cap altra superior definida per defecte (" +
							"bustiaId=" + id + ", " +
							"unitatOrganitzativaCodi=" + bustia.getUnitatCodi() + ")";
					logger.error(missatgeError);
					throw new ValidationException(
							id,
							BustiaEntity.class,
							missatgeError);
				}			
			}
		}
		
		bustiaRepository.delete(bustia);

		return toBustiaDto(
				bustia,
				false,
				false);
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
				contingutLogHelper.log(
						bu,
						LogTipusEnumDto.PER_DEFECTE,
						"false",
						null,
						false,
						false);
			}
			bu.updatePerDefecte(false);
		}
		// Registra al log la modificació de la bústia
		contingutLogHelper.log(
				bustia,
				LogTipusEnumDto.PER_DEFECTE,
				"true",
				null,
				false,
				false);
		bustia.updatePerDefecte(true);
		return toBustiaDto(
				bustia,
				false,
				false);
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
		BustiaDto resposta = toBustiaDto(
				bustia,
				false,
				false);
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
		List<BustiaDto> resposta = toBustiaDto(
				busties,
				false,
				false);
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
		
		PaginaDto<BustiaDto> resultPagina =  paginacioHelper.toPaginaDto(
				bustiaRepository.findByEntitatAndUnitatAndBustiaNomAndPareNotNullFiltrePaginat(
						entitat,
						filtre.getUnitatId() == null, 
						unitat,
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom(),
						filtre.getUnitatObsoleta() == null || filtre.getUnitatObsoleta() == false,
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio)),
				BustiaDto.class,
				new Converter<BustiaEntity, BustiaDto>() {
					@Override
					public BustiaDto convert(BustiaEntity source) {
						return toBustiaDto(
								source,
								false,
								true);
					}
				});
		omplirPermisosPerBusties(resultPagina.getContingut(), true);
		return resultPagina;
	}

	@Override
	@Transactional
	public List<BustiaDto> findActivesAmbEntitat(
			Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Cercant bústies actives de l'entitat ("
				+ "entitatId=" + entitatId + ", "
				+ "usuariCodi=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNull(entitat);
		return toBustiaDto(
				busties,
				false,
				false);
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
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndPareNotNull(entitat);
		return toBustiaDto(
				busties,
				false,
				false);
	}
	
	@Override
	@Transactional
	public List<BustiaDto> findAmbEntitatAndFiltre(
			Long entitatId, 
			String bustiaNomFiltre,
			Long unitatIdFiltre) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Cercant bústies de l'entitat ("
				+ "entitatId=" + entitatId + ", "
				+ "usuariCodi=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		
		UnitatOrganitzativaEntity unitat = unitatIdFiltre != null ? unitatOrganitzativaRepository.findOne(unitatIdFiltre): null;
		
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndUnitatAndBustiaNomAndPareNotNullFiltre(
				entitat,
				unitatIdFiltre == null, 
				unitat,
				bustiaNomFiltre == null || bustiaNomFiltre.isEmpty(), 
				bustiaNomFiltre);
		
		return toBustiaDto(
				busties,
				false,
				false);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<BustiaDto> findPermesesPerUsuari(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de busties permeses per un usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Obté la llista d'id's amb permisos per a l'usuari
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNull(entitat);
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
		if (busties.isEmpty()) {
			return paginacioHelper.getPaginaDtoBuida(BustiaDto.class);
		}
		List<Long> bustiaIds = new ArrayList<Long>();
		for (BustiaEntity bustia: busties) {
			bustiaIds.add(bustia.getId());
		}
		// Realitza la consulta
		Page<BustiaEntity> pagina = bustiaRepository.findByEntitatAndIdsAndFiltrePaginat(
				entitat,
				bustiaIds,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(
						paginacioParams));
		return paginacioHelper.toPaginaDto(
				pagina,
				BustiaDto.class,
				new Converter<BustiaEntity, BustiaDto>() {
					@Override
					public BustiaDto convert(BustiaEntity source) {
						return toBustiaDto(
								source,
								true,
								true);
					}
				});
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findPermesesPerUsuari(
			Long entitatId) {
		logger.debug("Consulta de busties permeses per un usuari ("
				+ "entitatId=" + entitatId + ")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Obté la llista d'id's amb permisos per a l'usuari
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNull(entitat);
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
		List<BustiaDto> bustiesRetorn = toBustiaDto(busties, true, true);
		
		return bustiesRetorn;
	}

	@Transactional
	@Override
	public ContingutDto enviarContingut(
			Long entitatId,
			Long bustiaId,
			Long contingutId,
			String comentari) {
		logger.debug("Enviant contingut a bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "contingutId=" + contingutId + ","
				+ "comentari=" + comentari + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		// Comprova l'accés al path del contingutOrigen
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				true,
				false,
				false,
				true);
		// Comprova la bústia
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		// Fa l'enviament
		ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
				contingut,
				bustia,
				comentari);
		// Registra al log l'enviament del contingut
		contingutLogHelper.log(
				contingut,
				LogTipusEnumDto.ENVIAMENT,
				contingutMoviment,
				true,
				true);
//		// Avisam per correu als responsables de la bústia de destí
		emailHelper.emailBustiaPendentContingut(
				bustia,
				contingut,
				contingutMoviment);
		// Refrescam cache usuaris bústia de destí
		bustiaHelper.evictElementsPendentsBustia(
				entitat,
				bustia);
		return contingutHelper.toContingutDto(
				contingut,
				true,
				false,
				false,
				false,
				false,
				false,
				false);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public Long registreAnotacioCrear(
			String entitatUnitatCodi,
			RegistreTipusEnum tipus,
			String unitatOrganitzativa,
			RegistreAnotacio anotacio) {
		logger.debug("Creant anotació de registre a la bústia ("
				+ "entitatUnitatCodi=" + entitatUnitatCodi + ", "
				+ "tipus=" + tipus + ", "
				+ "unitatOrganitzativa=" + unitatOrganitzativa + ","
				+ "anotacio=" + anotacio.getIdentificador() + ")");
		
		Long idAnotacioRetornada = null;
		
		EntitatEntity entitatPerUnitat = entitatRepository.findByUnitatArrel(entitatUnitatCodi);
		if (entitatPerUnitat == null) {
			throw new NotFoundException(
					
					entitatUnitatCodi,
					EntitatEntity.class);
		}
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatPerUnitat.getId(),
				false,
				false,
				false);
		BustiaEntity bustia = bustiaHelper.findBustiaDesti(
				entitat,
				unitatOrganitzativa);
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
		
		ReglaEntity reglaAplicable = reglaHelper.findAplicable(
				entitat,
				unitatOrganitzativa,
				anotacio);
		RegistreEntity anotacioEntity = registreHelper.toRegistreEntity(
				entitat,
				tipus,
				unitatOrganitzativa,
				anotacio,
				reglaAplicable);
		
		Boolean isDistribucioAsincrona = "true".equalsIgnoreCase(PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.dist.anotacio.asincrona"));
		
		if (anotacioEntity.getProcesEstat() == RegistreProcesEstatEnum.NO_PROCES) {
			anotacioEntity.updateProces(
					anotacioEntity.getData(), 
					RegistreProcesEstatEnum.PROCESSAT, 
					null);

		} else if (isDistribucioAsincrona) {
			guardarFitxersGesDoc(anotacioEntity, anotacio);
		} else {
			guardarFitxersGesDoc(anotacioEntity, anotacio);
			registreRepository.saveAndFlush(anotacioEntity);
			idAnotacioRetornada = anotacioEntity.getId();
		}
		
		registreRepository.saveAndFlush(anotacioEntity);
		
		contingutLogHelper.log(
				anotacioEntity,
				LogTipusEnumDto.CREACIO,
				anotacioEntity.getNom(),
				null,
				false,
				false);
		contingutHelper.ferIEnregistrarMoviment(
				anotacioEntity,
				bustia,
				null);
		bustiaHelper.evictElementsPendentsBustia(
				bustia.getEntitat(),
				bustia);
		
		return idAnotacioRetornada;
	}

	@Transactional
	@Override
	public void registreAnotacioCrear(
			String entitatUnitatCodi,
			String registreReferencia) {
		RegistreAnotacioResposta resposta = pluginHelper.registreEntradaConsultar(
				registreReferencia,
				entitatUnitatCodi);
		registreAnotacioCrear(
				entitatUnitatCodi,
				resposta.getTipus(),
				resposta.getUnitatAdministrativa(),
				resposta.getRegistreAnotacio());
	}

	
	
	@Transactional
	@Override
	public void enviarRegistreByEmail(
			Long entitatId,
			Long contingutId,
			Long registreId, 
			String adresses,
			String serverPortContext) throws MessagingException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		RegistreAnotacioDto registre = registreService.findOne(
				entitatId,
				contingutId,
				registreId);
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		
		RegistreEntity registreEntity = registreRepository.findByPareAndId(
				contingut,
				registreId);
		
		
		RegistreAnnexDetallDto justificant = null;
		if(registre.getJustificantArxiuUuid()!=null && !registre.getJustificantArxiuUuid().isEmpty()){
			justificant = registreService.getRegistreJustificant(
					entitatId,
					contingutId, 
					registreId);
		}

		
		List<RegistreAnnexDetallDto> anexos = registreService.getAnnexosAmbArxiu(
				entitatId,
				contingutId,
				registreId);
		
//		List<RegistreAnnexDetallDto> anexos = registreService.getAnnexos(
//				entitatId,
//				contingutId,
//				registreId);
		

		MimeMessage missatge = mailSender.createMimeMessage();
		
		missatge.setHeader("Content-Type", "text/html charset=UTF-8");

		MimeMessageHelper helper;
		helper = new MimeMessageHelper(missatge, true);
		
//		if (adresses == null && adresses.isEmpty()){
		missatge.addRecipients(RecipientType.TO, InternetAddress.parse(adresses));
//		} else {
//			helper.setTo("urszulal@limit.es");
//		}

		helper.setFrom(emailHelper.getRemitent());
		helper.setSubject("Distribució: "+registre.getExtracte());
		
		messageHelper.getMessage("registre.detalls.camp.tipus");
		
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
		if(justificant!=null){
			justificantDataCaptura = justificant.getDataCaptura() == null ? "" : sdf.format(justificant.getDataCaptura());	
		}
		
		String htmlJustificant="";
		if(registre.getJustificantArxiuUuid()!=null && !registre.getJustificantArxiuUuid().isEmpty()){
			
			htmlJustificant = 
					
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
							"			<tr>"+
							"				<th>"+ messageHelper.getMessage("registre.annex.detalls.camp.sicres.tipus.document") + "</th>"+
							"				<td>"  + (justificant.getSicresTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.sicresTipusDocument."+justificant.getSicresTipusDocument())) + "</td>"+
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
							"				<td>"  + Objects.toString(justificant.getFitxerNom(), "") + "("+Objects.toString(justificant.getFitxerTamany(), "")+" bytes)"+"</td>"+
							"			</tr>"+								
							"		</table>";

		}
		
		String htmlAnnexos = "";
		for(RegistreAnnexDetallDto annex: anexos){
			
			
			
			
			String htmlFirmes="";
			if(!annex.getFirmes().isEmpty()){
				
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
					
					"<a href=\"http://"+serverPortContext+"/modal/contingut/"+contingutId+"/registre/"+registreId+"/annex/"+annex.getId()+"/arxiu/DOCUMENT\"> Descarregar </a>"
					+
					"</td>"+
					"			</tr>"+
					
					htmlFirmes+
"";
		}
		
		String htmlAnnexosTable="";
		if(!registre.getAnnexos().isEmpty()){
			
			htmlAnnexosTable = 
					
					
							"		<table>"+
							"			<tr>"+
							"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.pipella.annexos") + "</th>"+
							"			</tr>"+
							htmlAnnexos +
							"		</table>";
		}
		

		
		

		String htmlInteressats = "";
		for(RegistreInteressat interessat: registre.getInteressats()){
			
			RegistreInteressat representant = interessat.getRepresentant();
			
			String htmlRepresentant = "";
			
			if(representant!=null){
				
				String representantTitle="";
				if(representant.getTipus().equals("PERSONA_FIS")){
					representantTitle = representant.getNom()+" "+representant.getLlinatge1()+" "+representant.getLlinatge2();
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
					"				<td>" + Objects.toString(representant.getPais(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.provincia") + "</th>"+
					"				<td>" + Objects.toString(representant.getProvincia(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.municipi") + "</th>"+
					"				<td>"  + Objects.toString(representant.getMunicipi(), "") + "</td>"+
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
					"				<td>"+ (representant.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(interessat.getCanalPreferent(), "")))+ "</td>"+
					"			</tr>"+					
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.observacions") + "</th>"+
					"				<td>"  + Objects.toString(representant.getObservacions(), "") + "</td>"+
					"			</tr>";											
			}
			
			
			String interesatTitle="";
			if(interessat.getTipus().equals("PERSONA_FIS")){
				interesatTitle = interessat.getNom()+" "+interessat.getLlinatge1()+" "+interessat.getLlinatge2();
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
					"				<td>" + Objects.toString(interessat.getPais(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.provincia") + "</th>"+
					"				<td>" + Objects.toString(interessat.getProvincia(), "") + "</td>"+
					"			</tr>"+
					"			<tr>"+
					"				<th>"+ messageHelper.getMessage("interessat.form.camp.municipi") + "</th>"+
					"				<td>"  + Objects.toString(interessat.getMunicipi(), "") + "</td>"+
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
		
		String htmlInteressatsTable="";
		if(!registre.getInteressats().isEmpty()){
			
			htmlInteressatsTable = 
					
					
							"		<table>"+
							"			<tr>"+
							"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.pipella.interessats") + "</th>"+
							"			</tr>"+
							htmlInteressats +
							"		</table>";
		}		
		
		String htmlText = 
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
				"	<div class=\"content\">"+
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
				"				<th>"+ messageHelper.getMessage("registre.detalls.camp.oficina") + "</th>"+
				"				<td>" + Objects.toString(registre.getOficinaDescripcio(), "") + " (" + Objects.toString(registre.getOficinaCodi(), "") + ")" + "</td>"+
				"			</tr>"+
				"		</table>"+

				"		<table>"+
				"			<tr>"+
				"				<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.titol.obligatories") + "</th>"+
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

		
		
		String plainTextInteressats = "";
		if(!registre.getInteressats().isEmpty()){
			
			plainTextInteressats=
				"\n"+
				messageHelper.getMessage("registre.detalls.pipella.interessats").toUpperCase()+"\n"+
				"================================================================================\n";
		}
		
		
		for(RegistreInteressat interessat: registre.getInteressats()){
			
			RegistreInteressat representant = interessat.getRepresentant();
			
			String plainTextRepresentant = "";
			
			if(representant!=null){
				
				String representantTitle="";
				if(representant.getTipus().equals("PERSONA_FIS")){
					representantTitle = representant.getNom()+" "+representant.getLlinatge1()+" "+representant.getLlinatge2();
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
					"\t\t\t"+ (representant.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(interessat.getCanalPreferent(), "")))+ "\n"+
					
					
					"\t"+ messageHelper.getMessage("interessat.form.camp.observacions") + 
					"\t\t\t"  + Objects.toString(representant.getObservacions(), "") + "\n";											
			}
			
			
			String interesatTitle="";
			if(interessat.getTipus().equals("PERSONA_FIS")){
				interesatTitle = interessat.getNom()+" "+interessat.getLlinatge1()+" "+interessat.getLlinatge2();
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
		
		String plainTextJustificant=""; 

		String plainTextAnnexos = "";
		
		if(!registre.getAnnexos().isEmpty()){
			
			plainTextAnnexos += 
			"\n"+
			messageHelper.getMessage("registre.detalls.pipella.annexos").toUpperCase()+"\n"+
			"================================================================================\n";

		}
		
		for(RegistreAnnexDetallDto annex: anexos){
			
			
			
			
			
			
			String plainTextFirmes = "";
			if (!annex.getFirmes().isEmpty()) {

				plainTextFirmes += 
						"\n" + 
						messageHelper.getMessage("registre.annex.detalls.camp.firmes").toUpperCase()
						+ "\n" + "---------------------------------------------------------\n";

				int i = 1;
				for (ArxiuFirmaDto firma : annex.getFirmes()) {
			
					
					String plainTextDetalls="";
					if(!firma.getDetalls().isEmpty()){
						
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
					
					for(ArxiuFirmaDetallDto detall: firma.getDetalls()){
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
			
			
			
			if(registre.getJustificantArxiuUuid()!=null && !registre.getJustificantArxiuUuid().isEmpty()){
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
		
		helper.setText(plainText, htmlText);
		mailSender.send(missatge);
	
		
		String logTo = "Destinitaris: " +adresses;
				
		
		contingutLogHelper.log(
				registreEntity,
				LogTipusEnumDto.ENVIAMENT_EMAIL,
				registreEntity.getNom(),
				logTo,
				false,
				false);
		
		
		
		
	}
	
	
		
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<BustiaContingutDto> contingutPendentFindByDatatable(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			BustiaUserFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant el contingut de l'usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + filtre.getBustia() + ", "
				+ "contingutDescripcio=" + filtre.getContingutDescripcio() + ", "
				+ "remitent=" + filtre.getRemitent() + ", "
				+ "dataRecepcioInici=" + filtre.getDataRecepcioInici() + ", "
				+ "dataRecepcioFi=" + filtre.getDataRecepcioFi() + ", "
				+ "estatContingut=" + filtre.getEstatContingut() + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Comprova la bústia i que l'usuari hi tengui accés
		BustiaEntity bustia = null;
		if (filtre.getBustia() != null && !filtre.getBustia().isEmpty())
			bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustia()),
					true);
		List<ContingutEntity> pares = new ArrayList<ContingutEntity>();
		if (bustiesUsuari != null && !bustiesUsuari.isEmpty()) {
			for (BustiaDto bustiaUsuari: bustiesUsuari) {
				pares.add(
						entityComprovarHelper.comprovarBustia(
						entitat,
						new Long(bustiaUsuari.getId()),
						true));
			}
		} else if (bustia != null) {
			pares.add(bustia);
		}
		Map<String, String[]> mapeigOrdenacio = new HashMap<String, String[]>();
		mapeigOrdenacio.put(
				"recepcioData",
				new String[] {"darrerMoviment.createdDate"});
		mapeigOrdenacio.put(
				"remitent",
				new String[] {"darrerMoviment.remitent.nom"});
		mapeigOrdenacio.put(
				"comentari",
				new String[] {"darrerMoviment.comentari"});
		
		Page<ContingutEntity> pagina;
		
		// Hibernate doesn't support empty collection as parameter so if pares is empty we dont make query but just create a new empty page 
		if (bustia == null && pares.isEmpty()) {
			pagina = new PageImpl<ContingutEntity>(new ArrayList<ContingutEntity>());
		} else {
			pagina = contingutRepository.findBustiaPendentByPareAndFiltre(
					(bustia == null),
					bustia,
					pares,
					filtre.getContingutDescripcio() == null || filtre.getContingutDescripcio().isEmpty(),
					filtre.getContingutDescripcio(),
					filtre.getRemitent() == null || filtre.getRemitent().isEmpty(),
					filtre.getRemitent(),
					(filtre.getDataRecepcioInici() == null),
					filtre.getDataRecepcioInici(),
					(filtre.getDataRecepcioFi() == null),
					filtre.getDataRecepcioFi(),
					(filtre.getEstatContingut() == null),
					(filtre.getEstatContingut() != null ? filtre.getEstatContingut().ordinal() : 1),
					paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
					paginacioParams.getFiltre(),
					paginacioHelper.toSpringDataPageable(
							paginacioParams,
							mapeigOrdenacio));
		}

		return paginacioHelper.toPaginaDto(
				pagina,
				BustiaContingutDto.class,
				new Converter<ContingutEntity, BustiaContingutDto>() {
					@Override
					public BustiaContingutDto convert(ContingutEntity source) {
						return toBustiaContingutDto(source);
					}
				});
	}

	@Transactional(readOnly = true)
	@Override
	public BustiaContingutDto contingutPendentFindOne(
			Long entitatId,
			Long bustiaId,
			Long contingutId) {
		logger.debug("Consultant un contingut pendent de la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "contingutId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Comprova la bústia i que l'usuari hi tengui accés
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		ContingutEntity contingutPendent = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				bustia);
		return toBustiaContingutDto(contingutPendent);
	}

	@Transactional(readOnly = true)
	@Override
	public long contingutPendentBustiesAllCount(
			Long entitatId) {
		logger.debug("Consultant els elements pendents a totes les busties ("
				+ "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return cacheHelper.countElementsPendentsBustiesUsuari(
				entitat,
				auth.getName());
	}

	@Transactional
	@Override
	public void contingutPendentReenviar(
			Long entitatId,
			Long bustiaOrigenId,
			Long[] bustiaDestiIds,
			Long contingutId,
			boolean deixarCopia,
			String comentari) throws NotFoundException {
		logger.debug("Reenviant contingut pendent de la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaOrigenId=" + bustiaOrigenId + ", "
				+ "bustiaDestiIds=" + bustiaDestiIds + ", "
				+ "contingutId=" + contingutId + ", "
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
			
		
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				bustiaOrigen);
		if (contingut instanceof RegistreEntity) {
			RegistreEntity registre = (RegistreEntity)contingut;
			if (registre.getRegla() != null && (RegistreProcesEstatEnum.PENDENT == registre.getProcesEstat() || RegistreProcesEstatSistraEnum.PENDENT == registre.getProcesEstatSistra())) {
				throw new ValidationException(
						contingutId,
						ContingutEntity.class,
						"Aquest contingut pendent no es pot moure perquè te activat el processament automàtic mitjançant una regla (reglaId=" + registre.getRegla().getId() + ")");
			}
		}
		
		/////////////////
		
		ContingutEntity contingutAux = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				bustiaOrigen);
		
		// Avisam per correu als responsables de la bústia de destí
		boolean primerEnviament = true;
		for (BustiaEntity bustia: bustiesDesti) {
			
			ContingutEntity contingutEnviar = null;
			
			if (!deixarCopia && primerEnviament) {
				contingutEnviar = contingut;
			} else {
				contingutEnviar = contingutHelper.ferCopiaContingut(contingutAux, bustia);
			}
			
			ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
					contingutEnviar,
					bustia,
					comentari);
			
			// Registra al log l'enviament del contingut
			contingutLogHelper.log(
					contingutEnviar,
					LogTipusEnumDto.REENVIAMENT,
					contingutMoviment,
					true,
					true);
			
			
			emailHelper.emailBustiaPendentContingut(
					bustia,
					contingutEnviar,
					contingutMoviment);
			
			bustiaHelper.evictElementsPendentsBustia(
					entitat,
					bustia);
			
			primerEnviament = false;
		}
		
		// Refrescam cache d'elements pendents de les bústies
		bustiaHelper.evictElementsPendentsBustia(
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
	
	
	@Override
	@Transactional
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(
			Long entitatId,
			String bustiaNomFiltre,
			Long unitatIdFiltre) {
		logger.debug("Consulta de l'arbre d'unitats organitzatives ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaNomFiltre=" + bustiaNomFiltre + ", "
				+ "unitatIdFiltre=" + unitatIdFiltre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		return bustiaHelper.findArbreUnitatsOrganitzativesAmbFiltre(
				entitat,
				bustiaNomFiltre,
				unitatIdFiltre);
	}
	

	@Override
	@Transactional
	public void updatePermis(
			Long entitatId,
			Long id,
			PermisDto permis) {
		logger.debug("Actualitzant permis per a la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
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
		bustiaHelper.evictElementsPendentsBustia(
				entitat,
				bustia);
	}

	@Override
	@Transactional
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) {
		logger.debug("Esborrant permis per a la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
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
		bustiaHelper.evictElementsPendentsBustia(
				entitat,
				bustia);
	}



	private BustiaDto toBustiaDto(
			BustiaEntity bustia,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead) {
		return (BustiaDto)contingutHelper.toContingutDto(
				bustia,
				false,
				ambFills,
				filtrarFillsSegonsPermisRead,
				false,
				true,
				false,
				false);
	}
	private List<BustiaDto> toBustiaDto(
			List<BustiaEntity> busties,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead) {
		List<BustiaDto> resposta = new ArrayList<BustiaDto>();
		for (BustiaEntity bustia: busties) {
			resposta.add(
					toBustiaDto(
							bustia,
							ambFills,
							filtrarFillsSegonsPermisRead));
		}
		return resposta;
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

	private BustiaContingutDto toBustiaContingutDto(
			ContingutEntity contingut) {
		Object deproxied = HibernateHelper.deproxy(contingut);
		BustiaContingutDto bustiaContingut = new BustiaContingutDto();
		bustiaContingut.setId(contingut.getId());
		bustiaContingut.setNom(contingut.getNom());
		
		List<ContingutDto> path = contingutHelper.getPathContingutComDto(
				contingut,
				false,
				false);
		
		bustiaContingut.setPath(path);
		
		BustiaDto pare = toBustiaDto((BustiaEntity)(contingut.getPare()), false, false);
		bustiaContingut.setPareId(pare.getId());
		if (contingut.getEsborrat() < 2)
			bustiaContingut.setEstatContingut(BustiaContingutFiltreEstatEnumDto.values()[contingut.getEsborrat()]);
		if (deproxied instanceof RegistreEntity) {
			RegistreEntity anotacio = (RegistreEntity)contingut;
			bustiaContingut.setTipus(BustiaContingutPendentTipusEnumDto.REGISTRE);
			bustiaContingut.setRecepcioData(anotacio.getCreatedDate().toDate());
			if (RegistreProcesEstatEnum.ERROR.equals(anotacio.getProcesEstat())) {
				bustiaContingut.setError(true);
			}
			bustiaContingut.setProcesAutomatic(anotacio.getRegla() != null && (RegistreProcesEstatEnum.PENDENT == anotacio.getProcesEstat() || RegistreProcesEstatSistraEnum.PENDENT == anotacio.getProcesEstatSistra()));
		}
		if (contingut.getDarrerMoviment() != null) {
			if (contingut.getDarrerMoviment().getRemitent() != null)
				bustiaContingut.setRemitent(contingut.getDarrerMoviment().getRemitent().getNom());
			if (contingut.getDarrerMoviment().getCreatedDate() != null)
				bustiaContingut.setRecepcioData(contingut.getDarrerMoviment().getCreatedDate().toDate());
			bustiaContingut.setComentari(contingut.getDarrerMoviment().getComentari());
		}
		
		bustiaContingut.setNumComentaris(contingutComentariRepository.countByContingut(contingut));
				
		bustiaContingut.setAlerta(alertaRepository.countByLlegidaAndContingutId(
				false,
				contingut.getId()) > 0);
		
		return bustiaContingut;
	}

	private void guardarFitxersGesDoc(
			RegistreEntity anotacioEntity, 
			RegistreAnotacio anotacio) {
		if (anotacioEntity.getAnnexos() != null && anotacioEntity.getAnnexos().size() > 0) {
			for (int i = 0; i < anotacioEntity.getAnnexos().size(); i++) {
				RegistreAnnexEntity annexEntity = anotacioEntity.getAnnexos().get(i);
				RegistreAnnex annex = anotacio.getAnnexos().get(i);
				if (annex.getFitxerContingut() != null && annex.getFitxerContingut().length > 0) {
					annexEntity.updateGesdocDocumentId(pluginHelper.gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, 
							new ByteArrayInputStream(annex.getFitxerContingut())));
				}
				
				for (int j = 0; j < annexEntity.getFirmes().size(); j++) {
					RegistreAnnexFirmaEntity firmaEntity = annexEntity.getFirmes().get(j);
					Firma firma = annex.getFirmes().get(j);
					if (firma.getContingut() != null && firma.getContingut().length > 0) {
						firmaEntity.updateGesdocFirmaId(pluginHelper.gestioDocumentalCreate(
								PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, 
								new ByteArrayInputStream(firma.getContingut())));
					}
				}
			}
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BustiaServiceImpl.class);

}
