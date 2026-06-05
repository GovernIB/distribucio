-- #855 Afegir columna i filtre de número de registre al monitor

-- Afegeix la columna al monitor
ALTER TABLE dis_mon_int ADD numero_registre VARCHAR(1024);

-- SELECT id,
--        descripcio,
--        CASE
--            WHEN REGEXP_LIKE(descripcio, '\s*anotació\s+de\s+registre', 'i') THEN null
--            WHEN REGEXP_LIKE(descripcio, '\s*anotació.*?[A-Za-z0-9/_-]*[0-9][A-Za-z0-9/_-]+', 'i')
--                THEN REGEXP_SUBSTR(descripcio, '\s*anotació.*?([A-Za-z0-9/_-]*[0-9][A-Za-z0-9/_-]+)', 1, 1, 'i', 1)
--            END AS numero_extraido
-- FROM dis_mon_int
-- WHERE descripcio LIKE '%anotació%';

UPDATE dis_mon_int
SET numero_registre = REGEXP_SUBSTR(
        REPLACE(REPLACE(REPLACE(descripcio, CHR(160), ' '), CHR(9), ' '), CHR(10), ' '),
        '\s*anotació.*?([A-Za-z0-9/_-]*[0-9][A-Za-z0-9/_-]+)', 1, 1, 'i', 1)
WHERE REGEXP_LIKE(descripcio, '\s*anotació.*?[A-Za-z0-9/_-]*[0-9][A-Za-z0-9/_-]+', 'i')
  AND NOT REGEXP_LIKE(descripcio, '\s*anotació\s+de\s+registre', 'i');