-- Distribucio 1.0.9

-- #690 Permetre exportar els justificants i els annexos de vàries anotacions de forma simultània
-- Afegeix els paràmetres de configuració de màxim de grandària, execucions per dia, ruta i configuració de la tasca d'esborrat d'arxius.
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (true,'GENERAL','es.caib.distribucio.fitxers', null,'Ruta de fitxers de Distribucio al servidor',0,'TEXT',false);
INSERT INTO DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES 
	('GENERAL_EXPORT_ZIP', 'GENERAL', 27, 'Configuració d''exportació masiva d''annexos a zip');
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (false,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.mida.max', 10,'Mida màxima del fitxer ZIP generat (MB)',0,'INT',false);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (false,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.exec.max', 5,'Nombre d''execucions màximes permeses per usuari',1,'INT',false);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (false,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.enabled', 'false','Habilitar l''exportació massiva d''annexos de registres',2,'BOOL',false);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (false,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.caducitat', 10,'Nombre de dies que es guardarà el fitxer ZIP abans de que s''esborri automàticament',3,'INT',false);
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) VALUES
    (false,'GENERAL_EXPORT_ZIP','es.caib.distribucio.exportar.annex.zip.caducitat.cron', '0 0 0 * * *','Tasca periòdica per esborrar documents .ZIP temporals de descàrrega massiva de documents',4,'CRON',false);
-- Nou camp per guardar la ruta a l'arxiu .zip generat
ALTER TABLE dis_execucio_massiva ADD nom_document VARCHAR(1024);

-- #850 Afegir filtre i columna "Comú" a la gestió de procediments i serveis
-- Nova columna per guardar si el servei o procediment és comú.
ALTER TABLE dis_servei ADD comu boolean DEFAULT false NOT NULL;
ALTER TABLE dis_procediment ADD comu boolean DEFAULT false NOT NULL;

-- #852 La consulta del localitzador d'annexos és molt lenta
-- Afegeix un índex a la columan de d'estat a l'Arxiu
CREATE INDEX DIS_ESTAT_ARX_REGANX_FK_I ON DIS_REGISTRE_ANNEX(ARXIU_ESTAT);

-- #855 Afegir columna i filtre de número de registre al monitor
-- Afegeix la columna al monitor
ALTER TABLE dis_mon_int ADD numero_registre varchar(300);

-- #860 Revisar el recompte d'errors de les pipelles del monitor
-- Esborra el paràmetre de configuració per comptar errors del monitor
DELETE FROM DIS_CONFIG WHERE KEY = 'es.caib.distribucio.monitor.integracio.errors.temps';


