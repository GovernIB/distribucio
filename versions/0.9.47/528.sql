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
	('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments',null,'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels procediments en segon pla. Per defecte s''aplicarà el cron corresponent per actualitzar cada divendres a les 15:30h','SCHEDULLED_PROCEDIMENT','7','0','TEXT', NULL, '0',null,null);
	
	
	
-- POSTGRESS 
Insert into DIS_CONFIG_GROUP 
	(CODE, PARENT_CODE, POSITION, DESCRIPTION) 
values 
	('SCHEDULLED_PROCEDIMENT', 'SCHEDULLED', '22', 'Tasca periòdica per actualitzar la taula de procediments');


Insert into DIS_CONFIG 
	(KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) 
values 
	('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments',null,'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels procediments en segon pla. Per defecte s''aplicarà el cron corresponent per actualitzar cada divendres a les 15:30h','SCHEDULLED_PROCEDIMENT','7',false,'TEXT',NULL,false,null,null);
