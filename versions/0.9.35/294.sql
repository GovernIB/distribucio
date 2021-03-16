-- #294 Quan es marqui un element per Coneixement/Tramitació s'afegirà text al final dels comentaris
-- Oracle:

ALTER TABLE DIS_CONT_MOV ADD COMENTARI_DESTINS VARCHAR2(256 CHAR);

-- Postgresql:

ALTER TABLE DIS_CONT_MOV ADD COMENTARI_DESTINS character varying(256)