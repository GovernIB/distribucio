-- Modificar la sincronització de procediments i servis amb Rolsac #720

INSERT INTO DIS_CONFIG
("KEY", VALUE, DESCRIPTION, GROUP_CODE, "POSITION", JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE)
VALUES('es.caib.distribucio.plugin.rolsac.service.url', 'https://dev.caib.es/rolsac/api/rest/v1', 'Url per a accedir a Rolsac', 'PROCEDIMENTS', 1, 1, 'TEXT', NULL, 1, NULL, NULL);

INSERT INTO DIS_CONFIG_GROUP
(CODE, PARENT_CODE, "POSITION", DESCRIPTION)
VALUES('SCHEDULLED_SERVEI', 'SCHEDULLED', 23, 'Tasca periòdica per actualitzar la taula de serveis');

INSERT INTO DIS_CONFIG
("KEY", VALUE, DESCRIPTION, GROUP_CODE, "POSITION", JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE)
VALUES('es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis', '0 0 17 * * *', 'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels serveis en segon pla. Per defecte s''aplicarà el cron corresponent per actualitzar cada divendres a les 15:30h', 'SCHEDULLED_SERVEI', 0, 0, 'TEXT', NULL, 0, NULL, TIMESTAMP '2025-01-09 15:39:49.120000');
