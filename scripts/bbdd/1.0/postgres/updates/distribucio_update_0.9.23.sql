
-- #59 Regles a partir del codi SIA
ALTER TABLE DIS_REGLA ADD COLUMN PROCEDIMENT_CODI CHARACTER VARYING(64);
ALTER TABLE DIS_REGLA ALTER COLUMN ASSUMPTE_CODI DROP NOT NULL;

-- #98 Eliminar informació de la data de còpia en el número de registre
-- Cal treure la data existent i modificar la restricció única per peremtre tenir números iguals

-- 1 Afegeix el camp número còpia per comptar el número de còpies per el número de registre formatat
alter table dis_registre add numero_copia integer default 0 not null;

-- 2 Esborra la constraint de clau única que no conté el número de còpia
alter table
   dis_registre
drop constraint
   dis_reg_mult_uk;

-- 3 Actualitza el número de còpia de les dades
do $$
declare
	n_copia integer;
begin
	n_copia := 1;
	raise notice 'Value: %', n_copia;
end $$;


   
do $$
declare
	n_copia integer;
	registre RECORD;
begin
	n_copia := 1;
	--raise notice 'Value: %', n_copia;
	raise notice 'Avaluació del número de còpia per tots els registres amb una data al número de registre';

	-- Selecciona tots els registres amb la data al número de registre
	for registre in 
		select r.id,
        	   r.numero
        from dis_registre r
        where  r.NUMERO ~ '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$'
	 -- Itera i informa el número de copia del registre
    loop
		RAISE NOTICE 'Registre % %:', registre.id, registre.numero;
		-- Consulta el número de fila segons el número de registre
		select rn
	        into n_copia
	    from (
		        select r.id,
		        	   rownum AS rn
				from dis_registre r
				where REGEXP_REPLACE(r.NUMERO,
		                 '(\_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$)',
		                 '')
		              = REGEXP_REPLACE(registre.NUMERO,
		                 '(\_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$)',
		                 '')
		              and r.NUMERO '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$'
				order by r.CREATEDDATE asc
	    )
	    where id = registre.id;
		-- Actualtitza el camp de número de còpia
		 NOTICE 'update dis_registre r set r.numero_copia = % where r.id = %', n_copia, registre.id;
		update dis_REGISTRE r set r.NUMERO_COPIA = n_copia where r.ID = registre.id;
    end loop;
   commit;
end $$;
    
-- 4 Actualitza les dades traient la data del final de la taula de registres i de la del contingut 
update DIS_REGISTRE r
set r.NUMERO = REGEXP_REPLACE(r.NUMERO,
                 '(\_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$)',
                 '')
where r.NUMERO ~ '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$';

update DIS_CONTINGUT c 
set c.nom = REGEXP_REPLACE(c.nom, '(\_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d)', '')
where c.nom ~ '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*'
	and (select r.NUMERO_COPIA from dis_registre r where r.id = c.id ) > 0;


-- 4 Afegeix novament la constraint amb el camp número còpia
alter table dis_registre
	add constraint DIS_REG_MULT_UK 
	unique (
			ENTITAT_CODI, 
			LLIBRE_CODI, 
			TIPUS, NUMERO, 
			DATA, 
			NUMERO_COPIA);



-- #88 Backoffices asíncrons

UPDATE DIS_REGISTRE SET PROCES_ESTAT = 'BUSTIA_PROCESSADA' WHERE PROCES_ESTAT = 'DISTRIBUIT_PROCESSAT';
UPDATE DIS_REGISTRE SET PROCES_ESTAT = 'BACK_PROCESSADA' WHERE PROCES_ESTAT = 'BACK_PROCESSADA';


alter table DIS_REGISTRE add BACK_PENDENT_DATA  timestamp without time zone;
alter table DIS_REGISTRE add BACK_REBUDA_DATA  timestamp without time zone;
alter table DIS_REGISTRE add BACK_PROCES_REBUTJ_ERROR_DATA  timestamp without time zone;
alter table DIS_REGISTRE add BACK_OBSERVACIONS character varying(1024);
alter table DIS_REGISTRE add BACK_RETRY_ENVIAR_DATA  timestamp without time zone;
