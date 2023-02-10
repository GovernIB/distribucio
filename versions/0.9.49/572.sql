--#572: Poder assignar anotacions a usuaris d'una bústia

-- Insereix les propietats:
-- es.caib.distribucio.assignar.anotacions=

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.assignar.anotacions','0','Permetre assignar una anotació a un usuari','GENERAL','22','0','BOOL',null,null);

-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.assignar.anotacions',false,'Permetre assignar una anotació a un usuari','GENERAL','22',false,'BOOL',null,null);