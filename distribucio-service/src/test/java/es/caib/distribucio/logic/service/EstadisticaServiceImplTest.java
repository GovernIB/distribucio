package es.caib.distribucio.logic.service;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.distribucio.logic.intf.dto.estadistic.DimEnum;
import es.caib.distribucio.logic.intf.dto.estadistic.FetEnum;
import es.caib.distribucio.logic.intf.dto.historic.HistoricTipusEnumDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static es.caib.comanda.model.server.monitoring.Format.LONG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de EstadisticaServiceImpl")
class EstadisticaServiceImplTest {

    @Mock private EntitatRepository entitatRepository;
    @Mock private UnitatOrganitzativaRepository organitzativaRepository;
    @Mock private BustiaRepository bustiaRepository;
    @Mock private HistoricAnotacioRepository historicAnotacioRepository;
    @Mock private HistoricEstatRepository historicEstatRepository;
    @Mock private HistoricBustiaRepository historicBustiaRepository;
    @Mock private EstadisticaMapper estadisticaMapper;

    @InjectMocks
    private EstadisticaServiceImpl estadisticaService;

    private LocalDate dataProva;
    private Date dataProvaConvertida;

    @BeforeEach
    void setUp() {
        dataProva = LocalDate.of(2023, 10, 25);
        dataProvaConvertida = Date.from(dataProva.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    // =========================================================================
    // 1. GET DIMENSIONS
    // =========================================================================
    @Nested
    @DisplayName("Mètode getDimensions")
    class GetDimensionsTests {

        @Test
        @DisplayName("getDimensions: Quan els repositoris retornen dades, llavors retorna les 4 dimensions amb valors ordenats")
        void getDimensions_QuanHiHaDades_LlavorsRetorna4DimensionsAmbValors() {
            // Arrange
            when(entitatRepository.findDistinctCodiOrderByCodiAsc()).thenReturn(Arrays.asList("ENT2", "ENT1"));
            when(organitzativaRepository.findDistinctCodiOrderByCodiAsc()).thenReturn(Collections.singletonList("UNT1"));
            when(bustiaRepository.findDistinctNomOrderByNomAsc()).thenReturn(Collections.emptyList());

            // Act
            List<DimensioDesc> resultat = estadisticaService.getDimensions();

            // Assert
            assertNotNull(resultat);
            assertEquals(4, resultat.size());

            // Verificar Dimensió ENT
            DimensioDesc dimEnt = resultat.stream().filter(d -> d.getCodi().equals(DimEnum.ENT.name())).findFirst().orElseThrow();
            assertEquals(DimEnum.ENT.getNom(), dimEnt.getNom());
            assertEquals(DimEnum.ENT.getDescripcio(), dimEnt.getDescripcio());
            assertEquals(Arrays.asList("ENT2", "ENT1"), dimEnt.getValors()); // El repositori ja ho retorna ordenat

            // Verificar Dimensió TIP (Enum ordenat)
            DimensioDesc dimTip = resultat.stream().filter(d -> d.getCodi().equals(DimEnum.TIP.name())).findFirst().orElseThrow();
            List<String> tipusEsperats = Arrays.stream(HistoricTipusEnumDto.values())
                    .map(Enum::name).sorted().collect(Collectors.toList());
            assertEquals(tipusEsperats, dimTip.getValors());

            verify(entitatRepository, times(1)).findDistinctCodiOrderByCodiAsc();
            verify(organitzativaRepository, times(1)).findDistinctCodiOrderByCodiAsc();
            verify(bustiaRepository, times(1)).findDistinctNomOrderByNomAsc();
        }

        @Test
        @DisplayName("getDimensions: Quan els repositoris retornen llistes buides, llavors retorna les 4 dimensions amb valors buits")
        void getDimensions_QuanLlistesBuides_LlavorsRetornaDimensionsBuides() {
            // Arrange
            when(entitatRepository.findDistinctCodiOrderByCodiAsc()).thenReturn(Collections.emptyList());
            when(organitzativaRepository.findDistinctCodiOrderByCodiAsc()).thenReturn(Collections.emptyList());
            when(bustiaRepository.findDistinctNomOrderByNomAsc()).thenReturn(Collections.emptyList());

            // Act
            List<DimensioDesc> resultat = estadisticaService.getDimensions();

            // Assert
            assertEquals(4, resultat.size());
            assertTrue(resultat.stream().allMatch(d -> d.getValors() != null)); // Assegurar que no són null, sinó buides o amb valors d'enum
        }
    }

    // =========================================================================
    // 2. GET INDICADORS
    // =========================================================================
    @Nested
    @DisplayName("Mètode getIndicadors")
    class GetIndicadorsTests {

        @Test
        @DisplayName("getIndicadors: Quan s'executa, llavors mapeja tots els valors de FetEnum amb format LONG")
        void getIndicadors_QuanSExecuta_LlavorsMapejaTotsElsFetsAmbFormatLong() {
            // Act
            List<IndicadorDesc> resultat = estadisticaService.getIndicadors();

            // Assert
            assertNotNull(resultat);
            assertEquals(FetEnum.values().length, resultat.size());

            // Verificar que tots tenen el format LONG i les dades correctes de l'enum
            for (FetEnum fet : FetEnum.values()) {
                IndicadorDesc desc = resultat.stream()
                        .filter(i -> i.getCodi().equals(fet.name()))
                        .findFirst().orElseThrow(() -> new AssertionError("No s'ha trobat l'indicador per " + fet.name()));

                assertEquals(fet.getNom(), desc.getNom());
                assertEquals(fet.getDescripcio(), desc.getDescripcio());
                assertEquals(LONG, desc.getFormat());
            }
        }
    }

    // =========================================================================
    // 3. CONSULTA ESTADÍSTIQUES (Data única)
    // =========================================================================
    @Nested
    @DisplayName("Mètode consultaEstadistiques (LocalDate)")
    class ConsultaEstadistiquesUnicTests {

        @Test
        @DisplayName("consultaEstadistiques: Quan es passa una data, llavors converteix a Date i consulta els 3 històrics i el mapper")
        void consultaEstadistiques_QuanDataValida_LlavorsConsultaRepositorisIMapper() {
            // Arrange
            List<HistoricAnotacioEntity> anotacions = Collections.singletonList(mock(HistoricAnotacioEntity.class));
            List<HistoricEstatEntity> estats = Collections.singletonList(mock(HistoricEstatEntity.class));
            List<HistoricBustiaEntity> busties = Collections.singletonList(mock(HistoricBustiaEntity.class));
            RegistresEstadistics resultatEsperat = mock(RegistresEstadistics.class);

            when(historicAnotacioRepository.findByDataAndUnitatNotNull(any(Date.class))).thenReturn(anotacions);
            when(historicEstatRepository.findByDataAndUnitatNotNull(any(Date.class))).thenReturn(estats);
            when(historicBustiaRepository.findByData(any(Date.class))).thenReturn(busties);
            when(estadisticaMapper.convertirRegistresEstadistics(eq(anotacions), eq(estats), eq(busties), any(Date.class)))
                    .thenReturn(resultatEsperat);

            // Act
            RegistresEstadistics resultat = estadisticaService.consultaEstadistiques(dataProva);

            // Assert
            assertSame(resultatEsperat, resultat);

            // Capturar les dates passades als repositoris per verificar la conversió correcta
            ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);

            verify(historicAnotacioRepository, times(1)).findByDataAndUnitatNotNull(dateCaptor.capture());
            verify(historicEstatRepository, times(1)).findByDataAndUnitatNotNull(dateCaptor.capture());
            verify(historicBustiaRepository, times(1)).findByData(dateCaptor.capture());

            // Totes les crides han de rebre exactament la mateixa data convertida
            List<Date> datesCapturades = dateCaptor.getAllValues();
            assertEquals(3, datesCapturades.size());
            assertTrue(datesCapturades.stream().allMatch(d -> d.equals(dataProvaConvertida)));

            verify(estadisticaMapper, times(1)).convertirRegistresEstadistics(anotacions, estats, busties, dataProvaConvertida);
        }

        @Test
        @DisplayName("consultaEstadistiques: Quan els repositoris retornen llistes buides, llavors el mapper rep llistes buides")
        void consultaEstadistiques_QuanDadesBuides_LlavorsMapperRepBuit() {
            // Arrange
            when(historicAnotacioRepository.findByDataAndUnitatNotNull(any())).thenReturn(Collections.emptyList());
            when(historicEstatRepository.findByDataAndUnitatNotNull(any())).thenReturn(Collections.emptyList());
            when(historicBustiaRepository.findByData(any())).thenReturn(Collections.emptyList());
            RegistresEstadistics resultatEsperat = mock(RegistresEstadistics.class);
            when(estadisticaMapper.convertirRegistresEstadistics(anyList(), anyList(), anyList(), any())).thenReturn(resultatEsperat);

            // Act
            RegistresEstadistics resultat = estadisticaService.consultaEstadistiques(dataProva);

            // Assert
            assertSame(resultatEsperat, resultat);
            verify(estadisticaMapper, times(1)).convertirRegistresEstadistics(
                    eq(Collections.emptyList()), eq(Collections.emptyList()), eq(Collections.emptyList()), any(Date.class));
        }
    }

    // =========================================================================
    // 4. CONSULTA ÚLTIMES ESTADÍSTIQUES
    // =========================================================================
    @Nested
    @DisplayName("Mètode consultaUltimesEstadistiques")
    class ConsultaUltimesEstadistiquesTests {

        @Test
        @DisplayName("consultaUltimesEstadistiques: Quan s'executa, llavors crida a consultaEstadistiques amb la data d'ahir")
        void consultaUltimesEstadistiques_QuanSExecuta_LlavorsCridaAmbDataDahir() {
            // Arrange
            LocalDate avuiMock = LocalDate.of(2023, 10, 26);
            LocalDate ahirEsperat = LocalDate.of(2023, 10, 25);
            RegistresEstadistics resultatMock = mock(RegistresEstadistics.class);

            // Mockejar LocalDate.now() per tenir un comportament determinista
            try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
                mockedLocalDate.when(LocalDate::now).thenReturn(avuiMock);

                // Espiar el mètode real o mockejar la crida interna. Com que és un mètode de la mateixa classe,
                // el millor és verificar que el mètode públic retorna el que toca, o fer un spy.
                // Aquí, com que consultaEstadistiques és públic, podem verificar el resultat final,
                // però per ser estrictes amb la cobertura, deixem que executi la lògica real.
                when(historicAnotacioRepository.findByDataAndUnitatNotNull(any())).thenReturn(Collections.emptyList());
                when(historicEstatRepository.findByDataAndUnitatNotNull(any())).thenReturn(Collections.emptyList());
                when(historicBustiaRepository.findByData(any())).thenReturn(Collections.emptyList());
                when(estadisticaMapper.convertirRegistresEstadistics(any(), any(), any(), any())).thenReturn(resultatMock);

                // Act
                RegistresEstadistics resultat = estadisticaService.consultaUltimesEstadistiques();

                // Assert
                assertSame(resultatMock, resultat);

                // Verificar que els repositoris van ser cridats amb la data d'ahir
                Date dataAhirEsperada = Date.from(ahirEsperat.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                verify(historicAnotacioRepository, times(1)).findByDataAndUnitatNotNull(eq(dataAhirEsperada));
            }
        }
    }
}