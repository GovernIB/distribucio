-- #424 No permetre reenviar registres a la bustia principal

-- Oracle
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat',null,'Marcar per no permetre reenviar anotacions a la bústia per defecte de l''entitat','GENERAL','11','0','BOOL',null,null);

-- Posgresql
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat',null,'Marcar per no permetre reenviar anotacions a la bústia per defecte de l''entitat','GENERAL','11',false,'BOOL',null,null);




