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
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.helper.HistoricHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
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
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
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

	/** Regorna la llista d'unitats organitzatives segons el filtre. Si hi ha 
	 * unitats escollides llavors retorna el llistat. Si hi ha una unitat superior
	 * llavors retorna la llista d'identificadors de les unitats amb bústia.
	 * Altrament retorna una llista buida.
	 * 
	 * @param entitatId
	 * @param unitatIdFiltre Filtre directe per llistat
	 * @param codiUnitatSuperior Codi de la unitat superior per retornar les UO descendents.
	 * 
	 * @return
	 */
	private List<Long> getUnitatsIds(
			Long entitatId, 
			String codiUnitatSuperior, 
			List<Long> unitatIdFiltre) {
		Set<Long> unitatsIds = new HashSet<Long>();
		if (unitatIdFiltre != null && !unitatIdFiltre.isEmpty()) {
			// Llista d'UOs directa.
			unitatsIds.addAll(unitatIdFiltre);			
		} else if (codiUnitatSuperior != null) {
			// Si es filtra per unitat superior llavors es passa la llista d'identificadors possibles de l'arbre.
			EntitatEntity entitat = entitatRepository.findOne(entitatId);
			// Consulta les possibles unitats descendents
			List<String> codisUnitatsDescendants = 
					unitatOrganitzativaHelper.getCodisOfUnitatsDescendants(entitat, codiUnitatSuperior); 
			codisUnitatsDescendants.add(codiUnitatSuperior);			
			// Consulta les unitats amb bústia amb codi entre els descendents
			List<String> codisFiltre;
			int iCodi = 0;
			do {
				codisFiltre = new ArrayList<String>();
				// Consulta de 1000 en 1000 per evitar errors
				while (iCodi < codisUnitatsDescendants.size() && codisFiltre.size() < 1000 ) {
					codisFiltre.add(codisUnitatsDescendants.get(iCodi++));
				}
				unitatsIds.addAll(
						unitatOrganitzativaRepository.findUnitatsIdsAmbBustiaPerCodis(
								entitat,
								codisFiltre));
			} while (iCodi < codisUnitatsDescendants.size());			
		}
		return new ArrayList<Long>(unitatsIds);
	}

	@Transactional
	@Override
	public void calcularDadesHistoriques(Date data) {
		historicHelper.calcularDades(data);		
	}

}
