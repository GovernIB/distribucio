package es.caib.distribucio.logic.service;

import es.caib.distribucio.logic.helper.*;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentEstat;
import es.caib.pluginsib.arxiu.api.ExpedientMetadades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de AnnexosServiceImpl")
class AnnexosServiceImplTest {

    @Mock private EntityComprovarHelper entityComprovarHelper;
    @Mock private PaginacioHelper paginacioHelper;
    @Mock private AnnexosAdminHelper annexosAdminHelper;
    @Mock private RegistreAnnexRepository registreAnnexRepository;
    @Mock private RegistreService registreService;
    @Mock private PluginHelper pluginHelper;
    @Mock private RegistreHelper registreHelper;
    @Mock private ConversioTipusHelper conversioTipusHelper;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private AnnexosServiceImpl annexosService;

    private EntitatEntity entitatMock;
    private RegistreEntity registreMock;
    private RegistreAnnexEntity annexMock;
    private Document documentMock;
    private DistribucioRegistreAnotacio anotacioMock;

    @BeforeEach
    void setUp() {
        entitatMock = mock(EntitatEntity.class);
        registreMock = mock(RegistreEntity.class);
        lenient().when(registreMock.getId()).thenReturn(1L);
        lenient().when(registreMock.getNumero()).thenReturn("2023/12345");
        lenient().when(registreMock.getArxiuUuid()).thenReturn("uuid-arxiu-123");

        annexMock = mock(RegistreAnnexEntity.class);
        lenient().when(annexMock.getId()).thenReturn(10L);
        lenient().when(annexMock.getRegistre()).thenReturn(registreMock);
        lenient().when(annexMock.getTitol()).thenReturn("Document de prova");
        lenient().when(annexMock.getFitxerArxiuUuid()).thenReturn("uuid-doc-123");
        lenient().when(annexMock.getArxiuEstat()).thenReturn(AnnexEstat.ESBORRANY);

        documentMock = mock(Document.class);
        lenient().when(documentMock.getEstat()).thenReturn(DocumentEstat.ESBORRANY);

        anotacioMock = mock(DistribucioRegistreAnotacio.class);
        lenient().when(anotacioMock.getUnitatOrganitzativaCodi()).thenReturn("UO001");
        lenient().when(anotacioMock.getExpedientArxiuUuid()).thenReturn("uuid-exp-123");
        lenient().when(anotacioMock.getProcedimentCodi()).thenReturn("PROC001");

        lenient().when(entityComprovarHelper.comprovarEntitat(anyLong(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(entitatMock);
    }

    // =========================================================================
    // 2. FIND ANNEX IDS
    // =========================================================================
    @Nested
    @DisplayName("Mètode findAnnexIds")
    class FindAnnexIdsTests {

        @Test
        @DisplayName("findAnnexIds: Quan es passa un filtre, llavors retorna la llista d'IDs")
        void findAnnexIds_QuanFiltreValid_LlavorsRetornaIds() {
            // Arrange
            AnnexosFiltreDto filtre = new AnnexosFiltreDto();
            filtre.setDataRecepcioFi(new Date());
            List<Long> idsEsperats = Arrays.asList(1L, 2L);

            when(registreAnnexRepository.findIdsByFiltre(any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                    .thenReturn(idsEsperats);

            // Act
            List<Long> resultat = annexosService.findAnnexIds(1L, filtre);

            // Assert
            assertEquals(2, resultat.size());
            verify(registreAnnexRepository, times(1)).findIdsByFiltre(any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any());
        }
    }

    // =========================================================================
    // 3. GUARDAR COM A DEFINITIU (Lògica complexa)
    // =========================================================================
    @Nested
    @DisplayName("Mètode guardarComADefinitiu")
    class GuardarComADefinitiuTests {

        @Test
        @DisplayName("guardarComADefinitiu: Quan ja és DEFINITIU, llavors retorna ok=true immediatament")
        void guardarComADefinitiu_QuanJaEsDefinitiu_LlavorsRetornaOkTrue() {
            // Arrange
            when(annexMock.getArxiuEstat()).thenReturn(AnnexEstat.DEFINITIU);
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertTrue(resultat.isOk());
            assertEquals("annex.accio.marcardefinitiu.jaDefinitiu", resultat.getKeyMessage());
            verify(registreService, never()).getArxiuDetall(anyLong());
        }

        @Test
        @DisplayName("guardarComADefinitiu: Quan l'expedient està TANCAT, llavors retorna ok=false")
        void guardarComADefinitiu_QuanExpedientTancat_LlavorsRetornaOkFalse() {
            // Arrange
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));
            ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
            arxiuDetall.setEniEstat(ExpedientEstatEnumDto.TANCAT);
            when(registreService.getArxiuDetall(1L)).thenReturn(arxiuDetall);

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertFalse(resultat.isOk());
            assertEquals("annex.accio.marcardefinitiu.expedientTancat", resultat.getKeyMessage());
        }

        @Test
        @DisplayName("guardarComADefinitiu: Quan el document ja és DEFINITIU a l'arxiu, llavors actualitza l'estat i retorna ok=false")
        void guardarComADefinitiu_QuanDocumentDefinitiuArxiu_LlavorsActualitzaIRetornaOkFalse() {
            // Arrange
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));
            ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
            arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
            when(registreService.getArxiuDetall(1L)).thenReturn(arxiuDetall);
            when(documentMock.getEstat()).thenReturn(DocumentEstat.DEFINITIU);
            when(pluginHelper.arxiuDocumentConsultar(any(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(documentMock);

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertFalse(resultat.isOk()); // Nota: El codi original posa setOk(false) aquí
            assertEquals("annex.accio.marcardefinitiu.definitiuArxiu", resultat.getKeyMessage());
            verify(annexMock, times(1)).setArxiuEstat(AnnexEstat.DEFINITIU);
            verify(registreAnnexRepository, times(1)).save(annexMock);
        }

        @Test
        @DisplayName("guardarComADefinitiu: Quan el document s'ha mogut a un expedient diferent, llavors retorna ok=false")
        void guardarComADefinitiu_QuanMogutBackoffice_LlavorsRetornaOkFalse() {
            // Arrange
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));
            ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
            arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
            when(registreService.getArxiuDetall(1L)).thenReturn(arxiuDetall);

            ExpedientMetadades metadades = mock(ExpedientMetadades.class);
            when(metadades.getIdentificador()).thenReturn("uuid-exp-diferent");
            when(documentMock.getExpedientMetadades()).thenReturn(metadades);
            when(pluginHelper.arxiuDocumentConsultar(any(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(documentMock);

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertFalse(resultat.isOk());
            assertEquals("annex.accio.marcardefinitiu.mogutBackoffice", resultat.getKeyMessage());
        }

        @Test
        @DisplayName("guardarComADefinitiu: Quan hi ha una excepció consultant l'arxiu, llavors captura i retorna error")
        void guardarComADefinitiu_QuanExcepcioArxiu_LlavorsCapturaIRetornaError() {
            // Arrange
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));
            when(registreService.getArxiuDetall(1L)).thenThrow(new RuntimeException("Error de xarxa"));

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertFalse(resultat.isOk());
            assertEquals("annex.accio.marcardefinitiu.errorArxiu", resultat.getKeyMessage());
            assertNotNull(resultat.getThrowable());
        }

        @Test
        @DisplayName("guardarComADefinitiu: Quan crearExpedientArxiu retorna errors, llavors retorna ok=false")
        void guardarComADefinitiu_QuanErrorCrearExpedient_LlavorsRetornaOkFalse() {
            // Arrange
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));
            ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
            arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
            when(registreService.getArxiuDetall(1L)).thenReturn(arxiuDetall);
            when(pluginHelper.arxiuDocumentConsultar(any(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(documentMock);
            when(registreHelper.getDistribucioRegistreAnotacio(1L)).thenReturn(anotacioMock);
            lenient().when(registreHelper.crearExpedientArxiu(any(), any(), any())).thenReturn(Collections.singletonList(new RuntimeException("Error creant expedient")));

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertFalse(resultat.isOk());
            assertEquals("annex.accio.marcardefinitiu.errorUpdate", resultat.getKeyMessage());
        }

        @Test
        @DisplayName("guardarComADefinitiu: Quan la validació de firma és invàlida, llavors retorna ok=false")
        void guardarComADefinitiu_QuanFirmaInvalida_LlavorsRetornaOkFalse() {
            // Arrange
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));
            ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
            arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
            when(registreService.getArxiuDetall(1L)).thenReturn(arxiuDetall);
            when(pluginHelper.arxiuDocumentConsultar(any(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(documentMock);
            when(registreHelper.getDistribucioRegistreAnotacio(1L)).thenReturn(anotacioMock);
            lenient().when(registreHelper.crearExpedientArxiu(any(), any(), any())).thenReturn(null); // Cap error

            DistribucioRegistreAnnex distAnnexMock = mock(DistribucioRegistreAnnex.class);
            when(distAnnexMock.getValidacioFirmaEstat()).thenReturn(ValidacioFirmaEnum.FIRMA_INVALIDA);
            when(conversioTipusHelper.convertir(annexMock, DistribucioRegistreAnnex.class)).thenReturn(distAnnexMock);

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertFalse(resultat.isOk());
            assertEquals("annex.accio.marcardefinitiu.errorFirma", resultat.getKeyMessage());
        }

        @Test
        @DisplayName("guardarComADefinitiu: Quan després del refresh l'estat segueix sent ESBORRANY, llavors retorna ok=false")
        void guardarComADefinitiu_QuanSegueixSendoEsborrany_LlavorsRetornaOkFalse() {
            // Arrange
            when(registreAnnexRepository.findById(10L)).thenReturn(Optional.of(annexMock));
            ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
            arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
            when(registreService.getArxiuDetall(1L)).thenReturn(arxiuDetall);
            when(pluginHelper.arxiuDocumentConsultar(any(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(documentMock);
            when(registreHelper.getDistribucioRegistreAnotacio(1L)).thenReturn(anotacioMock);
            lenient().when(registreHelper.crearExpedientArxiu(any(), any(), any())).thenReturn(null);

            DistribucioRegistreAnnex distAnnexMock = mock(DistribucioRegistreAnnex.class);
            when(distAnnexMock.getValidacioFirmaEstat()).thenReturn(ValidacioFirmaEnum.FIRMA_VALIDA);
            when(conversioTipusHelper.convertir(annexMock, DistribucioRegistreAnnex.class)).thenReturn(distAnnexMock);
            when(annexMock.getArxiuEstat()).thenReturn(AnnexEstat.ESBORRANY); // Simulem que segueix sent esborrany

            // Act
            ResultatAnnexDefinitiuDto resultat = annexosService.guardarComADefinitiu(10L);

            // Assert
            assertFalse(resultat.isOk());
            assertEquals("annex.accio.marcardefinitiu.senseFirma", resultat.getKeyMessage());
            verify(entityManager, times(1)).refresh(annexMock);
        }
    }

    // =========================================================================
    // 4. FIND MULTIPLE (Chunking)
    // =========================================================================
    @Nested
    @DisplayName("Mètode findMultiple")
    class FindMultipleTests {

        @Test
        @DisplayName("findMultiple: Quan la llista d'IDs és nul·la o buida, llavors retorna llista buida immediatament")
        void findMultiple_QuanLlistaNulaOBuida_LlavorsRetornaBuida() {
            // Act & Assert
            assertTrue(annexosService.findMultiple(1L, null, true).isEmpty());
            assertTrue(annexosService.findMultiple(1L, Collections.emptyList(), true).isEmpty());
            verify(registreAnnexRepository, never()).findByIdIn(anyList());
        }

        @Test
        @DisplayName("findMultiple: Quan hi ha menys de 100 IDs, llavors fa una sola consulta")
        void findMultiple_QuanMenysDe100Ids_LlavorsFaUnaSolaConsulta() {
            // Arrange
            List<Long> ids = Arrays.asList(1L, 2L, 3L);
            List<RegistreAnnexEntity> entitats = Collections.singletonList(annexMock);
            List<RegistreAnnexDto> dtos = Collections.singletonList(new RegistreAnnexDto());

            when(registreAnnexRepository.findByIdIn(ids)).thenReturn(entitats);
            when(conversioTipusHelper.convertirList(entitats, RegistreAnnexDto.class)).thenReturn(dtos);

            // Act
            List<RegistreAnnexDto> resultat = annexosService.findMultiple(1L, ids, true);

            // Assert
            assertEquals(1, resultat.size());
            verify(registreAnnexRepository, times(1)).findByIdIn(ids);
        }

        @Test
        @DisplayName("findMultiple: Quan hi ha més de 100 IDs, llavors fa consultes per lots (chunking)")
        void findMultiple_QuanMesDe100Ids_LlavorsFaConsultesPerLots() {
            // Arrange
            List<Long> ids = new ArrayList<>();
            for (int i = 1; i <= 150; i++) ids.add((long) i); // 150 elements

            List<RegistreAnnexEntity> lot1 = Collections.singletonList(annexMock);
            List<RegistreAnnexEntity> lot2 = Collections.singletonList(annexMock);

            when(registreAnnexRepository.findByIdIn(anyList())).thenReturn(lot1).thenReturn(lot2);
            when(conversioTipusHelper.convertirList(anyList(), eq(RegistreAnnexDto.class))).thenReturn(Collections.singletonList(new RegistreAnnexDto()));

            // Act
            List<RegistreAnnexDto> resultat = annexosService.findMultiple(1L, ids, true);

            // Assert
            assertEquals(2, resultat.size()); // 1 + 1
            verify(registreAnnexRepository, times(2)).findByIdIn(anyList());

            // Verificar que el segon lot va ser exactament de 50 elements (150 - 100)
            ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
            verify(registreAnnexRepository, times(2)).findByIdIn(captor.capture());
            assertEquals(100, captor.getAllValues().get(0).size());
            assertEquals(50, captor.getAllValues().get(1).size());
        }
    }

    // =========================================================================
    // 5. FIND COPIES REGISTRE
    // =========================================================================
    @Nested
    @DisplayName("Mètode findCopiesRegistre")
    class FindCopiesRegistreTests {

        @Test
        @DisplayName("findCopiesRegistre: Quan es passa un número, llavors delega a registreHelper")
        void findCopiesRegistre_QuanValid_LlavorsDelega() {
            // Arrange
            List<Integer> copies = Arrays.asList(1, 2);
            when(registreHelper.findCopiesRegistre("2023/12345")).thenReturn(copies);

            // Act
            List<Integer> resultat = annexosService.findCopiesRegistre("2023/12345");

            // Assert
            assertEquals(2, resultat.size());
            verify(registreHelper, times(1)).findCopiesRegistre("2023/12345");
        }
    }
}