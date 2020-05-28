-- #157 Distribucio es queda sense connexions a BBDD 
-- S'ha de millorar la consulta de registres. Es crea una columna nova per determinar si està pendent

-- Afegeix una columna de processament
ALTER TABLE DISTRIBUCIO.DIS_REGISTRE ADD PENDENT NUMBER DEFAULT 1 NOT NULL;
-- Posa com a processades les que tinguin l'estat de processat
UPDATE DIS_REGISTRE SET PENDENT = 0
WHERE PROCES_ESTAT IN ('BUSTIA_PROCESSADA', 'BACK_PENDENT', 'BACK_REBUDA', 'BACK_PROCESSADA', 'BACK_REBUTJADA', 'BACK_ERROR');
-- Crea un índex per a la nova columna 
CREATE INDEX DIS_REGISTRE_PENDENT_I ON DISTRIBUCIO.DIS_REGISTRE (PENDENT);
