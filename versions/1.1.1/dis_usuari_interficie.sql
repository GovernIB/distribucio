-- Interfície per defecte al entrar a l'aplicació (JSP o REACT)
-- Preferència de l'usuari: nul vol dir que mana la propietat de sistema.
ALTER TABLE DIS_USUARI ADD INTERFICIE_USUARI VARCHAR(5 CHAR);
-- PostgreSQL: ALTER TABLE DIS_USUARI ADD INTERFICIE_USUARI VARCHAR(5);

-- Propietat de sistema que s'aplica quan l'usuari no ha triat cap interfície al seu perfil.
INSERT INTO DIS_CONFIG (KEY,VALUE,DESCRIPTION,GROUP_CODE,POSITION,JBOSS_PROPERTY,TYPE_CODE,CONFIGURABLE) VALUES
    ('es.caib.distribucio.interface.defecte','REACT','Interfície (JSP o REACT) amb la que s''entra a l''aplicació quan l''usuari no n''ha triat cap al seu perfil','GENERAL',24,0,'TEXT',0);
-- PostgreSQL: els camps JBOSS_PROPERTY i CONFIGURABLE són booleans, s'han de posar a false en lloc de 0.
