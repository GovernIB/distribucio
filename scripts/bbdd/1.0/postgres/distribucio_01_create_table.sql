
CREATE TABLE DIS_ALERTA
(
  ID                   BIGSERIAL                          NOT NULL,
  TEXT                 character varying(256)          NOT NULL,
  ERROR                character varying(2048),
  LLEGIDA              boolean                         NOT NULL,
  CONTINGUT_ID         bigint,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);

CREATE TABLE DIS_USUARI
(
  CODI          		character varying(64)       NOT NULL,
  INICIALITZAT  		boolean,
  NIF           		character varying(9),
  NOM           		character varying(200),
  EMAIL         		character varying(200),
  EMAIL_ALTERNATIU 		character varying(200),  
  IDIOMA 				character varying(2)  DEFAULT 'CA' NOT NULL,
  REBRE_EMAILS  		boolean,
  EMAILS_AGRUPATS		boolean,
  VERSION       		bigint                      NOT NULL,
  ROL_ACTUAL            character varying(64)
);


CREATE TABLE DIS_ENTITAT
(
  ID                   BIGSERIAL                   NOT NULL,
  CODI                 character varying(64)    NOT NULL,
  NOM                  character varying(256)   NOT NULL,
  DESCRIPCIO           character varying(1024),
  CIF                  character varying(9)     NOT NULL,
  CODI_DIR3	           character varying(9)     NOT NULL,
  ACTIVA               boolean,
  VERSION              bigint                   NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone,
  FECHA_ACTUALIZACION  TIMESTAMP WITHOUT TIME ZONE,
  FECHA_SINCRONIZACION TIMESTAMP WITHOUT TIME ZONE,
  COLOR_FONS           character varying(32),
  COLOR_FONS           character varying(32)
);

CREATE TABLE DIS_CONTINGUT
(
  ID                   BIGSERIAL                   NOT NULL,
  NOM                  character varying(1024)  NOT NULL,
  TIPUS                character varying(8)       	NOT NULL,
  PARE_ID              bigint,
  ESBORRAT             integer,
  ARXIU_UUID           character varying(36),
  ARXIU_DATA_ACT       timestamp without time zone,
  CONTMOV_ID           bigint,
  ENTITAT_ID           bigint                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  VERSION              bigint                   NOT NULL
);


CREATE TABLE DIS_CONT_MOV
(
  ID                   BIGSERIAL                   NOT NULL,
  CONTINGUT_ID         bigint                   NOT NULL,
  ORIGEN_ID            bigint,
  DESTI_ID             bigint                   NOT NULL,
  REMITENT_CODI        character varying(64),
  COMENTARI            character varying(3940),
  COMENTARI_DESTINS    character varying(256),
  PER_CONEIXEMENT 	   boolean,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64),
  ORIGEN_NOM character varying(1024),
  DESTI_NOM character varying(1024),
  NUM_DUPLICAT         integer
);


CREATE TABLE DIS_CONT_MOV_EMAIL 
(
  ID 					BIGSERIAL 					NOT NULL, 
  DESTINATARI_CODI		character varying(64) 	NOT NULL, 
  DESTINATARI_EMAIL		character varying(256) 	NOT NULL,
  ENVIAMENT_AGRUPAT		boolean					NOT NULL,
  BUSTIA_ID 			BIGINT 					NOT NULL,
  CONTINGUT_MOVIMENT_ID BIGINT 					NOT NULL, 
  CONTINGUT_ID 			BIGINT 					NOT NULL,
  UNITAT_ORGANITZATIVA 	character varying(256),
  CREATEDDATE          	timestamp without time zone,
  LASTMODIFIEDDATE     	timestamp without time zone,
  CREATEDBY_CODI       	character varying(64),
  LASTMODIFIEDBY_CODI  	character varying(64)
);


CREATE TABLE DIS_CONT_LOG
(
  ID                   BIGSERIAL                   NOT NULL,
  TIPUS                character varying(30)       NOT NULL,
  CONTINGUT_ID         bigint                   NOT NULL,
  PARE_ID              bigint,
  CONTMOV_ID           bigint,
  OBJECTE_ID           character varying(256),
  OBJECTE_LOG_TIPUS    character varying(30),
  OBJECTE_TIPUS        character varying(12),
  PARAM1               character varying(256),
  PARAM2               character varying(256),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64)
);


CREATE TABLE DIS_BUSTIA
(
  ID           BIGSERIAL                           NOT NULL,
  PER_DEFECTE  boolean,
  ACTIVA       boolean,
  UNITAT_ID    BIGINT
);


CREATE TABLE DIS_REGISTRE
(
  ID                   BIGSERIAL                   NOT NULL,
  TIPUS                character varying(1)     NOT NULL,
  UNITAT_ADM           character varying(21)    NOT NULL,
  UNITAT_ADM_DESC      character varying(300),
  NUMERO               character varying(255)   NOT NULL,
  DATA                 timestamp without time zone NOT NULL,
  IDENTIFICADOR        character varying(100)   NOT NULL,
  ENTITAT_CODI         character varying(255)    NOT NULL,
  ENTITAT_DESC         character varying(255),
  OFICINA_CODI         character varying(21)    NOT NULL,
  OFICINA_DESC         character varying(300),
  LLIBRE_CODI          character varying(4)     NOT NULL,
  LLIBRE_DESC          character varying(255),
  EXTRACTE             character varying(240),
  ASSUMPTE_TIPUS_CODI  character varying(16),
  ASSUMPTE_TIPUS_DESC  character varying(100),
  ASSUMPTE_CODI        character varying(16),
  ASSUMPTE_DESC        character varying(255),
  PROCEDIMENT_CODI	   character varying(64),
  REFERENCIA           character varying(16),
  EXPEDIENT_NUM        character varying(80),
  NUM_ORIG 			   character varying(80),
  IDIOMA_CODI          character varying(19)     NOT NULL,
  IDIOMA_DESC          character varying(100),
  TRANSPORT_TIPUS_CODI character varying(20),
  TRANSPORT_TIPUS_DESC character varying(100),
  TRANSPORT_NUM        character varying(20),
  USUARI_CODI          character varying(20),
  USUARI_NOM           character varying(767),
  USUARI_CONTACTE      character varying(255),
  APLICACIO_CODI       character varying(255),
  APLICACIO_VERSIO     character varying(255),
  DOCFIS_CODI          character varying(19),
  DOCFIS_DESC          character varying(100),
  OBSERVACIONS         character varying(50),
  EXPOSA               text,
  SOLICITA             text,
  MOTIU_REBUIG         text,
  PROCES_DATA          timestamp without time zone,
  PROCES_ESTAT         character varying(64)    NOT NULL,
  PROCES_INTENTS       integer,
  PROCES_ERROR         text,
  REGLA_ID             bigint,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(256),
  PROCES_ESTAT_SISTRA  character varying(16),
  SISTRA_ID_TRAM       character varying(20),
  SISTRA_ID_PROC       character varying(100),
  DATA_ORIG            timestamp without time zone,
  OFICINA_ORIG_CODI    character varying(21),
  OFICINA_ORIG_DESC    character varying(100),
  JUSTIFICANT_ARXIU_UUID character varying(256),
  LLEGIDA              boolean,
  EXPEDIENT_ARXIU_UUID  character varying(100),
  DATA_TANCAMENT       timestamp without time zone,
  ARXIU_TANCAT         BOOLEAN DEFAULT FALSE	NOT NULL,
  ARXIU_TANCAT_ERROR   BOOLEAN DEFAULT FALSE	NOT NULL,  
  NUMERO_COPIA		   integer default 0 NOT NULL,
  BACK_PENDENT_DATA    timestamp without time zone,
  BACK_REBUDA_DATA    timestamp without time zone,
  BACK_PROCES_REBUTJ_ERROR_DATA  timestamp without time zone,
  BACK_OBSERVACIONS 	character varying(4000),
  BACK_RETRY_ENVIAR_DATA  timestamp without time zone,
  PRESENCIAL 			BOOLEAN,
  JUSTIFICANT_DESCARREGAT BOOLEAN DEFAULT FALSE,
  JUSTIFICANT_ID BIGINT,
  ENVIAT_PER_EMAIL BOOLEAN DEFAULT FALSE,
  PENDENT          BOOLEAN DEFAULT TRUE	NOT NULL,
  AGAFAT_PER 		character varying(64),
  SOBREESCRIURE           boolean default false,
  REACTIVAT               boolean default false
);


CREATE TABLE DIS_REGISTRE_ANNEX
(
  ID                   BIGSERIAL                   NOT NULL,
  TITOL                character varying(200)   NOT NULL,
  FITXER_NOM           character varying(256)    NOT NULL,
  FITXER_TAMANY        integer                  NOT NULL,
  FITXER_MIME          character varying(100),
  FITXER_ARXIU_UUID     character varying(256),
  DATA_CAPTURA         timestamp without time zone NOT NULL,
  LOCALITZACIO         character varying(80),
  ORIGEN_CIUADM        character varying(1)     NOT NULL,
  NTI_TIPUS_DOC        character varying(4)     NOT NULL,
  SICRES_TIPUS_DOC     character varying(2),
  NTI_ELABORACIO_ESTAT character varying(4),
  OBSERVACIONS         character varying(50),
  FIRMA_MODE           integer,
  FIRMA_FITXER_NOM     character varying(80),
  FIRMA_FITXER_TAMANY  integer,
  FIRMA_FITXER_MIME    character varying(30),
  FIRMA_FITXER_ARXIU_UUID  character varying(100),
  FIRMA_CSV            character varying(256),
  TIMESTAMP            character varying(100),
  VALIDACIO_OCSP       character varying(100),
  REGISTRE_ID          bigint                   NOT NULL,
  VERSION              bigint                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(256),
  GESDOC_DOC_ID 	   character varying(50),
  SIGN_DETALLS_DESCARREGAT BOOLEAN DEFAULT FALSE,
  META_DADES CHARACTER VARYING(4000),
  SOBREESCRIURE        boolean default false,
  VAL_FIRMA_ESTAT      character varying(64),
  VAL_FIRMA_ERROR      character varying(255)

);


CREATE TABLE DIS_REGISTRE_ANNEX_FIRMA
(
  ID                   BIGSERIAL                   	NOT NULL,
  TIPUS		           character varying(30),
  PERFIL    	       character varying(30),
  FITXER_NOM           character varying(256),
  TIPUS_MIME           character varying(30),
  CSV_REGULACIO		   character varying(640),
  ANNEX_ID             bigint                    	NOT NULL,
  AUTOFIRMA            boolean,
  CREATEDDATE          timestamp without time zone,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDDATE     timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(256),
  GESDOC_FIR_ID 	   character varying(50)
);

CREATE TABLE DIS_REGISTRE_FIRMA_DETALL
(  	ID BIGSERIAL NOT NULL,
    DATA TIMESTAMP WITHOUT TIME ZONE,
    RESPONSABLE_NIF CHARACTER VARYING(30),
    RESPONSABLE_NOM CHARACTER VARYING(256),
    EMISSOR_CERTIFICAT CHARACTER VARYING(2000),
    FIRMA_ID BIGINT,
    CREATEDBY_CODI       	CHARACTER VARYING(64),
    CREATEDDATE          	TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI  	CHARACTER VARYING(64),
    LASTMODIFIEDDATE     	TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE DIS_REGISTRE_INTER
(
  ID                   BIGSERIAL                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  ADRESA               character varying(640),
  CANAL_PREF           character varying(8),
  CODI_POSTAL          character varying(20),
  DOC_NUM              character varying(68),
  DOC_TIPUS            character varying(4),
  EMAIL                character varying(640),
  EMAIL_HAB            character varying(640),
  LLINATGE1            character varying(255),
  LLINATGE2            character varying(255),
  NOM                  character varying(255),
  OBSERVACIONS         character varying(640),
  PAIS                 character varying(100),
  PAIS_CODI            character varying(4),
  PROVINCIA            character varying(100),
  PROVINCIA_CODI       character varying(4),
  MUNICIPI             character varying(100),
  MUNICIPI_CODI        character varying(4),
  RAO_SOCIAL           character varying(2000),
  TELEFON              character varying(80),
  TIPUS                character varying(19)     NOT NULL,
  VERSION              bigint                   NOT NULL,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDBY_CODI  character varying(256),
  REGISTRE_ID          bigint                   NOT NULL,
  REPRESENTANT_ID      bigint,
  REPRESENTAT_ID       bigint,
  CODI_DIRE 			CHARACTER VARYING(64)
);


CREATE TABLE DIS_REGLA
(
  ID                   BIGSERIAL                   NOT NULL,
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  ACTIVA               boolean,
  ASSUMPTE_CODI        character varying(16),
  DESCRIPCIO           character varying(1024),
  NOM                  character varying(256)   NOT NULL,
  ORDRE                integer                  NOT NULL,
  TIPUS                character varying(32)    NOT NULL,
  UNITAT_CODI          character varying(9),
  VERSION              bigint                   NOT NULL,
  CREATEDBY_CODI       character varying(256),
  LASTMODIFIEDBY_CODI  character varying(256),
  ENTITAT_ID           bigint                   NOT NULL,
  CONTRASENYA          character varying(64),
  TIPUS_BACKOFFICE     character varying(255),
  INTENTS              integer,
  TEMPS_ENTRE_INTENTS  integer,
  URL                  character varying(256),
  USUARI               character varying(64),
  BUSTIA_ID            bigint,
  METAEXPEDIENT_ID     bigint,
  UNITAT_ID 		   BIGINT,
  PROCEDIMENT_CODI     CHARACTER VARYING(1024),
  BACKOFFICE_CODI      CHARACTER VARYING(64),
  BUSTIA_FILTRE_ID BIGINT,
  BACKOFFICE_DESTI_ID BIGINT,
  UNITAT_DESTI_ID BIGINT
);


CREATE TABLE DIS_ACL_CLASS
(
  ID     BIGSERIAL                              NOT NULL,
  CLASS  character varying(100)                 NOT NULL
);


CREATE TABLE DIS_ACL_SID
(
  ID         BIGSERIAL                          NOT NULL,
  PRINCIPAL  boolean                            NOT NULL,
  SID        character varying(100)             NOT NULL
);


CREATE TABLE DIS_ACL_ENTRY
(
  ID                   BIGSERIAL                NOT NULL,
  ACL_OBJECT_IDENTITY  bigint                   NOT NULL,
  ACE_ORDER            bigint                   NOT NULL,
  SID                  bigint                   NOT NULL,
  MASK                 bigint                   NOT NULL,
  GRANTING             boolean                  NOT NULL,
  AUDIT_SUCCESS        boolean                  NOT NULL,
  AUDIT_FAILURE        boolean                  NOT NULL
);


CREATE TABLE DIS_ACL_OBJECT_IDENTITY
(
  ID                  BIGSERIAL                 NOT NULL,
  OBJECT_ID_CLASS     bigint                    NOT NULL,
  OBJECT_ID_IDENTITY  bigint                    NOT NULL,
  PARENT_OBJECT       bigint,
  OWNER_SID           bigint                    NOT NULL,
  ENTRIES_INHERITING  boolean                   NOT NULL
);


CREATE TABLE DIS_CONT_COMMENT
(
  ID                   BIGSERIAL                   NOT NULL,
  CONTINGUT_ID         bigint 		        NOT NULL,
  TEXT		       character varying (4000),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDDATE     timestamp without time zone,
  CREATEDBY_CODI       character varying(64),
  LASTMODIFIEDBY_CODI  character varying(64)
);


CREATE TABLE DIS_UNITAT_ORGANITZATIVA (
  ID           				BIGSERIAL          NOT NULL,
  CODI 						CHARACTER VARYING(9) 		NOT NULL,
  DENOMINACIO 				CHARACTER VARYING(300) 		NOT NULL,
  NIF_CIF 					CHARACTER VARYING(9),
  CODI_UNITAT_SUPERIOR 		CHARACTER VARYING(9),
  CODI_UNITAT_ARREL 		CHARACTER VARYING(9),
  DATA_CREACIO_OFICIAL 		TIMESTAMP WITHOUT TIME ZONE,
  DATA_SUPRESSIO_OFICIAL 	TIMESTAMP WITHOUT TIME ZONE,
  DATA_EXTINCIO_FUNCIONAL 	TIMESTAMP WITHOUT TIME ZONE,
  DATA_ANULACIO 	 		TIMESTAMP WITHOUT TIME ZONE,
  ESTAT 					CHARACTER VARYING(1),
  CODI_PAIS 				CHARACTER VARYING(3),
  CODI_COMUNITAT 			CHARACTER VARYING(2),
  CODI_PROVINCIA 			CHARACTER VARYING(2),
  CODI_POSTAL 				CHARACTER VARYING(5),
  NOM_LOCALITAT 			CHARACTER VARYING(50),
  LOCALITAT 				CHARACTER VARYING(40),
  ADRESSA 					CHARACTER VARYING(70),
  TIPUS_VIA 	 			BIGINT,
  NOM_VIA 					CHARACTER VARYING(200),
  NUM_VIA 					CHARACTER VARYING(100),
  TIPUS_TRANSICIO 		    character varying(12),

  CREATEDDATE          		TIMESTAMP WITHOUT TIME ZONE,
  CREATEDBY_CODI       		CHARACTER VARYING(256),
  LASTMODIFIEDDATE     		TIMESTAMP WITHOUT TIME ZONE,
  LASTMODIFIEDBY_CODI  		CHARACTER VARYING(256),
  CODI_DIR3_ENTITAT		 	CHARACTER VARYING(9)
);

CREATE TABLE DIS_UO_SINC_REL (
  ANTIGA_UO         			BIGINT          NOT NULL,
  NOVA_UO           			BIGINT          NOT NULL
);


CREATE TABLE DIS_AVIS
(
  ID                   BIGSERIAL               			 NOT NULL,
  ASSUMPTE             character varying(256)            NOT NULL,
  MISSATGE             character varying(2048)           NOT NULL,
  DATA_INICI           timestamp without time zone       NOT NULL,
  DATA_FINAL           timestamp without time zone       NOT NULL,
  ACTIU                boolean                		     NOT NULL,
  AVIS_NIVELL          character varying(10)             NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);


CREATE TABLE DIS_CONT_LOG_PARAM
(
    ID BIGSERIAL NOT NULL,
    CONT_LOG_ID BIGINT NOT NULL,
    NUMERO BIGINT NOT NULL,
    VALOR CHARACTER VARYING(256) NOT NULL,
    CREATEDBY_CODI CHARACTER VARYING(64),
    CREATEDDATE TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI CHARACTER VARYING(64),
    LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE DIS_BACKOFFICE
(
    ID BIGSERIAL NOT NULL,
    CODI CHARACTER VARYING(20) NOT NULL,
    NOM CHARACTER VARYING(64) NOT NULL,
    URL CHARACTER VARYING(256) NOT NULL,
    USUARI CHARACTER VARYING(64),
    CONTRASENYA CHARACTER VARYING(64),
    INTENTS INTEGER,
    TEMPS_ENTRE_INTENTS INTEGER,
    ENTITAT_ID BIGINT NOT NULL,
    CREATEDBY_CODI CHARACTER VARYING(64 CHAR),
    CREATEDDATE TIMESTAMP WITHOUT TIME ZONE,
    LASTMODIFIEDBY_CODI CHARACTER VARYING(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE DIS_BUSTIA_FAVORIT
(
    ID                  BIGSERIAL        			NOT NULL, 
    BUSTIA_ID           BIGSERIAL        			NOT NULL, 
    USUARI_CODI         character varying(64)   	NOT NULL, 
	CREATEDBY_CODI      character varying(64), 
	CREATEDDATE         timestamp without time zone, 
	LASTMODIFIEDBY_CODI character varying(64), 
	LASTMODIFIEDDATE    timestamp without time zone
);

CREATE TABLE DIS_CONFIG
(
    KEY                  CHARACTER VARYING(256)     NOT NULL,
    VALUE                CHARACTER VARYING(2048),
    DESCRIPTION          CHARACTER VARYING(2048),
    GROUP_CODE           CHARACTER VARYING(128)     NOT NULL,
    POSITION             INTEGER                		 DEFAULT 0 NOT NULL,
    JBOSS_PROPERTY       BOOLEAN             			 DEFAULT 0 NOT NULL,
    TYPE_CODE            CHARACTER VARYING(128)     DEFAULT 'TEXT',
    ENTITAT_CODI         CHARACTER VARYING(64),
    CONFIGURABLE         BOOLEAN                    DEFAULT FALSE,
    LASTMODIFIEDBY_CODI  CHARACTER VARYING(64),
    LASTMODIFIEDDATE     TIMESTAMP WITHOUT TIMEZONE
);

CREATE TABLE DIS_CONFIG_GROUP
(
    CODE                 CHARACTER VARYING(128 CHAR)     NOT NULL,
    PARENT_CODE          CHARACTER VARYING(128 CHAR)     DEFAULT NULL,
    POSITION             INTEGER              			 DEFAULT 0 NOT NULL,
    DESCRIPTION          CHARACTER VARYING(512 CHAR)     NOT NULL
);

CREATE TABLE DIS_CONFIG_TYPE
(
    CODE                 CHARACTER VARYING(128 CHAR)     NOT NULL,
    VALUE                CHARACTER VARYING(2048 CHAR)    DEFAULT NULL
);

CREATE TABLE DIS_HIS_ANOTACIO
(
	ID						 BIGSERIAL	    NOT NULL,
    ENTITAT_ID               integer     NOT NULL,
    UNITAT_ID                integer,
    TIPUS                    character varying(16) NOT NULL,
    DATA                     timestamp without time zone   NOT NULL,
    ANOTACIONS               integer	    NOT NULL,
    ANOTACIONS_TOTAL         integer	    NOT NULL,
    REENVIAMENTS             integer	    NOT NULL,
    EMAILS                   integer	    NOT NULL,
    JUSTIFICANTS             integer	    NOT NULL,
    ANNEXOS                  integer	    NOT NULL,
    BUSTIES                  integer	    NOT NULL,
    USUARIS                  integer	    NOT NULL
);

CREATE TABLE DIS_HIS_ESTAT
(
	ID						 BIGSERIAL	    NOT NULL,
    ENTITAT_ID               integer     NOT NULL,
    UNITAT_ID                integer,
    TIPUS                    character varying(16) NOT NULL,
    DATA                     timestamp without time zone   NOT NULL,
    ESTAT                    character varying(64) NOT NULL,
    CORRECTE                 integer	    NOT NULL,
    CORRECTE_TOTAL           integer	    NOT NULL,
    ERROR                    integer	    NOT NULL,
    ERROR_TOTAL              integer	    NOT NULL,
    TOTAL                    integer	    NOT NULL
);

CREATE TABLE DIS_HIS_BUSTIA
(
	ID						 BIGSERIAL	    NOT NULL,
    ENTITAT_ID               integer     NOT NULL,
    UNITAT_ID                integer,
    TIPUS                    character varying(16) NOT NULL,
    DATA                     timestamp without time zone   NOT NULL,
    BUSTIA_ID                integer     NOT NULL,
    NOM                      character varying(1024) NOT NULL,
    USUARIS                  integer	    NOT NULL,
    USUARIS_PERMIS           integer	    NOT NULL,
    USUARIS_ROL              integer	    NOT NULL
);

CREATE TABLE DIS_BUSTIA_DEFAULT
(
    ID                      INTEGER                 NOT NULL,
    ENTITAT                 INTEGER                 NOT NULL,
    BUSTIA                  INTEGER                 NOT NULL,
    USUARI             CHARACTER VARYING(64)   NOT NULL,
    CREATEDDATE             TIMESTAMP WITHOUT TIMEZONE,
    LASTMODIFIEDDATE        TIMESTAMP WITHOUT TIMEZONE
);

CREATE TABLE DIS_MON_INT
(	
   	ID					BIGSERIAL 		NOT NULL,
	CODI 				character varying(64) 	NOT NULL, 
	DATA 				timestamp without time zone, 
	DESCRIPCIO 			character varying(1024), 
	TIPUS 				character varying(10), 
	TEMPS_RESPOSTA		BIGSERIAL, 
	ESTAT				character varying(5),
	CODI_USUARI			character varying(64),
	CODI_ENTITAT		character varying(64),
	ERROR_DESCRIPCIO	character varying(1024),
	EXCEPCIO_MSG		character varying(1024),
	EXCEPCIO_STACKTRACE	character varying(2048)
);

CREATE TABLE DIS_MON_INT_PARAM
(	
   	ID					BIGSERIAL 				NOT NULL, 
   	MON_INT_ID			BIGSERIAL				NOT NULL,
	NOM		 			character varying(64) 	NOT NULL,
	DESCRIPCIO 			character varying(1024)
);
