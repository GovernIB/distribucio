-- Endpoint, username i password per l'api del portafib #768

-- Oracle
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.endpoint', null, 'Endpoint', 'VALID_SIGN', '12', '1', 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.username', null, 'Usuari', 'VALID_SIGN', '13', '1', 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.password', null, 'Contrasenya', 'VALID_SIGN', '14', '1', 'CREDENTIALS', null, null);

-- Postgresql
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.endpoint', null, 'Endpoint', 'VALID_SIGN', '12', true, 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.username', null, 'Usuari', 'VALID_SIGN', '13', true, 'TEXT', null, null);
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.pluginsib.validatesignature.api.portafib.password', null, 'Contrasenya', 'VALID_SIGN', '14', true, 'CREDENTIALS', null, null);