-- #287 Posar com a pendents les anotacions rebutjades pels backoffices per poder processar-les

-- Oracle
UPDATE DIS_REGISTRE SET PENDENT = 1 WHERE PENDENT = 0 AND PROCES_ESTAT = 'BACK_REBUTJADA';

-- Postgres
UPDATE DIS_REGISTRE SET PENDENT = 1 WHERE PENDENT = 0 AND PROCES_ESTAT = 'BACK_REBUTJADA';
