package es.caib.distribucio.logic.service;

import es.caib.distribucio.logic.helper.AlertaHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.intf.dto.AlertaDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.persist.entity.AlertaEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.repository.AlertaRepository;
import es.caib.distribucio.persist.repository.ContingutRepository;
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
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de AlertaServiceImpl")
class AlertaServiceImplTest {

    @Mock private ContingutRepository contingutRepository;
    @Mock private AlertaRepository alertaRepository;
    @Mock private AlertaHelper alertaHelper;
    @Mock private ConversioTipusHelper conversioTipusHelper;
    @Mock private PaginacioHelper paginacioHelper;

    @InjectMocks
    private AlertaServiceImpl alertaService;

    private AlertaDto alertaDto;
    private AlertaEntity alertaEntity;
    private ContingutEntity contingutEntity;

    @BeforeEach
    void setUp() {
        alertaDto = new AlertaDto();
        alertaDto.setId(1L);
        alertaDto.setText("Text de prova");
        alertaDto.setError("Error de prova");
        alertaDto.setLlegida(false);
        alertaDto.setContingutId(100L);

        alertaEntity = mock(AlertaEntity.class);
        contingutEntity = mock(ContingutEntity.class);
    }

    // =========================================================================
    // 1. CREATE
    // =========================================================================
    @Nested
    @DisplayName("Mètode create")
    class CreateTests {

        @Test
        @DisplayName("create: Quan es passa un DTO vàlid, llavors crea l'alerta i retorna el DTO convertit")
        void create_QuanDtoValid_LlavorsCreaIRetornaDto() {
            // Arrange
            when(alertaHelper.crearAlerta(
                            eq(alertaDto.getText()),
                            eq(alertaDto.getError()),
                            eq(alertaDto.isLlegida()),
                            eq(alertaDto.getContingutId())))
                    .thenReturn(alertaEntity);
            when(alertaRepository.save(alertaEntity)).thenReturn(alertaEntity);
            when(conversioTipusHelper.convertir(alertaEntity, AlertaDto.class)).thenReturn(alertaDto);

            // Act
            AlertaDto resultat = alertaService.create(alertaDto);

            // Assert
            assertNotNull(resultat);
            assertEquals(alertaDto, resultat);
            verify(alertaHelper, times(1)).crearAlerta(
                    alertaDto.getText(),
                    alertaDto.getError(),
                    alertaDto.isLlegida(),
                    alertaDto.getContingutId());
            verify(alertaRepository, times(1)).save(alertaEntity);
            verify(conversioTipusHelper, times(1)).convertir(alertaEntity, AlertaDto.class);
        }
    }

    // =========================================================================
    // 2. UPDATE
    // =========================================================================
    @Nested
    @DisplayName("Mètode update")
    class UpdateTests {

        @Test
        @DisplayName("update: Quan l'alerta existeix, llavors l'actualitza i retorna el DTO")
        void update_QuanAlertaExisteix_LlavorsActualitzaIRetornaDto() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaEntity));
            when(contingutRepository.findById(100L)).thenReturn(Optional.of(contingutEntity));
            when(conversioTipusHelper.convertir(alertaEntity, AlertaDto.class)).thenReturn(alertaDto);

            // Act
            AlertaDto resultat = alertaService.update(alertaDto);

            // Assert
            assertNotNull(resultat);
            assertEquals(alertaDto, resultat);
            verify(alertaEntity, times(1)).update(
                    alertaDto.getText(),
                    alertaDto.getError(),
                    alertaDto.isLlegida());
            verify(alertaEntity, times(1)).updateContingut(contingutEntity);
            verify(conversioTipusHelper, times(1)).convertir(alertaEntity, AlertaDto.class);
        }

        @Test
        @DisplayName("update: Quan l'alerta NO existeix, llavors llança NotFoundException")
        void update_QuanAlertaNoExisteix_LlavorsLlancaNotFoundException() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                alertaService.update(alertaDto);
            });

            assertNotNull(exception);
            verify(alertaRepository, times(1)).findById(1L);
            verify(alertaEntity, never()).update(any(), any(), anyBoolean());
            verify(alertaEntity, never()).updateContingut(any());
        }

        @Test
        @DisplayName("update: Quan el contingut no existeix, llavors actualitza amb contingut = null")
        void update_QuanContingutNoExisteix_LlavorsActualitzaAmbContingutNull() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaEntity));
            when(contingutRepository.findById(100L)).thenReturn(Optional.empty());
            when(conversioTipusHelper.convertir(alertaEntity, AlertaDto.class)).thenReturn(alertaDto);

            // Act
            AlertaDto resultat = alertaService.update(alertaDto);

            // Assert
            assertNotNull(resultat);
            verify(alertaEntity, times(1)).updateContingut(null);
        }
    }

    // =========================================================================
    // 3. DELETE
    // =========================================================================
    @Nested
    @DisplayName("Mètode delete")
    class DeleteTests {

        @Test
        @DisplayName("delete: Quan l'alerta existeix, llavors l'esborra i retorna el DTO")
        void delete_QuanAlertaExisteix_LlavorsEsborraIRetornaDto() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaEntity));
            when(conversioTipusHelper.convertir(alertaEntity, AlertaDto.class)).thenReturn(alertaDto);

            // Act
            AlertaDto resultat = alertaService.delete(1L);

            // Assert
            assertNotNull(resultat);
            assertEquals(alertaDto, resultat);
            verify(alertaRepository, times(1)).delete(alertaEntity);
            verify(conversioTipusHelper, times(1)).convertir(alertaEntity, AlertaDto.class);
        }

        @Test
        @DisplayName("delete: Quan l'alerta NO existeix, llavors llança NotFoundException")
        void delete_QuanAlertaNoExisteix_LlavorsLlancaNotFoundException() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                alertaService.delete(1L);
            });

            assertNotNull(exception);
            verify(alertaRepository, times(1)).findById(1L);
            verify(alertaRepository, never()).delete(any());
        }
    }

    // =========================================================================
    // 4. FIND
    // =========================================================================
    @Nested
    @DisplayName("Mètode find")
    class FindTests {

        @Test
        @DisplayName("find: Quan l'alerta existeix, llavors retorna el DTO convertit")
        void find_QuanAlertaExisteix_LlavorsRetornaDto() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaEntity));
            when(conversioTipusHelper.convertir(alertaEntity, AlertaDto.class)).thenReturn(alertaDto);

            // Act
            AlertaDto resultat = alertaService.find(1L);

            // Assert
            assertNotNull(resultat);
            assertEquals(alertaDto, resultat);
            verify(alertaRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("find: Quan l'alerta NO existeix, llavors retorna null (convertit)")
        void find_QuanAlertaNoExisteix_LlavorsRetornaNull() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.empty());
            when(conversioTipusHelper.convertir(null, AlertaDto.class)).thenReturn(null);

            // Act
            AlertaDto resultat = alertaService.find(1L);

            // Assert
            assertNull(resultat);
            verify(alertaRepository, times(1)).findById(1L);
            verify(conversioTipusHelper, times(1)).convertir(null, AlertaDto.class);
        }
    }

    // =========================================================================
    // 5. FIND PAGINAT
    // =========================================================================
    @Nested
    @DisplayName("Mètode findPaginat")
    class FindPaginatTests {

        @Test
        @DisplayName("findPaginat: Quan la paginació està activada, llavors utilitza Pageable")
        void findPaginat_QuanPaginacioActivada_LlavorsUtilitzaPageable() {
            // Arrange
            PaginacioParamsDto params = new PaginacioParamsDto();
            Pageable pageable = mock(Pageable.class);
            Page<AlertaEntity> page = new PageImpl<>(Collections.singletonList(alertaEntity));
            PaginaDto<AlertaDto> paginaDto = new PaginaDto<>();

            when(paginacioHelper.esPaginacioActivada(params)).thenReturn(true);
            when(paginacioHelper.toSpringDataPageable(params)).thenReturn(pageable);
            when(alertaRepository.findAll(pageable)).thenReturn(page);
            when(paginacioHelper.toPaginaDto(page, AlertaDto.class)).thenReturn(paginaDto);

            // Act
            PaginaDto<AlertaDto> resultat = alertaService.findPaginat(params);

            // Assert
            assertNotNull(resultat);
            assertEquals(paginaDto, resultat);
            verify(paginacioHelper, times(1)).toSpringDataPageable(params);
            verify(paginacioHelper, never()).toSpringDataSort(any());
            verify(alertaRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("findPaginat: Quan la paginació NO està activada, llavors utilitza Sort")
        void findPaginat_QuanPaginacioNoActivada_LlavorsUtilitzaSort() {
            // Arrange
            PaginacioParamsDto params = new PaginacioParamsDto();
            Sort sort = mock(Sort.class);
            List<AlertaEntity> llista = Collections.singletonList(alertaEntity);
            PaginaDto<AlertaDto> paginaDto = new PaginaDto<>();

            when(paginacioHelper.esPaginacioActivada(params)).thenReturn(false);
            when(paginacioHelper.toSpringDataSort(params)).thenReturn(sort);
            when(alertaRepository.findAll(sort)).thenReturn(llista);
            when(paginacioHelper.toPaginaDto(llista, AlertaDto.class)).thenReturn(paginaDto);

            // Act
            PaginaDto<AlertaDto> resultat = alertaService.findPaginat(params);

            // Assert
            assertNotNull(resultat);
            assertEquals(paginaDto, resultat);
            verify(paginacioHelper, times(1)).toSpringDataSort(params);
            verify(paginacioHelper, never()).toSpringDataPageable(any());
            verify(alertaRepository, times(1)).findAll(sort);
        }
    }

    // =========================================================================
    // 6. FIND PAGINAT BY LLEGIDA
    // =========================================================================
    @Nested
    @DisplayName("Mètode findPaginatByLlegida")
    class FindPaginatByLlegidaTests {

        @Test
        @DisplayName("findPaginatByLlegida: Quan la paginació està activada, llavors cerca amb Pageable i inclou el contingut pare")
        void findPaginatByLlegida_QuanPaginacioActivada_LlavorsUtilitzaPageable() {
            // Arrange
            PaginacioParamsDto params = new PaginacioParamsDto();
            Pageable pageable = mock(Pageable.class);
            Page<AlertaEntity> page = new PageImpl<>(Collections.singletonList(alertaEntity));
            PaginaDto<AlertaDto> paginaDto = new PaginaDto<>();

            List<ContingutEntity> contingutsFills = new ArrayList<>();
            contingutsFills.add(contingutEntity);

            when(contingutRepository.findRegistresByPareId(100L)).thenReturn(contingutsFills);
            when(contingutRepository.getReferenceById(100L)).thenReturn(contingutEntity);
            when(paginacioHelper.esPaginacioActivada(params)).thenReturn(true);
            when(paginacioHelper.toSpringDataPageable(params)).thenReturn(pageable);
            when(alertaRepository.findByLlegidaAndContinguts(eq(true), anyList(), eq(pageable))).thenReturn(page);
            when(paginacioHelper.toPaginaDto(page, AlertaDto.class)).thenReturn(paginaDto);

            // Act
            PaginaDto<AlertaDto> resultat = alertaService.findPaginatByLlegida(true, 100L, params);

            // Assert
            assertNotNull(resultat);
            assertEquals(paginaDto, resultat);

            ArgumentCaptor<List<ContingutEntity>> captor = ArgumentCaptor.forClass(List.class);
            verify(alertaRepository, times(1)).findByLlegidaAndContinguts(eq(true), captor.capture(), eq(pageable));

            List<ContingutEntity> contingutsCapturats = captor.getValue();
            assertEquals(2, contingutsCapturats.size()); // 1 fill + 1 pare
            assertTrue(contingutsCapturats.contains(contingutEntity));

            verify(paginacioHelper, times(1)).toSpringDataPageable(params);
            verify(paginacioHelper, never()).toSpringDataSort(any());
        }

        @Test
        @DisplayName("findPaginatByLlegida: Quan la paginació NO està activada, llavors utilitza Sort")
        void findPaginatByLlegida_QuanPaginacioNoActivada_LlavorsUtilitzaSort() {
            // Arrange
            PaginacioParamsDto params = new PaginacioParamsDto();
            Sort sort = mock(Sort.class);
            List<AlertaEntity> llista = Collections.singletonList(alertaEntity);
            PaginaDto<AlertaDto> paginaDto = new PaginaDto<>();

            List<ContingutEntity> contingutsFills = new ArrayList<>();
            contingutsFills.add(contingutEntity);

            when(contingutRepository.findRegistresByPareId(100L)).thenReturn(contingutsFills);
            when(contingutRepository.getReferenceById(100L)).thenReturn(contingutEntity);
            when(paginacioHelper.esPaginacioActivada(params)).thenReturn(false);
            when(paginacioHelper.toSpringDataSort(params)).thenReturn(sort);
            when(alertaRepository.findByLlegidaAndContinguts(eq(false), anyList(), eq(sort))).thenReturn(llista);
            when(paginacioHelper.toPaginaDto(llista, AlertaDto.class)).thenReturn(paginaDto);

            // Act
            PaginaDto<AlertaDto> resultat = alertaService.findPaginatByLlegida(false, 100L, params);

            // Assert
            assertNotNull(resultat);
            assertEquals(paginaDto, resultat);
            verify(paginacioHelper, times(1)).toSpringDataSort(params);
            verify(paginacioHelper, never()).toSpringDataPageable(any());
            verify(alertaRepository, times(1)).findByLlegidaAndContinguts(eq(false), anyList(), eq(sort));
        }

        @Test
        @DisplayName("findPaginatByLlegida: Quan no hi ha continguts fills, llavors només inclou el contingut pare")
        void findPaginatByLlegida_QuanNoHiHaFills_LlavorsNomesInclouPare() {
            // Arrange
            PaginacioParamsDto params = new PaginacioParamsDto();
            Pageable pageable = mock(Pageable.class);
            Page<AlertaEntity> page = new PageImpl<>(Collections.emptyList());
            PaginaDto<AlertaDto> paginaDto = new PaginaDto<>();

            when(contingutRepository.findRegistresByPareId(100L)).thenReturn(new ArrayList<>());
            when(contingutRepository.getReferenceById(100L)).thenReturn(contingutEntity);
            when(paginacioHelper.esPaginacioActivada(params)).thenReturn(true);
            when(paginacioHelper.toSpringDataPageable(params)).thenReturn(pageable);
            when(alertaRepository.findByLlegidaAndContinguts(eq(true), anyList(), eq(pageable))).thenReturn(page);
            when(paginacioHelper.toPaginaDto(page, AlertaDto.class)).thenReturn(paginaDto);

            // Act
            PaginaDto<AlertaDto> resultat = alertaService.findPaginatByLlegida(true, 100L, params);

            // Assert
            assertNotNull(resultat);

            ArgumentCaptor<List<ContingutEntity>> captor = ArgumentCaptor.forClass(List.class);
            verify(alertaRepository, times(1)).findByLlegidaAndContinguts(eq(true), captor.capture(), eq(pageable));

            List<ContingutEntity> contingutsCapturats = captor.getValue();
            assertEquals(1, contingutsCapturats.size()); // Només el pare
            assertTrue(contingutsCapturats.contains(contingutEntity));
        }
    }
}