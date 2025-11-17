INSERT INTO DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION)
VALUES ('SCHEDULLED_EMAIL_ANOTACIO_ERROR_PROCESAR', 'SCHEDULLED', 24, 'Enviament periòdic per correu de les anotacions amb error de processament');

INSERT INTO DIS_CONFIG (GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE) values
    ('SCHEDULLED_EMAIL_ANOTACIO_ERROR_PROCESAR','es.caib.distribucio.enviar.anotacio.error.cron','0 0 0 * * *','Especificar l''expressió ''cron'' indicant l''interval de temps de les execucions de la tasca','0','CRON');

ALTER TABLE DIS_USUARI ADD EMAIL_ERROR_ANOTACIO NUMBER(1) DEFAULT 0