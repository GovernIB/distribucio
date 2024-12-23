-- Distribucio 1.0.1

-- #172 Actualitzar la versió Java i el servidor d'aplicacions 

-- Modificar les classes en els permisos
ALTER TABLE DIS_ACL_CLASS ADD CLASS_ID_TYPE character varying(255);
UPDATE DIS_ACL_CLASS SET CLASS_ID_TYPE='java.lang.Long';
ALTER TABLE DIS_ACL_CLASS ALTER COLUMN CLASS_ID_TYPE SET NOT NULL;
UPDATE dis_acl_class SET class = 'es.caib.distribucio.persist.entity.BustiaEntity' WHERE class = 'es.caib.distribucio.core.entity.BustiaEntity';
UPDATE dis_acl_class SET class = 'es.caib.distribucio.persist.entity.EntitatEntity' WHERE class = 'es.caib.distribucio.core.entity.EntitatEntity';

-- Canvia el plugin de dades d'usuari
UPDATE DIS_CONFIG SET VALUE = 'es.caib.distribucio.plugin.caib.usuari.DadesUsuariPluginKeycloak' 
WHERE KEY LIKE 'es.caib.distribucio.plugin.dades.usuari.class';

-- Afegir noves propietats configurables pel plugin d'usuaris
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl', NULL, 'Url del servidor de keycloak', 'USUARIS', '6', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm', NULL, 'Realm del keycloak', 'USUARIS', '7', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id', NULL, 'Client ID del keycloak', 'USUARIS', '8', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication', NULL, 'Client ID per autenticació del keycloak', 'USUARIS', '9', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret', NULL, 'Secret del client de keycloak', 'USUARIS', '10', true, 'CREDENTIALS', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID', 'nif', 'Mapeig del administrationID de keycloak', 'USUARIS', '11', false, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug', 'false', 'Activar el debug del plugin de keycloak', 'USUARIS', '12', false, 'BOOL', null, null);

-- 2024.09.02 Distribucio_1.0.1.pre12

-- Actualitza el plugin d'arxiu a la versió 3.0.0-SNAPSHOT
UPDATE DIS_CONFIG SET VALUE = 'es.caib.pluginsib.arxiu.caib.ArxiuPluginCaib' 
WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.class';

-- Actualita el plugin de validació de firmes a la versió 3.0.0-SNAPSHOT
UPDATE DIS_CONFIG SET VALUE = 'org.fundaciobit.pluginsib.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin' 
WHERE KEY LIKE 'es.caib.distribucio.plugin.validatesignature.class';

-- Canvi de claus en les propietats
UPDATE DIS_CONFIG
SET KEY = REPLACE(KEY, '.plugin.arxiu.caib','.pluginsib.arxiu.caib')
WHERE 
	KEY like '%.plugin.arxiu.caib%';

UPDATE DIS_CONFIG
SET KEY = REPLACE(KEY, '.plugins.validatesignature','.pluginsib.validatesignature')
WHERE 
	KEY like '%.plugins.validatesignature%';
	
-- Propietats pel plugin d'usuaris de LDAP per poder-les visualitzar
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.host_url', NULL, 'URL del servidor LDAP CAIB', 'USUARIS', '13', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.security_principal', NULL, 'Security principal pel servidor LDAP CAIB', 'USUARIS', '14', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.security_authentication', NULL, 'Security authentication pel servidor LDAP CAIB', 'USUARIS', '15', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.security_credentials', NULL, 'Security credentials pel servidor LDAP CAIB', 'USUARIS', '16', true, 'CREDENTIALS', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.users_context_dn', NULL, 'User context DN pel servidor LDAP CAIB', 'USUARIS', '17', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.search_scope', NULL, 'Search scope pel servidor LDAP CAIB', 'USUARIS', '18', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.username', NULL, 'Attribute username pel servidor LDAP CAIB', 'USUARIS', '19', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.mail', NULL, 'Attribute mail pel servidor LDAP CAIB', 'USUARIS', '20', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.administration_id', NULL, 'Attribute administration id pel servidor LDAP CAIB', 'USUARIS', '21', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.name', NULL, 'Attribute name pel servidor LDAP CAIB', 'USUARIS', '22', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.surname', NULL, 'Attribute surname pel servidor LDAP CAIB', 'USUARIS', '23', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.surname1', NULL, 'Attribute surname 1 pel servidor LDAP CAIB', 'USUARIS', '24', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.surname2', NULL, 'Attribute surname 2 pel servidor LDAP CAIB', 'USUARIS', '25', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.telephone', NULL, 'Attribute telephone pel servidor LDAP CAIB', 'USUARIS', '26', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.department', NULL, 'Attribute department pel servidor LDAP CAIB', 'USUARIS', '27', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.attribute.memberof', NULL, 'Attribute member of pel servidor LDAP CAIB', 'USUARIS', '28', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.prefix_role_match_memberof', NULL, 'Attribute prefix role match member of pel servidor LDAP CAIB', 'USUARIS', '29', true, 'TEXT', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.ldap.suffix_role_match_memberof', NULL, 'Attribute suffix role match member of pel servidor LDAP CAIB', 'USUARIS', '30', true, 'TEXT', null, null);

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.security.resourceAcces.api-interna','null','Codi del client per agafar els rols en l''autenticació de l''API REST interna. Per ex. goib-ws.','GENERAL','24',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.security.resourceAcces.api-externa','null','Codi del client per agafar els rols en l''autenticació de l''API REST externa. Per ex. goib-ws.','GENERAL','25',true,'TEXT',null,null);


-- Propietats per poder veure la configuració per certificat del plugin de validació de firmes.
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.type=JKS
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.type',null,'Tipus de magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',8,true,'TEXT',true);
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.password=****
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.password',null,'Password del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',9,true,'CREDENTIALS',true);
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias=preprod-dgmad
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.cert.alias',null,'Alies del certificat a emprar del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',10,true,'TEXT',true);
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.password=****
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.cert.password',null,'Password del certificat del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',11,true,'CREDENTIALS',true);

---- ROLLBACK
--UPDATE DIS_CONFIG SET VALUE = 'es.caib.plugin.arxiu.caib.ArxiuPluginCaib' 
--WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.class';
--UPDATE DIS_CONFIG
--SET KEY = REPLACE(KEY, '.pluginsib.arxiu.caib','.plugin.arxiu.caib')
--WHERE 
--	KEY like '%.pluginsib.arxiu.caib%';
--
--UPDATE DIS_CONFIG
--SET KEY = REPLACE(KEY, '.pluginsib.validatesignature','.plugins.validatesignature')
--WHERE 
--	KEY like '%.pluginsib.validatesignature%';
	
