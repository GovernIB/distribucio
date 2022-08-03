
-- Insereix la propietat per desar els annexos com esborrany quan els tipus
-- i els perfils de firmes no estiguin contemplades per l'Arxiu CAIB
-- (es.caib.distribucio.tasca.guardar.annexos.com.esborrany.si.perfils.o.tipus.no.existeix.al.caib)

-- Oracle
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,CONFIGURABLE,ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.com.esborrany.si.perfils.o.tipus.no.existeix.al.caib',0,'Guardar els annexos com a esborranys quan els tipus i perfils de firma no estàn contemplades a l''Arxiu CAIB','SCHEDULLED_ARXIU','6','0','BOOL',null,'0',null,null);

-- Postgresql
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,CONFIGURABLE,ENTITAT_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.com.esborrany.si.perfils.o.tipus.no.existeix.al.caib',false,'Guardar els annexos com a esborranys quan els tipus i perfils de firma no estàn contemplades a l''Arxiu CAIB','SCHEDULLED_ARXIU','6',false,'BOOL',null,false,null,null);
