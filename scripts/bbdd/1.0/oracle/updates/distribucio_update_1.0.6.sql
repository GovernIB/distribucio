-- Distribucio 1.0.6

-- Afegir informació del tràmit del procediment en l'anotació #537
ALTER TABLE DIS_REGISTRE ADD TRAMIT_CODI VARCHAR2(64 CHAR);
ALTER TABLE DIS_REGISTRE ADD TRAMIT_NOM VARCHAR2(255 CHAR);

-- Permetre escollir l'entitat per defecte #713
ALTER TABLE DIS_USUARI ADD ENTITAT_DEFECTE_ID NUMBER(19,0);
ALTER TABLE DIS_USUARI
    ADD CONSTRAINT DIS_ENTITAT_USUARI_FK
        FOREIGN KEY (ENTITAT_DEFECTE_ID)
            REFERENCES DIS_ENTITAT(ID);
            
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

GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_LIMIT_CANVI_ESTAT TO WWW_DISTRIBUCIO;

Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GENERAL_LIMIT_CANVI_ESTAT','GENERAL','26','Configuració de limit de canvis d''estat');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.horari.laboral', '* * 7-16 * * MON-FRI', 'Horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '1', '0', 'CRON');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.minut.laboral', '4', 'Limit canvi d''estat per minut en horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '2', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.minut.no.laboral', '8', 'Limit canvi d''estat per minut en horari no laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '3', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.dia.laboral', '8000', 'Limit canvi d''estat per dia en horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '4', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.limit.dia.no.laboral', '10000', 'Limit canvi d''estat per dia en horari no laboral', 'GENERAL_LIMIT_CANVI_ESTAT', '5', '0', 'INT');            

-- Permetre desactivar la consulta periòdica de procediments i serveis per entitat #769
Insert INTO DIS_CONFIG (KEY,VALUE, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position) VALUES('es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis.disable', NULL, 'Deshabilitar la sincronització de serveis', 'SCHEDULLED_SERVEI', 0, 'BOOL', NULL, 1, NULL, NULL, 1);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis.disable';
Insert INTO DIS_CONFIG (KEY,VALUE, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position) VALUES('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments.disable', NULL, 'Deshabilitar la sincronització de procediments', 'SCHEDULLED_PROCEDIMENT', 0, 'BOOL', NULL, 1, NULL, NULL, 8);
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments.disable';

-- Si una anotació roman massa temps en estat COMUNICADA AL BACKOFFICE que torni a estat PENDENT  #772
ALTER TABLE DIS_REGISTRE ADD BACK_COMUNICADA_DATA TIMESTAMP(6);
-- Posa la data de comunicada als registres existents a partir dels logs
UPDATE DIS_REGISTRE r 
SET r.BACK_COMUNICADA_DATA = 
NVL((SELECT max(CREATEDDATE)
	FROM DIS_CONT_LOG log
	WHERE log.TIPUS  IN ('BACK_COMUNICADA', 'BACK_REBUDA')
		AND log.CONTINGUT_ID = r.id),
	SYSDATE
)
WHERE r.PROCES_ESTAT = 'BACK_COMUNICADA'
AND r.BACK_COMUNICADA_DATA IS NULL;
-- Configuració de la tasca periòdica i dels temps màxims
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_COMUNICAT_A_PENDENT','SCHEDULLED','20','Tasca periòdica de canvi d''estat anotacions comunicades a pendents');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.tasca.canviarAPendent.temps.espera.execucio', null, 'Iterval de temps entre les execucions de la tasca (ms). Per defecte 60000', 'SCHEDULLED_COMUNICAT_A_PENDENT', '1', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.tasca.canviarAPendent.maxim.temps.estat.comunicada', null, 'Màxim de dies que pot estar comunicada abans de canviar l''estat a pendent d''usuari. Per defecte 30.', 'SCHEDULLED_COMUNICAT_A_PENDENT', '2', '0', 'INT');

-- Afegir nou permis d'usuari de només lectura #777
-- Actualitza tots els permisos existents
UPDATE DIS_ACL_ENTRY dae
SET dae.MASK = 3 -- complet read+write
WHERE dae.MASK = 1 -- lectura
  AND dae.ACL_OBJECT_IDENTITY IN (
    SELECT daoi.ID
    FROM DIS_ACL_OBJECT_IDENTITY daoi
    	INNER JOIN DIS_ACL_CLASS dac ON daoi.OBJECT_ID_CLASS = dac.ID 
    WHERE dac.CLASS LIKE 'es.caib.distribucio.persist.entity.BustiaEntity'
  );

--
UPDATE DIS_ACL_ENTRY dae
SET dae.MASK = 2
WHERE dae.MASK = 3
  AND dae.ACL_OBJECT_IDENTITY IN (
    SELECT daoi.ID
    FROM DIS_ACL_OBJECT_IDENTITY daoi
    	INNER JOIN DIS_ACL_CLASS dac ON daoi.OBJECT_ID_CLASS = dac.ID
    WHERE dac.CLASS LIKE 'es.caib.distribucio.persist.entity.BustiaEntity'
  );

INSERT INTO DIS_ACL_ENTRY (
    ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE
)
SELECT
    DIS_ACL_ENTRY_SEQ.nextval,
    acl_oid,
    base_max + rn,
    sid,
    1,
    granting,
    audit_success,
    audit_failure
FROM (
     SELECT
         e.ACL_OBJECT_IDENTITY        AS acl_oid,
         e.SID                        AS sid,
         e.GRANTING,
         e.AUDIT_SUCCESS,
         e.AUDIT_FAILURE,
         ROW_NUMBER() OVER (PARTITION BY e.ACL_OBJECT_IDENTITY ORDER BY e.SID, e.ID) AS rn,
         (SELECT NVL(MAX(d2.ACE_ORDER),0) FROM DIS_ACL_ENTRY d2
          WHERE d2.ACL_OBJECT_IDENTITY = e.ACL_OBJECT_IDENTITY) AS base_max
     FROM DIS_ACL_ENTRY e
     WHERE e.MASK = 2
       AND NOT EXISTS (
         SELECT 1 FROM DIS_ACL_ENTRY e2
         WHERE e2.ACL_OBJECT_IDENTITY = e.ACL_OBJECT_IDENTITY
           AND e2.SID = e.SID
           AND e2.MASK = 1)
       AND EXISTS (
         SELECT 1
         FROM DIS_ACL_OBJECT_IDENTITY daoi
                  JOIN DIS_ACL_CLASS dac ON daoi.OBJECT_ID_CLASS = dac.ID
         WHERE daoi.ID = e.ACL_OBJECT_IDENTITY
           AND dac.CLASS LIKE 'es.caib.distribucio.persist.entity.BustiaEntity')
 ) t;