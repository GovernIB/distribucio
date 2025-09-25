ALTER TABLE DIS_USUARI ADD COLUMN entitat_defecte_id BIGINT;

ALTER TABLE DIS_USUARI
    ADD CONSTRAINT ripea_entitat_usuari_fk
        FOREIGN KEY (entitat_defecte_id)
            REFERENCES DIS_ENTITAT(ID);