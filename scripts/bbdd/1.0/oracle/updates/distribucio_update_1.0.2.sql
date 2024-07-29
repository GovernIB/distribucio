-- Distribucio 1.0.2

-- #702 El codi d'entitat que apareix en el monitor d'integracions no és correcte

-- Oracle

-- Inserció de les configuracions que falten.

--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.path=/app/caib/crypto/preprod-dgdt.jks
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.path',null,'Ruta al certificat d''autenticació','VALID_SIGN','9','1','TEXT',null,null);
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.type=JKS
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.type',null,'Tipus de magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',8,1,'TEXT',1);
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.password=****
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.password',null,'Password del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',9,1,'CREDENTIALS',1);
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias=preprod-dgmad
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias',null,'Alies del certificat a emprar del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',10,1,'TEXT',1);
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.password=****
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.password',null,'Password del certificat del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',11,1,'CREDENTIALS',1);

-- Es fixen les noves propietats com a multi entitat
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.path';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.type';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.password';

-- Canvi de plugin
UPDATE DIS_CONFIG SET VALUE = 'org.fundaciobit.pluginsib.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin' WHERE KEY = 'plugins.validatesignature.class';

-- Canvi de claus en les propietats
UPDATE DIS_CONFIG
SET KEY = REPLACE(KEY, 'plugins.validatesignature','pluginsib.validatesignature')
WHERE 
	KEY like '%plugins.validatesignature.%' 
	AND NOT KEY LIKE '%plugins.validatesignature.maxBytes';

-- ROLLBACK
-- UPDATE DIS_CONFIG SET VALUE = 'org.fundaciobit.plugins.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin' WHERE KEY = 'plugins.validatesignature.class';
--UPDATE DIS_CONFIG
--SET KEY = REPLACE(KEY, 'pluginsib.validatesignature','plugins.validatesignature')
--WHERE 
--	KEY like '%plugins.validatesignature.%' 
--	AND NOT KEY LIKE '%plugins.validatesignature.maxBytes';


-- Creació de l'índex per la consulta d'anotacions pendents per bústia
CREATE INDEX DIS_I_REGISTRE_ID_PENDENT ON DIS_REGISTRE (ID, PENDENT);

	
