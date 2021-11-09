--#376 Recordar perfil de l'usuari
ALTER TABLE DIS_USUARI ADD ROL_ACTUAL character varying(64);

-- # 167 Afegir un mòdul d’estadístiques
-- Crear taules per anotacions, estat i busties

CREATE TABLE DIS_HIS_ANOTACIO
(
	ID						 BIGSERIAL	    NOT NULL,
    ENTITAT_ID               integer     NOT NULL,
    UNITAT_ID                integer,
    TIPUS                    character varying(16) NOT NULL,
    DATA                     timestamp without time zone   NOT NULL,
    ANOTACIONS               integer	    NOT NULL,
    ANOTACIONS_TOTAL         integer	    NOT NULL,
    REENVIAMENTS             integer	    NOT NULL,
    EMAILS                   integer	    NOT NULL,
    JUSTIFICANTS             integer	    NOT NULL,
    ANNEXOS                  integer	    NOT NULL,
    BUSTIES                  integer	    NOT NULL,
    USUARIS                  integer	    NOT NULL
);

CREATE TABLE DIS_HIS_ESTAT
(
	ID						 BIGSERIAL	    NOT NULL,
    ENTITAT_ID               integer     NOT NULL,
    UNITAT_ID                integer,
    TIPUS                    character varying(16) NOT NULL,
    DATA                     timestamp without time zone   NOT NULL,
    ESTAT                    character varying(64) NOT NULL,
    CORRECTE                 integer	    NOT NULL,
    CORRECTE_TOTAL           integer	    NOT NULL,
    ERROR                    integer	    NOT NULL,
    ERROR_TOTAL              integer	    NOT NULL,
    TOTAL                    integer	    NOT NULL
);

CREATE TABLE DIS_HIS_BUSTIA
(
	ID						 BIGSERIAL	    NOT NULL,
    ENTITAT_ID               integer     NOT NULL,
    UNITAT_ID                integer,
    TIPUS                    character varying(16) NOT NULL,
    DATA                     timestamp without time zone   NOT NULL,
    BUSTIA_ID                integer     NOT NULL,
    NOM                      character varying(1024) NOT NULL,
    USUARIS                  integer	    NOT NULL,
    USUARIS_PERMIS           integer	    NOT NULL,
    USUARIS_ROL              integer	    NOT NULL
);

-- PK
ALTER TABLE DIS_HIS_ANOTACIO ADD CONSTRAINT DIS_HIS_ANOTACIO_PK PRIMARY KEY (ID);
ALTER TABLE DIS_HIS_ESTAT ADD CONSTRAINT DIS_HIS_ESTAT_PK PRIMARY KEY (ID);
ALTER TABLE DIS_HIS_BUSTIA ADD CONSTRAINT DIS_HIS_BUSTIA_PK PRIMARY KEY (ID);

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


-- #301 Poder veure que usuaris tenen permisos sobre una bústia al reenviar 
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.reenviar.mostrar.permisos',null,'Mostrar usuaris amb permís sobre bústies al reenviar','GENERAL','11','0','BOOL',null,null);


-- #380 Poder seleccionar una bústia per defecte per usuari 
CREATE TABLE DIS_BUSTIA_DEFAULT
(
    ID                      INTEGER                 NOT NULL,
    ENTITAT                 INTEGER                 NOT NULL,
    BUSTIA                  INTEGER                 NOT NULL,
    USUARI             CHARACTER VARYING(64)   NOT NULL,
    CREATEDDATE             TIMESTAMP WITHOUT TIMEZONE,
    LASTMODIFIEDDATE        TIMESTAMP WITHOUT TIMEZONE
);

-- PK
ALTER TABLE ONLY DIS_BUSTIA_DEFAULT ADD (CONSTRAINT DIS_BUSTIA_DEFAULT_PK PRIMARY KEY (ID));

-- FK
ALTER TABLE ONLY DIS_BUSTIA_DEFAULT 
    ADD CONSTRAINT DIS_BUSTIA_DEF_BST_FK FOREIGN KEY (BUSTIA) REFERENCES DIS_BUSTIA(ID);
ALTER TABLE ONLY DIS_BUSTIA_DEFAULT 
    ADD CONSTRAINT DIS_BUSTIA_DEF_ENT_FK FOREIGN KEY (ENTITAT) REFERENCES DIS_ENTITAT(ID);
ALTER TABLE ONLY DIS_BUSTIA_DEFAULT 
    ADD CONSTRAINT DIS_BUSTIA_DEF_USR_FK FOREIGN KEY (USUARI) REFERENCES DIS_USUARI(CODI);