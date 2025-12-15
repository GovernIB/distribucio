-- Afegir nou permis d'usuari de només lectura #777

-- Actualitza tots els permisos existents
UPDATE DIS_ACL_ENTRY dae
SET dae.MASK = 2 -- esriptura
WHERE dae.MASK = 1 -- lectura
  AND dae.ACL_OBJECT_IDENTITY IN (
    SELECT daoi.ID
    FROM DIS_ACL_OBJECT_IDENTITY daoi
    	INNER JOIN DIS_ACL_CLASS dac ON daoi.OBJECT_ID_CLASS = dac.ID 
    WHERE dac.CLASS LIKE 'es.caib.distribucio.persist.entity.BustiaEntity'
  );

--
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