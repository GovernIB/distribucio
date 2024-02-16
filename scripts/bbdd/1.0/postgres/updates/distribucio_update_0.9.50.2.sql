-- Distribucio 0.9.50.2

-- Nova columna per configurar les regles per finalitzar avaluació
ALTER TABLE DIS_REGLA ADD COLUMN ATURAR_AVALUACIO BOOLEAN;
-- Nova propietat per establir si avaluar totes les regles o només la primera
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.avaluar.totes',null,'Nombre de reintents de aplicar regles','SCHEDULLED_REGLES','2',false,'BOOL',null,null);