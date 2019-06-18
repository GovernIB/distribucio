
CREATE TABLE DIS_ALERTA
(
  ID                   	NUMBER(19)             	NOT NULL,
  TEXT                 	VARCHAR2(256)           NOT NULL,
  ERROR                	VARCHAR2(2048),
  LLEGIDA              	NUMBER(1)               NOT NULL,
  CONTINGUT_ID         	NUMBER(19),
  CREATEDBY_CODI       	VARCHAR2(64),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64),
  LASTMODIFIEDDATE     	TIMESTAMP(6)
);

CREATE TABLE DIS_USUARI
(
  CODI          		VARCHAR2(64)            NOT NULL,
  INICIALITZAT  		NUMBER(1),
  NIF           		VARCHAR2(9),
  NOM           		VARCHAR2(200),
  EMAIL         		VARCHAR2(200),
  IDIOMA				VARCHAR2(2) DEFAULT 'CA' NOT NULL,
  REBRE_EMAILS  		NUMBER(1,0),
  EMAILS_AGRUPATS		NUMBER(1,0),
  VERSION       		NUMBER(19)              NOT NULL
);


CREATE TABLE DIS_ENTITAT
(
  ID                   NUMBER(19)               NOT NULL,
  CODI                 VARCHAR2(64)             NOT NULL,
  NOM                  VARCHAR2(256)            NOT NULL,
  DESCRIPCIO           VARCHAR2(1024),
  CIF                  VARCHAR2(9)              NOT NULL,
  CODI_DIR3            VARCHAR2(9)              NOT NULL,
  ACTIVA               NUMBER(1),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  FECHA_ACTUALIZACION  TIMESTAMP(6),
  FECHA_SINCRONIZACION TIMESTAMP(6)
);


CREATE TABLE DIS_CONTINGUT
(
  ID                   NUMBER(19)               NOT NULL,
  NOM                  VARCHAR2(1024)           NOT NULL,
  TIPUS                NUMBER(10)               NOT NULL,
  PARE_ID              NUMBER(19),
  ESBORRAT             NUMBER(10),
  ARXIU_UUID           VARCHAR2(36),
  ARXIU_DATA_ACT       TIMESTAMP(6),
  CONTMOV_ID           NUMBER(19),
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64),
  VERSION              NUMBER(19)               NOT NULL
);


CREATE TABLE DIS_CONT_MOV
(
  ID                   NUMBER(19)               NOT NULL,
  CONTINGUT_ID         NUMBER(19)               NOT NULL,
  ORIGEN_ID            NUMBER(19),
  DESTI_ID             NUMBER(19)               NOT NULL,
  REMITENT_CODI        VARCHAR2(64),
  COMENTARI            VARCHAR2(256),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);


CREATE TABLE DIS_CONT_MOV_EMAIL 
(
  ID 					NUMBER(19) 				NOT NULL, 
  DESTINATARI_CODI		VARCHAR2(64) 			NOT NULL,
  DESTINATARI_EMAIL		VARCHAR2(256) 			NOT NULL,
  ENVIAMENT_AGRUPAT		NUMBER(1)				NOT NULL,
  BUSTIA_ID 			NUMBER(19) 				NOT NULL,
  CONTINGUT_MOVIMENT_ID NUMBER(19) 				NOT NULL, 
  CONTINGUT_ID 			NUMBER(19) 				NOT NULL,
  UNITAT_ORGANITZATIVA 	VARCHAR2(256),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDDATE     	TIMESTAMP(6),
  CREATEDBY_CODI       	VARCHAR2(64),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64)
);
  

CREATE TABLE DIS_CONT_LOG
(
  ID                   NUMBER(19)               NOT NULL,
  TIPUS                NUMBER(10)               NOT NULL,
  CONTINGUT_ID         NUMBER(19)               NOT NULL,
  PARE_ID              NUMBER(19),
  CONTMOV_ID           NUMBER(19),
  OBJECTE_ID           VARCHAR2(256),
  OBJECTE_LOG_TIPUS    NUMBER(10),
  OBJECTE_TIPUS        NUMBER(10),
  PARAM1               VARCHAR2(256),
  PARAM2               VARCHAR2(256),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);


CREATE TABLE DIS_BUSTIA
(
  ID           NUMBER(19)                       NOT NULL,
  UNITAT_CODI  VARCHAR2(9)                      NOT NULL,
  PER_DEFECTE  NUMBER(1),
  ACTIVA       NUMBER(1),
  UNITAT_ID    NUMBER(19)
);


CREATE TABLE DIS_REGISTRE
(
  ID                     NUMBER(19)               NOT NULL,
  TIPUS                  VARCHAR2(1)              NOT NULL,
  UNITAT_ADM             VARCHAR2(21)             NOT NULL,
  UNITAT_ADM_DESC        VARCHAR2(100),
  NUMERO                 VARCHAR2(100)            NOT NULL,
  DATA                   TIMESTAMP(6)             NOT NULL,
  IDENTIFICADOR          VARCHAR2(100)            NOT NULL,
  ENTITAT_CODI           VARCHAR2(21)             NOT NULL,
  ENTITAT_DESC           VARCHAR2(100),
  OFICINA_CODI           VARCHAR2(21)             NOT NULL,
  OFICINA_DESC           VARCHAR2(100),
  LLIBRE_CODI            VARCHAR2(4)              NOT NULL,
  LLIBRE_DESC            VARCHAR2(100),
  EXTRACTE               VARCHAR2(240),
  ASSUMPTE_TIPUS_CODI    VARCHAR2(16)             NOT NULL,
  ASSUMPTE_TIPUS_DESC    VARCHAR2(100),
  ASSUMPTE_CODI          VARCHAR2(16),
  ASSUMPTE_DESC          VARCHAR2(100),
  PROCEDIMENT_CODI       VARCHAR2(64),
  REFERENCIA             VARCHAR2(16),
  EXPEDIENT_NUM          VARCHAR2(80),
  NUM_ORIG 			     VARCHAR2(80),
  IDIOMA_CODI            VARCHAR2(2)              NOT NULL,
  IDIOMA_DESC            VARCHAR2(100),
  TRANSPORT_TIPUS_CODI   VARCHAR2(2),
  TRANSPORT_TIPUS_DESC   VARCHAR2(100),
  TRANSPORT_NUM          VARCHAR2(20),
  USUARI_CODI            VARCHAR2(20),
  USUARI_NOM             VARCHAR2(80),
  USUARI_CONTACTE        VARCHAR2(160),
  APLICACIO_CODI         VARCHAR2(20),
  APLICACIO_VERSIO       VARCHAR2(15),
  DOCFIS_CODI            VARCHAR2(1),
  DOCFIS_DESC            VARCHAR2(100),
  OBSERVACIONS           VARCHAR2(50),
  EXPOSA                 VARCHAR2(4000),
  SOLICITA               VARCHAR2(4000),
  MOTIU_REBUIG           VARCHAR2(4000),
  PROCES_DATA            TIMESTAMP(6),
  PROCES_ESTAT           VARCHAR2(64)             NOT NULL,
  PROCES_INTENTS         NUMBER(10),
  PROCES_ERROR           VARCHAR2(1024),
  REGLA_ID               NUMBER(19),
  CREATEDDATE            TIMESTAMP(6),
  CREATEDBY_CODI         VARCHAR2(256),
  LASTMODIFIEDDATE       TIMESTAMP(6),
  LASTMODIFIEDBY_CODI    VARCHAR2(256),
  PROCES_ESTAT_SISTRA    VARCHAR2(16 CHAR),
  SISTRA_ID_TRAM 	     VARCHAR2(20 CHAR),
  SISTRA_ID_PROC 	     VARCHAR2(100 CHAR),
  DATA_ORIG              TIMESTAMP(6),
  OFICINA_ORIG_CODI      VARCHAR2(21),
  OFICINA_ORIG_DESC      VARCHAR2(100),
  JUSTIFICANT_ARXIU_UUID VARCHAR2(100),
  LLEGIDA                NUMBER(1),
  EXPEDIENT_ARXIU_UUID   VARCHAR2(100),
  DATA_TANCAMENT		 TIMESTAMP(6),
  ARXIU_TANCAT			 NUMBER(1) 	DEFAULT 0	NOT NULL,
  ARXIU_TANCAT_ERROR	 NUMBER(1) 	DEFAULT 0	NOT NULL,
  NUMERO_COPIA			 NUMBER(19)	DEFAULT 0	NOT NULL,
  BACK_PENDENT_DATA 	TIMESTAMP(6),
  BACK_REBUDA_DATA 	TIMESTAMP(6),
  BACK_PROCES_REBUTJ_ERROR_DATA 	TIMESTAMP(6),
  BACK_OBSERVACIONS 	VARCHAR2(1024),
  BACK_RETRY_ENVIAR_DATA TIMESTAMP(6)
);


CREATE TABLE DIS_REGISTRE_ANNEX
(
  ID                   NUMBER(19)               NOT NULL,
  TITOL                VARCHAR2(200)            NOT NULL,
  FITXER_NOM           VARCHAR2(256)            NOT NULL,
  FITXER_TAMANY        NUMBER(10)               NOT NULL,
  FITXER_MIME          VARCHAR2(100),
  FITXER_ARXIU_UUID    VARCHAR2(100),
  DATA_CAPTURA         TIMESTAMP(6)             NOT NULL,
  LOCALITZACIO         VARCHAR2(80),
  ORIGEN_CIUADM        VARCHAR2(1)              NOT NULL,
  NTI_TIPUS_DOC        VARCHAR2(4)              NOT NULL,
  SICRES_TIPUS_DOC     VARCHAR2(2)              NOT NULL,
  NTI_ELABORACIO_ESTAT VARCHAR2(4),
  OBSERVACIONS         VARCHAR2(50),
  FIRMA_MODE           NUMBER(10),
  FIRMA_FITXER_NOM     VARCHAR2(80),
  FIRMA_FITXER_TAMANY  NUMBER(10),
  FIRMA_FITXER_MIME    VARCHAR2(30),
  FIRMA_FITXER_ARXIU_UUID  VARCHAR2(100),
  FIRMA_CSV            VARCHAR2(50),
  TIMESTAMP            VARCHAR2(100),
  VALIDACIO_OCSP       VARCHAR2(100),
  REGISTRE_ID          NUMBER(19)               NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(256),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(256),
  GESDOC_DOC_ID 	   VARCHAR2(50)
);


CREATE TABLE DIS_REGISTRE_ANNEX_FIRMA
(
  ID                   NUMBER(19)               NOT NULL,
  TIPUS		           VARCHAR2(30),
  PERFIL    	       VARCHAR2(30),
  FITXER_NOM           VARCHAR2(256),
  TIPUS_MIME           VARCHAR2(30),
  CSV_REGULACIO		   VARCHAR2(640),
  ANNEX_ID             NUMBER(19)               NOT NULL,
  AUTOFIRMA            NUMBER(1),
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(256),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(256),
  GESDOC_FIR_ID 	   VARCHAR2(50)
);


CREATE TABLE DIS_REGISTRE_INTER
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  ADRESA               VARCHAR2(640),
  CANAL_PREF           VARCHAR2(8),
  CODI_POSTAL          VARCHAR2(20),
  DOC_NUM              VARCHAR2(68),
  DOC_TIPUS            VARCHAR2(4),
  EMAIL                VARCHAR2(640),
  EMAIL_HAB            VARCHAR2(640),
  LLINATGE1            VARCHAR2(120),
  LLINATGE2            VARCHAR2(120),
  MUNICIPI             VARCHAR2(100),
  NOM                  VARCHAR2(120),
  OBSERVACIONS         VARCHAR2(640),
  PAIS                 VARCHAR2(16),
  PROVINCIA            VARCHAR2(100),
  RAO_SOCIAL           VARCHAR2(320),
  TELEFON              VARCHAR2(80),
  TIPUS                VARCHAR2(4)              NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(256),
  LASTMODIFIEDBY_CODI  VARCHAR2(256),
  REGISTRE_ID          NUMBER(19)               NOT NULL,
  REPRESENTANT_ID      NUMBER(19),
  REPRESENTAT_ID       NUMBER(19)
);


CREATE TABLE DIS_REGLA
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  ACTIVA               NUMBER(1),
  ASSUMPTE_CODI        VARCHAR2(16),
  DESCRIPCIO           VARCHAR2(1024),
  NOM                  VARCHAR2(256)            NOT NULL,
  ORDRE                NUMBER(10)               NOT NULL,
  TIPUS                VARCHAR2(32)             NOT NULL,
  UNITAT_CODI          VARCHAR2(9),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(256),
  LASTMODIFIEDBY_CODI  VARCHAR2(256),
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  CONTRASENYA          VARCHAR2(64),
  TIPUS_BACKOFFICE     VARCHAR2(255),
  INTENTS              NUMBER(10),
  TEMPS_ENTRE_INTENTS  NUMBER(10),
  URL                  VARCHAR2(256),
  USUARI               VARCHAR2(64),
  BUSTIA_ID            NUMBER(19),
  METAEXPEDIENT_ID     NUMBER(19),
  UNITAT_ID 		   NUMBER(19),
  PROCEDIMENT_CODI     VARCHAR2(64)
);


CREATE TABLE DIS_ACL_CLASS
(
  ID     NUMBER(19)                             NOT NULL,
  CLASS  VARCHAR2(100)                          NOT NULL
);


CREATE TABLE DIS_ACL_SID
(
  ID         NUMBER(19)                         NOT NULL,
  PRINCIPAL  NUMBER(1)                          NOT NULL,
  SID        VARCHAR2(100)                      NOT NULL
);


CREATE TABLE DIS_ACL_ENTRY
(
  ID                   NUMBER(19)               NOT NULL,
  ACL_OBJECT_IDENTITY  NUMBER(19)               NOT NULL,
  ACE_ORDER            NUMBER(19)               NOT NULL,
  SID                  NUMBER(19)               NOT NULL,
  MASK                 NUMBER(19)               NOT NULL,
  GRANTING             NUMBER(1)                NOT NULL,
  AUDIT_SUCCESS        NUMBER(1)                NOT NULL,
  AUDIT_FAILURE        NUMBER(1)                NOT NULL
);


CREATE TABLE DIS_ACL_OBJECT_IDENTITY
(
  ID                  NUMBER(19)                NOT NULL,
  OBJECT_ID_CLASS     NUMBER(19)                NOT NULL,
  OBJECT_ID_IDENTITY  NUMBER(19)                NOT NULL,
  PARENT_OBJECT       NUMBER(19),
  OWNER_SID           NUMBER(19)                NOT NULL,
  ENTRIES_INHERITING  NUMBER(1)                 NOT NULL
);


CREATE TABLE DIS_CONT_COMMENT
(
  ID                   NUMBER(19)               NOT NULL,
  CONTINGUT_ID         NUMBER(19) 				NOT NULL,
  TEXT				   VARCHAR2 (1024),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64),
  LASTMODIFIEDBY_CODI  VARCHAR2(64)
);


CREATE TABLE DIS_UNITAT_ORGANITZATIVA (
  ID           				NUMBER(19)          NOT NULL,
  CODI 						VARCHAR2(9) 		NOT NULL,
  DENOMINACIO 				VARCHAR2(300) 		NOT NULL,
  NIF_CIF 					VARCHAR2(9),
  CODI_UNITAT_SUPERIOR 		VARCHAR2(9),
  CODI_UNITAT_ARREL 		VARCHAR2(9),
  DATA_CREACIO_OFICIAL 		TIMESTAMP(6),
  DATA_SUPRESSIO_OFICIAL 	TIMESTAMP(6),
  DATA_EXTINCIO_FUNCIONAL 	TIMESTAMP(6),
  DATA_ANULACIO 	 		TIMESTAMP(6),
  ESTAT 					VARCHAR2(1),
  CODI_PAIS 				VARCHAR2(3),
  CODI_COMUNITAT 			VARCHAR2(2),
  CODI_PROVINCIA 			VARCHAR2(2),
  CODI_POSTAL 				VARCHAR2(5),
  NOM_LOCALITAT 			VARCHAR2(50),
  LOCALITAT 				VARCHAR2(40),
  ADRESSA 					VARCHAR2(70),
  TIPUS_VIA 	 			NUMBER(19),
  NOM_VIA 					VARCHAR2(200),
  NUM_VIA 					VARCHAR2(100),
  TIPUS_TRANSICIO 		    NUMBER(1),

  CREATEDDATE          		TIMESTAMP(6),
  CREATEDBY_CODI       		VARCHAR2(256),
  LASTMODIFIEDDATE     		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(256),
  CODI_DIR3_ENTITAT		 	VARCHAR2(9)
);

CREATE TABLE DIS_UO_SINC_REL (
  ANTIGA_UO         			NUMBER(19)          NOT NULL,
  NOVA_UO           			NUMBER(19)          NOT NULL
);


