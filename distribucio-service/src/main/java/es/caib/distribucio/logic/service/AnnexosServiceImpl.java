/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.AnnexosAdminHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper.Converter;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.RegistreHelper;
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.ArxiuDetallDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentEstat;

/**
 * Implementació dels mètodes per a gestionar annexos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AnnexosServiceImpl implements AnnexosService {

	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	PaginacioHelper paginacioHelper;
	@Resource
	private AnnexosAdminHelper annexosAdminHelper;
	@Resource
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private RegistreHelper registreHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<RegistreAnnexDto> findAdmin(
			Long entitatId,
			AnnexosFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta d'annexos per usuari admin ("				
				+ "filtre=" + filtre + ")");		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		String tipusFirma = "";
		ArxiuFirmaTipusEnumDto arxiuFirmaTipusEnumDto = filtre.getTipusFirma();
		if (arxiuFirmaTipusEnumDto!=null) {
			tipusFirma = ArxiuConversions.toArxiuFirmaTipusEnumDto(arxiuFirmaTipusEnumDto);
		}
		
		Date dataRecepcioFi = filtre.getDataRecepcioFi();
		if (dataRecepcioFi != null) {
			Calendar c = new GregorianCalendar();
			c.setTime(dataRecepcioFi);
			c.add(Calendar.HOUR, 24);
			dataRecepcioFi = c.getTime();
		}
		
		Map<String, String[]> mapeigOrdenacio = new HashMap<String, String[]>();
		mapeigOrdenacio.put(
				"dataAnotacio",
				new String[] {"registre.data", "dataCaptura"});
		mapeigOrdenacio.put(
				"registreNumero",
				new String[] {"registre.numero"});
		
		Page<RegistreAnnexEntity> annexosPage = registreAnnexRepository.findByFiltrePaginat(
				entitat,
				!(filtre.getNumero()!=null &&  !filtre.getNumero().isEmpty()),
				filtre.getNumero(),
				filtre.getNumeroCopia() == null,
				filtre.getNumeroCopia(),
				filtre.getArxiuEstat()==null,
				filtre.getArxiuEstat(),
				filtre.getDataRecepcioInici() == null,
				filtre.getDataRecepcioInici(),
				dataRecepcioFi == null,
				dataRecepcioFi,
				!(tipusFirma!=null &&  !tipusFirma.isEmpty()),
				tipusFirma,
				!(filtre.getTitol()!=null &&  !filtre.getTitol().isEmpty()),
				filtre.getTitol(),
				!(filtre.getFitxerNom()!=null &&  !filtre.getFitxerNom().isEmpty()),
				filtre.getFitxerNom(),
				!(filtre.getFitxerTipusMime()!=null &&  !filtre.getFitxerTipusMime().isEmpty()),
				filtre.getFitxerTipusMime(),
				paginacioHelper.toSpringDataPageable(paginacioParams, mapeigOrdenacio));
		
		return paginacioHelper.toPaginaDto(
				annexosPage,
				RegistreAnnexDto.class,
				new Converter<RegistreAnnexEntity, RegistreAnnexDto>() {
					@Override
					public RegistreAnnexDto convert(RegistreAnnexEntity source) {
						return annexosAdminHelper.toRegistreAnnexDto(
								source);
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Long> findAnnexIds(
			Long entitatId,
			AnnexosFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		String tipusFirma = "";
		ArxiuFirmaTipusEnumDto arxiuFirmaTipusEnumDto = filtre.getTipusFirma();
		if (arxiuFirmaTipusEnumDto!=null) {
			tipusFirma = ArxiuConversions.toArxiuFirmaTipusEnumDto(arxiuFirmaTipusEnumDto);
		}

		Date dataRecepcioFi = filtre.getDataRecepcioFi();
		if (dataRecepcioFi != null) {
			Calendar c = new GregorianCalendar();
			c.setTime(dataRecepcioFi);
			c.add(Calendar.HOUR, 24);
			dataRecepcioFi = c.getTime();
		}
		
		List<Long> ids = registreAnnexRepository.findIdsByFiltre(
				entitat,
				!(filtre.getNumero()!=null &&  !filtre.getNumero().isEmpty()),
				filtre.getNumero(),
				filtre.getNumeroCopia() == null,
				filtre.getNumeroCopia(),
				filtre.getArxiuEstat()==null,
				filtre.getArxiuEstat(),
				filtre.getDataRecepcioInici() == null,
				filtre.getDataRecepcioInici(),
				dataRecepcioFi == null,
				dataRecepcioFi,
				!(tipusFirma!=null &&  !tipusFirma.isEmpty()),
				tipusFirma,
				!(filtre.getTitol()!=null &&  !filtre.getTitol().isEmpty()),
				filtre.getTitol(),
				!(filtre.getFitxerNom()!=null &&  !filtre.getFitxerNom().isEmpty()),
				filtre.getFitxerNom(),
				!(filtre.getFitxerTipusMime()!=null &&  !filtre.getFitxerTipusMime().isEmpty()),
				filtre.getFitxerTipusMime());
		
		
		return ids;
	}
			
	@Transactional(readOnly = false)
	@Override
	public ResultatAnnexDefinitiuDto guardarComADefinitiu(Long annexId) {		
		
		logger.debug("Guardar com a definitiu l'annex " + annexId);		
		RegistreAnnexEntity registreAnnex = registreAnnexRepository.findById(annexId).get();		
		RegistreEntity registre = registreAnnex.getRegistre();
		String arxiuDistribucioUuid = null;
		ResultatAnnexDefinitiuDto resultatAnnexDefinitiu = new ResultatAnnexDefinitiuDto();
		resultatAnnexDefinitiu.setAnnexId(annexId);
		resultatAnnexDefinitiu.setAnnexTitol(registreAnnex.getTitol());
		resultatAnnexDefinitiu.setAnotacioNumero(registre.getNumero());
		
		
		// Comprovar si a Distribució hi ha l'annex ja marcat com a definitiu:
		AnnexEstat arxiuEstat = registreAnnex.getArxiuEstat();
		if ((arxiuEstat!=null)&&(arxiuEstat.equals(AnnexEstat.DEFINITIU))) {		
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.jaDefinitiu");
			resultatAnnexDefinitiu.setOk(false);
			return resultatAnnexDefinitiu;						
		}
		
		
		try {
			
			// Si el registre està tancat ja no cal continuar:
			ArxiuDetallDto arxiuDetall = registreService.getArxiuDetall(registre.getId());
			if (arxiuDetall.getEniEstat()==ExpedientEstatEnumDto.TANCAT) {	
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.expedientTancat");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;								
			}
		
			// Si el document està com a definitiu en l'arxiu posar com a definitiu a distribució:
			Document document = pluginHelper.arxiuDocumentConsultar(
					registreAnnex.getFitxerArxiuUuid(), null, true, false, registre.getNumero());
			if (document.getEstat().equals(DocumentEstat.DEFINITIU)) {
				registreAnnex.setArxiuEstat(AnnexEstat.DEFINITIU);	
				registreAnnexRepository.save(registreAnnex);
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.definitiuArxiu");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;	
			}
			
			// Si el document s'ha mogut a un expedient del backoffice no continuem
			if (document.getExpedientMetadades() != null) {
				String expedientUuid = document.getExpedientMetadades().getIdentificador();
				//String arxiuDistribucioUuid = registreAnnex.getFitxerArxiuUuid();
				arxiuDistribucioUuid = registre.getArxiuUuid();
				if (!expedientUuid.equals(arxiuDistribucioUuid)) {	
					resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.mogutBackoffice");
					resultatAnnexDefinitiu.setOk(false);
					return resultatAnnexDefinitiu;	
				}
			}
		} catch (Exception ex) {
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorArxiu");
			resultatAnnexDefinitiu.setOk(false);
			resultatAnnexDefinitiu.setThrowable(ex);
			return resultatAnnexDefinitiu;
		}

		// Si arribem fins aquí podem reintentar guardar l'annex  a l'arxiu i provarà de validar i firmar en cas que sigui necessari
		
		List<Throwable> exceptions = null;
		
		DistribucioRegistreAnotacio distribucioRegistreAnotacio = 
				registreHelper.getDistribucioRegistreAnotacio(registre.getId());		
		String unitatOrganitzativaCodi = distribucioRegistreAnotacio.getUnitatOrganitzativaCodi();
		
		if (distribucioRegistreAnotacio.getExpedientArxiuUuid() == null)
			exceptions = registreHelper.crearExpedientArxiu(
					distribucioRegistreAnotacio, 
					unitatOrganitzativaCodi, 
					arxiuDistribucioUuid);

		if (exceptions != null && !exceptions.isEmpty()) {
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorUpdate");
			resultatAnnexDefinitiu.setOk(false);
			resultatAnnexDefinitiu.setThrowable(exceptions.get(0));
			return resultatAnnexDefinitiu;						
		}		
		
		try {
			DistribucioRegistreAnnex distribucioRegistreAnnex = conversioTipusHelper.convertir(
					registreAnnex, 
					DistribucioRegistreAnnex.class);
			
			registreHelper.crearAnnexInArxiu(
					annexId, 
					distribucioRegistreAnnex, 
					unitatOrganitzativaCodi,
					distribucioRegistreAnotacio.getExpedientArxiuUuid(),
					distribucioRegistreAnotacio.getProcedimentCodi());
			
			ValidacioFirmaEnum estatValidacioFirma = distribucioRegistreAnnex.getValidacioFirmaEstat();
			
			if (estatValidacioFirma != null && 
					(estatValidacioFirma.equals(ValidacioFirmaEnum.FIRMA_INVALIDA) || estatValidacioFirma.equals(ValidacioFirmaEnum.ERROR_VALIDANT))) {
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorFirma");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;		
			}
			
			entityManager.refresh(registreAnnex);
			
			if (registreAnnex.getArxiuEstat().equals(AnnexEstat.ESBORRANY)) {
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.senseFirma");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;
			}
			
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.updated");
			resultatAnnexDefinitiu.setOk(true);
		} catch (Exception ex) {
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorUpdate");
			resultatAnnexDefinitiu.setOk(false);
			resultatAnnexDefinitiu.setThrowable(ex);
		}		
		return resultatAnnexDefinitiu;				
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<RegistreAnnexDto> findMultiple(
			Long entitatId,
			List<Long> multipleAnnexosIds,
			boolean isAdmin) {
		logger.debug("Obtenint annexos per processar ("
				+ "entitatId=" + entitatId + ", "
				+ "multipleRegistreIds=" + multipleAnnexosIds
				+ "isAdmin=" + isAdmin + " )");
		
		if (multipleAnnexosIds == null || multipleAnnexosIds.isEmpty()) {
			return new ArrayList<RegistreAnnexDto>();
		}
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		
		List<RegistreAnnexEntity> annexos = registreAnnexRepository.findByIdIn(multipleAnnexosIds);

		List<RegistreAnnexDto> resposta = new ArrayList<RegistreAnnexDto>();
		for (RegistreAnnexEntity registreAnnexEntity: annexos) {
			RegistreAnnexDto registreAnnexDto = conversioTipusHelper.convertir(registreAnnexEntity, RegistreAnnexDto.class);
			
			resposta.add(registreAnnexDto);
		}
		return resposta;
	}

	@Override
	public List<Integer> findCopiesRegistre(String numero) {
		return registreHelper.findCopiesRegistre(numero);
	}

	private static final Logger logger = LoggerFactory.getLogger(AnnexosServiceImpl.class);
}
