-- #860 Revisar el recompte d'errors de les pipelles del monitor
-- Esborra el paràmetre de configuració per comptar errors del monitor
DELETE FROM DIS_CONFIG WHERE KEY = 'es.caib.distribucio.monitor.integracio.errors.temps';