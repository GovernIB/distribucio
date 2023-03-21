Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GENERAL',null,'0','Configuracions generals');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_ARXIU','SCHEDULLED','1','Tasca periòdica de guardar annexos a l''arxiu');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('EMAIL',null,'1','Enviament de correus');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED',null,'2','Configuració de les tasques periodiques');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('ARXIU',null,'3','Configuració de l''arxiu');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('UNITATS',null,'4','Configuració del plugin d''unitats organitzatives');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('GES_DOC',null,'5','Gestió documental (Sistema de fitxers)');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('USUARIS',null,'6','Configuració del plugin d''usuaris');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DISTRIBUCIO',null,'7','Configuració del plugin de distribució');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('PROCEDIMENTS',null,'8','Plugin de consulta de procediments');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('DADES_EXTERNES',null,'9','Plugin de dades externes');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SIGNATURA',null,'10','Plugin de signatura');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('VALID_SIGN',null,'11','Plugin de validació de firmes');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_BACKOFFICE','SCHEDULLED','15','Tasca periòdica d''enviament d''annotacions al backoffice');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_REGLES','SCHEDULLED','16','Tasca periòdica d''aplicació de regles del tipus backoffice');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_TANCAR','SCHEDULLED','17','Tasca periòdica de tancar contenidors pendents');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_EMAILS_NO_AGRUPATS','SCHEDULLED','18','Tasca periòdica de enviar emails no agrupats');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_EMAILS_AGRUPATS','SCHEDULLED','19','Tasca periòdica de enviar emails agrupats');
Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SCHEDULLED_MON_INT','SCHEDULLED','20','Tasca periòdica d''esborrat de dades antigues del Monitor d''Integracions');
Insert into DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) values ('SCHEDULLED_BACKOFFICE_ERRORS', 'SCHEDULLED', '21', 'Tasca periòdica de reintentar anotacions processades amb errors al backoffice');
Insert into DIS_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) values ('SCHEDULLED_PROCEDIMENT', 'SCHEDULLED', '22', 'Tasca periòdica per actualitzar la taula de procediments');


Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('BOOL',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('TEXT',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('INT',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('FLOAT',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('CRON',null);
Insert into DIS_CONFIG_TYPE (CODE,VALUE) values ('CREDENTIALS',null);

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.anotacio.processament.asincron',null,'Indica si les noves anotacions s''haurian de processar de manera asíncrona','SCHEDULLED','0',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio',null,'Interval de temps entre les execucions de la tasca (ms)','SCHEDULLED_ARXIU','1',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.max.reintents',null,'Nombre de reintents de guardar annexos a l''arxiu','SCHEDULLED_ARXIU','2',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.max.threads.parallel',null,'Nombre maxim de threads paral·lels','SCHEDULLED_ARXIU','3',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.innectivitat.cron',null,'Expressió ''cron'' per definir opcionalment un període d''innactivitat','SCHEDULLED_ARXIU','4',false,'CRON',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany',null,'Annexos amb firmes invàlides com esborranys. Per defecte és fals','SCHEDULLED_ARXIU','5',false,'BOOL',null,null);
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
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil',null,'Propietat per especificar el perfil de firma de l''usuari al Portasignatures. Es pot deixar buit si només en té un d''assignat.','SIGNATURA','5',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.signatura.portafib.username',null,'Nom del certificat a utilitzar en la firma. És opcional','SIGNATURA','6',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugin.validatesignature.class',null,'Classe de plugin de validació de firmes','VALID_SIGN','0',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.debug',null,'Indica si s''utiliza debug mode','VALID_SIGN','1',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.applicationID',null,'Id d''aplicació','VALID_SIGN','2',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.printxml',null,'Indica si es mostra xml en debug mode','VALID_SIGN','3',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.TransformersTemplatesPath',null,'Path per transformers','VALID_SIGN','4',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint',null,'Endpoint','VALID_SIGN','5',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username',null,'Usuari','VALID_SIGN','6',true,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password',null,'Contrasenya','VALID_SIGN','7',true,'CREDENTIALS',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio',null,'Interval de temps entre les execucions de la tasca (ms)','SCHEDULLED_BACKOFFICE','0',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.enviar.anotacions.max.reintents',null,'Nombre de reintents de enviar annotacions al backoffice','SCHEDULLED_BACKOFFICE','1',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio',null,'Interval de temps entre les execucions de la tasca (ms)','SCHEDULLED_REGLES','0',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.max.reintents',null,'Nombre de reintents de aplicar regles','SCHEDULLED_REGLES','1',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio',null,'Interval de temps entre les execucions de la tasca (ms)','SCHEDULLED_TANCAR','0',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tancament.expedient.dies',1825,'Interval de dies que han de passar per efectura el tencament de l''expedient a l''arxiu després de marcar l''anotació com a processada','SCHEDULLED_TANCAR','1',false,'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat',null,'Interval de temps entre les execucions de la tasca (ms)','SCHEDULLED_EMAILS_NO_AGRUPATS','0',false,'INT',null,null);
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
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.reenviar.mostrar.permisos',null,'Mostrar usuaris amb permís sobre bústies al reenviar','GENERAL','11',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.default.user.entorn',null,'Entorn on es troba l''aplicació','GENERAL','15',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat',null,'Marcar per no permetre reenviar anotacions a la bústia per defecte de l''entitat','GENERAL','11',false,'BOOL',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE)) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.periode', '3600000', 'Iterval de temps entre les execucions de la tasca (ms). Per defecte 3600000 ms', 'SCHEDULLED_MON_INT', 0, false, 'INT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE)) values ('es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.dies', '30', 'Dies màxim d''antiguitat per guardar registres. Per defecte 30 dies.', 'SCHEDULLED_MON_INT', 1, false, 'INT',null,null);
Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) values ('es.caib.distribucio.backoffice.interval.temps.reintentar.processament', null, 'Interval de temps entre les execucions de la tasca(ms)', 'SCHEDULLED_BACKOFFICE_ERRORS', '1', 'false', 'INT', null, 'false', null, null);
Insert into DIS_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI, LASTMODIFIEDDATE) values ('es.caib.distribucio.backoffice.reintentar.processament.max.reintents', null, 'Nombre màxim de reintents per reintentar el processament al backoffice', 'SCHEDULLED_BACKOFFICE_ERRORS', '2', 'false', 'INT', null, 'false', null, null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.monitor.integracio.errors.temps','48','Indica el temps que es tindrà en compte per mostrar el número d''errors (per defecte 48h.)','GENERAL','15',false,'INT',null, false, null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,CONFIGURABLE,ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.com.esborrany.si.perfils.o.tipus.no.existeix.al.caib',false,'Guardar els annexos com a esborranys quan els tipus i perfils de firma no estàn contemplades a l''Arxiu CAIB','SCHEDULLED_ARXIU','6',false,'BOOL',null,false,null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments',null,'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels procediments en segon pla. Per defecte s''aplicarà el cron corresponent per actualitzar cada divendres a les 15:30h','SCHEDULLED_PROCEDIMENT','7',false,'TEXT',NULL,false,null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.ca',null,'Ruta plantilla informe de traçabilitat en català','GENERAL','16',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.informe.trasabilitat.es',null,'Ruta plantilla informe de traçabilitat en castellà','GENERAL','17',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.ca',null,'Ruta plantilla històric en català','GENERAL','18',false,'TEXT',null,null);
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.plantilla.historic.es',null,'Ruta plantilla històric en castellà','GENERAL','19',false,'TEXT',null,null);

-- Actualització de les propietats que poden ser configurables a nivell d'entitat

UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.anotacions.permetre.reservar';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.favorits';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.coneixement';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.mostrar.permisos';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.arbre.nivell';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.sobreescriure.anotacions.duplicades';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.concsv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.email.remitent';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.aplicacio.codi';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.csv.definicio';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.document.versionable';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.suporta.metadades';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.csv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.connect';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.read';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.classificacio';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.serie.documental';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.connect.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.request.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.filesystem.base.dir';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.signarAnnexos';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.portafib.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugin.validatesignature.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.debug';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.applicationID';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.printxml';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.TransformersTemplatesPath';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.tasca.guardar.annexos.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.tasca.enviar.anotacions.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = 'true' WHERE KEY LIKE 'es.caib.distribucio.tasca.aplicar.regles.max.reintents';