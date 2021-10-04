--#376 Recordar perfil de l'usuari
-- Oracle:
ALTER TABLE DIS_USUARI ADD ROL_ACTUAL VARCHAR2(64 CHAR);

-- Postgresql:
ALTER TABLE DIS_USUARI ADD ROL_ACTUAL character varying(64);