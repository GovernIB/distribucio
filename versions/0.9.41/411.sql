-- #411 Permetre configurar una adreça de correu electrònic alternativa en el perfil de l'usuari
  
-- Oracle:
ALTER TABLE DIS_USUARI
ADD (
    EMAIL_ALTERNATIU VARCHAR2(200 CHAR) 
);

-- Postgresql:
ALTER TABLE DIS_USUARI
ADD EMAIL_ALTERNATIU character varying(200);