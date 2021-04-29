-- #295 Poder reenviar a bústies només per a coneixement

ALTER TABLE DIS_CONT_MOV ADD PER_CONEIXEMENT NUMBER(1);

-- #294 Quan es marqui un element per Coneixement/Tramitació s'afegirà text al final dels comentaris

ALTER TABLE DIS_CONT_MOV ADD COMENTARI_DESTINS VARCHAR2(256 CHAR);

-- #297 Poder marcar bústies com a favorits

CREATE TABLE DIS_BUSTIA_FAVORIT
(
    ID                  NUMBER(19,0)        NOT NULL, 
    BUSTIA_ID           NUMBER(19,0)        NOT NULL, 
    USUARI_CODI         VARCHAR2(64 CHAR)   NOT NULL, 
	CREATEDBY_CODI      VARCHAR2(64 CHAR), 
	CREATEDDATE         TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE    TIMESTAMP (6)
);

ALTER TABLE DIS_BUSTIA_FAVORIT ADD (
CONSTRAINT DIS_BUSTIA_FAV_BUSTIA_FK FOREIGN KEY (BUSTIA_ID)
    REFERENCES DIS_BUSTIA (ID),
CONSTRAINT DIS_BUSTIA_FAV_USUARI_FK FOREIGN KEY (USUARI_CODI)
    REFERENCES DIS_USUARI (CODI),
CONSTRAINT DIS_USUCRE_BUSTFAV_FK FOREIGN KEY (CREATEDBY_CODI)
    REFERENCES DIS_USUARI (CODI),
CONSTRAINT DIS_USUMOD_BUSTFAV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI)
    REFERENCES DIS_USUARI (CODI));
	
ALTER TABLE DIS_BUSTIA_FAVORIT 
	ADD CONSTRAINT DIS_BUSTIA_FAV_MULT_UK 
	UNIQUE (
			BUSTIA_ID, 
			USUARI_CODI);
	
CREATE INDEX DIS_BUSTIA_FAV_BUSTIA_FK_I ON DIS_BUSTIA_FAVORIT(BUSTIA_ID);
CREATE INDEX DIS_BUSTIA_FAV_USUARI_FK_I ON DIS_BUSTIA_FAVORIT(USUARI_CODI);

GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_BUSTIA_FAVORIT TO WWW_DISTRIBUCIO;

-- #299 Poder reservar/agafar un assentament abans de marcar-lo com a processat
ALTER TABLE DIS_REGISTRE ADD AGAFAT_PER VARCHAR2(64 CHAR);

ALTER TABLE DIS_REGISTRE ADD CONSTRAINT DIS_AGAFATPER_REGISTRE_FK FOREIGN KEY (AGAFAT_PER) REFERENCES DIS_USUARI (CODI);
CREATE INDEX DIS_AGAFATPER_REGISTRE_FK_I ON DIS_REGISTRE(AGAFAT_PER);

-- #316: Ampliar el camp comentari de 256 caràcters a 4096 caràcters
ALTER TABLE DIS_CONT_MOV MODIFY COMENTARI VARCHAR2(3940 CHAR);
ALTER TABLE DIS_CONT_COMMENT MODIFY TEXT VARCHAR2(4000 CHAR);

-- #305 Ampliar detall d'error de custòdia del document
ALTER TABLE DIS_REGISTRE MODIFY PROCES_ERROR VARCHAR2(3000 CHAR);
