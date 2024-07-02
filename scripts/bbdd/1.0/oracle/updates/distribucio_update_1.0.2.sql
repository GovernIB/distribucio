-- Distribucio 1.0.2

--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.path=/app/caib/crypto/preprod-dgdt.jks
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.path',null,'Ruta al certificat d''autenticació','VALID_SIGN','9','1','TEXT',null,null);
--Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.path',null,'Path del magatzem de claus amb el certificat per l''autenticació a Afirm@','VALID_SIGN',7,1,'TEXT',1);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.path';

--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.type=JKS
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.type',null,'Tipus de magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',8,1,'TEXT',1);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.type';
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.password=****
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.password',null,'Password del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',9,1,'CREDENTIALS',1);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.password';
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias=preprod-dgmad
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias',null,'Alies del certificat a emprar del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',10,1,'TEXT',1);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias';
--es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.password=****
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.password',null,'Password del certificat del magatzem de claus per l''autenticació a Afirm@','VALID_SIGN',11,1,'CREDENTIALS',1);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.ks.cert.password';

UPDATE dis_config
SET key = REPLACE(key, 'plugins.validatesignature','pluginsib.validatesignature')
where 
	key like '%plugins.validatesignature.class' or
	key like '%plugins.validatesignature.afirmacxf.debug' or
	key like '%plugins.validatesignature.afirmacxf.applicationID' or
	key like '%plugins.validatesignature.afirmacxf.printxml' or
	key like '%plugins.validatesignature.afirmacxf.TransformersTemplatesPath' or
	key like '%plugins.validatesignature.afirmacxf.endpoint' or
	key like '%plugins.validatesignature.afirmacxf.authorization.username' or
	key like '%plugins.validatesignature.afirmacxf.authorization.password' or
	key like '%plugins.validatesignature.afirmacxf.authorization.ks.type' or
	key like '%plugins.validatesignature.afirmacxf.authorization.ks.path' or
	key like '%plugins.validatesignature.afirmacxf.authorization.ks.password' or
	key like '%plugins.validatesignature.afirmacxf.authorization.ks.cert.alias' or
	key like '%plugins.validatesignature.afirmacxf.authorization.ks.cert.password';

insert into dis_config (key,value,configurable,group_code) values ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.ignoreservercertificates','true',1,'VALID_SIGN');
insert into dis_config (key,value,configurable,group_code) values ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.endpoint','https://afirmapre.caib.es/afirmaws/services/DSSAfirmaVerify',1,'VALID_SIGN');
insert into dis_config (key,value,configurable,group_code) values ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.path','/opt/caib/crypto/preprod-dgdt.jks',1,'VALID_SIGN');
insert into dis_config (key,value,configurable,group_code) values ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.type','JKS',1,'VALID_SIGN');
insert into dis_config (key,value,configurable,group_code) values ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.password',null,1,'VALID_SIGN');
insert into dis_config (key,value,configurable,group_code) values ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.cert.alias','preprod-dgmad',1,'VALID_SIGN');
insert into dis_config (key,value,configurable,group_code) values ('es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.cert.password',null,1,'VALID_SIGN');

-- ROLLBACK
--delete from dis_config where key='es.caib.distribucio.pluginsib.validatesignature.afirmacxf.ignoreservercertificates';
--delete from dis_config where key='es.caib.distribucio.pluginsib.validatesignature.afirmacxf.endpoint';
--delete from dis_config where key='es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.path';
--delete from dis_config where key='es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.type';
--delete from dis_config where key='es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.password';
--delete from dis_config where key='es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.cert.alias';
--delete from dis_config where key='es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.ks.cert.password';

--UPDATE dis_config
--SET key = REPLACE(key, 'pluginsib.validatesignature','plugins.validatesignature')
--where 
--	key like '%pluginsib.validatesignature.class' or
--	key like '%pluginsib.validatesignature.afirmacxf.debug' or
--	key like '%pluginsib.validatesignature.afirmacxf.applicationID' or
--	key like '%pluginsib.validatesignature.afirmacxf.printxml' or
--	key like '%pluginsib.validatesignature.afirmacxf.TransformersTemplatesPath' or
--	key like '%pluginsib.validatesignature.afirmacxf.endpoint' or
--	key like '%pluginsib.validatesignature.afirmacxf.authorization.username' or
--	key like '%pluginsib.validatesignature.afirmacxf.authorization.password' or
--	key like '%pluginsib.validatesignature.afirmacxf.authorization.ks.type' or
--	key like '%pluginsib.validatesignature.afirmacxf.authorization.ks.path' or
--	key like '%pluginsib.validatesignature.afirmacxf.authorization.ks.password' or
--	key like '%pluginsib.validatesignature.afirmacxf.authorization.ks.cert.alias' or
--	key like '%pluginsib.validatesignature.afirmacxf.authorization.ks.cert.password';
