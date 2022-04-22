-- #237: Millora de regles. Modificar definició de la regla

ALTER TABLE DIS_REGLA
ADD COLUMN BUSTIA_FILTRE_ID BIGINT,
ADD COLUMN BACKOFFICE_DESTI_ID BIGINT;

ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_BUSTIA_FILTRE_REGLA_FK FOREIGN KEY (BUSTIA_FILTRE_ID) REFERENCES DIS_BUSTIA(ID);
ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_BACKOFFICE_DESTI_REGLA_FK FOREIGN KEY (BACKOFFICE_DESTI_ID) REFERENCES DIS_BACKOFFICE(ID);

-- #235: Millora de regles. Manteniment de backoffices
-- ! posar un codi d'usuari que existeixi 

CREATE TABLE DIS_BACKOFFICE
(
    ID BIGSERIAL NOT NULL,
    TIPUS CHARACTER VARYING(256) NOT NULL,
    CODI CHARACTER VARYING(20) NOT NULL,
    NOM CHARACTER VARYING(64) NOT NULL,
    URL CHARACTER VARYING(256) NOT NULL,
    USUARI CHARACTER VARYING(64),
    CONTRASENYA CHARACTER VARYING(64),
    INTENTS INTEGER,
    TEMPS_ENTRE_INTENTS INTEGER,
    ENTITAT_ID BIGINT NOT NULL,
    CREATEDBY_CODI CHARACTER VARYING(64),
    CREATEDDATE TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI CHARACTER VARYING(64),
    LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE
);

ALTER TABLE DIS_BACKOFFICE ADD CONSTRAINT DIS_BACKOFFICE_PK PRIMARY KEY (ID);
ALTER TABLE DIS_BACKOFFICE ADD CONSTRAINT DIS_ENTITAT_BACKOFFICE_FK FOREIGN KEY (ENTITAT_ID) REFERENCES DIS_ENTITAT(ID);


-- 1 Afegeix els nous backoffices a la taula de backoffices
INSERT INTO DIS_BACKOFFICE
(
    ID,
    TIPUS,
    CODI,
    NOM,
    URL,
    USUARI,
    CONTRASENYA,
    INTENTS,
    TEMPS_ENTRE_INTENTS,
    ENTITAT_ID,
    CREATEDBY_CODI,
    CREATEDDATE
)
(
	SELECT
		nextval('DIS_HIBERNATE_SEQ'),
	    TIPUS,
	    CODI,
	    CODI,
	    URL,
	    USUARI,
	    CONTRASENYA,
	    INTENTS,
	    TEMPS_ENTRE_INTENTS,
	    ENTITAT_ID,
	    'u81599', -- CREATEDBY_CODI: ! posar un codi d'usuari que existeixi,
	    current_date
	FROM (
		SELECT DISTINCT
		    COALESCE (r.TIPUS_BACKOFFICE, 'DISTRIBUCIO') AS TIPUS ,
		    COALESCE (r.BACKOFFICE_CODI, 'Backoffice_'|| row_number() OVER (ORDER BY r.ID)) AS CODI,
		    COALESCE (URL, 'http://camp.obligatori') AS URL,
		    USUARI,
		    CONTRASENYA,
		    INTENTS,
		    TEMPS_ENTRE_INTENTS,
		    ENTITAT_ID
		FROM DIS_REGLA r
		WHERE r.TIPUS = 'BACKOFFICE'
	) as backoffice
);

-- 2 Actualitza la referència de les regles tipus bakcoffice amb el registre que li toqui
UPDATE DIS_REGLA r SET BACKOFFICE_DESTI_ID = 
( 
	SELECT ID 
	FROM DIS_BACKOFFICE b
	WHERE r.TIPUS_BACKOFFICE = b.TIPUS 
	 	AND (r.BACKOFFICE_CODI = b.CODI)
	 	AND r.URL = b.URL 
	 	AND r.ENTITAT_ID = b.ENTITAT_ID
	 	AND ((r.USUARI = b.USUARI ) OR (r.USUARI IS NULL AND b.USUARI IS NULL))
	 	AND ((r.CONTRASENYA = b.CONTRASENYA  ) OR (r.CONTRASENYA IS NULL AND b.CONTRASENYA IS NULL))
	 	AND ((r.INTENTS = b.INTENTS  ) OR (r.INTENTS IS NULL AND b.INTENTS IS NULL))
	 	AND ((r.TEMPS_ENTRE_INTENTS = b.TEMPS_ENTRE_INTENTS  ) OR (r.TEMPS_ENTRE_INTENTS IS NULL AND b.TEMPS_ENTRE_INTENTS IS NULL))
	LIMIT 1
)
WHERE R.BACKOFFICE_CODI IS NOT NULL;

-- #250: Ampliació de la columna DIS_REGISTRE.UNITAT_ADM_DESC per igualar-la a DIS_UNITAT_ORGANITZATIVA.DENOMINACIO
ALTER TABLE DIS_REGISTRE ALTER COLUMN UNITAT_ADM_DESC TYPE CHARACTER VARYING(300);

-- #236: Millora de regles. Nou tipus de regla "reenviar a UO"
ALTER TABLE DIS_REGLA ADD UNITAT_DESTI_ID BIGINT;

ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_UNITAT_DESTI_REGLA_FK FOREIGN KEY (UNITAT_DESTI_ID) REFERENCES DIS_UNITAT_ORGANITZATIVA(ID);