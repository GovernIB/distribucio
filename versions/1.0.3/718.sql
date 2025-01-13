--#718 No canviar de processada a pendent les anotacions reenviades

-- Insereix les propietats:
-- es.caib.distribucio.anotacions.reenviades.mantenir.estat=

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacions.reenviades.mantenir.estat','0','Indica si mantenir l''estat actual de l''anotació a l''hora de reenviar','GENERAL','24','0','BOOL',null,null);

-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacions.reenviades.mantenir.estat',false,'Indica si mantenir l''estat actual de l''anotació a l''hora de reenviar','GENERAL','24',false,'BOOL',null,null);