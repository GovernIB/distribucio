package es.caib.distribucio.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dis_acl_class")
public class AclClassEntity extends DistribucioPersistable<Long> {

    @Column(name = "class", nullable = false, unique = true, length = 255)
    private String aclClass;
}