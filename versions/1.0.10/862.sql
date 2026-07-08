-- #862 Estendre les regles per permetre filtrar per un tràmit específic d'un procediment o servei
-- Afegeix la nova columna per filtrar per codi
ALTER TABLE DIS_REGLA ADD TRAMIT_CODI varchar2(1024 CHAR) NULL;