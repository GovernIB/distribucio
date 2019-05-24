-- #102 Error enviant anotacions per email

-- Posa a null tots els identificadors del gestor documental als annexos que ja estiguin a l'arxiu
UPDATE DIS_REGISTRE_ANNEX
SET GESDOC_DOC_ID = NULL
WHERE 
	FITXER_ARXIU_UUID IS NOT NULL 
	AND GESDOC_DOC_ID IS NOT NULL;


-- LLeva tots els identificadors de gestió documentals a les firmes llurs annexos ja estiguin a l'arxiu i sense gestor docuemntal id
UPDATE DIS_REGISTRE_ANNEX_FIRMA f
SET f.GESDOC_FIR_ID = NULL
WHERE f.id IN (
	SELECT f_a.id
	FROM DIS_REGISTRE_ANNEX_FIRMA f_a
		INNER JOIN DIS_REGISTRE_ANNEX a ON f_a.ANNEX_ID = a.ID
	WHERE a.GESDOC_DOC_ID = NULL
);