-- #457 script per crear una nova propietat que guarda el temps (en hores) per recuperar el número d'errors al monitor d'integracions


Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.monitor.integracio.errors.temps','48','Indica el temps que es tindrà en compte per mostrar el número d''errors (per defecte 48h.)','GENERAL','15','0','INT',null, '0', null,null);