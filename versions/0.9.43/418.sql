-- #418 Variables configurables a nivell d'entitat. 
-- Script per afegir una nova columna a la bbdd on es mostra per mitjà 
-- d'un booleà si és una propietat configurable

-- Oracle
Insert into DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) 
	   values ('SCHEDULLED_BACKOFFICE_ERRORS', 'SCHEDULLED', '21', 'Tasca periòdica de reintentar anotacions processades amb errors al backoffice');
	   
-- Postgresql	   
Insert into DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) 
	   values ('SCHEDULLED_BACKOFFICE_ERRORS', 'SCHEDULLED', '21', 'Tasca periòdica de reintentar anotacions processades amb errors al backoffice');

	   
	   
-- Insereix dos noves propietats pel nou grup creat (SCHEDULLED_BACKOFFICE_ERRORS)
	   
-- Oracle
Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) 
	   values ('es.caib.distribucio.backoffice.interval.temps.reintentar.processament', null, 'Interval de temps entre les execucions de la tasca(ms)', 'SCHEDULLED_BACKOFFICE_ERRORS', '1', '0', 'INT', null, '0', null, null);

Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) 
	   values ('es.caib.distribucio.backoffice.reintentar.processament.max.reintents', null, 'Nombre màxim de reintents per reintentar el processament al backoffice', 'SCHEDULLED_BACKOFFICE_ERRORS', '2', '0', 'INT', null, '0', null, null);

	   
-- Postgresql
Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) 
	   values ('es.caib.distribucio.backoffice.interval.temps.reintentar.processament', null, 'Interval de temps entre les execucions de la tasca(ms)', 'SCHEDULLED_BACKOFFICE_ERRORS', '1', 'false', 'INT', null, 'false', null, null);
	   
Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) 
	   values ('es.caib.distribucio.backoffice.reintentar.processament.max.reintents', null, 'Nombre màxim de reintents per reintentar el processament al backoffice', 'SCHEDULLED_BACKOFFICE_ERRORS', '2', 'false', 'INT', null, 'false', null, null);
   