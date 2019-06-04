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
    WHERE a.GESDOC_DOC_ID IS NULL
          AND f_a.GESDOC_FIR_ID IS NOT NULL
);


-- #106 Error consultant bústia per defecte

-- Actualitza el codi dela unitat orgánica per a que coincideixi amb la unitat amb què tenen relació
UPDATE DIS_BUSTIA b
SET b.UNITAT_CODI = (
	SELECT uo.codi
	FROM DIS_UNITAT_ORGANITZATIVA uo
	WHERE uo.ID = b.UNITAT_ID
)
WHERE b.id IN (
	-- Selecciona les bústies amb problemes
	SELECT 
		b.id
	FROM dis_bustia b
		INNER JOIN DIS_UNITAT_ORGANITZATIVA uo ON b.UNITAT_ID = uo.ID
	WHERE b.UNITAT_CODI <> uo.CODI
);