-- #425 Afegeix l'entorn a l'assumpte del correu enviat

-- Insereix la propietat per l'assumpte.
-- es.caib.distribucio.default.user.entorn

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.default.user.entorn',null,'Entorn on es troba l''aplicació','GENERAL','15','0','TEXT',null,null);

-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.default.user.entorn',null,'Entorn on es troba l''aplicació','GENERAL','15',false,'TEXT',null,null);