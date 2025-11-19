-- Si una anotació roman massa temps en estat COMUNICADA AL BACKOFFICE que torni a estat PENDENT  #772
ALTER TABLE DIS_REGISTRE ADD BACK_COMUNICADA_DATA TIMESTAMP;

-- Posa la data de comunicada als registres existents a partir dels logs
UPDATE DIS_REGISTRE r 
SET r.BACK_COMUNICADA_DATA = 
NVL((SELECT max(CREATEDDATE)
	FROM DIS_CONT_LOG log
	WHERE log.TIPUS  IN ('BACK_COMUNICADA', 'BACK_REBUDA')
		AND log.CONTINGUT_ID = r.id),
	SYSDATE
)
WHERE r.PROCES_ESTAT = 'BACK_COMUNICADA'
AND r.BACK_COMUNICADA_DATA IS NULL;


INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_COMUNICAT_A_PENDENT','SCHEDULLED','20','Tasca periòdica de canvi d''estat anotacions comunicades a pendents');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.tasca.canviarAPendent.temps.espera.execucio', null, 'Iterval de temps entre les execucions de la tasca (ms). Per defecte 60000', 'SCHEDULLED_COMUNICAT_A_PENDENT', '1', '0', 'INT');
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE) VALUES ('es.caib.distribucio.tasca.canviarAPendent.maxim.temps.estat.comunicada', null, 'Màxim de dies que pot estar comunicada abans de canviar l''estat a pendent d''usuari. Per defecte 30.', 'SCHEDULLED_COMUNICAT_A_PENDENT', '2', '0', 'INT');
