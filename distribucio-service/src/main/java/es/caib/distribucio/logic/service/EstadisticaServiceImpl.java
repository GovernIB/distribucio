package es.caib.distribucio.logic.service;

import static es.caib.comanda.ms.estadistica.model.Format.LONG;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.distribucio.logic.intf.dto.estadistic.DimEnum;
import es.caib.distribucio.logic.intf.dto.estadistic.FetEnum;
import es.caib.distribucio.logic.intf.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.logic.intf.service.EstadisticaService;
import es.caib.distribucio.logic.mapper.EstadisticaMapper;
import es.caib.distribucio.persist.entity.HistoricAnotacioEntity;
import es.caib.distribucio.persist.entity.HistoricBustiaEntity;
import es.caib.distribucio.persist.entity.HistoricEstatEntity;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.HistoricAnotacioRepository;
import es.caib.distribucio.persist.repository.HistoricBustiaRepository;
import es.caib.distribucio.persist.repository.HistoricEstatRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;

/**
 * Implementació dels mètodes per recuperar estadístiques per Comanda.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EstadisticaServiceImpl implements EstadisticaService {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private UnitatOrganitzativaRepository organitzativaRepository;
	@Autowired
	private BustiaRepository bustiaRepository;
	@Autowired
	private HistoricAnotacioRepository historicAnotacioRepository;
	@Autowired
	private HistoricEstatRepository historicEstatRepository;
	@Autowired
	private HistoricBustiaRepository historicBustiaRepository;
	
	@Autowired
	private EstadisticaMapper estadisticaMapper;

	@Override
	public List<DimensioDesc> getDimensions() {
		List<String> entitatCodis = entitatRepository.findDistinctCodiOrderByCodiAsc();
		List<String> unitatCodis = organitzativaRepository.findDistinctCodiOrderByCodiAsc();
		List<String> bustiaNoms = bustiaRepository.findDistinctNomOrderByNomAsc();
		List<String> tipus = Arrays.stream(HistoricTipusEnumDto.values()).map(Enum::name).sorted()
				.collect(Collectors.toList());

		return List.of(
				DimensioDesc.builder().codi(DimEnum.ENT.name()).nom(DimEnum.ENT.getNom()).descripcio(DimEnum.ENT.getDescripcio()).valors(entitatCodis).build(),
				DimensioDesc.builder().codi(DimEnum.UNT.name()).nom(DimEnum.UNT.getNom()).descripcio(DimEnum.UNT.getDescripcio()).valors(unitatCodis).build(),
				DimensioDesc.builder().codi(DimEnum.BST.name()).nom(DimEnum.BST.getNom()).descripcio(DimEnum.BST.getDescripcio()).valors(bustiaNoms).build(),
				DimensioDesc.builder().codi(DimEnum.TIP.name()).nom(DimEnum.TIP.getNom()).descripcio(DimEnum.TIP.getDescripcio()).valors(tipus).build());
	}

	@Override
	public List<IndicadorDesc> getIndicadors() {
		return Arrays.stream(FetEnum.values())
			    .map(f -> IndicadorDesc.builder()
			        .codi(f.name())
			        .nom(f.getNom())
			        .descripcio(f.getDescripcio())
			        .format(LONG) // De moment sempre long
			        .build())
			    .collect(Collectors.toList());
	}

	@Override
	public RegistresEstadistics consultaUltimesEstadistiques() {
		LocalDate data = LocalDate.now().minusDays(1);
		return consultaEstadistiques(data);
	}

	@Override
	public RegistresEstadistics consultaEstadistiques(LocalDate localData) {
		Date data = Date.from(localData.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		
		// Històric anotacions
		List<HistoricAnotacioEntity> historicAnotacions = historicAnotacioRepository.findByData(data);

		// Històric anotacions per estat
		List<HistoricEstatEntity> historicPerEstat = historicEstatRepository.findByData(data);

		// Històric permisos per bústia
		List<HistoricBustiaEntity> historicBusties = historicBustiaRepository.findByData(data);

		return estadisticaMapper.convertirRegistresEstadistics(
				historicAnotacions, 
				historicPerEstat,
				historicBusties,
				data);
		
	}

	@Override
	public List<RegistresEstadistics> consultaEstadistiques(LocalDate startLocalData, LocalDate endLocalData) {
		List<RegistresEstadistics> resultat = new ArrayList<>();
		
		LocalDate currentDate = startLocalData;
        while (!currentDate.isAfter(endLocalData)) {
        	resultat.add(consultaEstadistiques(currentDate));
            currentDate = currentDate.plusDays(1);
        }
        
		return resultat;
	}

}
