-- #472 Crear API REST per canvi i consulta d'anotacions amb informació de annexos invàlids, 
-- modificar la llibreria d'utilitats
-- Crea una nova columna per guardar i mostrar l'estat del document.
-- Crea una columna per guardar el recompte d'annexos en estat d'esborrany


-- Oracle
ALTER TABLE DIS_REGISTRE_ANNEX ADD ARXIU_ESTAT VARCHAR2(20 CHAR);
ALTER TABLE DIS_REGISTRE ADD ANNEXOS_ESTAT_ESBORRANY NUMBER(8,0) DEFAULT 0;


-- Postgresql
ALTER TABLE DIS_REGISTRE_ANNEX ADD ARXIU_ESTAT character varying(20);
ALTER TABLE DIS_REGISTRE ADD ANNEXOS_ESTAT_ESBORRANY integer DEFAULT 0;

