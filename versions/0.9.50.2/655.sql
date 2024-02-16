--#655 Dues regles distintes amb mateix SIA i amb UO i bústia d'origen definides, s'executen al mateix temps

-- Nova columna per configurar les regles per finalitzar avaluació

-- Oracle

ALTER TABLE DIS_REGLA ADD ATURAR_AVALUACIO NUMBER(1) DEFAULT 0;
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.avaluar.totes',null,'Avaluar totes les regles en comtes de trobar la primera','SCHEDULLED_REGLES','2','0','BOOL',null,null);

-- Postgresql

ALTER TABLE DIS_REGLA ADD COLUMN ATURAR_AVALUACIO BOOLEAN;
Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.tasca.aplicar.regles.avaluar.totes',null,'Nombre de reintents de aplicar regles','SCHEDULLED_REGLES','2',false,'BOOL',null,null);
