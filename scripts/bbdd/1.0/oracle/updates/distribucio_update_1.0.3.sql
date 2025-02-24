-- Distribucio 1.0.3

-- Afegir un plugin de consulta de serveis #685

CREATE TABLE DIS_SERVEI (	
	ID 		NUMBER(19,0) NOT NULL , 
	CODI 	VARCHAR2(64 CHAR) NOT NULL , 
	NOM VARCHAR2(256 CHAR), 
	CODISIA VARCHAR2(64 CHAR), 
	ID_UNITAT_ORGANITZATIVA NUMBER(19,0) NOT NULL , 
	ENTITAT NUMBER(19,0) NOT NULL , 
	ESTAT VARCHAR2(20 CHAR) DEFAULT 'VIGENT' NOT NULL , 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6)
 );

 ALTER TABLE DIS_SERVEI ADD (
  CONSTRAINT DIS_SERVEI_PK PRIMARY KEY (ID));

-- dis_procediment foreign keys

ALTER TABLE dis_servei ADD CONSTRAINT dis_servei_entitat_fk FOREIGN KEY (entitat) REFERENCES dis_entitat(id);
ALTER TABLE dis_servei ADD CONSTRAINT dis_servei_unitat_fk FOREIGN KEY (id_unitat_organitzativa) REFERENCES dis_unitat_organitzativa(id);

-- Propietats configurables pels seveis
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SERVEIS',null,'23','Plugin de consulta de serveis');

INSERT INTO dis_config (key, value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position)
VALUES('es.caib.distribucio.plugin.servei.class', 'es.caib.distribucio.plugin.caib.servei.ServeiPluginRolsac', 'Classe de plugin de serveis', 'SERVEIS', 0, 'TEXT', NULL, 1, 'dis_super', null, 0);

INSERT INTO dis_config
(key, value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position)
VALUES('es.caib.distribucio.plugin.servei.rolsac.service.url', NULL, 'Url per a accedir serveis', 'SERVEIS', 1, 'TEXT', NULL, 1, NULL, NULL, 1);

INSERT INTO dis_config
(key, value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position)
VALUES('es.caib.distribucio.plugin.servei.rolsac.service.username', NULL, 'Nom de l''usuari per a accedir serveis', 'SERVEIS', 1, 'TEXT', NULL, 1, NULL, NULL, 2);

INSERT INTO dis_config
(key, value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position)
VALUES('es.caib.distribucio.plugin.servei.rolsac.service.password', NULL, 'Password per a accedir serveis', 'SERVEIS', 1, 'CREDENTIALS', NULL, 0, NULL, NULL, 3);


-- Permetre classifcar una anotació dins d'un servei #686

-- Oracle
ALTER TABLE dis_registre ADD servei_codi varchar2(64 CHAR) NULL;
ALTER TABLE dis_regla ADD servei_codi varchar2(1024 CHAR) NULL;

--#718 No canviar de processada a pendent les anotacions reenviades
-- Insereix les propietats:
-- es.caib.distribucio.anotacions.reenviades.mantenir.estat=
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacions.reenviades.mantenir.estat','0','Indica si mantenir l''estat actual de l''anotació a l''hora de reenviar','GENERAL','24','0','BOOL',null,null);

	
--#719 Augmentar la longitud del camp comentaris
ALTER TABLE DIS_CONT_COMMENT ADD TEXT_NEW CLOB;
UPDATE DIS_CONT_COMMENT SET TEXT_NEW = TEXT;
ALTER TABLE DIS_CONT_COMMENT DROP COLUMN TEXT;
ALTER TABLE DIS_CONT_COMMENT RENAME COLUMN TEXT_NEW TO TEXT;

-- Modificar la sincronització de procediments i servis amb Rolsac #720

INSERT INTO DIS_CONFIG
("KEY", VALUE, DESCRIPTION, GROUP_CODE, "POSITION", JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE)
VALUES('es.caib.distribucio.plugin.rolsac.service.url', 'https://dev.caib.es/rolsac/api/rest/v1', 'Url per a accedir a Rolsac', 'PROCEDIMENTS', 1, 1, 'TEXT', NULL, 1, NULL, NULL);

INSERT INTO DIS_CONFIG
("KEY", VALUE, DESCRIPTION, GROUP_CODE, "POSITION", JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE)
VALUES('es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis', '0 0 17 * * *', 'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels serveis en segon pla. Per defecte s''aplicarà el cron corresponent per actualitzar cada divendres a les 15:30h', 'SCHEDULLED_PROCEDIMENT', 7, 0, 'TEXT', 'dis_super', 0, NULL, TIMESTAMP '2025-01-09 15:39:49.120000');

INSERT INTO DIS_CONFIG_GROUP
(CODE, PARENT_CODE, "POSITION", DESCRIPTION)
VALUES('SCHEDULLED_SERVEI', 'SCHEDULLED', 23, 'Tasca periòdica per actualitzar la taula de serveis');

