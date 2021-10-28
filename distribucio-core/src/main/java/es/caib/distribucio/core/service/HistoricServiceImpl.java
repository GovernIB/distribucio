package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.historic.HistoricDadesDto;
import es.caib.distribucio.core.api.dto.historic.HistoricFiltreDto;
import es.caib.distribucio.core.api.service.HistoricService;
import es.caib.distribucio.core.helper.HistoricHelper;

/**
 * Implementació dels mètodes per consultar les dades estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class HistoricServiceImpl implements HistoricService {

	@Resource
	HistoricHelper historicHelper;
	
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

	private List<Long> getUnitatsIds(Long entitatId, List<Long> codiUnitatSuperior, List<Long> unitatIdFiltre) {
		List<Long> unitatsIds = new ArrayList<Long>();
		if (unitatIdFiltre != null && !unitatIdFiltre.isEmpty())
			unitatsIds.addAll(unitatsIds);
		if (codiUnitatSuperior != null) {
			for (Long unitatSuperiorId : codiUnitatSuperior) {
				//TODO: consultar arbre d'unitats
			}
		}
		return unitatsIds;
	}

}
