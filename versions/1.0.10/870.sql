-- #870 No es permet esborrar una entitat si algun usuari la té per defecte
-- Modifica la constraint per posar a null si s'esborra l'entitat
ALTER TABLE DIS_USUARI DROP CONSTRAINT DIS_ENTITAT_USUARI_FK;
ALTER TABLE DIS_USUARI
    ADD CONSTRAINT DIS_ENTITAT_USUARI_FK
        FOREIGN KEY (ENTITAT_DEFECTE_ID)
            REFERENCES DIS_ENTITAT(ID)
            ON DELETE SET NULL;