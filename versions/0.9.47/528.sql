-- Nova propietat configurable a nivell de entitat per establir el temps 
-- d'actualització automàtica per actualitzar la taula de procediments

-- ORACLE 
Insert into DIS_CONFIG_GROUP 
	(CODE, PARENT_CODE, POSITION, DESCRIPTION) 
values 
	('SCHEDULLED_PROCEDIMENT', 'SCHEDULLED', '22', 'Tasca periòdica per actualitzar la taula de procediments');


Insert into DIS_CONFIG 
	(KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) 
values 
	('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments',null,'Interval de dies entre execucions de la tasca (per defecte 25 dies)','SCHEDULLED_PROCEDIMENT','7','0','INT', NULL, '1',null,null);
	
	
	
-- POSTGRESS 
Insert into DIS_CONFIG_GROUP 
	(CODE, PARENT_CODE, POSITION, DESCRIPTION) 
values 
	('SCHEDULLED_PROCEDIMENT', 'SCHEDULLED', '22', 'Tasca periòdica per actualitzar la taula de procediments');


Insert into DIS_CONFIG 
	(KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) 
values 
	('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments',null,'Interval de dies entre execucions de la tasca (per defecte 25 dies)','SCHEDULLED_PROCEDIMENT','7',false,'INT',NULL,true,null,null);
