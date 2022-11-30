-- #528 Nova propietat configurable a nivell de entitat per establir el temps d'actualització automàtica per actualitzar la taula de procediments

Insert into DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) values ('SCHEDULLED_PROCEDIMENT', 'SCHEDULLED', '22', 'Tasca periòdica per actualitzar la taula de procediments');

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments',null,'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels procediments en segon pla. Per defecte s''aplicarà el cron corresponent per actualitzar cada divendres a les 15:30h','SCHEDULLED_PROCEDIMENT','7',false,'TEXT',NULL,false,null,null);
	
--#538 Agafar plantilla informe de traçabilitat i històric de filesystem

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.ca',null,'Ruta plantilla informe de traçabilitat en català','GENERAL','16',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.es',null,'Ruta plantilla informe de traçabilitat en castellà','GENERAL','17',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.ca',null,'Ruta plantilla històric en català','GENERAL','18',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.es',null,'Ruta plantilla històric en castellà','GENERAL','19',false,'TEXT',null,null);

-- #539 Nova columna a la taula 'DIS_PROCEDIMENT' que ens  indica si el procediment està vigent o obsolet. 

ALTER TABLE DIS_PROCEDIMENT ADD COLUMN ESTAT VARCHAR(20) DEFAULT 'VIGENT' NOT NULL;


