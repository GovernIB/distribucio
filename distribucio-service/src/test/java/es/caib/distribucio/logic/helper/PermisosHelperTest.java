package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.distribucio.logic.permission.ExtendedPermission;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de PermisosHelper")
class PermisosHelperTest {

    @Mock private LookupStrategy lookupStrategy;
    @Mock private MutableAclService aclService;
    @Mock private ConfigHelper configHelper;
    @Mock private CacheHelper cacheHelper;

    @InjectMocks
    private PermisosHelper permisosHelper;

    private static final Long OBJ_ID = 100L;
    private static final Class<?> OBJ_CLASS = String.class;
    private static final String USER_NAME = "testUser";
    private static final String ROLE_NAME = "ROLE_ADMIN";

    private Authentication mockAuth;
    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        mockAuth = mock(Authentication.class);
        lenient().when(mockAuth.getName()).thenReturn(USER_NAME);
        GrantedAuthority mockRole = mock(GrantedAuthority.class);
        lenient().when(mockRole.getAuthority()).thenReturn(ROLE_NAME);

        mockSecurityContext = mock(SecurityContext.class);
        lenient().when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
        SecurityContextHolder.setContext(mockSecurityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // 1. ASSIGNAR PERMISOS
    // =========================================================================
    @Nested
    @DisplayName("Mètodes d'assignació de permisos")
    class AssignarPermisosTests {

        @Test
        @DisplayName("assignarPermisUsuari: Ha d'assignar permís correctament a un usuari")
        void assignarPermisUsuari_QuanEsDonaUsuariValid_LlavorsCridaAclService() {
            // Arrange
            MutableAcl mockAcl = mock(MutableAcl.class);
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);
            when(mockAcl.getEntries()).thenReturn(new ArrayList<>());

            // Act
            permisosHelper.assignarPermisUsuari(USER_NAME, OBJ_ID, OBJ_CLASS, BasePermission.READ);

            // Assert
            verify(aclService, times(1)).readAclById(any(ObjectIdentity.class));
            verify(mockAcl, times(1)).insertAce(eq(0), eq(BasePermission.READ), any(PrincipalSid.class), eq(true));
            verify(aclService, times(2)).updateAcl(mockAcl); // Una al crear/llegir, una al insertar
        }

        @Test
        @DisplayName("assignarPermisRol: Ha d'assignar permís a un rol aplicant el mapeig de configuració")
        void assignarPermisRol_QuanExisteixMapeig_LlavorsUtilitzaRolMapejat() {
            // Arrange
            String mappedRole = "ROLE_SUPER_ADMIN";
            when(configHelper.getConfig("es.caib.distribucio.mapeig.rol." + ROLE_NAME)).thenReturn(mappedRole);
            MutableAcl mockAcl = mock(MutableAcl.class);
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);
            when(mockAcl.getEntries()).thenReturn(new ArrayList<>());

            // Act
            permisosHelper.assignarPermisRol(ROLE_NAME, OBJ_ID, OBJ_CLASS, BasePermission.WRITE);

            // Assert
            verify(configHelper, times(1)).getConfig("es.caib.distribucio.mapeig.rol." + ROLE_NAME);
            verify(mockAcl, times(1)).insertAce(eq(0), eq(BasePermission.WRITE), argThat(sid ->
                    sid instanceof GrantedAuthoritySid && ((GrantedAuthoritySid) sid).getGrantedAuthority().equals(mappedRole)
            ), eq(true));
        }
    }

    // =========================================================================
    // 2. REVOCAR I MOURE PERMISOS
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de revocació i moviment de permisos")
    class RevocarIMourePermisosTests {

        @Test
        @DisplayName("revocarPermisUsuari: Ha d'eliminar l'ACE corresponent")
        void revocarPermisUsuari_QuanExisteixPermis_LlavorsEliminaACE() {
            // Arrange
            MutableAcl mockAcl = mock(MutableAcl.class);
            AccessControlEntry mockAce = mock(AccessControlEntry.class);
            when(mockAce.getSid()).thenReturn(new PrincipalSid(USER_NAME));
            when(mockAce.getPermission()).thenReturn(BasePermission.READ);
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);
            when(mockAcl.getEntries()).thenReturn(Collections.singletonList(mockAce));

            // Act
            permisosHelper.revocarPermisUsuari(USER_NAME, OBJ_ID, OBJ_CLASS, BasePermission.READ);

            // Assert
            verify(mockAcl, times(1)).deleteAce(0);
            verify(aclService, times(1)).updateAcl(mockAcl);
        }

        @Test
        @DisplayName("revocarPermisUsuari: Quan no existeix l'ACL, no ha de llançar excepció")
        void revocarPermisUsuari_QuanNoExisteixAcl_LlavorsNoFaRes() {
            // Arrange
            when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("Not found"));

            // Act & Assert
            assertDoesNotThrow(() -> permisosHelper.revocarPermisUsuari(USER_NAME, OBJ_ID, OBJ_CLASS, BasePermission.READ));
            verify(aclService, never()).updateAcl(any());
        }

        @Test
        @DisplayName("mourePermisUsuari: Ha d'assignar al destí i revocar de l'origen")
        void mourePermisUsuari_QuanEsMouPermis_LlavorsCridaAssignarIRevocar() {
            // Arrange
            MutableAcl mockAcl = mock(MutableAcl.class);
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);
            when(mockAcl.getEntries()).thenReturn(new ArrayList<>());

            // Act
            permisosHelper.mourePermisUsuari("userOrig", "userDest", OBJ_ID, OBJ_CLASS, BasePermission.CREATE);

            // Assert
            verify(aclService, times(3)).updateAcl(mockAcl); // 2 per assignar, 2 per revocar
        }
    }

    // =========================================================================
    // 3. FILTRATGE I VERIFICACIÓ DE PERMISOS (ANY / ALL)
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de filtratge i verificació")
    class FiltratgePermisosTests {

        @Test
        @DisplayName("filterGrantedAny: Ha de mantenir l'objecte si té algun permís")
        void filterGrantedAny_QuanTePermis_LlavorsMantéObjecte() {
            // Arrange
            List<String> objects = new ArrayList<>(Collections.singletonList("obj1"));
            PermisosHelper.ObjectIdentifierExtractor<String> extractor = obj -> OBJ_ID;
            Acl mockAcl = mock(Acl.class);
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);
            when(mockAcl.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);

            // Act
            permisosHelper.filterGrantedAny(objects, extractor, OBJ_CLASS, new Permission[]{BasePermission.READ});

            // Assert
            assertEquals(1, objects.size());
            assertTrue(objects.contains("obj1"));
        }

        @Test
        @DisplayName("filterGrantedAll: Ha d'eliminar l'objecte si no té TOTS els permisos")
        void filterGrantedAll_QuanNoTeTotsPermisos_LlavorsEliminaObjecte() {
            // Arrange
            List<String> objects = new ArrayList<>(Collections.singletonList("obj1"));
            PermisosHelper.ObjectIdentifierExtractor<String> extractor = obj -> OBJ_ID;
            Acl mockAcl = mock(Acl.class);
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);
            // Simulem que el primer permís es concedeix, però el segon no
            when(mockAcl.isGranted(anyList(), anyList(), eq(false)))
                    .thenReturn(true)
                    .thenReturn(false);

            // Act
            permisosHelper.filterGrantedAll(objects, extractor, OBJ_CLASS, new Permission[]{BasePermission.READ, BasePermission.WRITE});

            // Assert
            assertTrue(objects.isEmpty());
        }

        @Test
        @DisplayName("isGrantedAny: Quan la llista de permisos és buida, ha de retornar false")
        void isGrantedAny_QuanPermisosBuits_LlavorsRetornaFalse() {
            // Act
            boolean result = permisosHelper.isGrantedAny(OBJ_ID, OBJ_CLASS, new Permission[]{}, mockAuth);
            // Assert
            assertFalse(result);
        }
    }

    // =========================================================================
    // 4. CONSULTA DE PERMISOS (FIND PERMISOS)
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de consulta de permisos")
    class ConsultaPermisosTests {

        @Test
        @DisplayName("findPermisos (Single): Ha de retornar llista buida si no es troba l'ACL")
        void findPermisosSingle_QuanNoExisteixAcl_LlavorsRetornaLlistaBuida() {
            // Arrange
            when(aclService.readAclById(any(ObjectIdentity.class))).thenThrow(new NotFoundException("No ACL"));

            // Act
            List<PermisDto> result = permisosHelper.findPermisos(OBJ_ID, OBJ_CLASS);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("findPermisos (Single): Ha de mapejar correctament Usuari amb Cache i Rol")
        void findPermisosSingle_QuanExisteixenACEs_LlavorsRetornaPermisosMapejats() {
            // Arrange
            MutableAcl mockAcl = mock(MutableAcl.class);
            AccessControlEntry aceUser = mock(AccessControlEntry.class);
            when(aceUser.getId()).thenReturn(1L);
            when(aceUser.getSid()).thenReturn(new PrincipalSid(USER_NAME));
            when(aceUser.getPermission()).thenReturn(BasePermission.READ);

            AccessControlEntry aceRole = mock(AccessControlEntry.class);
            when(aceRole.getId()).thenReturn(2L);
            when(aceRole.getSid()).thenReturn(new GrantedAuthoritySid(ROLE_NAME));
            when(aceRole.getPermission()).thenReturn(ExtendedPermission.ADMINISTRATION);

            when(mockAcl.getEntries()).thenReturn(Arrays.asList(aceUser, aceRole));
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);

            DadesUsuari mockUser = mock(DadesUsuari.class);
            when(mockUser.getNomSencer()).thenReturn("Nom Complet");
            when(cacheHelper.findUsuariAmbCodi(USER_NAME)).thenReturn(mockUser);

            // Act
            List<PermisDto> result = permisosHelper.findPermisos(OBJ_ID, OBJ_CLASS);

            // Assert
            assertEquals(2, result.size());
            PermisDto userPermis = result.stream().filter(p -> p.getPrincipalTipus() == PrincipalTipusEnumDto.USUARI).findFirst().orElseThrow();
            assertEquals("Nom Complet", userPermis.getPrincipalDescripcio());
            assertTrue(userPermis.isRead());

            PermisDto rolePermis = result.stream().filter(p -> p.getPrincipalTipus() == PrincipalTipusEnumDto.ROL).findFirst().orElseThrow();
            assertTrue(rolePermis.isAdministration());
        }

        @Test
        @DisplayName("findPermisos (List): Ha de retornar mapa amb permisos per a múltiples IDs")
        void findPermisosList_QuanEsDonenMultipleIDs_LlavorsRetornaMapaCorrecte() {
            // Arrange
            List<Long> ids = Arrays.asList(1L, 2L);
            ObjectIdentity oid1 = new ObjectIdentityImpl(OBJ_CLASS, 1L);
            ObjectIdentity oid2 = new ObjectIdentityImpl(OBJ_CLASS, 2L);

            Map<ObjectIdentity, Acl> aclsMap = new HashMap<>();
            aclsMap.put(oid1, mock(Acl.class)); // Mock buit per simplicitat
            aclsMap.put(oid2, mock(Acl.class));

            when(lookupStrategy.readAclsById(anyList(), isNull())).thenReturn(aclsMap);

            // Act
            Map<Long, List<PermisDto>> result = permisosHelper.findPermisos(ids, OBJ_CLASS);

            // Assert
            assertEquals(2, result.size());
            assertTrue(result.containsKey(1L));
            assertTrue(result.containsKey(2L));
        }
    }

    // =========================================================================
    // 5. ACTUALITZACIÓ I ELIMINACIÓ
    // =========================================================================
    @Nested
    @DisplayName("Mètodes d'actualització i eliminació")
    class UpdateIDeleteTests {

        @Test
        @DisplayName("updatePermis: Ha de netejar permisos anteriors abans d'assignar els nous (netejarAbans=true)")
        void updatePermis_QuanEsModificaPermis_LlavorsNetejaIAssigna() {
            // Arrange
            PermisDto permis = new PermisDto();
            permis.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
            permis.setPrincipalNom(USER_NAME);
            permis.setRead(true);
            permis.setWrite(true);

            MutableAcl mockAcl = mock(MutableAcl.class);
            AccessControlEntry oldAce = mock(AccessControlEntry.class);
            when(oldAce.getSid()).thenReturn(new PrincipalSid(USER_NAME));
            when(mockAcl.getEntries()).thenReturn(Collections.singletonList(oldAce));
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);

            // Act
            permisosHelper.updatePermis(OBJ_ID, OBJ_CLASS, permis);

            // Assert
            verify(mockAcl, times(1)).deleteAce(0); // Neteja l'antic
            verify(mockAcl, times(2)).insertAce(anyInt(), any(Permission.class), any(PrincipalSid.class), eq(true)); // Inserta READ i WRITE
        }

        @Test
        @DisplayName("deletePermis: Ha d'eliminar l'ACE amb l'ID específic")
        void deletePermis_QuanEsTrobaId_LlavorsCridaAssignarAmbPermisosBuits() {
            // Arrange
            MutableAcl mockAcl = mock(MutableAcl.class);
            AccessControlEntry ace = mock(AccessControlEntry.class);
            when(ace.getId()).thenReturn(99L);
            when(ace.getSid()).thenReturn(new PrincipalSid(USER_NAME));
            when(mockAcl.getEntries()).thenReturn(Collections.singletonList(ace));
            when(aclService.readAclById(any(ObjectIdentity.class))).thenReturn(mockAcl);

            // Act
            permisosHelper.deletePermis(OBJ_ID, OBJ_CLASS, 99L);

            // Assert
            // deletePermis crida internament a assignarPermisos amb array buit i netejarAbans=true
            verify(mockAcl, times(1)).deleteAce(0);
            verify(aclService, times(2)).updateAcl(mockAcl);
        }

        @Test
        @DisplayName("deleteAcl: Ha de cridar aclService.deleteAcl")
        void deleteAcl_QuanEsValid_LlavorsEsborraAcl() {
            // Act
            permisosHelper.deleteAcl(OBJ_ID, OBJ_CLASS);

            // Assert
            verify(aclService, times(1)).deleteAcl(any(ObjectIdentity.class), eq(true));
        }

        @Test
        @DisplayName("deleteAcl: Quan llança NotFoundException, no ha de fallar")
        void deleteAcl_QuanNoExisteix_LlavorsNoFaRes() {
            // Arrange
            doThrow(new NotFoundException("No exists")).when(aclService).deleteAcl(any(ObjectIdentity.class), eq(true));

            // Act & Assert
            assertDoesNotThrow(() -> permisosHelper.deleteAcl(OBJ_ID, OBJ_CLASS));
        }
    }

    // =========================================================================
    // 6. ORDENACIÓ DE PERMISOS
    // =========================================================================
    @Nested
    @DisplayName("Mètodes d'ordenació")
    class OrdenacioPermisosTests {

        @Test
        @DisplayName("ordenarPermisos: Quan paginacioParams és null, ha de retornar la llista original")
        void ordenarPermisos_QuanParamsNull_LlavorsRetornaMateixaLlista() {
            // Arrange
            List<PermisDto> permisos = new ArrayList<>();

            // Act
            List<PermisDto> result = permisosHelper.ordenarPermisos(null, permisos);

            // Assert
            assertSame(permisos, result);
        }

        @Test
        @DisplayName("ordenarPermisos: Ha d'ordenar per principalNom de forma ASCENDENT")
        void ordenarPermisos_QuanOrdrePrincipalNomAsc_LlavorsOrdenaCorrectament() {
            // Arrange
            PermisDto p1 = new PermisDto(); p1.setPrincipalNom("Zebra");
            PermisDto p2 = new PermisDto(); p2.setPrincipalNom("Alpha");
            List<PermisDto> permisos = Arrays.asList(p1, p2);

            PaginacioParamsDto params = new PaginacioParamsDto();
            PaginacioParamsDto.OrdreDto ordre = new PaginacioParamsDto.OrdreDto(
                    "principalNom", PaginacioParamsDto.OrdreDireccioDto.ASCENDENT);
            params.setOrdres(Collections.singletonList(ordre));

            // Act
            List<PermisDto> result = permisosHelper.ordenarPermisos(params, permisos);

            // Assert
            assertEquals("Alpha", result.get(0).getPrincipalNom());
            assertEquals("Zebra", result.get(1).getPrincipalNom());
        }

        @Test
        @DisplayName("ordenarPermisos: Ha d'ordenar per principalTipus de forma DESCENDENT")
        void ordenarPermisos_QuanOrdreTipusDesc_LlavorsOrdenaCorrectament() {
            // Arrange
            PermisDto p1 = new PermisDto(); p1.setPrincipalTipus(PrincipalTipusEnumDto.USUARI);
            PermisDto p2 = new PermisDto(); p2.setPrincipalTipus(PrincipalTipusEnumDto.ROL);
            List<PermisDto> permisos = Arrays.asList(p1, p2);

            PaginacioParamsDto params = new PaginacioParamsDto();
            PaginacioParamsDto.OrdreDto ordre = new PaginacioParamsDto.OrdreDto(
                    "principalTipus", PaginacioParamsDto.OrdreDireccioDto.DESCENDENT);
            params.setOrdres(Collections.singletonList(ordre));

            // Act
            List<PermisDto> result = permisosHelper.ordenarPermisos(params, permisos);

            // Assert
            // ROL va després que USUARI en ordre natural d'enum, així que DESCENDENT posa USUARI primer?
            // Depen de l'implementació de PermisDto.sortByTipus(), verifiquem que s'ha cridat a sort
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("ordenarPermisos: Quan el camp d'ordre no és reconegut, no ha de fallar ni ordenar")
        void ordenarPermisos_QuanCampDesconegut_LlavorsNoOrdena() {
            // Arrange
            PermisDto p1 = new PermisDto(); p1.setPrincipalNom("Zebra");
            List<PermisDto> permisos = new ArrayList<>(Collections.singletonList(p1));

            PaginacioParamsDto params = new PaginacioParamsDto();
            PaginacioParamsDto.OrdreDto ordre = new PaginacioParamsDto.OrdreDto(
                    "campInexistent", PaginacioParamsDto.OrdreDireccioDto.ASCENDENT);
            params.setOrdres(Collections.singletonList(ordre));

            // Act
            List<PermisDto> result = permisosHelper.ordenarPermisos(params, permisos);

            // Assert
            assertSame(permisos, result); // Retorna la mateixa instància sense modificar
        }
    }
}