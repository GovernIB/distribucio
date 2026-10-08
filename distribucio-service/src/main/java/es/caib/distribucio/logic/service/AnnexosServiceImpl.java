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
import es.caib.distribucio.logic.helper.RegistreHelper;
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.persist.entity.EntitatEntity;
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
		return annexosAdminHelper.guardarComADefinitiu(annexId);
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
		
		List<RegistreAnnexDto> resposta = new ArrayList<RegistreAnnexDto>();
		// Consulta de 1000 en 1000 els que estan en el llistat.
		List<RegistreAnnexEntity> annexos;
		for (int i = 0; i < multipleAnnexosIds.size(); i += 100) {
			 annexos = registreAnnexRepository.findByIdIn(multipleAnnexosIds.subList(i, Math.min(multipleAnnexosIds.size(), i+100)));
			 resposta.addAll(conversioTipusHelper.convertirList(annexos, RegistreAnnexDto.class));
		}
		return resposta;
	}

	@Override
	public List<Integer> findCopiesRegistre(String numero) {
		return registreHelper.findCopiesRegistre(numero);
	}

	private static final Logger logger = LoggerFactory.getLogger(AnnexosServiceImpl.class);
}
