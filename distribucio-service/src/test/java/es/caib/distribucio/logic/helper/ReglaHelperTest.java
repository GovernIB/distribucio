package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
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

@ExtendWith(MockitoExtension.class)
class ReglaHelperTest {

    @Mock private ConfigHelper configHelper;
    @Mock private ReglaRepository reglaRepository;
    @Mock private BustiaRepository bustiaRepository;
    @Mock private BustiaHelper bustiaHelper;

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

        when(configHelper.getAsBoolean(eq("es.caib.distribucio.tasca.aplicar.regles.avaluar.totes"), anyBoolean())).thenReturn(true);

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

        when(configHelper.getAsBoolean(eq("es.caib.distribucio.tasca.aplicar.regles.avaluar.totes"), anyBoolean())).thenReturn(true);
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

        when(configHelper.getAsBoolean(eq("es.caib.distribucio.tasca.aplicar.regles.avaluar.totes"), anyBoolean())).thenReturn(true);

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
