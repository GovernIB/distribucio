-- Afegeix els paràmetres de configuració de màxim de grandària, execucions per dia, ruta i configuració de la tasca d'esborrat d'arxius.
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (1,'GENERAL','es.caib.distribucio.fitxers', null,'Ruta de fitxers de Distribucio al servidor',0,'TEXT',0);
INSERT INTO DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES 
	('GENERAL_EXPORT_ZIP', 'GENERAL', 27, 'Configuració d''exportació masiva d''annexos a zip');
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.mida.max', 10,'Mida màxima del fitxer ZIP generat (MB)',0,'INT',0);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.exec.max', 5,'Nombre d''execucions màximes permeses per usuari',1,'INT',0);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.enabled', 'false','Habilitar l''exportació massiva d''annexos de registres',2,'BOOL',0);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.caducitat', 10,'Nombre de dies que es guardarà el fitxer ZIP abans de que s''esborri automàticament',3,'INT',0);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.caducitat.cron', '0 0 0 * * *','Tasca periòdica per esborrar documents .ZIP temporals de descàrrega massiva de documents',4,'CRON',0);
-- Nou camp per guardar la ruta a l'arxiu .zip generat
ALTER TABLE dis_execucio_massiva ADD nom_document VARCHAR(1024);