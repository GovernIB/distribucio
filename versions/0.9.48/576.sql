#576     Revisar per què les propietats fixades a null no s'actualitzen

 
UPDATE DIS_CONFIG  t
  SET t."KEY"  = REPLACE(t."KEY" , 'csv_generation_definition', 'csv.generation.definition')
  WHERE t."KEY" LIKE  'es.caib.distribucio%.plugin.arxiu.caib.csv_generation_definition';
