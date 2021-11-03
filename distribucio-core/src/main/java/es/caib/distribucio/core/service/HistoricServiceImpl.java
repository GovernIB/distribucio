package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.historic.HistoricDadesDto;
import es.caib.distribucio.core.api.dto.historic.HistoricFiltreDto;
import es.caib.distribucio.core.api.service.HistoricService;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.HistoricHelper;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;

/**
 * Implementació dels mètodes per consultar les dades estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class HistoricServiceImpl implements HistoricService {

	@Resource
	HistoricHelper historicHelper;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

	
	@Transactional(readOnly = true)
	@Override
	public HistoricDadesDto getDadesHistoriques(
			Long entitatId,
			HistoricFiltreDto filtre) {

		List<Long> unitatsIds = this.getUnitatsIds(
									entitatId,
									filtre.getCodiUnitatSuperior(),
									filtre.getUnitatIdFiltre());
		
		return historicHelper.findDades(
				entitatId,
				unitatsIds,
				filtre.getTipusAgrupament(),
				filtre.getDadesMostrar(),
				filtre.getDataIniciQuery(),
				filtre.getDataFiQuery());
	}

	private List<Long> getUnitatsIds(
			Long entitatId, 
			String codiUnitatSuperior, 
			List<Long> unitatIdFiltre) {
		Set<Long> unitatsIds = new HashSet<Long>();
		if (unitatIdFiltre != null && !unitatIdFiltre.isEmpty()) {
			unitatsIds.addAll(unitatIdFiltre);			
		} else if (codiUnitatSuperior != null) {	
			List<UnitatOrganitzativaEntity> unitatsSuperiors = 
					unitatOrganitzativaRepository.findUnitatsSuperiors(entitatId, true, "");
			for (UnitatOrganitzativaEntity us : unitatsSuperiors) {
				unitatsIds.add(us.getId());
			}
		}
		return new ArrayList<Long>(unitatsIds);
	}

	@Transactional
	@Override
	public void calcularDadesHistoriques(Date data) {
		historicHelper.calcularDades(data);		
	}

}
