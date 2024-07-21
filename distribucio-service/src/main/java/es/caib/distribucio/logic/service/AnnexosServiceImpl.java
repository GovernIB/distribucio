/**
 * 
 */
package es.caib.distribucio.logic.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.AnnexosAdminHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper.Converter;
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.service.AnnexosService;
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
	
	@Transactional(readOnly = false)
	@Override
	public void guardarComADefinitiu(			
			Long id) {
		logger.debug("Guardar com a definitiu l'annex " + id);		
		RegistreAnnexEntity registreAnnex = registreAnnexRepository.findById(id).get();
		registreAnnex.setArxiuEstat(AnnexEstat.DEFINITIU);	
		registreAnnexRepository.save(registreAnnex);
	}

	private static final Logger logger = LoggerFactory.getLogger(AnnexosServiceImpl.class);
}
