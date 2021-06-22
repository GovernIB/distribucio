--#359 Modificar la configuració d'autenticació dels backoffices: ampliar usuari y contrasenya de 64 a 255 caracteres
ALTER TABLE DIS_BACKOFFICE MODIFY USUARI character varying(255);
ALTER TABLE DIS_BACKOFFICE MODIFY CONTRASENYA character varying(255);