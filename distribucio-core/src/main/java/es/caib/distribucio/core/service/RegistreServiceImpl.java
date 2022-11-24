/**
 * 
 */
package es.caib.distribucio.core.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.ArxiuContingutDto;
import es.caib.distribucio.core.api.dto.ArxiuContingutTipusEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto.ClassificacioResultatEnumDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.HistogramPendentsEntryDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexFirmaDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.RegistreEnviatPerEmailEnumDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreReintentsEnumDto;
import es.caib.distribucio.core.api.dto.RegistreMarcatPerSobreescriureEnumDto;
import es.caib.distribucio.core.api.dto.RegistreNombreAnnexesEnumDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.api.registre.ValidacioFirmaEnum;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.ws.backoffice.Annex;
import es.caib.distribucio.core.api.service.ws.backoffice.AnnexEstat;
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
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.ProcedimentEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.entity.VistaMovimentEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ContingutLogHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.GestioDocumentalHelper;
import es.caib.distribucio.core.helper.HistogramPendentsHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper.Converter;
import es.caib.distribucio.core.helper.PermisosHelper;
import es.caib.distribucio.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.core.helper.UsuariHelper;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.ContingutLogRepository;
import es.caib.distribucio.core.repository.ProcedimentRepository;
import es.caib.distribucio.core.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.core.repository.RegistreAnnexRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.repository.VistaMovimentRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
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
	private RegistreAnnexFirmaRepository registreAnnexFirmaRepository;
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
	@Autowired
	private VistaMovimentRepository vistaMovimentRepository;
	@Autowired
	private ContingutLogRepository contingutLogRepository;
	
	@Resource
	private ContingutLogHelper contingutLogHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Autowired
	private HistogramPendentsHelper historicsPendentHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	
	@PersistenceContext
    private EntityManager entityManager;

	
	@Transactional(readOnly = true)
	@Override
	public List<HistogramPendentsEntryDto> getHistogram() {
		return historicsPendentHelper.getAll();
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments) throws NotFoundException {
		return findOne(entitatId, registreId, isVistaMoviments, null);		
	}
		
	@Transactional(readOnly = true)
	@Override
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments,
			String rolActual) throws NotFoundException {
		
		return registreHelper.findOne(entitatId, registreId, isVistaMoviments, rolActual);
	}


	@Override
	@Transactional(readOnly = true)
	public List<RegistreDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds,
			boolean isAdmin) {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "multipleRegistreIds=" + multipleRegistreIds
				+ "isAdmin=" + isAdmin + " )");
		
		if (multipleRegistreIds == null || multipleRegistreIds.isEmpty()) {
			return new ArrayList<RegistreDto>();
		}
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		
		List<RegistreEntity> registres;
		if (!isAdmin) {
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
			if (bustiesPermeses == null || bustiesPermeses.isEmpty()) {
				return new ArrayList<RegistreDto>();
			}
			registres = registreRepository.findByPareInAndIdIn(
					bustiesPermeses,
					multipleRegistreIds);
		} else {
			registres = registreRepository.findByIdIn(multipleRegistreIds);
		}

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



	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutDto> findRegistre(
			Long entitatId,
			List<BustiaDto> bustiesPermesesPerUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			boolean isAdmin) {
		Timer.Context contextTotal  = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistre")).time();
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
					!isAdmin);
		}
		String bustiesIds="";
			
		boolean totesLesbusties =false;
		List<Long> busties = new ArrayList<Long>();
		if (bustiesPermesesPerUsuari != null && !bustiesPermesesPerUsuari.isEmpty()) { 
			for (BustiaDto bustiaUsuari: bustiesPermesesPerUsuari) {
				busties.add(bustiaUsuari.getId());
				bustiesIds +=  bustiaUsuari.getId() + ", ";
			}
		} else if (bustia != null) {	
			busties.add(bustia.getId());
		} else {
			totesLesbusties = isAdmin;
			busties.add(0L);
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
		Page<RegistreEntity> pagina = null;
		
			
		boolean esPendent = RegistreProcesEstatSimpleEnumDto.PENDENT.equals(filtre.getProcesEstatSimple()); 
		boolean esProcessat = RegistreProcesEstatSimpleEnumDto.PROCESSAT.equals(filtre.getProcesEstatSimple());;

		Boolean enviatPerEmail = null;
		if (filtre.getEnviatPerEmail() != null) {
			if (filtre.getEnviatPerEmail() == RegistreEnviatPerEmailEnumDto.ENVIAT) {
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
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());
		
		int maxReintents = 0;
		if (filtre.getReintents() != null) {
			maxReintents = getGuardarAnnexosMaxReintentsProperty(entitat);
		}
		boolean senseBackoffice = false;
		if (filtre.getBackCodi() != null && 
			filtre.getBackCodi().equals("senseBackoffice")) {
			senseBackoffice = true;
		}

		logger.trace("Consultant el contingut de l'usuari ("
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
				+ "bustiesIds= " + (totesLesbusties ? "(totes)" : bustiesIds) + ", " 
				+ "enviatPerEmail= " + filtre.getEnviatPerEmail() + ", " 
				+ "procesEstatSimple= " + filtre.getProcesEstatSimple() + ", " 
				+ "nomesAmbError= " + filtre.isNomesAmbErrors() + ", " 
				+ "nomesAmbEsborranys= " + filtre.isNomesAmbEsborranys() + ", " 
				+ "estat= " + filtre.getEstat() + ", " 
				+ "unitat= " + filtre.getUnitatId() + ", " 
				+ "reintents = " + filtre.getReintents() + ", "
				+ "paginacioParams=" + "[paginaNum=" + paginacioParams.getPaginaNum() + ", paginaTamany=" + paginacioParams.getPaginaTamany() + ", ordres=" + paginacioParams.getOrdres() + "]" + ")");

		Timer.Context contextTotalfindRegistreByPareAndFiltre = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistreUser.findRegistreByPareAndFiltre")).time();
		long beginTime = new Date().getTime();
		
		try {
			
			pagina = (Page<RegistreEntity>) this.findRegistresFiltrats(
					false, // per retornar una pàgina
					entitat,
					totesLesbusties,
					busties,
					StringUtils.isEmpty(filtre.getNumero()),
					filtre.getNumero() != null ? filtre.getNumero().trim() : "",
					StringUtils.isEmpty(filtre.getTitol()),
					filtre.getTitol() != null ? filtre.getTitol().trim() : "",
					filtre.getNumeroOrigen() == null || filtre.getNumeroOrigen().isEmpty(),
					filtre.getNumeroOrigen() != null ? filtre.getNumeroOrigen().trim() : "",
					filtre.getRemitent() == null || filtre.getRemitent().isEmpty(),
					filtre.getRemitent() != null ? filtre.getRemitent().trim() : "",
					(filtre.getDataRecepcioInici() == null),
					filtre.getDataRecepcioInici(),
					(dataRecepcioFi == null),
					dataRecepcioFi,
					esProcessat,
					esPendent,
					filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
					filtre.getInteressat() != null ? filtre.getInteressat().trim() : "",
					enviatPerEmail == null,
					enviatPerEmail,
					tipusFisicaCodi == null,
					tipusFisicaCodi,
					filtre.getBackCodi() == null || filtre.getBackCodi().isEmpty(),
					filtre.getBackCodi() != null ? filtre.getBackCodi().trim() : "",
					senseBackoffice,
					filtre.getEstat() == null,
					filtre.getEstat(),
					filtre.getReintents() == null, 
					filtre.getReintents() != null ? (filtre.getReintents() == RegistreFiltreReintentsEnumDto.SI ? true : false) : false,
					maxReintents, 
					filtre.isNomesAmbErrors(),
					filtre.isNomesAmbEsborranys(),
					unitat == null,
					unitat,
					filtre.getSobreescriure() == null,
					filtre.getSobreescriure() != null ? (filtre.getSobreescriure() == RegistreMarcatPerSobreescriureEnumDto.SI ? true : false) : null,
					filtre.getProcedimentCodi() == null, 
					filtre.getProcedimentCodi() != null ? filtre.getProcedimentCodi() : "", 
					filtre.getNombreAnnexes(),
					paginacioHelper.toSpringDataPageable(paginacioParams, mapeigOrdenacio));

			contextTotalfindRegistreByPareAndFiltre.stop();
			long endTime = new Date().getTime();
			logger.trace("findRegistreByPareAndFiltre executed with no errors in: " + (endTime - beginTime) + "ms");
		} catch (Exception e) {
			long endTime = new Date().getTime();
			logger.error("findRegistreByPareAndFiltre executed with errors in: " + (endTime - beginTime) + "ms", e);
			contextTotalfindRegistreByPareAndFiltre.stop();
			throw new RuntimeException(e);
		}
		
		
		Timer.Context contextTotaltoPaginaDto = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistreUser.toPaginaDto")).time();

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

	/** Crea la query dinàmicament segons els paràmetres de cerca i retoran una llista d'indentificadors o un resultat paginat.
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	private Object findRegistresFiltrats(
			boolean nomesIds,
			EntitatEntity entitat,
			boolean esBustiesTotes,
			List<Long> bustiesIds,
			boolean esNullNumero,
			String numero,
			boolean esNullExtracte,
			String extracte,
			boolean esNumeroOrigen,
			String numeroOrigen,
			boolean esNullRemitent,
			String remitent,
			boolean esNullDataInici,
			Date dataInici,
			boolean esNullDataFi,
			Date dataFi,
			boolean esProcessat,
			boolean esPendent,
			boolean esNullInteressat,
			String interessat,
			boolean esNullEnviatPerEmail,
			Boolean enviatPerEmail,
			boolean esNullDocumentacioFisicaCodi,
			String documentacioFisicaCodi,
			boolean esNullBackCodi,
			String backCodi,
			boolean senseBackoffice, 
			boolean esNullProcesEstat, 
			RegistreProcesEstatEnum procesEstat,
			boolean esNullReintentsPendents,
			Boolean reintentsPendents,
			int maxReintents, 
			boolean nomesAmbErrors,
			boolean nomesAmbEsborranys,
			boolean esNullUnitatOrganitzativa,
			UnitatOrganitzativaEntity unitatOrganitzativa,
			boolean esNullSobreescriure,
			Boolean sobreescriure,
			boolean esNullProcedimentCodi,
			String procedimentCodi,
			RegistreNombreAnnexesEnumDto nombreAnnexos, 
			Pageable pageable) {
		
		Object ret = null; // Retorna una llista d'identificadors o una pàgina
		
		// Construeix la select
		String sqlFrom = "from RegistreEntity r ";
		if (!esNullRemitent) {
			sqlFrom += 		" left outer join r.darrerMoviment.remitent as remitent ";
		}
		if (nombreAnnexos != null) {
			sqlFrom += 		" left outer join r.annexos as annex ";
								//" with (r.justificant.id <> annex.id and r.justificantArxiuUuid <> annex.fitxerArxiuUuid) ";

		}

		// Where
		Map<String, Object> parametres = new HashMap<>();
		StringBuilder sqlWhere = new StringBuilder(" where ");
		sqlWhere.append("    (r.entitat = :entitat) ");
		parametres.put("entitat", entitat);
		
		if (!esBustiesTotes) {
			sqlWhere.append("and r.pare.id in (:bustiesIds) ");
			parametres.put("bustiesIds", bustiesIds);
		}
		if (!esNullNumero) {
			sqlWhere.append("and lower(r.numero) like lower('%'||:numero||'%') ");
			parametres.put("numero", numero);
		}
		if (!esNullExtracte) {
			sqlWhere.append("and lower(r.extracte) like lower('%'||:extracte||'%') ");
			parametres.put("extracte", extracte);
		}
		if (!esNumeroOrigen) {
			sqlWhere.append("and lower(r.numeroOrigen) like lower('%'||:numeroOrigen||'%') ");
			parametres.put("numeroOrigen", numeroOrigen);
		}
		if (!esNullRemitent) {
			sqlWhere.append("and lower(remitent.nom) like lower('%'||:remitent||'%') ");
			parametres.put("remitent", remitent);
		}
		if (!esNullDataInici) {
			sqlWhere.append("and r.data >= :dataInici ");
			parametres.put("dataInici", dataInici);
		}
		if (!esNullDataFi) {
			sqlWhere.append("and r.data < :dataFi ");
			parametres.put("dataFi", dataFi);
		}
		if (esProcessat) {
			sqlWhere.append("and r.pendent = false ");
		}
		if (esPendent) {
			sqlWhere.append("and r.pendent = true ");
		}
		if (!esNullEnviatPerEmail) {
			sqlWhere.append("and r.enviatPerEmail = :enviatPerEmail ");
			parametres.put("enviatPerEmail", enviatPerEmail);
		}
		if (!esNullSobreescriure) {
			sqlWhere.append("and r.sobreescriure = :sobreescriure ");
			parametres.put("sobreescriure", sobreescriure.booleanValue());
		}
		if (!esNullDocumentacioFisicaCodi) {
			sqlWhere.append("and r.documentacioFisicaCodi = :documentacioFisicaCodi ");
			parametres.put("documentacioFisicaCodi", documentacioFisicaCodi);
		}
		if (!esNullUnitatOrganitzativa) {
			sqlWhere.append("and r.pare.id in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa) ");
			parametres.put("unitatOrganitzativa", unitatOrganitzativa);
		}
		if (!esNullReintentsPendents) {
			sqlWhere.append("and ((:reintentsPendents = true and r.procesIntents < :maxReintents) ");
			sqlWhere.append("		or (:reintentsPendents = false and r.procesIntents >= :maxReintents)) ");
			parametres.put("reintentsPendents", reintentsPendents);
			parametres.put("maxReintents", maxReintents);
		}
		if (!esNullProcesEstat) {
			sqlWhere.append("and r.procesEstat = :procesEstat ");
			parametres.put("procesEstat", procesEstat);
		}
		if (nomesAmbErrors) {
			sqlWhere.append("and r.procesError != null ");
		}
		if (nomesAmbEsborranys) {
			sqlWhere.append("and r.annexosEstatEsborrany > 0 ");
		}
		if (!esNullInteressat) {
			sqlWhere.append("and (select count(interessat) ");
			sqlWhere.append("			from r.interessats as interessat ");
			sqlWhere.append("			where ");
			sqlWhere.append("				(lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%') ");
			sqlWhere.append("					or lower(interessat.raoSocial) like lower('%'||:interessat||'%')) ");
			sqlWhere.append("					) > 0 ");
			parametres.put("interessat", interessat);
		}
		
		if (!esNullBackCodi) {
			if (senseBackoffice) {
				sqlWhere.append("and r.backCodi is null ");
			} else {
				sqlWhere.append("and lower(r.backCodi) like lower('%'||:backCodi||'%') ");
				parametres.put("backCodi", backCodi);
			}
		}
		if (!esNullReintentsPendents) {
			if (reintentsPendents) {
				sqlWhere.append("and r.procesIntents < :maxReintents ");
			} else {
				sqlWhere.append("and r.procesIntents >= :maxReintents ");				
			}
			parametres.put("maxReintents", maxReintents);
		}
		
		if (!esNullProcedimentCodi) {
			sqlWhere.append("and r.procedimentCodi = :procedimentCodi ");				
			parametres.put("procedimentCodi", procedimentCodi);
		}
		
		if (nombreAnnexos != null) {
			sqlWhere.append("and r.annexos.size " + this.getNombreAnnexosSize(nombreAnnexos));
		}

		StringBuilder sqlOrder = new StringBuilder();
		if (pageable != null && pageable.getSort() != null) {
			Iterator<Order> orders = pageable.getSort().iterator();
			if (orders.hasNext()) {
				sqlOrder.append(" order by ");
			}
			Order order;
			while (orders.hasNext()) {
				order = orders.next();
				sqlOrder.append(order.getProperty()).append(" ").append(order.getDirection());
				if (orders.hasNext()) {
					sqlOrder.append(", ");
				}
			}
		}
		
		String sqlSelect = "select " + (nomesIds ? " r.id " : "r ") + sqlFrom + sqlWhere + sqlOrder;
		String sqlCount = "select count(r.id) " + sqlFrom + sqlWhere;


		Session session = entityManager.unwrap(Session.class);
		
		Query selectQuery = session.createQuery(sqlSelect);
		if (pageable != null) {
			selectQuery = selectQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
									.setMaxResults(pageable.getPageSize());
		}
		Query countQuery = session.createQuery(sqlCount);
		
		Object value;
		for (String parametre : parametres.keySet()) {
			value = parametres.get(parametre);
			if (value instanceof Collection) {
				selectQuery.setParameterList(parametre, (Collection) value);
				countQuery.setParameterList(parametre, (Collection) value);
			} else {
				selectQuery.setParameter(parametre, value);
				countQuery.setParameter(parametre, value);
			}
		}
		
		if (nomesIds) {
			// select
			List<Long> ids = (List<Long>) selectQuery.list();
			ret = ids;
			
		} else {
			// select
			List<RegistreEntity> resultats = 
					(List<RegistreEntity>)
					selectQuery.list();

			// count
			Page<RegistreEntity> pagina = (Page<RegistreEntity>) new PageImpl<RegistreEntity>(
		              resultats,
		              pageable, 
		              (long) countQuery.uniqueResult()); 
			
			ret = pagina;			
		}		
		return ret;
	}


	private Object getNombreAnnexosSize(RegistreNombreAnnexesEnumDto nombreAnnexos) {
		// 			sqlWhere.append("and r.annexos.size > 100 ");
		String ret = "= 1";
		switch (nombreAnnexos) {
			case AMB_1:
				ret = "= 2 ";
				break;
			case AMB_2:
				ret = "= 3 ";
				break;
			case AMB_3:
				ret = "= 4 ";
				break;
			case AMB_4:
				ret = "= 5 ";
				break;
			case AMB_5:
				ret = "= 6 ";
				break;
			case DE_6_A_10:
				ret = "between 7 and 11 ";
				break;
			case DE_11_A_20:
				ret = "between 12 and 21 ";
				break;
			case DE_21_A_50:
				ret = "between 22 and 51 ";
				break;
			case DE_51_A_100:
				ret = "between 52 and 101 ";
				break;
			case MES_DE_100:
				ret = "> 101 ";
				break;
		}
		return ret;
	}


	private int getGuardarAnnexosMaxReintentsProperty(EntitatEntity entitat) {
		EntitatDto entitatDto = conversioTipusHelper.convertir(entitat, EntitatDto.class);
		String maxReintents = configHelper.getConfig(entitatDto, "es.caib.distribucio.tasca.guardar.annexos.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutDto> findMovimentsRegistre(
			Long entitatId,
			List<BustiaDto> bustiesPermesesPerUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		Timer.Context contextTotal  = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findMovimentsRegistre")).time();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Comprova la bústia i que l'usuari hi tengui accés
		BustiaEntity bustia = null, bustiaOrigen = null;
		if (filtre.getBustia() != null && !filtre.getBustia().isEmpty()) {
			bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustia()),
					false);
		}
		if (filtre.getBustiaOrigen() != null && !filtre.getBustiaOrigen().isEmpty()) {
			bustiaOrigen = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustiaOrigen()),
					false);
		}
		String bustiesIds="";
			
		boolean totesLesbusties =false;
		List<Long> busties = new ArrayList<Long>();
		if (bustiesPermesesPerUsuari != null && !bustiesPermesesPerUsuari.isEmpty()) { 
			for (BustiaDto bustiaUsuari: bustiesPermesesPerUsuari) {
				busties.add(bustiaUsuari.getId());
				bustiesIds +=  bustiaUsuari.getId() + ", ";
			}
		} else {
			busties.add(0L);
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
		Page<VistaMovimentEntity> pagina = null;
		
			
		boolean esPendent = RegistreProcesEstatSimpleEnumDto.PENDENT.equals(filtre.getProcesEstatSimple()); 
		boolean esProcessat = RegistreProcesEstatSimpleEnumDto.PROCESSAT.equals(filtre.getProcesEstatSimple());;

		Boolean enviatPerEmail = null;
		if (filtre.getEnviatPerEmail() != null) {
			if (filtre.getEnviatPerEmail() == RegistreEnviatPerEmailEnumDto.ENVIAT) {
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
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());

		logger.trace("Consultant el contingut de l'usuari ("
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
				+ "bustiesIds= " + (totesLesbusties ? "(totes)" : bustiesIds) + ", " 
				+ "enviatPerEmail= " + filtre.getEnviatPerEmail() + ", " 
				+ "procesEstatSimple= " + filtre.getProcesEstatSimple() + ", " 
				+ "nomesAmbError= " + filtre.isNomesAmbErrors() + ", " 
				+ "estat= " + filtre.getEstat() + ", " 
				+ "unitat= " + filtre.getUnitatId() + ", " 
				+ "bustiaOrigen= " + filtre.getBustiaOrigen() + ", " 
				+ "bustiaDesti= " + filtre.getBustia() + ", " 
				+ "paginacioParams=" + "[paginaNum=" + paginacioParams.getPaginaNum() + ", paginaTamany=" + paginacioParams.getPaginaTamany() + ", ordres=" + paginacioParams.getOrdres() + "]" + ")");

		Timer.Context contextTotalfindRegistreByPareAndFiltre = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistreUser.findRegistreByPareAndFiltre")).time();
		long beginTime = new Date().getTime();
		try {
			
			pagina = vistaMovimentRepository.findMovimentsByFiltre(
					entitat.getId(), 
					totesLesbusties, 
					busties, 
					StringUtils.isEmpty(filtre.getNumero()),
					filtre.getNumero() != null ? filtre.getNumero().trim() : "",
					StringUtils.isEmpty(filtre.getTitol()),
					filtre.getTitol() != null ? filtre.getTitol().trim() : "",
					filtre.getNumeroOrigen() == null || filtre.getNumeroOrigen().isEmpty(),
					filtre.getNumeroOrigen() != null ? filtre.getNumeroOrigen().trim() : "",
					filtre.getRemitent() == null || filtre.getRemitent().isEmpty(),
					filtre.getRemitent() != null ? filtre.getRemitent().trim() : "",
					(filtre.getDataRecepcioInici() == null),
					filtre.getDataRecepcioInici(),
					(dataRecepcioFi == null),
					dataRecepcioFi,
					esProcessat,
					esPendent,
					filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
					filtre.getInteressat() != null ? filtre.getInteressat().trim() : "",
					enviatPerEmail == null,
					enviatPerEmail,
					tipusFisicaCodi == null,
					tipusFisicaCodi,
					filtre.getBackCodi() == null || filtre.getBackCodi().isEmpty(),
					filtre.getBackCodi() != null ? filtre.getBackCodi().trim() : "",
					filtre.getEstat() == null,
					filtre.getEstat(),
					filtre.isNomesAmbErrors(),
					unitat == null,
					unitat,
					bustiaOrigen == null,
					bustiaOrigen != null ? bustiaOrigen.getId() : null,
					bustia == null,
					bustia != null ? bustia.getId() : null,
					paginacioHelper.toSpringDataPageable(
							paginacioParams,
							mapeigOrdenacio));
			contextTotalfindRegistreByPareAndFiltre.stop();
			long endTime = new Date().getTime();
			logger.trace("findMovimentsRegistre executed with no errors in: " + (endTime - beginTime) + "ms");
		} catch (Exception e) {
			long endTime = new Date().getTime();
			logger.error("findMovimentsRegistre executed with errors in: " + (endTime - beginTime) + "ms", e);
			contextTotalfindRegistreByPareAndFiltre.stop();
			throw new RuntimeException(e);
		}
		
		
		Timer.Context contextTotaltoPaginaDto = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findMovimentsRegistre.toPaginaDto")).time();

		PaginaDto<ContingutDto> pag = paginacioHelper.toPaginaDto(
				pagina,
				ContingutDto.class,
				new Converter<VistaMovimentEntity, ContingutDto>() {
					@Override
					public ContingutDto convert(VistaMovimentEntity source) {
						return contingutHelper.movimentToContingutDto(source);
					}
				});
		contextTotaltoPaginaDto.stop();
		
		contextTotal.stop();
		return pag;
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Long> findRegistreIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean onlyAmbMoviments, 
			boolean isAdmin) {
		List<Long> ids;
		final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "contingutPendentFindIds"));
		Timer.Context contextTotal = timerTotal.time();

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
					!isAdmin);
		
		boolean totesLesbusties =false;
		List<Long> busties = new ArrayList<Long>();
		if (bustiesUsuari != null && !bustiesUsuari.isEmpty()) {
			for (BustiaDto bustiaUsuari: bustiesUsuari) {
				entityComprovarHelper.comprovarBustia(
						entitat,
						new Long(bustiaUsuari.getId()),
						!isAdmin);
				busties.add(bustiaUsuari.getId());
			}
		} else if (bustia != null) {
			busties.add(bustia.getId());
		} else {
			busties.add(0L);
			totesLesbusties = true;
		}

		boolean esPendent = RegistreProcesEstatSimpleEnumDto.PENDENT.equals(filtre.getProcesEstatSimple()); 
		boolean esProcessat = RegistreProcesEstatSimpleEnumDto.PROCESSAT.equals(filtre.getProcesEstatSimple());;

		Boolean enviatPerEmail = null;
		if (filtre.getEnviatPerEmail() != null) {
			if (filtre.getEnviatPerEmail() == RegistreEnviatPerEmailEnumDto.ENVIAT) {
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

		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());

		int maxReintents = 0;
		if (filtre.getReintents() != null) {
			maxReintents = getGuardarAnnexosMaxReintentsProperty(entitat);
		}

		boolean senseBackoffice = false;
		if (filtre.getBackCodi() != null && 
			filtre.getBackCodi().equals("senseBackoffice")) {
			senseBackoffice = true;
		}

		logger.debug("Consultant els identificadors del contingut de l'usuari ("
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
				+ "bustiesIds= " + (totesLesbusties ? "(totes)" : busties) + ", " 
				+ "enviatPerEmail= " + filtre.getEnviatPerEmail() + ", " 
				+ "procesEstatSimple= " + filtre.getProcesEstatSimple() + ", " 
				+ "nomesAmbError= " + filtre.isNomesAmbErrors() + ", " 
				+ "nomesAmbEsborranys= " + filtre.isNomesAmbEsborranys() + ", " 
				+ "estat= " + filtre.getEstat() + ", " 
				+ "unitat= " + filtre.getUnitatId() + ", " 
				+ "reintents= " + filtre.getReintents() + ")");
		
		
		ids= (List<Long>) this.findRegistresFiltrats(
				true, // per retornar una llista d'identificadors
				entitat,
				totesLesbusties,
				busties,
				StringUtils.isEmpty(filtre.getNumero()),
				filtre.getNumero() != null ? filtre.getNumero().trim() : "",
				StringUtils.isEmpty(filtre.getTitol()),
				filtre.getTitol() != null ? filtre.getTitol().trim() : "",
				filtre.getNumeroOrigen() == null || filtre.getNumeroOrigen().isEmpty(),
				filtre.getNumeroOrigen() != null ? filtre.getNumeroOrigen().trim() : "",
				filtre.getRemitent() == null || filtre.getRemitent().isEmpty(),
				filtre.getRemitent() != null ? filtre.getRemitent().trim() : "",
				(filtre.getDataRecepcioInici() == null),
				filtre.getDataRecepcioInici(),
				(dataRecepcioFi == null),
				dataRecepcioFi,
				esProcessat,
				esPendent,
				filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
				filtre.getInteressat() != null ? filtre.getInteressat().trim() : "",
				enviatPerEmail == null,
				enviatPerEmail,
				tipusFisicaCodi == null,
				tipusFisicaCodi,
				filtre.getBackCodi() == null || filtre.getBackCodi().isEmpty(),
				filtre.getBackCodi() != null ? filtre.getBackCodi().trim() : "",
				senseBackoffice,
				filtre.getEstat() == null,
				filtre.getEstat(),
				filtre.getReintents() == null, 
				filtre.getReintents() != null ? (filtre.getReintents() == RegistreFiltreReintentsEnumDto.SI ? true : false) : false,
				maxReintents, 
				filtre.isNomesAmbErrors(),
				filtre.isNomesAmbEsborranys(),
				unitat == null,
				unitat,
				filtre.getSobreescriure() == null,
				filtre.getSobreescriure() != null ? (filtre.getSobreescriure() == RegistreMarcatPerSobreescriureEnumDto.SI ? true : false) : null,
				filtre.getProcedimentCodi() == null, 
				filtre.getProcedimentCodi() != null ? filtre.getProcedimentCodi() : "", 
				filtre.getNombreAnnexes(),
				null);
	

		contextTotal.stop();
		return ids;
	}

	
	


	@Transactional(readOnly = true)
	@Override
	public List<String> findRegistreMovimentsIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean isAdmin) {
		List<String> ids = new ArrayList<String>();
		final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(BustiaServiceImpl.class, "findRegistreMovimentsIds"));
		Timer.Context contextTotal = timerTotal.time();

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		// Comprova la bústia i que l'usuari hi tengui accés
		BustiaEntity bustia = null, bustiaOrigen = null;
		if (filtre.getBustia() != null && !filtre.getBustia().isEmpty())
			bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustia()),
					false);
		
		if (filtre.getBustiaOrigen() != null && !filtre.getBustiaOrigen().isEmpty()) {
			bustiaOrigen = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustiaOrigen()),
					false);
		}

		boolean totesLesbusties =false;
		List<Long> busties = new ArrayList<Long>();
		if (bustiesUsuari != null && !bustiesUsuari.isEmpty()) {
			for (BustiaDto bustiaUsuari: bustiesUsuari) {
				entityComprovarHelper.comprovarBustia(
						entitat,
						new Long(bustiaUsuari.getId()),
						!isAdmin);
				busties.add(bustiaUsuari.getId());
			}
		} else if (bustia != null) {
			busties.add(bustia.getId());
		} else {
			busties.add(0L);
			totesLesbusties = true;
		}

		boolean esPendent = RegistreProcesEstatSimpleEnumDto.PENDENT.equals(filtre.getProcesEstatSimple()); 
		boolean esProcessat = RegistreProcesEstatSimpleEnumDto.PROCESSAT.equals(filtre.getProcesEstatSimple());;

		Boolean enviatPerEmail = null;
		if (filtre.getEnviatPerEmail() != null) {
			if (filtre.getEnviatPerEmail() == RegistreEnviatPerEmailEnumDto.ENVIAT) {
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

		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());

		logger.debug("Consultant els identificadors del contingut de l'usuari ("
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
				+ "bustiesIds= " + (totesLesbusties ? "(totes)" : busties) + ", " 
				+ "procesEstatSimple= " + filtre.getProcesEstatSimple() + ", " 
				+ "nomesAmbError= " + filtre.isNomesAmbErrors() + ", " 
				+ "estat= " + filtre.getEstat() + ", " 
				+ "unitat= " + filtre.getUnitatId() + ")");

		ids = vistaMovimentRepository.findRegistreIdsByFiltre(
				entitat.getId(),
				totesLesbusties,
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
				filtre.getBackCodi() == null || filtre.getBackCodi().isEmpty(),
				filtre.getBackCodi() != null ? filtre.getBackCodi().trim() : "",
				filtre.getEstat() == null,
				filtre.getEstat(),
				filtre.isNomesAmbErrors(),
				unitat == null,
				unitat,
				bustiaOrigen == null,
				bustiaOrigen != null ? bustiaOrigen.getId() : null,
				bustia == null,
				bustia != null ? bustia.getId() : null);
		
		contextTotal.stop();
		return ids;
	}
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutDto> findMovimentRegistre(
			Long entitatId,
			List<BustiaDto> bustiesPermesesPerUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			boolean isAdmin) {
		
		Timer.Context contextTotal  = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findMovimentRegistre")).time();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		// Comprova la bústia i que l'usuari hi tengui accés
		BustiaEntity bustia = null, bustiaOrigen = null;
		if (filtre.getBustia() != null && !filtre.getBustia().isEmpty()) {
			bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustia()),
					false);
		}
		if (filtre.getBustiaOrigen() != null && !filtre.getBustiaOrigen().isEmpty()) {
			bustiaOrigen = entityComprovarHelper.comprovarBustia(
					entitat,
					new Long(filtre.getBustiaOrigen()),
					false);
		}
		String bustiesIds="";
			
		boolean totesLesbusties =false;
		List<Long> busties = new ArrayList<Long>();
		if (bustiesPermesesPerUsuari != null && !bustiesPermesesPerUsuari.isEmpty()) { 
			for (BustiaDto bustiaUsuari: bustiesPermesesPerUsuari) {
				busties.add(bustiaUsuari.getId());
				bustiesIds +=  bustiaUsuari.getId() + ", ";
			}
		} else {
			busties.add(0L);
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
		Page<VistaMovimentEntity> pagina = null;
		
			
		boolean esPendent = RegistreProcesEstatSimpleEnumDto.PENDENT.equals(filtre.getProcesEstatSimple()); 
		boolean esProcessat = RegistreProcesEstatSimpleEnumDto.PROCESSAT.equals(filtre.getProcesEstatSimple());;

		Boolean enviatPerEmail = null;
		if (filtre.getEnviatPerEmail() != null) {
			if (filtre.getEnviatPerEmail() == RegistreEnviatPerEmailEnumDto.ENVIAT) {
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
		
		UnitatOrganitzativaEntity unitat = filtre.getUnitatId() == null ? null : unitatOrganitzativaRepository.findOne(filtre.getUnitatId());

		logger.trace("Consultant el contingut de l'usuari ("
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
				+ "bustiesIds= " + (totesLesbusties ? "(totes)" : bustiesIds) + ", " 
				+ "enviatPerEmail= " + filtre.getEnviatPerEmail() + ", " 
				+ "procesEstatSimple= " + filtre.getProcesEstatSimple() + ", " 
				+ "nomesAmbError= " + filtre.isNomesAmbErrors() + ", " 
				+ "estat= " + filtre.getEstat() + ", " 
				+ "unitat= " + filtre.getUnitatId() + ", " 
				+ "bustiaOrigen= " + filtre.getBustiaOrigen() + ", " 
				+ "bustiaDesti= " + filtre.getBustia() + ", " 
				+ "paginacioParams=" + "[paginaNum=" + paginacioParams.getPaginaNum() + ", paginaTamany=" + paginacioParams.getPaginaTamany() + ", ordres=" + paginacioParams.getOrdres() + "]" + ")");

		Timer.Context contextTotalfindRegistreByPareAndFiltre = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findRegistreUser.findRegistreByPareAndFiltre")).time();
		long beginTime = new Date().getTime();
		try {
			
			pagina = vistaMovimentRepository.findMovimentsByFiltre(
					entitat.getId(), 
					totesLesbusties, 
					busties, 
					StringUtils.isEmpty(filtre.getNumero()),
					filtre.getNumero() != null ? filtre.getNumero().trim() : "",
					StringUtils.isEmpty(filtre.getTitol()),
					filtre.getTitol() != null ? filtre.getTitol().trim() : "",
					filtre.getNumeroOrigen() == null || filtre.getNumeroOrigen().isEmpty(),
					filtre.getNumeroOrigen() != null ? filtre.getNumeroOrigen().trim() : "",
					filtre.getRemitent() == null || filtre.getRemitent().isEmpty(),
					filtre.getRemitent() != null ? filtre.getRemitent().trim() : "",
					(filtre.getDataRecepcioInici() == null),
					filtre.getDataRecepcioInici(),
					(dataRecepcioFi == null),
					dataRecepcioFi,
					esProcessat,
					esPendent,
					filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
					filtre.getInteressat() != null ? filtre.getInteressat().trim() : "",
					enviatPerEmail == null,
					enviatPerEmail,
					tipusFisicaCodi == null,
					tipusFisicaCodi,
					filtre.getBackCodi() == null || filtre.getBackCodi().isEmpty(),
					filtre.getBackCodi() != null ? filtre.getBackCodi().trim() : "",
					filtre.getEstat() == null,
					filtre.getEstat(),
					filtre.isNomesAmbErrors(),
					unitat == null,
					unitat,
					bustiaOrigen == null,
					bustiaOrigen != null ? bustiaOrigen.getId() : null,
					bustia == null,
					bustia != null ? bustia.getId() : null,
					paginacioHelper.toSpringDataPageable(
							paginacioParams,
							mapeigOrdenacio));
			contextTotalfindRegistreByPareAndFiltre.stop();
			long endTime = new Date().getTime();
			logger.trace("findMovimentRegistre executed with no errors in: " + (endTime - beginTime) + "ms");
		} catch (Exception e) {
			long endTime = new Date().getTime();
			logger.error("findMovimentRegistre executed with errors in: " + (endTime - beginTime) + "ms", e);
			contextTotalfindRegistreByPareAndFiltre.stop();
			throw new RuntimeException(e);
		}
		
		
		Timer.Context contextTotaltoPaginaDto = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "findMovimentRegistre.toPaginaDto")).time();

		PaginaDto<ContingutDto> pag = paginacioHelper.toPaginaDto(
				pagina,
				ContingutDto.class,
				new Converter<VistaMovimentEntity, ContingutDto>() {
					@Override
					public ContingutDto convert(VistaMovimentEntity source) {
						return contingutHelper.movimentToContingutDto(source);
					}
				});
		contextTotaltoPaginaDto.stop();
		
		contextTotal.stop();
		return pag;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutDto> getPathContingut(
			Long entitatId, 
			Long bustiaId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				true, 
				false,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				false);
		return contingutHelper.getPathContingutComDto(bustia, true, true);
	}

	@Transactional(readOnly = true)
	@Override
	public AnotacioRegistreEntrada findOneForBackoffice(
			AnotacioRegistreId id)  {
		logger.debug("Obtenint anotació de registre per backoffice("
				+ "id=" + id + ")");
		AnotacioRegistreEntrada anotacioPerBackoffice = new AnotacioRegistreEntrada();
		try {
			RegistreEntity registreEntity = this.getRegistrePerIdentificador(id);

			EntitatDto entitatDto = new EntitatDto();
			entitatDto.setCodi(registreEntity.getEntitatCodi());
			ConfigHelper.setEntitat(entitatDto);

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
			anotacioPerBackoffice.setJustificantFitxerArxiuUuid(registreEntity.getJustificantArxiuUuid());
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
			RegistreEntity registre = this.getRegistrePerIdentificador(id);			
			
			EntitatDto entitatDto = new EntitatDto();
			entitatDto.setCodi(registre.getEntitatCodi());
			ConfigHelper.setEntitat(entitatDto);
			
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
				
				int dies = getPropertyExpedientDiesTancament();
				Date ara = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(ara);
				c.add(Calendar.DATE, dies);
				Date dataTancament = c.getTime();
				registre.updateDataTancament(dataTancament);
				
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

	/** Obté el registre per identificador. Com que les anotacions reenviades tenen el mateix identificador si se'n troba més d'una
	 * es retorna la darrera comunicada a un backoffice.
	 * 
	 * @param id
	 * @return
	 */
	private RegistreEntity getRegistrePerIdentificador(AnotacioRegistreId id) throws Exception {

		RegistreEntity registre = null;
		
		String clauSecreta = registreHelper.getClauSecretaProperty();
				
		// Cerca el registre per clau i identificador encriptades tenint en compte que pot haver anotacions reenviades		
		List<RegistreEntity> registres = registreRepository.findByNumero(id.getIndetificador());
		if (registres.isEmpty()) {
			throw new NotFoundException(
					id,
					RegistreEntity.class);
		}

		String encryptedIdentificator = "";
		for(RegistreEntity r : registres) {
			encryptedIdentificator = RegistreHelper.encrypt(
					id.getIndetificador() + "_" + new Long(r.getId()),
					clauSecreta);	
			if (encryptedIdentificator.equals(id.getClauAcces())) {
				registre = r;
				break;
			}
		}
		
		if (registre == null && registres.size() > 0) {
			// Codifica només l'identificador com es feia fins la versió 0.9.43.1 
			encryptedIdentificator = RegistreHelper.encrypt(id.getIndetificador(), clauSecreta);
			if (encryptedIdentificator.equals(id.getClauAcces())) {
				registre = registres.get(0);
				if (registres.size() > 1) {
					logger.warn("S'han trobat " + registres.size() + " registres per l'identficiador " + id.getIndetificador() + " en la consulta pel backoffice");
					registres = contingutLogRepository.findByNumeroAndComunidaBackoffice(id.getIndetificador());
					if (registres != null && !registres.isEmpty()) {
						registre = registres.get(0);
					}
				}
			}
		}
		
		if (registre == null) {
			throw new RuntimeException("La clau o identificador és incorrecte");
		}		

		return registre;
	}


	@Transactional
	@Override
	public RegistreAnnexDto getRegistreJustificant(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments) throws NotFoundException {
		
		final Timer timegetRegistreJustificant = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "getRegistreJustificant"));
		Timer.Context contexgetRegistreJustificant = timegetRegistreJustificant.time();
		
		
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		RegistreEntity registre = registreRepository.findByEntitatAndId(
				entitat,
				registreId);
		if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura() && !isVistaMoviments)
			entityComprovarHelper.comprovarBustia(
						entitat,
						registre.getPareId(),
						true);

		RegistreAnnexDto justificant = getJustificantPerRegistre(
					entitat, 
					registre);
		justificant.setRegistreId(registreId);
		
		contexgetRegistreJustificant.stop();
		return justificant;
	}

	@Override
	public boolean reintentarEnviamentBackofficeAdmin(
			Long entitatId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per admins (" +
				"entitatId=" + entitatId + ", " +
				"registreId=" + registreId + ")");

		List<Long> pendentsIds = new ArrayList<>();
		pendentsIds.add(registreId);
		Throwable exceptionProcessant = registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIds);
		return exceptionProcessant == null;

	}

	@Override
	@Transactional
	public boolean reintentarBustiaPerDefecte(
			Long entitatId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació sense bústia per admins (" +
				"entitatId=" + entitatId + ", " +
				"registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		RegistreEntity anotacio = registreRepository.findByEntitatAndId(entitat, registreId);		
		boolean success = true;
		if (anotacio.getPare() == null) {
			try {
				// Cerca la bústia destí
				BustiaEntity bustia = bustiaHelper.findBustiaDesti(
						entitat,
						anotacio.getUnitatAdministrativa());
				UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findPerEntitatAndCodi(
						entitat.getCodi(),
						anotacio.getUnitatAdministrativa());
				
				ReglaEntity reglaAplicable = null;
				if (RegistreProcesEstatEnum.ARXIU_PENDENT.equals(anotacio.getProcesEstat())) {
					reglaAplicable = reglaHelper.findAplicable(
							entitat,
							unitat.getId(),
							bustia.getId(),
							anotacio.getProcedimentCodi(),
							anotacio.getAssumpteCodi());
					anotacio.updateRegla(reglaAplicable);
				}
				
				// Mateixes accions que a bustiaService.moveAnotacioToBustiaPerDefecte
				contingutHelper.ferIEnregistrarMoviment(
						anotacio,
						bustia,
						"Anotacio sense bústia reprocessada des del llistat de l'administrador " +
						"per assignar bústia per defecte" + (reglaAplicable != null  ? "i regla aplicable" : ""),
						false,
						null);
				
				if (reglaAplicable == null 
						&& anotacio.getPare() != null) {
					emailHelper.createEmailsPendingToSend(
							(BustiaEntity) anotacio.getPare(),
							anotacio,
							anotacio.getDarrerMoviment());
				}

			} catch (Exception e) {
				success = false;
			}
		}
		return success;
	}
	
	/** Mètode per reintenta el reprocessament. Com que pot trigar molt s'ha hagut de 
	 * configurar el timeout al jboss.xml.
	 */
	@Override
	@Transactional(timeout=900)
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per admins (" +
				"entitatId=" + entitatId + ", " +
				"registreId=" + registreId + ")");
		
		RegistreDto anotacio = registreHelper.findOne(entitatId, registreId, false, null);
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		boolean pendentArxiu = RegistreProcesEstatEnum.ARXIU_PENDENT.equals(anotacio.getProcesEstat()) || RegistreProcesEstatEnum.BUSTIA_PROCESSADA.equals(anotacio.getProcesEstat());
		boolean pendentRegla = RegistreProcesEstatEnum.REGLA_PENDENT.equals(anotacio.getProcesEstat());
		Exception exceptionProcessant = null;
		if (pendentArxiu || pendentRegla) {
			if (pendentArxiu) {
				exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(
						registreId);
			}
			if (exceptionProcessant == null && pendentRegla) {
				exceptionProcessant = registreHelper.processarAnotacioPendentRegla(
						registreId);
			}
		} else {
			throw new ValidationException(
					registreId,
					RegistreEntity.class,
					"L'anotació de registre no es troba en estat pendent");
		}
		
		return exceptionProcessant == null;
	}
	
	@Override
	@Transactional(timeout=900)
	public boolean processarAnnexosAdmin(
			Long entitatId,
			Long registreId) {
		logger.debug("Intentant desat d'annexos pendents de l'anotació per admins (" +
				"entitatId=" + entitatId + ", " +
				"registreId=" + registreId + ")");

		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		Exception exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(registreId);
		return exceptionProcessant == null;
	}


	@Override
	public boolean reintentarProcessamentUser(
			Long entitatId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per usuaris (" +
				"entitatId=" + entitatId + ", " +
				"registreId=" + registreId + ")");
		Exception exceptionProcessant = processarAnotacioPendent(entitatId, registreId);
		return exceptionProcessant == null;
	}
	
	
	@Transactional
	@Override
	public boolean marcarPendent(
			Long entitatId,
			Long registreId,
			String text, 
			String rolActual) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(registreId, null);
		boolean findRol = false;
		if (!rolActual.equals("DIS_ADMIN") && !rolActual.equals("DIS_ADMIN_LECTURA")) {
			findRol = true;
		}
		entityComprovarHelper.comprovarBustia(entitat, registre.getPareId(), findRol /*!rolActual.equals("DIS_ADMIN")*/);

		registre.setProces(RegistreProcesEstatEnum.BUSTIA_PENDENT);

		// if expedient is not already closed in arxiu avoid closing it
		if (!registre.getArxiuTancat() && registre.getDataTancament() != null) {
			registre.updateDataTancament(null);
		}
		
		List<String> params = new ArrayList<>();
		params.add(registre.getNom());
		params.add(null);
		contingutLogHelper.log(
				registre,
				LogTipusEnumDto.MARCAMENT_PENDENT,
				params,
				false);
		return contingutHelper.publicarComentariPerContingut(
				entitatId,
				registreId,
				text).isPublicat();
	}
	
	
	
	

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexFitxer(Long annexId, boolean ambVersioImprimible) {
		return registreHelper.getAnnexFitxer(annexId, ambVersioImprimible);
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
	public FitxerDto getZipDocumentacio(
			Long registreId,
			String rolActual) throws Exception {

		FitxerDto zip = new FitxerDto();

		RegistreEntity registre = registreRepository.findOne(registreId);
		EntitatDto entitatDto = null;
		if (rolActual == null) {
			entitatDto = conversioTipusHelper.convertir(registre.getEntitat(), EntitatDto.class);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);		
		List<String> errors = new ArrayList<String>();	
		try {
			String nom;
			FitxerDto fitxer = null;
			// Annexos
			if (!registre.getAnnexos().isEmpty()) {
				Set<String> nomsArxius = new HashSet<String>();
				
				int nThreads = getPropertyZipNumThrads();
				ExecutorService executor = Executors.newFixedThreadPool(nThreads);

				SecurityContext context = SecurityContextHolder.getContext();
				DelegatingSecurityContextRunnable wrappedRunnable;
				for (RegistreAnnexEntity annex : registre.getAnnexos()) {						

					// S'exclouen els documents tècnics de la descàrrega
					if (annex.getSicresTipusDocument() == null
							|| !RegistreAnnexSicresTipusDocumentEnum.INTERN.equals(annex.getSicresTipusDocument())) {

						Runnable thread = 
								new GetZipDocumentacioThread(
								rolActual == null ? entitatDto : ConfigHelper.getEntitat(),
								registreHelper,
								registre.getJustificant() != null ? registre.getJustificant().getId() : null,
								registre.getNumero(),
								annex.getId(),
								annex.getTitol(),
								annex.getFitxerNom(),
								nomsArxius,
								zos,
								executor,
								errors);
						
						wrappedRunnable = new DelegatingSecurityContextRunnable(thread, context);
						executor.execute(wrappedRunnable);					}
				}

				try {Thread.sleep(200);} catch(Exception e) {};				
		        executor.shutdown();
		        
		        while (!executor.isTerminated()) {
		        	try {
		        		executor.awaitTermination(100, TimeUnit.MILLISECONDS);
		        	} catch (InterruptedException e) {}
		        }
		        if (!errors.isEmpty()) {
		        	throw new Exception(errors.size() + " errors generant el .zip " + errors.get(0));
		        }
			} else {
				// Justificant en cas de no tenir annexos
				try {
					fitxer = registreHelper.getJustificant(registreId);
					nom = fitxer.getNom();
					ZipEntry entry = new ZipEntry(revisarContingutNom(nom));
					entry.setSize(fitxer.getContingut().length);
					zos.putNextEntry(entry);
					zos.write(fitxer.getContingut());
					zos.closeEntry();
				} catch (Exception e) {
					String errMsg = "Error afegint justificant del registre " + registre.getNumero() + " al document zip comprimit: " + e.getMessage();
					logger.error(errMsg);
					throw new Exception(errMsg, e);
				}
			}			
			
			zos.close();
			
			if (!errors.isEmpty()) {
				throw new Exception(errors.size() + " errors generant el .zip " + errors.get(0));
			} else {	
				zip.setNom(revisarContingutNom(registre.getNumero()) + ".zip");
				zip.setContingut(baos.toByteArray());
				zip.setContentType("application/zip");
				return zip;
			}

		} catch (Exception ex) {
			String errMsg = "Error generant el .zip de documentació pel registre " + registre.getNumero() + " amb ID " + registreId + " : " + ex.getMessage();
			logger.error(errMsg, ex);
			throw new Exception(errMsg, ex);
		}
	}
	
	/** Classe runable per obtenir el document en un thread diferent i guardar-lo en el zip.
	 * 
	 */
	protected class GetZipDocumentacioThread implements Runnable {

		private EntitatDto entitatActual;
		private Long justificantId;
		private String registreNumero;
		private Long annexID;
		private String annexTitol; 
		private String annexFitxerNom;
		private Set<String> nomsArxius;
		private ZipOutputStream zip;
		ExecutorService executor;
		private List<String> errors;		
		
		/** Constructor amb els objectes de consulta i el zip per actualitzar. 
		 * @param registreHelper 
		 * @param errors 
		 * @param errors */
		public GetZipDocumentacioThread(
				EntitatDto entitatActual,
				RegistreHelper registreHelper, 
				Long justificantId, 
				String registreNumero, 
				Long annexID,
				String annexTitol, 
				String annexFitxerNom, 
				Set<String> nomsArxius,
				ZipOutputStream zip, 
				ExecutorService executor, 
				List<String> errors) {
			this.entitatActual = entitatActual;
			this.justificantId = justificantId;
			this.registreNumero = registreNumero;
			this.annexID = annexID;
			this.annexTitol = annexTitol;
			this.annexFitxerNom = annexFitxerNom;
			this.nomsArxius = nomsArxius;
			this.zip = zip;
			this.executor = executor;
			this.errors = errors;			
		}

		@Transactional(propagation=Propagation.REQUIRED)
		@Override
		public void run() {
			ConfigHelper.setEntitat(this.entitatActual);
			try {		
				String nom;
				FitxerDto fitxer = new FitxerDto();
				try {
					fitxer = registreHelper.getAnnexFitxer(annexID, true);
				} catch (Exception e) {
					logger.warn("Error obtenint la versió imprimible del l'annex " + this.annexID + "\"" + annexTitol + "\", es procedeix a consultar l'original. Error: "
								+ e.getClass() + " " + e.getMessage());
					fitxer = registreHelper.getAnnexFitxer(annexID, false);
				}
						
				String fitxerNom = annexFitxerNom;
				if (justificantId == null || annexID != justificantId) {
					if (fitxerNom.startsWith(annexTitol)) {
						nom = fitxerNom;
					} else {
						nom = annexTitol + " - " + fitxerNom;
					}
				} else {
					nom = fitxerNom;
				}
				synchronized (nomsArxius) {
					ZipEntry entry = new ZipEntry(getZipRecursNom(revisarContingutNom(nom), nomsArxius));
					entry.setSize(fitxer.getContingut().length);
					zip.putNextEntry(entry);
					zip.write(fitxer.getContingut());
					zip.closeEntry();
				}
			} catch (Throwable  e) {
				String errMsg = "Error afegint l'annex " + annexTitol + " del registre " + registreNumero + " al document zip comprimit: " + e.getMessage();
				logger.error(errMsg);				
				errors.add(errMsg);
				executor.shutdownNow();				
			}						
		}		
	}
	
	private static String revisarContingutNom(String nom) {
		if (nom == null) {
			return null;
		}
		return nom.replace("&", "&amp;").replaceAll("[\\\\/:*?\"<>|]", "_");
	}
	private String getZipRecursNom(String nom, Set<String> nomsArxius) {
		String recursNom;

		// Vigila que no es repeteixi
		int comptador = 0;
		do {
			recursNom = FilenameUtils.removeExtension(nom) + 
					(comptador > 0 ? " (" + comptador + ")" : "") +
					"." + FilenameUtils.getExtension(nom);
			comptador++;
		} while (nomsArxius.contains(recursNom));

		// Guarda en nom com a utiltizat
		nomsArxius.add(recursNom);
		return recursNom;
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
			Long registreId,
			Long annexId,
			boolean isVistaMoviments) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura() && !isVistaMoviments) {
			RegistreEntity registre = registreRepository.findByEntitatAndId(entitat, registreId);
			entityComprovarHelper.comprovarBustia(
					entitat,
					registre.getPareId(),
					true);
		}
		
		return registreHelper.getAnnexAmbFirmes(
				annexId);
	}
	
	@Transactional(readOnly = true)
	@Override
	public RegistreAnnexDto getAnnexSenseFirmes(
			Long entitatId,
			Long registreId,
			Long annexId,
			boolean isVistaMoviments) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		RegistreEntity registre = registreRepository.findByEntitatAndId(entitat, registreId);
		if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura() && !isVistaMoviments) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					registre.getPareId(),
					true);			
		}

		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findByRegistreAndId(registre, annexId);
		
		RegistreAnnexDto annex = conversioTipusHelper.convertir(
				registreAnnexEntity,
				RegistreAnnexDto.class);
		if (annex.getFitxerNom().contains(".xml")) {
			Map<String, String> metadadesMap = new HashMap<String, String>();
			metadadesMap.put("eni:id_tramite", registre.getProcedimentCodi());
			annex.setMetaDadesMap(metadadesMap);
		}
		return annex;
	}
	

	@Transactional(readOnly = true)
	@Override
	public int getNumberThreads() {
		return registreHelper.getMaxThreadsParallelProperty();
	}
	
	@Transactional
	@Override
	public RegistreDto marcarLlegida(
			Long entitatId,
			Long registreId) {
		logger.debug("Marcan com a llegida l'anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		RegistreEntity registre = registreRepository.findByEntitatAndId(
				entitat,
				registreId);

		if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura())
			entityComprovarHelper.comprovarBustia(
						entitat,
						registre.getPareId(),
						true);

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
	
	@Transactional
	@Override
	public void marcarSobreescriure(
			Long entitatId,
			Long registreId) {
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(registreId, null);
		registre.updateSobreescriure(true);
	}	

	@Override
	@Transactional
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long registreId,
			String procedimentCodi)
			throws NotFoundException {
		logger.debug("classificant l'anotació de registre (" +
				"entitatId=" + entitatId + ", " +
				"registreId=" + registreId + ", " +
				"procedimentCodi=" + procedimentCodi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
				
		RegistreEntity registre = registreRepository.findOneAmbBloqueig(entitatId, registreId);
		
		if (isPermesReservarAnotacions())
			registreHelper.comprovarRegistreAlliberat(registre);
		
		if (registre.getPare() == null)
			throw new ValidationException(
					registreId,
					RegistreEntity.class,
					"El registre (id=" + registreId + ") no té cap bústia assignada i per tant no es pot recuperar la llista de procediments associats a la bústia per procedir a la classificació.");

		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				registre.getPareId(),
				this.comprovarPermisLectura());
		
		registre.updateProcedimentCodi(procedimentCodi);
		ReglaEntity reglaAplicable = reglaHelper.findAplicable(
				entitat,
				bustia.getUnitatOrganitzativa().getId(),
				registre.getPare() != null? registre.getPare().getId() : null,
				registre.getProcedimentCodi(),
				registre.getAssumpteCodi());
		ClassificacioResultatDto classificacioResultat = new ClassificacioResultatDto();
		if (reglaAplicable != null) {
			registre.updateRegla(reglaAplicable);
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
		
		
		// Recuperar registres duplicats
		List<RegistreEntity> registreRepetit = registreRepository.findRegistresByEntitatCodiAndLlibreCodiAndRegistreTipusAndNumeroAndDataAndEsborrat(
				registre.getEntitatCodi(),
				registre.getLlibreCodi(),
				RegistreTipusEnum.ENTRADA.getValor(),
				registre.getNumero(),
				registre.getData(),
				0);

		// Comprovar si existeixen moviments del registre al destí seleccionat
		List<ContingutMovimentEntity> contingutMoviments = contingutHelper.comprovarExistenciaAnotacioEnDesti(registreRepetit, bustia.getId());

		ContingutEntity registrePerClassificar = null;
		Map<RegistreEntity, ContingutMovimentEntity> registresAmbCanviEstat = new HashMap<RegistreEntity, ContingutMovimentEntity>();
		for (ContingutMovimentEntity contingutMovimentEntity : contingutMoviments) {
			registrePerClassificar = contingutMovimentEntity.getContingut();
			
			// Tornar a marcar l'anotació com a pendent si estava processada i es rep per duplicat
			registre = (RegistreEntity)registrePerClassificar;
			if (!registre.getPendent()) {
				registresAmbCanviEstat.put(registre, contingutMovimentEntity);
			}
			// Actualitzar històric i crear coa correus per cada registre a crear
			updateMovimentDetail(
					registrePerClassificar, 
					contingutMovimentEntity);
		}
		
		
		return classificacioResultat;
	}
	
	private void updateMovimentDetail(
			ContingutEntity registrePerClassificar, 
			ContingutMovimentEntity contingutMoviment) {
		
		contingutLogHelper.logMoviment(
				registrePerClassificar,
				LogTipusEnumDto.CLASSIFICAR,
				contingutMoviment,
				true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId) {
		logger.debug("classificant l'anotació de registre (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ")");
		List<ProcedimentDto> dtos = new ArrayList<ProcedimentDto>();
		if (bustiaId != null && bustiaId > 0) {
			
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					true,
					false,
					false);
			BustiaEntity bustia = entityComprovarHelper.comprovarBustia(	
															entitat,
															bustiaId,
															this.comprovarPermisLectura());
			List<String> llistaUnitatsDescendents = unitatOrganitzativaHelper.getCodisOfUnitatsDescendants(entitat, bustia.getUnitatOrganitzativa().getCodi());			
			llistaUnitatsDescendents.add(bustia.getUnitatOrganitzativa().getCodi());
			// Cerca del llistat de procediments amb consulta a la bbdd
			List<ProcedimentEntity> procediments = getPerUnitatOrganitzativaIDescendents(entitatId, llistaUnitatsDescendents);
			if (procediments != null) {
//				getProcediments(dtos, procediments);
				dtos = conversioTipusHelper.convertirList(procediments, ProcedimentDto.class);
			}
		}
		// Ordenar per codi SIA
		if (!dtos.isEmpty()) {
			Collections.sort(dtos);
		}
		return dtos;
	}
	
	@Transactional
	private String getCodiUnitatOrganitzativaArrel(String codiUnitat) {
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodi(codiUnitat);
		if (unitatOrganitzativa == null) {
			return codiUnitat;		
		} 
		if (!unitatOrganitzativa.getCodi().equals(unitatOrganitzativa.getCodiUnitatArrel())) {
			return getCodiUnitatOrganitzativaArrel(unitatOrganitzativa.getCodiUnitatSuperior());
		}
		return unitatOrganitzativa.getCodi();
		
	}
	
	
	private List<ProcedimentEntity> getPerUnitatOrganitzativaIDescendents(Long entitatId, List<String> llistaUnitatsOrganitzatives) {
		List<ProcedimentEntity> llistaProcediments = new ArrayList<>(); 
		for (String codiUnitatOrganitzativa : llistaUnitatsOrganitzatives) {
			List<ProcedimentEntity> procediments = procedimentRepository.findByCodiUnitatOrganitzativa(entitatId, codiUnitatOrganitzativa);
			llistaProcediments.addAll(procediments);
		}
		
		return llistaProcediments;
	}

	/** Retorna true si no és administrador nii admin lectura. */
	private boolean comprovarPermisLectura() {
		return !usuariHelper.isAdmin() && !usuariHelper.isAdminLectura();
	}


	@Transactional
	@Override
	public void bloquejar(Long entitatId, Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Agafant l'anotació com a usuari (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "usuari=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		if (!isPermesReservarAnotacions()) {
			throw new ValidationException(
					id, 
					RegistreEntity.class, 
					"La funcionalitat de reserva d'anotacions no està activa");
		}
		
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(id, null);
		entityComprovarHelper.comprovarBustia(entitat, registre.getPareId(), true);
		
		registreHelper.bloquejar(
				registre, 
				usuariHelper.getUsuariAutenticat().getCodi());
	}
	
	@Transactional
	@Override
	public void alliberar(Long entitatId, Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Alliberant l'anotació com a usuari (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "usuari=" + auth.getName() + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		boolean permetreReservarAnotacio = configHelper.getAsBoolean("es.caib.distribucio.anotacions.permetre.reservar");
		if (!permetreReservarAnotacio) {
			throw new ValidationException(
					id, 
					RegistreEntity.class, 
					"La funcionalitat de reserva d'anotacions no està activa");
		}
		
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(id, null);
		entityComprovarHelper.comprovarBustia(entitat, registre.getPareId(), true);
		
		registreHelper.alliberar(
				registre, 
				usuariHelper.getUsuariAutenticat().getCodi());
	}

	@Transactional
	@Override
	public ValidacioFirmaEnum validarFirmes(Long entitatId, Long registreId, Long annexId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Validant les firmes d'annex (" + "entitatId=" + entitatId + ", " + "registreId=" + registreId + ", annexId=" + annexId + ", usuari=" + auth.getName() + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ValidacioFirmaEnum validacioFirma = registreHelper.validaFirmes(
						registreAnnexRepository.findOne(annexId));
		
		// Si la firma és vàlida i està com a esborrany i el document està firmat llavors es pot 
		// guardar com a definitiu
		if (ValidacioFirmaEnum.isValida(validacioFirma)) {
			// Recuperar informació de l'annex
			RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
			// Modificar 
			if (annex.getFitxerArxiuUuid() != null) {
				pluginHelper.arxiuDocumentSetDefinitiu(annex);
			}
		}
		return validacioFirma;
	}
	


	@Override
	@Transactional(readOnly=true)
	public List<ProcedimentDto> procedimentFindByCodiSia(long entitatId, String codiSia) {

		List<ProcedimentDto> procedimentDto = new ArrayList<>();
		
		// Treu el procediment cridant a la bbdd
		List<ProcedimentEntity> procediments = procedimentRepository.findByCodiSia(
				entitatId, 
				codiSia == null || codiSia.isEmpty(), 
				codiSia != null && !codiSia.isEmpty() ? codiSia : "");
		
		procedimentDto = conversioTipusHelper.convertirList(procediments, ProcedimentDto.class);

				
		return procedimentDto;
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
				boolean retornarAnnexIFirmaContingut = configHelper.getAsBoolean(
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
										firma.getPerfil() != null ? es.caib.distribucio.core.api.service.ws.backoffice.FirmaPerfil.valueOf(firma.getPerfil().name()) : null);
								break;
							}
						}
					}
					// Estat DEFINITIU/ESBORRANY
					switch(document.getEstat()) {
					case DEFINITIU:
						annexPerBackoffice.setEstat(AnnexEstat.DEFINITIU);
						break;
					case ESBORRANY:
						annexPerBackoffice.setEstat(AnnexEstat.ESBORRANY);
						break;
					}
					// Informació de si l'annex és vàlid
					boolean documentValid = true;
					StringBuilder documentError = new StringBuilder();
					if (annexEntity.getValidacioFirmaEstat() == ValidacioFirmaEnum.FIRMA_INVALIDA) {
						documentValid = false;
						documentError.append("El document original tenia firmes invàlides.");
					}
					if (document.getMetadades() != null && document.getMetadades().getFormat() == null) {
						documentError.append(" El document no té un format reconegut per l'Arxiu");
					}
//					if (document.getEstat() == DocumentEstat.ESBORRANY) {
//						documentValid = false;
//						documentError.append(" El document s'ha guardat com esborrany per poder distribuir-lo.");
//					}
					annexPerBackoffice.setDocumentValid(documentValid);
					if (!documentValid) {
						annexPerBackoffice.setDocumentError(documentError.toString());
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
			case COMPAREIXENSA:
				ntiTipoDocumento = NtiTipoDocumento.COMPAREIXENSA;
				break;
			case CONVOCATORIA:
				ntiTipoDocumento = NtiTipoDocumento.CONVOCATORIA;
				break;
			case DICTAMEN_COMISSIO:
				ntiTipoDocumento = NtiTipoDocumento.DICTAMEN_COMISSIO;
				break;
			case ESCRIT:
				ntiTipoDocumento = NtiTipoDocumento.ESCRIT;
				break;
			case ESQUEMA:
				ntiTipoDocumento = NtiTipoDocumento.ESMENA;
				break;
			case INFORME_PONENCIA:
				ntiTipoDocumento = NtiTipoDocumento.INFORME_PONENCIA;
				break;
			case INICIATIVA_LEGISLATIVA:
				ntiTipoDocumento = NtiTipoDocumento.INICIATIVA_LEGISLATIVA;
				break;
			case INICIATIVA__LEGISLATIVA:
				ntiTipoDocumento = NtiTipoDocumento.INICIATIVA__LEGISLATIVA;
				break;
			case INSTRUCCIO:
				ntiTipoDocumento = NtiTipoDocumento.INSTRUCCIO;
				break;
			case INTERPELACIO:
				ntiTipoDocumento = NtiTipoDocumento.INTERPELACIO;
				break;
			case LLEI:
				ntiTipoDocumento = NtiTipoDocumento.LLEI;
				break;
			case MOCIO:
				ntiTipoDocumento = NtiTipoDocumento.MOCIO;
				break;
			case ORDRE_DIA:
				ntiTipoDocumento = NtiTipoDocumento.ORDRE_DIA;
				break;
			case PETICIO:
				ntiTipoDocumento = NtiTipoDocumento.PETICIO;
				break;
			case PREGUNTA:
				ntiTipoDocumento = NtiTipoDocumento.PREGUNTA;
				break;
			case PROPOSICIO_NO_LLEI:
				ntiTipoDocumento = NtiTipoDocumento.PROPOSICIO_NO_LLEI;
				break;
			case PROPOSTA_RESOLUCIO:
				ntiTipoDocumento = NtiTipoDocumento.PROPOSTA_RESOLUCIO;
				break;
			case RESPOSTA:
				ntiTipoDocumento = NtiTipoDocumento.RESPOSTA;
				break;
			case SOLICITUD_INFORMACIO:
				ntiTipoDocumento = NtiTipoDocumento.SOLICITUD_INFORMACIO;
				break;
			default:
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
			case ADMIN_LECTURA:
				ntiOrigen = NtiOrigen.ADMIN_LECTURA;
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
		case ADMIN_LECTURA:
			interessatBase.setTipus(InteressatTipus.ADMIN_LECTURA);
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
		
		if (registreInteressatEntity.getTipus() == RegistreInteressatTipusEnum.ADMINISTRACIO 
			&& registreInteressatEntity.getTipus() == RegistreInteressatTipusEnum.ADMIN_LECTURA 
			&& registreInteressatEntity.getDocumentTipus() == RegistreInteressatDocumentTipusEnum.CODI_ORIGEN) {
			interessatBase.setOrganCodi(registreInteressatEntity.getDocumentNum());
		}
		
		
		return interessatBase;
	}

	private Exception processarAnotacioPendent(long entitatId, long anotacioId) {
		
		RegistreDto anotacio = registreHelper.findOne(entitatId, anotacioId, false, null);
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		boolean pendentArxiu = RegistreProcesEstatEnum.ARXIU_PENDENT.equals(anotacio.getProcesEstat()) || RegistreProcesEstatEnum.BUSTIA_PROCESSADA.equals(anotacio.getProcesEstat());
		boolean pendentRegla = RegistreProcesEstatEnum.REGLA_PENDENT.equals(anotacio.getProcesEstat());
		Exception exceptionProcessant = null;
		if (pendentArxiu || pendentRegla) {
			if (pendentArxiu) {
				exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(
						anotacioId);
			}
			if (exceptionProcessant == null && pendentRegla) {
				exceptionProcessant = registreHelper.processarAnotacioPendentRegla(
						anotacioId);
			}
		} else {
			throw new ValidationException(
					anotacioId,
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
	
	private boolean isPermesReservarAnotacions() {
		return configHelper.getAsBoolean("es.caib.distribucio.anotacions.permetre.reservar");
	}
	
	/** Número de dies per programar el tancament de l'expedient a l'Arxiu, per defecte 4 anys (1461 dies). */
	private int getPropertyExpedientDiesTancament() {
		String numDies = configHelper.getConfig(
				"es.caib.distribucio.tancament.expedient.dies",
				"1461");
		return Integer.parseInt(numDies);
	}

	/** Número de threads màxim per a la consulta y generació del .zip de documentació (per defecte 3 i com a mínim 1). */
	private int getPropertyZipNumThrads() {
		String numThreads = configHelper.getConfig(
				"es.caib.distribucio.contingut.generacio.zip.num.threads",
				"3");
		int ret = 3;
		try {
			ret = Integer.parseInt(numThreads);
			if (ret < 1) {
				ret = 1;
			}
		} catch(Exception e) {
			logger.error(
					"Error interpretant la propietat de número de threads per la descàrrega .zip de la documentació : "
							+ numThreads + ". Error: " + e.getMessage(),e);
		}
		return ret;
	}


	@Transactional
	@Override
	public List<RegistreAnnexFirmaDto> getDadesAnnexFirmesSenseDetall(Long annexId) {
		List<RegistreAnnexFirmaDto> registresAnnexFirmesDto = new ArrayList<>();
		RegistreAnnexFirmaEntity registreAnnexFirmaEntity = registreAnnexFirmaRepository.getRegistreAnnexFirmaSenseDetall(annexId);
		if(registreAnnexFirmaEntity != null) {
			RegistreAnnexFirmaDto registreAnnexFirmaDto = conversioTipusHelper.convertir(registreAnnexFirmaEntity, RegistreAnnexFirmaDto.class);
			registresAnnexFirmesDto.add(registreAnnexFirmaDto);
		}
		return registresAnnexFirmesDto;
	}

	@Override
	public String obtenirRegistreIdEncriptat(Long registreId) {

		String clau = null;
		try {
			clau = registreHelper.encriptar(String.valueOf(registreId));
		} catch (Exception e) {
			logger.error("Error al encriptar l'id del registre: " + e.toString(), e);
		}
		return clau;
	}

	@Override
	public String obtenirRegistreIdDesencriptat(String clau) throws Exception{
		try {
			clau = registreHelper.desencriptar(clau);
		} catch (Exception e) {
			throw new Exception("Error al desencriptar l'id del registre: " + e.toString(), e);
		}
		return clau;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegistreServiceImpl.class);

}
