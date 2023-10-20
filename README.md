# DISTRIBUCIO
DISTRIBUCIO és una solució tecnològica desenvolupada pel Govern de les Illes Balears que permet distribuir anotacions de registre a les diferents bústies dels organismes.

DISTRIBUCIO ofereix un servei web per donar d'altra anotacions de registre i que aquestes es distribueixin en les bústies definides per les diferents unitats organitzatives. També permet definir bústies per defecte i regles per distribuir automàticament les anotacions en les diferents bústies segons el codi DIR3 de la unitat organitzativa destí i el codi de procediement SIA. L'usuari podrà marcar com a processades les anotacions de registre o també es podran crear regles per processar-les automàticament i enviar-les a una altra aplicació de backoffice que s'encarregarà del seu processament.
## Compilar
Executar la següent comanda:

```
$ mvn clean install
```
La compilació del projecte crea varis fitxers per a executar o desplegar l'aplicació:
- distribucio-back/target/distribucio-back-X.X.X.war: fitxer per a executar l'aplicació directament amb Java.
- distribucio-ear/target/distribucio.ear: fitxer per a executar l'aplicació sobre un servidor JBoss EAP 7.2.

## Execució amb Java
Crear un fitxer application.properties amb la configuració de l'aplicació a la mateixa carpeta a on hi ha el fitxer distribucio-back-X.X.X.war. El contingut del fitxer ha de tenir, com a mínim, el següent contingut:

```
spring.datasource.url=jdbc:oracle:thin:@DB_HOST:DB_PORT:DB_SID
spring.datasource.username=DB_USERNAME
spring.datasource.password=DB_PASSWORD

spring.mail.host=MAIL_HOST
spring.mail.port=MAIL_PORT
spring.mail.username=MAIL_USERNAME
spring.mail.password=MAIL_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.starttls.enable=false

spring.security.oauth2.client.provider.keycloak.issuer-uri=https://AUTH_HOST/realms/AUTH_REALM
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
spring.security.oauth2.client.registration.keycloak.client-id=AUTH_CLIENTID
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code

es.caib.distribucio.plugin.dades.usuari.class=es.caib.distribucio.plugin.caib.usuari.DadesUsuariPluginPluginsIb
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.url=PLUGINSIB_USERINFO_URL
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.realm=PLUGINSIB_USERINFO_REALM
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.client_id=PLUGINSIB_USERINFO_CLIENTID
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.password_secret=PLUGINSIB_USERINFO_SECRET
```
Iniciar l'aplicació amb la següent comanda:

```
$ java -jar distribucio-back-X.X.X.war
```
## Execució sobre JBoss EAP 7.2

### Configuració dels datasources
Modificar la secció `<subsystem xmlns="urn:jboss:domain:datasources:5.0">` del fitxer /standalone/configuration/standalone.xml amb el següent contingut:

```
        <subsystem xmlns="urn:jboss:domain:datasources:5.0">
            <datasources>
                <datasource jndi-name="java:jboss/datasources/distribucioDS" pool-name="distribucioDB" enabled="true">
                    <driver>oracle</driver>
                    <connection-url>JBOSS_DB_URL</connection-url>
                    <security>
                        <user-name>JBOSS_DB_USERNAME</user-name>
                        <password>JBOSS_DB_PASSWORD</password>
                    </security>
                    <pool>
                        <min-pool-size>1</min-pool-size>
                        <max-pool-size>10</max-pool-size>
                        <prefill>true</prefill>
                    </pool>
                </datasource>
                <drivers>
                    <driver name="oracle" module="com.oracle">
                        <driver-class>oracle.jdbc.driver.OracleDriver</driver-class>
                    </driver>
                </drivers>
            </datasources>
        </subsystem>
```
En aquest contingut d'exemple s'han de substituir les següents variables:
- JBOSS_DB_URL: URL (en el format requerit pel driver JDBC) d'accés a la base de dades.
- JBOSS_DB_USERNAME: Usuari d'accés a la base de dades.
- JBOSS_DB_PASSWORD: Contrasenya d'accés a la base de dades.

### Configuració de Keycloak
Modificar la secció `<subsystem xmlns="urn:jboss:domain:keycloak:1.1">` del fitxer /standalone/configuration/standalone.xml amb el següent contingut:

```
        <subsystem xmlns="urn:jboss:domain:keycloak:1.1">
            <realm name="GOIB">
                <auth-server-url>JBOSS_AUTH_URL</auth-server-url>
                <ssl-required>external</ssl-required>
            </realm>
            <secure-deployment name="distribucio-api-externa.war">
                <realm>JBOSS_AUTH_REALM</realm>
                <resource>JBOSS_AUTH_CLIENTID</resource>
                <use-resource-role-mappings>false</use-resource-role-mappings>
                <public-client>true</public-client>
                <verify-token-audience>true</verify-token-audience>
                <principal-attribute>preferred_username</principal-attribute>
            </secure-deployment>
            <secure-deployment name="distribucio-api-interna.war">
                <realm>JBOSS_AUTH_REALM</realm>
                <resource>JBOSS_AUTH_CLIENTID</resource>
                <use-resource-role-mappings>false</use-resource-role-mappings>
                <public-client>true</public-client>
                <verify-token-audience>true</verify-token-audience>
                <principal-attribute>preferred_username</principal-attribute>
            </secure-deployment>
            <secure-deployment name="distribucio-back.war">
                <realm>JBOSS_AUTH_REALM}</realm>
                <resource>JBOSS_AUTH_CLIENTID</resource>
                <use-resource-role-mappings>false</use-resource-role-mappings>
                <public-client>true</public-client>
                <verify-token-audience>true</verify-token-audience>
                <principal-attribute>preferred_username</principal-attribute>
            </secure-deployment>
        </subsystem>
```
En aquest contingut d'exemple s'han de substituir les següents variables:
- JBOSS_AUTH_URL: URL d'accés al servidor Keycloak.
- JBOSS_AUTH_REALM: Realm que s'utilitzarà per a l'autenticació.
- JBOSS_AUTH_CLIENTID: Id del client que s'utilitzarà per a l'autenticació.

### Configuració de les propietats
Modificar o afegir la secció `<system-properties>` del fitxer /standalone/configuration/standalone.xml amb el següent contingut:

```
    <system-properties>
        <property name="es.caib.distribucio.properties" value="JBOSS_PROPS_PATH/jboss.properties"/>
        <property name="es.caib.distribucio.system.properties" value="JBOSS_PROPS_PATH/jboss_system.properties"/>
    </system-properties>
```
En aquest contingut d'exemple s'han de substituir les següents variables:
- JBOSS_PROPS_PATH: carpeta a on es troben els fitxers de propietats.

Exemple de contingut del fitxer jboss.properties:

```
es.caib.distribucio.plugin.dades.usuari.class=es.caib.distribucio.plugin.caib.usuari.DadesUsuariPluginPluginsIb
```
Exemple de contingut del fitxer jboss_system.properties:

```
es.caib.distribucio.files.path=FILES_PATH
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.url=DADESUSUARI_SERVICE_URL
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.realm=DADESUSUARI_SERVICE_REALM
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.client_id=DADESUSUARI_CLIENT_ID
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.password_secret=DADESUSUARI_PASSWD_SECRET
```
### Desplegament i execució
Per a desplegar l'aplicació s'ha de copiar el fitxer distribucio-ear/target/distribucio.ear generat amb la copilació de maven a la carpeta /standalone/deployments.
Per a iniciar el servidor jboss s'ha d'anar a la carpeta bin del servidor i executar la següent comanda:

```
$ ./standalone.sh
```
## Generar scripts de base de dades
- Crear el fitxer distribucio-persistence/liquibase/databasechangelog.csv que contengui l'estat actual de la base de dades.
- Executar la següent comanda

```
$ mvn -pl distribucio-persistence liquibase:updateSQL
```
- L'execució d'aquesta comanda generarà l'script d'actualització a dins distribucio-persistence/liquibase/script.sql i actualitzará el fitxer distribucio-persistence/liquibase/databasechangelog.csv.
