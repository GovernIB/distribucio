-- #418 Variables configurables a nivell d'entitat. 
-- Script per afegir una nova columna a la bbdd on es mostra per mitjà 
-- d'un booleà si és una propietat configurable

ALTER TABLE DIS_CONFIG ADD entitat_codi character varying(64);
ALTER TABLE DIS_CONFIG ADD CONFIGURABLE BOOLEAN DEFAULT FALSE;
ALTER TABLE DIS_MON_INT ADD CODI_ENTITAT character varying(64);

UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.anotacions.permetre.reservar';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.favorits';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.coneixement';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.mostrar.permisos';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.arbre.nivell';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.sobreescriure.anotacions.duplicades';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.concsv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.email.remitent';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.aplicacio.codi';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.csv.definicio';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.document.versionable';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.suporta.metadades';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.csv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.connect';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.read';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.classificacio';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.serie.documental';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.connect.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.request.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.filesystem.base.dir';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.signarAnnexos';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.portafib.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.validatesignature.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.debug';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.applicationID';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.printxml';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.TransformersTemplatesPath';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.tasca.guardar.annexos.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.tasca.enviar.anotacions.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.tasca.aplicar.regles.max.reintents';

-- #463 Posar annexos amb firma no vàlida com a esborrany a l'arxiu 
-- Noves columens per guardar l'estat de la validació i la descripció de l'error
 
ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    VAL_FIRMA_ESTAT VARCHAR2(64 CHAR) 
);
ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    VAL_FIRMA_ERROR VARCHAR2(1000 CHAR) 
);

-- Insereix la propietat per desar els annexos amb firmes invàlides com esborrany o no
-- es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany',null,'Annexos amb firmes invàlides com esborranys. Per defecte és fals','SCHEDULLED_ARXIU','5',false,'BOOL',null,null);


-- #465 Reintentar processament d'anotacions amb estat 'Processada al backoffice amb errors

-- Insereix un nou grup de tasques en segon pla
Insert into DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) 
	   values ('SCHEDULLED_BACKOFFICE_ERRORS', 'SCHEDULLED', '21', 'Tasca periòdica de reintentar anotacions processades amb errors al backoffice');

-- Insereix dos noves propietats pel nou grup creat (SCHEDULLED_BACKOFFICE_ERRORS)	   
Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) 
	   values ('es.caib.distribucio.backoffice.interval.temps.reintentar.processament', null, 'Interval de temps entre les execucions de la tasca(ms)', 'SCHEDULLED_BACKOFFICE_ERRORS', '1', false, 'INT', null, false, null, null);	   
Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) 
	   values ('es.caib.distribucio.backoffice.reintentar.processament.max.reintents', null, 'Nombre màxim de reintents per reintentar el processament al backoffice', 'SCHEDULLED_BACKOFFICE_ERRORS', '2', false, 'INT', null, false, null, null);
  
	   
-- Nous índexos per optimització de consultes
CREATE INDEX DIS_CONTLOG_CONTLOGPARAM_FK_I ON DIS_CONT_LOG_PARAM(CONT_LOG_ID);
CREATE INDEX DIS_I_MON_INT_PARAM_MON ON DIS_MON_INT_PARAM(MON_INT_ID);
CREATE INDEX DIS_I_REG_EXP_UUID ON DIS_REGISTRE (EXPEDIENT_ARXIU_UUID,ID);
CREATE INDEX DIS_I_CONTINGUT_ID_ESBORRAT ON DIS_CONTINGUT (ID,ESBORRAT);


-- #472 Crear API REST per canvi i consulta d'anotacions amb informació de annexos invàlids, 
-- modificar la llibreria d'utilitats
-- Crea una nova columna per guardar i mostrar l'estat del document.
-- Crea una columna per guardar el recompte d'annexos en estat d'esborrany

ALTER TABLE DIS_REGISTRE_ANNEX ADD ARXIU_ESTAT character varying(20);
ALTER TABLE DIS_REGISTRE ADD ANNEXOS_ESTAT_ESBORRANY integer DEFAULT 0;