-- #411 Permetre configurar una adreça de correu electrònic alternativa en el perfil de l'usuari
ALTER TABLE DIS_USUARI
ADD EMAIL_ALTERNATIU character varying(200);

-- #419 Nova propietat per definir rangs de temps d'inactivitat de reintents de custòdia de documents
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.guardar.annexos.innectivitat.cron',null,'Expressió ''cron'' per definir opcionalment un període d''innactivitat','SCHEDULLED_ARXIU','4',false,'CRON',null,null);
