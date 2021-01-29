/**
 * 
 */
package es.caib.distribucio.core.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.AnotacioRegistreFiltreDto;
import es.caib.distribucio.core.api.dto.ArxiuContingutDto;
import es.caib.distribucio.core.api.dto.ArxiuContingutTipusEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto.ClassificacioResultatEnumDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.RegistreEnviatPerEmailEnumDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.ws.backoffice.Annex;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.DocumentTipus;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;
import es.caib.distribucio.core.api.service.ws.backoffice.Interessat;
import es.caib.distribucio.core.api.service.ws.backoffice.InteressatBase;
import es.caib.distribucio.core.api.service.ws.backoffice.InteressatTipus;
import es.caib.distribucio.core.api.service.ws.backoffice.NtiEstadoElaboracion;
import es.caib.distribucio.core.api.service.ws.backoffice.NtiOrigen;
import es.caib.distribucio.core.api.service.ws.backoffice.NtiTipoDocumento;
import es.caib.distribucio.core.api.service.ws.backoffice.Representant;
import es.caib.distribucio.core.api.service.ws.backoffice.SicresTipoDocumento;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ContingutLogHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.GestioDocumentalHelper;
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
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.RegistreAnnexRepository;
import es.caib.distribucio.core.repository.RegistreFirmaDetallRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;

/**
 * Implementació dels mètodes per a gestionar anotacions
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class RegistreServiceImpl implements RegistreService {

	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	RegistreFirmaDetallRepository registreFirmaDetallRepository;
	@Autowired
	private BustiaRepository bustiaRepository;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private BustiaHelper bustiaHelper;
	@Autowired
	private RegistreHelper registreHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ReglaHelper reglaHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private GestioDocumentalHelper gestioDocumentalHelper;	
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	
	
	@Resource
	private ContingutLogHelper contingutLogHelper;
	@Resource
	private UsuariHelper usuariHelper;
	
	@Transactional(readOnly = true)
	@Override
	public RegistreDto findOne(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);

		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		
		if (registre == null) {
			registre = registreRepository.findOne(registreId);
			if (registre == null) {
				throw new NotFoundException(registreId, RegistreEntity.class);
			} else {
				throw new NotFoundException(registreId, RegistreEntity.class, registre.getPare().getNom());
			}
			
		} else {
			
			RegistreDto registreAnotacio = (RegistreDto)contingutHelper.toContingutDto(
					registre,
					false,
					false,
					false,
					false,
					true,
					false,
					true);
			contingutHelper.tractarInteressats(registreAnotacio.getInteressats());	

			// Traiem el justificant de la llista d'annexos si té el mateix id o uuid
			for (RegistreAnnexDto annexDto : registreAnotacio.getAnnexos()) {
				if ((registre.getJustificant() != null && registreAnotacio.getJustificant().getId().equals(annexDto.getId()))
						|| registre.getJustificantArxiuUuid() != null && registre.getJustificantArxiuUuid().equals(annexDto.getFitxerArxiuUuid()) ) {
					registreAnotacio.getAnnexos().remove(annexDto);
					break;
				}
			}
			
			return registreAnotacio;
		}

	}


	@Override
	@Transactional(readOnly = true)
	public List<RegistreDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds) {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "multipleRegistreIds=" + multipleRegistreIds + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		List<BustiaEntity> bustiesPermeses = bustiaRepository.findByEntitatAndPareNotNullOrderByNomAsc(entitat);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAll(
				bustiesPermeses,
				new ObjectIdentifierExtractor<BustiaEntity>() {
					@Override
					public Long getObjectIdentifier(BustiaEntity bustia) {
						return bustia.getId();
					}
				},
				BustiaEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		List<RegistreEntity> registres = registreRepository.findByPareInAndIdIn(
				bustiesPermeses,
				multipleRegistreIds);
		List<RegistreDto> resposta = new ArrayList<RegistreDto>();
		for (RegistreEntity registre: registres) {
			resposta.add((RegistreDto)contingutHelper.toContingutDto(
					registre,
					false,
					false,
					false,
					false,
					true,
					false,
					true));
		}
		return resposta;
	}


	@Transactional(readOnly = true)
	@Override
	public PaginaDto<RegistreDto> findRegistreAdmin(
			Long entitatId,
			AnotacioRegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta d'anotacions de registre per usuari admin (" +
				"entitatId=" + entitatId + ", " +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		Date dataInici = filtre.getDataCreacioInici();
		if (dataInici != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dataInici = cal.getTime();
		}
		Date dataFi = filtre.getDataCreacioFi();
		if (dataFi != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataFi = cal.getTime();
		}
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());

		logger.debug(">>> Filtre: " + filtre);
		Page<RegistreEntity> registres = registreRepository.findByFiltrePaginat(
				entitat, 
				(filtre.getNom() == null || filtre.getNom().isEmpty()),
				filtre.getNom() != null ? filtre.getNom().trim() : "",
				(filtre.getNumeroOrigen() == null) || filtre.getNumeroOrigen().isEmpty(),
				filtre.getNumeroOrigen() != null? filtre.getNumeroOrigen().trim() : "",
				(unitat == null),
				unitat,
				(filtre.getBustia() == null),
				(filtre.getBustia() != null ? Long.parseLong(filtre.getBustia()) : null),
				(dataInici == null),
				dataInici,
				(dataFi == null),
				dataFi,
				(filtre.getEstat() == null),
				filtre.getEstat(),
				filtre.isNomesAmbErrors(),
				(filtre.getBackCodi() == null || filtre.getBackCodi().isEmpty()),
				filtre.getBackCodi() != null? filtre.getBackCodi().trim() : "",
				paginacioHelper.toSpringDataPageable(paginacioParams));
		return paginacioHelper.toPaginaDto(
				registres,
				RegistreDto.class,
				new Converter<RegistreEntity, RegistreDto>() {
					@Override
					public RegistreDto convert(RegistreEntity source) {
						return (RegistreDto)contingutHelper.toContingutDto(
								source,
								false,
								false,
								false,
								false,
								true,
								false,
								true);
					}
				});
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<Long> findRegistreAdminIdsAmbFiltre(
			Long entitatId,
			AnotacioRegistreFiltreDto filtre) {
		logger.debug("Consulta d'anotacions de registre per usuari admin (" +
				"entitatId=" + entitatId + ", " +
				"filtre=" + filtre  +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		Date dataInici = filtre.getDataCreacioInici();
		if (dataInici != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dataInici = cal.getTime();
		}
		Date dataFi = filtre.getDataCreacioFi();
		if (dataFi != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataFi = cal.getTime();
		}
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());
		
		logger.debug(">>> Filtre: " + filtre);
		List<Long> registres = registreRepository.findIdsByFiltre(
				entitat, 
				(filtre.getNom() == null || filtre.getNom().isEmpty()),
				filtre.getNom() != null? filtre.getNom().trim() : "",
				(filtre.getNumeroOrigen() == null) || filtre.getNumeroOrigen().isEmpty(),
				filtre.getNumeroOrigen() != null? filtre.getNumeroOrigen().trim() : "",
				(unitat == null),
				unitat,
				(filtre.getBustia() == null),
				(filtre.getBustia() != null ? Long.parseLong(filtre.getBustia()) : null),
				(dataInici == null),
				dataInici,
				(dataFi == null),
				dataFi,
				(filtre.getEstat() == null),
				filtre.getEstat(),
				filtre.isNomesAmbErrors(),
				(filtre.getBackCodi() == null || filtre.getBackCodi().isEmpty()),
				filtre.getBackCodi() != null? filtre.getBackCodi().trim() : "");
		return registres;
	}

	

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutDto> findRegistreUser(
			Long entitatId,
			List<BustiaDto> bustiesPermesesPerUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistreUser"));
		Timer.Context contextTotal = timerTotal.time();

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Comprova la bústia i que l'usuari hi tengui accés
		BustiaEntity bustia = null;
		if (filtre.getBustia() != null && !filtre.getBustia().isEmpty()) {
			bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustia()),
					true);
		}
		String bustiesIds="";
			
		List<Long> busties = new ArrayList<Long>();
		if (bustiesPermesesPerUsuari != null && !bustiesPermesesPerUsuari.isEmpty()) { 
			for (BustiaDto bustiaUsuari: bustiesPermesesPerUsuari) {
				busties.add(bustiaUsuari.getId());
				bustiesIds +=  bustiaUsuari.getId() + ", ";
			}
		} else if (bustia != null) {	
			busties.add(bustia.getId());
		}
		

		Map<String, String[]> mapeigOrdenacio = new HashMap<String, String[]>();
		mapeigOrdenacio.put(
				"darrerMovimentData",
				new String[] {"darrerMoviment.createdDate"});
		mapeigOrdenacio.put(
				"darrerMovimentUsuari.nom",
				new String[] {"remitent.nom"});
		mapeigOrdenacio.put(
				"darrerMovimentComentari",
				new String[] {"darrerMoviment.comentari"});
		Page<RegistreEntity> pagina;
		
		// Hibernate doesn't support empty collection as parameter in database query so if busties is empty we dont execute query but just create a new empty pagina 
		if (bustia == null && busties.isEmpty()) {
			pagina = new PageImpl<RegistreEntity>(new ArrayList<RegistreEntity>());
		} else {
			
			boolean esPendent = RegistreProcesEstatSimpleEnumDto.PENDENT.equals(filtre.getProcesEstatSimple()); 
			boolean esProcessat = RegistreProcesEstatSimpleEnumDto.PROCESSAT.equals(filtre.getProcesEstatSimple());;

			Boolean enviatPerEmail = null;
			if (filtre.getRegistreEnviatPerEmailEnum() != null) {
				if (filtre.getRegistreEnviatPerEmailEnum() == RegistreEnviatPerEmailEnumDto.ENVIAT) {
					enviatPerEmail = true;
				} else {
					enviatPerEmail = false;
				}
			}
			
			String tipusFisicaCodi = null;
			if (filtre.getTipusDocFisica() != null) {
				tipusFisicaCodi = String.valueOf(filtre.getTipusDocFisica().getValue());
			}
			
			
			Date dataRecepcioFi = filtre.getDataRecepcioFi();
			if (dataRecepcioFi != null) {
				Calendar c = new GregorianCalendar();
				c.setTime(dataRecepcioFi);
				c.add(Calendar.HOUR, 24);
				dataRecepcioFi = c.getTime();
			}
			logger.info("Consultant el contingut de l'usuari ("
					+ "entitatId=" + entitatId + ", "
					+ "bustiaId=" + filtre.getBustia() + ", "
					+ "numero=" + filtre.getNumero() + ", "
					+ "titol=" + filtre.getTitol() + ", "
					+ "numeroOrigen=" + filtre.getNumeroOrigen() + ", "
					+ "remitent=" + filtre.getRemitent() + ", "
					+ "dataRecepcioInici=" + filtre.getDataRecepcioInici() + ", "
					+ "dataRecepcioFi=" + filtre.getDataRecepcioFi() + ", "
					+ "estatContingut=" + filtre.getProcesEstatSimple() + ", "
					+ "interessat=" + filtre.getInteressat() + ", " 
					+ "bustiesIds= " + bustiesIds + ", " 
					+ "paginacioParams=" + "[paginaNum=" + paginacioParams.getPaginaNum() + ", paginaTamany=" + paginacioParams.getPaginaTamany() + ", ordres=" + paginacioParams.getOrdres() + "]" + ")");

			final Timer timerTotalfindRegistreByPareAndFiltre = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistreUser.findRegistreByPareAndFiltre"));
			Timer.Context contextTotalfindRegistreByPareAndFiltre = timerTotalfindRegistreByPareAndFiltre.time();
			long beginTime = new Date().getTime();
			try {
				pagina = registreRepository.findRegistreByPareAndFiltre(
						busties,
						StringUtils.isEmpty(filtre.getNumero()),
						filtre.getNumero() != null ? filtre.getNumero() : "",
						StringUtils.isEmpty(filtre.getTitol()),
						filtre.getTitol() != null ? filtre.getTitol() : "",
						filtre.getNumeroOrigen() == null || filtre.getNumeroOrigen().isEmpty(),
						filtre.getNumeroOrigen() != null ? filtre.getNumeroOrigen() : "",
						filtre.getRemitent() == null || filtre.getRemitent().isEmpty(),
						filtre.getRemitent() != null ? filtre.getRemitent() : "",
						(filtre.getDataRecepcioInici() == null),
						filtre.getDataRecepcioInici(),
						(dataRecepcioFi == null),
						dataRecepcioFi,
						esProcessat,
						esPendent,
						filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
						filtre.getInteressat() != null ? filtre.getInteressat() : "",
						enviatPerEmail == null,
						enviatPerEmail,
						tipusFisicaCodi == null,
						tipusFisicaCodi,
						paginacioHelper.toSpringDataPageable(paginacioParams,
								mapeigOrdenacio));
				contextTotalfindRegistreByPareAndFiltre.stop();
				long endTime = new Date().getTime();
				logger.info("findRegistreByPareAndFiltre executed with no errors in: " + (endTime - beginTime) + "ms");
			} catch (Exception e) {
				long endTime = new Date().getTime();
				logger.error("findRegistreByPareAndFiltre executed with errors in: " + (endTime - beginTime) + "ms", e);
				contextTotalfindRegistreByPareAndFiltre.stop();
				throw new RuntimeException(e);
			}
		}
		
		final Timer timerTotaltoPaginaDto = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistreUser.toPaginaDto"));
		Timer.Context contextTotaltoPaginaDto = timerTotaltoPaginaDto.time();

		PaginaDto<ContingutDto> pag = paginacioHelper.toPaginaDto(
				pagina,
				ContingutDto.class,
				new Converter<RegistreEntity, ContingutDto>() {
					@Override
					public ContingutDto convert(RegistreEntity source) {
						return contingutHelper.toContingutDto(
								source,
								false,
								false,
								false,
								false,
								true,
								false,
								true);
					}
				});
		contextTotaltoPaginaDto.stop();
		
		contextTotal.stop();
		return pag;
	}


	@Transactional(readOnly = true)
	@Override
	public AnotacioRegistreEntrada findOneForBackoffice(
			AnotacioRegistreId id)  {
		logger.debug("Obtenint anotació de registre per backoffice("
				+ "id=" + id + ")");
		AnotacioRegistreEntrada anotacioPerBackoffice = new AnotacioRegistreEntrada();
		try {
			// check if anotacio was sent with correct key
			String clauSecreta = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.backoffice.integracio.clau");
			if (clauSecreta == null) {
				throw new RuntimeException("Clau secreta no specificada al fitxer de propietats");
			}
			String encryptedIdentificator = RegistreHelper.encrypt(
					id.getIndetificador(),
					clauSecreta);
			if (!encryptedIdentificator.equals(id.getClauAcces())) {
				throw new RuntimeException("La clau o identificador és incorrecte");
			}
			RegistreEntity registreEntity = registreRepository.findByNumero(id.getIndetificador());
			anotacioPerBackoffice.setIdentificador(registreEntity.getNumero());
			anotacioPerBackoffice.setData(registreEntity.getData());
			anotacioPerBackoffice.setExtracte(registreEntity.getExtracte());
			anotacioPerBackoffice.setEntitatCodi(registreEntity.getEntitatCodi());
			anotacioPerBackoffice.setEntitatDescripcio(registreEntity.getEntitatDescripcio());
			anotacioPerBackoffice.setUsuariCodi(registreEntity.getUsuariCodi());
			anotacioPerBackoffice.setUsuariNom(registreEntity.getUsuariNom());
			anotacioPerBackoffice.setOficinaCodi(registreEntity.getOficinaCodi());
			anotacioPerBackoffice.setOficinaDescripcio(registreEntity.getOficinaDescripcio());
			anotacioPerBackoffice.setLlibreCodi(registreEntity.getLlibreCodi());
			anotacioPerBackoffice.setLlibreDescripcio(registreEntity.getLlibreDescripcio());
			anotacioPerBackoffice.setDocFisicaCodi(registreEntity.getDocumentacioFisicaCodi());
			anotacioPerBackoffice.setDocFisicaDescripcio(registreEntity.getDocumentacioFisicaDescripcio());
			anotacioPerBackoffice.setAssumpteTipusCodi(registreEntity.getAssumpteTipusCodi());
			anotacioPerBackoffice.setAssumpteTipusDescripcio(registreEntity.getAssumpteTipusDescripcio());
			anotacioPerBackoffice.setAssumpteCodiCodi(registreEntity.getAssumpteCodi());
			anotacioPerBackoffice.setProcedimentCodi(registreEntity.getProcedimentCodi());
			anotacioPerBackoffice.setAssumpteCodiDescripcio(registreEntity.getAssumpteDescripcio());
			anotacioPerBackoffice.setTransportTipusCodi(registreEntity.getTransportTipusCodi());
			anotacioPerBackoffice.setTransportTipusDescripcio(registreEntity.getTransportTipusDescripcio());
			anotacioPerBackoffice.setTransportNumero(registreEntity.getTransportNumero());
			anotacioPerBackoffice.setIdiomaCodi(registreEntity.getIdiomaCodi());
			anotacioPerBackoffice.setIdomaDescripcio(registreEntity.getIdiomaDescripcio());
			anotacioPerBackoffice.setObservacions(registreEntity.getObservacions());
			anotacioPerBackoffice.setOrigenRegistreNumero(registreEntity.getNumeroOrigen());
			anotacioPerBackoffice.setOrigenData(registreEntity.getDataOrigen());
			anotacioPerBackoffice.setAplicacioCodi(registreEntity.getAplicacioCodi());
			anotacioPerBackoffice.setAplicacioVersio(registreEntity.getAplicacioVersio());
			anotacioPerBackoffice.setRefExterna(registreEntity.getReferencia());
			anotacioPerBackoffice.setExpedientNumero(registreEntity.getExpedientNumero());
			anotacioPerBackoffice.setExposa(registreEntity.getExposa());
			anotacioPerBackoffice.setSolicita(registreEntity.getSolicita());
			anotacioPerBackoffice.setDestiCodi(registreEntity.getUnitatAdministrativa());
			anotacioPerBackoffice.setDestiDescripcio(registreEntity.getUnitatAdministrativaDescripcio());
			anotacioPerBackoffice.setInteressats(toInteressats(registreEntity.getInteressats()));
			anotacioPerBackoffice.setAnnexos(getAnnexosPerBackoffice(registreEntity.getId()));
		} catch (Exception ex){
			throw new RuntimeException(ex);
		}
		return anotacioPerBackoffice;
	}

	@SuppressWarnings("incomplete-switch")
	@Transactional
	@Override
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		try {
			// check if anotacio was sent with correct key
			String clauSecreta = PropertiesHelper.getProperties().getProperty(
					"es.caib.distribucio.backoffice.integracio.clau");
			if (clauSecreta == null)
				throw new RuntimeException("Clau secreta no specificada al fitxer de propietats");
			String encryptedIdentificator = RegistreHelper.encrypt(id.getIndetificador(),
					clauSecreta);
			if (!encryptedIdentificator.equals(id.getClauAcces()))
				throw new RuntimeException("La clau o identificador és incorrecte");
			RegistreEntity registre = registreRepository.findByNumero(id.getIndetificador());
			switch (estat) {
			case REBUDA:
				registre.updateBackEstat(
						RegistreProcesEstatEnum.BACK_REBUDA,
						observacions);
				registre.updateBackRebudaData(new Date());
				contingutLogHelper.log(
						registre,
						LogTipusEnumDto.BACK_REBUDA,
						null,
						false);
				break;
			case PROCESSADA:
				registre.updateBackEstat(
						RegistreProcesEstatEnum.BACK_PROCESSADA,
						observacions);
				registre.updateBackProcesRebutjErrorData(new Date());
				registreHelper.tancarExpedientArxiu(registre.getId());
				contingutLogHelper.log(
						registre,
						LogTipusEnumDto.BACK_PROCESSADA,
						null,
						false);
				break;
			case REBUTJADA:
				registre.updateBackEstat(
						RegistreProcesEstatEnum.BACK_REBUTJADA,
						observacions);
				registre.updateBackProcesRebutjErrorData(new Date());
				contingutLogHelper.log(
						registre,
						LogTipusEnumDto.BACK_REBUTJADA,
						null,
						false);				
				break;
			case ERROR:
				registre.updateBackEstat(
						RegistreProcesEstatEnum.BACK_ERROR,
						observacions);
				registre.updateBackProcesRebutjErrorData(new Date());
				contingutLogHelper.log(
						registre,
						LogTipusEnumDto.BACK_ERROR,
						null,
						false);
				break;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Transactional
	@Override
	public RegistreAnnexDto getRegistreJustificant(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException {
		
		final Timer timegetRegistreJustificant = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "getRegistreJustificant"));
		Timer.Context contexgetRegistreJustificant = timegetRegistreJustificant.time();
		
		
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		boolean comprovarPermisBustia = ! usuariHelper.isAdmin();
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					comprovarPermisBustia);

		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		RegistreAnnexDto justificant = getJustificantPerRegistre(
					entitat, 
					registre);
		justificant.setRegistreId(registreId);
		
		contexgetRegistreJustificant.stop();
		return justificant;
	}

	@Transactional(readOnly = true)
	@Override
	public List<RegistreAnnexDto> getAnnexos(
			Long entitatId,
			Long bustiaId,
			Long registreId	
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		boolean comprovarPermisBustia = ! usuariHelper.isAdmin();
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					comprovarPermisBustia);

		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		List<RegistreAnnexDto> anexosDto = new ArrayList<>();
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			
			RegistreAnnexDto annex = conversioTipusHelper.convertir(
					annexEntity,
					RegistreAnnexDto.class);
			anexosDto.add(annex);
		}
		return anexosDto;
	}

	@Transactional
	@Override
	public void rebutjar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String motiu) {
		logger.debug("Rebutjar anotació de registre a la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(
				registreId,
				null);
		if (!registre.getPare().equals(bustia)) {
			logger.error("No s'ha trobat el registre a dins la bústia (" +
					"bustiaId=" + bustiaId + "," +
					"registreId=" + registreId + ")");
			throw new ValidationException(
					registreId,
					RegistreEntity.class,
					"La bústia especificada (id=" + bustiaId + ") no coincideix amb la bústia de l'anotació de registre");
		}
		registre.updateMotiuRebuig(motiu);
		bustiaHelper.evictCountElementsPendentsBustiesUsuari(
				entitat,
				bustia);
	}

	@Override
	@Transactional
	public boolean reintentarEnviamentBackofficeAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per admins (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");

		List<Long> pendentsIds = new ArrayList<>();
		pendentsIds.add(registreId);
		Throwable exceptionProcessant = registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIds);
		return exceptionProcessant == null;

	}

	@Override
	@Transactional
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per admins (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				false);
		RegistreEntity anotacio = entityComprovarHelper.comprovarRegistre(
				registreId,
				bustia);
		Exception exceptionProcessant = processarAnotacioPendent(anotacio);
		return exceptionProcessant == null;
	}

	@Override
	@Transactional
	public boolean reintentarProcessamentUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per usuaris (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity anotacio = entityComprovarHelper.comprovarRegistre(
				registreId,
				bustia);
		Exception exceptionProcessant = processarAnotacioPendent(anotacio);
		return exceptionProcessant == null;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexFitxer(
			Long annexId) {
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(annexId);
		FitxerDto fitxerDto = new FitxerDto();
		Document document = null;
		
		// if annex is already created in arxiu take content from arxiu
		if (registreAnnexEntity.getFitxerArxiuUuid() != null && !registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
			
			document = pluginHelper.arxiuDocumentConsultar(registreAnnexEntity.getFitxerArxiuUuid(), null, true, true);
			if (document != null) {
				DocumentContingut documentContingut = document.getContingut();
				if (documentContingut != null) {
					fitxerDto.setNom(registreAnnexEntity.getFitxerNom());
					fitxerDto.setContentType(documentContingut.getTipusMime());
					fitxerDto.setContingut(documentContingut.getContingut());
					fitxerDto.setTamany(documentContingut.getContingut().length);
				}
			}

		// if annex is not yet created in arxiu take content from gestio documental
		} else {
			
			// if annex has signature attached, contingut of document is located in firma
			if (registreAnnexEntity.getFirmes() != null && !registreAnnexEntity.getFirmes().isEmpty()) {
				RegistreAnnexFirmaEntity firmaEntity = registreAnnexEntity.getFirmes().get(0);
				if (firmaEntity.getTipus() != "TF02" && firmaEntity.getTipus() != "TF04") {

					if (firmaEntity.getGesdocFirmaId() != null) {
						ByteArrayOutputStream streamAnnexFirma = new ByteArrayOutputStream();
						gestioDocumentalHelper.gestioDocumentalGet(
								firmaEntity.getGesdocFirmaId(), 
								GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, 
								streamAnnexFirma);
						byte[] firmaContingut = streamAnnexFirma.toByteArray();
						
						fitxerDto.setNom(firmaEntity.getFitxerNom());
						fitxerDto.setContentType(firmaEntity.getTipusMime());
						fitxerDto.setContingut(firmaContingut);
						fitxerDto.setTamany(firmaContingut.length);
					}
				}
			} else {
				
				if (registreAnnexEntity.getGesdocDocumentId() != null) {
					ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
					gestioDocumentalHelper.gestioDocumentalGet(
							registreAnnexEntity.getGesdocDocumentId(), 
							GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, 
							streamAnnex);
					byte[] annexContingut = streamAnnex.toByteArray();
					
					fitxerDto.setNom(registreAnnexEntity.getFitxerNom());
					fitxerDto.setContentType(registreAnnexEntity.getFitxerTipusMime());
					fitxerDto.setContingut(annexContingut);
					fitxerDto.setTamany(annexContingut.length);
					
				} 
			}
		}

		return fitxerDto;
	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexFirmaFitxer(
			Long annexId,
			int indexFirma) {
		FitxerDto fitxerDto = new FitxerDto();
		
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(annexId);
		RegistreAnnexFirmaEntity firmaEntity = registreAnnexEntity.getFirmes().get(indexFirma);
		
		// if annex is already created in arxiu take firma content from arxiu
		if (registreAnnexEntity.getFitxerArxiuUuid() != null && !registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
		
			Document document = pluginHelper.arxiuDocumentConsultar(registreAnnexEntity.getFitxerArxiuUuid(), null, true);
			if (document != null) {
				List<Firma> firmes = document.getFirmes();
				if (firmes != null && firmes.size() > 0) {
					
					Iterator<Firma> it = firmes.iterator();
					while (it.hasNext()) {
						Firma firma = it.next();
						if (firma.getTipus() == FirmaTipus.CSV) {
							it.remove();
						}
					}
					Firma firma = firmes.get(indexFirma);
					
					if (firma != null && firmaEntity != null) {
						fitxerDto.setNom(firmaEntity.getFitxerNom());
						fitxerDto.setContentType(firmaEntity.getTipusMime());
						fitxerDto.setContingut(firma.getContingut());
						fitxerDto.setTamany(firma.getContingut().length);
					}
				}
			}
		
		// if annex is not yet created in arxiu take firma content from gestio documental
		} else {
			if (firmaEntity.getGesdocFirmaId() != null) {
				ByteArrayOutputStream streamAnnexFirma = new ByteArrayOutputStream();
				gestioDocumentalHelper.gestioDocumentalGet(
						firmaEntity.getGesdocFirmaId(), 
						GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, 
						streamAnnexFirma);
				byte[] firmaContingut = streamAnnexFirma.toByteArray();
				
				fitxerDto.setNom(firmaEntity.getFitxerNom());
				fitxerDto.setContentType(firmaEntity.getTipusMime());
				fitxerDto.setContingut(firmaContingut);
				fitxerDto.setTamany(firmaContingut.length);
			} 
		}
		
		return fitxerDto;
	}
	
	@Transactional(readOnly = true)
	public FitxerDto getZipDocumentacio(Long registreId) throws Exception {

		FitxerDto zip = new FitxerDto();
				
		RegistreEntity registre = registreRepository.findOne(registreId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		try {
			// Comprova l'accés a la bústia
			entityComprovarHelper.comprovarBustia(
						registre.getEntitat(),
						registre.getPare().getId(),
						true);
			// Justificant
			FitxerDto fitxer;
			// Annexos
			String nom;
			for (RegistreAnnexEntity annex : registre.getAnnexos()) {
				fitxer = this.getAnnexFitxer(annex.getId());
				try {
					if (registre.getJustificant() == null 
							|| annex.getId() != registre.getJustificant().getId()) 
						nom = annex.getTitol() + " - " + fitxer.getNom();
					else
						nom = fitxer.getNom();
					ZipEntry entry = new ZipEntry(revisarContingutNom(nom));
					entry.setSize(fitxer.getContingut().length);
					zos.putNextEntry(entry);
					zos.write(fitxer.getContingut());
					zos.closeEntry();
				} catch (Exception e) {
					String errMsg = "Error afegint l'annex " + annex.getTitol() + " del registre " + registre.getNumero() + " al document zip comprimit: " + e.getMessage();
					logger.error(errMsg, e);
					throw new Exception(errMsg, e);
				}
			}
			zos.close();
			
			zip.setNom(revisarContingutNom(registre.getNumero()) + ".zip");
			zip.setContingut(baos.toByteArray());
			zip.setContentType("application/zip");

		} catch (Exception ex) {
			String errMsg = "Error generant el .zip de documentació pel registre " + registre.getNumero() + " amb ID " + registreId + " : " + ex.getMessage();
			logger.error(errMsg, ex);
			throw new Exception(errMsg, ex);
		}
		return zip;
	}

	private static String revisarContingutNom(String nom) {
		if (nom == null) {
			return null;
		}
		return nom.replace("&", "&amp;").replaceAll("[\\\\/:*?\"<>|]", "_");
	}

	

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getJustificant(
			Long registreId) {
		RegistreEntity registre = registreRepository.findOne(registreId);
		FitxerDto arxiu = new FitxerDto();
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(registre.getJustificantArxiuUuid(), null, true, true);
		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				arxiu.setNom(registreHelper.obtenirJustificantNom(document));
				arxiu.setContentType(documentContingut.getTipusMime());
				arxiu.setContingut(documentContingut.getContingut());
				arxiu.setTamany(documentContingut.getContingut().length);
			}
		}
		return arxiu;
	}


	@Override
	@Transactional
	public RegistreAnnexDto getAnnexAmbFirmes(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			Long annexId) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		boolean comprovarPermisBustia = ! usuariHelper.isAdmin();
		entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				comprovarPermisBustia);
		
		return registreHelper.getAnnexAmbFirmes(
				annexId);
	}

	


	
	
	@Transactional(readOnly = true)
	@Override
	public RegistreAnnexDto getAnnexSenseFirmes(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			Long annexId
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		boolean comprovarPermisBustia = ! usuariHelper.isAdmin();
		entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					comprovarPermisBustia);
		
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(annexId);
		
		RegistreAnnexDto annex = conversioTipusHelper.convertir(
				registreAnnexEntity,
				RegistreAnnexDto.class);
		return annex;
	}



	@Transactional(readOnly = true)
	@Override
	public RegistreDto findAmbIdentificador(String identificador) {
		RegistreDto registreAnotacioDto;
		RegistreEntity registre = registreRepository.findByIdentificador(identificador);
		if (registre != null)
			registreAnotacioDto = (RegistreDto) contingutHelper.toContingutDto(registre);
		else
			registreAnotacioDto = null;
		return registreAnotacioDto;
	}

	@Transactional
	@Override
	public void updateProces(
			Long registreId, 
			RegistreProcesEstatEnum procesEstat, 
			RegistreProcesEstatSistraEnum procesEstatSistra,
			String resultadoProcesamiento) {
		logger.debug("Actualitzar estat procés anotació de registre ("
				+ "registreId=" + registreId + ", "
				+ "procesEstat=" + procesEstat + ", "
				+ "procesEstatSistra=" + procesEstatSistra + ", "
				+ "resultadoProcesamiento=" + resultadoProcesamiento + ")");
		RegistreEntity registre = registreRepository.findOne(registreId);
		registre.updateProces(
				procesEstat,
				resultadoProcesamiento != null ? new Exception(resultadoProcesamiento) : null);
		registre.updateProcesSistra(procesEstatSistra);
	}

	@Transactional (readOnly = true)
	@Override
	public List<String> findPerBackofficeSistra(
			String identificadorProcediment, 
			String identificadorTramit,
			RegistreProcesEstatSistraEnum procesEstatSistra, 
			Date desdeDate, 
			Date finsDate) {
		logger.debug("Consultant els numeros d'entrada del registre pel backoffice Sistra ("
				+ "identificadorProcediment=" + identificadorProcediment + ", "
				+ "identificadorTramit=" + identificadorTramit + ", "
				+ "procesEstatSistra=" + procesEstatSistra + ", "
				+ "desdeDate=" + desdeDate + ", "
				+ "finsDate=" + finsDate + ")");
		return registreRepository.findPerBackofficeSistra(
				identificadorProcediment,
				identificadorTramit,
				procesEstatSistra == null,
				procesEstatSistra,
				desdeDate == null,
				desdeDate,
				finsDate == null,
				finsDate);
	}

	@Transactional
	@Override
	public RegistreDto marcarLlegida(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Marcan com a llegida l'anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);

		RegistreEntity registre = registreRepository.findByPareAndId(
					bustia,
					registreId);
		registre.updateLlegida(true);
		
		return (RegistreDto) contingutHelper.toContingutDto(
				registre);		
	}

	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId) {
		logger.debug("Obtenint informació de l'arxiu per l'anotacio ("
				+ "registreAnotacioId=" + registreAnotacioId + ")");
		RegistreEntity registre = registreRepository.findOne(registreAnotacioId);
		ArxiuDetallDto arxiuDetall = null;
		if (registre.getExpedientArxiuUuid() != null) {
			arxiuDetall = new ArxiuDetallDto();
			es.caib.plugins.arxiu.api.Expedient arxiuExpedient = pluginHelper.arxiuExpedientInfo(registre.getExpedientArxiuUuid());
			List<ContingutArxiu> continguts = arxiuExpedient.getContinguts();
			arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
			arxiuDetall.setNom(arxiuExpedient.getNom());
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataObertura(metadades.getDataObertura());
				arxiuDetall.setEniClassificacio(metadades.getClassificacio());
				if (metadades.getEstat() != null) {
					switch (metadades.getEstat()) {
					case OBERT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
						break;
					case TANCAT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.TANCAT);
						break;
					case INDEX_REMISSIO:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.INDEX_REMISSIO);
						break;
					}
				}
				arxiuDetall.setEniInteressats(metadades.getInteressats());
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
			}
			if (continguts != null) {
				List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
				for (ContingutArxiu cont: continguts) {
					ArxiuContingutDto detallFill = new ArxiuContingutDto();
					detallFill.setIdentificador(
							cont.getIdentificador());
					detallFill.setNom(
							cont.getNom());
					if (cont.getTipus() != null) {
						switch (cont.getTipus()) {
						case EXPEDIENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
							break;
						case DOCUMENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
							break;
						case CARPETA:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
							break;
						}
					}
					detallFills.add(detallFill);
				}
				arxiuDetall.setFills(detallFills);
			}
		}
		return arxiuDetall;
	}

	@Override
	@Transactional
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String procedimentCodi)
			throws NotFoundException {
		logger.debug("classificant l'anotació de registre (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ", " +
				"procedimentCodi=" + procedimentCodi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		registre.updateProcedimentCodi(procedimentCodi);
		ReglaEntity reglaAplicable = reglaHelper.findAplicable(
				entitat,
				bustia.getUnitatOrganitzativa().getId(),
				registre.getPare().getId(),
				registre.getProcedimentCodi(),
				registre.getAssumpteCodi());
		ClassificacioResultatDto classificacioResultat = new ClassificacioResultatDto();
		if (reglaAplicable != null) {
			registre.updateRegla(reglaAplicable);
			bustiaHelper.evictCountElementsPendentsBustiesUsuari(
					entitat,
					bustia);
			List<ReglaEntity> reglesApplied = new ArrayList<ReglaEntity>();
			Exception ex = reglaHelper.aplicarControlantException(registre, reglesApplied);

			if (ex == null) {
				ReglaEntity lastReglaApplied = reglesApplied.get(reglesApplied.size() - 1);
				if (ReglaTipusEnumDto.BUSTIA.equals(lastReglaApplied.getTipus())) {
					classificacioResultat.setResultat(ClassificacioResultatEnumDto.REGLA_BUSTIA);
					BustiaEntity novaBustia = (BustiaEntity)(registreRepository.getOne(registreId).getPare());
					classificacioResultat.setBustiaNom(novaBustia.getNom());
					classificacioResultat.setBustiaUnitatOrganitzativa(
							unitatOrganitzativaHelper.toDto(novaBustia.getUnitatOrganitzativa()));
				} else if (ReglaTipusEnumDto.UNITAT.equals(lastReglaApplied.getTipus())){
					classificacioResultat.setResultat(ClassificacioResultatEnumDto.REGLA_UNITAT);
					BustiaEntity novaBustia = (BustiaEntity)(registreRepository.getOne(registreId).getPare());
					classificacioResultat.setBustiaNom(novaBustia.getNom());
					classificacioResultat.setBustiaUnitatOrganitzativa(
							unitatOrganitzativaHelper.toDto(novaBustia.getUnitatOrganitzativa()));
					
				} else if (ReglaTipusEnumDto.BACKOFFICE.equals(lastReglaApplied.getTipus())) {
					classificacioResultat.setResultat(ClassificacioResultatEnumDto.REGLA_BACKOFFICE);
				}
			} else {
				classificacioResultat.setResultat(ClassificacioResultatEnumDto.REGLA_ERROR);
			}
		} else {
			classificacioResultat.setResultat(ClassificacioResultatEnumDto.SENSE_CANVIS);
		}
		return classificacioResultat;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId) {
		logger.debug("classificant l'anotació de registre (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		List<Procediment> procediments = pluginHelper.procedimentFindByCodiDir3(bustia.getUnitatOrganitzativa().getCodi());
		List<ProcedimentDto> dtos = new ArrayList<ProcedimentDto>();
		if (procediments != null) {
			for (Procediment procediment: procediments) {
				if (procediment.getCodigoSIA() != null && !procediment.getCodigoSIA().isEmpty()) {
					ProcedimentDto dto = new ProcedimentDto();
					dto.setCodi(procediment.getCodigo());
					dto.setCodiSia(procediment.getCodigoSIA());
					dto.setNom(procediment.getNombre());
					dtos.add(dto);
				}
			}
		}
		return dtos;
	}

	private List<Annex> getAnnexosPerBackoffice(Long registreId) throws NotFoundException {
		logger.debug("Obtenint annexos per enviar al backoffice (" + "registreId=" + registreId + ")");
		RegistreEntity registre = registreRepository.findOne(registreId);
		List<Annex> annexosPerBackoffice = new ArrayList<Annex>(); 
		for (RegistreAnnexEntity annexEntity : registre.getAnnexos()) {
			
			if ((registre.getJustificant() != null && registre.getJustificant().getId().equals(annexEntity.getId()))
					|| (registre.getJustificantArxiuUuid() != null && registre.getJustificantArxiuUuid().equals(annexEntity.getFitxerArxiuUuid()))){
				// El justificant no es retorna com un annex
				
			} else {
				Annex annexPerBackoffice = new Annex();
				annexPerBackoffice.setTitol(annexEntity.getTitol());
				annexPerBackoffice.setNom(annexEntity.getFitxerNom());
				annexPerBackoffice.setUuid(annexEntity.getFitxerArxiuUuid());
				annexPerBackoffice.setTamany(annexEntity.getFitxerTamany());
				annexPerBackoffice.setTipusMime(annexEntity.getFitxerTipusMime());
				annexPerBackoffice.setNtiTipoDocumental(toNtiTipoDocumento(annexEntity.getNtiTipusDocument()));
				annexPerBackoffice.setNtiOrigen(toNtiOrigen(annexEntity.getOrigenCiutadaAdmin()));
				annexPerBackoffice.setNtiFechaCaptura(annexEntity.getDataCaptura());
				annexPerBackoffice.setSicresTipoDocumento(toSicresTipoDocumento(annexEntity.getSicresTipusDocument()));
				annexPerBackoffice.setObservacions(annexEntity.getObservacions());
				annexPerBackoffice.setNtiEstadoElaboracion(NtiEstadoElaboracion.valueOf((annexEntity.getNtiElaboracioEstat().toString())));
				boolean retornarAnnexIFirmaContingut = PropertiesHelper.getProperties().getAsBoolean(
						"es.caib.distribucio.backoffice.integracio.retornarAnnexIFirmaContingut");
				// annex should be stored in arxiu
				if (annexEntity.getFitxerArxiuUuid() != null && !annexEntity.getFitxerArxiuUuid().isEmpty()) {
					Document document = pluginHelper.arxiuDocumentConsultar(
							annexEntity.getFitxerArxiuUuid(),
							null,
							retornarAnnexIFirmaContingut);
	
					if(retornarAnnexIFirmaContingut)
						annexPerBackoffice.setContingut(document.getContingut().getContingut());
					
					
					
					// if document is signed
					if (document.getFirmes() != null && !document.getFirmes().isEmpty()) {
						RegistreAnnexFirmaEntity registreAnneFirma = annexEntity.getFirmes().get(0);
						for (Firma firma : document.getFirmes()) {
							// we want to use first firma that is not CSV type
							if (!FirmaTipus.CSV.equals(firma.getTipus())) {
	
								boolean detached = FirmaTipus.XADES_DET.equals(firma.getTipus())
										|| FirmaTipus.CADES_DET.equals(firma.getTipus());
								if (detached && retornarAnnexIFirmaContingut) {
										annexPerBackoffice.setFirmaContingut(firma.getContingut());
										annexPerBackoffice.setFirmaTamany(firma.getContingut().length);
										annexPerBackoffice.setFirmaNom(registreAnneFirma.getFitxerNom());
										annexPerBackoffice.setFirmaTipusMime(registreAnneFirma.getTipusMime());
								}
								annexPerBackoffice.setFirmaTipus(
										firma.getTipus() != null ? es.caib.distribucio.core.api.service.ws.backoffice.FirmaTipus.valueOf(firma.getTipus().name()) : null);
								annexPerBackoffice.setFirmaPerfil(
										registreAnneFirma.getPerfil() != null ? es.caib.distribucio.core.api.service.ws.backoffice.FirmaPerfil.valueOf(firma.getPerfil().name()) : null);
								break;
							}
						}
					}
				} else {
					throw new RuntimeException("Error en la consulta de annexos per backofice. Annex " + annexEntity.getTitol() + "de registre " + registre.getIdentificador() + " no te uuid de arxiu");
				}
				annexosPerBackoffice.add(annexPerBackoffice);
				
			}
		}
		return annexosPerBackoffice;
	}
	
	
	
	

	private NtiTipoDocumento toNtiTipoDocumento(RegistreAnnexNtiTipusDocumentEnum registreAnnexNtiTipusDocument){
		NtiTipoDocumento ntiTipoDocumento = null;
		
		if (registreAnnexNtiTipusDocument != null) {
			switch (registreAnnexNtiTipusDocument) {
			
			case RESOLUCIO:
				ntiTipoDocumento = NtiTipoDocumento.RESOLUCIO;
				break;
			case ACORD:
				ntiTipoDocumento = NtiTipoDocumento.ACORD;
				break;	
			case CONTRACTE:
				ntiTipoDocumento = NtiTipoDocumento.CONTRACTE;
				break;	
			case CONVENI:
				ntiTipoDocumento = NtiTipoDocumento.CONVENI;
				break;
			case DECLARACIO:
				ntiTipoDocumento = NtiTipoDocumento.DECLARACIO;
				break;
			case COMUNICACIO:
				ntiTipoDocumento = NtiTipoDocumento.COMUNICACIO;
				break;	
			case NOTIFICACIO:
				ntiTipoDocumento = NtiTipoDocumento.NOTIFICACIO;
				break;	
			case PUBLICACIO:
				ntiTipoDocumento = NtiTipoDocumento.PUBLICACIO;
				break;	
			case ACUS_REBUT:
				ntiTipoDocumento = NtiTipoDocumento.JUSTIFICANT_RECEPCIO;
				break;	
			case ACTE:
				ntiTipoDocumento = NtiTipoDocumento.ACTA;
				break;	
			case CERTIFICAT:
				ntiTipoDocumento = NtiTipoDocumento.CERTIFICAT;
				break;	
			case DILIGENCIA:
				ntiTipoDocumento = NtiTipoDocumento.DILIGENCIA;
				break;	
			case INFORME:
				ntiTipoDocumento = NtiTipoDocumento.INFORME;
				break;	
			case SOLICITUD:
				ntiTipoDocumento = NtiTipoDocumento.SOLICITUD;
				break;	
			case DENUNCIA:
				ntiTipoDocumento = NtiTipoDocumento.DENUNCIA;
				break;	
			case ALEGACIONS:
				ntiTipoDocumento = NtiTipoDocumento.ALEGACIO;
				break;	
			case RECURSOS:
				ntiTipoDocumento = NtiTipoDocumento.RECURS;
				break;	
			case COMUNICACIO_CIUTADA:
				ntiTipoDocumento = NtiTipoDocumento.COMUNICACIO_CIUTADA;
				break;	
			case FACTURA:
				ntiTipoDocumento = NtiTipoDocumento.FACTURA;
				break;							
			case ALTRES_INCAUTATS:
				ntiTipoDocumento = NtiTipoDocumento.ALTRES_INCAUTATS;
				break;	
			case ALTRES:
				ntiTipoDocumento = NtiTipoDocumento.ALTRES;
				break;	
			}
		}	
		return ntiTipoDocumento;
	}
	
	private NtiOrigen toNtiOrigen(RegistreAnnexOrigenEnum registreAnnexOrigenEnum){
		NtiOrigen ntiOrigen = null;
		
		if (registreAnnexOrigenEnum != null) {
			switch (registreAnnexOrigenEnum) {
			case CIUTADA:
				ntiOrigen = NtiOrigen.CIUTADA;
				break;
			case ADMINISTRACIO:
				ntiOrigen = NtiOrigen.ADMINISTRACIO;
				break;	
			}
		}	
		return ntiOrigen;
	}
	
	
	private SicresTipoDocumento toSicresTipoDocumento(RegistreAnnexSicresTipusDocumentEnum registreAnnexSicresTipusDocumentEnum) {
		SicresTipoDocumento sicresTipoDocumento = null;

		if (registreAnnexSicresTipusDocumentEnum != null) {
			switch (registreAnnexSicresTipusDocumentEnum) {
			case FORM:
				sicresTipoDocumento = SicresTipoDocumento.FORMULARI;
				break;
			case FORM_ADJUNT:
				sicresTipoDocumento = SicresTipoDocumento.ADJUNT;
				break;
			case INTERN:
				sicresTipoDocumento = SicresTipoDocumento.TECNIC_INTERN;
				break;
			}
		}
		return sicresTipoDocumento;
	}

	private List<Interessat> toInteressats(List<RegistreInteressatEntity> registreInteressats) {
		List<Interessat> interessatsPerBackoffice = new ArrayList<>();
		for (RegistreInteressatEntity registreInteressatEntity : registreInteressats) {
			
			if (registreInteressatEntity.getRepresentat() == null) {
				Interessat interessatPerBackoffice = (Interessat) toInteressatBase(registreInteressatEntity, true);
				if (registreInteressatEntity.getRepresentant() != null) {
					Representant representant = (Representant) toInteressatBase(registreInteressatEntity.getRepresentant(), false);
					interessatPerBackoffice.setRepresentant(representant);
				}
				interessatsPerBackoffice.add(interessatPerBackoffice);
			}
		}
		return interessatsPerBackoffice;
	}

	private InteressatBase toInteressatBase(RegistreInteressatEntity registreInteressatEntity, boolean isInteressat) {
		InteressatBase interessatBase;
		if (isInteressat) {
			interessatBase = new Interessat();
		} else {
			interessatBase = new Representant();
		}
		
		switch (registreInteressatEntity.getTipus()) {
		case PERSONA_FIS:
			interessatBase.setTipus(InteressatTipus.PERSONA_FISICA);
			break;
		case PERSONA_JUR:
			interessatBase.setTipus(InteressatTipus.PERSONA_JURIDICA);
			break;
		case ADMINISTRACIO:
			interessatBase.setTipus(InteressatTipus.ADMINISTRACIO);
			break;
		}
		if (registreInteressatEntity.getDocumentTipus() != null) {
			switch (registreInteressatEntity.getDocumentTipus()) {
			case NIF:
				interessatBase.setDocumentTipus(DocumentTipus.NIF);
				break;
			case CIF:
				interessatBase.setDocumentTipus(DocumentTipus.CIF);
				break;
			case PASSAPORT:
				interessatBase.setDocumentTipus(DocumentTipus.PASSAPORT);
				break;
			case ESTRANGER:
				interessatBase.setDocumentTipus(DocumentTipus.NIE);
				break;
			case ALTRES:
				interessatBase.setDocumentTipus(DocumentTipus.ALTRES);
				break;
			case CODI_ORIGEN:
				interessatBase.setDocumentTipus(DocumentTipus.CODI_ORIGEN);
				break;
			}
		}
		interessatBase.setDocumentNumero(registreInteressatEntity.getDocumentNum());
		interessatBase.setRaoSocial(registreInteressatEntity.getRaoSocial());
		interessatBase.setNom(registreInteressatEntity.getNom());
		interessatBase.setLlinatge1(registreInteressatEntity.getLlinatge1());
		interessatBase.setLlinatge2(registreInteressatEntity.getLlinatge2());
		
		interessatBase.setPaisCodi(registreInteressatEntity.getPaisCodi());
		interessatBase.setProvinciaCodi(registreInteressatEntity.getProvinciaCodi());
		interessatBase.setMunicipiCodi(registreInteressatEntity.getMunicipiCodi());
		interessatBase.setPais(registreInteressatEntity.getPais());
		interessatBase.setProvincia(registreInteressatEntity.getProvincia());
		interessatBase.setMunicipi(registreInteressatEntity.getMunicipi());
		interessatBase.setAdresa(registreInteressatEntity.getAdresa());
		interessatBase.setCp(registreInteressatEntity.getCodiPostal());
		interessatBase.setEmail(registreInteressatEntity.getEmail());
		interessatBase.setTelefon(registreInteressatEntity.getTelefon());
		interessatBase.setAdresaElectronica(registreInteressatEntity.getEmail());
		interessatBase.setCanal(registreInteressatEntity.getCanalPreferent() != null ? registreInteressatEntity.getCanalPreferent().toString() : null);
		interessatBase.setObservacions(registreInteressatEntity.getObservacions());
		
		if (registreInteressatEntity.getTipus() == RegistreInteressatTipusEnum.ADMINISTRACIO && registreInteressatEntity.getDocumentTipus() == RegistreInteressatDocumentTipusEnum.CODI_ORIGEN) {
			interessatBase.setOrganCodi(registreInteressatEntity.getDocumentNum());
		}
		
		
		return interessatBase;
	}

	private Exception processarAnotacioPendent(RegistreEntity anotacio) {
		boolean pendentArxiu = RegistreProcesEstatEnum.ARXIU_PENDENT.equals(anotacio.getProcesEstat()) || RegistreProcesEstatEnum.BUSTIA_PROCESSADA.equals(anotacio.getProcesEstat());
		boolean pendentRegla = RegistreProcesEstatEnum.REGLA_PENDENT.equals(anotacio.getProcesEstat());
		Exception exceptionProcessant = null;
		if (pendentArxiu || pendentRegla) {
			if (pendentArxiu) {
				exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(
						anotacio.getId());
			}
			if (exceptionProcessant == null && pendentRegla) {
				exceptionProcessant = registreHelper.processarAnotacioPendentRegla(
						anotacio.getId());
			}
		} else {
			throw new ValidationException(
					anotacio.getId(),
					RegistreEntity.class,
					"L'anotació de registre no es troba en estat pendent");
		}
		return exceptionProcessant;
	}

	
	private RegistreAnnexDto getJustificantPerRegistre(
			EntitatEntity entitat,
			RegistreEntity registre) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitat.getId() + ", "
				+ "registreId=" + registre.getId() + ", "
				+ "justificantUuid=" + registre.getJustificantArxiuUuid() + ")");
		
		RegistreAnnexEntity justificant;
		if (!registre.isJustificantDescarregat()) {
			justificant = registreHelper.loadJustificantToDB(registre.getId());
		} else {
			justificant = registreRepository.getOne(registre.getId()).getJustificant();
		}
		
		return conversioTipusHelper.convertir(
				justificant,
				RegistreAnnexDto.class);
	}
	

	private static final Logger logger = LoggerFactory.getLogger(RegistreServiceImpl.class);
}
