--#591 Revisar error de grandària en la validació de firmes d'annexos

-- Insereix les propietats:

-- es.caib.distribucio.plugin.validatesignature.maxBytes=

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.maxBytes','20971520','LLargada màxima en bytes que es poden enviar a validar. Per exemple: 20971520 (20Mb)','VALID_SIGN','8','0','INT',null,null);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.maxBytes';

-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.maxBytes','20971520','LLargada màxima en bytes que es poden enviar a validar. Per exemple: 20971520 (20Mb)','VALID_SIGN','8',false,'INT',null,null);
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.maxBytes';
