package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.ServeiHelper;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.service.ServeiService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ServeiEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.ServeiRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.servei.Servei;

/**
 * Implementació del servei de gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class ServeiServiceImpl implements ServeiService{
	
	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ServeiHelper serveiHelper;

	@Override
	@Transactional(readOnly = true) 
	public PaginaDto<ServeiDto> findAmbFiltre(
			Long entitatId, 
			ServeiFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.trace("Cercant els serveis segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		
		PaginaDto<ServeiDto> llistaServeis = null;
        EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);

		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodiDir3EntitatAndId(entitat.getCodiDir3(), filtre.getUnitatOrganitzativa());
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("codiServei", new String[]{"codi"});
		llistaServeis = paginacioHelper.toPaginaDto(
				serveiRepository.findAmbFiltrePaginat(
						entitatId, 
						unitatOrganitzativa == null, 
						unitatOrganitzativa != null ? unitatOrganitzativa : null, 
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() != null ? filtre.getCodi() : "", 
						filtre.getNom() == null || filtre.getNom().isEmpty(), 
						filtre.getNom() != null ? filtre.getNom() : "", 
						filtre.getCodiSia() == null || filtre.getCodiSia().isEmpty(), 
						filtre.getCodiSia() != null ? filtre.getCodiSia() : "", 
						filtre.getEstat() == null, 
						filtre.getEstat(),
                        filtre.isNomesComu(),
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio)), 
				ServeiDto.class);

		
		return llistaServeis;
	}

    @Override
    @Transactional
    public ServeiDto findAndUpdateServei(Long entitatId, String serveiCodi) throws Exception {
        ServeiEntity serveiEntity = serveiHelper.findAndUpdateServei(entitatId, serveiCodi);
		return conversioTipusHelper.convertir(
				serveiEntity, 
				ServeiDto.class);
    }

	/** Mètode per trobar i actualitzar els serveis. Es pot fer manualment o des de la tasca
	 * programada.
	 */
	public void findAndUpdateServeis(Long entitatId) throws Exception {
		serveiHelper.findAndUpdateServeis(entitatId);
	}

	@Transactional
	private void updateServeis(
			List<Servei> serveis,
			Long entitatId,
			Long unitatOrganitzativaId) {
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findById(unitatOrganitzativaId).orElse(null);
		for (Servei servei: serveis) {
			if (servei.getCodigo() != null && !servei.getCodigo().isEmpty()) {
				ServeiEntity serveiEntity = serveiRepository.findByCodi(
						entitatId,
						servei.getCodigo());
				EntitatEntity entitat = entitatRepository.findById(entitatId).orElse(null);
				if (serveiEntity == null) {
					serveiEntity = ServeiEntity.getBuilder(
							servei.getCodigo(),
							servei.getNombre(),
							servei.getCodigoSIA(),
							ServeiEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat,
                            servei.isComun()).built();
					serveiRepository.save(serveiEntity);
				} else {
					serveiEntity.update(
							servei.getCodigo(),
							servei.getNombre(),
							servei.getCodigoSIA(),
							ServeiEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat,
                            servei.isComun());
					serveiRepository.save(serveiEntity);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ServeiDto findByCodiSia(Long entitatId, String codiSia) {
		return conversioTipusHelper.convertir(
				serveiRepository.findByCodiSia(
						entitatId, 
						codiSia), 
				ServeiDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServeiDto> findByNomOrCodiSia(Long entitatId, String search) {
		if (search == null || search.isEmpty()) {
			return new ArrayList<>();
		}			
		return conversioTipusHelper.convertirList(
				serveiRepository.findByNomOrCodiSia(
						entitatId, 
						search != null ? search : ""), 
				ServeiDto.class);
	}

	@Override
	public boolean isUpdatingServeis(Long entitatId) {
		UpdateProgressDto progres = serveiHelper.serveisActualitzacio.get(entitatId);
		return progres != null 
				&& progres.getEstat() != UpdateProgressDto.Estat.FINALITZAT
				&& progres.getEstat() != UpdateProgressDto.Estat.ERROR;
	}
	
	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return serveiHelper.serveisActualitzacio.get(entitatId);
	}

	private static final Logger logger = LoggerFactory.getLogger(ServeiServiceImpl.class);

}
