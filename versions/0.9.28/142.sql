alter table dis_registre modify numero varchar2(255);
alter table dis_registre modify idioma_codi varchar2(19);
alter table dis_registre modify entitat_codi varchar2(255);
alter table dis_registre modify entitat_desc varchar2(255);
alter table dis_registre modify oficina_desc varchar2(300);
alter table dis_registre modify llibre_desc varchar2(255);
alter table dis_registre modify assumpte_desc varchar2(255);
alter table dis_registre modify transport_tipus_codi varchar2(20);
alter table dis_registre modify usuari_nom varchar2(767);
alter table dis_registre modify usuari_contacte varchar2(255);
alter table dis_registre modify aplicacio_codi varchar2(255);
alter table dis_registre modify aplicacio_versio varchar2(255);
alter table dis_registre modify docfis_codi varchar2(19);
alter table dis_registre modify justificant_arxiu_uuid varchar2(256);

alter table dis_registre_inter modify tipus varchar2(19);
alter table dis_registre_inter modify nom varchar2(255);
alter table dis_registre_inter modify llinatge1 varchar2(255);
alter table dis_registre_inter modify llinatge2 varchar2(255);
alter table dis_registre_inter modify rao_social varchar2(2000);

alter table dis_registre_annex modify fitxer_arxiu_uuid varchar2(256);

