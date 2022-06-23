-- #463 Posar annexos amb firma no vàlida com a esborrany a l'arxiu 
-- Noves columens per guardar l'estat de la validació i la descripció de l'error
 
-- Oracle:
ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    VAL_FIRMA_ESTAT VARCHAR2(64 CHAR) 
);
ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    VAL_FIRMA_ERROR VARCHAR2(255 CHAR) 
);

-- Postgresql:
ALTER TABLE DIS_REGISTRE_ANNEX
ADD VAL_FIRMA_ESTAT character varying(64);
ALTER TABLE DIS_REGISTRE_ANNEX
ADD VAL_FIRMA_ERROR character varying(255);


-- Insereix la propietat per desar els annexos amb firmes invàlides com esborrany o no
-- es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany

-- Oracle
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany',null,'Annexos amb firmes invàlides com esborranys. Per defecte és fals','SCHEDULLED_ARXIU','5','0','BOOL',null,null);

-- Postgresql
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany',null,'Annexos amb firmes invàlides com esborranys. Per defecte és fals','SCHEDULLED_ARXIU','5',false,'BOOL',null,null);
