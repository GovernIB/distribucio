-- #423 Millorar el monitor d'integracions 
-- Creació de les taules pel monitor i els paràmetres

-- Postgresql

CREATE TABLE DIS_MON_INT
(	
   	ID					BIGSERIAL 		NOT NULL,
	CODI 				character varying(64) 	NOT NULL, 
	DATA 				timestamp without time zone, 
	DESCRIPCIO 			character varying(1024), 
	TIPUS 				character varying(10), 
	TEMPS_RESPOSTA		BIGSERIAL, 
	ESTAT				character varying(4),
	CODI_USUARI			character varying(64),
	ERROR_DESCRIPCIO	character varying(1024),
	EXCEPCIO_MSG		character varying(1024),
	EXCEPCIO_STACKTRACE	character varying(2048)
);

ALTER TABLE DIS_MON_INT ADD (
  CONSTRAINT DIS_MON_INT_PK PRIMARY KEY (ID));
  

CREATE TABLE DIS_MON_INT_PARAM
(	
   	ID					BIGSERIAL 				NOT NULL, 
   	MON_INT_ID			BIGSERIAL				NOT NULL,
	NOM		 			character varying(64) 	NOT NULL,
	DESCRIPCIO 			character varying(1024)
);

ALTER TABLE DIS_MON_INT_PARAM ADD (
  CONSTRAINT DIS_MON_INT_PARAM_PK PRIMARY KEY (ID));

ALTER TABLE DIS_MON_INT_PARAM ADD CONSTRAINT DIS_MONINTPARAM_MONINT_FK 
	FOREIGN KEY (MON_INT_ID) REFERENCES DIS_MON_INT(ID);


--Insereix la propietat per la nova tasca periòdica.
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_MON_INT','SCHEDULLED','20','Tasca periòdica d''esborrat de dades antigues del Monitor d''Integracions');
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.periode', '3600000', 'Iterval de temps entre les execucions de la tasca (ms). Per defecte 3600000 ms', 'SCHEDULLED_MON_INT', 0, false, 'INT');
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.dies', '30', 'Dies màxim d''antigitat per guardar registres. Per defecte 30 dies.', 'SCHEDULLED_MON_INT', 1, false, 'INT');


-- #425 Afegeix l'entorn a l'assumpte del correu enviat

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.default.user.entorn',null,'Entorn on es troba l''aplicació','GENERAL','15',false,'TEXT',null,null);

-- #398 Separar la secció plugins de l'aplicació de les propietats de configuració 
 
DELETE FROM DIS_CONFIG_GROUP WHERE CODE LIKE 'PLUGINS';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 3 WHERE CODE LIKE 'ARXIU';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 4 WHERE CODE LIKE 'UNITATS';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 5 WHERE CODE LIKE 'GES_DOC';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 6 WHERE CODE LIKE 'USUARIS';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 7 WHERE CODE LIKE 'DISTRIBUCIO';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 8 WHERE CODE LIKE 'PROCEDIMENTS';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 9 WHERE CODE LIKE 'DADES_EXTERNES';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 10 WHERE CODE LIKE 'SIGNATURA';
UPDATE DIS_CONFIG_GROUP SET PARENT_CODE = NULL, POSITION = 11 WHERE CODE LIKE 'VALID_SIGN';
