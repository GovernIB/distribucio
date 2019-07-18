CREATE TABLE DIS_AVIS
(
  ID                   NUMBER(19)               NOT NULL,
  ASSUMPTE             VARCHAR2(256)            NOT NULL,
  MISSATGE             VARCHAR2(2048)           NOT NULL,
  DATA_INICI           TIMESTAMP(6)             NOT NULL,
  DATA_FINAL           TIMESTAMP(6)             NOT NULL,
  ACTIU                NUMBER(1)                NOT NULL,
  AVIS_NIVELL         VARCHAR2(10)             NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6)

);