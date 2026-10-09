package es.caib.distribucio.logic.service;

import es.caib.distribucio.logic.helper.HistoricHelper;
import es.caib.distribucio.logic.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.logic.intf.dto.historic.HistoricDadesDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricFiltreDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de HistoricServiceImpl")
class HistoricServiceImplTest {

    @Mock private HistoricHelper historicHelper;
    @Mock private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
    @Mock private EntitatRepository entitatRepository;
    @Mock private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

    @InjectMocks
    private HistoricServiceImpl historicService;

    private EntitatEntity entitatMock;
    private HistoricDadesDto dadesMock;

    @BeforeEach
    void setUp() {
        entitatMock = mock(EntitatEntity.class);
        dadesMock = mock(HistoricDadesDto.class);

        // Comportament per defecte per evitar NPEs en crides no relacionades amb el test
        lenient().when(entitatRepository.getReferenceById(anyLong())).thenReturn(entitatMock);
        lenient().when(historicHelper.findDades(anyLong(), anyList(), any(), any(), any(), any())).thenReturn(dadesMock);
    }

    // =========================================================================
    // 1. GET DADES HISTÒRIQUES (Lògica de Filtratge i Chunking)
    // =========================================================================
    @Nested
    @DisplayName("Mètode getDadesHistoriques")
    class GetDadesHistoriquesTests {

        @Test
        @DisplayName("Quan hi ha filtre directe d'unitats, llavors ignora la unitat superior i usa la llista directa")
        void getDadesHistoriques_QuanFiltreDirecte_LlavorsUsaLlistaDirecta() {
            // Arrange
            HistoricFiltreDto filtre = new HistoricFiltreDto();
            filtre.setUnitatIdFiltre(Arrays.asList(10L, 20L));
            filtre.setCodiUnitatSuperior("UO-SUPERIOR"); // Hauria de ser ignorat

            // Act
            HistoricDadesDto resultat = historicService.getDadesHistoriques(1L, filtre);

            // Assert
            assertSame(dadesMock, resultat);
            // Verifiquem que NO es va cridar al helper d'unitats ni al repositori
            verify(unitatOrganitzativaHelper, never()).getCodisOfUnitatsDescendants(any(), any());
            verify(unitatOrganitzativaRepository, never()).findUnitatsIdsAmbBustiaPerCodis(any(), anyList());

            // Verifiquem que es va passar la llista directa al helper
            ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
            verify(historicHelper, times(1)).findDades(eq(1L), captor.capture(), any(), any(), any(), any());
            assertEquals(Arrays.asList(20L, 10L), captor.getValue());
        }

        @Test
        @DisplayName("Quan hi ha unitat superior i pocs descendents (<1000), llavors fa una sola consulta al repositori")
        void getDadesHistoriques_QuanUnitatSuperiorIPocsDescendents_LlavorsFaUnaSolaConsulta() {
            // Arrange
            HistoricFiltreDto filtre = new HistoricFiltreDto();
            filtre.setCodiUnitatSuperior("UO-ARREL");

            when(unitatOrganitzativaHelper.getCodisOfUnitatsDescendants(entitatMock, "UO-ARREL"))
                    .thenReturn(new ArrayList<>(Arrays.asList("UO-FILLA-1", "UO-FILLA-2")));
            when(unitatOrganitzativaRepository.findUnitatsIdsAmbBustiaPerCodis(eq(entitatMock), anyList()))
                    .thenReturn(new ArrayList<>(Arrays.asList(30L, 40L)));

            // Act
            HistoricDadesDto resultat = historicService.getDadesHistoriques(1L, filtre);

            // Assert
            assertSame(dadesMock, resultat);

            // El llistat final ha de tenir 3 elements: "UO-FILLA-1", "UO-FILLA-2" i "UO-ARREL"
            ArgumentCaptor<List<String>> captorCodi = ArgumentCaptor.forClass(List.class);
            verify(unitatOrganitzativaRepository, times(1)).findUnitatsIdsAmbBustiaPerCodis(eq(entitatMock), captorCodi.capture());

            List<String> codisConsultats = captorCodi.getValue();
            assertEquals(3, codisConsultats.size());
            assertTrue(codisConsultats.contains("UO-ARREL"));
            assertTrue(codisConsultats.contains("UO-FILLA-1"));

            verify(historicHelper, times(1)).findDades(eq(1L), eq(Arrays.asList(40L, 30L)), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Quan hi ha unitat superior i molts descendents (>1000), llavors fa consultes per lots (chunking)")
        void getDadesHistoriques_QuanUnitatSuperiorIMoltsDescendents_LlavorsFaChunking() {
            // Arrange
            HistoricFiltreDto filtre = new HistoricFiltreDto();
            filtre.setCodiUnitatSuperior("UO-ARREL");

            // Generem 1000 descendents. +1 de l'arrel = 1001 elements totals.
            // Això forçarà el bucle do-while a executar-se 2 vegades (1000 + 1)
            List<String> moltsDescendents = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                moltsDescendents.add("UO-" + i);
            }

            when(unitatOrganitzativaHelper.getCodisOfUnitatsDescendants(entitatMock, "UO-ARREL")).thenReturn(moltsDescendents);

            // Simulem que el repositori retorna IDs diferents per cada lot
            when(unitatOrganitzativaRepository.findUnitatsIdsAmbBustiaPerCodis(eq(entitatMock), anyList()))
                    .thenReturn(Collections.singletonList(100L)) // Primer lot
                    .thenReturn(Collections.singletonList(200L)); // Segon lot

            // Act
            historicService.getDadesHistoriques(1L, filtre);

            // Assert
            ArgumentCaptor<List<String>> captorCodi = ArgumentCaptor.forClass(List.class);
            // S'ha de cridar exactament 2 vegades
            verify(unitatOrganitzativaRepository, times(2)).findUnitatsIdsAmbBustiaPerCodis(eq(entitatMock), captorCodi.capture());

            List<List<String>> totsElsLots = captorCodi.getAllValues();
            assertEquals(2, totsElsLots.size());

            // El primer lot ha de tenir exactament 1000 elements
            assertEquals(1000, totsElsLots.get(0).size());
            // El segon lot ha de tenir exactament 1 element (l'arrel afegida al final)
            assertEquals(1, totsElsLots.get(1).size());
            assertEquals("UO-ARREL", totsElsLots.get(1).get(0));

            // Verifiquem que al helper final li arriba la unió de tots dos resultats (100L i 200L)
            // Nota: com que usem un Set internament, l'ordre pot variar, però la mida i contingut són clau
            ArgumentCaptor<List<Long>> captorIds = ArgumentCaptor.forClass(List.class);
            verify(historicHelper, times(1)).findDades(eq(1L), captorIds.capture(), any(), any(), any(), any());

            List<Long> idsFinals = captorIds.getValue();
            assertEquals(2, idsFinals.size());
            assertTrue(idsFinals.contains(100L));
            assertTrue(idsFinals.contains(200L));
        }

        @Test
        @DisplayName("Quan no hi ha cap filtre (ni directe ni superior), llavors passa una llista buida")
        void getDadesHistoriques_QuanSenseFiltres_LlavorsPassaLlistaBuida() {
            // Arrange
            HistoricFiltreDto filtre = new HistoricFiltreDto(); // Tot null per defecte

            // Act
            HistoricDadesDto resultat = historicService.getDadesHistoriques(1L, filtre);

            // Assert
            assertSame(dadesMock, resultat);
            verify(unitatOrganitzativaHelper, never()).getCodisOfUnitatsDescendants(any(), any());
            verify(unitatOrganitzativaRepository, never()).findUnitatsIdsAmbBustiaPerCodis(any(), anyList());

            ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
            verify(historicHelper, times(1)).findDades(eq(1L), captor.capture(), any(), any(), any(), any());
            assertTrue(captor.getValue().isEmpty());
        }
    }

    // =========================================================================
    // 2. MÈTODES DE CÀLCUL (Delegació simple)
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de càlcul i recalcul")
    class CalculDadesTests {

        @Test
        @DisplayName("calcularDadesHistoriques: Quan es crida, llavors delega al helper amb la data")
        void calcularDadesHistoriques_QuanEsCrida_LlavorsDelega() {
            // Arrange
            Date dataProva = new Date();

            // Act
            historicService.calcularDadesHistoriques(dataProva);

            // Assert
            verify(historicHelper, times(1)).calcularDades(dataProva);
        }

        @Test
        @DisplayName("recalcularTotals: Quan es crida, llavors delega al helper amb la data")
        void recalcularTotals_QuanEsCrida_LlavorsDelega() {
            // Arrange
            Date dataProva = new Date();

            // Act
            historicService.recalcularTotals(dataProva);

            // Assert
            verify(historicHelper, times(1)).recalcularTotals(dataProva);
        }
    }
}