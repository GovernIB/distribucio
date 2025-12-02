/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import es.caib.distribucio.persist.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.logic.helper.BustiaHelper;
import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ContingutHelper;
import es.caib.distribucio.logic.helper.ContingutLogHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EmailHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.MessageHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper.Converter;
import es.caib.distribucio.logic.helper.PermisosHelper;
import es.caib.distribucio.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.logic.helper.RegistreHelper;
import es.caib.distribucio.logic.helper.ReglaHelper;
import es.caib.distribucio.logic.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.logic.helper.UsuariHelper;
import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.BustiaContingutDto;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.BustiaFiltreDto;
import es.caib.distribucio.logic.intf.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UsuariBustiaFavoritDto;
import es.caib.distribucio.logic.intf.dto.UsuariPermisDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.BustiaDadesObertesDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.UsuariDadesObertesDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreInteressat;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.permission.ExtendedPermission;
import es.caib.distribucio.persist.repository.BustiaDefaultRepository;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.ContingutComentariRepository;
import es.caib.distribucio.persist.repository.ContingutMovimentRepository;
import es.caib.distribucio.persist.repository.ContingutRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.persist.repository.ReglaRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.persist.repository.UsuariBustiaFavoritRepository;
import es.caib.distribucio.persist.repository.UsuariRepository;
import es.caib.distribucio.persist.repository.VistaMovimentRepository;
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
	private UsuariHelper usuariHelper;
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
	@Autowired
	private UsuariBustiaFavoritRepository usuariBustiaFavoritRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private VistaMovimentRepository vistaMovimentRepository;
	@Autowired
	private BustiaDefaultRepository bustiaDefaultRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@Autowired
	private ConfigHelper configHelper;
	
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
		UnitatOrganitzativaEntity unitat = unitatOrganitzativaRepository.findById(bustia.getUnitatOrganitzativa().getId()).orElse(null);
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
		UnitatOrganitzativaEntity unitatOrganitzativaDesti = unitatOrganitzativaRepository.getReferenceById(bustiaModifications.getUnitatOrganitzativa().getId());
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
		
		// Cerca la bústia superior
		BustiaEntity bustiaPare = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNull(
				entitat,
				unitatOrganitzativaDesti);
		// Si la bústia superior no existeix la crea
		if (bustiaPare == null) {
			bustiaPare = bustiaRepository.save(
					BustiaEntity.getBuilder(
							entitat,
							unitatOrganitzativaDesti.getDenominacio(),
							unitatOrganitzativaDesti.getCodi(),
							unitatOrganitzativaDesti,
							null).build());
		}
		bustiaOriginal.updatePare(bustiaPare);
		
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
	public List<UsuariPermisDto> getUsuarisPerBustia(Long bustiaId){
		return new ArrayList<>(this.getUsuarisPerBustia(bustiaId, true, true).values());
	}
			
	@Override
	public Map<String, UsuariPermisDto> getUsuarisPerBustia(Long bustiaId, boolean directe, boolean perRol){
		BustiaEntity bustiaEntity = bustiaRepository.getReferenceById(bustiaId);
		return contingutHelper.findUsuarisAmbPermisReadPerContenidor(bustiaEntity, directe, perRol);
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
		
		// Comprovar que hi ha una bústia al mateix nivell o a la UO superior activa
		try {
			bustiaHelper.findBustiaDesti(entitat, entity.getUnitatOrganitzativa().getCodi());
		} catch (ValidationException e) {
			throw new RuntimeException(messageHelper.getMessage("bustia.service.desactivar.perdefecte.error", new Object[] {entity.getNom()}));
		}
		
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
		
		// Esborra les relacions com a favorita d'usuaris per evitar errors de relació
		List<UsuariBustiaFavoritEntity> usuariBustiaFavoritEntity = usuariBustiaFavoritRepository.findByBustia(bustia.getId());
		for (UsuariBustiaFavoritEntity usuariBustiaFavorit : usuariBustiaFavoritEntity) {
			usuariBustiaFavoritRepository.deleteById(usuariBustiaFavorit.getId());
		}

		// Posa a null la bústia per defecte pels usuaris
		List<BustiaDefaultEntity> bustiesDefault  = bustiaDefaultRepository.findByBustia(bustia);
		for (BustiaDefaultEntity bustiaDefault : bustiesDefault) {
			bustiaDefaultRepository.delete(bustiaDefault);
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
		logger.trace("Cercant la bústia ("
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
		omplirPermisosPerBusties(llista, true, null);
		return resposta;
	}
	
	@Override
	@Transactional(readOnly = true)
	public BustiaDto findByIdAmbPermisosOrdenats(
			Long entitatId,
			Long id,
			PaginacioParamsDto paginacio) {
		logger.trace("Cercant la bústia ("
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
		omplirPermisosPerBusties(llista, true, paginacio);
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public BustiaDto findById(
			Long id) {
		logger.trace("Cercant la bústia (" + "id=" + id + ")");
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
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
		omplirPermisosPerBusties(llista, true, null);
		return resposta;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findAmbUnitatCodiAdmin(
			Long entitatId,
			String unitatCodi) {
		logger.trace("Cercant les bústies de la unitat per admins ("
				+ "entitatId=" + entitatId + ", "
				+ "unitatCodi=" + unitatCodi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(entitat.getCodiDir3(), unitatCodi);
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNotNull(entitat, unitatOrganitzativa);
		List<BustiaDto> resposta = bustiaHelper.toBustiaDto(
				busties,
				false,
				false,
				true);
		omplirPermisosPerBusties(resposta, false, null);
		return resposta;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findAmbUnitatId(
			Long entitatId,
			Long unitatId) {
		logger.trace("Cercant les bústies de la unitat per admins ("
				+ "entitatId=" + entitatId + ", "
				+ "unitatId=" + unitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.getReferenceById(unitatId);
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndUnitatOrganitzativaAndPareNotNull(entitat, unitatOrganitzativa);
		List<BustiaDto> resposta = bustiaHelper.toBustiaDto(
				busties,
				false,
				false,
				true);
		omplirPermisosPerBusties(resposta, false, null);
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<BustiaDto> findAmbFiltreAdmin(
			Long entitatId,
			BustiaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.trace("Cercant les bústies segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("unitat", new String[]{"unitatId"});
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findById(filtre.getUnitatId()).orElse(null);
		// Si es filtra per unitat superior llavors es passa la llista d'identificadors possibles de l'arbre.
		List<String> codisUnitatsSuperiors = bustiaHelper.getCodisUnitatsSuperiors(entitat, filtre.getCodiUnitatSuperior()); 
		// Es crean diferents llistes de 1000 com a màxim per evitar error a la consulta
		List<String> llistaCodisUnitats1 = new ArrayList<>();
		List<String> llistaCodisUnitats2 = new ArrayList<>();
		List<String> llistaCodisUnitats3 = new ArrayList<>();
		List<String> llistaCodisUnitats4 = new ArrayList<>();
		List<String> llistaCodisUnitats5 = new ArrayList<>();
		for(int i=0; i<codisUnitatsSuperiors.size(); i++) {
			if (i>=0 && i<1000) {
				llistaCodisUnitats1.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=1000 && i<2000) {
				llistaCodisUnitats2.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=2000 && i<3000) {
				llistaCodisUnitats3.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=3000 && i<4000) {
				llistaCodisUnitats4.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=4000 && i<5000) {
				llistaCodisUnitats5.add(codisUnitatsSuperiors.get(i));
			}
		}
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
//						!llistaCodisUnitats1.isEmpty() ? llistaCodisUnitats1 : null,
//						!llistaCodisUnitats2.isEmpty() ? llistaCodisUnitats2 : llistaCodisUnitats1,
//						!llistaCodisUnitats3.isEmpty() ? llistaCodisUnitats3 : llistaCodisUnitats1,
//						!llistaCodisUnitats4.isEmpty() ? llistaCodisUnitats4 : llistaCodisUnitats1,
//						!llistaCodisUnitats5.isEmpty() ? llistaCodisUnitats5 : llistaCodisUnitats1,
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
		omplirPermisosPerBusties(resultPagina.getContingut(), true, null);
		return resultPagina;
	}

	@Override
	@Transactional
	public List<BustiaDto> findActivesAmbEntitat(
			Long entitatId) {
		
		
		final Timer timerfindActivesAmbEntitat = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findActivesAmbEntitat"));
		Timer.Context contextfindActivesAmbEntitat = timerfindActivesAmbEntitat.time();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.trace("Cercant bústies actives de l'entitat ("
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
		logger.trace("Cercant bústies de l'entitat ("
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
            BustiaFiltreDto filtre) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.trace("Cercant bústies de l'entitat ("
				+ "entitatId=" + entitatId + ", "
				+ "usuariCodi=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() != null ? unitatOrganitzativaRepository.findById(filtre.getUnitatId()).orElse(null) : null;
		
		// Si es filtra per unitat superior llavors es passa la llista d'identificadors possibles de l'arbre.
		List<String> codisUnitatsSuperiors = bustiaHelper.getCodisUnitatsSuperiors(entitat, filtre.getCodiUnitatSuperior()); 
		if (codisUnitatsSuperiors.isEmpty())
			codisUnitatsSuperiors.add("-"); // per evitar error per llista buida
		

		// Es crean diferents llistes de 1000 com a màxim per evitar error a la consulta
		List<String> llistaCodisUnitats1 = new ArrayList<>();
		List<String> llistaCodisUnitats2 = new ArrayList<>();
		List<String> llistaCodisUnitats3 = new ArrayList<>();
		List<String> llistaCodisUnitats4 = new ArrayList<>();
		List<String> llistaCodisUnitats5 = new ArrayList<>();
		for(int i=0; i<codisUnitatsSuperiors.size(); i++) {
			if (i>=0 && i<1000) {
				llistaCodisUnitats1.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=1000 && i<2000) {
				llistaCodisUnitats2.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=2000 && i<3000) {
				llistaCodisUnitats3.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=3000 && i<4000) {
				llistaCodisUnitats4.add(codisUnitatsSuperiors.get(i));
			} else  if (i>=4000 && i<5000) {
				llistaCodisUnitats5.add(codisUnitatsSuperiors.get(i));
			}
		}
				
		final Timer timerfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre"));
		Timer.Context contextfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre = timerfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre.time();
		List<BustiaEntity> busties = bustiaRepository.findAmbEntitatAndFiltreAmbLlistes(
				entitat,
				filtre.getUnitatId() == null,
				unitat,
				filtre.getNom() == null || filtre.getNom().isEmpty(),
				filtre.getNom() != null ? filtre.getNom().trim() : "",
				filtre.getCodiUnitatSuperior() == null || filtre.getCodiUnitatSuperior().isEmpty(),
				//codisUnitatsSuperiors,
				!llistaCodisUnitats1.isEmpty() ? llistaCodisUnitats1 : null,
				!llistaCodisUnitats2.isEmpty() ? llistaCodisUnitats2 : llistaCodisUnitats1,
				!llistaCodisUnitats3.isEmpty() ? llistaCodisUnitats3 : llistaCodisUnitats1,
				!llistaCodisUnitats4.isEmpty() ? llistaCodisUnitats4 : llistaCodisUnitats1,
				!llistaCodisUnitats5.isEmpty() ? llistaCodisUnitats5 : llistaCodisUnitats1,
				filtre.getUnitatObsoleta() == null || filtre.getUnitatObsoleta() == false,
				filtre.getPerDefecte() == null || filtre.getPerDefecte() == false,
				filtre.getActiva() == null || filtre.getActiva() == false);
		contextfindByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre.stop();

        if (filtre.getPermis() != null && filtre.getPermis()) {
            Map<Long, List<PermisDto>> permisos = permisosHelper.findPermisos(busties.stream().map(DistribucioPersistable::getId).collect(Collectors.toList()), BustiaEntity.class);
            busties = busties.stream().filter(bustia -> {
                List<PermisDto> permis = permisos.get(bustia.getId());
                return permis != null && permis.size()>1;
            }).collect(Collectors.toList());
        }

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
	@Transactional
	public List<BustiaContingutDto> findAmbEntitatAndFiltrePerInput(
			Long entitatId, 
			ContingutTipusEnumDto tipus, 
			String filtre) {
		List<ContingutEntity> busties = null;
		
		busties = bustiaRepository.findAmbEntitatAndFiltreInput(
				entitatId, 
				tipus,  
				filtre == null || filtre.isEmpty(), 
				filtre != null ? filtre : "");
		
		return conversioTipusHelper.convertirList(
				busties, 
				BustiaContingutDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findBustiesPermesesPerUsuari(
			Long entitatId,
			boolean mostrarInactives) {
		logger.trace("Consulta de busties permeses per un usuari ("
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
				new Permission[] {
						ExtendedPermission.READ						
				},
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

	@Override
	@Transactional(readOnly = true)
	public List<BustiaDto> findBustiesPerUsuari(
			Long entitatId,
			boolean mostrarInactives) {
		logger.trace("Consulta de busties permeses per un usuari ("
				+ "entitatId=" + entitatId + ")");
		final Timer findBustiesPerUsuariTimer = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findBustiesPerUsuari"));
		Timer.Context findBustiesPerUsuariContext = findBustiesPerUsuariTimer.time();
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
		
		final Timer findBustiesPerUsuariTimerToBustiaDto = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findBustiesPerUsuari.ToBustiaDto"));
		Timer.Context findBustiesPerUsuariContextToBustiaDto = findBustiesPerUsuariTimerToBustiaDto.time();
		List<BustiaDto> bustiesRetorn = bustiaHelper.toBustiaDto(busties, false, true,
				false);
		findBustiesPerUsuariContextToBustiaDto.stop();
		
		findBustiesPerUsuariContext.stop();
		return bustiesRetorn;
	}

	
	@Transactional
	@Override
	public List<BustiaDto> consultaBustiesOrigen(
			Long entitatId, 
			List<BustiaDto> bustiesPermesesPerUsuari,
			boolean mostrarInactivesOrigen) {
		final Timer consultaBustiesOrigenTimer = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "consultaBustiesOrigen"));
		Timer.Context consultaBustiesOrigenContext = consultaBustiesOrigenTimer.time();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		List<Long> bustiesOrigenIds = null;
		List<Long> busties = new ArrayList<Long>();
		boolean totesLesbusties = false;
		long beginTime = new Date().getTime();
		if (bustiesPermesesPerUsuari != null && !bustiesPermesesPerUsuari.isEmpty()) { 
			for (BustiaDto bustiaUsuari: bustiesPermesesPerUsuari) {
				busties.add(bustiaUsuari.getId());
			}
		} else {
			busties.add(0L);
		}
		final Timer consultaBustiesOrigenFindRegistresAndBustiesTimer = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "consultaBustiesOrigenFindRegistresAndBusties"));
		Timer.Context consultaBustiesOrigenFindRegistresAndBustiesContext = consultaBustiesOrigenFindRegistresAndBustiesTimer.time();
		try {
			bustiesOrigenIds = vistaMovimentRepository.findBustiesOrigenByFiltre(entitat.getId(), totesLesbusties, busties);
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = new Date().getTime();
			logger.error("findRegistresByBusties or findBustiesOrigenByRegistres executed with errors in: " + (endTime - beginTime) + "ms", e);
			consultaBustiesOrigenFindRegistresAndBustiesContext.stop();
			throw new RuntimeException(e);
		}
		consultaBustiesOrigenFindRegistresAndBustiesContext.stop();
		final Timer consultaBustiesOrigenConversioTimer = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "consultaBustiesOrigenConversioTimer"));
		Timer.Context consultaBustiesOrigenConversioContext = consultaBustiesOrigenConversioTimer.time();
		List<BustiaEntity> bustiesOrigenUnique = new ArrayList<BustiaEntity>();
		for (Long bustiaId : bustiesOrigenIds) {
			if (bustiaId != null) {
				BustiaEntity bustia = entityComprovarHelper.comprovarBustia(entitat, bustiaId, false);
					bustiesOrigenUnique.add(bustia);
			}
		}
		consultaBustiesOrigenConversioContext.stop();
		List<BustiaDto> bustiesDto = bustiaHelper.toBustiaDto(
				bustiesOrigenUnique,
				false,
				false,
				false);
		consultaBustiesOrigenContext.stop();
		return bustiesDto;
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
		if (registreRepetit != null && !registreRepetit.isSobreescriure()) {
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
				false,
				null);
		logger.debug("Bústia per defecte de l'anotació (" +
				"anotacioNumero=" + anotacioEntity.getNumero() + ", "  +
				"bustia=" + bustia.getId() + ")");
		
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
	public Throwable registreAnotacioCrearIProcessar(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatOrganitzativaCodi,
			RegistreAnotacio registreAnotacio) {
		
		Timer.Context context = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar")).time();
		
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
				registreAnotacio.getServeiCodi(),
				registreAnotacio.getAssumpteCodi(), 
				registreAnotacio.isPresencial());
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
		if (reglaAplicable == null 
				&& anotacioEntity.getPare() != null) {
			emailHelper.createEmailsPendingToSend(
					(BustiaEntity) anotacioEntity.getPare(),
					anotacioEntity,
					anotacioEntity.getDarrerMoviment());
		}
		contextmoveAnotacioToBustiaPerDefecte.stop();

		// Si ve informat amb uuid no guardar a l'arxiu
		Boolean isRegistreArxiuPendent = registreRepository.isRegistreArxiuPendentByUuid(anotacioEntity.getId(), entitat);
		if (!isRegistreArxiuPendent && reglaAplicable == null) {
			anotacioEntity.setNewProcesEstat(RegistreProcesEstatEnum.BUSTIA_PENDENT);
		} else if (!isRegistreArxiuPendent && reglaAplicable != null) {
			anotacioEntity.setNewProcesEstat(RegistreProcesEstatEnum.REGLA_PENDENT);
		}
		
		//-- apply rules of type bustia or unitat ---
		Timer.Context contextprocessarAnotacioPendentRegla = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.processarAnotacioPendentRegla")).time();
		Throwable exceptionProcessant = null;
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
			logger.trace("L'anotació es processarà de forma asíncrona (" +
					"entitatUnitatCodi=" + entitatCodi + ", " +
					"tipus=" + tipus + ", " +
					"unitatOrganitzativa=" + unitatOrganitzativaCodi + ", " +
					"anotacio=" + registreAnotacio.getNumero() + ")");
		}
		contextprocess.stop();
		
		context.stop();
		return exceptionProcessant;
	}


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public long registreAnotacioCrear(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatOrganitzativaCodi,
			RegistreAnotacio registreAnotacio) throws Exception {
		
		Timer.Context context = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar")).time();
		
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
				registreAnotacio.getServeiCodi(),
				registreAnotacio.getAssumpteCodi(), 
				registreAnotacio.isPresencial());
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
		if (reglaAplicable == null 
				&& anotacioEntity.getPare() != null) {
			emailHelper.createEmailsPendingToSend(
					(BustiaEntity) anotacioEntity.getPare(),
					anotacioEntity,
					anotacioEntity.getDarrerMoviment());
		}
		contextmoveAnotacioToBustiaPerDefecte.stop();
		

		// Si ve informat amb uuid no guardar a l'arxiu
		Boolean isRegistreArxiuPendent = registreRepository.isRegistreArxiuPendentByUuid(anotacioEntity.getId(), entitat);
		if (!isRegistreArxiuPendent && reglaAplicable == null) {
			anotacioEntity.setNewProcesEstat(RegistreProcesEstatEnum.BUSTIA_PENDENT);
		} else if (!isRegistreArxiuPendent && reglaAplicable != null) {
			anotacioEntity.setNewProcesEstat(RegistreProcesEstatEnum.REGLA_PENDENT);
		}
		
		//-- apply rules of type bustia or unitat ---
		Timer.Context contextprocessarAnotacioPendentRegla = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.processarAnotacioPendentRegla")).time();
		Exception exceptionProcessant = null;
		if (reglaAplicable != null && (reglaAplicable.getTipus() == ReglaTipusEnumDto.BUSTIA || reglaAplicable.getTipus() == ReglaTipusEnumDto.UNITAT)) {
			exceptionProcessant = registreHelper.processarAnotacioPendentRegla(anotacioEntity.getId());
		}
		contextprocessarAnotacioPendentRegla.stop();
		context.stop();
		
		if (exceptionProcessant != null) {
			throw exceptionProcessant;
		}
		return anotacioEntity.getId();
	}

	@Override
	public Throwable registreAnotacioProcessar(
			Long registreId) {
		
		Throwable exceptionProcessant = null;
		
		Timer.Context context = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar")).time();
		
		RegistreEntity anotacioEntity = registreRepository.getReferenceById(registreId);

		//------- process --------
		Timer.Context contextprocess = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioCrearIProcessar.process")).time();
		// if asynchronous processing is turned off save anotacio in arxiu and apply reglas immediately
		if (!bustiaHelper.isProcessamentAsincronProperty()) {
			logger.trace("L'anotació es processarà inmediatament (" +
					"entitatUnitatCodi=" + anotacioEntity.getEntitatCodi() + ", " +
					"tipus=" + anotacioEntity.getTipus() + ", " +
					"unitatOrganitzativa=" + anotacioEntity.getUnitatAdministrativa() + ", " +
					"anotacio=" + anotacioEntity.getIdentificador() + ")");
			exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(anotacioEntity.getId());
			if (exceptionProcessant == null) {
				exceptionProcessant = registreHelper.processarAnotacioPendentRegla(anotacioEntity.getId());
			}
		// if asynchronous processing is turned on there are two @Scheduled methods that will periodically process anotacions pending to save in arxiu or to apply regla to
		} else {
			logger.trace("L'anotació es processarà de forma asíncrona (" +
					"entitatUnitatCodi=" + anotacioEntity.getEntitatCodi() + ", " +
					"tipus=" + anotacioEntity.getTipus() + ", " +
					"unitatOrganitzativa=" + anotacioEntity.getUnitatAdministrativa() + ", " +
					"anotacio=" + anotacioEntity.getIdentificador() + ")");
		}
		contextprocess.stop();
		
		context.stop();
		return exceptionProcessant;
	}



	@Transactional
	@Override
	public void registreAnotacioEnviarPerEmail(
			Long entitatId,
			Long registreId, 
			String adresses, 
			String motiu,
			boolean isVistaMoviments,
			String rolActual) throws MessagingException {
		
		final Timer timerregistreAnotacioEnviarPerEmail = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "registreAnotacioEnviarPerEmail"));
		Timer.Context contextregistreAnotacioEnviarPerEmail = timerregistreAnotacioEnviarPerEmail.time();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		RegistreDto registre = registreService.findOne(
				entitatId,
				registreId,
				isVistaMoviments,
				rolActual);
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura() && !isVistaMoviments)
			entityComprovarHelper.comprovarBustia(
					entitat,
					registre.getPareId(),
					true);
		RegistreAnnexDto justificant = null;
		if (registre.getJustificantArxiuUuid() != null && !registre.getJustificantArxiuUuid().isEmpty()) {
			justificant = registreService.getRegistreJustificant(
					entitatId,
					registreId,
					isVistaMoviments);
		}
		List<RegistreAnnexDto> anexos = registreHelper.getAnnexosAmbFirmes(
				entitatId,
				registreId,
				isVistaMoviments,
				rolActual);
		
		String appBaseUrl = configHelper.getConfig("es.caib.distribucio.app.base.url");
		String concsvBaseUrl = configHelper.getConfig("es.caib.distribucio.concsv.base.url");
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
		StringBuilder htmlJustificant = new StringBuilder();
		htmlJustificant.append("");
		if (registre.getJustificantArxiuUuid() != null && !registre.getJustificantArxiuUuid().isEmpty()) {
			htmlJustificant = getHtmlJustificant(
					justificant,
					justificantDataCaptura,
					concsvBaseUrl,
					appBaseUrl,
					registreId);
		}
		StringBuilder htmlAnnexosTable = new StringBuilder();
		htmlAnnexosTable.append("");
		if (!registre.getAnnexos().isEmpty()) {
			htmlAnnexosTable = getHtmlAnnexosTable(
					anexos,
					sdf,
					appBaseUrl,
					concsvBaseUrl,
					registreId);
		}
		StringBuilder htmlInteressatsTable = new StringBuilder();
		htmlInteressatsTable.append("");
		if (!registre.getInteressats().isEmpty()) {
			htmlInteressatsTable = getHtmlInteressatsTable(registre);
		}
		// ### Usuari que reenvia l'anotació ###
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		DadesUsuari dadesUsuariActual = null;
		if (auth != null) {
			dadesUsuariActual = cacheHelper.findUsuariAmbCodi(auth.getName());
		}
		if (dadesUsuariActual == null) {
			UsuariEntity usuari = usuariHelper.getUsuariByCodi(auth.getName());
			dadesUsuariActual = new DadesUsuari(
					usuari.getCodi(), 
					usuari.getNom(), 
					usuari.getNom(), 
					null,
					usuari.getNif(),
					usuari.getEmailAlternatiu() != null ? usuari.getEmailAlternatiu() : usuari.getEmail(), 
					true);
		}
		
		StringBuilder html = getHtml(
				registre,
				registreData,
				message18nRegistreTipus,
				registreDataOrigen,
				registreCreatedDate,
				htmlJustificant,
				htmlAnnexosTable,
				htmlInteressatsTable,
				dadesUsuariActual, 
				motiu, 
				appBaseUrl);
		
		
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
		
		
		String entorn = configHelper.getConfig("es.caib.distribucio.default.user.entorn");
		
		if (entorn != null) {
			helper.setSubject("Distribució - " + entorn + ": " + registre.getNom());
			
		}else {
			helper.setSubject("Distribució: " + registre.getNom());
		}
		
		messageHelper.getMessage("registre.detalls.camp.tipus");
		
		helper.setText(plainText, html.toString());
		
		mailSender.send(missatge);
		
		List<String> params = new ArrayList<>();
		RegistreEntity registreEntity = registreRepository.findByEntitatAndId(
				entitat,
				registreId);
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
		logger.trace("Consultant els elements pendents a totes les busties ("
				+ "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		// Obté la llista d'id's amb permisos per a l'usuari
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNullOrderByNomAsc(entitat);
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
		
		long count;
		if (!busties.isEmpty()) {
			count = contingutRepository.countPendentsByPares(
					busties);
		}else {
			count = 0;
		}
		return count;
	}

	@Transactional
	@Override
	public void registreReenviar(
			Long entitatId,
			Long[] bustiaDestiIds,
			Long registreId,
			boolean opcioDeixarCopiaSelectada,
			String comentari,
			Long[] perConeixement,
			Map<Long, String> destinsUsuari,
			Long destiLogic) throws NotFoundException {
		
		logger.debug("Reenviant contingut pendent de la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaDestiIds=" + bustiaDestiIds + ", "
				+ "opcioDeixarCopiaSelectada=" + opcioDeixarCopiaSelectada + ", "
				+ "registreId=" + registreId + ", "
				+ "comentari=" + comentari + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		RegistreEntity reg = registreRepository.findOneAmbBloqueig(entitatId, registreId);
		
		if (reg == null) {
			throw new NotFoundException(registreId, RegistreEntity.class);
		}
		
		if (destiLogic == null && isPermesReservarAnotacions())
			registreHelper.comprovarRegistreAlliberat(reg);
	
		BustiaEntity bustiaOrigenLogic = null;
		BustiaEntity bustiaOrigen = null;
		if (destiLogic == null && !usuariHelper.isAdmin() && !usuariHelper.isAdminLectura())
			bustiaOrigen = entityComprovarHelper.comprovarBustia(
				entitat,
				destiLogic != null ? destiLogic : reg.getPareId(),
				true);
		if (destiLogic != null)
			bustiaOrigenLogic = entityComprovarHelper.comprovarBustia(
					entitat,
					destiLogic,
					false);
		
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
		
		
		ContingutEntity registreOriginal = entityComprovarHelper.comprovarContingut(
				entitat,
				registreId,
				bustiaOrigen);
		
		List<ContingutEntity> nousContinguts = new ArrayList<ContingutEntity>();
		List<RegistreEntity> registresExistents = new ArrayList<RegistreEntity>();
		
		boolean crearComentariInformatiu = Boolean.parseBoolean(configHelper.getConfig("es.caib.distribucio.contingut.enviar.crear.comentari"));
				
		for (int i = 0; i < bustiesDesti.size(); i++) {
			BustiaEntity bustia = bustiesDesti.get(i);
			boolean assentamentPerConeixement = bustiesPerConeixement.contains(bustia);
			ContingutEntity registrePerReenviar = null;
			boolean ferCopia = opcioDeixarCopiaSelectada || !isLastIteration(i, bustiesDesti);

//			### Recuperar registres duplicats ###
			RegistreEntity registre = (RegistreEntity)registreOriginal;
			List<RegistreEntity> registreRepetit = registreRepository.findRegistresByEntitatCodiAndLlibreCodiAndRegistreTipusAndNumeroAndDataAndEsborrat(
					registre.getEntitatCodi(),
					registre.getLlibreCodi(),
					RegistreTipusEnum.ENTRADA.getValor(),
					registre.getNumero(),
					registre.getData(),
					0);
			
//			### Comprovar si existeixen moviments del registre al destí seleccionat ###
			List<ContingutMovimentEntity> contingutMoviments = contingutHelper.comprovarExistenciaAnotacioEnDesti(registreRepetit, bustia.getId());

			if (!contingutMoviments.isEmpty() && isPermesSobreescriureAnotacions()) {
				Map<RegistreEntity, ContingutMovimentEntity> registresAmbCanviEstat = new HashMap<RegistreEntity, ContingutMovimentEntity>();
//				### Còpia registre original (origen) per esborrar en cas de no deixar còpia ###
				RegistreEntity registreOriginalR = registre;
//				### Sobrescriure cada moviment/registre existent al destí ###
				for (ContingutMovimentEntity contingutMovimentEntity : contingutMoviments) {
//					### Tractar registre existent si és un reenviament duplicat ###
					registrePerReenviar = contingutMovimentEntity.getContingut();
					
					contingutHelper.updateMovimentExistent(
							contingutMovimentEntity, 
							registrePerReenviar,
							comentari,
							assentamentPerConeixement, 
							bustiaOrigenLogic,
							ferCopia);
					
//					### Tornar a marcar l'anotació com a pendent si estava processada i es reb per duplicat ###
					registre = (RegistreEntity)registrePerReenviar;
					if (!registre.getPendent() && ! isMantenirEstatReenviades()) {
						registresAmbCanviEstat.put(registre, contingutMovimentEntity);
					}
//					### Actualitzar històric i crear coa correus per cada registre a crear ###
					updateMovimentDetail(
							registrePerReenviar, 
							registreOriginal, 
							contingutMovimentEntity, 
							bustia, 
							entitat, 
							nousContinguts,
							opcioDeixarCopiaSelectada);
					registresExistents.add(registre);
				}
				
//				### Esborrar registre si no s'ha marcat per còpia i el reenviament no és a ell mateix ### 
				if (!ferCopia && !registresExistents.contains(registreOriginalR)) {
					try {
						registreHelper.esborrarRegistre(registreOriginalR);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Hi ha hagut un error esborrant l'anotació origen", e);
						throw new RuntimeException(e);
					}
				}
//				###Enviar correus si tot ha anat bé
				for (Map.Entry<RegistreEntity, ContingutMovimentEntity> registreCanviatEstat: registresAmbCanviEstat.entrySet()) {
					registre = registreCanviatEstat.getKey();
					ContingutMovimentEntity contingutMovimentEntity = registreCanviatEstat.getValue();
					registre.updatePendent(true);
					registre.updateReactivat(true);
					RegistreProcesEstatEnum estatAnterior = registre.getProcesEstat();
					registre.updateProces(RegistreProcesEstatEnum.BUSTIA_PENDENT, null);
//					### Enviar correu avís reactivació anotació ###
					emailHelper.sendEmailReactivacioAnotacio(
							registre,
							contingutMovimentEntity,
							registre.getProcesEstat(),
							estatAnterior);
				}
				emailHelper.sendEmailDuplicacioAnotacio(contingutMoviments, bustia);
			} else {
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
						assentamentPerConeixement,
						bustiaOrigenLogic);
				
				updateMovimentDetail(
						registrePerReenviar, 
						registreOriginal, 
						contingutMoviment, 
						bustia, 
						entitat, 
						nousContinguts, 
						opcioDeixarCopiaSelectada);
			}
			
			if (isPermesAssignarAnotacions()) {
				String usuari = destinsUsuari.get(bustia.getId());
				if (usuari != null && !usuari.isEmpty() && !usuari.equals("|")) {
					String[] usuariArr = usuari.split("\\|");
					String usuariCodi = usuariArr[0];
					String comentariUsuari = usuariArr.length > 1 ? usuariArr[1] : null;
					registreService.assignar(entitatId, registrePerReenviar.getId(), usuariCodi, comentariUsuari);	
				}
			}
			
			if (crearComentariInformatiu) {				
				StringBuilder comentariContingut = generarTextDesti(
						assentamentPerConeixement?"":bustia.getNom(), 
						assentamentPerConeixement?bustia.getNom():"");
				if (comentariContingut.length() > 0) {
					ContingutComentariEntity comentariAmbBustiesDesti = ContingutComentariEntity.getBuilder(
							registrePerReenviar, 
							comentariContingut.toString()).build();
					contingutComentariRepository.save(comentariAmbBustiesDesti);
					List<ContingutMovimentEntity> contingutMoviment= contingutMovimentRepository.findByContingutOrderByCreatedDateAsc(registrePerReenviar);
					for (ContingutMovimentEntity moviment: contingutMoviment) {
						moviment.updateComentariDestins(comentariContingut.toString());
					}
				}				
			}				
		}		
		
		if (comentari != null && !comentari.isEmpty()) {
			for (ContingutEntity contingut: nousContinguts) {
				//etiqueta reenviat
				String reenviat = "<span class='label label-default'>" + messageHelper.getMessage("bustia.pendent.accio.reenviar.comentari") + "</span> " + comentari;
				ContingutComentariEntity comentariReenviament = ContingutComentariEntity.getBuilder(
						contingut, 
						reenviat).build();
				contingutComentariRepository.save(comentariReenviament);
			}
		}		
	}
	
	private void updateMovimentDetail(
			ContingutEntity registrePerReenviar, 
			ContingutEntity registreOriginal, 
			ContingutMovimentEntity contingutMoviment, 
			BustiaEntity bustia, 
			EntitatEntity entitat,
			List<ContingutEntity> nousContinguts,
			boolean opcioDeixarCopiaSelectada) {
		// when anotacio processed by bustia user is resent to another bustia in new bustia it should be again pending 
		if (registrePerReenviar.getClass() == RegistreEntity.class) {
			RegistreEntity registre = (RegistreEntity) registrePerReenviar;
			if (registre.getProcesEstat() == RegistreProcesEstatEnum.BUSTIA_PROCESSADA) { //En cas d'anotacions duplicades l'estat es canvia en el mètode anterior
				registre.updateProcesMultipleExcepcions(
						RegistreProcesEstatEnum.BUSTIA_PENDENT,
						null);
			}
		}	
		if (opcioDeixarCopiaSelectada) {
            List<String> params = new ArrayList<>();
            params.add("ORIGINAL_AMB_COPIA");
			contingutLogHelper.logMoviment(
					registreOriginal,
					LogTipusEnumDto.REENVIAMENT,
					contingutMoviment,
					true,
                    params);
		}
        List<String> params = new ArrayList<>();
        params.add(opcioDeixarCopiaSelectada ?"COPIA": "ORIGINAL");
		contingutLogHelper.logMoviment(
				registrePerReenviar,
				LogTipusEnumDto.REENVIAMENT,
				contingutMoviment,
				true,
                params);
		emailHelper.createEmailsPendingToSend(
				bustia,
				registrePerReenviar,
				contingutMoviment);
		nousContinguts.add(registrePerReenviar);
	}
	
	@Transactional(readOnly = true)
	@Override
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzatives(
			Long entitatId,
			boolean nomesBusties,
			boolean nomesBustiesPermeses,
			boolean comptarElementsPendents) {
		logger.trace("Consulta de l'arbre d'unitats organitzatives ("
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
		logger.trace("Consulta de l'arbre d'unitats organitzatives (" +
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
		entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		permisosHelper.deletePermis(
				id,
				BustiaEntity.class,
				permisId);
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
					false,
					null);
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
	
	private StringBuilder generarTextDesti(
			String assentamentPerTramitarNom, 
			String assentamentPerConeixementNom) {
		StringBuilder comentariContingut = new StringBuilder();
		if (!assentamentPerTramitarNom.isEmpty()) {
			String bustiesTramitacio = messageHelper.getMessage("registre.anotacio.enviat.pertramitar");
			comentariContingut.append(bustiesTramitacio);			
			comentariContingut.append(assentamentPerTramitarNom);			
		}
		
		if (!assentamentPerConeixementNom.isEmpty()) {
			if (!assentamentPerTramitarNom.isEmpty())
				comentariContingut.append("\n\r");			
			String bustiesConeixment = messageHelper.getMessage("registre.anotacio.enviat.perconeixment");
			comentariContingut.append(bustiesConeixment);			
			comentariContingut.append(assentamentPerConeixementNom);	
		}
		return comentariContingut;
	}

	private void omplirPermisosPerBusties(
			List<? extends BustiaDto> busties,
			boolean ambLlistaPermisos,
			PaginacioParamsDto paginacio) {
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
			for (BustiaDto bustia: busties) {
				List<PermisDto> permisosBustia = permisos.get(bustia.getId());

				permisosHelper.ordenarPermisos(paginacio, permisosBustia);
				
				bustia.setPermisos(permisosBustia);
			
			}
		}
	}
	
	private StringBuilder getHtmlJustificant(RegistreAnnexDto justificant, Object justificantDataCaptura, String concsvBaseUrl, String appBaseUrl, Long registreId) {
		StringBuilder htmlJustificant = new StringBuilder(); 
		htmlJustificant.append("		<table>")
					   .append("			<tr>")
					   .append("				<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.titol.justificant")).append("</th>")
					   .append("			</tr>")
					   .append(				"<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.data.captura")).append("</th>")
					   .append("				<td>").append(justificantDataCaptura).append("</td>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.origen")).append("</th>")
					   .append("				<td>").append(Objects.toString(justificant.getOrigenCiutadaAdmin(), "")).append("</td>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.estat.elaboracio")).append("</th>")
					   .append("				<td>").append((justificant.getNtiElaboracioEstat()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiElaboracioEstat." + Objects.toString(justificant.getNtiElaboracioEstat(), "")))).append("</td>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.tipus.documental")).append("</th>")
					   .append("				<td>").append((justificant.getNtiTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiTipusDocument." + Objects.toString(justificant.getNtiTipusDocument(), "")))).append("</td>")
					   .append("			</tr>");
		
		if (justificant.getLocalitzacio() == null) {
			htmlJustificant.append("");
		}else {
			htmlJustificant.append("		<tr>")
						   .append("			<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.localitzacio")).append("</th>")
						   .append("			<td>").append(justificant.getLocalitzacio())
						   .append("		</tr>"); 
		}
		
		if (justificant.getObservacions() == null) {
			htmlJustificant.append("");
		}else {
			htmlJustificant.append("		<tr>")
						   .append("			<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.observacions")).append("</th>")
						   .append("			<td>").append(justificant.getObservacions())
						   .append("		</tr>");
		}			
		
		htmlJustificant.append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.fitxer")).append("</th>")
					   .append("				<td>").append(Objects.toString(justificant.getFitxerNom(), "")).append("("+Objects.toString(justificant.getFitxerTamany(), "")+" bytes)");
		
		if (concsvBaseUrl != null && justificant.getFirmaCsv() != null) {
			htmlJustificant.append("				<a href=\""+concsvBaseUrl+"/view.xhtml?hash="+justificant.getFirmaCsv()+"\"> Descarregar </a>");
		}else {
			htmlJustificant.append("				<a href=\""+appBaseUrl+"/modal/contingut/registre/"+registreId+"/justificant\"> Descarregar </a>");
		}
		
		htmlJustificant.append(					"</td>")
					   .append("			</tr>")
					   .append("		</table>");
		
		return htmlJustificant;
	}	
	
	private StringBuilder getHtmlAnnexosTable(List<RegistreAnnexDto> anexos, SimpleDateFormat sdf, String appBaseUrl, String concsvBaseUrl, Long registreId) {
		
		StringBuilder htmlAnnexos = new StringBuilder();
		htmlAnnexos.append("");
		for (RegistreAnnexDto annex: anexos) {
			StringBuilder htmlFirmes = new StringBuilder();
			htmlFirmes.append("");
			if (annex.getFirmes() != null && !annex.getFirmes().isEmpty()){
				htmlFirmes.append("			<tr>")
						  .append("				<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.annex.detalls.camp.firmes")).append("</th>")
						  .append("			</tr>");
				int i=1;
				for (ArxiuFirmaDto firma: annex.getFirmes()){
					StringBuilder htmlDetalls = new StringBuilder();
					htmlDetalls.append("");
					for(ArxiuFirmaDetallDto detall: firma.getDetalls()){
						htmlDetalls.append("<tr>")
								   .append("	<td>").append((detall.getData() == null ? messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.data.nd") : sdf.format(detall.getData()))).append("</td>")
								   .append("	<td>").append(Objects.toString(detall.getResponsableNif(), "")).append("</td>")
								   .append("	<td>").append(Objects.toString(detall.getResponsableNom(), "")).append("</td>")
								   .append("	<td>").append(Objects.toString(detall.getEmissorCertificat(), "")).append("</td>")
								   .append("</tr>");
					}
					htmlFirmes.append("		<tr>")
							  .append("			<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.annex.detalls.camp.firma")).append(" ").append(i);
					
					if (!firma.isAutofirma()) {
						htmlFirmes.append("");
					}else {
						htmlFirmes.append("(").append(messageHelper.getMessage("registre.annex.detalls.camp.firma.autoFirma")).append(")");						
					}
					
					htmlFirmes.append("			</th>")
							  .append("		</tr>")
							  .append("		<tr>")
							  .append("			<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaTipus")).append("</th>")
							  .append("			<td>").append((firma.getTipus()==null ? "": messageHelper.getMessage("document.nti.tipfir.enum." + Objects.toString(firma.getTipus(), "")))).append("</td>")
							  .append("		</tr>")
							  .append("		<tr>")
							  .append("			<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaPerfil")).append("</th>")
							  .append("			<td>").append(Objects.toString(firma.getPerfil(), "")).append("</td>")
							  .append("		</tr>");
					
					if (firma.getTipus() == ArxiuFirmaTipusEnumDto.PADES && firma.getTipus() == ArxiuFirmaTipusEnumDto.CADES_ATT && firma.getTipus() == ArxiuFirmaTipusEnumDto.XADES_ENV) {
						htmlFirmes.append("");
					}else {
						htmlFirmes.append("	<tr>")
								  .append("		<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.fitxer")).append("</th>")
								  .append("		<td>").append(Objects.toString(firma.getFitxerNom(), "")).append("</td>")
								  .append("	</tr>");
					}
					
					if (firma.getCsvRegulacio() == null || firma.getCsvRegulacio().isEmpty()) {
						htmlFirmes.append("");
					}else {
						htmlFirmes.append("	<tr>")
								  .append("		<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaCsvRegulacio")).append("</th>")
								  .append("		<td>").append(Objects.toString(firma.getCsvRegulacio(), "")).append("</td>")
								  .append("	</tr>");
					}
					
					if (firma.getDetalls().isEmpty()) {
						htmlFirmes.append("");
					}else {
						htmlFirmes.append("	<tr>")
								  .append("		<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls")).append("</th>")
								  .append("		<td>")
								  .append("<table class=\"table teble-striped table-bordered\">")
								  .append("<thead>")
								  .append("	<tr>")
								  .append("		<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.data")).append("</th>")
								  .append("		<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.nif")).append("</th>")
								  .append("		<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.nom")).append("</th>")
								  .append("		<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.firmaDetalls.emissor")).append("</th>")
								  .append("	</tr>")
								  .append("<tbody>")
								  .append(htmlDetalls)
								  .append("</tbody>")
								  .append("</table>")
								  .append("		</td>")
								  .append("	</tr>");
					}					
								
					i++;
				}
			}
			htmlAnnexos.append("			<tr>")
					   .append("				<th class=\"tableHeader\" colspan=\"2\">" + annex.getTitol()).append("</th>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.data.captura")).append("</th>")
					   .append("				<td>").append((annex.getDataCaptura() == null ? "" : sdf.format(annex.getDataCaptura()))).append("</td>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.origen")).append("</th>")
					   .append("				<td>").append(Objects.toString(annex.getOrigenCiutadaAdmin(), "")).append("</td>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.estat.elaboracio")).append("</th>")
					   .append("				<td>").append((annex.getNtiElaboracioEstat()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiElaboracioEstat." + Objects.toString(annex.getNtiElaboracioEstat(), "")))).append("</td>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.eni.tipus.documental")).append("</th>")
					   .append("				<td>").append((annex.getNtiTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.ntiTipusDocument." + Objects.toString(annex.getNtiTipusDocument(), "")))).append("</td>")
					   .append("			</tr>")
					   .append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.sicres.tipus.document")).append("</th>")
					   .append("				<td>").append((annex.getSicresTipusDocument()==null ? "": messageHelper.getMessage("registre.annex.detalls.camp.sicresTipusDocument."+annex.getSicresTipusDocument()))).append("</td>")
					   .append("			</tr>");
			
			if (annex.getLocalitzacio() == null) {
				htmlAnnexos.append("");
			}else {
				htmlAnnexos.append("		<tr>")
						   .append("			<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.localitzacio")).append("</th>")
						   .append("			<td>").append(annex.getLocalitzacio())
						   .append("		</tr>");
			}
			
			if (annex.getObservacions() == null) {
				htmlAnnexos.append("");
			}else {
				htmlAnnexos.append("		<tr>")
						   .append("			<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.observacions")).append("</th>")
						   .append("			<td>").append(annex.getObservacions())
						   .append("		</tr>");
			}
			
			htmlAnnexos.append("			<tr>")
					   .append("				<th>").append(messageHelper.getMessage("registre.annex.detalls.camp.fitxer")).append("</th>")
					   .append("				<td>").append(Objects.toString(annex.getFitxerNom(), "")).append("(").append(Objects.toString(annex.getFitxerTamany(), "")).append(" bytes)");
			
			if (concsvBaseUrl != null && annex.getFirmaCsv() != null) {
				htmlAnnexos.append("<a href=\""+concsvBaseUrl+"/view.xhtml?hash="+annex.getFirmaCsv()+"\"> Descarregar </a>");
			} else {
				htmlAnnexos.append("<a href=\""+appBaseUrl+"/modal/contingut/registre/"+registreId+"/annex/"+annex.getId()+"/arxiu/DOCUMENT\"> Descarregar </a>");
			}
			
			htmlAnnexos.append("			</td>")
					   .append("		</tr>")
					   .append(htmlFirmes)
					   .append("");
		}
		
		StringBuilder htmlAnnexosTable = new StringBuilder();
		htmlAnnexosTable.append("	<table>")
						.append("		<tr>")
						.append("			<th class=\"tableHeader\" colspan=\"2\">" + messageHelper.getMessage("registre.detalls.pipella.annexos")).append("</th>")
						.append("		</tr>")
						.append(htmlAnnexos)
						.append("	</table>");		
		
		return htmlAnnexosTable;
		
	}	
	
	
	private StringBuilder getHtmlInteressatsTable(RegistreDto registre) {
		StringBuilder htmlInteressats = new StringBuilder();
		htmlInteressats.append("");
		for (RegistreInteressat interessat: registre.getInteressats()) {
			RegistreInteressat representant = interessat.getRepresentant();
			StringBuilder htmlRepresentant = new StringBuilder();
			htmlRepresentant.append("");
			if (representant!=null){
				
				String representantTitle="";
				if(representant.getTipus().equals("PERSONA_FIS")){
					String representatLlinatge2 = representant.getLlinatge2()!=null ? representant.getLlinatge2():"";
					representantTitle = representant.getNom()+" "+representant.getLlinatge1()+" "+representatLlinatge2;
				} else {
					representantTitle = representant.getRaoSocial();
				}
				
				htmlRepresentant.append("			<tr>")
								.append("				<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.interessat.detalls.camp.representant")).append("</th>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th class=\"tableHeader\" colspan=\"2\">").append(representantTitle).append("</th>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.interessat.tipus")).append("</th>")
								.append("				<td>").append((representant.getTipus()==null ? "": messageHelper.getMessage("registre.interessat.tipus.enum." + Objects.toString(representant.getTipus(), "")))).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.interessat.document")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getDocumentTipus(), "")+": "+representant.getDocumentNum()).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.pais")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getPais(), "")).append((representant.getPaisCodi() == null ? "" : " (" + representant.getPaisCodi() + ")")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.provincia")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getProvincia(), "")).append((representant.getProvinciaCodi() == null ? "" : " (" + representant.getProvinciaCodi() + ")")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.municipi")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getMunicipi(), "")).append((representant.getMunicipiCodi() == null ? "" : " (" + representant.getMunicipiCodi() + ")")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.adresa")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getAdresa(), "")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.codiPostal")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getCodiPostal(), "")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.email")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getEmail(), "")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.telefon")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getTelefon(), "")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("registre.interessat.detalls.camp.emailHabilitat")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getEmailHabilitat(), "")).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent")).append("</th>")
								.append("				<td>").append((representant.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(representant.getCanalPreferent(), "")))).append("</td>")
								.append("			</tr>")
								.append("			<tr>")
								.append("				<th>").append(messageHelper.getMessage("interessat.form.camp.observacions")).append("</th>")
								.append("				<td>").append(Objects.toString(representant.getObservacions(), "")).append("</td>")
								.append("			</tr>");											
			}
			String interesatTitle="";
			if (interessat.getTipus().equals("PERSONA_FIS")) {
				String interessatLlinatge1 = interessat.getLlinatge2()!=null ? interessat.getLlinatge2() : "";
				interesatTitle = interessat.getNom()+" "+interessat.getLlinatge1()+" "+interessatLlinatge1;
			} else {
				interesatTitle = interessat.getRaoSocial();
			}
			htmlInteressats.append("			<tr>")
						   .append("				<th class=\"tableHeader\" colspan=\"2\">").append(interesatTitle).append("</th>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.interessat.tipus")).append("</th>")
						   .append("				<td>").append((interessat.getTipus()==null ? "": messageHelper.getMessage("registre.interessat.tipus.enum." + Objects.toString(interessat.getTipus(), "")))).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.interessat.document")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getDocumentTipus(), "")).append(": ").append(interessat.getDocumentNum()).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.pais")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getPais(), "")).append((interessat.getPaisCodi() == null ? "" : " (" + interessat.getPaisCodi() + ")")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.provincia")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getProvincia(), "")).append((interessat.getProvinciaCodi() == null ? "" : " (" + interessat.getProvinciaCodi() + ")")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.municipi")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getMunicipi(), "")).append((interessat.getMunicipiCodi() == null ? "" : " (" + interessat.getMunicipiCodi() + ")")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.adresa")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getAdresa(), "")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.codiPostal")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getCodiPostal(), "")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.email")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getEmail(), "")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.telefon")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getTelefon(), "")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("registre.interessat.detalls.camp.emailHabilitat")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getEmailHabilitat(), "")).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent")).append("</th>")
						   .append("				<td>").append((interessat.getCanalPreferent()==null ? "": messageHelper.getMessage("registre.interessat.detalls.camp.canalPreferent." + Objects.toString(interessat.getCanalPreferent(), "")))).append("</td>")
						   .append("			</tr>")
						   .append("			<tr>")
						   .append("				<th>").append(messageHelper.getMessage("interessat.form.camp.observacions")).append("</th>")
						   .append("				<td>").append(Objects.toString(interessat.getObservacions(), "")).append("</td>")
						   .append("			</tr>")
						   .append(htmlRepresentant);
		}
		
		
		StringBuilder htmlInteressatsTable = new StringBuilder();
		htmlInteressatsTable.append("		<table>")
							.append("			<tr>")
							.append("				<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.pipella.interessats")).append("</th>")
							.append("			</tr>")
							.append(htmlInteressats)
							.append("		</table>");
	
		return htmlInteressatsTable;
	}	

	private  StringBuilder getHtml(
			RegistreDto registre, 
			Object registreData, 
			String message18nRegistreTipus, 
			Object registreDataOrigen, 
			Object registreCreatedDate, 
			StringBuilder htmlJustificant, 
			StringBuilder htmlAnnexosTable, 
			StringBuilder htmlInteressatsTable, 
			DadesUsuari usuariActual, 
			String motiu, 
			String appBaseUrl) {
		String idEncriptat = "";		

		try {
			idEncriptat = registreHelper.encriptar(String.valueOf(registre.getId()));
		} catch (Exception e) {
		    logger.error("Error al encriptar l'identificador del registre: " + e.toString(), e);
		}
		
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html>")
			.append("<html>")
			.append("<head>")
			.append("<style>")
			.append("body {")
			.append("	margin: 0px;")
			.append("	font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;")
			.append("	font-size: 14px;")
			.append("	color: #333;")
			.append("}")
			.append("table {")
			.append("	")
			.append("	border-radius: 4px;")
			.append("	width: 100%;")
			.append("	border-collapse: collapse;")
			.append("	margin-bottom: 10px;")
			.append("}")
			
			.append("td, th {")
			.append("	border-bottom: solid 0.5px #ddd;")
			.append("	height: 38px;")
			.append("	border: 1px solid #ddd;")
			.append("	padding-left: 8px;")
			.append("	padding-right: 8px;")
			.append("}")
			
			.append(".tableHeader {")
			.append("	background-color: #f5f5f5;")
			.append("	border-top-left-radius: 4px;")
			.append("	border-top-righ-radius: 4px;")
			.append("}")
			
			.append(".header {")
			.append("	height: 30px;")
			.append("	background-color: #ff9523;")
			.append("	height: 90px;")
			.append("	text-align: center;")
			.append("	line-height: 100px;")
			.append("}")
			.append(".content {")
			.append("	margin: auto;")
			.append("	width: 70%;")
			.append("	padding: 10px;")
			.append("}")
				
			.append(".footer {")
			.append("	height: 30px;")
			.append("	background-color: #ff9523;")
			.append("	text-align: center;")
				
			.append("}")
				
			.append(".headerText {")
			.append("    font-weight: bold;")
			.append("    font-family: \"Trebuchet MS\", Helvetica, sans-serif;")
			.append("    color: white;")
			.append("    font-size: 30px;")
			.append("	display: inline-block;")
			.append("	vertical-align: middle;")
			.append("	line-height: normal; ")
			.append("}")
				
			.append(".footerText {")
			.append("    font-weight: bold;")
			.append("    font-family: \"Trebuchet MS\", Helvetica, sans-serif;")
			.append("    color: white;")
			.append("    font-size: 13px;")
			.append("	display: inline-block;")
			.append("	vertical-align: middle;")
			.append("	line-height: normal; ")
			.append("}")
			
			.append("</style>")
			.append("</head>")
			.append("<body>")
			.append("	<div class=\"header\">")
			.append("	<span class=\"headerText\">").append(messageHelper.getMessage("registre.titol").toUpperCase()).append("</span> ")
			.append("	</div>")
			.append("	<div class=\"content\">");
			
			if (usuariActual == null) {
				html.append("");
			}else {
				html.append("		<table>")
					.append("			<tr>")
					.append("				<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.remitent.titol")).append("</th>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.remitent.id")).append("</th>")
					.append("				<td>").append(usuariActual.getCodi()).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.remitent.nom")).append("</th>")
					.append("				<td>").append(usuariActual.getNom()).append(" ").append((usuariActual.getLlinatges() != null ? usuariActual.getLlinatges() : "")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.remitent.email")).append("</th>")
					.append("				<td>").append(usuariActual.getEmail() != null ? usuariActual.getEmail() : "-").append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.bustia")).append("</th>")
					.append("				<td>").append((registre.getPare() != null? registre.getPare().getNom() : "-")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.motiu")).append("</th>")
					.append("				<td>").append(motiu).append("</td>")
					.append("			</tr>")
					.append("		</table>");
			}
				
				
				html.append("		<table>")
					.append("			<tr>")
					.append("				<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.titol")).append("</th>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.tipus")).append("</th>")
					.append("				<td>").append(messageHelper.getMessage("registre.anotacio.tipus.enum." + Objects.toString(registre.getRegistreTipus(), ""))).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.numero")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getNumero(), "")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.data")).append("</th>")
					.append("				<td>").append(registreData).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.proces.estat")).append("</th>")
					.append("				<td>").append(messageHelper.getMessage("registre.proces.estat.enum." + registre.getProcesEstat())).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.proces.presencial")).append("</th>")
					.append("				<td>").append(messageHelper.getMessage("boolean." + Objects.toString(registre.getPresencial(), ""))).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.fitxers")).append("</th>")
					.append("			    <td>").append("<a href=\"" + appBaseUrl + "/public/" + idEncriptat + "/descarregarZipPublic\"> " + messageHelper.getMessage("registre.detalls.descarregarJustificantAnnexos") + " </a>").append("</td>")
					.append("			</tr>")
					.append("		</table>")

					.append("		<table>")
					.append("			<tr>")
					.append("				<th class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.titol.obligatories")).append("</th>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.oficina")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getOficinaDescripcio(), "")).append(" (" + Objects.toString(registre.getOficinaCodi(), "") + ")").append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.llibre")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getLlibreDescripcio(), "")).append(" (" + Objects.toString(registre.getLlibreCodi(), "") + ")").append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.extracte")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getExtracte(), "")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.docfis")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getDocumentacioFisicaCodi(), "")).append("-").append(Objects.toString(registre.getDocumentacioFisicaDescripcio(), "")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(message18nRegistreTipus).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getUnitatAdministrativaDescripcio(), "")).append(" (" + Objects.toString(registre.getUnitatAdministrativa(), "") + ")").append("</td>")
					.append("			</tr>		")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.assumpte.tipus")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getAssumpteTipusDescripcio(), "")).append(" (" + Objects.toString(registre.getAssumpteTipusCodi(), "") + ")").append("</td>")
					.append("			</tr>	")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.idioma")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getIdiomaDescripcio(), "")).append(" (" + Objects.toString(registre.getIdiomaCodi(), "") + ")").append("</td>")
					.append("			</tr>		")
					.append("		</table>")

					.append("		<table>")
					.append("			<tr>")
					.append("				<th colspan=\"4\" class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.titol.opcionals")).append("</th>")
					.append("			</tr>			")
					.append("			<tr>")
					.append("				<th colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.camp.assumpte.codi")).append("</th>")
					.append("				<td colspan=\"2\">").append(Objects.toString(registre.getAssumpteCodi(), "")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.camp.procediment")).append("</th>")
					.append("				<td colspan=\"2\">").append(Objects.toString(registre.getProcedimentCodi(), "")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.refext")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getReferencia(), "")).append("</td>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.numexp")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getExpedientNumero(), "")).append("</td>	")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.transport.tipus")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getTransportTipusDescripcio(), "")).append(" (" + Objects.toString(registre.getTransportTipusCodi(), "") + ")").append("</td>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.transport.num")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getTransportNumero(), "")).append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.camp.origen.oficina")).append("</th>")
					.append("				<td colspan=\"2\">").append(Objects.toString(registre.getOficinaOrigenDescripcio(), "")).append(" (" + Objects.toString(registre.getOficinaOrigenCodi(), "") + ")").append("</td>")
					.append("			</tr>		")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.origen.num")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getNumeroOrigen(), "")).append("</td>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.origen.data")).append("</th>")
					.append("				<td>").append(registreDataOrigen).append("</td>")
					.append("			</tr>	")
					.append("			<tr>")
					.append("				<th colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.camp.observacions")).append("</th>")
					.append("				<td colspan=\"2\">").append(Objects.toString(registre.getObservacions(), "")).append("</td>")
					.append("			</tr>			")
					.append("		</table>")
				
					.append("		<table>")
					.append("			<tr>")
					.append("				<th colspan=\"4\" class=\"tableHeader\" colspan=\"2\">").append(messageHelper.getMessage("registre.detalls.titol.seguiment")).append("</th>")
					.append("			</tr>			")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.entitat")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getEntitatDescripcio(), "")).append(" (" + Objects.toString(registre.getEntitatCodi(), "") + ")").append("</td>")
					.append("			</tr>")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.aplicacio")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getAplicacioCodi(), "")).append(Objects.toString(registre.getAplicacioVersio(), "")).append("</td>")
					.append("			</tr>		")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.usuari")).append("</th>")
					.append("				<td>").append(Objects.toString(registre.getUsuariNom(), "")).append(" (" + Objects.toString(registre.getUsuariCodi(), "") + ")").append("</td>")
					.append("			</tr>	")
					.append("			<tr>")
					.append("				<th>").append(messageHelper.getMessage("registre.detalls.camp.distribucio.alta")).append("</th>")
					.append("				<td>").append(registreCreatedDate).append("</td>")
					.append("			</tr>")		
					.append("		</table>")				
					.append(htmlJustificant)
					.append(htmlInteressatsTable) 
					.append(htmlAnnexosTable)
					.append("	</div>")
					.append("	<div class=\"footer\">")
					.append("	<span class=\"footerText\">")
					.append("		Distribució - Govern Illes Baleares")
					.append("	</span>")

					.append("	</div>")
					.append("</body>")
					.append("</html>");
		
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
		entityComprovarHelper.comprovarBustia(
				entitat,
				id,
				false);
		permisosHelper.updatePermis(
				id,
				BustiaEntity.class,
				permis);
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
		
		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
		
		ArbreDto<UnitatOrganitzativaDto> arbre = bustiaHelper.getArbreUnitatsSuperiors(entitat, filtre, null);
				
		return arbre.toDadesList();
	}

	@Transactional
	@Override
	public void addToFavorits(Long entitatId, Long bustiaId) {
		final Timer timeraddToFavorits = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "addToFavorits"));
		Timer.Context contextaddToFavorits = timeraddToFavorits.time();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false);
		
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat, 
				bustiaId, 
				false);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuariActual = usuariRepository.findById(auth.getName()).orElse(null);
		
		UsuariBustiaFavoritEntity usuariBustiaFavorit = UsuariBustiaFavoritEntity.getBuilder(
				bustia, 
				usuariActual).build();
		usuariBustiaFavoritRepository.save(usuariBustiaFavorit);
		contextaddToFavorits.stop();
	}

	@Transactional
	@Override
	public PaginaDto<UsuariBustiaFavoritDto> getBustiesFavoritsUsuariActual(
			Long entitatId, 
			PaginacioParamsDto paginacioParams) {
		final Timer timergetBustiesFavoritsUsuariActual = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "getBustiesFavoritsUsuariActual"));
		Timer.Context contextgetBustiesFavoritsUsuariActual = timergetBustiesFavoritsUsuariActual.time();
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuariActual = usuariRepository.getReferenceById(auth.getName());
		
		Page<UsuariBustiaFavoritEntity> usuariBustiesFavorits = usuariBustiaFavoritRepository.findByUsuari(
				usuariActual, 
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		PaginaDto<UsuariBustiaFavoritDto> paginaDto = paginacioHelper.toPaginaDto(
				usuariBustiesFavorits, 
				UsuariBustiaFavoritDto.class,
				new Converter<UsuariBustiaFavoritEntity, UsuariBustiaFavoritDto>() { //només necessitam el nom de la búsita i l'id de l'entity
					@Override
					public UsuariBustiaFavoritDto convert(UsuariBustiaFavoritEntity source) {
						return bustiaHelper.toUsuariBustiaFavoritDto(source);
					}
				});
		contextgetBustiesFavoritsUsuariActual.stop();
		return paginaDto;
	}
	
	@Transactional
	@Override
	public List<Long> getIdsBustiesFavoritsUsuariActual(Long entitatId) {
		final Timer timergetBustiesFavoritsUsuariActual = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "getIdsBustiesFavoritsUsuariActual"));
		Timer.Context contextgetBustiesFavoritsUsuariActual = timergetBustiesFavoritsUsuariActual.time();
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false);
		List<Long> idsBustiesFavorits = new ArrayList<Long>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuariActual = usuariRepository.getReferenceById(auth.getName());
		
		List<UsuariBustiaFavoritEntity> usuariBustiesFavorits = usuariBustiaFavoritRepository.findByUsuari(usuariActual);
		for (UsuariBustiaFavoritEntity usuariBustiaFavoritEntity : usuariBustiesFavorits) {
			idsBustiesFavorits.add(usuariBustiaFavoritEntity.getBustia().getId());
		}
		contextgetBustiesFavoritsUsuariActual.stop();
		return idsBustiesFavorits;
	}
	
	@Transactional
	@Override
	public void removeFromFavorits(
			Long entitatId, 
			Long id) {
		final Timer timerremoveFromFavorits = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "removeFromFavorits"));
		Timer.Context contextremoveFromFavorits = timerremoveFromFavorits.time();
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false);
		
		//cercar per id bustia/usuari o per id taula favorits
		UsuariBustiaFavoritEntity usuariBustiaFavorit = usuariBustiaFavoritRepository.findById(id).orElse(null);
		if (usuariBustiaFavorit == null)  {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			BustiaEntity bustia = bustiaRepository.getReferenceById(id);
			UsuariEntity usuari = usuariRepository.findByCodi(auth.getName());
			
			usuariBustiaFavorit = usuariBustiaFavoritRepository.findByBustiaAndUsuari(bustia, usuari);
		}
		usuariBustiaFavoritRepository.delete(usuariBustiaFavorit);
		contextremoveFromFavorits.stop();
	}
	
	@Transactional
	@Override
	public boolean checkIfFavoritExists(
			Long entitatId, 
			Long id) {
		final Timer timerremoveFromFavorits = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "checkIfFavoritExists"));
		Timer.Context contextremoveFromFavorits = timerremoveFromFavorits.time();
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false, 
				false);		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		BustiaEntity bustia = bustiaRepository.getReferenceById(id);
		UsuariEntity usuari = usuariRepository.findByCodi(auth.getName());
		
		Long usuariBustiaFavoritId = usuariBustiaFavoritRepository.findIdByBustiaAndUsuari(bustia, usuari);
		contextremoveFromFavorits.stop();
		return (usuariBustiaFavoritId != null) ? true : false;
	}
	
	private boolean isPermesReservarAnotacions() {
		return configHelper.getAsBoolean("es.caib.distribucio.anotacions.permetre.reservar");
	}
	
	private boolean isPermesSobreescriureAnotacions() {
		return configHelper.getAsBoolean("es.caib.distribucio.sobreescriure.anotacions.duplicades");
	}
	
	private boolean isPermesAssignarAnotacions() {
		return configHelper.getAsBoolean("es.caib.distribucio.assignar.anotacions");
	}
	
	private boolean isMantenirEstatReenviades() {
		return configHelper.getAsBoolean("es.caib.distribucio.anotacions.reenviades.mantenir.estat");
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BustiaDadesObertesDto> findBustiesPerDadesObertes(
			Long id, 
			String uo, 
			String uoSuperior) {

		List<BustiaDadesObertesDto> resultat = new ArrayList<BustiaDadesObertesDto>();
		
		// Crea la llista d'unitats orgàniques superiors
		boolean isCodisUosSuperiorsEmpty = false;
		List<String> codisUosSuperiors = new ArrayList<String>();
		UnitatOrganitzativaEntity uoEntity = null;
		if (uo != null && !uo.isEmpty()) {
			List<UnitatOrganitzativaEntity> uos = unitatOrganitzativaRepository.findAllByCodi(uo);
			if (uos.isEmpty())
				return resultat;
			codisUosSuperiors.add(uo);
		} else if (uoSuperior != null && !uoSuperior.isEmpty()) {
			// Arbre d'unitats superiors
			List<UnitatOrganitzativaEntity> uosSuperiorsEntity = unitatOrganitzativaRepository.findAllByCodi(uoSuperior);
			if (uosSuperiorsEntity.isEmpty())
				return resultat;
			for (UnitatOrganitzativaEntity uoSuperiorEntity : uosSuperiorsEntity) {
				EntitatEntity entitat = entitatRepository.findByCodiDir3(uoSuperiorEntity.getCodiDir3Entitat());
				codisUosSuperiors.addAll(bustiaHelper.getCodisUnitatsSuperiors(entitat, uoSuperior));
			}
		} else {
			// no es filtra per UO
			isCodisUosSuperiorsEmpty = true;
		}
		if (codisUosSuperiors.isEmpty()) {
			// Per a que la consulta no falli
			codisUosSuperiors.add("-");
		}

		// Troba la llista de bústies
		List<BustiaEntity> busties = bustiaRepository.findBustiesPerDadesObertes(
				id == null,
				id != null? id : 0L,
				uoEntity == null,
				uoEntity,
				isCodisUosSuperiorsEmpty,
				codisUosSuperiors);
		
		for (BustiaEntity bustia : busties ) {			
			resultat.add(adBustiaDto(bustia));			
		}

		return resultat;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsuariDadesObertesDto> findBustiesUsuarisPerDadesObertes(String usuari, Long id, String uoDir3Entitat, String uo,
			String uoSuperior, Boolean rol, Boolean permis) {
		
		List<UsuariDadesObertesDto> resultat = new ArrayList<UsuariDadesObertesDto>();
		
		if (rol != null && rol == false && permis != null && permis == false)
			return resultat;
		
		// Crea la llista d'unitats orgàniques superiors
		boolean isCodisUosSuperiorsEmpty = false;
		List<String> codisUosSuperiors = new ArrayList<String>();
		UnitatOrganitzativaEntity uoEntity = null;
		if (uo != null && !uo.isEmpty()) {
            List<UnitatOrganitzativaEntity> uoEntityList = unitatOrganitzativaRepository.findAllByCodi(uo);
			if (uoEntityList.isEmpty())
				return resultat;
			codisUosSuperiors.add(uo);
		} else if (uoSuperior != null && !uoSuperior.isEmpty()) {
			// Arbre d'unitats superiors
            List<UnitatOrganitzativaEntity> uoSuperiorEntityList = unitatOrganitzativaRepository.findByCodiUnitatSuperior(uoSuperior);
			if (uoSuperiorEntityList.isEmpty())
				return resultat;
            for(UnitatOrganitzativaEntity uoSuperiorEntity :uoSuperiorEntityList) {
                EntitatEntity entitat = entitatRepository.findByCodiDir3(uoSuperiorEntity.getCodiDir3Entitat());
                if (entitat != null) { // No hi ha entitat per aquesta UO superior
                	codisUosSuperiors.addAll(bustiaHelper.getCodisUnitatsSuperiors(entitat, uoSuperior));
                }
            }
		} else {
			// no es filtra per UO
			isCodisUosSuperiorsEmpty = true;
		}
		if (codisUosSuperiors.isEmpty()) {
			// Per a que la consulta no falli
			codisUosSuperiors.add("-");
		}

		// Troba la llista de bústies
		List<BustiaEntity> busties = bustiaRepository.findBustiesPerDadesObertes(
				id == null,
				id != null? id : 0L,
				uoEntity == null,
				uoEntity,
				isCodisUosSuperiorsEmpty,
				codisUosSuperiors);

		// Troba tots els usuaris per les diferents bústies i filtra el resultat segons l'usuari i si es filtra per rol o permís
		for (BustiaEntity bustia : busties) {
			List<UsuariPermisDto> usuarisPermis = this.getUsuarisPerBustia(bustia.getId());
			for (UsuariPermisDto usuariPermis : usuarisPermis) {
				if ( (usuari == null || usuari.trim().isEmpty() || usuariPermis.getCodi().equals(usuari))
						&& (rol == null || (rol.booleanValue() == (usuariPermis.getRols().size() > 0)))
						&& (permis == null || (permis.booleanValue() == usuariPermis.isHasUsuariPermission()) ))
				{
						resultat.add(adUsuariDto(usuariPermis, bustia));
				}
			}
		}
		
		return resultat;
	}
	
	private BustiaDadesObertesDto adBustiaDto(BustiaEntity bustia) {
		BustiaDadesObertesDto bustiaDto = new BustiaDadesObertesDto();
		String codiUOsuperior = bustia.getUnitatOrganitzativa().getCodiUnitatSuperior();
		
		if (codiUOsuperior.contains("A999999")) {
			codiUOsuperior = bustia.getEntitat().getCodiDir3();
		}
		bustiaDto.setId(bustia.getId());
		bustiaDto.setNom(bustia.getNom());
		bustiaDto.setUO(bustia.getUnitatOrganitzativa().getCodi());
		bustiaDto.setUoNom(bustia.getUnitatOrganitzativa().getDenominacio());
		bustiaDto.setUOsuperior(bustia.getUnitatOrganitzativa().getCodiUnitatSuperior());	
		UnitatOrganitzativaEntity uoDtosuperiorEntity = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(bustia.getEntitat().getCodiDir3(), codiUOsuperior);
		if (uoDtosuperiorEntity != null) {
			String nomUnitatSuperior = CercarNomUnitatSuperior(bustia.getEntitat().getCodiDir3(), uoDtosuperiorEntity.getCodi());
			bustiaDto.setUOsuperiorNom(nomUnitatSuperior);
		}
		bustiaDto.setPerDefecte(bustia.isPerDefecte());
		
		return bustiaDto;
	}
	
	private UsuariDadesObertesDto adUsuariDto(UsuariPermisDto usuariPermis, BustiaEntity bustia) {
		
		UsuariDadesObertesDto usuariDadesObertes = new UsuariDadesObertesDto();
		usuariDadesObertes.setUsuari(usuariPermis.getCodi());
		usuariDadesObertes.setUsuariNom(usuariPermis.getNom());
		usuariDadesObertes.setBustiaId(bustia.getId());
		usuariDadesObertes.setBustiaNom(bustia.getNom());
		usuariDadesObertes.setUo(bustia.getUnitatOrganitzativa().getCodi());
		usuariDadesObertes.setUoNom(bustia.getUnitatOrganitzativa().getDenominacio());
		usuariDadesObertes.setUoSuperior(bustia.getUnitatOrganitzativa().getCodiUnitatSuperior());
		UnitatOrganitzativaDto uoSuperiorEntity = null;
		if (bustia.getUnitatOrganitzativa().getCodiUnitatSuperior().contains("A99999")) {
			uoSuperiorEntity = unitatOrganitzativaHelper.findByCodiDir3EntitatAndCodi(bustia.getEntitat().getCodiDir3(), bustia.getEntitat().getCodiDir3());
		} else {
			uoSuperiorEntity = unitatOrganitzativaHelper.findByCodiDir3EntitatAndCodi(bustia.getEntitat().getCodiDir3(), bustia.getUnitatOrganitzativa().getCodiUnitatSuperior());
		}
		if (uoSuperiorEntity != null) {
			usuariDadesObertes.setUoSuperiorNom(uoSuperiorEntity.getDenominacio());
		}
		usuariDadesObertes.setRol(usuariPermis.getRols().size() > 0);
		usuariDadesObertes.setPermis(usuariPermis.isHasUsuariPermission());

		return usuariDadesObertes;
		
	}
	
	private String CercarNomUnitatSuperior (String codiDir3Entitat, String codi) {
		String nomUnitatSuperior = "";
		boolean continuaCercant = true;
		UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(codiDir3Entitat, codi);
		while(continuaCercant) {
			if (unitatOrganitzativaEntity == null) {
		        break;
		    }
			
			String codiSuperior = unitatOrganitzativaEntity.getCodiUnitatSuperior();
			
			if (codiSuperior.equals(unitatOrganitzativaEntity.getCodiDir3Entitat()) 
				|| codiSuperior.contains("A99999")) {
				nomUnitatSuperior = unitatOrganitzativaEntity.getDenominacio();
				continuaCercant = false;
			} else {
				unitatOrganitzativaEntity = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(codiDir3Entitat, codiSuperior);
			}
		}
		
		return nomUnitatSuperior;
	}	

	
	private static final Logger logger = LoggerFactory.getLogger(BustiaServiceImpl.class);
}
