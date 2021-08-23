--#350 Eliminar el camp "Tipus" en la gestió dels backoffices
ALTER TABLE DIS_BACKOFFICE DROP COLUMN TIPUS;

-- #287 Posar com a pendents les anotacions rebutjades pels backoffices per poder processar-les
UPDATE DIS_REGISTRE SET PENDENT = 1 WHERE PENDENT = 0 AND PROCES_ESTAT = 'BACK_REBUTJADA';
UPDATE DIS_REGISTRE SET PENDENT = 1 WHERE PENDENT = 0 AND PROCES_ESTAT = 'BACK_ERROR';

-- #362 Error esborrant bústia quan hi ha moviments de registres que hi fan referència
-- Elimina las FK de la taula de moviments
ALTER TABLE DIS_CONT_MOV DROP CONSTRAINT DIS_ORIGEN_CONTMOV_FK;
ALTER TABLE DIS_CONT_MOV DROP CONSTRAINT DIS_DESTI_CONTMOV_FK;

-- #378 Problema per definir regla amb múltiples SIAs 
ALTER TABLE DIS_REGLA ALTER COLUMN PROCEDIMENT_CODI TYPE CHARACTER VARYING(1024);