-- Distribucio 0.9.50.2

--#655 Nova columna per configurar les regles per finalitzar avaluació
ALTER TABLE DIS_REGLA ADD COLUMN ATURAR_AVALUACIO BOOLEAN;
