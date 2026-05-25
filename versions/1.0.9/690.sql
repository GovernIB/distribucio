INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (1,'GENERAL','es.caib.distribucio.fitxers', null,'Ruta on es gordarán els documents',0,'TEXT',0);

INSERT INTO DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES ('GENERAL_EXPORT_ZIP', 'GENERAL', 27, 'Configuració d''exportació masiva d''annexos a zip');

INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.mida.max', 10,'Mida màxima del fitxer ZIP generat (MB)',0,'INT',0);

INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.exec.max', 5,'Nombre d''execucions màximes permeses per usuari',1,'INT',0);

INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.enabled', 'false','Habilitar la exportació massiva d''annexos de registres',2,'BOOL',0);

INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.caducitat', 10,'Nombre de dies que es gordará el fitxer ZIP',3,'INT',0);

INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (0,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.caducitat.cron', '0 0 0 * * *','Tasca periòdica per esborrar documents .ZIP temporals de descàrrega massiva de documents',4,'CRON',0);

ALTER TABLE dis_execucio_massiva ADD nom_document VARCHAR(64);