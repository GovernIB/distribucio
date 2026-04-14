package es.caib.distribucio.persist.entity;

import javax.persistence.*;

@Entity
@Table(name = "dis_acl_entry", uniqueConstraints = @UniqueConstraint(columnNames = {"acl_object_identity", "ace_order"}))
public class AclEntryEntity extends DistribucioPersistable<Long> {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "acl_object_identity", nullable = false)
    private AclObjectIdentityEntity aclObjectIdentity;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sid", nullable = false)
    private AclSidEntity aclSid;

    @Column(name = "ace_order", nullable = false, length = 11)
    private long aceOrder;

    @Column(name = "mask", nullable = false, length = 11)
    private long mask;

    @Column(name = "granting", nullable = false, length = 1)
    private boolean granting;

    @Column(name = "audit_success", nullable = false, length = 1)
    private boolean auditSuccess;

    @Column(name = "audit_failure", nullable = false, length = 1)
    private boolean auditFailure;

}