-- Modificació dels valors de la propietat que s'encarrega 
-- d'executar la tasca en segon pla d'actualitzar procediments

-- ORACLE
UPDATE DIS_CONFIG dc 
SET dc.VALUE = NULL  
WHERE dc.KEY = 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments'

UPDATE DIS_CONFIG dc 
SET dc.TYPE_CODE = 'TEXT' 
WHERE dc.KEY = 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments'

UPDATE DIS_CONFIG dc 
SET dc.DESCRIPTION = 'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels procediments en segon pla' 
WHERE dc.KEY = 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments'

-- POSTGRES
UPDATE DIS_CONFIG dc 
SET dc.VALUE = NULL  
WHERE dc.KEY = 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments'

UPDATE DIS_CONFIG dc 
SET dc.TYPE_CODE = 'TEXT' 
WHERE dc.KEY = 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments'

UPDATE DIS_CONFIG dc 
SET dc.DESCRIPTION = 'Especificar l''expressió ''cron'' indicant l''interval de temps de les actualitzacions dels procediments en segon pla' 
WHERE dc.KEY = 'es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments'