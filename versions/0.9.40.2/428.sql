
-- #428 Revisar el rendiment en la descàrrega del ZIP del justificant i els annexos

-- Oracle
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.generacio.zip.num.threads',null,'Número de threads màxim per a la consulta y generació del .zip de documentació (per defecte 3)','GENERAL','14','0','INT',null,null);

-- Postgresql
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.contingut.generacio.zip.num.threads',null,'Número de threads màxim per a la consulta y generació del .zip de documentació (per defecte 3)','GENERAL','14','0','INT',null,null);
