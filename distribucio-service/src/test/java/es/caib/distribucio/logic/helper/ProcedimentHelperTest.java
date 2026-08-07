package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ProcedimentEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.ProcedimentRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.Link;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de ProcedimentHelper")
class ProcedimentHelperTest {

    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
    @Mock private PluginHelper pluginHelper;
    @Mock private ConversioTipusHelper conversioTipusHelper;

    @InjectMocks
    private ProcedimentHelper procedimentHelper;

    private EntitatEntity entitat;
    private UnitatOrganitzativaEntity uoArrel;
    private UnitatOrganitzativaEntity uoDesti;

    @BeforeEach
    void setUp() {
        entitat = new EntitatEntity();
        ReflectionTestUtils.setField(entitat, "id", 1L);
        ReflectionTestUtils.setField(entitat, "codiDir3", "A00000000");

        uoArrel = new UnitatOrganitzativaEntity();
        ReflectionTestUtils.setField(uoArrel, "id", 10L);
        ReflectionTestUtils.setField(uoArrel, "codi", "A00000000");

        uoDesti = new UnitatOrganitzativaEntity();
        ReflectionTestUtils.setField(uoDesti, "id", 20L);
        ReflectionTestUtils.setField(uoDesti, "codi", "A00000001");
        ReflectionTestUtils.setField(uoDesti, "codiDir3Entitat", "A00000001");
    }

    // =========================================================================
    // 1. ACTUALITZACIÓ DE PROCEDIMENTS NO VIGENTS
    // =========================================================================
    @Nested
    @DisplayName("Mètode actualtizarProcedimentsNoVigents")
    class ActualitzarNoVigentsTests {

        @Test
        @DisplayName("Quan tots els procediments vigents són al mapa, llavors no s'extingeix cap")
        void actualtizarProcedimentsNoVigents_QuanTotsSonAlMapa_LlavorsCapExtingit() {
            // Arrange
            ProcedimentEntity proc1 = new ProcedimentEntity();
            proc1.setCodi("PROC1");
            proc1.setEstat(ProcedimentEstatEnumDto.VIGENT);

            when(procedimentRepository.findAllByEntitatAndEstat(entitat, ProcedimentEstatEnumDto.VIGENT))
                    .thenReturn(Collections.singletonList(proc1));

            Map<String, Procediment> mapaDistribucio = new HashMap<>();
            mapaDistribucio.put("PROC1", mock(Procediment.class));

            // Act
            procedimentHelper.actualtizarProcedimentsNoVigents(entitat, mapaDistribucio);

            // Assert
            assertEquals(ProcedimentEstatEnumDto.VIGENT, proc1.getEstat());
            verify(procedimentRepository, times(1)).findAllByEntitatAndEstat(entitat, ProcedimentEstatEnumDto.VIGENT);
        }

        @Test
        @DisplayName("Quan falten procediments al mapa, llavors es marquen com a EXTINGIT")
        void actualtizarProcedimentsNoVigents_QuanFaltenAlMapa_LlavorsMarcaExtingits() {
            // Arrange
            ProcedimentEntity proc1 = new ProcedimentEntity();
            proc1.setCodi("PROC1");
            proc1.setCodiSia("SIA1");
            proc1.setNom("Procediment 1");
            proc1.setEstat(ProcedimentEstatEnumDto.VIGENT);

            ProcedimentEntity proc2 = new ProcedimentEntity();
            proc2.setCodi("PROC2");
            proc2.setCodiSia("SIA2");
            proc2.setNom("Procediment 2");
            proc2.setEstat(ProcedimentEstatEnumDto.VIGENT);

            when(procedimentRepository.findAllByEntitatAndEstat(entitat, ProcedimentEstatEnumDto.VIGENT))
                    .thenReturn(Arrays.asList(proc1, proc2));

            Map<String, Procediment> mapaDistribucio = new HashMap<>();
            mapaDistribucio.put("PROC1", mock(Procediment.class)); // PROC2 falta

            // Act
            procedimentHelper.actualtizarProcedimentsNoVigents(entitat, mapaDistribucio);

            // Assert
            assertEquals(ProcedimentEstatEnumDto.VIGENT, proc1.getEstat());
            assertEquals(ProcedimentEstatEnumDto.EXTINGIT, proc2.getEstat());
        }

        @Test
        @DisplayName("Quan no hi ha procediments vigents a BBDD, llavors no fa res")
        void actualtizarProcedimentsNoVigents_QuanLlistaBuida_LlavorsNoFaRes() {
            // Arrange
            when(procedimentRepository.findAllByEntitatAndEstat(entitat, ProcedimentEstatEnumDto.VIGENT))
                    .thenReturn(Collections.emptyList());
            Map<String, Procediment> mapaDistribucio = new HashMap<>();

            // Act
            procedimentHelper.actualtizarProcedimentsNoVigents(entitat, mapaDistribucio);

            // Assert
            verify(procedimentRepository, times(1)).findAllByEntitatAndEstat(entitat, ProcedimentEstatEnumDto.VIGENT);
        }
    }

    // =========================================================================
    // 2. ACTUALITZACIÓ / CREACIÓ DE PROCEDIMENT (Happy Path & Updates)
    // =========================================================================
    @Nested
    @DisplayName("Mètode actualitzaProcediment")
    class ActualitzaProcedimentTests {

        @Test
        @DisplayName("Quan el procediment no existeix, llavors el crea i el guarda")
        void actualitzaProcediment_QuanNoExisteix_LlavorsCreaIGuarda() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            when(procMock.getCodigo()).thenReturn("NEW_PROC");
            when(procMock.getNombre()).thenReturn("Nou Procediment");
            when(procMock.getCodigoSIA()).thenReturn("SIA_NEW");
            when(procMock.isComun()).thenReturn(true);
            when(procMock.getUnidadAdministrativa()).thenReturn(null); // Simplificat per aquest test

            when(procedimentRepository.findByCodi(entitat.getId(), "NEW_PROC")).thenReturn(null);

            ProcedimentDto dtoMock = mock(ProcedimentDto.class);
            when(conversioTipusHelper.convertir(any(ProcedimentEntity.class), eq(ProcedimentDto.class))).thenReturn(dtoMock);

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("DEFAULT", uoArrel);

            // Act
            ProcedimentDto resultat = procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            ArgumentCaptor<ProcedimentEntity> captor = ArgumentCaptor.forClass(ProcedimentEntity.class);
            verify(procedimentRepository, times(1)).save(captor.capture());

            ProcedimentEntity entityGuardada = captor.getValue();
            assertEquals("NEW_PROC", entityGuardada.getCodi());
            assertEquals(ProcedimentEstatEnumDto.VIGENT, entityGuardada.getEstat());
            assertTrue(entityGuardada.isComu());
            assertEquals(dtoMock, resultat);
        }

        @Test
        @DisplayName("Quan el procediment existeix sense canvis, llavors NO crida a update")
        void actualitzaProcediment_QuanExisteixSenseCanvis_LlavorsNoActualitza() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            when(procMock.getCodigo()).thenReturn("PROC1");
            when(procMock.getNombre()).thenReturn("Nom Vell");
            when(procMock.getCodigoSIA()).thenReturn("SIA1");
            lenient().when(procMock.isComun()).thenReturn(false);
            when(procMock.getUnidadAdministrativa()).thenReturn(null);

            ProcedimentEntity existent = mock(ProcedimentEntity.class); // Usem mock per poder verificar 'update'
            when(existent.getCodiSia()).thenReturn("SIA1");
            when(existent.getNom()).thenReturn("Nom Vell");
            when(existent.getEstat()).thenReturn(ProcedimentEstatEnumDto.VIGENT);
            lenient().when(existent.getUnitatOrganitzativa()).thenReturn(uoArrel);
            lenient().when(existent.isComu()).thenReturn(false);

            when(procedimentRepository.findByCodi(entitat.getId(), "PROC1")).thenReturn(existent);
            when(conversioTipusHelper.convertir(eq(existent), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("DEFAULT", uoArrel);

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(existent, never()).update(anyString(), anyString(), anyString(), any(), any(), any(), anyBoolean());
            verify(procedimentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Quan el procediment existeix amb canvis, llavors crida a update amb tots els camps")
        void actualitzaProcediment_QuanExisteixAmbCanvis_LlavorsActualitza() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            when(procMock.getCodigo()).thenReturn("PROC1");
            when(procMock.getNombre()).thenReturn("Nom Nou"); // Canvi
            when(procMock.getCodigoSIA()).thenReturn("SIA_NOU"); // Canvi
            when(procMock.isComun()).thenReturn(true); // Canvi
            when(procMock.getUnidadAdministrativa()).thenReturn(null);

            ProcedimentEntity existent = mock(ProcedimentEntity.class);
            when(existent.getCodiSia()).thenReturn("SIA_VELL");
            when(existent.getNom()).thenReturn("Nom Vell");
            when(existent.getEstat()).thenReturn(ProcedimentEstatEnumDto.EXTINGIT); // Canvi
            when(existent.getUnitatOrganitzativa()).thenReturn(uoArrel);
            Link uA = new Link();
            uA.setCodigo("DEFAULT");
            when(procMock.getUnidadAdministrativa()).thenReturn(uA);
            when(existent.isComu()).thenReturn(false);

            when(procedimentRepository.findByCodi(entitat.getId(), "PROC1")).thenReturn(existent);
            when(conversioTipusHelper.convertir(eq(existent), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("DEFAULT", uoDesti); // Canvi de UO

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(existent).update(
                    eq("PROC1"), eq("Nom Nou"), eq("SIA_NOU"),
                    eq(ProcedimentEstatEnumDto.VIGENT), eq(uoDesti), eq(entitat), eq(true)
            );
        }

        @Test
        @DisplayName("Quan es produeix una excepció, llavors la captura, logueja i retorna la conversió de null")
        void actualitzaProcediment_QuanLlançaExcepcio_LlavorsGestionaErrorIRetornaNullConvertit() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            when(procMock.getUnidadAdministrativa()).thenThrow(new RuntimeException("Error simulat"));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            ProcedimentDto resultat = procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(procedimentRepository, never()).save(any());
            // Com que procedimentEntity queda null, es crida convertir amb null
            verify(conversioTipusHelper, times(1)).convertir(isNull(), eq(ProcedimentDto.class));
            assertNull(resultat); // Assumint que convertir(null) retorna null
        }
    }

    // =========================================================================
    // 3. RESOLUCIÓ D'UNITAT ORGANITZATIVA (Lògica complexa i Edge Cases)
    // =========================================================================
    @Nested
    @DisplayName("Mètode privat resoldreUnitatOrganitzativa (via actualitzaProcediment)")
    class ResolucioUOTests {

        @Test
        @DisplayName("Quan la UO ja és al mapa, llavors la retorna directament sense cridar plugins")
        void resoldreUO_QuanJaEsAlMapa_LlavorsRetornaDirectament() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_UO_1");
            when(procMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(procMock.getCodigo()).thenReturn("PROC1");

            when(procedimentRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("CODI_UO_1", uoDesti);

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, never()).procedimentGetUnitatAdministrativa(anyString());
            verify(unitatOrganitzativaRepository, never()).findByCodiDir3EntitatAndCodi(anyString(), anyString());
        }

        @Test
        @DisplayName("Quan la UO no és al mapa, llavors la consulta al plugin i la guarda al mapa")
        void resoldreUO_QuanNoEsAlMapa_LlavorsConsultaPluginIGuarda() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_UO_NOU");
            when(procMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(procMock.getCodigo()).thenReturn("PROC1");

            UnitatAdministrativa uaMock = mock(UnitatAdministrativa.class);
            when(uaMock.getCodiDir3()).thenReturn("DIR3_NOU");
            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_UO_NOU")).thenReturn(uaMock);

            when(unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi("A00000000", "DIR3_NOU")).thenReturn(uoDesti);
            when(procedimentRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, times(1)).procedimentGetUnitatAdministrativa("CODI_UO_NOU");
            verify(unitatOrganitzativaRepository, times(1)).findByCodiDir3EntitatAndCodi("A00000000", "DIR3_NOU");
            assertEquals(uoDesti, mapaUO.get("CODI_UO_NOU"));
        }

        @Test
        @DisplayName("Quan el plugin falla, llavors reintenta fins a 5 vegades abans de rendir-se en aquest nivell")
        void resoldreUO_QuanPluginFalla_LlavorsReintentaFinsA5Vegades() throws Exception {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_UO_FALLIDA");
            when(procMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(procMock.getCodigoSia()).thenReturn("SIA_TEST");
            when(procMock.getCodigo()).thenReturn("PROC1");

            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_UO_FALLIDA")).thenThrow(new RuntimeException("Error de xarxa"));

            when(procedimentRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, times(6)).procedimentGetUnitatAdministrativa("CODI_UO_FALLIDA");
        }

        @Test
        @DisplayName("Quan la UO no té codiDir3, llavors cerca recursivament pel pareCodi")
        void resoldreUO_QuanNoTeCodiDir3_LlavorsCercaPare() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_FILL");
            when(procMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(procMock.getCodigo()).thenReturn("PROC1");

            // Primera crida: retorna pareCodi però no codiDir3
            UnitatAdministrativa uaFill = mock(UnitatAdministrativa.class);
            when(uaFill.getCodiDir3()).thenReturn(null);
            when(uaFill.getPareCodi()).thenReturn("CODI_PARE");

            // Segona crida: el pare sí que té codiDir3
            UnitatAdministrativa uaPare = mock(UnitatAdministrativa.class);
            when(uaPare.getCodiDir3()).thenReturn("DIR3_PARE");
            lenient().when(uaPare.getPareCodi()).thenReturn(null);

            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_FILL")).thenReturn(uaFill);
            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_PARE")).thenReturn(uaPare);

            when(unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi("A00000000", "DIR3_PARE")).thenReturn(uoDesti);
            when(procedimentRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, times(1)).procedimentGetUnitatAdministrativa("CODI_FILL");
            verify(pluginHelper, times(1)).procedimentGetUnitatAdministrativa("CODI_PARE");
            verify(unitatOrganitzativaRepository, times(1)).findByCodiDir3EntitatAndCodi("A00000000", "DIR3_PARE");
        }

        @Test
        @DisplayName("Quan no es troba la UO enlloc, llavors fa fallback a la unitat arrel (codiUnitatArrel)")
        void resoldreUO_QuanNoEsTrobaRes_LlavorsFallbackAArrel() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_INEXISTENT");
            when(procMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(procMock.getCodigo()).thenReturn("PROC1");

            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_INEXISTENT")).thenReturn(null);

            // Fallback a l'arrel
            when(unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi("A00000000", "A00000000")).thenReturn(uoArrel);

            when(procedimentRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(unitatOrganitzativaRepository, times(1)).findByCodiDir3EntitatAndCodi("A00000000", "A00000000");
            assertEquals(uoArrel, mapaUO.get("CODI_INEXISTENT"));
        }

        @Test
        @DisplayName("Quan el Link d'unitat administrativa és null, llavors retorna null sense cridar res")
        void resoldreUO_QuanLinkEsNull_LlavorsRetornaNull() {
            // Arrange
            Procediment procMock = mock(Procediment.class);
            when(procMock.getUnidadAdministrativa()).thenReturn(null);
            when(procMock.getCodigo()).thenReturn("PROC1");

            when(procedimentRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ProcedimentDto.class))).thenReturn(mock(ProcedimentDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            procedimentHelper.actualitzaProcediment(procMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, never()).procedimentGetUnitatAdministrativa(anyString());
            verify(unitatOrganitzativaRepository, never()).findByCodiDir3EntitatAndCodi(anyString(), anyString());
        }
    }
}