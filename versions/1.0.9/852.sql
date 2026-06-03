-- #852 La consulta del localitzador d'annexos és molt lenta

-- Afegeix un índex a la columan de d'estat a l'Arxiu
CREATE INDEX DIS_ESTAT_ARX_REGANX_FK_I ON DIS_REGISTRE_ANNEX(ARXIU_ESTAT);
