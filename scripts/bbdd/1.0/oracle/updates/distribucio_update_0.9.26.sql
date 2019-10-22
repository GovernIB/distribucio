-- #143 Error DIS_CONTINGUT_MULT_UK creant bústies
-- Elimina la restricció de la taula de continguts

ALTER TABLE DIS_CONTINGUT DROP CONSTRAINT DIS_CONTINGUT_MULT_UK;
