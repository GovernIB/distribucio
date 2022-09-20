-- #457 script per crear una nova propietat que guarda el temps (en hores) per recuperar el número d'errors al monitor d'integracions
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI, CONFIGURABLE, ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.monitor.integracio.errors.temps','48','Indica el temps que es tindrà en compte per mostrar el número d''errors (per defecte 48h.)','GENERAL','15',false,'INT',null, false, null,null);

-- #478 Revisar tractament de perfils de firma 
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,CONFIGURABLE,ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.com.esborrany.si.perfils.o.tipus.no.existeix.al.caib',false,'Guardar els annexos com a esborranys quan els tipus i perfils de firma no estàn contemplades a l''Arxiu CAIB','SCHEDULLED_ARXIU','6',false,'BOOL',null,false,null,null);
