-- Subgrups per a les propietats que penjaven directament dels grups GENERAL i SCHEDULLED.
-- El menú de propietats només ha de tenir pares que s'expandeixen i fills amb les propietats.
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('GENERAL_COMUNES','GENERAL',0,'Configuracions comunes');
INSERT INTO DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_COMUNES','SCHEDULLED',0,'Configuració comuna de les tasques periòdiques');

-- Mou les propietats (les globals i les còpies per entitat, que comparteixen el grup) als subgrups nous.
UPDATE DIS_CONFIG SET GROUP_CODE = 'GENERAL_COMUNES' WHERE GROUP_CODE = 'GENERAL';
UPDATE DIS_CONFIG SET GROUP_CODE = 'SCHEDULLED_COMUNES' WHERE GROUP_CODE = 'SCHEDULLED';

-- A partir d'ara les propietats noves d'aquests apartats s'han d'afegir als subgrups, no als pares.
-- Funciona igual a Oracle i a PostgreSQL.
