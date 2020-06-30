--------------------- 140: Revisar enumerats a la BBDD -------------------------------
-- dis_contingut.tipus
alter table dis_contingut add column temp_tipus character varying(8);
update dis_contingut set temp_tipus = tipus;
alter table dis_contingut alter column tipus drop not null;
update dis_contingut set tipus = null;  
alter table dis_contingut alter column tipus character varying(8);
update dis_contingut set tipus = temp_tipus;
alter table dis_contingut alter column tipus set not null;
alter table dis_contingut drop column temp_tipus;

update dis_contingut set tipus = 'BUSTIA' where tipus = '0';  
update dis_contingut set tipus = 'REGISTRE' where tipus = '1';  

-- dis_cont_log.tipus
alter table dis_cont_log add column temp_tipus character varying(30);
update dis_cont_log set temp_tipus = tipus;
alter table dis_cont_log alter column tipus drop not null;
update dis_cont_log set tipus = null;  
alter table dis_cont_log alter column tipus character varying(30);
update dis_cont_log set tipus = temp_tipus;
alter table dis_cont_log alter column tipus set not null;
alter table dis_cont_log drop column temp_tipus;

update dis_cont_log set tipus = 'CREACIO' where tipus = '0';
update dis_cont_log set tipus = 'CONSULTA' where tipus = '1';
update dis_cont_log set tipus = 'MODIFICACIO' where tipus = '2';
update dis_cont_log set tipus = 'ELIMINACIO' where tipus = '3';
update dis_cont_log set tipus = 'RECUPERACIO' where tipus = '4';
update dis_cont_log set tipus = 'ELIMINACIODEF' where tipus = '5';
update dis_cont_log set tipus = 'ACTIVACIO' where tipus = '6';
update dis_cont_log set tipus = 'DESACTIVACIO' where tipus = '7';
update dis_cont_log set tipus = 'AGAFAR' where tipus = '8';
update dis_cont_log set tipus = 'ALLIBERAR' where tipus = '9';
update dis_cont_log set tipus = 'COPIA' where tipus = '10';
update dis_cont_log set tipus = 'MOVIMENT' where tipus = '11';
update dis_cont_log set tipus = 'ENVIAMENT' where tipus = '12';
update dis_cont_log set tipus = 'REENVIAMENT' where tipus = '13';
update dis_cont_log set tipus = 'PROCESSAMENT' where tipus = '14';
update dis_cont_log set tipus = 'TANCAMENT' where tipus = '15';
update dis_cont_log set tipus = 'REOBERTURA' where tipus = '16';
update dis_cont_log set tipus = 'ACUMULACIO' where tipus = '17';
update dis_cont_log set tipus = 'DISGREGACIO' where tipus = '18';
update dis_cont_log set tipus = 'PER_DEFECTE' where tipus = '19';
update dis_cont_log set tipus = 'PFIRMA_ENVIAMENT' where tipus = '20';
update dis_cont_log set tipus = 'PFIRMA_CANCELACIO' where tipus = '21';
update dis_cont_log set tipus = 'PFIRMA_CALLBACK' where tipus = '22';
update dis_cont_log set tipus = 'PFIRMA_FIRMA' where tipus = '23';
update dis_cont_log set tipus = 'PFIRMA_REBUIG' where tipus = '24';
update dis_cont_log set tipus = 'PFIRMA_REINTENT' where tipus = '25';
update dis_cont_log set tipus = 'ARXIU_CSV' where tipus = '26';
update dis_cont_log set tipus = 'ARXIU_CUSTODIAT' where tipus = '27';
update dis_cont_log set tipus = 'CUSTODIA_CANCELACIO' where tipus = '28';
update dis_cont_log set tipus = 'FIRMA_CLIENT' where tipus = '29';
update dis_cont_log set tipus = 'NOTIFICACIO_ENTREGADA' where tipus = '30';
update dis_cont_log set tipus = 'NOTIFICACIO_REBUTJADA' where tipus = '31';
update dis_cont_log set tipus = 'NOTIFICACIO_REINTENT' where tipus = '32';
update dis_cont_log set tipus = 'ENVIAMENT_EMAIL' where tipus = '33';
update dis_cont_log set tipus = 'MARCAMENT_PROCESSAT' where tipus = '34';
update dis_cont_log set tipus = 'DISTRIBUCIO' where tipus = '35';
update dis_cont_log set tipus = 'REGLA_APLICAR' where tipus = '36';
update dis_cont_log set tipus = 'BACK_REBUDA' where tipus = '37';
update dis_cont_log set tipus = 'BACK_PROCESSADA' where tipus = '38';
update dis_cont_log set tipus = 'BACK_REBUTJADA' where tipus = '39';
update dis_cont_log set tipus = 'BACK_ERROR' where tipus = '40';

-- dis_cont_log.objecte_log_tipus
alter table dis_cont_log add column temp_tipus character varying(30);
update dis_cont_log set temp_tipus = objecte_log_tipus;
update dis_cont_log set objecte_log_tipus = null;  
alter table dis_cont_log alter column objecte_log_tipus character varying(30);
update dis_cont_log set objecte_log_tipus = temp_tipus;
alter table dis_cont_log drop column temp_tipus;

update dis_cont_log set objecte_log_tipus = 'CREACIO' where objecte_log_tipus = '0';
update dis_cont_log set objecte_log_tipus = 'CONSULTA' where objecte_log_tipus = '1';
update dis_cont_log set objecte_log_tipus = 'MODIFICACIO' where objecte_log_tipus = '2';
update dis_cont_log set objecte_log_tipus = 'ELIMINACIO' where objecte_log_tipus = '3';
update dis_cont_log set objecte_log_tipus = 'RECUPERACIO' where objecte_log_tipus = '4';
update dis_cont_log set objecte_log_tipus = 'ELIMINACIODEF' where objecte_log_tipus = '5';
update dis_cont_log set objecte_log_tipus = 'ACTIVACIO' where objecte_log_tipus = '6';
update dis_cont_log set objecte_log_tipus = 'DESACTIVACIO' where objecte_log_tipus = '7';
update dis_cont_log set objecte_log_tipus = 'AGAFAR' where objecte_log_tipus = '8';
update dis_cont_log set objecte_log_tipus = 'ALLIBERAR' where objecte_log_tipus = '9';
update dis_cont_log set objecte_log_tipus = 'COPIA' where objecte_log_tipus = '10';
update dis_cont_log set objecte_log_tipus = 'MOVIMENT' where objecte_log_tipus = '11';
update dis_cont_log set objecte_log_tipus = 'ENVIAMENT' where objecte_log_tipus = '12';
update dis_cont_log set objecte_log_tipus = 'REENVIAMENT' where objecte_log_tipus = '13';
update dis_cont_log set objecte_log_tipus = 'PROCESSAMENT' where objecte_log_tipus = '14';
update dis_cont_log set objecte_log_tipus = 'TANCAMENT' where objecte_log_tipus = '15';
update dis_cont_log set objecte_log_tipus = 'REOBERTURA' where objecte_log_tipus = '16';
update dis_cont_log set objecte_log_tipus = 'ACUMULACIO' where objecte_log_tipus = '17';
update dis_cont_log set objecte_log_tipus = 'DISGREGACIO' where objecte_log_tipus = '18';
update dis_cont_log set objecte_log_tipus = 'PER_DEFECTE' where objecte_log_tipus = '19';
update dis_cont_log set objecte_log_tipus = 'PFIRMA_ENVIAMENT' where objecte_log_tipus = '20';
update dis_cont_log set objecte_log_tipus = 'PFIRMA_CANCELACIO' where objecte_log_tipus = '21';
update dis_cont_log set objecte_log_tipus = 'PFIRMA_CALLBACK' where objecte_log_tipus = '22';
update dis_cont_log set objecte_log_tipus = 'PFIRMA_FIRMA' where objecte_log_tipus = '23';
update dis_cont_log set objecte_log_tipus = 'PFIRMA_REBUIG' where objecte_log_tipus = '24';
update dis_cont_log set objecte_log_tipus = 'PFIRMA_REINTENT' where objecte_log_tipus = '25';
update dis_cont_log set objecte_log_tipus = 'ARXIU_CSV' where objecte_log_tipus = '26';
update dis_cont_log set objecte_log_tipus = 'ARXIU_CUSTODIAT' where objecte_log_tipus = '27';
update dis_cont_log set objecte_log_tipus = 'CUSTODIA_CANCELACIO' where objecte_log_tipus = '28';
update dis_cont_log set objecte_log_tipus = 'FIRMA_CLIENT' where objecte_log_tipus = '29';
update dis_cont_log set objecte_log_tipus = 'NOTIFICACIO_ENTREGADA' where objecte_log_tipus = '30';
update dis_cont_log set objecte_log_tipus = 'NOTIFICACIO_REBUTJADA' where objecte_log_tipus = '31';
update dis_cont_log set objecte_log_tipus = 'NOTIFICACIO_REINTENT' where objecte_log_tipus = '32';
update dis_cont_log set objecte_log_tipus = 'ENVIAMENT_EMAIL' where objecte_log_tipus = '33';
update dis_cont_log set objecte_log_tipus = 'MARCAMENT_PROCESSAT' where objecte_log_tipus = '34';
update dis_cont_log set objecte_log_tipus = 'DISTRIBUCIO' where objecte_log_tipus = '35';
update dis_cont_log set objecte_log_tipus = 'REGLA_APLICAR' where objecte_log_tipus = '36';
update dis_cont_log set objecte_log_tipus = 'BACK_REBUDA' where objecte_log_tipus = '37';
update dis_cont_log set objecte_log_tipus = 'BACK_PROCESSADA' where objecte_log_tipus = '38';
update dis_cont_log set objecte_log_tipus = 'BACK_REBUTJADA' where objecte_log_tipus = '39';
update dis_cont_log set objecte_log_tipus = 'BACK_ERROR' where objecte_log_tipus = '40';

-- dis_cont_log.objecte_tipus
alter table dis_cont_log add column temp_tipus character varying(12);
update dis_cont_log set temp_tipus = objecte_tipus;
update dis_cont_log set objecte_tipus = null;  
alter table dis_cont_log alter column objecte_tipus character varying(12);
update dis_cont_log set objecte_tipus = temp_tipus;
alter table dis_cont_log drop column temp_tipus;

update dis_cont_log set objecte_tipus= 'CONTINGUT' where objecte_tipus = '0';
update dis_cont_log set objecte_tipus= 'EXPEDIENT' where objecte_tipus = '1';
update dis_cont_log set objecte_tipus= 'CARPETA' where objecte_tipus = '2';
update dis_cont_log set objecte_tipus= 'DOCUMENT' where objecte_tipus = '3';
update dis_cont_log set objecte_tipus= 'DADA' where objecte_tipus = '4';
update dis_cont_log set objecte_tipus= 'BUSTIA' where objecte_tipus = '5';
update dis_cont_log set objecte_tipus= 'ARXIU' where objecte_tipus = '6';
update dis_cont_log set objecte_tipus= 'INTERESSAT' where objecte_tipus = '7';
update dis_cont_log set objecte_tipus= 'REGISTRE' where objecte_tipus = '8';
update dis_cont_log set objecte_tipus= 'RELACIO' where objecte_tipus = '9';
update dis_cont_log set objecte_tipus= 'NOTIFICACIO' where objecte_tipus = '10';
update dis_cont_log set objecte_tipus= 'PUBLICACIO' where objecte_tipus = '11';
update dis_cont_log set objecte_tipus= 'ALTRES' where objecte_tipus = '12';

-- dis_unitat_organitzativa.tipus_transicio
alter table dis_unitat_organitzativa add column temp_tipus character varying(12);
update dis_unitat_organitzativa set temp_tipus = tipus_transicio;
update dis_unitat_organitzativa set tipus_transicio = null;  
alter table dis_unitat_organitzativa alter column tipus_transicio character varying(12);
update dis_unitat_organitzativa set tipus_transicio = temp_tipus;
alter table dis_unitat_organitzativa drop column temp_tipus;

update dis_unitat_organitzativa set tipus_transicio= 'DIVISIO' where tipus_transicio = '0';
update dis_unitat_organitzativa set tipus_transicio= 'FUSIO' where tipus_transicio = '1';
update dis_unitat_organitzativa set tipus_transicio= 'SUBSTITUCIO' where tipus_transicio = '2';





-- 142: Revisar i corregir llargades dels camps de les anotacions ------------------------------
alter table dis_registre alter column numero type character varying(255);
alter table dis_registre alter column idioma_codi type character varying(19);
alter table dis_registre alter column entitat_codi type character varying(255);
alter table dis_registre alter column entitat_desc type character varying(255);
alter table dis_registre alter column oficina_desc type character varying(300);
alter table dis_registre alter column llibre_desc type character varying(255);
alter table dis_registre alter column assumpte_desc type character varying(255);
alter table dis_registre alter column transport_tipus_codi type character varying(20);
alter table dis_registre alter column usuari_nom type character varying(767);
alter table dis_registre alter column usuari_contacte type character varying(255);
alter table dis_registre alter column aplicacio_codi type character varying(255);
alter table dis_registre alter column aplicacio_versio type character varying(255);
alter table dis_registre alter column docfis_codi type character varying(19);
alter table dis_registre alter column justificant_arxiu_uuid type character varying(256);

alter table dis_registre_inter alter column tipus type character varying(19);
alter table dis_registre_inter alter column nom type character varying(255);
alter table dis_registre_inter alter column llinatge1 type character varying(255);
alter table dis_registre_inter alter column llinatge2 type character varying(255);
alter table dis_registre_inter alter column rao_social type character varying(2000);

alter table dis_registre_annex alter column fitxer_arxiu_uuid type character varying(256);


-- 151: Rendiment de l'enviament per email d'una anotació de registre

ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    SIGN_DETALLS_DESCARREGAT BOOLEAN DEFAULT FALSE
);

CREATE TABLE DIS_REGISTRE_FIRMA_DETALL
(  	ID BIGSERIAL NOT NULL,
    DATA TIMESTAMP WITHOUT TIMEZONE,
    RESPONSABLE_NIF CHARACTER VARYING(30),
    RESPONSABLE_NOM CHARACTER VARYING(256),
    EMISSOR_CERTIFICAT CHARACTER VARYING(2000),
    FIRMA_ID BIGINT,
    CREATEDBY_CODI       	CHARACTER VARYING(64),
    CREATEDDATE          	TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI  	CHARACTER VARYING(64),
    LASTMODIFIEDDATE     	TIMESTAMP WITHOUT TIME ZONE
);
ALTER TABLE ONLY DIS_REGISTRE_FIRMA_DETALL ADD CONSTRAINT DIS_REGISTRE_FIRMA_DETALL_PK PRIMARY KEY (ID);
ALTER TABLE DIS_REGISTRE_FIRMA_DETALL ADD CONSTRAINT DIS_FIRMA_REGISTRE_DETALL_FK FOREIGN KEY (FIRMA_ID) REFERENCES DIS_REGISTRE_ANNEX_FIRMA (ID);


ALTER TABLE DIS_REGISTRE_ANNEX ALTER COLUMN FIRMA_CSV CHARACTER VARYING(256);


--------------------- 175: Guardar informació de firmes d'annexos per no haver-els de consultar cada vegada -------------------------------
ALTER TABLE DIS_REGISTRE
ADD (
    JUSTIFICANT_DESCARREGAT BOOLEAN DEFAULT FALSE
);
ALTER TABLE DIS_REGISTRE
ADD (
    JUSTIFICANT_ID BIGINT
);
ALTER TABLE DIS_REGISTRE ADD CONSTRAINT DIS_JUSTIFICANT_REGISTRE_FK FOREIGN KEY (JUSTIFICANT_ID) REFERENCES DIS_REGISTRE_ANNEX (ID);
ALTER TABLE DIS_REGISTRE_ANNEX ALTER COLUMN SICRES_TIPUS_DOC DROP NOT NULL;
ALTER TABLE DIS_REGISTRE_ANNEX DROP CONSTRAINT DIS_REGANX_MULT_UK;

