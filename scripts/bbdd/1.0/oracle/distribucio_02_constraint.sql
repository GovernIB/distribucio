ALTER TABLE DIS_ALERTA ADD (
  CONSTRAINT DIS_ALERTA_PK PRIMARY KEY (ID));

ALTER TABLE DIS_USUARI ADD (
  CONSTRAINT DIS_USUARI_PK PRIMARY KEY (CODI));

ALTER TABLE DIS_ENTITAT ADD (
  CONSTRAINT DIS_ENTITAT_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_ENTITAT_CODI_UK UNIQUE (CODI));

ALTER TABLE DIS_CONT_MOV ADD (
  CONSTRAINT DIS_CONTMOV_PK PRIMARY KEY (ID));
  
ALTER TABLE DIS_CONT_MOV_EMAIL ADD (
  CONSTRAINT DIS_CONT_MOV_EMAIL_PK PRIMARY KEY (ID));

ALTER TABLE DIS_CONT_LOG ADD (
  CONSTRAINT DIS_CONT_LOG_PK PRIMARY KEY (ID));

ALTER TABLE DIS_BUSTIA ADD (
  CONSTRAINT DIS_BUSTIA_PK PRIMARY KEY (ID));
  
ALTER TABLE DIS_UNITAT_ORGANITZATIVA ADD (
  CONSTRAINT DIS_UNITAT_ORGANITZATIVA_PK PRIMARY KEY (ID));

ALTER TABLE DIS_REGISTRE ADD (
  CONSTRAINT DIS_REGISTRE_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_REG_MULT_UK UNIQUE (ENTITAT_CODI, LLIBRE_CODI, TIPUS, NUMERO, DATA, NUMERO_COPIA));

ALTER TABLE DIS_REGISTRE_ANNEX_FIRMA ADD (
  CONSTRAINT DIS_REGANXFIR_PK PRIMARY KEY (ID));  

ALTER TABLE DIS_REGISTRE_INTER ADD (
  CONSTRAINT DIS_REGINT_PK PRIMARY KEY (ID));

ALTER TABLE DIS_REGLA ADD (
  CONSTRAINT DIS_REGLA_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_REGLA_MULT_UK UNIQUE (ENTITAT_ID, NOM, TIPUS, ASSUMPTE_CODI));
  
ALTER TABLE DIS_CONT_COMMENT ADD (
  CONSTRAINT DIS_CONTCOMMENT_PK PRIMARY KEY (ID));  

ALTER TABLE DIS_REGISTRE_FIRMA_DETALL ADD (
  CONSTRAINT DIS_REGISTRE_FIRMA_DETALL_PK PRIMARY KEY (ID)); 
 
ALTER TABLE DIS_ALERTA ADD (
  CONSTRAINT DIS_CONTINGUT_ALERTA_FK FOREIGN KEY (CONTINGUT_ID) 
    REFERENCES DIS_CONTINGUT (ID),
  CONSTRAINT DIS_USUCRE_ALERTA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_ALERTA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));
    
ALTER TABLE DIS_ENTITAT ADD (
  CONSTRAINT DIS_USUCRE_ENTITAT_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_ENTITAT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));

ALTER TABLE DIS_CONTINGUT ADD (
  CONSTRAINT DIS_PARE_CONTINGUT_FK FOREIGN KEY (PARE_ID) 
    REFERENCES DIS_CONTINGUT(ID),
  CONSTRAINT DIS_CONTMOV_CONTINGUT_FK FOREIGN KEY (CONTMOV_ID) 
    REFERENCES DIS_CONT_MOV (ID),
  CONSTRAINT DIS_ENTITAT_CONTINGUT_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES DIS_ENTITAT (ID),
  CONSTRAINT DIS_USUCRE_CONTINGUT_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_CONTINGUT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));

ALTER TABLE DIS_CONT_MOV ADD (
  CONSTRAINT DIS_ORIGEN_CONTMOV_FK FOREIGN KEY (ORIGEN_ID) 
    REFERENCES DIS_CONTINGUT (ID),
  CONSTRAINT DIS_DESTI_CONTMOV_FK FOREIGN KEY (DESTI_ID) 
    REFERENCES DIS_CONTINGUT (ID),
  CONSTRAINT DIS_REMITENT_CONTMOV_FK FOREIGN KEY (REMITENT_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUCRE_CONTMOV_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_CONTMOV_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));

ALTER TABLE DIS_CONT_LOG ADD (
  CONSTRAINT DIS_PARE_CONTLOG_FK FOREIGN KEY (PARE_ID) 
    REFERENCES DIS_CONT_LOG (ID),
  CONSTRAINT DIS_CONTMOV_CONTLOG_FK FOREIGN KEY (CONTMOV_ID) 
    REFERENCES DIS_CONT_MOV (ID),
  CONSTRAINT DIS_USUCRE_CONTLOG_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_CONTLOG_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));

ALTER TABLE DIS_BUSTIA ADD (
  CONSTRAINT DIS_CONTINGUT_BUSTIA_FK FOREIGN KEY (ID) 
    REFERENCES DIS_CONTINGUT (ID));

ALTER TABLE DIS_REGISTRE ADD (
  CONSTRAINT DIS_CONTINGUT_REGISTRE_FK FOREIGN KEY (ID) 
    REFERENCES DIS_CONTINGUT (ID),
  CONSTRAINT DIS_REGLA_REGISTRE_FK FOREIGN KEY (REGLA_ID) 
    REFERENCES DIS_REGLA (ID),
  CONSTRAINT DIS_USUCRE_REGISTRE_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_REGISTRE_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));

ALTER TABLE DIS_REGISTRE_ANNEX ADD (
  CONSTRAINT DIS_USUCRE_REGANX_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_REGISTRE_REGANX_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES DIS_REGISTRE (ID),
  CONSTRAINT DIS_USUMOD_REGANX_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));
    
ALTER TABLE DIS_REGISTRE_ANNEX_FIRMA ADD (
  CONSTRAINT DIS_USUCRE_REGANXFIR_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_FIRMA_ANNEX_FK FOREIGN KEY (ANNEX_ID) 
    REFERENCES DIS_REGISTRE_ANNEX (ID),
  CONSTRAINT DIS_USUMOD_REGANXFIR_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));

ALTER TABLE DIS_REGISTRE_INTER ADD (
  CONSTRAINT DIS_USUCRE_REGINT_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_REGISTRE_REGINT_FK FOREIGN KEY (REGISTRE_ID) 
    REFERENCES DIS_REGISTRE (ID),
  CONSTRAINT DIS_REPRESENTANT_REGINT_FK FOREIGN KEY (REPRESENTANT_ID) 
    REFERENCES DIS_REGISTRE_INTER (ID),
  CONSTRAINT DIS_REPRESENTAT_REGINT_FK FOREIGN KEY (REPRESENTAT_ID) 
    REFERENCES DIS_REGISTRE_INTER (ID),
  CONSTRAINT DIS_USUMOD_REGINT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));

ALTER TABLE DIS_REGLA ADD (
  CONSTRAINT DIS_ENTITAT_REGLA_FK FOREIGN KEY (ENTITAT_ID) 
    REFERENCES DIS_ENTITAT (ID),
  CONSTRAINT DIS_BUSTIA_REGLA_FK FOREIGN KEY (BUSTIA_ID) 
    REFERENCES DIS_BUSTIA (ID),
  CONSTRAINT DIS_USUCRE_REGLA_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_REGLA_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));


ALTER TABLE DIS_ACL_CLASS ADD (
  CONSTRAINT DIS_ACL_CLASS_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_ACL_CLASS_CLASS_UK UNIQUE (CLASS));

ALTER TABLE DIS_ACL_ENTRY ADD (
  CONSTRAINT DIS_ACL_ENTRY_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_ACL_ENTRY_IDENT_ORDER_UK UNIQUE (ACL_OBJECT_IDENTITY, ACE_ORDER));

ALTER TABLE DIS_ACL_OBJECT_IDENTITY ADD (
  CONSTRAINT DIS_ACL_OID_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_ACL_IOD_CLASS_IDENTITY_UK UNIQUE (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY));

ALTER TABLE DIS_ACL_SID ADD (
  CONSTRAINT DIS_ACL_SID_PK PRIMARY KEY (ID),
  CONSTRAINT DIS_ACL_SID_PRINCIPAL_SID_UK UNIQUE (SID, PRINCIPAL));


ALTER TABLE DIS_ACL_ENTRY ADD CONSTRAINT DIS_ACL_ENTRY_GRANTING_CK
  CHECK (GRANTING in (1,0));

ALTER TABLE DIS_ACL_ENTRY ADD CONSTRAINT DIS_ACL_ENTRY_AUDIT_SUCCESS_CK
  CHECK (AUDIT_SUCCESS in (1,0));

ALTER TABLE DIS_ACL_ENTRY ADD CONSTRAINT DIS_ACL_ENTRY_AUDIT_FAILURE_CK
  CHECK (AUDIT_FAILURE in (1,0));

ALTER TABLE DIS_ACL_OBJECT_IDENTITY ADD CONSTRAINT DIS_ACL_OID_ENTRIES_CK
  CHECK (ENTRIES_INHERITING in (1,0));

ALTER TABLE DIS_ACL_SID ADD CONSTRAINT DIS_ACL_SID_PRINCIPAL_CK
  CHECK (PRINCIPAL in (1,0));


ALTER TABLE DIS_ACL_ENTRY ADD CONSTRAINT DIS_ACL_OID_ENTRY_FK
  FOREIGN KEY (ACL_OBJECT_IDENTITY)
  REFERENCES DIS_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE DIS_ACL_ENTRY ADD CONSTRAINT DIS_ACL_SID_ENTRY_FK
  FOREIGN KEY (SID)
  REFERENCES DIS_ACL_SID (ID);

ALTER TABLE DIS_ACL_OBJECT_IDENTITY ADD CONSTRAINT DIS_ACL_CLASS_OID_FK
  FOREIGN KEY (OBJECT_ID_CLASS)
  REFERENCES DIS_ACL_CLASS (ID);

ALTER TABLE DIS_ACL_OBJECT_IDENTITY ADD CONSTRAINT DIS_ACL_PARENT_OID_FK
  FOREIGN KEY (PARENT_OBJECT)
  REFERENCES DIS_ACL_OBJECT_IDENTITY (ID);

ALTER TABLE DIS_ACL_OBJECT_IDENTITY ADD CONSTRAINT DIS_ACL_SID_OID_FK
  FOREIGN KEY (OWNER_SID)
  REFERENCES DIS_ACL_SID (ID);
  
ALTER TABLE DIS_CONT_COMMENT ADD (
  CONSTRAINT DIS_CONT_CONTCOMMENT_FK FOREIGN KEY (CONTINGUT_ID) 
    REFERENCES DIS_CONTINGUT (ID),
  CONSTRAINT DIS_USUCRE_CONTCOMMENT_FK FOREIGN KEY (CREATEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI),
  CONSTRAINT DIS_USUMOD_CONTCOMMENT_FK FOREIGN KEY (LASTMODIFIEDBY_CODI) 
    REFERENCES DIS_USUARI (CODI));
    
 ALTER TABLE DIS_CONT_MOV_EMAIL ADD (
  CONSTRAINT DIS_BUSTIA_CONTMOVEMAIL_FK FOREIGN KEY (BUSTIA_ID) REFERENCES DIS_BUSTIA (ID),
  CONSTRAINT DIS_CONTMOV_CONTMOVEMAIL_FK FOREIGN KEY (CONTINGUT_MOVIMENT_ID) REFERENCES DIS_CONT_MOV (ID),
  CONSTRAINT DIS_CONT_CONTMOVEMAIL_FK FOREIGN KEY (CONTINGUT_ID) REFERENCES DIS_CONTINGUT (ID));
  
ALTER TABLE DIS_BUSTIA ADD (
  CONSTRAINT DIS_UNITAT_BUSTIA_FK FOREIGN KEY (UNITAT_ID) 
    REFERENCES DIS_UNITAT_ORGANITZATIVA (ID));
 
ALTER TABLE DIS_REGLA ADD (
  CONSTRAINT DIS_UNITAT_REGLA_FK FOREIGN KEY (UNITAT_ID) 
    REFERENCES DIS_UNITAT_ORGANITZATIVA (ID));

ALTER TABLE DIS_AVIS ADD (
  CONSTRAINT DIS_AVIS_PK PRIMARY KEY (ID));

ALTER TABLE DIS_UO_SINC_REL ADD (
  CONSTRAINT DIS_UNITAT_ANTIGA_FK FOREIGN KEY (ANTIGA_UO) 
    REFERENCES DIS_UNITAT_ORGANITZATIVA (ID));
ALTER TABLE DIS_UO_SINC_REL ADD (
  CONSTRAINT DIS_UNITAT_NOVA_FK FOREIGN KEY (NOVA_UO) 
    REFERENCES DIS_UNITAT_ORGANITZATIVA (ID));
ALTER TABLE DIS_UO_SINC_REL ADD CONSTRAINT DIS_UO_SINC_REL_MULT_UK UNIQUE (ANTIGA_UO, NOVA_UO);


ALTER TABLE DIS_REGISTRE_FIRMA_DETALL ADD CONSTRAINT DIS_FIRMA_REGISTRE_DETALL_FK FOREIGN KEY (FIRMA_ID) REFERENCES DIS_REGISTRE_ANNEX_FIRMA (ID);
ALTER TABLE DIS_REGISTRE ADD CONSTRAINT DIS_JUSTIFICANT_REGISTRE_FK FOREIGN KEY (JUSTIFICANT_ID) REFERENCES DIS_REGISTRE_ANNEX (ID);

ALTER TABLE DIS_CONT_LOG_PARAM ADD CONSTRAINT DIS_CONT_LOG_PARAM_PK PRIMARY KEY (ID);
ALTER TABLE DIS_CONT_LOG_PARAM ADD CONSTRAINT DIS_CONT_LOG_CONT_LOG_PARAM_FK FOREIGN KEY (CONT_LOG_ID) REFERENCES DIS_CONT_LOG(ID);


ALTER TABLE DIS_BACKOFFICE ADD CONSTRAINT DIS_BACKOFFICE_PK PRIMARY KEY (ID);
ALTER TABLE DIS_BACKOFFICE ADD CONSTRAINT DIS_ENTITAT_BACKOFFICE_FK FOREIGN KEY (ENTITAT_ID) REFERENCES DIS_ENTITAT(ID);

ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_BUSTIA_FILTRE_REGLA_FK FOREIGN KEY (BUSTIA_FILTRE_ID) REFERENCES DIS_BUSTIA(ID);
ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_BACKOFFICE_DESTI_REGLA_FK FOREIGN KEY (BACKOFFICE_DESTI_ID) REFERENCES DIS_BACKOFFICE(ID);
ALTER TABLE DIS_REGLA ADD CONSTRAINT DIS_UNITAT_DESTI_REGLA_FK FOREIGN KEY (UNITAT_DESTI_ID) REFERENCES DIS_UNITAT_ORGANITZATIVA(ID);