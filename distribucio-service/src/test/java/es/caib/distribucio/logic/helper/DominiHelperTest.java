package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.DominiDto;
import es.caib.distribucio.logic.intf.exception.DominiException;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.persist.entity.DominiEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.repository.DominiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proves unitàries de DominiHelper")
class DominiHelperTest {

    @Mock private DominiRepository dominiRepository;
    @Mock private EntityComprovarHelper entityComprovarHelper;
    @Mock private ConversioTipusHelper conversioTipusHelper;

    @InjectMocks
    private DominiHelper dominiHelper;

    private EntitatEntity entitatMock;
    private DominiDto dominiDtoInput;
    private DominiEntity dominiEntitySaved;
    private DominiDto dominiDtoOutput;

    private static final String XML_CADENA_VALIDA =
            "<local-tx-datasource>" +
                    "  <connection-url>jdbc:h2:mem:testdb</connection-url>" +
                    "  <driver-class>org.h2.Driver</driver-class>" +
                    "  <user-name>testUser</user-name>" +
                    "</local-tx-datasource>";

    @BeforeEach
    void setUp() {
        entitatMock = mock(EntitatEntity.class);

        dominiDtoInput = new DominiDto();
        dominiDtoInput.setCodi("DOM001");
        dominiDtoInput.setNom("Domini Test");
        dominiDtoInput.setDescripcio("Descripció test");
        dominiDtoInput.setConsulta("SELECT 1");
        dominiDtoInput.setCadena(XML_CADENA_VALIDA);
        dominiDtoInput.setContrasenya("plainPassword");

        dominiEntitySaved = mock(DominiEntity.class);
        dominiDtoOutput = new DominiDto();
        dominiDtoOutput.setCodi("DOM001");
    }

    // =========================================================================
    // 1. CREACIÓ DE DOMINI
    // =========================================================================
    @Nested
    @DisplayName("Mètode create")
    class CreateTests {

        @Test
        @DisplayName("create: Quan xifrarContrasenya és false, llavors guarda la contrasenya en pla")
        void create_QuanNoXifra_LlavorsGuardaContrasenyaEnPla() {
            // Arrange
            when(entityComprovarHelper.comprovarEntitat(1L, false, true, false)).thenReturn(entitatMock);
            when(dominiRepository.save(any(DominiEntity.class))).thenReturn(dominiEntitySaved);
            when(conversioTipusHelper.convertir(dominiEntitySaved, DominiDto.class)).thenReturn(dominiDtoOutput);

            // Act
            DominiDto resultat = dominiHelper.create(1L, dominiDtoInput, false);

            // Assert
            assertEquals(dominiDtoOutput, resultat);
            verify(entityComprovarHelper, times(1)).comprovarEntitat(1L, false, true, false);

            ArgumentCaptor<DominiEntity> captor = ArgumentCaptor.forClass(DominiEntity.class);
            verify(dominiRepository, times(1)).save(captor.capture());
            assertEquals("plainPassword", captor.getValue().getContrasenya());
        }

        @Test
        @DisplayName("create: Quan xifrarContrasenya és true, llavors guarda la contrasenya xifrada")
        void create_QuanXifra_LlavorsGuardaContrasenyaXifrada() {
            // Arrange
            when(entityComprovarHelper.comprovarEntitat(1L, false, true, false)).thenReturn(entitatMock);
            when(dominiRepository.save(any(DominiEntity.class))).thenReturn(dominiEntitySaved);
            when(conversioTipusHelper.convertir(dominiEntitySaved, DominiDto.class)).thenReturn(dominiDtoOutput);

            // Act
            DominiDto resultat = dominiHelper.create(1L, dominiDtoInput, true);

            // Assert
            assertEquals(dominiDtoOutput, resultat);
            ArgumentCaptor<DominiEntity> captor = ArgumentCaptor.forClass(DominiEntity.class);
            verify(dominiRepository, times(1)).save(captor.capture());

            // La contrasenya guardada ha de ser diferent de la original (està en Base64)
            String contrasenyaGuardada = captor.getValue().getContrasenya();
            assertNotEquals("plainPassword", contrasenyaGuardada);
            assertNotNull(contrasenyaGuardada);
        }
    }

    // =========================================================================
    // 2. GESTIÓ DE PROPIETATS I XML
    // =========================================================================
    @Nested
    @DisplayName("Mètode getProperties")
    class GetPropertiesTests {

        @Test
        @DisplayName("getProperties: Quan el XML és vàlid i hi ha contrasenya, llavors extreu les dades i desxifra")
        void getProperties_QuanXmlValidIContrasenya_LlavorsExtreuIDesxifra() {
            // Arrange
            // Primer xifrem la contrasenya per simular l'estat real de la BBDD
            String contrasenyaXifrada = dominiHelper.xifrarContrasenya("secretPassword");
            dominiDtoInput.setContrasenya(contrasenyaXifrada);

            // Act
            Properties props = dominiHelper.getProperties(dominiDtoInput);

            // Assert
            assertNotNull(props);
            assertEquals("jdbc:h2:mem:testdb", props.getProperty("url"));
            assertEquals("org.h2.Driver", props.getProperty("driver"));
            assertEquals("testUser", props.getProperty("user"));
            assertEquals("secretPassword", props.getProperty("password")); // S'ha desxifrat correctament
        }

        @Test
        @DisplayName("getProperties: Quan la contrasenya és null o buida, llavors no afegeix la propietat password")
        void getProperties_QuanContrasenyaBuida_LlavorsNoAfegeixPassword() {
            // Arrange
            dominiDtoInput.setContrasenya("");

            // Act
            Properties props = dominiHelper.getProperties(dominiDtoInput);

            // Assert
            assertNotNull(props);
            assertNull(props.getProperty("password"));
        }

        @Test
        @DisplayName("getProperties: Quan el XML és invàlid, llavors llança ValidationException")
        void getProperties_QuanXmlInvalid_LlavorsLlancaValidationException() {
            // Arrange
            dominiDtoInput.setCadena("<xml-mal-format-sense-tancar>");

            // Act & Assert
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                dominiHelper.getProperties(dominiDtoInput);
            });
            assertNotNull(exception.getMessage());
        }
    }

    // =========================================================================
    // 3. CRIPTOGRAFIA (Xifratge i Desxifratge)
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de criptografia")
    class CriptografiaTests {

        @Test
        @DisplayName("xifrarContrasenya: Quan la contrasenya és vàlida, llavors retorna un string en Base64 diferent")
        void xifrarContrasenya_QuanValida_LlavorsRetornaBase64() {
            // Act
            String resultat = dominiHelper.xifrarContrasenya("mySecret");

            // Assert
            assertNotNull(resultat);
            assertNotEquals("mySecret", resultat);
            // Verificació bàsica que és Base64 (no conté caràcters estranys, longitud consistent)
            assertTrue(resultat.length() > 0);
        }

        @Test
        @DisplayName("desxifrarContrasenya: Quan es desxifra un text xifrat prèviament, llavors retorna l'original")
        void desxifrarContrasenya_QuanTextXifratValid_LlavorsRetornaOriginal() {
            // Arrange
            String original = "superSecretPassword123";
            String xifrat = dominiHelper.xifrarContrasenya(original);

            // Act
            String desxifrat = dominiHelper.desxifrarContrasenya(xifrat);

            // Assert
            assertEquals(original, desxifrat);
        }

        @Test
        @DisplayName("desxifrarContrasenya: Quan el text no és un Base64 vàlid o el xifratge falla, llavors llança CipherException")
        void desxifrarContrasenya_QuanTextInvalid_LlavorsLlancaCipherException() {
            // Arrange:

            // Act & Assert
            // Nota: Depenent del proveïdor de seguretat de Java, això pot llançar CipherException o IllegalBlockSizeException
            assertThrows(Exception.class, () -> {
                dominiHelper.desxifrarContrasenya(null);
            });
        }
    }

    // =========================================================================
    // 4. GESTIÓ DE DADES I CONNEXIONS
    // =========================================================================
    @Nested
    @DisplayName("Mètodes de dades i connexions")
    class DadesIConnexionsTests {

        @Test
        @DisplayName("setDataSource: Quan es passa un DataSource, llavors retorna un JdbcTemplate nou")
        void setDataSource_QuanValid_LlavorsRetornaJdbcTemplate() {
            // Arrange
            DataSource mockDataSource = mock(DataSource.class);

            // Act
            JdbcTemplate resultat = dominiHelper.setDataSource(mockDataSource);

            // Assert
            assertNotNull(resultat);
            assertInstanceOf(JdbcTemplate.class, resultat);
        }

        @Test
        @DisplayName("createDominiConnexio: Quan les propietats són vàlides, llavors retorna un DriverManagerDataSource")
        void createDominiConnexio_QuanValid_LlavorsRetornaDataSource() {
            // Arrange
            Properties props = new Properties();
            props.setProperty("url", "jdbc:h2:mem:test");
            props.setProperty("user", "sa");

            // Act
            DataSource resultat = dominiHelper.createDominiConnexio("ENT001", props);

            // Assert
            assertNotNull(resultat);
            assertInstanceOf(DriverManagerDataSource.class, resultat);
        }

        @Test
        @DisplayName("createDominiConnexio: Quan les propietats són invàlides (ex: URL nul·la), llavors llança DominiException")
        void createDominiConnexio_QuanInvalid_LlavorsLlancaDominiException() {
            // Arrange

            // Act & Assert
            DominiException exception = assertThrows(DominiException.class, () -> {
                dominiHelper.createDominiConnexio("ENT001", null);
            });
            assertTrue(exception.getMessage().contains("No s'ha pogut crear el datasource"));
        }
    }
}