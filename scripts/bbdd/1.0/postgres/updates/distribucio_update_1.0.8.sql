-- Distribucio 1.0.8

-- #826 Millorar la tasca periòdica d'enviament d'anotacions al backoffice
-- Nou camp per configurar els períodes d'espera entre reintents
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.entre.intents',null,'Periode de temps entre reintents (ISO-8601). Per defecte n intent x 10 minuts','SCHEDULLED_BACKOFFICE','2',false,'TEXT',null,null);

-- #827 Afegir un camp d'adreça de correu de responsable tècnic a la configuració d'un backoffice
-- Afegeix la nova propietat per configurar el temps entre correus.
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.email.backoffice.responsable.temps','1440','Temps entre correus al responsable del backoffice (minuts)','EMAIL','3','0','INT',null,null);
-- Afegeix les columnes per guardar si enviar email, darrer email i la data del darrer email.
ALTER TABLE DIS_BACKOFFICE ADD ENVIAR_EMAIL_RESPONSABLE boolean DEFAULT false;
ALTER TABLE DIS_BACKOFFICE ADD EMAIL_RESPONSABLE varchar(100);
ALTER TABLE DIS_BACKOFFICE ADD DARRER_EMAIL timestamp without time zone;


-- #846 Crear un índex de BD per la consulta d'accions massives
CREATE INDEX DIS_ESTAT_ELEM_EXMASS_CONT_I ON DIS_EXECUCIO_MASSIVA_CONT(ELEMENT_ID, ESTAT);
