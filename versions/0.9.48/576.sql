-- #576 Revisar per què les propietats fixades a null no s'actualitzen

-- Actualitza la clau antiga amb la nova clau

-- Oracle

UPDATE DIS_CONFIG
SET KEY  = REPLACE(KEY, 'csv.definicio', 'csv_generation_definition')
WHERE KEY LIKE  'es.caib.distribucio%.plugin.arxiu.caib.csv.definicio';

-- Postgesql

UPDATE DIS_CONFIG
SET KEY  = REPLACE(KEY, 'csv.definicio', 'csv_generation_definition')
WHERE KEY LIKE  'es.caib.distribucio%.plugin.arxiu.caib.csv.definicio';