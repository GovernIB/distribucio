-- #389: Afegir funcionalitat per a administradors de poder marcar una anotació o múltiples, com a acció massiva, per que es sobreescriguin al redistribuir l'anotació
ALTER TABLE DIS_REGISTRE
ADD (
    SOBREESCRIURE NUMBER(1) DEFAULT 0
);
