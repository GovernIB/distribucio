-- Distribucio 1.0.7

-- Permetre definir els elements per pàgina per defecte #651
ALTER TABLE DIS_USUARI ADD NUM_ELEMENTS_PAGINA BIGINT;

-- Actualitzar el plugin de dades externes #689
Insert into DIS_CONFIG (JBOSS_PROPERTY,GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE,CONFIGURABLE) values
    (true,'DADES_EXTERNES','es.caib.distribucio.plugin.dadesext.dir3.rest.url', null,'Url del plugin rest','2','TEXT',true);

-- Correu periòdic anotacions de registre que no es poden processar automàticament #771
INSERT INTO DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION)
VALUES ('SCHEDULLED_EMAIL_ANOTACIO_ERROR_PROCESAR', 'SCHEDULLED', 24, 'Enviament periòdic per correu de les anotacions amb error de processament');

INSERT INTO DIS_CONFIG (GROUP_CODE,KEY,VALUE,DESCRIPTION,POSITION,TYPE_CODE) values
    ('SCHEDULLED_EMAIL_ANOTACIO_ERROR_PROCESAR','es.caib.distribucio.enviar.anotacio.error.cron','0 0 0 * * *','Especificar l''expressió ''cron'' indicant l''interval de temps de les execucions de la tasca','0','CRON');

ALTER TABLE DIS_USUARI ADD EMAIL_ERROR_ANOTACIO boolean DEFAULT false;

-- Permetre publicar avisos permanents #786
ALTER TABLE DIS_AVIS MODIFY DATA_FINAL NULL;

-- Mostrar la informació de les firmes no criptogràfiques (firma àgil) #811
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('VALID_SIGN_AGIL',null,'12','Plugin de validació de firmes àgils');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.validarsignatura.agil.activa',null,'Acitvar validació firmes àgils','VALID_SIGN_AGIL','0','0','BOOL',null,null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugin.validarsignatura.agil.class','es.caib.distribucio.plugin.caib.validacio.ValidacioFirmaPluginApiEvidenciesIB','Classe de plugin de validació de firmes àgils (EvidenciesIB)','VALID_SIGN_AGIL','1','0','TEXT',null,null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugins.validarsignatura.agil.api.evidenciesib.endpoint',null,'URL API EXTERNA EvidenciesIB','VALID_SIGN_AGIL','2','1','TEXT',null,null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugins.validarsignatura.agil.api.evidenciesib.username',null,'Usuari integració de API EvidenciesIB','VALID_SIGN_AGIL','3','1','TEXT',null,null);
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.plugins.validarsignatura.agil.api.evidenciesib.password',null,'Contrasenya usuario integració API EvidenciesIB','VALID_SIGN_AGIL','4','1','CREDENTIALS',null,null);

-- Afegir un camp d'adreça de correu de responsable tècnic a la configuració d'un backoffice #827
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) VALUES ('es.caib.distribucio.email.backoffice.responsable.temps','1440','Temps entre correus al responsable del backoffice (minuts)','EMAIL','3',false,'INT',null,null);

ALTER TABLE DIS_BACKOFFICE ADD COLUMN ENVIAR_EMAIL_RESPONSABLE   boolean;
ALTER TABLE DIS_BACKOFFICE ADD COLUMN EMAIL_RESPONSABLE          VARCHAR(100);
ALTER TABLE DIS_BACKOFFICE ADD COLUMN DARRER_EMAIL               TIMESTAMP WITHOUT TIME ZONE;
