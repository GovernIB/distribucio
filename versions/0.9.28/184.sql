--------------------- 184: Error filtrant anotacions pendents. Apareixen anotacions processades a la bústia
UPDATE DIS_REGISTRE
SET PENDENT = 0
WHERE PROCES_ESTAT = 'BUSTIA_PROCESSADA'
    AND PENDENT <> 0;

