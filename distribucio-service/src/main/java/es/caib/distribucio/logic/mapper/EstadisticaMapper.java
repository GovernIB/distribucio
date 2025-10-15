package es.caib.distribucio.logic.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import es.caib.comanda.ms.estadistica.model.Dimensio;
import es.caib.comanda.ms.estadistica.model.Fet;
import es.caib.comanda.ms.estadistica.model.RegistreEstadistic;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.comanda.ms.estadistica.model.Temps;
import es.caib.distribucio.logic.helper.HibernateHelper;
import es.caib.distribucio.logic.intf.dto.estadistic.DimEnum;
import es.caib.distribucio.logic.intf.dto.estadistic.DimensioDistribucio;
import es.caib.distribucio.logic.intf.dto.estadistic.FetDistribucio;
import es.caib.distribucio.logic.intf.dto.estadistic.FetEnum;
import es.caib.distribucio.persist.entity.HistoricAnotacioEntity;
import es.caib.distribucio.persist.entity.HistoricBustiaEntity;
import es.caib.distribucio.persist.entity.HistoricEstatEntity;

/**
 * Mapper per convertir històric anotacions a registres estadístics.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EstadisticaMapper {

    public RegistresEstadistics convertirRegistresEstadistics(
            List<HistoricAnotacioEntity> historicAnotacions,
            List<HistoricEstatEntity> historicPerEstat,
            List<HistoricBustiaEntity> historicBusties,
            Date data) {

        List<RegistreEstadistic> registresEstadistics = Stream.of(
                    safeStream(historicAnotacions).map(this::registreFromAnotacio),
                    safeStream(historicPerEstat).map(this::registreFromEstat),
                    safeStream(historicBusties).map(this::registreFromBustia)
                )
                .flatMap(s -> s)
                .collect(Collectors.toList());
   
//        // Agrupar per dimensions i sumar els fets
//        Map<List<Dimensio>, List<RegistreEstadistic>> groupedByDim = registresEstadistics.stream()
//                .collect(Collectors.groupingBy(RegistreEstadistic::getDimensions));
        
        // Dimensio no té el mètode equals i hashcode per agrupar les dimensions iguals
        Map<String, List<RegistreEstadistic>> groupedByDim = registresEstadistics
        		.stream()
        		.collect(Collectors.groupingBy(r -> buildKey(r.getDimensions())));

        List<RegistreEstadistic> mergedRegistresEstadistics = groupedByDim.entrySet().stream()
                .map(entry -> {
                	// Agafam les dimensions del primer registre (totes son iguals dins d'un grup de registres)
        	        List<Dimensio> dimensions = entry.getValue().get(0).getDimensions();
                    List<RegistreEstadistic> fets = entry.getValue();
                    
                    Map<FetEnum, Double> sumFets = new EnumMap<>(FetEnum.class);
                    for (FetEnum fet : FetEnum.values()) {
                        Double sum = fets.stream()
                                .flatMap(r -> r.getFets().stream())
                                .filter(f -> f.getCodi().equals(fet.name()))
                                .map(Fet::getValor)
                                .filter(Objects::nonNull)
                                .reduce(Double::sum) // Suma els valors existents
                                .orElse(null);       // Si no hi ha valors -> null

                        sumFets.put(fet, sum);
                    }
                    
                    // Reconstruir la llista de fets combinats
                    List<Fet> combinedFets = sumFets.entrySet().stream()
                            .map(e -> new FetDistribucio(e.getKey(), e.getValue()))
                            .collect(Collectors.toList());

                    // Reconstruir el registre estadístic amb els fets ja combinats
                    return RegistreEstadistic.builder()
                            .dimensions(dimensions)
                            .fets(combinedFets)
                            .build();
                })
                .collect(Collectors.toList());

        return RegistresEstadistics.builder()
                .temps(Temps.builder().data(data).build())
                .fets(mergedRegistresEstadistics)
                .build();
    }
    
    // Generar una clau única de les dimensions
    private String buildKey(List<Dimensio> dimensions) {
        return dimensions.stream()
                .sorted(Comparator.comparing(Dimensio::getCodi))
                .map(d -> d.getCodi() + "=" + (d.getValor() == null ? "" : d.getValor()))
                .collect(Collectors.joining("|"));
        // Resultat: ENT=CAIB|UNT=A04027007|TIP=DIARI
    }


    private RegistreEstadistic registreFromAnotacio(HistoricAnotacioEntity anotacio) {
    	if (HibernateHelper.isProxy(anotacio)) anotacio = HibernateHelper.deproxy(anotacio);
    	
        List<Dimensio> dimensions = List.of(
                new DimensioDistribucio(DimEnum.ENT, anotacio.getEntitat().getCodi()),
                new DimensioDistribucio(DimEnum.UNT, anotacio.getUnitat() != null ? anotacio.getUnitat().getCodi() : null),
                new DimensioDistribucio(DimEnum.TIP, anotacio.getTipus()),
                new DimensioDistribucio(DimEnum.BST, (String) null)
        );

        Map<FetEnum, Function<HistoricAnotacioEntity, Long>> fetsValors = Map.of(
                FetEnum.ANT_UO_NOV, e -> e.getAnotacions(),
                FetEnum.ANT_UO_TOT, e -> e.getAnotacionsTotal(),
                FetEnum.ANT_UO_RNV, e -> e.getReenviaments(),
                FetEnum.ANT_UO_EML, e -> e.getEmails(),
                FetEnum.ANT_UO_JST, e -> e.getJustificants(),
                FetEnum.ANT_UO_ANX, e -> e.getAnnexos(),
                FetEnum.ANT_UO_BST, e -> e.getBusties(),
                FetEnum.ANT_UO_USR, e -> e.getUsuaris()
        );
        
        return registreEstadisticFromEntity(anotacio, dimensions, fetsValors);
    }

    private RegistreEstadistic registreFromEstat(HistoricEstatEntity estat) {
    	if (HibernateHelper.isProxy(estat)) estat = HibernateHelper.deproxy(estat);
    	
        List<Dimensio> dimensions = List.of(
                new DimensioDistribucio(DimEnum.ENT, estat.getEntitat().getCodi()),
                new DimensioDistribucio(DimEnum.UNT, estat.getUnitat() != null ? estat.getUnitat().getCodi() : null),
                new DimensioDistribucio(DimEnum.TIP, estat.getTipus()),
                new DimensioDistribucio(DimEnum.BST, (String) null)
        );

        Map<FetEnum, Function<HistoricEstatEntity, Long>> fetsValors = Map.of();
        
		switch (estat.getEstat()) {
			case ARXIU_PENDENT:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_ARX_PND_OKY, HistoricEstatEntity::getCorrecte),
						Map.entry(FetEnum.ANT_ARX_PND_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
						Map.entry(FetEnum.ANT_ARX_PND_ERR, HistoricEstatEntity::getError),
						Map.entry(FetEnum.ANT_ARX_PND_ERR_TOT, HistoricEstatEntity::getErrorTotal),
						Map.entry(FetEnum.ANT_ARX_PND_EST_TOT, HistoricEstatEntity::getTotal));
				break;
	
			case BUSTIA_PENDENT:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BST_PND_OKY, HistoricEstatEntity::getCorrecte),
		                Map.entry(FetEnum.ANT_BST_PND_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
		                Map.entry(FetEnum.ANT_BST_PND_ERR, HistoricEstatEntity::getError),
		                Map.entry(FetEnum.ANT_BST_PND_ERR_TOT, HistoricEstatEntity::getErrorTotal),
		                Map.entry(FetEnum.ANT_BST_PND_EST_TOT, HistoricEstatEntity::getTotal));
				break;
			case BUSTIA_PROCESSADA:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BST_PRC_OKY, HistoricEstatEntity::getCorrecte),
		                Map.entry(FetEnum.ANT_BST_PRC_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
		                Map.entry(FetEnum.ANT_BST_PRC_ERR, HistoricEstatEntity::getError),
		                Map.entry(FetEnum.ANT_BST_PRC_ERR_TOT, HistoricEstatEntity::getErrorTotal),
		                Map.entry(FetEnum.ANT_BST_PRC_EST_TOT, HistoricEstatEntity::getTotal));
				break;
			case BACK_PENDENT:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BCK_PND_OKY, HistoricEstatEntity::getCorrecte),
						Map.entry(FetEnum.ANT_BCK_PND_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
						Map.entry(FetEnum.ANT_BCK_PND_ERR, HistoricEstatEntity::getError),
						Map.entry(FetEnum.ANT_BCK_PND_ERR_TOT, HistoricEstatEntity::getErrorTotal),
						Map.entry(FetEnum.ANT_BCK_PND_EST_TOT, HistoricEstatEntity::getTotal));
				break;
	
			case BACK_REBUDA:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BCK_REB_OKY, HistoricEstatEntity::getCorrecte),
						Map.entry(FetEnum.ANT_BCK_REB_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
						Map.entry(FetEnum.ANT_BCK_REB_ERR, HistoricEstatEntity::getError),
						Map.entry(FetEnum.ANT_BCK_REB_ERR_TOT, HistoricEstatEntity::getErrorTotal),
						Map.entry(FetEnum.ANT_BCK_REB_EST_TOT, HistoricEstatEntity::getTotal));
				break;
	
			case BACK_REBUTJADA:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BCK_RBJ_OKY, HistoricEstatEntity::getCorrecte),
						Map.entry(FetEnum.ANT_BCK_RBJ_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
						Map.entry(FetEnum.ANT_BCK_RBJ_ERR, HistoricEstatEntity::getError),
						Map.entry(FetEnum.ANT_BCK_RBJ_ERR_TOT, HistoricEstatEntity::getErrorTotal),
						Map.entry(FetEnum.ANT_BCK_RBJ_EST_TOT, HistoricEstatEntity::getTotal));
				break;
	
			case BACK_PROCESSADA:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BCK_PRC_OKY, HistoricEstatEntity::getCorrecte),
						Map.entry(FetEnum.ANT_BCK_PRC_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
						Map.entry(FetEnum.ANT_BCK_PRC_ERR, HistoricEstatEntity::getError),
						Map.entry(FetEnum.ANT_BCK_PRC_ERR_TOT, HistoricEstatEntity::getErrorTotal),
						Map.entry(FetEnum.ANT_BCK_PRC_EST_TOT, HistoricEstatEntity::getTotal));
				break;
	
			case BACK_COMUNICADA:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BCK_COM_OKY, HistoricEstatEntity::getCorrecte),
						Map.entry(FetEnum.ANT_BCK_COM_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
						Map.entry(FetEnum.ANT_BCK_COM_ERR, HistoricEstatEntity::getError),
						Map.entry(FetEnum.ANT_BCK_COM_ERR_TOT, HistoricEstatEntity::getErrorTotal),
						Map.entry(FetEnum.ANT_BCK_COM_EST_TOT, HistoricEstatEntity::getTotal));
				break;
	
				
			case BACK_ERROR:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_BCK_ERR_OKY, HistoricEstatEntity::getCorrecte),
		                Map.entry(FetEnum.ANT_BCK_ERR_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
		                Map.entry(FetEnum.ANT_BCK_ERR_ERR, HistoricEstatEntity::getError),
		                Map.entry(FetEnum.ANT_BCK_ERR_ERR_TOT, HistoricEstatEntity::getErrorTotal),
		                Map.entry(FetEnum.ANT_BCK_ERR_EST_TOT, HistoricEstatEntity::getTotal));
				break;
			case REGLA_PENDENT:
				fetsValors = Map.ofEntries(
						Map.entry(FetEnum.ANT_REG_PND_OKY, HistoricEstatEntity::getCorrecte),
						Map.entry(FetEnum.ANT_REG_PND_OKY_TOT, HistoricEstatEntity::getCorrecteTotal),
						Map.entry(FetEnum.ANT_REG_PND_ERR, HistoricEstatEntity::getError),
						Map.entry(FetEnum.ANT_REG_PND_ERR_TOT, HistoricEstatEntity::getErrorTotal),
						Map.entry(FetEnum.ANT_REG_PND_EST_TOT, HistoricEstatEntity::getTotal));
				break;
			default:
				fetsValors = Map.ofEntries(Map.entry(FetEnum.ANT_EST_UNKNOWN, HistoricEstatEntity::getTotal));
				break;
		}
        
        return registreEstadisticFromEntity(estat, dimensions, fetsValors);
    }

    private RegistreEstadistic registreFromBustia(HistoricBustiaEntity bustia) {
    	if (HibernateHelper.isProxy(bustia)) bustia = HibernateHelper.deproxy(bustia);
    	
        List<Dimensio> dimensions = List.of(
                new DimensioDistribucio(DimEnum.ENT, bustia.getEntitat().getCodi()),
                new DimensioDistribucio(DimEnum.UNT, bustia.getUnitat().getCodi()),
                new DimensioDistribucio(DimEnum.TIP, bustia.getTipus()),
                new DimensioDistribucio(DimEnum.BST, bustia.getNom())
        );
        
        Map<FetEnum, Function<HistoricBustiaEntity, Long>> fetsValors = Map.of(
                FetEnum.BST_PRM_USR, e -> e.getUsuarisPermis(),
                FetEnum.BST_PRM_ROL, e -> e.getUsuarisRol(),
                FetEnum.BST_PRM_TOT, e -> e.getUsuaris()
        );
        
        return registreEstadisticFromEntity(bustia, dimensions, fetsValors);
    }
    
    private <T> RegistreEstadistic registreEstadisticFromEntity(
            T entity,
            List<Dimensio> dimensions,
            Map<FetEnum, Function<T, Long>> fetsValors) {
        List<Fet> fets = new ArrayList<>();
        for (FetEnum fet : FetEnum.values()) {
            fets.add(new FetDistribucio(fet, fetsValors.getOrDefault(fet, x -> null).apply(entity)));
        }

        return RegistreEstadistic.builder()
                .dimensions(dimensions)
                .fets(fets)
                .build();
    }
    
    private <T> Stream<T> safeStream(List<T> list) {
        return list == null ? Stream.empty() : list.stream();
    }

}
