-- #59 Regles a partir del codi SIA
ALTER TABLE DIS_REGLA ADD PROCEDIMENT_CODI VARCHAR2(64);

ALTER TABLE DIS_REGLA MODIFY (ASSUMPTE_CODI NULL);