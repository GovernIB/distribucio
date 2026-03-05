-- Oracle
INSERT INTO DIS_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (5, 'LOGS', 'Logs del servidor' );
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.distribucio.plugin.fitxer.logs.path', null,'Ruta on es guarden el fitxers de logs del servidor','LOGS', 0, 1, 'TEXT', 0);

-- Postgresql
INSERT INTO DIS_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (5, 'LOGS', 'Logs del servidor' );
INSERT INTO DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.distribucio.plugin.fitxer.logs.path', null,'Ruta on es guarden el fitxers de logs del servidor','LOGS', false, 1, 'TEXT', false);
