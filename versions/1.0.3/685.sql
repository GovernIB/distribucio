-- public.dis_servei definition

-- Drop table

-- DROP TABLE public.dis_servei;

CREATE TABLE public.dis_servei (
	id bigserial NOT NULL,
	codi varchar(64) NOT NULL,
	nom varchar(256) NULL,
	codisia varchar(64) NULL,
	id_unitat_organitzativa bigserial NOT NULL,
	entitat bigserial NOT NULL,
	estat varchar(20) NOT NULL DEFAULT 'VIGENT'::character varying,
	createdby_codi varchar(64) NULL,
	createddate timestamp NULL,
	lastmodifiedby_codi varchar(64) NULL,
	lastmodifieddate timestamp NULL,
	CONSTRAINT dis_servei_pk PRIMARY KEY (id)
);


-- public.dis_procediment foreign keys

ALTER TABLE public.dis_servei ADD CONSTRAINT dis_servei_entitat_fk FOREIGN KEY (entitat) REFERENCES dis_entitat(id);
ALTER TABLE public.dis_servei ADD CONSTRAINT dis_servei_unitat_fk FOREIGN KEY (id_unitat_organitzativa) REFERENCES dis_unitat_organitzativa(id);

Insert into DIS_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) values ('SERVEIS',null,'23','Plugin de consulta de serveis');

INSERT INTO public.dis_config
("key", value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, "position")
VALUES('es.caib.distribucio.plugin.servei.class', 'es.caib.distribucio.plugin.caib.servei.ServeiPluginRolsac', 'Classe de plugin de serveis', 'SERVEIS', false, 'TEXT', NULL, true, 'dis_super', '2024-05-14 11:50:22.533', 0);

INSERT INTO public.dis_config
("key", value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, "position")
VALUES('es.caib.distribucio.plugin.servei.rolsac.service.url', NULL, 'Url per a accedir serveis', 'SERVEIS', true, 'TEXT', NULL, true, NULL, NULL, 1);

INSERT INTO public.dis_config
("key", value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, "position")
VALUES('es.caib.distribucio.plugin.servei.rolsac.service.username', NULL, 'Nom de l''usuari per a accedir serveis', 'SERVEIS', true, 'TEXT', NULL, true, NULL, NULL, 2);

INSERT INTO public.dis_config
("key", value, description, group_code, jboss_property, type_code, entitat_codi, configurable, lastmodifiedby_codi, lastmodifieddate, "position")
VALUES('es.caib.distribucio.plugin.servei.rolsac.service.password', NULL, 'Password per a accedir serveis', 'SERVEIS', true, 'CREDENTIALS', NULL, false, NULL, NULL, 3);