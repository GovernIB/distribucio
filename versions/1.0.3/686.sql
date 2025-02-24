-- Permetre classifcar una anotació dins d'un servei #686

-- Oracle
ALTER TABLE dis_registre ADD servei_codi varchar2(64 CHAR) NULL;
ALTER TABLE dis_regla ADD servei_codi varchar2(1024 CHAR) NULL;

-- Postgres
ALTER TABLE dis_registre ADD servei_codi varchar(64) NULL;
ALTER TABLE dis_regla ADD servei_codi varchar(1024) NULL;

