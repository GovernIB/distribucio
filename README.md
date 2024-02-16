# DISTRIBUCIO
DISTRIBUCIO és una solució tecnològica desenvolupada pel Govern de les Illes Balears que permet distribuir anotacions de registre a les diferents bústies dels organismes.
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

es.caib.distribucio.app.base.url=https://localhost:8080/distribucio
es.caib.distribucio.node.esborrar.definitiu=false
es.caib.distribucio.tasca.notificacio.pendent.periode.execucio=30000
es.caib.distribucio.tasca.regla.pendent.periode.execucio=30000
es.caib.distribucio.segonpla.massives.periode.comprovacio=30000

es.caib.distribucio.files.path=FILES_PATH

es.caib.distribucio.concsv.base.url=CONCSV_BASE_URL

es.caib.distribucio.plugin.dadesext.service.url=DADESEXT_SERVICE_URL

es.caib.distribucio.plugin.arxiu.caib.base.url=ARXIU_BASE_URL
es.caib.distribucio.plugin.arxiu.caib.usuari=ARXIU_USERNAME
es.caib.distribucio.plugin.arxiu.caib.contrasenya=ARXIU_PASSWORD
es.caib.distribucio.plugin.arxiu.csv.base.url=ARXIU_CSV_BASE_URL
es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.url=ARXIU_CONV_IMPRIMIBLE_URL
es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.usuari=ARXIU_CONV_IMPRIMIBLE_USERNAME
es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.contrasenya=ARXIU_CONV_IMPRIMIBLE_PASSWORD

es.caib.distribucio.plugin.procediment.rolsac.service.url=ROLSAC_SERVICE_URL
es.caib.distribucio.plugin.procediment.rolsac.service.username=ROLSAC_SERVICE_USERNAME
es.caib.distribucio.plugin.procediment.rolsac.service.password=ROLSAC_SERVICE_PASSWORD

es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url=DIR3_SERVICE_URL
es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username=DIR3_SERVICE_USERNAME
es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password=DIR3_SERVICE_PASSWORD
es.caib.distribucio.plugin.unitats.cerca.dir3.service.url=DIR3_CERCA_SERVICE_URL

es.caib.distribucio.plugin.signatura.class=es.caib.distribucio.plugin.caib.signatura.FirmaSimplePluginPortafib
es.caib.distribucio.plugin.signatura.portafib.plugins.signatureserver.portafib.api_passarela_url=PORTAFIB_PASARELA_URL
es.caib.distribucio.plugin.signatura.portafib.plugins.signatureserver.portafib.api_passarela_username=PORTAFIB_PASARELA_USERNAME
es.caib.distribucio.plugin.signatura.portafib.plugins.signatureserver.portafib.api_passarela_password=PORTAFIB_PASARELA_PASSWORD
es.caib.distribucio.plugin.signatura.portafib.username=PORTAFIB_PARAM_USERNAME
es.caib.distribucio.plugin.signatura.portafib.location=PORTAFIB_PARAM_LOCATION
es.caib.distribucio.plugin.signatura.portafib.signerEmail=PORTAFIB_PARAM_SIGNER_EMAIL

es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint=FIRMASERVIDOR_URL
es.caib.distribucio.plugin.api.firma.en.servidor.simple.username=FIRMASERVIDOR_USERNAME
es.caib.distribucio.plugin.api.firma.en.servidor.simple.password=FIRMASERVIDOR_PASSWORD

es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint=VALIDATESIGNATURE_SERVICE_URL
es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username=VALIDATESIGNATURE_SERVICE_USERNAME
es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password=VALIDATESIGNATURE_SERVICE_PASSWORD

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
es.caib.distribucio.app.base.url=https://dev.caib.es/distribucio
es.caib.distribucio.node.esborrar.definitiu=false
es.caib.distribucio.tasca.notificacio.pendent.periode.execucio=30000
es.caib.distribucio.tasca.regla.pendent.periode.execucio=30000
es.caib.distribucio.segonpla.massives.periode.comprovacio=30000
```
Exemple de contingut del fitxer jboss_system.properties:

```
es.caib.distribucio.files.path=FILES_PATH

es.caib.distribucio.concsv.base.url=CONCSV_BASE_URL

es.caib.distribucio.plugin.dadesext.service.url=DADESEXT_SERVICE_URL

es.caib.distribucio.plugin.arxiu.caib.base.url=ARXIU_BASE_URL
es.caib.distribucio.plugin.arxiu.caib.usuari=ARXIU_USERNAME
es.caib.distribucio.plugin.arxiu.caib.contrasenya=ARXIU_PASSWORD
es.caib.distribucio.plugin.arxiu.csv.base.url=ARXIU_CSV_BASE_URL
es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.url=ARXIU_CONV_IMPRIMIBLE_URL
es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.usuari=ARXIU_CONV_IMPRIMIBLE_USERNAME
es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.contrasenya=ARXIU_CONV_IMPRIMIBLE_PASSWORD

es.caib.distribucio.plugin.procediment.rolsac.service.url=ROLSAC_SERVICE_URL
es.caib.distribucio.plugin.procediment.rolsac.service.username=ROLSAC_SERVICE_USERNAME
es.caib.distribucio.plugin.procediment.rolsac.service.password=ROLSAC_SERVICE_PASSWORD

es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url=DIR3_SERVICE_URL
es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username=DIR3_SERVICE_USERNAME
es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password=DIR3_SERVICE_PASSWORD
es.caib.distribucio.plugin.unitats.cerca.dir3.service.url=DIR3_CERCA_SERVICE_URL

es.caib.distribucio.plugin.signatura.class=es.caib.distribucio.plugin.caib.signatura.FirmaSimplePluginPortafib
es.caib.distribucio.plugin.signatura.portafib.plugins.signatureserver.portafib.api_passarela_url=PORTAFIB_PASARELA_URL
es.caib.distribucio.plugin.signatura.portafib.plugins.signatureserver.portafib.api_passarela_username=PORTAFIB_PASARELA_USERNAME
es.caib.distribucio.plugin.signatura.portafib.plugins.signatureserver.portafib.api_passarela_password=PORTAFIB_PASARELA_PASSWORD
es.caib.distribucio.plugin.signatura.portafib.username=PORTAFIB_PARAM_USERNAME
es.caib.distribucio.plugin.signatura.portafib.location=PORTAFIB_PARAM_LOCATION
es.caib.distribucio.plugin.signatura.portafib.signerEmail=PORTAFIB_PARAM_SIGNER_EMAIL

es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint=FIRMASERVIDOR_URL
es.caib.distribucio.plugin.api.firma.en.servidor.simple.username=FIRMASERVIDOR_USERNAME
es.caib.distribucio.plugin.api.firma.en.servidor.simple.password=FIRMASERVIDOR_PASSWORD

es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint=VALIDATESIGNATURE_SERVICE_URL
es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username=VALIDATESIGNATURE_SERVICE_USERNAME
es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password=VALIDATESIGNATURE_SERVICE_PASSWORD

es.caib.distribucio.plugin.dades.usuari.class=es.caib.distribucio.plugin.caib.usuari.DadesUsuariPluginPluginsIb
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.url=PLUGINSIB_USERINFO_URL
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.realm=PLUGINSIB_USERINFO_REALM
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.client_id=PLUGINSIB_USERINFO_CLIENTID
es.caib.distribucio.plugin.dades.usuari.pluginsib.service.password_secret=PLUGINSIB_USERINFO_SECRET
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
