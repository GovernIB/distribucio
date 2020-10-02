-- #155: Actualitzar interficie d'integració amb registre per tractar metadades de digitalització 

ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    META_DADES CHARACTER VARYING(4000)
);




-- #227: Revisar scripts de BBDD per a que la seqüència utilitzada sigui DIS_HIBERNATE_SEQ 

CREATE SEQUENCE DIS_HIBERNATE_SEQ
    START WITH (SELECT CURRVAL('HIBERNATE_SEQUENCE')
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999999999
    CACHE 20;

