-- #287 Posar com a pendents les anotacions rebutjades pels backoffices per poder processar-les
UPDATE DIS_REGISTRE SET PENDENT = true WHERE PENDENT = false AND PROCES_ESTAT = 'BACK_REBUTJADA';
UPDATE DIS_REGISTRE SET PENDENT = true WHERE PENDENT = false AND PROCES_ESTAT = 'BACK_ERROR';

--#350 Eliminar el camp "Tipus" en la gestió dels backoffices
ALTER TABLE DIS_BACKOFFICE DROP COLUMN TIPUS;

-- #354 Implementar una nova secció de configuració general de l'aplicació
CREATE TABLE DIS_CONFIG
(
    KEY                  CHARACTER VARYING(256)     NOT NULL,
    VALUE                CHARACTER VARYING(2048),
    DESCRIPTION          CHARACTER VARYING(2048),
    GROUP_CODE           CHARACTER VARYING(128)     NOT NULL,
    POSITION             INTEGER                		 DEFAULT 0 NOT NULL,
    JBOSS_PROPERTY       BOOLEAN             			 DEFAULT false NOT NULL,
    TYPE_CODE            CHARACTER VARYING(128)     DEFAULT 'TEXT',
    LASTMODIFIEDBY_CODI  CHARACTER VARYING(64),
    LASTMODIFIEDDATE     TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE DIS_CONFIG_GROUP
(
    CODE                 CHARACTER VARYING(128)     NOT NULL,
    PARENT_CODE          CHARACTER VARYING(128)     DEFAULT NULL,
    POSITION             INTEGER              			 DEFAULT 0 NOT NULL,
    DESCRIPTION          CHARACTER VARYING(512)     NOT NULL
);

CREATE TABLE DIS_CONFIG_TYPE
(
    CODE                 CHARACTER VARYING(128)     NOT NULL,
    VALUE                CHARACTER VARYING(2048)    DEFAULT NULL
);

ALTER TABLE DIS_CONFIG ADD CONSTRAINT DIS_CONFIG_PK PRIMARY KEY (KEY);

ALTER TABLE DIS_CONFIG_GROUP ADD CONSTRAINT DIS_CONFIG_GROUP_PK PRIMARY KEY (CODE);

ALTER TABLE DIS_CONFIG_TYPE ADD CONSTRAINT DIS_CONFIG_TYPE_PK PRIMARY KEY (CODE);

ALTER TABLE DIS_CONFIG ADD CONSTRAINT DIS_CONFIG_GROUP_FK FOREIGN KEY (GROUP_CODE) REFERENCES DIS_CONFIG_GROUP(CODE);
    
    
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DISTRIBUCIO','PLUGINS','10','Configuració del plugin de distribució');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GENERAL',null,'0','Configuracions generals');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('EMAIL',null,'1','Enviament de correus');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED',null,'2','Configuració de les tasques periodiques');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('PLUGINS',null,'3','Plugins de l''aplicació');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GES_DOC','PLUGINS','4','Gestió documental (Sistema de fitxers)');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_REGLES','SCHEDULLED','16','Tasca periòdica d''aplicació de regles del tipus backoffice');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('UNITATS','PLUGINS','1','Configuració del plugin d''unitats organitzatives');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('PROCEDIMENTS','PLUGINS','11','Plugin de consulta de procediments');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DADES_EXTERNES','PLUGINS','12','Plugin de dades externes');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('VALID_SIGN','PLUGINS','14','Plugin de validació de firmes');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SIGNATURA','PLUGINS','13','Plugin de signatura');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_BACKOFFICE','SCHEDULLED','15','Tasca periòdica d''enviament d''annotacions al backoffice');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_TANCAR','SCHEDULLED','17','Tasca periòdica de tancar contenidors pendents');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_EMAILS_NO_AGRUPATS','SCHEDULLED','18','Tasca periòdica de enviar emails no agrupats');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('ARXIU','PLUGINS','0','Configuració de l''arxiu');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('USUARIS','PLUGINS','7','Configuració del plugin d''usuaris');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_EMAILS_AGRUPATS','SCHEDULLED','19','Tasca periòdica de enviar emails agrupats');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_ARXIU','SCHEDULLED','1','Tasca periòdica de guardar annexos a l''arxiu');

Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('BOOL',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('TEXT',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('INT',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('FLOAT',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('CRON',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('CREDENTIALS',null);

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacio.processament.asincron',null,'Indica si les noves anotacions s''haurian de processar de manera asíncrona','SCHEDULLED','0',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio',null,'Iterval de temps entre les execucions de la tasca (ms)','SCHEDULLED_ARXIU','1',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.max.reintents',null,'Nombre de reintents de guardar annexos a l''arxiu','SCHEDULLED_ARXIU','2',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.max.threads.parallel',null,'Nombre maxim de threads paral·lels','SCHEDULLED_ARXIU','3',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.distribucio.fitxers.class',null,'Plugin de distribució','DISTRIBUCIO','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.document.versionable',null,'Document versionable','ARXIU','6',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.unitats.organitzatives.class',null,'Classe de plugin d''unitats organitzatives','UNITATS','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url',null,'Servei URL','UNITATS','1',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.default.user.language',null,'Llenguatge per defecte de l''aplicació','GENERAL','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username',null,'Usuari','UNITATS','2',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password',null,'Contrasenya','UNITATS','3',true,'CREDENTIALS',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.unitats.organitzatives.dir3.connect.timeout',null,'Connect timeout','UNITATS','4',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.unitats.organitzatives.dir3.request.timeout',null,'Request timeout','UNITATS','5',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.procediment.class',null,'Classe de plugin de procediments','PROCEDIMENTS','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.app.base.url',null,'Especificar la URL base de l''aplicació','GENERAL','1',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.procediment.rolsac.service.url',null,'Url per a accedir procediments','PROCEDIMENTS','1',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.procediment.rolsac.service.username',null,'Nom de l''usuari per a accedir procediments','PROCEDIMENTS','2',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.procediment.rolsac.service.password',null,'Password per a accedir procediments','PROCEDIMENTS','3',true,'CREDENTIALS',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dadesext.class',null,'Classe de plugin de dades externes','DADES_EXTERNES','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dadesext.service.url',null,'Servei URL','DADES_EXTERNES','1',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.email.jndi',null,'JNDI email','EMAIL','0',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.email.remitent',null,'Remitent dels correus electronics','EMAIL','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.signatura.class',null,'Classe de plugin de signatura','SIGNATURA','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.signatura.signarAnnexos',null,'Indica si s''han de signar annexos','SIGNATURA','1',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint',null,'Endpoint','SIGNATURA','2',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.api.firma.en.servidor.simple.username',null,'Usuari','SIGNATURA','3',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.api.firma.en.servidor.simple.password',null,'Contrasenya','SIGNATURA','4',true,'CREDENTIALS',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil',null,'Propietat opcional per especificar el perfil de firma per la firma simple en el servidor. Per defecte: "CADES_DETACHED"','SIGNATURA','5',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.validatesignature.class',null,'Classe de plugin de validació de firmes','VALID_SIGN','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.debug',null,'Indica si s''utiliza debug mode','VALID_SIGN','1',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.applicationID',null,'Id d''aplicació','VALID_SIGN','2',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.printxml',null,'Indica si es mostra xml en debug mode','VALID_SIGN','3',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.TransformersTemplatesPath',null,'Path per transformers','VALID_SIGN','4',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint',null,'Endpoint','VALID_SIGN','5',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username',null,'Usuari','VALID_SIGN','6',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password',null,'Contrasenya','VALID_SIGN','7',true,'CREDENTIALS',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio',null,'Iterval de temps entre les execucions de la tasca (ms)','SCHEDULLED_BACKOFFICE','0',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.enviar.anotacions.max.reintents',null,'Nombre de reintents de enviar annotacions al backoffice','SCHEDULLED_BACKOFFICE','1',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio',null,'Iterval de temps entre les execucions de la tasca (ms)','SCHEDULLED_REGLES','0',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.max.reintents',null,'Nombre de reintents de aplicar regles','SCHEDULLED_REGLES','1',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio',null,'Iterval de temps entre les execucions de la tasca (ms)','SCHEDULLED_TANCAR','0',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat',null,'Iterval de temps entre les execucions de la tasca (ms)','SCHEDULLED_EMAILS_NO_AGRUPATS','0',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.segonpla.email.bustia.cron.enviament.agrupat',null,'Especificar l''expressió ''cron'' indicant l''interval de temps de les execucions de la tasca','SCHEDULLED_EMAILS_AGRUPATS','0',false,'CRON',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.url',null,'Url per generar versió imprimible','ARXIU','11',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.usuari',null,'Usuari per generar versió imprimible','ARXIU','12',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.contrasenya',null,'Contrasenya per generar versió imprimible','ARXIU','13',true,'CREDENTIALS',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.datasource.jndi',null,'JNDI','GENERAL','2',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.hibernate.dialect',null,'Dialect','GENERAL','3',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.hibernate.show_sql',null,'Indica si mostrar SQL en logs','GENERAL','4',true,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.hibernate.hbm2ddl.auto',null,'hbm2ddl','GENERAL','5',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacions.registre.expedient.classificacio',null,'Classificació','ARXIU','14',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacions.registre.expedient.serie.documental',null,'Serie documental','ARXIU','15',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.backoffice.integracio.clau',null,'Clau per xifratge','SCHEDULLED_BACKOFFICE','2',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.backoffice.integracio.retornarAnnexIFirmaContingut',null,'Enviar contingut al backoffice','SCHEDULLED_BACKOFFICE','3',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacions.permetre.reservar',null,'Poder reservar/agafar un assentament abans de marcar-lo com a processat','GENERAL','6',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu',null,'Duplicar contingut en arxiu','GENERAL','8',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.reenviar.favorits',null,'Favorits permes','GENERAL','9',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.enviar.coneixement',null,'Poder reenviar a bústies només per a coneixement','GENERAL','10',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.enviar.arbre.nivell',null,'Propietat per indicar fins a quin nivell obrir l''arbre d''unitats al reenviar una anotació (defecte = 1)','GENERAL','11',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.concsv.base.url',null,'Url base al servei de CONCSV','GENERAL','12',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.class',null,'Classe Arxiu','ARXIU','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.entitat.logos.base.dir',null,'Ruta de una carpeta amb Logos','GENERAL','7',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.suporta.metadades',null,'Suporta metadades','ARXIU','7',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.aplicacio.codi',null,'Codi aplicació','ARXIU','2',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.csv.definicio',null,'CSV definició','ARXIU','5',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.timeout.connect',null,'Timeout connect','ARXIU','9',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.timeout.read',null,'Timeout read','ARXIU','10',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.csv.base.url',null,'Url base CSV','ARXIU','8',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.base.url',null,'Url base ARXIU','ARXIU','1',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.usuari',null,'Usuari arxiu','ARXIU','3',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.arxiu.caib.contrasenya',null,'Password arxiu','ARXIU','4',true,'CREDENTIALS',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.gesdoc.class',null,'Nom de la classe per a gestionar l''emmagatzament de documents','GES_DOC','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.gesdoc.filesystem.base.dir',null,'Directori del sistema de fitxers on emmagatzemar els documents','GES_DOC','1',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dades.usuari.class',null,'Classe per a gestionar l''accés al plugin d''usuaris','USUARIS','0',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dades.usuari.jdbc.datasource.jndi.name',null,'Datasource dels usuaris','USUARIS','1',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dades.usuari.jdbc.query.codi',null,'Consulta d''usuaris per codi','USUARIS','2',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dades.usuari.jdbc.query.nif',null,'Consulta d''usuaris per nif','USUARIS','3',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dades.usuari.jdbc.query.rols',null,'Consulta d''usuaris per rols','USUARIS','4',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.dades.usuari.jdbc.query.grup',null,'Consulta d''usuaris per grup','USUARIS','5',true,'INT',null,null);

    
    
    
    


-- #362 Error esborrant bústia quan hi ha moviments de registres que hi fan referència
-- Elimina las FK de la taula de moviments
ALTER TABLE DIS_CONT_MOV DROP CONSTRAINT DIS_ORIGEN_CONTMOV_FK;
ALTER TABLE DIS_CONT_MOV DROP CONSTRAINT DIS_DESTI_CONTMOV_FK;

ALTER TABLE DIS_CONT_MOV ADD ORIGEN_NOM character varying(1024);
ALTER TABLE DIS_CONT_MOV ADD DESTI_NOM character varying(1024);

UPDATE DIS_CONT_MOV SET ORIGEN_NOM = (SELECT c.NOM FROM DIS_CONTINGUT c WHERE c.ID = ORIGEN_ID)
WHERE ORIGEN_ID IS NOT NULL;

UPDATE DIS_CONT_MOV SET DESTI_NOM = (SELECT c.NOM FROM DIS_CONTINGUT c WHERE c.ID = DESTI_ID)
WHERE DESTI_ID IS NOT NULL;

--#374 Posar el logo de l'entitat a FileSystem
ALTER TABLE DIS_ENTITAT DROP COLUMN LOGO_CAP;

-- #378 Problema per definir regla amb múltiples SIAs 
ALTER TABLE DIS_REGLA ALTER COLUMN PROCEDIMENT_CODI TYPE CHARACTER VARYING(1024);