package es.caib.distribucio.rest.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AnotacioRegistreIdTest {

    @Test
    public void testParseComunicarAnotacionsPendents() {
        try {
            String json = "[ {\r\n"
                    + "\"identificador\" : \"GOIBE1111/2022\",\r\n"
                    + "\"clauAcces\" : \"hv/8hXYRSFYtVEjZyDfEE8KdOIiX3N1UEXGiltl3rbw=\"\r\n"
                    + "}, {\r\n"
                    + "\"indetificador\" : \"GOIBE2222/2022\",\r\n"
                    + "\"clauAcces\" : \"AzRwN0QQV70RR4IxXFZsPj2FYJ2VA7LDiKap19MmA/U=\"\r\n"
                    + "} ]";

            ObjectMapper mapper = new ObjectMapper();

            List<AnotacioRegistreId> anotacionsIds = mapper.readValue(json, new TypeReference<List<AnotacioRegistreId>>(){});

            System.out.println("Ids: " + anotacionsIds.size());
            assertEquals(anotacionsIds.size(), 2);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
}
