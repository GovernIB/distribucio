-- #103 Afegir el camp "backoffice" a les anotacions de registre i a les regles
ALTER TABLE DIS_REGLA ADD BACKOFFICE_CODI VARCHAR2(20);
ALTER TABLE DIS_REGISTRE ADD BACK_CODI VARCHAR2(20);