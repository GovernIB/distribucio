package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ServeiEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.ServeiRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.Link;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import es.caib.distribucio.plugin.servei.Servei;
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
@DisplayName("Proves unitàries de ServeiHelper")
class ServeiHelperTest {

    @Mock private ServeiRepository serveiRepository;
    @Mock private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
    @Mock private PluginHelper pluginHelper;
    @Mock private ConversioTipusHelper conversioTipusHelper;

    @InjectMocks
    private ServeiHelper serveiHelper;

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
    // 1. ACTUALITZACIÓ DE SERVEIS NO VIGENTS
    // =========================================================================
    @Nested
    @DisplayName("Mètode actualtizarServeisNoVigents")
    class ActualitzarNoVigentsTests {

        @Test
        @DisplayName("Quan tots els serveis vigents són al mapa, llavors no s'extingeix cap")
        void actualtizarServeisNoVigents_QuanTotsSonAlMapa_LlavorsCapExtingit() {
            // Arrange
            ServeiEntity servei1 = new ServeiEntity();
            servei1.setCodi("SERV1");
            servei1.setEstat(ServeiEstatEnumDto.VIGENT);

            when(serveiRepository.findAllByEntitatAndEstat(entitat, ServeiEstatEnumDto.VIGENT))
                    .thenReturn(Collections.singletonList(servei1));

            Map<String, Servei> mapaRolsac = new HashMap<>();
            mapaRolsac.put("SERV1", mock(Servei.class));

            // Act
            serveiHelper.actualtizarServeisNoVigents(entitat, mapaRolsac);

            // Assert
            assertEquals(ServeiEstatEnumDto.VIGENT, servei1.getEstat());
            verify(serveiRepository, times(1)).findAllByEntitatAndEstat(entitat, ServeiEstatEnumDto.VIGENT);
        }

        @Test
        @DisplayName("Quan falten serveis al mapa, llavors es marquen com a EXTINGIT")
        void actualtizarServeisNoVigents_QuanFaltenAlMapa_LlavorsMarcaExtingits() {
            // Arrange
            ServeiEntity servei1 = new ServeiEntity();
            servei1.setCodi("SERV1");
            servei1.setCodiSia("SIA1");
            servei1.setNom("Servei 1");
            servei1.setEstat(ServeiEstatEnumDto.VIGENT);

            ServeiEntity servei2 = new ServeiEntity();
            servei2.setCodi("SERV2");
            servei2.setCodiSia("SIA2");
            servei2.setNom("Servei 2");
            servei2.setEstat(ServeiEstatEnumDto.VIGENT);

            when(serveiRepository.findAllByEntitatAndEstat(entitat, ServeiEstatEnumDto.VIGENT))
                    .thenReturn(Arrays.asList(servei1, servei2));

            Map<String, Servei> mapaRolsac = new HashMap<>();
            mapaRolsac.put("SERV1", mock(Servei.class)); // SERV2 falta

            // Act
            serveiHelper.actualtizarServeisNoVigents(entitat, mapaRolsac);

            // Assert
            assertEquals(ServeiEstatEnumDto.VIGENT, servei1.getEstat());
            assertEquals(ServeiEstatEnumDto.EXTINGIT, servei2.getEstat());
        }

        @Test
        @DisplayName("Quan no hi ha serveis vigents a BBDD, llavors no fa res")
        void actualtizarServeisNoVigents_QuanLlistaBuida_LlavorsNoFaRes() {
            // Arrange
            when(serveiRepository.findAllByEntitatAndEstat(entitat, ServeiEstatEnumDto.VIGENT))
                    .thenReturn(Collections.emptyList());
            Map<String, Servei> mapaRolsac = new HashMap<>();

            // Act
            serveiHelper.actualtizarServeisNoVigents(entitat, mapaRolsac);

            // Assert
            verify(serveiRepository, times(1)).findAllByEntitatAndEstat(entitat, ServeiEstatEnumDto.VIGENT);
        }
    }

    // =========================================================================
    // 2. ACTUALITZACIÓ / CREACIÓ DE SERVEI (Happy Path & Updates)
    // =========================================================================
    @Nested
    @DisplayName("Mètode actualitzaServei")
    class ActualitzaServeiTests {

        @Test
        @DisplayName("Quan el servei no existeix, llavors el crea i el guarda")
        void actualitzaServei_QuanNoExisteix_LlavorsCreaIGuarda() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            when(serveiMock.getCodigo()).thenReturn("NEW_SERV");
            when(serveiMock.getNombre()).thenReturn("Nou Servei");
            when(serveiMock.getCodigoSIA()).thenReturn("SIA_NEW");
            when(serveiMock.isComun()).thenReturn(true);
            when(serveiMock.getUnidadAdministrativa()).thenReturn(null);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);

            when(serveiRepository.findByCodi(entitat.getId(), "NEW_SERV")).thenReturn(null);
            ServeiDto dtoMock = mock(ServeiDto.class);
            when(conversioTipusHelper.convertir(any(ServeiEntity.class), eq(ServeiDto.class))).thenReturn(dtoMock);

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("DEFAULT", uoArrel);

            // Act
            ServeiDto resultat = serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            ArgumentCaptor<ServeiEntity> captor = ArgumentCaptor.forClass(ServeiEntity.class);
            verify(serveiRepository, times(1)).save(captor.capture());

            ServeiEntity entityGuardada = captor.getValue();
            assertEquals("NEW_SERV", entityGuardada.getCodi());
            assertEquals(ServeiEstatEnumDto.VIGENT, entityGuardada.getEstat());
            assertTrue(entityGuardada.isComu());
            assertEquals(dtoMock, resultat);
        }

        @Test
        @DisplayName("Quan el servei existeix sense canvis, llavors NO crida a update")
        void actualitzaServei_QuanExisteixSenseCanvis_LlavorsNoActualitza() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            when(serveiMock.getCodigo()).thenReturn("SERV1");
            when(serveiMock.getNombre()).thenReturn("Nom Vell");
            when(serveiMock.getCodigoSIA()).thenReturn("SIA1");
            lenient().when(serveiMock.isComun()).thenReturn(false);
            when(serveiMock.getUnidadAdministrativa()).thenReturn(null);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);

            ServeiEntity existent = mock(ServeiEntity.class);
            when(existent.getCodiSia()).thenReturn("SIA1");
            when(existent.getNom()).thenReturn("Nom Vell");
            when(existent.getEstat()).thenReturn(ServeiEstatEnumDto.VIGENT);
            lenient().when(existent.getUnitatOrganitzativa()).thenReturn(uoArrel);
            lenient().when(existent.isComu()).thenReturn(false);

            when(serveiRepository.findByCodi(entitat.getId(), "SERV1")).thenReturn(existent);
            when(conversioTipusHelper.convertir(eq(existent), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("DEFAULT", uoArrel);

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(existent, never()).update(anyString(), anyString(), anyString(), any(), any(), any(), anyBoolean());
            verify(serveiRepository, never()).save(any());
        }

        @Test
        @DisplayName("Quan el servei existeix amb canvis, llavors crida a update amb tots els camps")
        void actualitzaServei_QuanExisteixAmbCanvis_LlavorsActualitza() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            when(serveiMock.getCodigo()).thenReturn("SERV1");
            when(serveiMock.getNombre()).thenReturn("Nom Nou"); // Canvi
            when(serveiMock.getCodigoSIA()).thenReturn("SIA_NOU"); // Canvi
            when(serveiMock.isComun()).thenReturn(true); // Canvi

            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("DEFAULT");
            when(serveiMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);

            ServeiEntity existent = mock(ServeiEntity.class);
            when(existent.getCodiSia()).thenReturn("SIA_VELL");
            when(existent.getNom()).thenReturn("Nom Vell");
            when(existent.getEstat()).thenReturn(ServeiEstatEnumDto.EXTINGIT); // Canvi
            when(existent.getUnitatOrganitzativa()).thenReturn(uoArrel);
            when(existent.isComu()).thenReturn(false);

            when(serveiRepository.findByCodi(entitat.getId(), "SERV1")).thenReturn(existent);
            when(conversioTipusHelper.convertir(eq(existent), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("DEFAULT", uoDesti); // Canvi de UO

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(existent, times(1)).update(
                    eq("SERV1"), eq("Nom Nou"), eq("SIA_NOU"),
                    eq(ServeiEstatEnumDto.VIGENT), eq(uoDesti), eq(entitat), eq(true)
            );
        }

        @Test
        @DisplayName("Quan es produeix una excepció, llavors la captura, logueja i retorna la conversió de null")
        void actualitzaServei_QuanLlançaExcepcio_LlavorsGestionaErrorIRetornaNullConvertit() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            when(serveiMock.getUnidadAdministrativa()).thenThrow(new RuntimeException("Error simulat"));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            ServeiDto resultat = serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(serveiRepository, never()).save(any());
            verify(conversioTipusHelper, times(1)).convertir(isNull(), eq(ServeiDto.class));
            assertNull(resultat);
        }
    }

    // =========================================================================
    // 3. RESOLUCIÓ D'UNITAT ORGANITZATIVA (Lògica complexa i Edge Cases)
    // =========================================================================
    @Nested
    @DisplayName("Mètode privat resoldreUnitatOrganitzativa (via actualitzaServei)")
    class ResolucioUOTests {

        @Test
        @DisplayName("Quan la UO ja és al mapa, llavors la retorna directament sense cridar plugins")
        void resoldreUO_QuanJaEsAlMapa_LlavorsRetornaDirectament() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_UO_1");
            when(serveiMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);
            when(serveiMock.getCodigo()).thenReturn("SERV1");

            when(serveiRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();
            mapaUO.put("CODI_UO_1", uoDesti);

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, never()).procedimentGetUnitatAdministrativa(anyString());
            verify(unitatOrganitzativaRepository, never()).findByCodiDir3EntitatAndCodi(anyString(), anyString());
        }

        @Test
        @DisplayName("Quan unidadAdministrativaLink és null, llavors fa fallback a organoInstructorLink")
        void resoldreUO_QuanLinkAdminNull_LlavorsUsaOrganoInstructor() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            Link instructorLinkMock = mock(Link.class);
            when(instructorLinkMock.getCodigo()).thenReturn("CODI_INSTRUCTOR");

            when(serveiMock.getUnidadAdministrativa()).thenReturn(null); // Clau del test
            when(serveiMock.getOrganoInstructor()).thenReturn(instructorLinkMock);
            when(serveiMock.getCodigo()).thenReturn("SERV1");

            UnitatAdministrativa uaMock = mock(UnitatAdministrativa.class);
            when(uaMock.getCodiDir3()).thenReturn("DIR3_INST");
            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_INSTRUCTOR")).thenReturn(uaMock);

            when(unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi("A00000000", "DIR3_INST")).thenReturn(uoDesti);
            when(serveiRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, times(1)).procedimentGetUnitatAdministrativa("CODI_INSTRUCTOR");
            assertEquals(uoDesti, mapaUO.get("CODI_INSTRUCTOR"));
        }

        @Test
        @DisplayName("Quan el plugin falla, llavors reintenta fins a 5 vegades abans de rendir-se")
        void resoldreUO_QuanPluginFalla_LlavorsReintentaFinsA5Vegades() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_UO_FALLIDA");
            when(serveiMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);
            when(serveiMock.getCodigoSia()).thenReturn("SIA_TEST");
            when(serveiMock.getCodigo()).thenReturn("SERV1");

            // thenThrow() sense límit farà que falli les 5 vegades
            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_UO_FALLIDA")).thenThrow(new RuntimeException("Error de xarxa"));

            when(serveiRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, times(6)).procedimentGetUnitatAdministrativa("CODI_UO_FALLIDA");
        }

        @Test
        @DisplayName("Quan la UO no té codiDir3, llavors cerca recursivament pel pareCodi")
        void resoldreUO_QuanNoTeCodiDir3_LlavorsCercaPare() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_FILL");
            when(serveiMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);
            when(serveiMock.getCodigo()).thenReturn("SERV1");

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
            when(serveiRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, times(1)).procedimentGetUnitatAdministrativa("CODI_FILL");
            verify(pluginHelper, times(1)).procedimentGetUnitatAdministrativa("CODI_PARE");
            verify(unitatOrganitzativaRepository, times(1)).findByCodiDir3EntitatAndCodi("A00000000", "DIR3_PARE");
        }

        @Test
        @DisplayName("Quan no es troba la UO enlloc, llavors fa fallback a la unitat arrel (codiUnitatArrel)")
        void resoldreUO_QuanNoEsTrobaRes_LlavorsFallbackAArrel() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            Link linkMock = mock(Link.class);
            when(linkMock.getCodigo()).thenReturn("CODI_INEXISTENT");
            when(serveiMock.getUnidadAdministrativa()).thenReturn(linkMock);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);
            when(serveiMock.getCodigo()).thenReturn("SERV1");

            when(pluginHelper.procedimentGetUnitatAdministrativa("CODI_INEXISTENT")).thenReturn(null);

            // Fallback a l'arrel
            when(unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi("A00000000", "A00000000")).thenReturn(uoArrel);

            when(serveiRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(unitatOrganitzativaRepository, times(1)).findByCodiDir3EntitatAndCodi("A00000000", "A00000000");
            assertEquals(uoArrel, mapaUO.get("CODI_INEXISTENT"));
        }

        @Test
        @DisplayName("Quan ambdós links (Admin i Instructor) són null, llavors retorna null sense cridar res")
        void resoldreUO_QuanAmbdosLinksSonNull_LlavorsRetornaNull() {
            // Arrange
            Servei serveiMock = mock(Servei.class);
            when(serveiMock.getUnidadAdministrativa()).thenReturn(null);
            when(serveiMock.getOrganoInstructor()).thenReturn(null);
            when(serveiMock.getCodigo()).thenReturn("SERV1");

            when(serveiRepository.findByCodi(anyLong(), anyString())).thenReturn(null);
            when(conversioTipusHelper.convertir(any(), eq(ServeiDto.class))).thenReturn(mock(ServeiDto.class));

            Map<String, UnitatOrganitzativaEntity> mapaUO = new HashMap<>();

            // Act
            serveiHelper.actualitzaServei(serveiMock, mapaUO, entitat);

            // Assert
            verify(pluginHelper, never()).procedimentGetUnitatAdministrativa(anyString());
            verify(unitatOrganitzativaRepository, never()).findByCodiDir3EntitatAndCodi(anyString(), anyString());
        }
    }
}