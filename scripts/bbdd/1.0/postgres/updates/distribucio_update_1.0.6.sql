-- Distribucio 1.0.6  (PostgreSQL)

-- Afegir informació del tràmit del procediment en l'anotació #537
ALTER TABLE dis_registre ADD COLUMN tramit_codi VARCHAR(64);
ALTER TABLE dis_registre ADD COLUMN tramit_nom VARCHAR(255);

-- #713 Permetre escollir entitat per defecte
ALTER TABLE dis_usuari ADD COLUMN entitat_defecte_id BIGINT;

ALTER TABLE dis_usuari
    ADD CONSTRAINT dis_entitat_usuari_fk
        FOREIGN KEY (entitat_defecte_id)
            REFERENCES dis_entitat(id);

-- #761 Afegir límits de respostes de canvi d'estat
CREATE TABLE dis_limit_canvi_estat (
    id BIGINT NOT NULL,
    usuari_codi VARCHAR(64) NOT NULL,
    descripcio VARCHAR(255) NOT NULL,
    lim_min_lab INTEGER,
    lim_min_nolab INTEGER,
    lim_dia_lab INTEGER,
    lim_dia_nolab INTEGER,
    CONSTRAINT dis_limit_canvi_estat_pk PRIMARY KEY (id)
);


INSERT INTO dis_config_group (code, parent_code, position, description)
VALUES ('GENERAL_LIMIT_CANVI_ESTAT', 'GENERAL', 26,
        'Configuració de limit de canvis d''estat');
INSERT INTO dis_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.distribucio.horari.laboral', '* * 7-16 * * MON-FRI',
        'Horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', 1, false, 'CRON');
INSERT INTO dis_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.distribucio.limit.minut.laboral', '4',
        'Limit canvi d''estat per minut en horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', 2, false, 'INT');
INSERT INTO dis_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.distribucio.limit.minut.no.laboral', '8',
        'Limit canvi d''estat per minut en horari no laboral', 'GENERAL_LIMIT_CANVI_ESTAT', 3, false, 'INT');
INSERT INTO dis_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.distribucio.limit.dia.laboral', '8000',
        'Limit canvi d''estat per dia en horari laboral', 'GENERAL_LIMIT_CANVI_ESTAT', 4, false, 'INT');
INSERT INTO dis_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.distribucio.limit.dia.no.laboral', '10000',
        'Limit canvi d''estat per dia en horari no laboral', 'GENERAL_LIMIT_CANVI_ESTAT', 5, false, 'INT');

-- Permetre desactivar la consulta periòdica de procediments i serveis per entitat #769
INSERT INTO dis_config (key, value, description, group_code, jboss_property, type_code,
                        entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position)
VALUES ('es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis.disable',
        NULL, 'Deshabilitar la sincronització de serveis',
        'SCHEDULLED_SERVEI', 0, 'BOOL', NULL, 1, NULL, NULL, 1);

UPDATE dis_config
SET configurable = 1
WHERE key LIKE 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis.disable';

INSERT INTO dis_config (key, value, description, group_code, jboss_property, type_code,
                        entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, position)
VALUES ('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments.disable',
        NULL, 'Deshabilitar la sincronització de procediments',
        'SCHEDULLED_PROCEDIMENT', 0, 'BOOL', NULL, 1, NULL, NULL, 8);

UPDATE dis_config
SET configurable = 1
WHERE key LIKE 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments.disable';

-- Si una anotació roman massa temps en estat COMUNICADA AL BACKOFFICE que torni a estat PENDENT  #772
ALTER TABLE dis_registre ADD COLUMN back_comunicada_data TIMESTAMP(6);
-- Posa la data de comunicada als registres existents a partir dels logs
UPDATE dis_registre r
SET back_comunicada_data =
    COALESCE(
        (SELECT MAX(createddate)
         FROM dis_cont_log log
         WHERE log.tipus IN ('BACK_COMUNICADA', 'BACK_REBUDA')
           AND log.contingut_id = r.id),
        NOW()
    )
WHERE r.proces_estat = 'BACK_COMUNICADA'
  AND r.back_comunicada_data IS NULL;
-- Configuració de la tasca periòdica i dels temps màxims
INSERT INTO dis_config_group (code, parent_code, position, description)
VALUES ('SCHEDULLED_COMUNICAT_A_PENDENT', 'SCHEDULLED', 20,
        'Tasca periòdica de canvi d''estat anotacions comunicades a pendents');
INSERT INTO dis_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.distribucio.tasca.canviarAPendent.temps.espera.execucio',
        NULL, 'Interval de temps entre les execucions de la tasca (ms). Per defecte 60000',
        'SCHEDULLED_COMUNICAT_A_PENDENT', 1, false, 'INT');
INSERT INTO dis_config (key, value, description, group_code, position, jboss_property, type_code)
VALUES ('es.caib.distribucio.tasca.canviarAPendent.maxim.temps.estat.comunicada',
        NULL,'Màxim de dies que pot estar comunicada abans de canviar l''estat a pendent d''usuari. Per defecte 30.',
        'SCHEDULLED_COMUNICAT_A_PENDENT', 2, false, 'INT');

-- Afegir nou permis d'usuari de només lectura #777
UPDATE dis_acl_entry dae
SET mask = 3
WHERE mask = 1
  AND acl_object_id_identity IN (
        SELECT daoi.id
        FROM dis_acl_object_identity daoi
        JOIN dis_acl_class dac ON daoi.object_id_class = dac.id
        WHERE dac.class LIKE 'es.caib.distribucio.persist.entity.BustiaEntity'
  );

-- Afegeix el permís que falta de lectura si no existeix
INSERT INTO dis_acl_entry
(id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT
    nextval('dis_acl_entry_seq'),
    acl_oid,
    base_max + rn,
    sid,
    1,
    granting,
    audit_success,
    audit_failure
FROM (
     SELECT
         e.acl_object_identity AS acl_oid,
         e.sid,
         e.granting,
         e.audit_success,
         e.audit_failure,
         ROW_NUMBER() OVER (PARTITION BY e.acl_object_identity ORDER BY e.sid, e.id) AS rn,
         (SELECT COALESCE(MAX(d2.ace_order), 0)
          FROM dis_acl_entry d2
          WHERE d2.acl_object_identity = e.acl_object_identity) AS base_max
     FROM dis_acl_entry e
     WHERE e.mask = 2
       AND NOT EXISTS (
         SELECT 1
         FROM dis_acl_entry e2
         WHERE e2.acl_object_identity = e.acl_object_identity
           AND e2.sid = e.sid
           AND e2.mask = 1
       )
       AND EXISTS (
         SELECT 1
         FROM dis_acl_object_identity daoi
         JOIN dis_acl_class dac ON daoi.object_id_class = dac.id
         WHERE daoi.id = e.acl_object_identity
           AND dac.class LIKE 'es.caib.distribucio.persist.entity.BustiaEntity'
       )
) t;
