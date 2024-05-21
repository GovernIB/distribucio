-- #155: Actualitzar interficie d'integració amb registre per tractar metadades de digitalització 

ALTER TABLE DIS_REGISTRE_ANNEX
ADD (
    META_DADES VARCHAR2(4000)
);

-- #228: Error en altes d'anotacions amb observacions de més de 50 caràcters

ALTER TABLE DIS_ALERTA MODIFY 
(
  TEXT                 	VARCHAR2(256 CHAR),
  ERROR                	VARCHAR2(2048 CHAR),
  CREATEDBY_CODI       	VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_USUARI MODIFY
(
  CODI          		VARCHAR2(64 CHAR),
  NIF           		VARCHAR2(9 CHAR),
  NOM           		VARCHAR2(200 CHAR),
  EMAIL         		VARCHAR2(200 CHAR),
  IDIOMA				VARCHAR2(2 CHAR)
);

ALTER TABLE DIS_ENTITAT MODIFY
(
  CODI                 VARCHAR2(64 CHAR),
  NOM                  VARCHAR2(256 CHAR),
  DESCRIPCIO           VARCHAR2(1024 CHAR),
  CIF                  VARCHAR2(9 CHAR),
  CODI_DIR3            VARCHAR2(9 CHAR),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_CONTINGUT MODIFY
(
  NOM                  VARCHAR2(1024 CHAR),
  TIPUS                VARCHAR2(8 CHAR),
  ARXIU_UUID           VARCHAR2(36 CHAR),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_CONT_MOV MODIFY
(
  REMITENT_CODI        VARCHAR2(64 CHAR),
  COMENTARI            VARCHAR2(256 CHAR),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


ALTER TABLE DIS_CONT_MOV_EMAIL MODIFY 
(
  DESTINATARI_CODI		VARCHAR2(64 CHAR),
  DESTINATARI_EMAIL		VARCHAR2(256 CHAR),
  UNITAT_ORGANITZATIVA 	VARCHAR2(256 CHAR),
  CREATEDBY_CODI       	VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_CONT_LOG MODIFY
(
  TIPUS                VARCHAR2(30 CHAR),
  OBJECTE_ID           VARCHAR2(256 CHAR),
  OBJECTE_LOG_TIPUS    VARCHAR2(30 CHAR),
  OBJECTE_TIPUS        VARCHAR2(12 CHAR),
  PARAM1               VARCHAR2(256 CHAR),
  PARAM2               VARCHAR2(256 CHAR),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_REGISTRE MODIFY
(
  TIPUS                  VARCHAR2(1 CHAR),
  UNITAT_ADM             VARCHAR2(21 CHAR),
  UNITAT_ADM_DESC        VARCHAR2(100 CHAR),
  NUMERO                 VARCHAR2(255 CHAR),
  IDENTIFICADOR          VARCHAR2(100 CHAR),
  ENTITAT_CODI           VARCHAR2(255 CHAR),
  ENTITAT_DESC           VARCHAR2(255 CHAR),
  OFICINA_CODI           VARCHAR2(21 CHAR),
  OFICINA_DESC           VARCHAR2(300 CHAR),
  LLIBRE_CODI            VARCHAR2(4 CHAR),
  LLIBRE_DESC            VARCHAR2(255 CHAR),
  EXTRACTE               VARCHAR2(240 CHAR),
  ASSUMPTE_TIPUS_CODI    VARCHAR2(16 CHAR),
  ASSUMPTE_TIPUS_DESC    VARCHAR2(100 CHAR),
  ASSUMPTE_CODI          VARCHAR2(16 CHAR),
  ASSUMPTE_DESC          VARCHAR2(255 CHAR),
  PROCEDIMENT_CODI       VARCHAR2(64 CHAR),
  REFERENCIA             VARCHAR2(16 CHAR),
  EXPEDIENT_NUM          VARCHAR2(80 CHAR),
  NUM_ORIG 			     VARCHAR2(80 CHAR),
  IDIOMA_CODI            VARCHAR2(19 CHAR),
  IDIOMA_DESC            VARCHAR2(100 CHAR),
  TRANSPORT_TIPUS_CODI   VARCHAR2(20 CHAR),
  TRANSPORT_TIPUS_DESC   VARCHAR2(100 CHAR),
  TRANSPORT_NUM          VARCHAR2(20 CHAR),
  USUARI_CODI            VARCHAR2(20 CHAR),
  USUARI_NOM             VARCHAR2(767 CHAR),
  USUARI_CONTACTE        VARCHAR2(255 CHAR),
  APLICACIO_CODI         VARCHAR2(255 CHAR),
  APLICACIO_VERSIO       VARCHAR2(255 CHAR),
  DOCFIS_CODI            VARCHAR2(19 CHAR),
  DOCFIS_DESC            VARCHAR2(100 CHAR),
  OBSERVACIONS           VARCHAR2(50 CHAR),
  PROCES_ESTAT           VARCHAR2(64 CHAR),
  PROCES_ERROR           VARCHAR2(1024 CHAR),
  CREATEDBY_CODI         VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI    VARCHAR2(256 CHAR),
  PROCES_ESTAT_SISTRA    VARCHAR2(16 CHAR),
  SISTRA_ID_TRAM 	     VARCHAR2(20 CHAR),
  SISTRA_ID_PROC 	     VARCHAR2(100 CHAR),
  OFICINA_ORIG_CODI      VARCHAR2(21 CHAR),
  OFICINA_ORIG_DESC      VARCHAR2(100 CHAR),
  JUSTIFICANT_ARXIU_UUID VARCHAR2(256 CHAR),
  EXPEDIENT_ARXIU_UUID   VARCHAR2(100 CHAR),
  BACK_OBSERVACIONS 	VARCHAR2(4000 CHAR),
  BACK_CODI 			VARCHAR2(20 CHAR)
);

ALTER TABLE DIS_REGISTRE_ANNEX MODIFY
(
  TITOL                VARCHAR2(200 CHAR),
  FITXER_NOM           VARCHAR2(256 CHAR),
  FITXER_MIME          VARCHAR2(100 CHAR),
  FITXER_ARXIU_UUID    VARCHAR2(256 CHAR),
  LOCALITZACIO         VARCHAR2(80 CHAR),
  ORIGEN_CIUADM        VARCHAR2(1 CHAR),
  NTI_TIPUS_DOC        VARCHAR2(4 CHAR),
  SICRES_TIPUS_DOC     VARCHAR2(2 CHAR),
  NTI_ELABORACIO_ESTAT VARCHAR2(4 CHAR),
  OBSERVACIONS         VARCHAR2(50 CHAR),
  FIRMA_FITXER_NOM     VARCHAR2(80 CHAR),
  FIRMA_FITXER_MIME    VARCHAR2(30 CHAR),
  FIRMA_FITXER_ARXIU_UUID  VARCHAR2(100 CHAR),
  FIRMA_CSV            VARCHAR2(256 CHAR),
  TIMESTAMP            VARCHAR2(100 CHAR),
  VALIDACIO_OCSP       VARCHAR2(100 CHAR),
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  GESDOC_DOC_ID 	   VARCHAR2(50 CHAR),
  META_DADES 		   VARCHAR2(4000 CHAR)
);

ALTER TABLE DIS_REGISTRE_ANNEX_FIRMA MODIFY
(
  TIPUS		           VARCHAR2(30 CHAR),
  PERFIL    	       VARCHAR2(30 CHAR),
  FITXER_NOM           VARCHAR2(256 CHAR),
  TIPUS_MIME           VARCHAR2(30 CHAR),
  CSV_REGULACIO		   VARCHAR2(640 CHAR),
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  GESDOC_FIR_ID 	   VARCHAR2(50 CHAR)
);

ALTER TABLE DIS_REGISTRE_FIRMA_DETALL MODIFY
(
    RESPONSABLE_NIF VARCHAR2(30 CHAR),
    RESPONSABLE_NOM VARCHAR2(256 CHAR),
    EMISSOR_CERTIFICAT VARCHAR2(2000 CHAR),
    CREATEDBY_CODI       	VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI  	VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_REGISTRE_INTER MODIFY
(
  ADRESA               VARCHAR2(640 CHAR),
  CANAL_PREF           VARCHAR2(8 CHAR),
  CODI_POSTAL          VARCHAR2(20 CHAR),
  DOC_NUM              VARCHAR2(68 CHAR),
  DOC_TIPUS            VARCHAR2(4 CHAR),
  EMAIL                VARCHAR2(640 CHAR),
  EMAIL_HAB            VARCHAR2(640 CHAR),
  LLINATGE1            VARCHAR2(255 CHAR),
  LLINATGE2            VARCHAR2(255 CHAR),
  NOM                  VARCHAR2(255 CHAR),
  OBSERVACIONS         VARCHAR2(640 CHAR),
  PAIS                 VARCHAR2(100 CHAR),
  PAIS_CODI            VARCHAR2(4 CHAR),
  PROVINCIA            VARCHAR2(100 CHAR),
  PROVINCIA_CODI       VARCHAR2(4 CHAR),
  MUNICIPI             VARCHAR2(100 CHAR),
  MUNICIPI_CODI        VARCHAR2(4 CHAR),
  RAO_SOCIAL           VARCHAR2(2000 CHAR),
  TELEFON              VARCHAR2(80 CHAR),
  TIPUS                VARCHAR2(19 CHAR),
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  CODI_DIRE 			VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_REGLA MODIFY
(
  ASSUMPTE_CODI        VARCHAR2(16 CHAR),
  DESCRIPCIO           VARCHAR2(1024 CHAR),
  NOM                  VARCHAR2(256 CHAR),
  TIPUS                VARCHAR2(32 CHAR),
  UNITAT_CODI          VARCHAR2(9 CHAR),
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  CONTRASENYA          VARCHAR2(64 CHAR),
  TIPUS_BACKOFFICE     VARCHAR2(255 CHAR),
  URL                  VARCHAR2(256 CHAR),
  USUARI               VARCHAR2(64 CHAR),
  PROCEDIMENT_CODI     VARCHAR2(64 CHAR),
  BACKOFFICE_CODI     VARCHAR2(20 CHAR)
);

ALTER TABLE DIS_ACL_CLASS MODIFY
(
  CLASS  VARCHAR2(100 CHAR)
);

ALTER TABLE DIS_ACL_SID MODIFY
(
  SID        VARCHAR2(100 CHAR)
);

ALTER TABLE DIS_CONT_COMMENT MODIFY
(
  TEXT				   VARCHAR2 (1024 CHAR),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_UNITAT_ORGANITZATIVA MODIFY 
(
  CODI 						VARCHAR2(9 CHAR),
  DENOMINACIO 				VARCHAR2(300 CHAR),
  NIF_CIF 					VARCHAR2(9 CHAR),
  CODI_UNITAT_SUPERIOR 		VARCHAR2(9 CHAR),
  CODI_UNITAT_ARREL 		VARCHAR2(9 CHAR),
  ESTAT 					VARCHAR2(1 CHAR),
  CODI_PAIS 				VARCHAR2(3 CHAR),
  CODI_COMUNITAT 			VARCHAR2(2 CHAR),
  CODI_PROVINCIA 			VARCHAR2(2 CHAR),
  CODI_POSTAL 				VARCHAR2(5 CHAR),
  NOM_LOCALITAT 			VARCHAR2(50 CHAR),
  LOCALITAT 				VARCHAR2(40 CHAR),
  ADRESSA 					VARCHAR2(70 CHAR),
  NOM_VIA 					VARCHAR2(200 CHAR),
  NUM_VIA 					VARCHAR2(100 CHAR),
  TIPUS_TRANSICIO 		    VARCHAR2(12 CHAR),
  CREATEDBY_CODI       		VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI  		VARCHAR2(256 CHAR),
  CODI_DIR3_ENTITAT		 	VARCHAR2(9 CHAR)
);

ALTER TABLE DIS_AVIS MODIFY
(
  ASSUMPTE             VARCHAR2(256 CHAR),
  MISSATGE             VARCHAR2(2048 CHAR),
  AVIS_NIVELL         VARCHAR2(10 CHAR),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);

ALTER TABLE DIS_CONT_LOG_PARAM MODIFY
(
    VALOR VARCHAR2(256 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR)
);