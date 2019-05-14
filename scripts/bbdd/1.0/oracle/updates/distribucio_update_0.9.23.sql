-- #98 Eliminar informació de la data de còpia en el número de registre
-- Cal treure la data existent i modificar la restricció única per peremtre tenir números iguals

-- 1 Afegeix el camp número còpia per comptar el número de còpies per el número de registre formatat
ALTER TABLE DIS_REGISTRE ADD NUMERO_COPIA NUMBER(19);
UPDATE DIS_REGISTRE SET NUMERO_COPIA = 0;

-- 2 Esborra la constraint de clau única que no conté el número de còpia
ALTER TABLE
   DIS_REGISTRE
drop constraint
   DIS_REG_MULT_UK;

-- 3 Actualitza el número de còpia de les dades
declare 
	n_copia number;
begin
	dbms_output.put_line('Avaluació del número de còpia per tots els registres amb una data al número de registre');


	-- Selecciona tots els registres amb la data al número de registre
    for registre in 
          ( select r.id,
          		   r.numero
            from dis_registre r
            where  REGEXP_LIKE(r.NUMERO, '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$')
          )
        -- Itera i informa el número de copia del registre
    loop
	    	dbms_output.put_line('Registre '||registre.id||' '||registre.numero||':');
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
		              and REGEXP_LIKE(r.NUMERO, '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$')
				order by r.CREATEDDATE asc
	        )
	        where id = registre.id;

			-- Actualtitza el camp de número de còpia
			dbms_output.put_line('update dis_registre r set r.numero_copia = '||n_copia||' where r.id = '||registre.id);
			update dis_REGISTRE R set R.NUMERO_COPIA = n_copia where R.ID = registre.id;
    end loop;
   commit;
end;
    
-- 4 Actualitza les dades traient la data del final de la taula de registres i de la del contingut 
UPDATE DIS_REGISTRE r
SET r.NUMERO = REGEXP_REPLACE(r.NUMERO,
                 '(\_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$)',
                 '')
WHERE REGEXP_LIKE(r.NUMERO, '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*$');

UPDATE DIS_CONTINGUT c 
SET c.nom = REGEXP_REPLACE(c.nom, '(\_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d)', '')
WHERE REGEXP_LIKE(c.nom, '_\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d*')
	AND (SELECT r.NUMERO_COPIA FROM dis_registre r WHERE r.id = c.id ) > 0;


-- 4 Afegeix novament la constraint amb el camp número còpia
ALTER TABLE DIS_REGISTRE 
	ADD CONSTRAINT DIS_REG_MULT_UK 
	UNIQUE (
			ENTITAT_CODI, 
			LLIBRE_CODI, 
			TIPUS, NUMERO, 
			DATA, 
			NUMERO_COPIA);

