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