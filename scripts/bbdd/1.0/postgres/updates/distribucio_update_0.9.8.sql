ALTER TABLE dis_registre add data_tancament TIMESTAMP without TIME ZONE;
ALTER TABLE dis_registre add arxiu_tancat boolean default false NOT NULL;
ALTER TABLE dis_registre add arxiu_tancat_error boolean default false NOT NULL;