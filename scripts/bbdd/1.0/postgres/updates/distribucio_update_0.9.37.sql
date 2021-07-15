--#350 Eliminar el camp "Tipus" en la gestió dels backoffices
ALTER TABLE DIS_BACKOFFICE DROP COLUMN TIPUS;

-- #287 Posar com a pendents les anotacions rebutjades pels backoffices per poder processar-les
UPDATE DIS_REGISTRE SET PENDENT = 1 WHERE PENDENT = 0 AND PROCES_ESTAT = 'BACK_REBUTJADA';