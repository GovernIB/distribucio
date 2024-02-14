--#655 Dues regles distintes amb mateix SIA i amb UO i bústia d'origen definides, s'executen al mateix temps

-- Nova columna per configurar les regles per finalitzar avaluació

-- Oracle

ALTER TABLE DIS_REGLA ADD ATURAR_AVALUACIO NUMBER(1) DEFAULT 0;

-- Postgresql

ALTER TABLE DIS_REGLA ADD COLUMN ATURAR_AVALUACIO BOOLEAN;

