INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.email.backoffice.responsable.temps','1440','Temps entre correus al responsable del backoffice (minuts)','EMAIL','3','0','INT',null,null);

ALTER TABLE DIS_BACKOFFICE
    ADD (
        ENVIAR_EMAIL_RESPONSABLE   NUMBER(1),
        EMAIL_RESPONSABLE          VARCHAR2(100 CHAR),
        DARRER_EMAIL               TIMESTAMP(6)
    );