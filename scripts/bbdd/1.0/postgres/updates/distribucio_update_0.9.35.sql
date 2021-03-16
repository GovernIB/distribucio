-- #295 Poder reenviar a bústies només per a coneixement

ALTER TABLE DIS_CONT_MOV ADD PER_CONEIXEMENT BOOLEAN;

-- #294 Quan es marqui un element per Coneixement/Tramitació s'afegirà text al final dels comentaris

ALTER TABLE DIS_CONT_MOV ADD COMENTARI_DESTINS character varying(256)