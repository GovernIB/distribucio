package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.persist.entity.*;
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
                eq(dto.getTramitCodi() == null),
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
}