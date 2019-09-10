-- #94 Implementació d'un sistema d'avisos 
CREATE TABLE DIS_AVIS
(
  ID                   BIGSERIAL               			 NOT NULL,
  ASSUMPTE             character varying(256)            NOT NULL,
  MISSATGE             character varying(2048)           NOT NULL,
  DATA_INICI           timestamp without time zone(6)    NOT NULL,
  DATA_FINAL           timestamp without time zone(6)    NOT NULL,
  ACTIU                boolean                		     NOT NULL,
  AVIS_NIVELL          character varying(10)             NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);

ALTER TABLE DIS_AVIS ADD CONSTRAINT DIS_AVIS_PK PRIMARY KEY (ID);

CREATE INDEX DIS_AVIS_DATA_INICI_I ON DIS_AVIS(DATA_INICI);
CREATE INDEX DIS_AVIS_DATA_FINAL_I ON DIS_AVIS(DATA_FINAL);


-- #103 Afegir el camp "backoffice" a les anotacions de registre i a les regles
ALTER TABLE DIS_REGLA ADD COLUMN BACKOFFICE_CODI CHARACTER VARYING(20);
ALTER TABLE DIS_REGISTRE ADD COLUMN BACK_CODI CHARACTER VARYING(20);


-- #118: Nous camps pel WS d'alta d'anotacions de registre 
ALTER TABLE DIS_REGISTRE ADD COLUMN PRESENCIAL BOOLEAN;
ALTER TABLE DIS_REGISTRE_INTER ADD COLUMN CODI_DIRE CHARACTER VARYING(64);

-- #123 Eliminar obligatorietat del camp Tipo Asunto
ALTER TABLE DIS_REGISTRE ALTER COLUMN ASSUMPTE_TIPUS_CODI DROP NOT NULL;

--#130: Falta el codi d'òrgan pels interessats de tipus administració pública 
ALTER TABLE DIS_REGISTRE_INTER ADD ORGAN_CODI CHARACTER VARYING(9);