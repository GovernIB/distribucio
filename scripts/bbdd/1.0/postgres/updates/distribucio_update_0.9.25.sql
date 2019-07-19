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