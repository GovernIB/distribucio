-- #389: Afegir funcionalitat per a administradors de poder marcar una anotació o múltiples, com a acció massiva, per que es sobreescriguin al redistribuir l'anotació
ALTER TABLE DIS_REGISTRE
ADD (
    SOBREESCRIURE BOOLEAN DEFAULT FALSE
);

-- #296 Posar icona/color en els assentaments que estiguin "duplicats" en una bústia 
ALTER TABLE DIS_REGISTRE ADD REACTIVAT BOOLEAN DEFAULT FALSE;
ALTER TABLE DIS_CONT_MOV ADD NUM_DUPLICAT INTEGER;

-- Deixar constància del número de duplicitat d'una anotació (x vegades reenviada a una bústia)
UPDATE DIS_CONT_MOV MOV 
    SET NUM_DUPLICAT = (
        SELECT COUNT(R.NUMERO)
        	FROM DIS_REGISTRE R
        	INNER JOIN DIS_CONT_MOV M ON M.CONTINGUT_ID = R.ID
        	WHERE MOV.DESTI_ID = M.DESTI_ID 
        	AND MOV.CONTINGUT_ID = M.CONTINGUT_ID
        	GROUP BY R.NUMERO, M.DESTI_ID
); 

Insert into DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,LASTMODIFIEDBY_CODI,LASTMODIFIEDDATE) values ('es.caib.distribucio.sobreescriure.anotacions.duplicades','false','Permetre sobreescriure anotacions duplicades al reenviar','GENERAL','12','0','BOOL',null,null);
