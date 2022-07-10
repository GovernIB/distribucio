-- #472 Crear API REST per canvi i consulta d'anotacions amb informació de annexos invàlids, 
-- modificar la llibreria d'utilitats
-- Crea una nova columna per guardar i mostrar l'estat del document.


-- Oracle
ALTER TABLE DIS_REGISTRE_ANNEX ADD ARXIU_ESTAT VARCHAR2(20 CHAR);

-- Postgresql
ALTER TABLE DIS_REGISTRE_ANNEX ADD ARXIU_ESTAT character varying(20);
