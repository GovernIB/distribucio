-- Oracle

ALTER TABLE DIS_REGLA
ADD (
    UNITAT_DESTI_ID NUMBER(19)
);

ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_UNITAT_DESTI_REGLA_FK FOREIGN KEY (UNITAT_DESTI_ID) REFERENCES DIS_UNITAT_ORGANITZATIVA(ID);

-- Postgres

-- #236: Millora de regles. Nou tipus de regla "reenviar a UO"
ALTER TABLE DIS_REGLA ADD UNITAT_DESTI_ID BIGINT;

ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_UNITAT_DESTI_REGLA_FK FOREIGN KEY (UNITAT_DESTI_ID) REFERENCES DIS_UNITAT_ORGANITZATIVA(ID);