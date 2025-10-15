-- Implementar un sistema de salut que pugui ser consultat per un sistema centralitzat de monitoratge #703

-- Oracle/Postgresql

UPDATE DIS_CONFIG C SET C.VALUE = 'es.caib.distribucio.plugin.caib.arxiu.ArxiuPluginSalutCaib' WHERE C.KEY = 'es.caib.distribucio.plugin.arxiu.class';
UPDATE DIS_CONFIG C SET C.VALUE = 'es.caib.distribucio.plugin.caib.validacio.ValidacioFirmaPluginApiPortafib' WHERE C.KEY = 'es.caib.distribucio.plugin.validatesignature.class';