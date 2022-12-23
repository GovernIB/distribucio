-- Nova columna a la taula 'DIS_REGLA' que ens 
-- indica si s'aplicarà a un registre presencial o no. 


-- Oracle
ALTER TABLE DIS_REGLA ADD PRESENCIAL NUMBER(1);

-- Postgress
ALTER TABLE DIS_REGLA ADD COLUMN PRESENCIAL BOOLEAN;