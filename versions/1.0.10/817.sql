alter table DIS_AVIS
    add ENTITAT NUMBER(19)
        constraint DIS_AVIS_ENT_FK
            references DIS_ENTITAT;