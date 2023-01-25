--#567 Poder metadatar assentaments registrals en distribució

-- Insereix les propietats:
-- es.caib.distribucio.permetre.metadades.registre=

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.permetre.metadades.registre','0','Permetre metadatar assentaments registrals','GENERAL','21','0','BOOL',null,null);

CREATE TABLE DIS_METADADA
(
  ID                    NUMBER(19)              NOT NULL,
  CODI                  VARCHAR2(64)            NOT NULL,
  NOM                   VARCHAR2(256)           NOT NULL,
  TIPUS                 NUMBER(10)              NOT NULL,
  MULTIPLICITAT         NUMBER(10)              NOT NULL,
  ACTIVA                NUMBER(1)               NOT NULL,
  READ_ONLY             NUMBER(1)               NOT NULL,
  ORDRE                 NUMBER(10)              NOT NULL,
  DESCRIPCIO            VARCHAR2(1024),
  ENTITAT_ID            NUMBER(19)              NOT NULL,
  VERSION               NUMBER(19)              NOT NULL,
  VALOR 				VARCHAR2(255),
  CREATEDBY_CODI        VARCHAR2(64),
  CREATEDDATE           TIMESTAMP(6),
  LASTMODIFIEDBY_CODI   VARCHAR2(64),
  LASTMODIFIEDDATE      TIMESTAMP(6)
);

ALTER TABLE DIS_METADADA ADD (
  CONSTRAINT DIS_METADADA_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_METADADA_CODI_UK UNIQUE (CODI),
  CONSTRAINT DIS_METADADA_ENTITAT_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES DIS_ENTITAT (ID),
  CONSTRAINT DIS_USUCRE_METADADA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_METADADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));
    
CREATE TABLE DIS_DADA
(
  ID                   NUMBER(19)               NOT NULL,
  ORDRE                NUMBER(10),
  VALOR                VARCHAR2(256 CHAR)       NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  METADADA_ID          NUMBER(19)               NOT NULL,
  REGISTRE_ID          NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  LASTMODIFIEDDATE     TIMESTAMP(6)
);

ALTER TABLE DIS_DADA ADD (
  CONSTRAINT DIS_DADA_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_DADA_MULT_UK UNIQUE (METADADA_ID, REGISTRE_ID, ORDRE),
  CONSTRAINT DIS_REGISTRE_DADA_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES DIS_REGISTRE (ID),
  CONSTRAINT DIS_METADADA_DADA_FK FOREIGN KEY (METADADA_ID) 
    REFERENCES DIS_METADADA (ID),
  CONSTRAINT DIS_USUCRE_DADA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_DADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));
    
CREATE TABLE DIS_DOMINI
(
  ID                   NUMBER(19)           	NOT NULL,
  CODI                 VARCHAR2(64 CHAR)		NOT NULL,
  NOM                  VARCHAR2(256 CHAR)	    NOT NULL,
  DESCRIPCIO           VARCHAR2(256 CHAR),
  CONSULTA             VARCHAR2(256 CHAR)       NOT NULL,
  CADENA               VARCHAR2(256 CHAR)       NOT NULL,
  CONTRASENYA          VARCHAR2(256 CHAR)       NOT NULL,
  ENTITAT_ID           NUMBER(19)           	NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_DOMINI ADD (
  CONSTRAINT DIS_DOMINI_PK PRIMARY KEY (ID));

ALTER TABLE DIS_DOMINI ADD (
  CONSTRAINT DIS_ENTITAT_DOMINI_Fk FOREIGN KEY (ENTITAT_ID) 
    REFERENCES DIS_ENTITAT (ID));
    
-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.permetre.metadades.registre',false,'Permetre metadatar assentaments registrals','GENERAL','21',false,'BOOL',null,null);

CREATE TABLE DIS_METADADA
(
  ID                    BIGSERIAL              			NOT NULL,
  CODI                  character varying(64)           NOT NULL,
  NOM                   character varying(256)          NOT NULL,
  TIPUS                 BIGSERIAL              			NOT NULL,
  MULTIPLICITAT         BIGSERIAL              			NOT NULL,
  ACTIVA                boolean               			NOT NULL,
  READ_ONLY             boolean               			NOT NULL,
  ORDRE                 BIGSERIAL              			NOT NULL,
  DESCRIPCIO            character varying(1024),
  ENTITAT_ID            BIGSERIAL              			NOT NULL,
  VERSION               bigint              			NOT NULL,
  VALOR 				character varying(255),
  CREATEDBY_CODI        character varying(64),
  CREATEDDATE           timestamp without time zone,
  LASTMODIFIEDBY_CODI   character varying(64),
  LASTMODIFIEDDATE      timestamp without time zone
);

ALTER TABLE DIS_METADADA ADD (
  CONSTRAINT DIS_METADADA_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_METADADA_CODI_UK UNIQUE (CODI),
  CONSTRAINT DIS_METADADA_ENTITAT_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES DIS_ENTITAT (ID),
  CONSTRAINT DIS_USUCRE_METADADA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_METADADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));
    
CREATE TABLE DIS_DADA
(
  ID                   BIGSERIAL               			NOT NULL,
  ORDRE                BIGSERIAL,
  VALOR                character varying(256 CHAR)      NOT NULL,
  VERSION              bigint               			NOT NULL,
  METADADA_ID          BIGSERIAL              			NOT NULL,
  REGISTRE_ID          BIGSERIAL               			NOT NULL,
  CREATEDBY_CODI       character varying(64 CHAR),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64 CHAR),
  LASTMODIFIEDDATE     timestamp without time zone
);

ALTER TABLE DIS_DADA ADD (
  CONSTRAINT DIS_DADA_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_DADA_MULT_UK UNIQUE (METADADA_ID, REGISTRE_ID, ORDRE),
  CONSTRAINT DIS_REGISTRE_DADA_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES DIS_REGISTRE (ID),
  CONSTRAINT DIS_METADADA_DADA_FK FOREIGN KEY (METADADA_ID) 
    REFERENCES DIS_METADADA (ID),
  CONSTRAINT DIS_USUCRE_DADA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_DADA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));
    
CREATE TABLE DIS_DOMINI
(
  ID                   BIGSERIAL           				NOT NULL,
  CODI                 character varying(64 CHAR)		NOT NULL,
  NOM                  character varying(256 CHAR)	    NOT NULL,
  DESCRIPCIO           character varying(256 CHAR),
  CONSULTA             character varying(256 CHAR)      NOT NULL,
  CADENA               character varying(256 CHAR)      NOT NULL,
  CONTRASENYA          character varying(256 CHAR)      NOT NULL,
  ENTITAT_ID           BIGSERIAL          				NOT NULL,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(64 CHAR),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64 CHAR)
);

ALTER TABLE DIS_DOMINI ADD (
  CONSTRAINT DIS_DOMINI_PK PRIMARY KEY (ID));

ALTER TABLE DIS_DOMINI ADD (
  CONSTRAINT DIS_ENTITAT_DOMINI_Fk FOREIGN KEY (ENTITAT_ID) 
    REFERENCES DIS_ENTITAT (ID));
