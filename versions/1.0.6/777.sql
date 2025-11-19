-- Afegir nou permis d'usuari de només lectura #777

-- Actualitza tots els permisos existents
UPDATE DIS_ACL_ENTRY dae
SET dae.MASK = 2 -- complet
WHERE dae.MASK = 1 -- lectura
  AND dae.ACL_OBJECT_IDENTITY IN (
    SELECT daoi.ID
    FROM DIS_ACL_OBJECT_IDENTITY daoi
    	INNER JOIN DIS_ACL_CLASS dac ON daoi.OBJECT_ID_CLASS = dac.ID 
    WHERE dac.CLASS LIKE 'es.caib.distribucio.persist.entity.BustiaEntity'
  );