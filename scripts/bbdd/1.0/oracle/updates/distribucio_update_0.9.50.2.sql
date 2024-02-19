-- Distribucio 0.9.50.2

-- Nova columna per configurar les regles per finalitzar avaluació
ALTER TABLE DIS_REGLA ADD ATURAR_AVALUACIO NUMBER(1) DEFAULT 0;
-- Nova propietat per establir si avaluar totes les regles o només la primera
<<<<<<< dis-1.0
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.avaluar.totes',null,'Avaluar totes les regles en comtes de trobar la primera','SCHEDULLED_REGLES','2','0','BOOL',null,null);
=======
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.avaluar.totes',null,'Avaluar totes les regles en comtes de trobar la primera','SCHEDULLED_REGLES','2','0','BOOL',null,null);
-- Corregir la descripció de la tasca d'execució de regles
UPDATE DIS_CONFIG_GROUP SET DESCRIPTION = 'Tasca periòdica d''aplicació de regles' WHERE CODE = 'SCHEDULLED_REGLES';
>>>>>>> 39dbe6f Tancament i correccions DISTRIBUCIÓ v0.9.50.2 (2024.02.15 13:00h)
