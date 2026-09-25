package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.persist.entity.*;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.ReglaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReglaHelperTest {

    @Mock private ConfigHelper configHelper;
    @Mock private ReglaRepository reglaRepository;
    @Mock private BustiaRepository bustiaRepository;
    @Mock private BustiaHelper bustiaHelper;
    @Mock private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

    @InjectMocks
    private ReglaHelper reglaHelper;

    private final EntitatEntity entitatActual = new EntitatEntity();
    private final RegistreSimulatDto dto = new RegistreSimulatDto();
    private final List<RegistreSimulatAccionDto> simulatAccions = new ArrayList<>();
    private final List<ReglaEntity> regles = new ArrayList<>();

    @BeforeEach
    void setUp() {
//        lenient().when(configHelper.getAsBoolean(anyString(), anyBoolean())).thenAnswer(i->i.getArguments()[1]);
        lenient().when(reglaRepository.findAplicables(
                eq(entitatActual),
                eq(dto.getUnitatId()),
                eq(dto.getBustiaId()),
                eq(dto.getProcedimentCodi() != null ? dto.getProcedimentCodi() : ""),
                eq(dto.getServeiCodi() != null ? dto.getServeiCodi() : ""),
                eq(dto.getTramitCodi() != null ? dto.getTramitCodi() : ""),
                eq(dto.getAssumpteCodi() != null ? dto.getAssumpteCodi() : ""),
                eq(dto.getPresencial() == null),
                eq(dto.getPresencial())
        )).thenReturn(regles);
    }

    @Test
    void testAplicarSimulation_BACKOFFICE() {
        // Given
        ReglaEntity regla = new ReglaEntity();
        ReflectionTestUtils.setField(regla, "nom", "Regla");
        ReflectionTestUtils.setField(regla, "tipus", ReglaTipusEnumDto.BACKOFFICE);
        regles.add(regla);

        BackofficeEntity backoffice = new BackofficeEntity();
        backoffice.setNom("Backoffice");
        ReflectionTestUtils.setField(regla, "backofficeDesti", backoffice);


        // When
        reglaHelper.aplicarSimulation(
                entitatActual,
                dto,
                new ArrayList<ReglaEntity>(),
                simulatAccions,
                (dto.getPresencial() != null
                        ?ReglaPresencialEnumDto.SI.equals(dto.getPresencial())
                        :null)
        );

        // Then
        assertFalse(simulatAccions.isEmpty());
        assertEquals("Regla", simulatAccions.get(0).getReglaNom());
        assertEquals("Backoffice", simulatAccions.get(0).getParam());
    }

    @Test
    void testAplicarSimulation_UNITAT() {
        // Given
        ReglaEntity regla = new ReglaEntity();
        ReflectionTestUtils.setField(regla, "nom", "Regla");
        ReflectionTestUtils.setField(regla, "tipus", ReglaTipusEnumDto.UNITAT);
        ReflectionTestUtils.setField(regla, "entitat", entitatActual);
        regles.add(regla);

        UnitatOrganitzativaEntity unitat = UnitatOrganitzativaEntity.getBuilder("Unitat", "Denominació").build();
        ReflectionTestUtils.setField(unitat, "id", 10L);
        regla.setUnitatDesti(unitat);

        BustiaEntity bustia = BustiaEntity.getBuilder(entitatActual, "Bustia", unitat.getCodi(), unitat, null).build();

        when(bustiaHelper.findBustiaDesti(eq(entitatActual), eq(unitat.getCodi()))).thenReturn(bustia);

        // When
        reglaHelper.aplicarSimulation(
                entitatActual,
                dto,
                new ArrayList<ReglaEntity>(),
                simulatAccions,
                (dto.getPresencial() != null
                        ?ReglaPresencialEnumDto.SI.equals(dto.getPresencial())
                        :null)
        );

        // Then
        assertFalse(simulatAccions.isEmpty());
        assertEquals(10L, dto.getUnitatId());
        assertEquals("Regla", simulatAccions.get(0).getReglaNom());
        assertEquals("Unitat - Denominació", simulatAccions.get(0).getParam());
        assertNull(simulatAccions.get(1).getReglaNom());
        assertEquals("Bustia", simulatAccions.get(1).getParam());
    }

    @Test
    void testAplicarSimulation_BUSTIA() {
        // Given
        ReglaEntity regla = new ReglaEntity();
        ReflectionTestUtils.setField(regla, "nom", "Regla");
        ReflectionTestUtils.setField(regla, "tipus", ReglaTipusEnumDto.BUSTIA);
        regles.add(regla);

        UnitatOrganitzativaEntity unitat = UnitatOrganitzativaEntity.getBuilder("Unitat", "Denominació").build();
        ReflectionTestUtils.setField(unitat, "id", 10L);
        regla.setUnitatDesti(unitat);

        BustiaEntity bustia = BustiaEntity.getBuilder(entitatActual, "Bustia", unitat.getCodi(), unitat, null).build();
        ReflectionTestUtils.setField(bustia, "id", 20L);
        ReflectionTestUtils.setField(regla, "bustiaDesti", bustia);


        // When
        reglaHelper.aplicarSimulation(
                entitatActual,
                dto,
                new ArrayList<ReglaEntity>(),
                simulatAccions,
                (dto.getPresencial() != null
                        ?ReglaPresencialEnumDto.SI.equals(dto.getPresencial())
                        :null)
        );

        // Then
        assertFalse(simulatAccions.isEmpty());
        assertEquals("Regla", simulatAccions.get(0).getReglaNom());
        assertEquals("Bustia", simulatAccions.get(0).getParam());
        assertEquals(10L, dto.getUnitatId());
        assertEquals(20L, dto.getBustiaId());
    }

    // ---------- Simular (orquestració) ----------

    private UnitatOrganitzativaEntity unitatSimulacio(String codiDir3Entitat) {
        UnitatOrganitzativaEntity unitat = UnitatOrganitzativaEntity.getBuilder("U1", "Unitat").build();
        ReflectionTestUtils.setField(unitat, "id", 10L);
        unitat.setCodiDir3Entitat(codiDir3Entitat);
        return unitat;
    }

    private BustiaEntity bustiaSimulacio(UnitatOrganitzativaEntity unitat, Long id) {
        BustiaEntity bustia = BustiaEntity.getBuilder(entitatActual, "Bustia", unitat.getCodi(), unitat, null).build();
        ReflectionTestUtils.setField(bustia, "id", id);
        return bustia;
    }

    private void entitatSimulacio() {
        ReflectionTestUtils.setField(entitatActual, "id", 1L);
        ReflectionTestUtils.setField(entitatActual, "codiDir3", "E1");
    }

    @Test
    void testSimular_senseBustia_primeraAccioEsBustiaPerDefecte() {
        entitatSimulacio();
        UnitatOrganitzativaEntity unitat = unitatSimulacio("E1");
        BustiaEntity bustia = bustiaSimulacio(unitat, 20L);
        when(unitatOrganitzativaRepository.findById(10L)).thenReturn(Optional.of(unitat));
        when(bustiaHelper.findBustiaDesti(entitatActual, "U1")).thenReturn(bustia);
        when(configHelper.getAsBoolean(anyString(), anyBoolean())).thenReturn(true);
        RegistreSimulatDto entrada = new RegistreSimulatDto();
        entrada.setUnitatId(10L);

        List<RegistreSimulatAccionDto> accions = reglaHelper.simular(entitatActual, entrada);

        assertFalse(accions.isEmpty());
        assertEquals(RegistreSimulatAccionEnumDto.BUSTIA_PER_DEFECTE, accions.get(0).getAccion());
        assertEquals("Bustia", accions.get(0).getParam());
        assertNull(accions.get(0).getReglaNom());
    }

    @Test
    void testSimular_bustiaInexistent_llancaNotFound() {
        entitatSimulacio();
        UnitatOrganitzativaEntity unitat = unitatSimulacio("E1");
        when(unitatOrganitzativaRepository.findById(10L)).thenReturn(Optional.of(unitat));
        when(bustiaRepository.findById(99L)).thenReturn(Optional.empty());
        RegistreSimulatDto entrada = new RegistreSimulatDto();
        entrada.setUnitatId(10L);
        entrada.setBustiaId(99L);

        assertThrows(NotFoundException.class, () -> reglaHelper.simular(entitatActual, entrada));
    }

    @Test
    void testSimular_unitatDAltraEntitat_llancaValidation() {
        entitatSimulacio();
        UnitatOrganitzativaEntity unitat = unitatSimulacio("ALTRA");
        when(unitatOrganitzativaRepository.findById(10L)).thenReturn(Optional.of(unitat));
        RegistreSimulatDto entrada = new RegistreSimulatDto();
        entrada.setUnitatId(10L);

        assertThrows(ValidationException.class, () -> reglaHelper.simular(entitatActual, entrada));
    }

    @Test
    void testSimular_presencialSiIntentaRegles_iNoMutaLEntrada() {
        entitatSimulacio();
        UnitatOrganitzativaEntity unitat = unitatSimulacio("E1");
        BustiaEntity bustia = bustiaSimulacio(unitat, 20L);
        when(unitatOrganitzativaRepository.findById(10L)).thenReturn(Optional.of(unitat));
        when(bustiaRepository.findById(20L)).thenReturn(Optional.of(bustia));
        when(configHelper.getAsBoolean(anyString(), anyBoolean())).thenReturn(true);
        RegistreSimulatDto entrada = new RegistreSimulatDto();
        entrada.setUnitatId(10L);
        entrada.setBustiaId(20L);
        entrada.setPresencial(ReglaPresencialEnumDto.SI);

        List<RegistreSimulatAccionDto> accions = reglaHelper.simular(entitatActual, entrada);

        // Cap regla aplicable i bústia informada: no hi ha cap acció.
        assertTrue(accions.isEmpty());
        verify(reglaRepository).findAplicables(
                eq(entitatActual), eq(10L), eq(20L), eq(""), eq(""), eq(""), eq(""), eq(false), eq(ReglaPresencialEnumDto.SI));
        // El DTO rebut no queda modificat.
        assertEquals(10L, entrada.getUnitatId());
        assertEquals(20L, entrada.getBustiaId());
    }

    @Test
    void testSimular_sensePresencial_noFiltraPerPresencial() {
        entitatSimulacio();
        UnitatOrganitzativaEntity unitat = unitatSimulacio("E1");
        BustiaEntity bustia = bustiaSimulacio(unitat, 20L);
        when(unitatOrganitzativaRepository.findById(10L)).thenReturn(Optional.of(unitat));
        when(bustiaRepository.findById(20L)).thenReturn(Optional.of(bustia));
        when(configHelper.getAsBoolean(anyString(), anyBoolean())).thenReturn(true);
        RegistreSimulatDto entrada = new RegistreSimulatDto();
        entrada.setUnitatId(10L);
        entrada.setBustiaId(20L);

        reglaHelper.simular(entitatActual, entrada);

        verify(reglaRepository).findAplicables(
                eq(entitatActual), eq(10L), eq(20L), eq(""), eq(""), eq(""), eq(""), eq(true), isNull());
    }

    // ---------- Aplicar manualment ----------

    @Test
    void testFindRegistresAplicables_senseFiltres_usaPlaceholders() {
        // Given: regla sense cap filtre (ni SIA, ni unitat, ni bústia, ni assumpte, ni presencial)
        ReglaEntity regla = new ReglaEntity();
        List<RegistreEntity> esperats = new ArrayList<>();
        when(reglaRepository.findRegistres(
                eq(entitatActual),
                eq(true),
                eq(List.of(0L)),
                eq(true),
                eq(false),
                eq(true),
                eq(0L),
                eq(List.of("-")),
                eq(List.of("-")),
                eq(true),
                eq("-"))).thenReturn(esperats);

        // When
        List<RegistreEntity> resultat = reglaHelper.findRegistresAplicables(entitatActual, regla);

        // Then: comportament històric, una regla sense procediment ni servei no troba res (placeholder "-")
        assertSame(esperats, resultat);
    }

    @Test
    void testFindRegistresAplicables_codisSeparatsPerEspai() {
        // Given
        ReglaEntity regla = new ReglaEntity();
        ReflectionTestUtils.setField(regla, "procedimentCodiFiltre", "111 222");
        ReflectionTestUtils.setField(regla, "serveiCodiFiltre", "333");
        ReflectionTestUtils.setField(regla, "assumpteCodiFiltre", "ASS");
        ReflectionTestUtils.setField(regla, "presencial", ReglaPresencialEnumDto.SI);
        List<RegistreEntity> esperats = new ArrayList<>();
        when(reglaRepository.findRegistres(
                eq(entitatActual),
                eq(true),
                eq(List.of(0L)),
                eq(false),
                eq(true),
                eq(true),
                eq(0L),
                eq(List.of("111", "222")),
                eq(List.of("333")),
                eq(false),
                eq("ASS"))).thenReturn(esperats);

        // When / Then
        assertSame(esperats, reglaHelper.findRegistresAplicables(entitatActual, regla));
    }

    @Test
    void testAplicarManualment_assignaLaReglaIRetornaNumeros() {
        // Given
        ReglaEntity regla = new ReglaEntity();
        RegistreEntity r1 = mock(RegistreEntity.class);
        RegistreEntity r2 = mock(RegistreEntity.class);
        when(r1.getNumero()).thenReturn("N1");
        when(r2.getNumero()).thenReturn("N2");
        when(reglaRepository.findRegistres(any(), anyBoolean(), any(), anyBoolean(), anyBoolean(), anyBoolean(), any(), any(), any(), anyBoolean(), any())).
                thenReturn(List.of(r1, r2));

        // When
        List<String> numeros = reglaHelper.aplicarManualment(entitatActual, regla);

        // Then
        assertEquals(List.of("N1", "N2"), numeros);
        verify(r1).updateRegla(regla);
        verify(r2).updateRegla(regla);
    }
}
