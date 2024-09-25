package es.caib.distribucio.plugin.caib.usuari;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link DadesUsuariPluginKeycloak} class.
 */
public class DadesUsuariPluginKeycloakTest {

  private DadesUsuariPluginKeycloak keycloak;

  @Before
  public void setUp(){
	    
    // Given
    String propertyKeyBase = "es.caib.distribucio.plugin.dades.usuari.";
    Properties properties = new Properties();
    // Local 7.3
//    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl", "http://localhost:8080/auth/");
//    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm", "GOIB");
//    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id", "goib-ws");
//    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication", "goib-ws");
//    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret", "b6a5b8b4-0ac3-4379-872a-2de828aa9d29");
//    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID", "NIF");
//    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug", "true");
    // PRE
    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl", "https://loginpre.caib.es/auth/");
    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm", "webpre");
    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id", "goib-ws");
    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication", "goib-ws");
    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret", "****");
    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID", "nif");
    properties.put("es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug", "true");

    keycloak = new DadesUsuariPluginKeycloak(propertyKeyBase, properties);
  }

  /**
   * Test case for {@link DadesUsuariPluginKeycloak#getUsernamesByRol} method.
   */
  @Test
  public void testGetUsernamesByRol(){

    // Given
    String rol = "DIS_ADMIN";
    
    // When
    try{
        var usuaris = keycloak.getUsernamesByRol(rol);

        assertTrue(usuaris != null && usuaris.length > 0);
    } catch(Exception ex){
    	String errMsg = "Excepció no controlada en testGetUsernamesByRol: " + ex.toString();
    	System.err.println(errMsg);
    	ex.printStackTrace(System.err);
        // then
        fail(errMsg);
    }
  }

  @After
  public void tearDown(){
    keycloak = null;
  }

}