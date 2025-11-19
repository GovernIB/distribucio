-- Afegir límits de respostes de canvi d'estat per aplicació #761
CREATE TABLE DIS_LIMIT_CANVI_ESTAT (
    ID NUMBER(19) NOT NULL,
    USUARI_CODI VARCHAR2(64 char) NOT NULL,
    DESCRIPCIO VARCHAR2(255 char) NOT NULL,
    LIM_MIN_LAB NUMBER,
    LIM_MIN_NOLAB NUMBER,
    LIM_DIA_LAB NUMBER,
    LIM_DIA_NOLAB NUMBER
);
ALTER TABLE DIS_LIMIT_CANVI_ESTAT ADD (
  CONSTRAINT DIS_LIMIT_CANVI_ESTAT_PK PRIMARY KEY (ID));

Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GENERAL_LIMIT_CANVI_ESTAT','GENERAL','26','Configuració de limit de canvis d''estat');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.horari.laboral', '* * 7-16 * * MON-FRI', 'Horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '1', '0', 'CRON');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.minut.laboral', '4', 'Limit canvi d''estat per minut en horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '2', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.minut.no.laboral', '8', 'Limit canvi d''estat per minut en horari no laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '3', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.dia.laboral', '8000', 'Limit canvi d''estat per dia en horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '4', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.dia.no.laboral', '10000', 'Limit canvi d''estat per dia en horari no laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '5', '0', 'INT');