/**
 * 
 */
package es.caib.distribucio.logic.service;

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
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.ArxiuDetallDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;

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
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private PluginHelper pluginHelper;

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<RegistreAnnexDto> findAdmin(			
			AnnexosFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta d'annexos per usuari admin ("				
				+ "filtre=" + filtre + ")");		
		
		
		String tipusFirma = "";
		ArxiuFirmaTipusEnumDto arxiuFirmaTipusEnumDto = filtre.getTipusFirma();
		if (arxiuFirmaTipusEnumDto!=null) {
			tipusFirma = ArxiuConversions.toArxiuFirmaTipusEnumDto(arxiuFirmaTipusEnumDto);
		}
		
		return paginacioHelper.toPaginaDto(
//				registreAnnexRepository.findAll(paginacioHelper.toSpringDataPageable(paginacioParams)),
				
				
				registreAnnexRepository.findByFiltrePaginat(
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
	public String guardarComADefinitiu(			
			Long annexId) {

		
		logger.debug("Guardar com a definitiu l'annex " + annexId);		
		RegistreAnnexEntity registreAnnex = registreAnnexRepository.findById(annexId).get();
		
		// si el registre està tancat ja no cal continuar
		ArxiuDetallDto arxiuDetall = registreService.getArxiuDetall(registreAnnex.getRegistre().getId());
		if (arxiuDetall.getEniEstat()==ExpedientEstatEnumDto.TANCAT) {
			return "L'expedient està tancat en l'arxiu"; 
		}
		
		// obtenir els detalls d'ARxiu de l'expedient si té uuid
		// si està tancat a l'arxiu no continuar
		
		// obtenir els detalls de l'Arxiu sense contingut de l'annjex
//		Document document = pluginHelper.arxiuDocumentConsultar(null, null, false, null);
		
		// si el document és definitiu i el registreAnnex no llavors actualitzar la info a bbdd arxiuEstat
		
		// si l'uuid de l'annex a l'Arxiu no coincideix amb l'uuide del registre no continuar, s'ha mogut
//		document.getExpedientMetadades().getIdentificador()

		// si arribem fins aquí podem reintentar guardar l'annex  a l'arxiu i provarà de validar i firmar en cas que sigui necessari
		
		
		// 
//		String arxiuUuid = registreAnnex.getFitxerArxiuUuid();
//		
//		DistribucioRegistreAnnex distribucioAnnex = conversioTipusHelper.convertir(
//				registreAnnex, 
//				DistribucioRegistreAnnex.class);		
//		Document arxiuDocument = getArxiuPlugin().documentDetalls(
//				arxiuUuid,
//				null,
//				false);
		
//		arxiuDocument.getExpedientMetadades().getEstat()==es.caib.plugins.arxiu.api.ExpedientEstat.TANCAT		
		
		
		
		registreAnnex.setArxiuEstat(AnnexEstat.DEFINITIU);	
		registreAnnexRepository.save(registreAnnex);
		return "";
	}
	
	@Transactional(readOnly = false)
	@Override
	public void guardarComADefinitiuMultiple(			
			List<Long> ids) {
		logger.debug("Guardar com a definitiu els annexos " + ids.toString());		
		List<RegistreAnnexEntity> registreAnnexList = registreAnnexRepository.findAllById(ids);
		for (RegistreAnnexEntity registreAnnex: registreAnnexList) {
			registreAnnex.setArxiuEstat(AnnexEstat.DEFINITIU);			
			registreAnnexRepository.save(registreAnnex);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(AnnexosServiceImpl.class);
}
