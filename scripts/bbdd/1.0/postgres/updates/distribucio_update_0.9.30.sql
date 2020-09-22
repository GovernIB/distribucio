-- #155: Actualitzar interficie d'integració amb registre per tractar metadades de digitalització 

ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    META_DADES CHARACTER VARYING(4000)
);
