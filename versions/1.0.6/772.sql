
ALTER TABLE DIS_REGISTRE ADD BACK_COMUNICADA_DATA TIMESTAMP;

INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_COMUNICAT_A_PENDENT','SCHEDULLED','20','Tasca periòdica de canvi d''estat anotacions comunicades a pendents');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.comunicada', null, 'Iterval de temps entre les execucions de la tasca (ms)', 'SCHEDULLED_COMUNICAT_A_PENDENT', '1', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.tasca.enviar.anotacions.backoffice.maxim.temps.estat.comunicada', '60', 'Màxim de dies que pot estar comunicada abans de canviar l''estat a pendent d''usuari.', 'SCHEDULLED_COMUNICAT_A_PENDENT', '2', '0', 'INT');
