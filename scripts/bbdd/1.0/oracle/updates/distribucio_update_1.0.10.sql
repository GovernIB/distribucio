-- Distribucio 1.0.10

-- #817 Permetre publicar avisos per entitat
-- Afegeix la columna
ALTER TABLE DIS_AVIS ADD ENTITAT NUMBER(19);
-- Afegeix la clau forana
ALTER TABLE DIS_AVIS ADD
  CONSTRAINT DIS_AVIS_ENT_FK FOREIGN KEY (ENTITAT) 
    REFERENCES DIS_ENTITAT (ID);
    
-- #862 Estendre les regles per permetre filtrar per un tràmit específic d'un procediment o servei
-- Afegeix la nova columna per filtrar per codi
ALTER TABLE DIS_REGLA ADD TRAMIT_CODI varchar2(1024 CHAR) NULL;

-- #870 No es permet esborrar una entitat si algun usuari la té per defecte
-- Modifica la constraint per posar a null si s'esborra l'entitat
ALTER TABLE DIS_USUARI DROP CONSTRAINT DIS_ENTITAT_USUARI_FK;
ALTER TABLE DIS_USUARI
    ADD CONSTRAINT DIS_ENTITAT_USUARI_FK
        FOREIGN KEY (ENTITAT_DEFECTE_ID)
            REFERENCES DIS_ENTITAT(ID)
            ON DELETE SET NULL;