-- #423 Millorar el monitor d'integracions 
-- Creació de les taules pel monitor i els paràmetres

-- Oracle

CREATE TABLE DIS_MON_INT
(	
   	ID					NUMBER(19,0) 		NOT NULL,
	CODI 				VARCHAR2(64 CHAR) 	NOT NULL, 
	DATA 				TIMESTAMP (6), 
	DESCRIPCIO 			VARCHAR2(1024 CHAR), 
	TIPUS 				VARCHAR2(10 CHAR), 
	TEMPS_RESPOSTA		NUMBER(19,0), 
	ESTAT				VARCHAR2(5 CHAR),
	CODI_USUARI			VARCHAR2(64 CHAR),
	ERROR_DESCRIPCIO	VARCHAR2(1024 CHAR),
	EXCEPCIO_MSG		VARCHAR2(1024 CHAR),
	EXCEPCIO_STACKTRACE	VARCHAR2(2048 CHAR)
);

ALTER TABLE DIS_MON_INT ADD (
  CONSTRAINT DIS_MON_INT_PK PRIMARY KEY (ID));
  


GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_MON_INT TO WWW_DISTRIBUCIO;


CREATE TABLE DIS_MON_INT_PARAM
(	
   	ID					NUMBER(19,0) 			NOT NULL, 
   	MON_INT_ID			NUMBER(19)				NOT NULL,
	NOM		 			VARCHAR2(64 CHAR) 		NOT NULL,
	DESCRIPCIO 			VARCHAR2(1024 CHAR)
);

ALTER TABLE DIS_MON_INT_PARAM ADD (
  CONSTRAINT DIS_MON_INT_PARAM_PK PRIMARY KEY (ID));

ALTER TABLE DIS_MON_INT_PARAM ADD CONSTRAINT DIS_MONINTPARAM_MONINT_FK 
	FOREIGN KEY (MON_INT_ID) REFERENCES DIS_MON_INT(ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_MON_INT_PARAM TO WWW_DISTRIBUCIO;

-- Insereix la propietat per la nova tasca periòdica.
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_MON_INT','SCHEDULLED','29','Tasca periòdica d''esborrat de dades antigues del Monitor d''Integracions');
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.periode', '3600000', 'Iterval de temps entre les execucions de la tasca (ms). Per defecte 3600000 ms', 'SCHEDULLED_MON_INT', 0, 0, 'INT');
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.dies', '30', 'Dies màxim d''antigitat per guardar registres. Per defecte 30 dies.', 'SCHEDULLED_MON_INT', 1, 0, 'INT');




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
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_MON_INT','SCHEDULLED','29','Tasca periòdica d''esborrat de dades antigues del Monitor d''Integracions');
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.periode', '3600000', 'Iterval de temps entre les execucions de la tasca (ms). Per defecte 3600000 ms', 'SCHEDULLED_MON_INT', 0, 0, 'INT');
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.dies', '30', 'Dies màxim d''antigitat per guardar registres. Per defecte 30 dies.', 'SCHEDULLED_MON_INT', 1, 0, 'INT');


