
-- #391 Afegir comprovació de la signatura dels documents signats per portafib

-- Propietats pel la firma en sevidor

-- Canvia la descripció del perfil.
UPDATE DIS_CONFIG
SET DESCRIPTION = 'Propietat per especificar el perfil de firma de l''usuari al Portasignatures. Es pot deixar buit si només en té un d''assignat.'
WHERE KEY LIKE 'es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil';

-- Insereix la propietat pel certificat.
-- es.caib.distribucio.plugin.signatura.portafib.username=afirmades-firma
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) 
VALUES ('es.caib.distribucio.plugin.signatura.portafib.username',null,'Nom del certificat a utilitzar en la firma. És opcional','SIGNATURA','6',false,'TEXT',null,null);

