-- Unificar nomemclatura de les aplicacions #898 (backoffice -> aplicació de client)
UPDATE DIS_CONFIG_GROUP SET DESCRIPTION = 'Tasca periòdica d''enviament d''annotacions a l''aplicació de client' WHERE CODE = 'SCHEDULLED_BACKOFFICE';
UPDATE DIS_CONFIG_GROUP SET DESCRIPTION = 'Tasca periòdica de reintentar anotacions processades amb errors a l''aplicació de client' WHERE CODE = 'SCHEDULLED_BACKOFFICE_ERRORS';

UPDATE DIS_CONFIG SET DESCRIPTION = 'Enviar contingut a l''aplicació de client' WHERE KEY = 'es.caib.distribucio.backoffice.integracio.retornarAnnexIFirmaContingut';
UPDATE DIS_CONFIG SET DESCRIPTION = 'Nombre màxim de reintents per reintentar el processament a l''aplicació de client' WHERE KEY = 'es.caib.distribucio.backoffice.reintentar.processament.max.reintents';
UPDATE DIS_CONFIG SET DESCRIPTION = 'Temps entre correus al responsable de l''aplicació de client (minuts)' WHERE KEY = 'es.caib.distribucio.email.backoffice.responsable.temps';
UPDATE DIS_CONFIG SET DESCRIPTION = 'Nombre de reintents de enviar annotacions a l''aplicació de client'  WHERE KEY like '%.tasca.enviar.anotacions.max.reintents';