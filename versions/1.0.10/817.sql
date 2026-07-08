-- #817 Permetre publicar avisos per entitat
-- Afegeix la columna
ALTER TABLE DIS_AVIS ADD ENTITAT NUMBER(19);
-- Afegeix la clau forana
ALTER TABLE DIS_AVIS ADD
  CONSTRAINT DIS_AVIS_ENT_FK FOREIGN KEY (ENTITAT) 
    REFERENCES DIS_ENTITAT (ID);