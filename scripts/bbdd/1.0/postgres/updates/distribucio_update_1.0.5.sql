-- Distribucio 1.0.5

-- Implementar un sistema de salut que pugui ser consultat per un sistema centralitzat de monitoratge #703
UPDATE DIS_CONFIG C SET C.VALUE = 'es.caib.distribucio.plugin.caib.arxiu.ArxiuPluginSalutCaib' WHERE C.KEY = 'es.caib.distribucio.plugin.arxiu.class';
UPDATE DIS_CONFIG C SET C.VALUE = 'es.caib.distribucio.plugin.caib.validacio.ValidacioFirmaPluginApiPortafib' WHERE C.KEY = 'es.caib.distribucio.plugin.validatesignature.class';

-- Endpoint, username i password per l'api del portafib #768
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.endpoint', null, 'Endpoint', 'VALID_SIGN', '12', true, 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.username', null, 'Usuari', 'VALID_SIGN', '13', true, 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.password', null, 'Contrasenya', 'VALID_SIGN', '14', true, 'CREDENTIALS', null, null);