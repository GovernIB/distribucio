ALTER TABLE DIS_ACL_CLASS ADD CLASS_ID_TYPE VARCHAR2(255);
UPDATE DIS_ACL_CLASS SET CLASS_ID_TYPE='java.lang.Long';
ALTER TABLE DIS_ACL_CLASS MODIFY CLASS_ID_TYPE NOT NULL;
UPDATE dis_acl_class SET class = 'es.caib.distribucio.persist.entity.BustiaEntity' WHERE class = 'es.caib.distribucio.core.entity.BustiaEntity';
UPDATE dis_acl_class SET class = 'es.caib.distribucio.persist.entity.EntitatEntity' WHERE class = 'es.caib.distribucio.core.entity.EntitatEntity';
