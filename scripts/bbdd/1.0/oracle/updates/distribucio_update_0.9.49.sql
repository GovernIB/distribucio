-- Distribucio 0.9.49

--#572: Poder assignar anotacions a usuaris d'una bústia
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.assignar.anotacions','0','Permetre assignar una anotació a un usuari','GENERAL','22','0','BOOL',null,null);

--#599 No poder assignar anotacions a usuaris inactius
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.mostrar.usuaris.inactius.ldap','1','Indica si mostrar els usuaris inactius a la LDAP que tenen permís sobre una bústia','GENERAL','23','0','BOOL',null,null);