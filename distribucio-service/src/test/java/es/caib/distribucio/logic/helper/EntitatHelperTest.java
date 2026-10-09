package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.exception.PropietatNotFoundException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de EntitatHelper")
class EntitatHelperTest {

    @Mock private ConfigHelper configHelper;
    @Mock private EntitatRepository entitatRepository;

    @InjectMocks
    private EntitatHelper entitatHelper;

    // @TempDir crea un directori temporal aïllat que s'esborra automàticament després del test
    @TempDir
    Path tempDir;

    private static final String ENTITAT_CODI = "ENT001";
    private static final byte[] LOGO_BYTES = "Contingut del logo en bytes".getBytes();

    @BeforeEach
    void setUp() {
        // Configurar el comportament per defecte: el directori de logos és el nostre directori temporal
        lenient().when(configHelper.getConfig("es.caib.distribucio.entitat.logos.base.dir"))
                .thenReturn(tempDir.toString());
    }

    // =========================================================================
    // 1. CREACIÓ DE LOGO (createLogo)
    // =========================================================================
    @Nested
    @DisplayName("Mètode createLogo")
    class CreateLogoTests {

        @Test
        @DisplayName("Quan es passa una extensió vàlida, llavors crea el fitxer amb l'extensió i esborra els antics")
        void createLogo_QuanExtensionValida_LlavorsCreaFitxerIEsborraAntics() throws IOException {
            // Arrange
            // Creem un fitxer "antic" per verificar que s'esborra
            Path carpetaEntitat = tempDir.resolve(ENTITAT_CODI);
            Files.createDirectories(carpetaEntitat);
            File fitxerAntic = carpetaEntitat.resolve("logo_vell.jpg").toFile();
            fitxerAntic.createNewFile();
            assertTrue(fitxerAntic.exists());

            // Act
            entitatHelper.createLogo(ENTITAT_CODI, "png", LOGO_BYTES);

            // Assert
            File fitxerNou = carpetaEntitat.resolve("logo_ENT001.png").toFile();
            assertTrue(fitxerNou.exists());
            assertFalse(fitxerAntic.exists()); // S'ha esborrat
            assertArrayEquals(LOGO_BYTES, Files.readAllBytes(fitxerNou.toPath()));
        }

        @Test
        @DisplayName("Quan l'extensió és buida (\"\"), llavors crea el fitxer sense punt ni extensió")
        void createLogo_QuanExtensionBuida_LlavorsCreaSenseExtension() throws IOException {
            // Act
            entitatHelper.createLogo(ENTITAT_CODI, "", LOGO_BYTES);

            // Assert
            File fitxerNou = tempDir.resolve(ENTITAT_CODI).resolve("logo_ENT001").toFile();
            assertTrue(fitxerNou.exists());
            assertArrayEquals(LOGO_BYTES, Files.readAllBytes(fitxerNou.toPath()));
        }

        @Test
        @DisplayName("Quan la ruta configurada apunta a un fitxer (no directori), llavors llança RuntimeException (listFiles == null)")
        void createLogo_QuanRutaEsUnFitxer_LlavorsLlancaRuntimeException() {
            // Arrange: Enganyem el configHelper perquè retorni la ruta d'un FITXER, no d'un directori
            Path fitxerFals = tempDir.resolve("no_es_un_directori.txt");
            try {
                Files.createFile(fitxerFals);
            } catch (IOException e) {
                fail("No s'ha pogut crear el fitxer de prova");
            }
            when(configHelper.getConfig("es.caib.distribucio.entitat.logos.base.dir"))
                    .thenReturn(fitxerFals.toString());

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                entitatHelper.createLogo(ENTITAT_CODI, "png", LOGO_BYTES);
            });
            assertTrue(ex.getMessage().contains("No s'ha pogut crear la ruta pel logo"));
        }
    }

    // =========================================================================
    // 2. OBTENCIÓ DE LOGO (getLogo)
    // =========================================================================
    @Nested
    @DisplayName("Mètode getLogo")
    class GetLogoTests {

        @Test
        @DisplayName("Quan la carpeta existeix però està buida, llavors retorna null")
        void getLogo_QuanCarpetaBuida_LlavorsRetornaNull() throws IOException {
            // Arrange
            Path carpetaEntitat = tempDir.resolve(ENTITAT_CODI);
            Files.createDirectories(carpetaEntitat); // Carpeta buida

            // Act
            byte[] resultat = entitatHelper.getLogo(ENTITAT_CODI);

            // Assert
            assertNull(resultat);
            // Nota: El codi original fa un logger.warn, però com que logger és estàtic i privat,
            // no el podem verificar fàcilment sense MockedStatic. El retorn null és la verificació clau.
        }

        @Test
        @DisplayName("Quan la carpeta no existeix, llavors retorna null sense llançar excepció")
        void getLogo_QuanCarpetaNoExisteix_LlavorsRetornaNull() {
            // Act
            byte[] resultat = entitatHelper.getLogo("ENTITAT_INEXISTENT");

            // Assert
            assertNull(resultat);
        }
    }

    // =========================================================================
    // 3. ELIMINACIÓ DE LOGOS (removeLogos)
    // =========================================================================
    @Nested
    @DisplayName("Mètode removeLogos")
    class RemoveLogosTests {

        @Test
        @DisplayName("Quan existeixen logos, llavors els esborra tots")
        void removeLogos_QuanExisteixenLogos_LlavorsElsEsborra() throws IOException {
            // Arrange
            Path carpetaEntitat = tempDir.resolve(ENTITAT_CODI);
            Files.createDirectories(carpetaEntitat);
            Files.createFile(carpetaEntitat.resolve("logo1.png"));
            Files.createFile(carpetaEntitat.resolve("logo2.jpg"));

            // Creem una subcarpeta per verificar que NO s'esborra (només fitxers)
            Files.createDirectory(carpetaEntitat.resolve("subcarpeta"));

            // Act
            entitatHelper.removeLogos(ENTITAT_CODI);

            // Assert
            File[] fitxersRestants = carpetaEntitat.toFile().listFiles();
            assertNotNull(fitxersRestants);
            assertEquals(1, fitxersRestants.length); // Només ha de quedar la subcarpeta
            assertTrue(fitxersRestants[0].isDirectory());
        }

        @Test
        @DisplayName("Quan la carpeta no existeix, llavors no fa res i no llança excepció")
        void removeLogos_QuanNoExisteixCarpeta_LlavorsNoFaRes() {
            // Act & Assert
            assertDoesNotThrow(() -> entitatHelper.removeLogos("ENTITAT_INEXISTENT"));
        }
    }

    // =========================================================================
    // 4. CONSULTES D'ENTITAT (Delegació al repositori)
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de consulta d'entitat")
    class ConsultaEntitatTests {

        @Test
        @DisplayName("getCodiEntitat: Quan es passa un ID, llavors delega al repositori i retorna el codi")
        void getCodiEntitat_QuanValid_LlavorsDelegaAlRepositori() {
            // Arrange
            when(entitatRepository.getCodiEntitatPerId(1L)).thenReturn("ENT_CODI_1");

            // Act
            String resultat = entitatHelper.getCodiEntitat(1L);

            // Assert
            assertEquals("ENT_CODI_1", resultat);
            verify(entitatRepository, times(1)).getCodiEntitatPerId(1L);
        }

        @Test
        @DisplayName("getCodiEntitatRegistre: Quan es passa un ID d'anotació, llavors delega al repositori")
        void getCodiEntitatRegistre_QuanValid_LlavorsDelegaAlRepositori() {
            // Arrange
            when(entitatRepository.getCodiEntitatPerAnotacioId(99L)).thenReturn("ENT_CODI_99");

            // Act
            String resultat = entitatHelper.getCodiEntitatRegistre(99L);

            // Assert
            assertEquals("ENT_CODI_99", resultat);
            verify(entitatRepository, times(1)).getCodiEntitatPerAnotacioId(99L);
        }
    }

    // =========================================================================
    // 5. CONFIGURACIÓ I EXCEPCIONS (getLogosDir)
    // =========================================================================
    @Nested
    @DisplayName("Mètode privat getLogosDir (via excepcions públiques)")
    class GetLogosDirTests {

        @Test
        @DisplayName("Quan la propietat de configuració és null, llavors llança PropietatNotFoundException")
        void getLogosDir_QuanConfigNull_LlancapropietatNotFoundException() {
            // Arrange
            when(configHelper.getConfig("es.caib.distribucio.entitat.logos.base.dir")).thenReturn(null);

            // Act & Assert
            PropietatNotFoundException ex = assertThrows(PropietatNotFoundException.class, () -> {
                entitatHelper.createLogo(ENTITAT_CODI, "png", LOGO_BYTES);
            });
            // Verifiquem que el missatge de l'excepció conté el nom de la propietat
            assertTrue(ex.getMessage().contains("es.caib.distribucio.entitat.logos.base.dir"));
        }
    }
}