--#376 Recordar perfil de l'usuari
ALTER TABLE DIS_USUARI ADD ROL_ACTUAL VARCHAR2(64 CHAR);

-- # 167 Afegir un mòdul d’estadístiques
-- Crear taules per anotacions, estat i busties

CREATE TABLE DIS_HIS_ANOTACIO
(
	ID						 NUMBER(19)	    NOT NULL,
    ENTITAT_ID               NUMBER(19)     NOT NULL,
    UNITAT_ID                NUMBER(19),
    TIPUS                    VARCHAR2(16 CHAR) NOT NULL,
    DATA                     TIMESTAMP(6)   NOT NULL,
    ANOTACIONS               NUMBER(19)	    NOT NULL,
    ANOTACIONS_TOTAL         NUMBER(19)	    NOT NULL,
    REENVIAMENTS             NUMBER(19)	    NOT NULL,
    EMAILS                   NUMBER(19)	    NOT NULL,
    JUSTIFICANTS             NUMBER(19)	    NOT NULL,
    ANNEXOS                  NUMBER(19)	    NOT NULL,
    BUSTIES                  NUMBER(19)	    NOT NULL,
    USUARIS                  NUMBER(19)	    NOT NULL
);

CREATE TABLE DIS_HIS_ESTAT
(
	ID						 NUMBER(19)	    NOT NULL,
    ENTITAT_ID               NUMBER(19)     NOT NULL,
    UNITAT_ID                NUMBER(19),
    TIPUS                    VARCHAR2(16 CHAR) NOT NULL,
    DATA                     TIMESTAMP(6)   NOT NULL,
    ESTAT                    VARCHAR2(64 CHAR) NOT NULL,
    CORRECTE                 NUMBER(19)	    NOT NULL,
    CORRECTE_TOTAL           NUMBER(19)	    NOT NULL,
    ERROR                    NUMBER(19)	    NOT NULL,
    ERROR_TOTAL              NUMBER(19)	    NOT NULL,
    TOTAL                    NUMBER(19)	    NOT NULL
);

CREATE TABLE DIS_HIS_BUSTIA
(
	ID						 NUMBER(19)	    NOT NULL,
    ENTITAT_ID               NUMBER(19)     NOT NULL,
    UNITAT_ID                NUMBER(19),
    TIPUS                    VARCHAR2(16 CHAR) NOT NULL,
    DATA                     TIMESTAMP(6)   NOT NULL,
    BUSTIA_ID                NUMBER(19)     NOT NULL,
    NOM                      VARCHAR2(1024 CHAR) NOT NULL,
    USUARIS                  NUMBER(19)	    NOT NULL,
    USUARIS_PERMIS           NUMBER(19)	    NOT NULL,
    USUARIS_ROL              NUMBER(19)	    NOT NULL
);

-- PK
ALTER TABLE DIS_HIS_ANOTACIO ADD (CONSTRAINT DIS_HIS_ANOTACIO_PK PRIMARY KEY (ID));
ALTER TABLE DIS_HIS_ESTAT ADD (CONSTRAINT DIS_HIS_ESTAT_PK PRIMARY KEY (ID));
ALTER TABLE DIS_HIS_BUSTIA ADD (CONSTRAINT DIS_HIS_BUSTIA_PK PRIMARY KEY (ID));

-- FK
ALTER TABLE DIS_HIS_ANOTACIO 
    ADD CONSTRAINT DIS_ENTITAT_HIS_ANOT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES DIS_ENTITAT(ID);
ALTER TABLE DIS_HIS_ANOTACIO 
    ADD CONSTRAINT DIS_UNITAT_HIS_ANOT_FK FOREIGN KEY (UNITAT_ID) REFERENCES DIS_UNITAT_ORGANITZATIVA(ID);

ALTER TABLE DIS_HIS_ESTAT
    ADD CONSTRAINT DIS_ENTITAT_HIS_ESTAT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES DIS_ENTITAT(ID);
ALTER TABLE DIS_HIS_ESTAT
    ADD CONSTRAINT DIS_UNITAT_HIS_ESTAT_FK FOREIGN KEY (UNITAT_ID) REFERENCES DIS_UNITAT_ORGANITZATIVA(ID);
    
ALTER TABLE DIS_HIS_BUSTIA
    ADD CONSTRAINT DIS_ENTITAT_HIS_BUSTIA_FK FOREIGN KEY (ENTITAT_ID) REFERENCES DIS_ENTITAT(ID);
ALTER TABLE DIS_HIS_BUSTIA
    ADD CONSTRAINT DIS_UNITAT_HIS_BUSTIA_FK FOREIGN KEY (UNITAT_ID) REFERENCES DIS_UNITAT_ORGANITZATIVA(ID);

-- I
CREATE INDEX DIS_I_HIST_ANOT_DATA ON DIS_HIS_ANOTACIO(DATA);
CREATE INDEX DIS_I_HIST_ESTAT_DATA ON DIS_HIS_ESTAT(DATA);
CREATE INDEX DIS_I_HIST_BUSTIA_DATA ON DIS_HIS_BUSTIA(DATA);

-- GRANT
GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_HIS_ANOTACIO TO WWW_DISTRIBUCIO;
GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_HIS_ESTAT TO WWW_DISTRIBUCIO;
GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_HIS_BUSTIA TO WWW_DISTRIBUCIO;


-- #301 Poder veure que usuaris tenen permisos sobre una bústia al reenviar 
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.reenviar.mostrar.permisos',null,'Mostrar usuaris amb permís sobre bústies al reenviar','GENERAL','11','0','BOOL',null,null);

-- #380 Poder seleccionar una bústia per defecte per usuari 
CREATE TABLE DIS_BUSTIA_DEFAULT
(
    ID                      NUMBER(19)             NOT NULL,
    ENTITAT                 NUMBER(19)             NOT NULL,
    BUSTIA                  NUMBER(19)             NOT NULL,
    USUARI  	            VARCHAR2(64 CHAR)      NOT NULL,
    CREATEDDATE             TIMESTAMP(6),
    LASTMODIFIEDDATE        TIMESTAMP(6)
);

-- PK
ALTER TABLE DIS_BUSTIA_DEFAULT ADD (CONSTRAINT DIS_BUSTIA_DEFAULT_PK PRIMARY KEY (ID));

-- FK
ALTER TABLE DIS_BUSTIA_DEFAULT 
    ADD CONSTRAINT DIS_BUSTIA_DEF_BST_FK FOREIGN KEY (BUSTIA) REFERENCES DIS_BUSTIA(ID);
ALTER TABLE DIS_BUSTIA_DEFAULT 
    ADD CONSTRAINT DIS_BUSTIA_DEF_ENT_FK FOREIGN KEY (ENTITAT) REFERENCES DIS_ENTITAT(ID);
ALTER TABLE DIS_BUSTIA_DEFAULT 
    ADD CONSTRAINT DIS_BUSTIA_DEF_USR_FK FOREIGN KEY (USUARI) REFERENCES DIS_USUARI(CODI);
    
GRANT SELECT, UPDATE, INSERT, DELETE ON DIS_BUSTIA_DEFAULT TO WWW_DISTRIBUCIO;


-- #391 Afegir comprovació de la signatura dels documents signats per portafib

-- Propietats pel la firma en sevidor

-- Canvia la descripció del perfil.
UPDATE DIS_CONFIG
SET DESCRIPTION = 'Propietat per especificar el perfil de firma de l''usuari al Portasignatures. Es pot deixar buit si només en té un d''assignat.'
WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil';

-- Insereix la propietat pel certificat.
-- es.caib.distribucio.plugin.signatura.portafib.username=afirmades-firma
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
VALUES ('es.caib.distribucio.plugin.signatura.portafib.username',null,'Nom del certificat a utilitzar en la firma. És opcional','SIGNATURA','6','0','TEXT',null,null);
