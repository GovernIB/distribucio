ALTER TABLE DIS_REGISTRE MODIFY PROCES_ESTAT VARCHAR2(64);

UPDATE DIS_REGISTRE SET PROCES_ESTAT = 'BUSTIA_PENDENT' WHERE PROCES_ESTAT = 'NO_PROCES';
UPDATE DIS_REGISTRE SET PROCES_ESTAT = 'BUSTIA_PENDENT' WHERE PROCES_ESTAT = 'PENDENT';
UPDATE DIS_REGISTRE SET PROCES_ESTAT = 'DISTRIBUIT_PROCESSAT' WHERE PROCES_ESTAT = 'PROCESSAT';
UPDATE DIS_REGISTRE SET PROCES_ESTAT = 'ARXIU_PENDENT' WHERE PROCES_ESTAT = 'ERROR';
