--#599 No poder assignar anotacions a usuaris inactius

-- Insereix les propietats:
-- es.caib.distribucio.mostrar.usuaris.inactius.ldap=

-- Oracle

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.mostrar.usuaris.inactius.ldap','1','Indica si mostrar els usuaris inactius a la LDAP que tenen permís sobre una bústia','GENERAL','23','0','BOOL',null,null);

-- Postgresql

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.mostrar.usuaris.inactius.ldap',true,'Indica si mostrar els usuaris inactius a la LDAP que tenen permís sobre una bústia','GENERAL','23',false,'BOOL',null,null);