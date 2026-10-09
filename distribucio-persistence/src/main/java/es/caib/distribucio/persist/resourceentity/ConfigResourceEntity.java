package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ConfigResource;
import es.caib.distribucio.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = BaseConfig.DB_PREFIX + "config")
@Getter
@Setter
@NoArgsConstructor
public class ConfigResourceEntity implements ResourceEntity<ConfigResource, String> {
    @Id
    @Column(name = "KEY", length = 256, updatable = false, nullable = false)
    private String key;

    @Column(name = "KEY", insertable = false, updatable = false)
    private String id;

    @Column(name = "VALUE", length = 2048, nullable = true)
    private String value;

    @Column(name = "DESCRIPTION", length = 2048, nullable = true)
    private String description;

    @Column(name = "JBOSS_PROPERTY", nullable = false)
    private boolean jbossProperty;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_CODE", updatable = false)
    private ConfigGroupResourceEntity group;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_CODE", updatable = false)
    private ConfigTypeResourceEntity type;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITAT_CODI", referencedColumnName = "codi")
    private EntitatResourceEntity entitat;
    @Column(name = "ENTITAT_CODI", length = 64, insertable = false, updatable = false)
    private String entitatCodi;
    
    @Column(name = "CONFIGURABLE")
    private boolean configurable;

    @Column(name = "POSITION")
    private int position;

    @ManyToOne
    @JoinColumn(name = "LASTMODIFIEDBY_CODI", updatable = false)
    private UsuariResourceEntity lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(name = "LASTMODIFIEDDATE", updatable = false)
    private Date lastModifiedDate;

    /**
     * Per a mapejar el Dto de la vista.
     *
     * @return El llistat de possibles valors que pot prendre la propietat
     */
    public List<String> getValidValues() {
       return type == null ? Collections.<String>emptyList() : type.getValidValues();
    }
    public String getTypeCode() {
        return type == null ? "" : type.getCode();
    }

    public void update(String value) {
        this.value = value;
    }

	@Override
	public String getId() {
		return this.key;
	}
	@Override
	public boolean isNew() {
		return this.key == null;
	}
   
}