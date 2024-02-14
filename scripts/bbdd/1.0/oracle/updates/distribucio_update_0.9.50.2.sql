-- Distribucio 0.9.50.2

--#655 Nova columna per configurar les regles per finalitzar avaluació
ALTER TABLE DIS_REGLA ADD ATURAR_AVALUACIO NUMBER(1) DEFAULT 0;