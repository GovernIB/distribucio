--#538 Agafar plantilla informe de traçabilitat i històric de filesystem

-- Insereix les propietats:
-- es.caib.distribucio.plantilla.informe.trasabilitat.ca=
-- es.caib.distribucio.plantilla.informe.trasabilitat.es=
-- es.caib.distribucio.plantilla.historic.ca=
-- es.caib.distribucio.plantilla.historic.es=

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.ca',null,'Ruta plantilla informe de traçabilitat en català','GENERAL','16','0','TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.es',null,'Ruta plantilla informe de traçabilitat en castellà','GENERAL','17','0','TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.ca',null,'Ruta plantilla històric en català','GENERAL','18','0','TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.es',null,'Ruta plantilla històric en castellà','GENERAL','19','0','TEXT',null,null);

-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.ca',null,'Ruta plantilla informe de traçabilitat en català','GENERAL','16',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.es',null,'Ruta plantilla informe de traçabilitat en castellà','GENERAL','17',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.ca',null,'Ruta plantilla històric en català','GENERAL','18',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.es',null,'Ruta plantilla històric en castellà','GENERAL','19',false,'TEXT',null,null);
