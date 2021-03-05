-- #286: Permetre realitzar accions sobre anotacions pendent d'Arxiu que hagin esgotat els reintents

UPDATE DIS_REGISTRE SET PROCES_INTENTS = PROCES_INTENTS -1 WHERE PROCES_ESTAT = 'ARXIU_PENDENT';
UPDATE DIS_REGISTRE SET PROCES_INTENTS = 0 WHERE PROCES_INTENTS = -1;
