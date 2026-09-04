package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.ProcedimentHelper;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto.Estat;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ProcedimentEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.ProcedimentRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.procediment.Procediment;

/**
 * Implementació del procediment de gestió de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class ProcedimentServiceImpl implements ProcedimentService{
	
	@Autowired
	private ProcedimentRepository procedimentRepository;
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
	private ProcedimentHelper procedimentHelper;
	

	@Override
	@Transactional(readOnly = true) 
	public PaginaDto<ProcedimentDto> findAmbFiltre(
			Long entitatId, 
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.trace("Cercant els procediments segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		
		PaginaDto<ProcedimentDto> llistaProcediments = null;
        EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);

		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findByCodiDir3EntitatAndId(entitat.getCodiDir3(), filtre.getUnitatOrganitzativa());
		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
		mapeigPropietatsOrdenacio.put("codiProcediment", new String[]{"codi"});
		llistaProcediments = paginacioHelper.toPaginaDto(
				procedimentRepository.findAmbFiltrePaginat(
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
				ProcedimentDto.class);

		
		return llistaProcediments;
	}

    @Override
    @Transactional
    public ProcedimentDto findAndUpdateProcediment(Long entitatId, String procedimentCodi) throws Exception {
        return procedimentHelper.findAndUpdateProcediment(entitatId, procedimentCodi);
    }

	/** Mètode per trobar i actualitzar els procediments. Es pot fer manualment o des de la tasca
	 * programada.
	 */
	@Override
	@Transactional	
	public void findAndUpdateProcediments(Long entitatId) throws Exception {
		procedimentHelper.findAndUpdateProcediments(entitatId);
	}

	@Transactional
	private void updateProcediments(
			List<Procediment> procediments,
			Long entitatId,
			Long unitatOrganitzativaId) {
		UnitatOrganitzativaEntity unitatOrganitzativa = unitatOrganitzativaRepository.findById(unitatOrganitzativaId).orElse(null);
		for (Procediment procediment: procediments) {
			if (procediment.getCodigo() != null && !procediment.getCodigo().isEmpty()) {
				ProcedimentEntity procedimentEntity = procedimentRepository.findByCodi(
						entitatId,
						procediment.getCodigo());
				EntitatEntity entitat = entitatRepository.findById(entitatId).orElse(null);
				if (procedimentEntity == null) {
					procedimentEntity = ProcedimentEntity.getBuilder(
							procediment.getCodigo(),
							procediment.getNombre(),
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat,
                            procediment.isComun()).built();
					procedimentRepository.save(procedimentEntity);
				} else {
					procedimentEntity.update(
							procediment.getCodigo(),
							procediment.getNombre(),
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,
							unitatOrganitzativa, 
							entitat,
                            procediment.isComun());
					procedimentRepository.save(procedimentEntity);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ProcedimentDto findByCodiSia(Long entitatId, String codiSia) {
		return conversioTipusHelper.convertir(
				procedimentRepository.findByCodiSia(
						entitatId, 
						codiSia), 
				ProcedimentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> findByNomOrCodiSia(Long entitatId, String search) {
		if (search == null || search.isEmpty()) {
			return new ArrayList<>();
		}			
		return conversioTipusHelper.convertirList(
				procedimentRepository.findByNomOrCodiSia(
						entitatId, 
						search != null ? search : ""), 
				ProcedimentDto.class);
	}

	public boolean isUpdatingProcediments(Long entitatId) {
		return procedimentHelper.isUpdatingProcediments(entitatId);
	}

	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return procedimentHelper.progressosActualitzacio.get(entitatId);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentServiceImpl.class);

}
