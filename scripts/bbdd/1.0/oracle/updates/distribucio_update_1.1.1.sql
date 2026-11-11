-- Distribucio 1.1.1

-- #669 Canvia a interfície REACT
-- Afegir camp de darrera conexió de l'usuari
ALTER TABLE DIS_USUARI ADD DARRER_PERIODE TIMESTAMP;
ALTER TABLE DIS_USUARI ADD DARRERA_ACTIVITAT TIMESTAMP;

-- Subgrups per a les propietats comunes que penjaven directament dels grups GENERAL i SCHEDULLED.
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('GENERAL_COMUNES','GENERAL',0,'Configuracions comunes');
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_COMUNES','SCHEDULLED',0,'Configuració comuna de les tasques periòdiques');

-- Mou les propietats (les globals i les còpies per entitat, que comparteixen el grup) als subgrups nous.
UPDATE DIS_CONFIG SET GROUP_CODE = 'GENERAL_COMUNES' WHERE GROUP_CODE = 'GENERAL';
UPDATE DIS_CONFIG SET GROUP_CODE = 'SCHEDULLED_COMUNES' WHERE GROUP_CODE = 'SCHEDULLED';

-- Taules per les excepcions
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
    LASTMODIFIEDDATE    TIMESTAMP
);
ALTER TABLE DIS_EXCEPCIO_LOG ADD (
  CONSTRAINT DIS_EXCEPCIO_LOG_PK PRIMARY KEY (ID));
CREATE INDEX DIS_EXCEPCIO_LOG_DATA_I ON DIS_EXCEPCIO_LOG(DATA);
GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_EXCEPCIO_LOG TO WWW_DISTRIBUCIO;
-- Tasca periòdica per esborrar excepcios
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_EXCEPCIONS','SCHEDULLED',23,'Tasca periòdica d''esborrat de les excepcions antigues');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES
    ('es.caib.distribucio.tasca.excepcions.esborrar.antics.cron','0 0 3 * * *','Expressió cron de la tasca d''esborrat de les excepcions antigues. Per defecte cada dia a les 3:00 (0 0 3 * * *)','SCHEDULLED_EXCEPCIONS',0,0,'CRON',0);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES
    ('es.caib.distribucio.tasca.excepcions.esborrar.antics.dies','45','Dies màxim d''antiguitat de les excepcions guardades. Per defecte 45 dies','SCHEDULLED_EXCEPCIONS',1,0,'INT',0);

-- Interfície per defecte al entrar a l'aplicació (JSP o REACT)
ALTER TABLE DIS_USUARI ADD INTERFICIE_USUARI VARCHAR2(5 CHAR);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES
    ('es.caib.distribucio.interface.defecte','REACT','Interfície (JSP o REACT) amb la que s''entra a l''aplicació quan l''usuari no n''ha triat cap al seu perfil','GENERAL',24,0,'TEXT',0);

-- Preferencies de tema visual i estil de menu de la interficie REACT (perfil de l'usuari)
ALTER TABLE DIS_USUARI ADD TEMA_APLICACIO VARCHAR2(16 CHAR);
ALTER TABLE DIS_USUARI ADD ESTIL_MENU VARCHAR2(16 CHAR) DEFAULT 'TEMA' NOT NULL;

-- Camps de tema visual dark (color de fons, color de lletra i logo) per a DIS_ENTITAT
ALTER TABLE DIS_ENTITAT ADD COLOR_FONS_DARK VARCHAR2(32 CHAR);
ALTER TABLE DIS_ENTITAT ADD COLOR_LLETRA_DARK VARCHAR2(32 CHAR);
ALTER TABLE DIS_ENTITAT ADD LOGO_CAP_DARK BLOB;    
  
-- #910 Timeout per les accions massives
INSERT INTO DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) 
VALUES (0,'SCHEDULLED_EXECUCIONS_MASSIVES','es.caib.distribucio.segonpla.timeout.execucio.massiva', 10,'Temps màxim en minuts per executar cada acció individual (per defecte 10)',1,'INT',0);

-- #915 Nou plugin d'usuaris per Soffid
-- Configuració del plugin d'usuaris per Soffid
INSERT INTO DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES ('SOFFID', 'USUARIS', 2, 'Configuració del plugin d''usuaris (soffid)');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.soffid.serverurl', NULL, 'Url del servidor de Soffid', 'SOFFID', 0, 1, 'TEXT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.soffid.username', NULL, 'Nom de l''usuari de Soffid', 'SOFFID', 1, 1, 'TEXT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.soffid.password', NULL, 'Contrasenya de l''usuari de Soffid', 'SOFFID', 2, 1, 'CREDENTIALS');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.soffid.entorn', NULL, 'Entorn del client de Soffid', 'SOFFID', 3, 1, 'TEXT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.soffid.debug', NULL, 'Depurar Soffid', 'SOFFID', 4, 1, 'BOOL');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.soffid.minimumcharacterstosearch', NULL, 'Número de caràcters mínims abans cercar. Per defecte 3', 'SOFFID', 5, 1, 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.plugin.dades.usuari.pluginsib.userinformation.soffid.maxallowednumberofresultsinpartialsearches', NULL, 'Número màxim de resultats per cerques. Per defecte 100', 'SOFFID', 6, 1, 'INT');
