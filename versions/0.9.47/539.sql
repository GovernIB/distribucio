-- Nova columna a la taula 'DIS_PROCEDIMENT' que ens 
-- indica si el procediment està vigent o obsolet. 


-- Oracle
ALTER TABLE DIS_PROCEDIMENT ADD (
ESTAT VARCHAR(20) DEFAULT 'VIGENT' NOT NULL);

-- Postgress
ALTER TABLE DIS_PROCEDIMENT ADD COLUMN
ESTAT VARCHAR(20) DEFAULT 'VIGENT' NOT NULL;
