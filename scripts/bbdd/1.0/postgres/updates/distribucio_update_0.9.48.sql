-- Distribucio 0.9.48

-- #557 Nova columna a la taula 'DIS_REGLA' que ens indica si s'aplicarà a un registre presencial o no. 
ALTER TABLE DIS_REGLA ADD COLUMN PRESENCIAL smallint;

--#558 Poder canviar títol en classificar
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.modificar.titol',false,'Permetre modificar el títol d''una anotació en classificar','GENERAL','20',false,'BOOL',null,null);

--#567 Poder metadatar assentaments registrals en distribució
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
  NO_APLICA				boolean               			DEFAULT 0,
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


-- #576 Revisar per què les propietats fixades a null no s'actualitzen
-- Actualitza la clau antiga amb la nova clau
UPDATE DIS_CONFIG
SET KEY  = REPLACE(KEY, 'csv.definicio', 'csv_generation_definition')
WHERE KEY LIKE  'es.caib.distribucio%.plugin.arxiu.caib.csv.definicio';

