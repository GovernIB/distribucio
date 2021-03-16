-- #295 Poder reenviar a bústies només per a coneixement
-- Oracle:

ALTER TABLE DIS_CONT_MOV ADD PER_CONEIXEMENT NUMBER(1);

-- Postgresql:

ALTER TABLE DIS_CONT_MOV ADD PER_CONEIXEMENT BOOLEAN;