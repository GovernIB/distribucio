-- Log d'excepcions a base de dades: abans es guardaven en un array en memòria i es perdien en reiniciar.
CREATE TABLE DIS_EXCEPCIO_LOG (
    ID                  NUMBER(19,0) NOT NULL,
    DATA                TIMESTAMP NOT NULL,
    TIPUS               VARCHAR2(512 CHAR),
    OBJECT_ID           VARCHAR2(512 CHAR),
    OBJECT_CLASS        VARCHAR2(512 CHAR),
    URI                 VARCHAR2(1024 CHAR),
    ORIGEN              VARCHAR2(512 CHAR),
    PARAM1              VARCHAR2(512 CHAR),
    PARAM2              VARCHAR2(512 CHAR),
    MESSAGE             VARCHAR2(4000 CHAR),
    STACKTRACE          CLOB,
    ENTITAT_CODI        VARCHAR2(64 CHAR),
    CREATEDBY_CODI      VARCHAR2(64 CHAR),
    CREATEDDATE         TIMESTAMP,
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE    TIMESTAMP,
    CONSTRAINT DIS_EXCEPCIO_LOG_PK PRIMARY KEY (ID)
);
-- PostgreSQL: NUMBER(19,0) -> BIGINT, VARCHAR2(n CHAR) -> VARCHAR(n) i CLOB -> TEXT.

-- La tasca d'esborrat i el llistat filtren i ordenen per DATA.
CREATE INDEX DIS_EXCEPCIO_LOG_DATA_I ON DIS_EXCEPCIO_LOG(DATA);

GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_EXCEPCIO_LOG TO WWW_DISTRIBUCIO;
-- PostgreSQL: no cal el GRANT.

-- Propietats de la tasca periòdica d'esborrat de les excepcions antigues.
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_EXCEPCIONS','SCHEDULLED',23,'Tasca periòdica d''esborrat de les excepcions antigues');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES
    ('es.caib.distribucio.tasca.excepcions.esborrar.antics.cron','0 0 3 * * *','Expressió cron de la tasca d''esborrat de les excepcions antigues. Per defecte cada dia a les 3:00 (0 0 3 * * *)','SCHEDULLED_EXCEPCIONS',0,0,'CRON',0);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES
    ('es.caib.distribucio.tasca.excepcions.esborrar.antics.dies','45','Dies màxim d''antiguitat de les excepcions guardades. Per defecte 45 dies','SCHEDULLED_EXCEPCIONS',1,0,'INT',0);
-- PostgreSQL: els camps JBOSS_PROPERTY i CONFIGURABLE són booleans, s'han de posar a false en lloc de 0.
