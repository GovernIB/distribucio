package es.caib.distribucio.persist.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "dis_ACL_OBJECT_IDENTITY",
        uniqueConstraints = @UniqueConstraint(name = "dis_ACL_OBJ_ID_CLASS_IDENT_UQ", columnNames = {
                "OBJECT_ID_CLASS", "OBJECT_ID_IDENTITY"}))
public class AclObjectIdentityEntity extends DistribucioPersistable<Long> {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id_class", nullable = false)
    private AclClassEntity aclClass;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent_object", nullable = true)
    private AclObjectIdentityEntity parentObject;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_sid", nullable = true)
    private AclSidEntity ownerSid;

    @Column(name = "object_id_identity", nullable = false)
    private long objectIdIdentity;

    @Column(name = "entries_inheriting", nullable = false, length = 1)
    private boolean entriesInheriting;

    @OneToMany(mappedBy = "aclObjectIdentity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AclEntryEntity> aclEntries;

}