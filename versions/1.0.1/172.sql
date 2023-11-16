-- #172 Actualitzar la versió Java i el servidor d'aplicacions 

-- Oracle

-- Modificar les classes en els permisos
ALTER TABLE DIS_ACL_CLASS ADD CLASS_ID_TYPE VARCHAR2(255);
UPDATE DIS_ACL_CLASS SET CLASS_ID_TYPE='java.lang.Long';
ALTER TABLE DIS_ACL_CLASS MODIFY CLASS_ID_TYPE NOT NULL;
UPDATE dis_acl_class SET class = 'es.caib.distribucio.persist.entity.BustiaEntity' WHERE class = 'es.caib.distribucio.core.entity.BustiaEntity';
UPDATE dis_acl_class SET class = 'es.caib.distribucio.persist.entity.EntitatEntity' WHERE class = 'es.caib.distribucio.core.entity.EntitatEntity';

-- Afegir noves propietats configurables pel plugin d'usuaris
UPDATE DIS_CONFIG SET VALUE = 'es.caib.distribucio.plugin.caib.usuari.DadesUsuariPluginKeycloak' 
WHERE KEY LIKE 'es.caib.distribucio.plugin.dades.usuari.class';


INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl', NULL, 'Url del servidor de keycloak', 'USUARIS', '6', '1', 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm', NULL, 'Realm del keycloak', 'USUARIS', '7', '1', 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id', NULL, 'Client ID del keycloak', 'USUARIS', '8', '1', 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication', NULL, 'Client ID per autenticació del keycloak', 'USUARIS', '9', '1', 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret', NULL, 'Secret del client de keycloak', 'USUARIS', '10', '1', 'CREDENTIALS', null, null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID', 'NIF', 'Mapeig del administrationID de keycloak', 'USUARIS', '11', '0', 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug', 'false', 'Activar el debug del plugin de keycloak', 'USUARIS', '12', '0', 'BOOL', null, null);


-- Postgresql
