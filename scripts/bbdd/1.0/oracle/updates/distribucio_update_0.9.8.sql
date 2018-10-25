ALTER TABLE dis_registre add data_tancament TIMESTAMP(6);
ALTER TABLE dis_registre add arxiu_tancat NUMBER(1) default 0 NOT NULL;
ALTER TABLE dis_registre add arxiu_tancat_error NUMBER(1) default 0 NOT NULL;