
CREATE TABLE DIS_ALERTA
(
  ID                   	NUMBER(19)             	NOT NULL,
  TEXT                 	VARCHAR2(256 CHAR)           NOT NULL,
  ERROR                	VARCHAR2(2048 CHAR),
  LLEGIDA              	NUMBER(1)               NOT NULL,
  CONTINGUT_ID         	NUMBER(19),
  CREATEDBY_CODI       	VARCHAR2(64 CHAR),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64 CHAR),
  LASTMODIFIEDDATE     	TIMESTAMP(6)
);

CREATE TABLE DIS_USUARI
(
  CODI          		VARCHAR2(64 CHAR)            NOT NULL, 
  INICIALITZAT  		NUMBER(1),
  NIF           		VARCHAR2(9 CHAR),
  NOM           		VARCHAR2(200 CHAR),
  EMAIL         		VARCHAR2(200 CHAR),
  EMAIL_ALTERNATIU 		VARCHAR2(200 CHAR),  
  IDIOMA				VARCHAR2(2 CHAR) DEFAULT 'CA' NOT NULL,
  REBRE_EMAILS  		NUMBER(1,0),
  EMAILS_AGRUPATS		NUMBER(1,0),
  VERSION       		NUMBER(19)              NOT NULL,
  ROL_ACTUAL			VARCHAR2(64 CHAR)
);


CREATE TABLE DIS_ENTITAT
(
  ID                   NUMBER(19)               NOT NULL,
  CODI                 VARCHAR2(64 CHAR)             NOT NULL,
  NOM                  VARCHAR2(256 CHAR)            NOT NULL,
  DESCRIPCIO           VARCHAR2(1024 CHAR),
  CIF                  VARCHAR2(9 CHAR)              NOT NULL,
  CODI_DIR3            VARCHAR2(9 CHAR)              NOT NULL,
  ACTIVA               NUMBER(1),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  FECHA_ACTUALIZACION  TIMESTAMP(6),
  FECHA_SINCRONIZACION TIMESTAMP(6),
  COLOR_FONS           VARCHAR2(32 CHAR),
  COLOR_LLETRA         VARCHAR2(32 CHAR)
);


CREATE TABLE DIS_CONTINGUT
(
  ID                   NUMBER(19)               NOT NULL,
  NOM                  VARCHAR2(1024 CHAR)           NOT NULL,
  TIPUS                VARCHAR2(8 CHAR)              NOT NULL,
  PARE_ID              NUMBER(19),
  ESBORRAT             NUMBER(10),
  ARXIU_UUID           VARCHAR2(36 CHAR),
  ARXIU_DATA_ACT       TIMESTAMP(6),
  CONTMOV_ID           NUMBER(19),
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  VERSION              NUMBER(19)               NOT NULL
);


CREATE TABLE DIS_CONT_MOV
(
  ID                   NUMBER(19)               NOT NULL,
  CONTINGUT_ID         NUMBER(19)               NOT NULL,
  REMITENT_CODI        VARCHAR2(64 CHAR),
  COMENTARI            VARCHAR2(3940 CHAR),
  COMENTARI_DESTINS    VARCHAR2(256 CHAR)
  PER_CONEIXEMENT 	   NUMBER(1)
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  ORIGEN_NOM VARCHAR2(1024 CHAR),
  DESTI_NOM VARCHAR2(1024 CHAR),
  NUM_DUPLICAT         NUMBER(10)
);


CREATE TABLE DIS_CONT_MOV_EMAIL 
(
  ID 					NUMBER(19) 				NOT NULL, 
  DESTINATARI_CODI		VARCHAR2(64 CHAR) 			NOT NULL,
  DESTINATARI_EMAIL		VARCHAR2(256 CHAR) 			NOT NULL,
  ENVIAMENT_AGRUPAT		NUMBER(1)				NOT NULL,
  BUSTIA_ID 			NUMBER(19) 				NOT NULL,
  CONTINGUT_MOVIMENT_ID NUMBER(19) 				NOT NULL, 
  CONTINGUT_ID 			NUMBER(19) 				NOT NULL,
  UNITAT_ORGANITZATIVA 	VARCHAR2(256 CHAR),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDDATE     	TIMESTAMP(6),
  CREATEDBY_CODI       	VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64 CHAR)
);
  

CREATE TABLE DIS_CONT_LOG
(
  ID                   NUMBER(19)               NOT NULL,
  TIPUS                VARCHAR2(30 CHAR)             NOT NULL,
  CONTINGUT_ID         NUMBER(19)               NOT NULL,
  PARE_ID              NUMBER(19),
  CONTMOV_ID           NUMBER(19),
  OBJECTE_ID           VARCHAR2(256 CHAR),
  OBJECTE_LOG_TIPUS    VARCHAR2(30 CHAR),
  OBJECTE_TIPUS        VARCHAR2(12 CHAR),
  PARAM1               VARCHAR2(256 CHAR),
  PARAM2               VARCHAR2(256 CHAR),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE DIS_BUSTIA
(
  ID           NUMBER(19)                       NOT NULL,
  PER_DEFECTE  NUMBER(1),
  ACTIVA       NUMBER(1),
  UNITAT_ID    NUMBER(19)
);


CREATE TABLE DIS_REGISTRE
(
  ID                     NUMBER(19)               NOT NULL,
  TIPUS                  VARCHAR2(1 CHAR)              NOT NULL,
  UNITAT_ADM             VARCHAR2(21 CHAR)             NOT NULL,
  UNITAT_ADM_DESC        VARCHAR2(300 CHAR),
  NUMERO                 VARCHAR2(255 CHAR)            NOT NULL,
  DATA                   TIMESTAMP(6)             NOT NULL,
  IDENTIFICADOR          VARCHAR2(100 CHAR)            NOT NULL,
  ENTITAT_CODI           VARCHAR2(255 CHAR)             NOT NULL,
  ENTITAT_DESC           VARCHAR2(255 CHAR),
  OFICINA_CODI           VARCHAR2(21 CHAR)             NOT NULL,
  OFICINA_DESC           VARCHAR2(300 CHAR),
  LLIBRE_CODI            VARCHAR2(4 CHAR)              NOT NULL,
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
  IDIOMA_CODI            VARCHAR2(19 CHAR)              NOT NULL,
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
  EXPOSA                 CLOB,
  SOLICITA               CLOB,
  MOTIU_REBUIG           CLOB,
  PROCES_DATA            TIMESTAMP(6),
  PROCES_ESTAT           VARCHAR2(64 CHAR)             NOT NULL,
  PROCES_INTENTS         NUMBER(10),
  PROCES_ERROR           CLOB,
  REGLA_ID               NUMBER(19),
  CREATEDDATE            TIMESTAMP(6),
  CREATEDBY_CODI         VARCHAR2(256 CHAR),
  LASTMODIFIEDDATE       TIMESTAMP(6),
  LASTMODIFIEDBY_CODI    VARCHAR2(256 CHAR),
  PROCES_ESTAT_SISTRA    VARCHAR2(16 CHAR),
  SISTRA_ID_TRAM 	     VARCHAR2(20 CHAR),
  SISTRA_ID_PROC 	     VARCHAR2(100 CHAR),
  DATA_ORIG              TIMESTAMP(6),
  OFICINA_ORIG_CODI      VARCHAR2(21 CHAR),
  OFICINA_ORIG_DESC      VARCHAR2(100 CHAR),
  JUSTIFICANT_ARXIU_UUID VARCHAR2(256 CHAR),
  LLEGIDA                NUMBER(1),
  EXPEDIENT_ARXIU_UUID   VARCHAR2(100 CHAR),
  DATA_TANCAMENT		 TIMESTAMP(6),
  ARXIU_TANCAT			 NUMBER(1) 	DEFAULT 0	NOT NULL,
  ARXIU_TANCAT_ERROR	 NUMBER(1) 	DEFAULT 0	NOT NULL,
  NUMERO_COPIA			 NUMBER(19)	DEFAULT 0	NOT NULL,
  BACK_PENDENT_DATA 	TIMESTAMP(6),
  BACK_REBUDA_DATA 	TIMESTAMP(6),
  BACK_PROCES_REBUTJ_ERROR_DATA 	TIMESTAMP(6),
  BACK_OBSERVACIONS 	VARCHAR2(4000 CHAR),
  BACK_RETRY_ENVIAR_DATA TIMESTAMP(6),
  BACK_CODI VARCHAR2(20 CHAR),
  PRESENCIAL NUMBER(1),
  JUSTIFICANT_DESCARREGAT NUMBER(1) DEFAULT 0,
  JUSTIFICANT_ID NUMBER(19),
  ENVIAT_PER_EMAIL NUMBER(1) DEFAULT 0,
  PENDENT	 NUMBER(1) 	DEFAULT 1	NOT NULL,
  AGAFAT_PER 	VARCHAR2(64 CHAR),
  REACTIVAT             NUMBER(1) DEFAULT 0,
  ANNEXOS_ESTAT_ESBORRANY NUMBER(8,0) DEFAULT 0
);


CREATE TABLE DIS_REGISTRE_ANNEX
(
  ID                   NUMBER(19)               NOT NULL,
  TITOL                VARCHAR2(200 CHAR)            NOT NULL,
  FITXER_NOM           VARCHAR2(256 CHAR)            NOT NULL,
  FITXER_TAMANY        NUMBER(10)               NOT NULL,
  FITXER_MIME          VARCHAR2(100 CHAR),
  FITXER_ARXIU_UUID    VARCHAR2(256 CHAR),
  DATA_CAPTURA         TIMESTAMP(6)             NOT NULL,
  LOCALITZACIO         VARCHAR2(80 CHAR),
  ORIGEN_CIUADM        VARCHAR2(1 CHAR)              NOT NULL,
  NTI_TIPUS_DOC        VARCHAR2(4 CHAR)              NOT NULL,
  SICRES_TIPUS_DOC     VARCHAR2(2 CHAR),
  NTI_ELABORACIO_ESTAT VARCHAR2(4 CHAR),
  OBSERVACIONS         VARCHAR2(50 CHAR),
  FIRMA_MODE           NUMBER(10),
  FIRMA_FITXER_NOM     VARCHAR2(80 CHAR),
  FIRMA_FITXER_TAMANY  NUMBER(10),
  FIRMA_FITXER_MIME    VARCHAR2(30 CHAR),
  FIRMA_FITXER_ARXIU_UUID  VARCHAR2(100 CHAR),
  FIRMA_CSV            VARCHAR2(256 CHAR),
  TIMESTAMP            VARCHAR2(100 CHAR),
  VALIDACIO_OCSP       VARCHAR2(100 CHAR),
  REGISTRE_ID          NUMBER(19)               NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  GESDOC_DOC_ID 	   VARCHAR2(50 CHAR),
  SIGN_DETALLS_DESCARREGAT NUMBER(1) DEFAULT 0,
  META_DADES VARCHAR2(4000 CHAR),
  SOBREESCRIURE        NUMBER(1) DEFAULT 0,
  VAL_FIRMA_ESTAT     VARCHAR2(64 CHAR),
  VAL_FIRMA_ERROR     VARCHAR2(1000 CHAR),
  ARXIU_ESTAT         VARCHAR2(20 CHAR)
);


CREATE TABLE DIS_REGISTRE_ANNEX_FIRMA
(
  ID                   NUMBER(19)               NOT NULL,
  TIPUS		           VARCHAR2(30 CHAR),
  PERFIL    	       VARCHAR2(30 CHAR),
  FITXER_NOM           VARCHAR2(256 CHAR),
  TIPUS_MIME           VARCHAR2(30 CHAR),
  CSV_REGULACIO		   VARCHAR2(640 CHAR),
  ANNEX_ID             NUMBER(19)               NOT NULL,
  AUTOFIRMA            NUMBER(1),
  CREATEDDATE          TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  GESDOC_FIR_ID 	   VARCHAR2(50 CHAR)
);

CREATE TABLE DIS_REGISTRE_FIRMA_DETALL
(
	ID NUMBER(19),
    DATA TIMESTAMP,
    RESPONSABLE_NIF VARCHAR2(30 CHAR),
    RESPONSABLE_NOM VARCHAR2(256 CHAR),
    EMISSOR_CERTIFICAT VARCHAR2(2000 CHAR),
    FIRMA_ID NUMBER(19),
    CREATEDBY_CODI       	VARCHAR2(64 CHAR),
    CREATEDDATE          	TIMESTAMP(6),
    LASTMODIFIEDBY_CODI  	VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE     	TIMESTAMP(6)
);


CREATE TABLE DIS_REGISTRE_INTER
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
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
  TIPUS                VARCHAR2(19 CHAR)              NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  REGISTRE_ID          NUMBER(19)               NOT NULL,
  REPRESENTANT_ID      NUMBER(19),
  REPRESENTAT_ID       NUMBER(19),
  CODI_DIRE 			VARCHAR2(64 CHAR)
);


CREATE TABLE DIS_REGLA
(
  ID                   NUMBER(19)               NOT NULL,
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  ACTIVA               NUMBER(1),
  ASSUMPTE_CODI        VARCHAR2(16 CHAR),
  DESCRIPCIO           VARCHAR2(1024 CHAR),
  NOM                  VARCHAR2(256 CHAR)            NOT NULL,
  ORDRE                NUMBER(10)               NOT NULL,
  TIPUS                VARCHAR2(32 CHAR)             NOT NULL,
  UNITAT_CODI          VARCHAR2(9 CHAR),
  VERSION              NUMBER(19)               NOT NULL,
  CREATEDBY_CODI       VARCHAR2(256 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(256 CHAR),
  ENTITAT_ID           NUMBER(19)               NOT NULL,
  CONTRASENYA          VARCHAR2(64 CHAR),
  TIPUS_BACKOFFICE     VARCHAR2(255 CHAR),
  INTENTS              NUMBER(10),
  TEMPS_ENTRE_INTENTS  NUMBER(10),
  URL                  VARCHAR2(256 CHAR),
  USUARI               VARCHAR2(64 CHAR),
  BUSTIA_ID            NUMBER(19),
  METAEXPEDIENT_ID     NUMBER(19),
  UNITAT_ID 		   NUMBER(19),
  PROCEDIMENT_CODI     VARCHAR2(1024 CHAR),
  BACKOFFICE_CODI     VARCHAR2(20 CHAR),
  BUSTIA_FILTRE_ID NUMBER(19),
  BACKOFFICE_DESTI_ID NUMBER(19),
  UNITAT_DESTI_ID NUMBER(19)
);


CREATE TABLE DIS_ACL_CLASS
(
  ID     NUMBER(19)                             NOT NULL,
  CLASS  VARCHAR2(100 CHAR)                          NOT NULL
);


CREATE TABLE DIS_ACL_SID
(
  ID         NUMBER(19)                         NOT NULL,
  PRINCIPAL  NUMBER(1)                          NOT NULL,
  SID        VARCHAR2(100 CHAR)                      NOT NULL
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
  TEXT				   VARCHAR2 (4000 CHAR),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR)
);


CREATE TABLE DIS_UNITAT_ORGANITZATIVA (
  ID           				NUMBER(19)          NOT NULL,
  CODI 						VARCHAR2(9 CHAR) 		NOT NULL,
  DENOMINACIO 				VARCHAR2(300 CHAR) 		NOT NULL,
  NIF_CIF 					VARCHAR2(9 CHAR),
  CODI_UNITAT_SUPERIOR 		VARCHAR2(9 CHAR),
  CODI_UNITAT_ARREL 		VARCHAR2(9 CHAR),
  DATA_CREACIO_OFICIAL 		TIMESTAMP(6),
  DATA_SUPRESSIO_OFICIAL 	TIMESTAMP(6),
  DATA_EXTINCIO_FUNCIONAL 	TIMESTAMP(6),
  DATA_ANULACIO 	 		TIMESTAMP(6),
  ESTAT 					VARCHAR2(1 CHAR),
  CODI_PAIS 				VARCHAR2(3 CHAR),
  CODI_COMUNITAT 			VARCHAR2(2 CHAR),
  CODI_PROVINCIA 			VARCHAR2(2 CHAR),
  CODI_POSTAL 				VARCHAR2(5 CHAR),
  NOM_LOCALITAT 			VARCHAR2(50 CHAR),
  LOCALITAT 				VARCHAR2(40 CHAR),
  ADRESSA 					VARCHAR2(70 CHAR),
  TIPUS_VIA 	 			NUMBER(19),
  NOM_VIA 					VARCHAR2(200 CHAR),
  NUM_VIA 					VARCHAR2(100 CHAR),
  TIPUS_TRANSICIO 		    VARCHAR2(12 CHAR),

  CREATEDDATE          		TIMESTAMP(6),
  CREATEDBY_CODI       		VARCHAR2(256 CHAR),
  LASTMODIFIEDDATE     		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(256 CHAR),
  CODI_DIR3_ENTITAT		 	VARCHAR2(9 CHAR)
);

CREATE TABLE DIS_UO_SINC_REL (
  ANTIGA_UO         			NUMBER(19)          NOT NULL,
  NOVA_UO           			NUMBER(19)          NOT NULL
);


CREATE TABLE DIS_AVIS
(
  ID                   NUMBER(19)               NOT NULL,
  ASSUMPTE             VARCHAR2(256 CHAR)            NOT NULL,
  MISSATGE             VARCHAR2(2048 CHAR)           NOT NULL,
  DATA_INICI           TIMESTAMP(6)             NOT NULL,
  DATA_FINAL           TIMESTAMP(6)             NOT NULL,
  ACTIU                NUMBER(1)                NOT NULL,
  AVIS_NIVELL         VARCHAR2(10 CHAR)             NOT NULL,
  CREATEDBY_CODI       VARCHAR2(64 CHAR),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  VARCHAR2(64 CHAR),
  LASTMODIFIEDDATE     TIMESTAMP(6)
);


CREATE TABLE DIS_CONT_LOG_PARAM
(
    ID NUMBER(19) NOT NULL,
    CONT_LOG_ID NUMBER(19) NOT NULL,
    NUMERO NUMBER(19) NOT NULL,
    VALOR VARCHAR2(256 CHAR) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP(6)
);

CREATE TABLE DIS_BACKOFFICE
(
    ID NUMBER(19) NOT NULL,
    CODI VARCHAR2(20 CHAR) NOT NULL,
    NOM VARCHAR2(64 CHAR) NOT NULL,
    URL VARCHAR2(256 CHAR) NOT NULL,
    USUARI VARCHAR2(64 CHAR),
    CONTRASENYA VARCHAR2(64 CHAR),
    INTENTS NUMBER(10,0),
    TEMPS_ENTRE_INTENTS NUMBER(10,0),
    ENTITAT_ID NUMBER(19) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP(6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP(6)
);

CREATE TABLE DIS_BUSTIA_FAVORIT
(
    ID                  NUMBER(19,0)        NOT NULL, 
    BUSTIA_ID           NUMBER(19,0)        NOT NULL, 
    USUARI_CODI         VARCHAR2(64 CHAR)   NOT NULL, 
	CREATEDBY_CODI      VARCHAR2(64 CHAR), 
	CREATEDDATE         TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE    TIMESTAMP (6)
);

CREATE TABLE DIS_CONFIG
(
    KEY                  VARCHAR2(256 CHAR)     NOT NULL,
    VALUE                VARCHAR2(2048 CHAR),
    DESCRIPTION          VARCHAR2(2048 CHAR),
    GROUP_CODE           VARCHAR2(128 CHAR)     NOT NULL,
    POSITION             NUMBER(3)              DEFAULT 0 NOT NULL,
    JBOSS_PROPERTY       NUMBER(1)              DEFAULT 0 NOT NULL,
    TYPE_CODE            VARCHAR2(128 CHAR)     DEFAULT 'TEXT',
    ENTITAT_CODI         VARCHAR2(64 CHAR),
    CONFIGURABLE         NUMBER(1)              DEFAULT 0,
    LASTMODIFIEDBY_CODI  VARCHAR2(64),
    LASTMODIFIEDDATE     TIMESTAMP(6)
);

CREATE TABLE DIS_CONFIG_GROUP
(
    CODE                 VARCHAR2(128 CHAR)     NOT NULL,
    PARENT_CODE          VARCHAR2(128 CHAR)     DEFAULT NULL,
    POSITION             NUMBER(3)              DEFAULT 0 NOT NULL,
    DESCRIPTION          VARCHAR2(512 CHAR)     NOT NULL
);

CREATE TABLE DIS_CONFIG_TYPE
(
    CODE                 VARCHAR2(128 CHAR)     NOT NULL,
    VALUE                VARCHAR2(2048 CHAR)   DEFAULT NULL
);

CREATE TABLE DIS_HIS_ANOTACIO
(
	ID						 NUMBER(19)	    NOT NULL,
    ENTITAT_ID               NUMBER(19)     NOT NULL,
    UNITAT_ID                NUMBER(19),
    TIPUS                    VARCHAR2(16 CHAR) NOT NULL,
    DATA                     TIMESTAMP(6)   NOT NULL,
    ANOTACIONS               NUMBER(19)	    NOT NULL,
    ANOTACIONS_TOTAL         NUMBER(19)	    NOT NULL,
    REENVIAMENTS             NUMBER(19)	    NOT NULL,
    EMAILS                   NUMBER(19)	    NOT NULL,
    JUSTIFICANTS             NUMBER(19)	    NOT NULL,
    ANNEXOS                  NUMBER(19)	    NOT NULL,
    BUSTIES                  NUMBER(19)	    NOT NULL,
    USUARIS                  NUMBER(19)	    NOT NULL
);

CREATE TABLE DIS_HIS_ESTAT
(
	ID						 NUMBER(19)	    NOT NULL,
    ENTITAT_ID               NUMBER(19)     NOT NULL,
    UNITAT_ID                NUMBER(19),
    TIPUS                    VARCHAR2(16 CHAR) NOT NULL,
    DATA                     TIMESTAMP(6)   NOT NULL,
    ESTAT                    VARCHAR2(64 CHAR) NOT NULL,
    CORRECTE                 NUMBER(19)	    NOT NULL,
    CORRECTE_TOTAL           NUMBER(19)	    NOT NULL,
    ERROR                    NUMBER(19)	    NOT NULL,
    ERROR_TOTAL              NUMBER(19)	    NOT NULL,
    TOTAL                    NUMBER(19)	    NOT NULL
);

CREATE TABLE DIS_HIS_BUSTIA
(
	ID						 NUMBER(19)	    NOT NULL,
    ENTITAT_ID               NUMBER(19)     NOT NULL,
    UNITAT_ID                NUMBER(19),
    TIPUS                    VARCHAR2(16 CHAR) NOT NULL,
    DATA                     TIMESTAMP(6)   NOT NULL,
    BUSTIA_ID                NUMBER(19)     NOT NULL,
    NOM                      VARCHAR2(1024 CHAR) NOT NULL,
    USUARIS                  NUMBER(19)	    NOT NULL,
    USUARIS_PERMIS           NUMBER(19)	    NOT NULL,
    USUARIS_ROL              NUMBER(19)	    NOT NULL
);

CREATE TABLE DIS_BUSTIA_DEFAULT
(
    ID                      NUMBER(19)             NOT NULL,
    ENTITAT                 NUMBER(19)             NOT NULL,
    BUSTIA                  NUMBER(19)             NOT NULL,
    USUARI  	            VARCHAR2(64 CHAR)      NOT NULL,
    CREATEDDATE             TIMESTAMP(6),
    LASTMODIFIEDDATE        TIMESTAMP(6)
);

CREATE TABLE DIS_MON_INT
(	
   	ID					NUMBER(19,0) 		NOT NULL,
	CODI 				VARCHAR2(64 CHAR) 	NOT NULL, 
	DATA 				TIMESTAMP (6), 
	DESCRIPCIO 			VARCHAR2(1024 CHAR), 
	TIPUS 				VARCHAR2(10 CHAR), 
	TEMPS_RESPOSTA		NUMBER(19,0), 
	ESTAT				VARCHAR2(5 CHAR),
	CODI_USUARI			VARCHAR2(64 CHAR),
    CODI_ENTITAT        VARCHAR2(64 CHAR),
	ERROR_DESCRIPCIO	VARCHAR2(1024 CHAR),
	EXCEPCIO_MSG		VARCHAR2(1024 CHAR),
	EXCEPCIO_STACKTRACE	VARCHAR2(2048 CHAR)
);

CREATE TABLE DIS_MON_INT_PARAM
(	
   	ID					NUMBER(19,0) 			NOT NULL, 
   	MON_INT_ID			NUMBER(19)				NOT NULL,
	NOM		 			VARCHAR2(64 CHAR) 		NOT NULL,
	DESCRIPCIO 			VARCHAR2(1024 CHAR)
);

CREATE TABLE DIS_PROCEDIMENT 
(
  ID						NUMBER(19)	    	NOT NULL,
  CODI          			VARCHAR2(64)		NOT NULL,
  NOM						VARCHAR2(256),
  CODISIA					VARCHAR2(64),
  ID_UNITAT_ORGANITZATIVA	NUMBER(19),
  ENTITAT					NUMBER(19),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);