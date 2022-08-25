-- #457 Revisar el número d'errades en el monitor d'integracions 
-- Nova propietat per comptar els darrers errors del monitor d'integracions, per defecte 48h

-- Oracle
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,CONFIGURABLE,ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.monitor.integracio.errors.temps','48','Indica el temps que es tindrà en compte per mostrar el número d''errors (per defecte 48h.)','GENERAL','15','0','INT',null, '0', null,null);

-- Postgresql
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,CONFIGURABLE,ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.monitor.integracio.errors.temps','48','Indica el temps que es tindrà en compte per mostrar el número d''errors (per defecte 48h.)','GENERAL','15',false,'INT',null, false, null,null);
