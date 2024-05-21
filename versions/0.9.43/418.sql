-- #418 Variables configurables a nivell d'entitat. 
-- Script per afegir una nova columna a la bbdd on es mostra per mitjà 
-- d'un booleà si és una propietat configurable i columna codi entitat

-- Oracle

ALTER TABLE DIS_CONFIG ADD ENTITAT_CODI VARCHAR2(64 CHAR);
ALTER TABLE DIS_CONFIG ADD CONFIGURABLE NUMBER(1) DEFAULT 0;
ALTER TABLE DIS_MON_INT ADD CODI_ENTITAT VARCHAR2(64 CHAR);

UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.anotacions.permetre.reservar';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.favorits';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.coneixement';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.mostrar.permisos';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.arbre.nivell';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.sobreescriure.anotacions.duplicades';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.concsv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.email.remitent';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.aplicacio.codi';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.csv.definicio';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.document.versionable';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.suporta.metadades';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.csv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.connect';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.read';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.classificacio';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.serie.documental';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.connect.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.request.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.filesystem.base.dir';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.signarAnnexos';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.portafib.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugin.validatesignature.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.debug';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.applicationID';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.printxml';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.TransformersTemplatesPath';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.tasca.guardar.annexos.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.tasca.enviar.anotacions.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = 1 WHERE KEY LIKE 'es.caib.distribucio.tasca.aplicar.regles.max.reintents';


	   
-- Postgresql
   
ALTER TABLE DIS_CONFIG ADD entitat_codi character varying(64);
ALTER TABLE DIS_CONFIG ADD CONFIGURABLE BOOLEAN DEFAULT FALSE;
ALTER TABLE DIS_MON_INT ADD CODI_ENTITAT character varying(64);


UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.anotacions.permetre.reservar';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.favorits';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.coneixement';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.reenviar.mostrar.permisos';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.contingut.enviar.arbre.nivell';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.sobreescriure.anotacions.duplicades';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.concsv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.email.remitent';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.aplicacio.codi';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.csv.definicio';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.document.versionable';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.suporta.metadades';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.csv.base.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.connect';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.timeout.read';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.usuari';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.arxiu.caib.conversio.imprimible.contrasenya';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.classificacio';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.anotacions.registre.expedient.serie.documental';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.connect.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.unitats.organitzatives.dir3.request.timeout';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.gesdoc.filesystem.base.dir';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.distribucio.fitxers.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.procediment.rolsac.service.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.dadesext.service.url';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.signarAnnexos';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.signatura.portafib.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugin.validatesignature.class';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.debug';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.applicationID';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.printxml';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.TransformersTemplatesPath';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.endpoint';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.password';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.tasca.guardar.annexos.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.tasca.enviar.anotacions.max.reintents';
UPDATE DIS_CONFIG SET CONFIGURABLE = true WHERE KEY LIKE 'es.caib.distribucio.tasca.aplicar.regles.max.reintents';