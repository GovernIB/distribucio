--#538 Agafar plantilla informe de traçabilitat i històric de filesystem

-- Insereix les propietats:
-- es.caib.distribucio.contingut.modificar.titol=

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.modificar.titol','0','Permetre modificar el títol d''una anotació en classificar','GENERAL','20','0','BOOL',null,null);

-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.modificar.titol',false,'Permetre modificar el títol d''una anotació en classificar','GENERAL','20',false,'BOOL',null,null);