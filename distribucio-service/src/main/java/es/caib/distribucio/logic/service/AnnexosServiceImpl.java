/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
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
		
		return paginacioHelper.toPaginaDto(
//				registreAnnexRepository.findAll(paginacioHelper.toSpringDataPageable(paginacioParams)),
				
				
				registreAnnexRepository.findByFiltrePaginat(
						entitat,
						!(filtre.getNumero()!=null &&  !filtre.getNumero().isEmpty()),
						filtre.getNumero(),
						filtre.getArxiuEstat()==null,
						filtre.getArxiuEstat(),
						!(tipusFirma!=null &&  !tipusFirma.isEmpty()),
						tipusFirma,
						!(filtre.getTitol()!=null &&  !filtre.getTitol().isEmpty()),
						filtre.getTitol(),
						!(filtre.getFitxerNom()!=null &&  !filtre.getFitxerNom().isEmpty()),
						filtre.getFitxerNom(),
						!(filtre.getFitxerTipusMime()!=null &&  !filtre.getFitxerTipusMime().isEmpty()),
						filtre.getFitxerTipusMime(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
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
	public List<Long> findAnnexIds(AnnexosFiltreDto filtre) {
		
		String tipusFirma = "";
		ArxiuFirmaTipusEnumDto arxiuFirmaTipusEnumDto = filtre.getTipusFirma();
		if (arxiuFirmaTipusEnumDto!=null) {
			tipusFirma = ArxiuConversions.toArxiuFirmaTipusEnumDto(arxiuFirmaTipusEnumDto);
		}
		
		List<Long> ids = registreAnnexRepository.findIdsByFiltre(
				!(filtre.getNumero()!=null &&  !filtre.getNumero().isEmpty()),
				filtre.getNumero(),
				filtre.getArxiuEstat()==null,
				filtre.getArxiuEstat(),
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
		Long anotacioId = registre.getId();
		String arxiuDistribucioUuid = null;
		ResultatAnnexDefinitiuDto resultatAnnexDefinitiu = new ResultatAnnexDefinitiuDto();
		resultatAnnexDefinitiu.setAnnexId(annexId);
		resultatAnnexDefinitiu.setAnotacioNumero(anotacioId);
		
		
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
			throw ex;
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
			return resultatAnnexDefinitiu;						
		}		
		
		try {
			DistribucioRegistreAnnex distribucioRegistreAnnex = conversioTipusHelper.convertir(
					registreAnnex, 
					DistribucioRegistreAnnex.class);
			
			List<String> titolsAnnexes = annexosAdminHelper.getTitolsAnnexes(registre);
			
			boolean titolRepetit = Collections.frequency(titolsAnnexes, distribucioRegistreAnnex.getTitol()) > 1;

			registreHelper.crearAnnexInArxiu(
					annexId, 
					distribucioRegistreAnnex, 
					unitatOrganitzativaCodi,
					distribucioRegistreAnotacio.getExpedientArxiuUuid(),
					distribucioRegistreAnotacio.getProcedimentCodi(), 
					titolRepetit);
		} catch (Exception ex) {
			throw ex;
		}
		
		resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.updated");
		resultatAnnexDefinitiu.setOk(true);
		return resultatAnnexDefinitiu;				
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AnnexosServiceImpl.class);
}
