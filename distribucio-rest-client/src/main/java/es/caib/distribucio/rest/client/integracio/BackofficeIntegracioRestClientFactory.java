package es.caib.distribucio.rest.client.integracio;

public class BackofficeIntegracioRestClientFactory {

    /**
     * Crea un client per a connectar-se amb la API REST v2 de DISTRIBUCIO per a la integració de backoffices.
     *
     * @param baseUrl Url de Distribucio
     * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Distribucio
     * @param password Contrasenya de l'usuari
     * @return Client per a interactuar amb Distribucio. El client per defecte està configurat amb autenticació tipus BASIC,
     *  	i amb timeouts de 20s de connexió i 2 min de lectura.
     */
    public static BackofficeIntegracioRestClient getRestClient(String baseUrl, String username, String password) {
        return new BackofficeIntegracioRestClient(baseUrl, username, password);
    }

    /**
     * Crea un client per a connectar-se amb la API REST v2 de DISTRIBUCIO per a la integració de backoffices.
     *
     * @param baseUrl Url de Distribucio
     * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Distribucio
     * @param password Contrasenya de l'usuari
     * @param autenticacioBasic Indica si utilitzar autenticació basic. En cas negatiu s'utilitzarà autenticació form.
     * @return Client per a interactuar amb Distribucio. El client per defecte està configurat amb autenticació tipus BASIC,
     *  	i amb timeouts de 20s de connexió i 2 min de lectura.
     */
    public static BackofficeIntegracioRestClient getRestClient(String baseUrl, String username, String password, boolean autenticacioBasic) {
        return new BackofficeIntegracioRestClient(baseUrl, username, password, autenticacioBasic);
    }

    /**
     * Crea un client per a connectar-se amb la API REST v2 de DISTRIBUCIO per a la integració de backoffices.
     *
     * @param baseUrl Url de Distribucio
     * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Distribucio
     * @param password Contrasenya de l'usuari
     * @param connecTimeout Timeout de connexió en milisegons
     * @param readTimeout Timeout de lectura en milisegons
     * @return Client per a interactuar amb Distribucio. El client per defecte està configurat amb autenticació tipus BASIC,
     *  	i amb timeouts de 20s de connexió i 2 min de lectura.
     */
    public static BackofficeIntegracioRestClient getRestClient(String baseUrl, String username, String password, int connecTimeout, int readTimeout) {
        return new BackofficeIntegracioRestClient(baseUrl, username, password, connecTimeout, readTimeout);
    }

    /**
     * Crea un client per a connectar-se amb la API REST v2 de DISTRIBUCIO per a la integració de backoffices.
     *
     * @param baseUrl Url de Distribucio
     * @param username Usuari de tipus aplicació amb el que es vol interactuar amb Distribucio
     * @param password Contrasenya de l'usuari
     * @param autenticacioBasic Indica si utilitzar autenticació basic. En cas negatiu s'utilitzarà autenticació form.
     * @param connecTimeout Timeout de connexió en milisegons
     * @param readTimeout Timeout de lectura en milisegons
     * @return Client per a interactuar amb Distribucio. El client per defecte està configurat amb autenticació tipus BASIC,
     *  	i amb timeouts de 20s de connexió i 2 min de lectura.
     */
    public static BackofficeIntegracioRestClient getRestClient(String baseUrl, String username, String password, boolean autenticacioBasic, int connecTimeout, int readTimeout) {
        return new BackofficeIntegracioRestClient(baseUrl, username, password, autenticacioBasic, connecTimeout, readTimeout);
    }
}
