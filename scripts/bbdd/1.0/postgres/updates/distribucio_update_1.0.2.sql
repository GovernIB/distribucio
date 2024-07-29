-- Distribucio 1.0.2

-- Creació de l'índex per la consulta d'anotacions pendents per bústia
CREATE INDEX DIS_I_REGISTRE_ID_PENDENT ON DIS_REGISTRE (ID, PENDENT);
