--#359 Modificar la configuració d'autenticació dels backoffices: ampliar usuari y contrasenya de 64 a 255 caracteres
ALTER TABLE DIS_BACKOFFICE MODIFY USUARI VARCHAR2(255 CHAR);
ALTER TABLE DIS_BACKOFFICE MODIFY CONTRASENYA VARCHAR2(255 CHAR);