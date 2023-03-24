#576     Revisar per què les propietats fixades a null no s'actualitzen

UPDATE DIS_CONFIG  t
   SET t."KEY"  = REPLACE(t."KEY" , 'csv.definicio', 'csv_generation_definition')
  WHERE t."KEY" LIKE  'es.caib.distribucio%.plugin.arxiu.caib.csv.definicio';