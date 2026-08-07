package es.caib.distribucio.logic.service;

import es.caib.distribucio.logic.helper.*;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.permission.ExtendedPermission;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.repository.AvisRepository;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de EntitatServiceImpl")
class EntitatServiceImplTest {

    @Mock private EntitatRepository entitatRepository;
    @Mock private BustiaRepository bustiaRepository;
    @Mock private ConversioTipusHelper conversioTipusHelper;
    @Mock private PaginacioHelper paginacioHelper;
    @Mock private PermisosHelper permisosHelper;
    @Mock private CacheHelper cacheHelper;
    @Mock private PermisosEntitatHelper permisosEntitatHelper;
    @Mock private EntityComprovarHelper entityComprovarHelper;
    @Mock private EntitatHelper entitatHelper;
    @Mock private ConfigHelper configHelper;
    @Mock private AvisRepository avisRepository;

    @InjectMocks
    private EntitatServiceImpl entitatService;

    private EntitatDto entitatDto;
    private EntitatEntity entitatEntity;
    private Authentication mockAuth;
    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        entitatDto = new EntitatDto();
        entitatDto.setId(1L);
        entitatDto.setCodi("ENT001");
        entitatDto.setCodiDir3("A00000000");
        entitatDto.setNom("Entitat Prova");

        entitatEntity = mock(EntitatEntity.class);
        lenient().when(entitatEntity.getId()).thenReturn(1L);
        lenient().when(entitatEntity.getCodi()).thenReturn("ENT001");
        lenient().when(entitatEntity.getCodiDir3()).thenReturn("A00000000");

        mockAuth = mock(Authentication.class);
        lenient().when(mockAuth.getName()).thenReturn("testUser");
        mockSecurityContext = mock(SecurityContext.class);
        lenient().when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
        SecurityContextHolder.setContext(mockSecurityContext);

        lenient().when(entityComprovarHelper.comprovarEntitat(anyLong(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(entitatEntity);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // 1. CREATE
    // =========================================================================
    @Nested
    @DisplayName("Mètode create")
    class CreateTests {

        @Test
        @DisplayName("create: Quan no hi ha logo, llavors crea l'entitat sense gestionar logos")
        void create_QuanNoHiHaLogo_LlavorsCreaSenseLogos() {
            // Arrange
            entitatDto.setLogoCapBytes(null);
            when(entitatRepository.save(any(EntitatEntity.class))).thenReturn(entitatEntity);
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            EntitatDto resultat = entitatService.create(entitatDto);

            // Assert
            assertNotNull(resultat);
            verify(entitatHelper, never()).removeLogos(anyString());
            verify(entitatHelper, never()).createLogo(anyString(), anyString(), any());
            verify(configHelper, times(1)).crearConfigsEntitat("ENT001");
            verify(entitatRepository, times(1)).save(any(EntitatEntity.class));
        }

        @Test
        @DisplayName("create: Quan hi ha logo, llavors esborra els antics i crea el nou")
        void create_QuanHiHaLogo_LlavorsEsborraAnticsICreaNou() {
            // Arrange
            entitatDto.setLogoCapBytes(new byte[]{1, 2, 3});
            entitatDto.setLogoExtension("png");
            when(entitatRepository.save(any(EntitatEntity.class))).thenReturn(entitatEntity);
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            entitatService.create(entitatDto);

            // Assert
            verify(entitatHelper, times(1)).removeLogos("A00000000");
            verify(entitatHelper, times(1)).createLogo("A00000000", "png", new byte[]{1, 2, 3});
        }
    }

    // =========================================================================
    // 2. UPDATE
    // =========================================================================
    @Nested
    @DisplayName("Mètode update")
    class UpdateTests {

        @Test
        @DisplayName("update: Quan eliminarLogoCap és true, llavors esborra els logos")
        void update_QuanEliminarLogoCapTrue_LlavorsEsborraLogos() {
            // Arrange
            entitatDto.setEliminarLogoCap(true);
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            entitatService.update(entitatDto);

            // Assert
            verify(entitatHelper, times(1)).removeLogos("A00000000");
            verify(entitatHelper, never()).createLogo(anyString(), anyString(), any());
            verify(entitatEntity, times(1)).update(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("update: Quan eliminarLogoCap és false i hi ha nou logo, llavors crea el nou logo")
        void update_QuanEliminarLogoCapFalseINouLogo_LlavorsCreaNouLogo() {
            // Arrange
            entitatDto.setEliminarLogoCap(false);
            entitatDto.setLogoCapBytes(new byte[]{4, 5, 6});
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            entitatService.update(entitatDto);

            // Assert
            verify(entitatHelper, never()).removeLogos(anyString());
            verify(entitatHelper, times(1)).createLogo("A00000000", null, new byte[]{4, 5, 6});
        }

        @Test
        @DisplayName("update: Quan eliminarLogoCap és false i NO hi ha nou logo, llavors no toca els logos")
        void update_QuanEliminarLogoCapFalseISenseNouLogo_LlavorsNoTocaLogos() {
            // Arrange
            entitatDto.setEliminarLogoCap(false);
            entitatDto.setLogoCapBytes(null);
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            entitatService.update(entitatDto);

            // Assert
            verify(entitatHelper, never()).removeLogos(anyString());
            verify(entitatHelper, never()).createLogo(anyString(), anyString(), any());
        }
    }

    // =========================================================================
    // 3. UPDATE ACTIVA & DELETE
    // =========================================================================
    @Nested
    @DisplayName("Mètodes updateActiva i delete")
    class UpdateActivaIDeleteTests {

        @Test
        @DisplayName("updateActiva: Quan és vàlid, llavors actualitza l'estat i retorna el DTO")
        void updateActiva_QuanValid_LlavorsActualitzaIRetorna() {
            // Arrange
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            EntitatDto resultat = entitatService.updateActiva(1L, true);

            // Assert
            assertNotNull(resultat);
            verify(entitatEntity, times(1)).updateActiva(true);
        }

        @Test
        @DisplayName("delete: Quan és vàlid, llavors esborra avis, entitat, configs i ACLs")
        void delete_QuanValid_LlavorsEsborraTot() {
            // Arrange
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            EntitatDto resultat = entitatService.delete(1L);

            // Assert
            assertNotNull(resultat);
            verify(avisRepository, times(1)).deleteAllByEntitatId(1L);
            verify(entitatRepository, times(1)).delete(entitatEntity);
            verify(configHelper, times(1)).deleteConfigEntitat("ENT001");
            verify(permisosHelper, times(1)).deleteAcl(1L, EntitatEntity.class);
        }
    }

    // =========================================================================
    // 4. CONSULTES (Find)
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de consulta (Find)")
    class FindTests {

        @Test
        @DisplayName("findById: Quan existeix, llavors converteix i omple permisos")
        void findById_QuanExisteix_LlavorsConverteixIOmplePermisos() {
            // Arrange
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            EntitatDto resultat = entitatService.findById(1L);

            // Assert
            assertNotNull(resultat);
            verify(permisosEntitatHelper, times(1)).omplirPermisosPerEntitat(entitatDto);
        }

        @Test
        @DisplayName("findByIdWithLogo: Quan la recuperació del logo falla, llavors captura l'excepció i retorna el DTO sense petar")
        void findByIdWithLogo_QuanFallaLogo_LlavorsCapturaExcepcioIRetornaDto() {
            // Arrange
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);
            doThrow(new RuntimeException("Error de fitxer")).when(entitatHelper).getLogo("A00000000");

            // Act
            EntitatDto resultat = entitatService.findByIdWithLogo(1L);

            // Assert
            assertNotNull(resultat);
            assertNull(resultat.getLogoCapBytes()); // No s'ha pogut establir
            verify(permisosEntitatHelper, times(1)).omplirPermisosPerEntitat(entitatDto);
        }

        @Test
        @DisplayName("findByCodi: Quan existeix, llavors converteix i omple permisos")
        void findByCodi_QuanExisteix_LlavorsConverteixIOmplePermisos() {
            // Arrange
            when(entitatRepository.findByCodi("ENT001")).thenReturn(entitatEntity);
            when(conversioTipusHelper.convertir(entitatEntity, EntitatDto.class)).thenReturn(entitatDto);

            // Act
            EntitatDto resultat = entitatService.findByCodi("ENT001");

            // Assert
            assertNotNull(resultat);
            verify(permisosEntitatHelper, times(1)).omplirPermisosPerEntitat(entitatDto);
        }

        @Test
        @DisplayName("findByCodi: Quan NO existeix (null), llavors retorna null sense petar")
        void findByCodi_QuanNoExisteix_LlavorsRetornaNull() {
            // Arrange
            when(entitatRepository.findByCodi("NO_EXISTS")).thenReturn(null);

            // Act
            EntitatDto resultat = entitatService.findByCodi("NO_EXISTS");

            // Assert
            assertNull(resultat);
            verify(permisosEntitatHelper, never()).omplirPermisosPerEntitat(any());
        }
    }

    // =========================================================================
    // 5. PAGINACIÓ
    // =========================================================================
    @Nested
    @DisplayName("Mètode findPaginat")
    class FindPaginatTests {

        @Test
        @DisplayName("findPaginat: Quan la paginació està activada, llavors utilitza Pageable")
        void findPaginat_QuanPaginacioActivada_LlavorsUtilitzaPageable() {
            // Arrange
            PaginacioParamsDto params = new PaginacioParamsDto();
            params.setFiltre("test");
            Pageable pageable = mock(Pageable.class);
            Page<EntitatEntity> page = new PageImpl<>(Collections.singletonList(entitatEntity));
            PaginaDto<EntitatDto> paginaDto = new PaginaDto<>();
            paginaDto.setContingut(Collections.singletonList(entitatDto));

            when(paginacioHelper.esPaginacioActivada(params)).thenReturn(true);
            when(paginacioHelper.toSpringDataPageable(params)).thenReturn(pageable);
            when(entitatRepository.findByFiltrePaginat(eq(false), eq("test"), eq(pageable))).thenReturn(page);
            when(paginacioHelper.toPaginaDto(page, EntitatDto.class)).thenReturn(paginaDto);

            // Act
            PaginaDto<EntitatDto> resultat = entitatService.findPaginat(params);

            // Assert
            assertNotNull(resultat);
            verify(permisosEntitatHelper, times(1)).omplirPermisosPerEntitats(paginaDto.getContingut(), true);
        }

        @Test
        @DisplayName("findPaginat: Quan la paginació NO està activada, llavors utilitza Sort")
        void findPaginat_QuanPaginacioNoActivada_LlavorsUtilitzaSort() {
            // Arrange
            PaginacioParamsDto params = new PaginacioParamsDto();
            params.setFiltre(null); // Cobrir la branca de filtre null
            Sort sort = mock(Sort.class);
            List<EntitatEntity> llista = Collections.singletonList(entitatEntity);
            PaginaDto<EntitatDto> paginaDto = new PaginaDto<>();
            paginaDto.setContingut(Collections.singletonList(entitatDto));

            when(paginacioHelper.esPaginacioActivada(params)).thenReturn(false);
            when(paginacioHelper.toSpringDataSort(params)).thenReturn(sort);
            when(entitatRepository.findByFiltrePaginat(eq(true), eq(""), eq(sort))).thenReturn(llista);
            when(paginacioHelper.toPaginaDto(llista, EntitatDto.class)).thenReturn(paginaDto);

            // Act
            PaginaDto<EntitatDto> resultat = entitatService.findPaginat(params);

            // Assert
            assertNotNull(resultat);
            verify(paginacioHelper, never()).toSpringDataPageable(any());
        }
    }

    // =========================================================================
    // 6. USUARI ACTUAL I CACHE
    // =========================================================================
    @Nested
    @DisplayName("Mètodes d'usuari actual i cache")
    class UsuariActualICacheTests {

        @Test
        @DisplayName("findAccessiblesUsuariActual: Quan l'usuari està autenticat, llavors consulta la cache")
        void findAccessiblesUsuariActual_QuanAutenticat_LlavorsConsultaCache() {
            // Arrange
            List<EntitatDto> entitatsCache = Collections.singletonList(entitatDto);
            when(cacheHelper.findEntitatsAccessiblesUsuari("testUser")).thenReturn(entitatsCache);

            // Act
            List<EntitatDto> resultat = entitatService.findAccessiblesUsuariActual();

            // Assert
            assertEquals(1, resultat.size());
            verify(cacheHelper, times(1)).findEntitatsAccessiblesUsuari("testUser");
        }

        @Test
        @DisplayName("findAccessiblesUsuariActual: Quan NO hi ha usuari autenticat, llavors retorna llista buida")
        void findAccessiblesUsuariActual_QuanNoAutenticat_LlavorsRetornaBuida() {
            // Arrange
            when(mockSecurityContext.getAuthentication()).thenReturn(null);

            // Act
            List<EntitatDto> resultat = entitatService.findAccessiblesUsuariActual();

            // Assert
            assertTrue(resultat.isEmpty());
            verify(cacheHelper, never()).findEntitatsAccessiblesUsuari(anyString());
        }

        @Test
        @DisplayName("evictEntitatsAccessiblesUsuari: Quan es crida, llavors esborra la cache")
        void evictEntitatsAccessiblesUsuari_QuanEsCrida_LlavorsEsborraCache() {
            // Act
            entitatService.evictEntitatsAccessiblesUsuari();

            // Assert
            verify(cacheHelper, times(1)).evictAllEntitatsUsuariCache();
        }
    }

    // =========================================================================
    // 7. PERMISOS SUPERUSUARI
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de permisos Superusuari")
    class PermisosSuperTests {

        @Test
        @DisplayName("findPermisSuper: Quan és vàlid, llavors delega a permisosHelper")
        void findPermisSuper_QuanValid_LlavorsDelega() {
            // Arrange
            List<PermisDto> permisos = Collections.singletonList(new PermisDto());
            when(permisosHelper.findPermisos(1L, EntitatEntity.class)).thenReturn(permisos);

            // Act
            List<PermisDto> resultat = entitatService.findPermisSuper(1L);

            // Assert
            assertEquals(1, resultat.size());
            verify(permisosHelper, times(1)).findPermisos(1L, EntitatEntity.class);
        }

        @Test
        @DisplayName("deletePermisSuper: Quan és vàlid, llavors delega a permisosHelper")
        void deletePermisSuper_QuanValid_LlavorsDelega() {
            // Act
            entitatService.deletePermisSuper(1L, 99L);

            // Assert
            verify(permisosHelper, times(1)).deletePermis(1L, EntitatEntity.class, 99L);
        }
    }

    // =========================================================================
    // 8. PERMISOS ADMINISTRADOR (Amb verificació de seguretat)
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de permisos Administrador")
    class PermisosAdminTests {

        @Test
        @DisplayName("findPermisAdmin: Quan té permisos, llavors retorna la llista de permisos")
        void findPermisAdmin_QuanTePermisos_LlavorsRetornaLlista() {
            // Arrange
            when(permisosHelper.isGrantedAny(eq(1L), eq(EntitatEntity.class),
                    argThat(arr -> arr.length == 2 &&
                            arr[0] == ExtendedPermission.ADMINISTRATION &&
                            arr[1] == ExtendedPermission.ADMIN_LECTURA),
                    eq(mockAuth)))
                    .thenReturn(true);
            when(permisosHelper.findPermisos(1L, EntitatEntity.class)).thenReturn(Collections.singletonList(new PermisDto()));

            // Act
            List<PermisDto> resultat = entitatService.findPermisAdmin(1L);

            // Assert
            assertEquals(1, resultat.size());
        }

        @Test
        @DisplayName("findPermisAdmin: Quan NO té permisos, llavors llança SecurityException")
        void findPermisAdmin_QuanNoTePermisos_LlavorsLlancaSecurityException() {
            // Arrange
            when(permisosHelper.isGrantedAny(anyLong(), eq(EntitatEntity.class), any(), eq(mockAuth))).thenReturn(false);

            // Act & Assert
            SecurityException ex = assertThrows(SecurityException.class, () -> {
                entitatService.findPermisAdmin(1L);
            });
            assertEquals("Sense permisos per administrar aquesta entitat", ex.getMessage());
        }

        @Test
        @DisplayName("deletePermisAdmin: Quan té permisos (ADMINISTRATION), llavors esborra el permís")
        void deletePermisAdmin_QuanTePermisos_LlavorsEsborra() {
            // Arrange: Nota que deletePermisAdmin només comprova ADMINISTRATION, no ADMIN_LECTURA
            when(permisosHelper.isGrantedAny(eq(1L), eq(EntitatEntity.class),
                    argThat(arr -> arr.length == 1 && arr[0] == ExtendedPermission.ADMINISTRATION),
                    eq(mockAuth)))
                    .thenReturn(true);

            // Act
            entitatService.deletePermisAdmin(1L, 99L);

            // Assert
            verify(permisosHelper, times(1)).deletePermis(1L, EntitatEntity.class, 99L);
        }

        @Test
        @DisplayName("deletePermisAdmin: Quan NO té permisos, llavors llança SecurityException")
        void deletePermisAdmin_QuanNoTePermisos_LlavorsLlancaSecurityException() {
            // Arrange
            when(permisosHelper.isGrantedAny(anyLong(), eq(EntitatEntity.class), any(), eq(mockAuth))).thenReturn(false);

            // Act & Assert
            assertThrows(SecurityException.class, () -> {
                entitatService.deletePermisAdmin(1L, 99L);
            });
        }
    }
}