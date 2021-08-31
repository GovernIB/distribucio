package es.caib.distribucio.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe del model de dades de configuracio de properties.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "DIS_CONFIG")
@EntityListeners(AuditingEntityListener.class)
public class ConfigEntity {

	@Id
    @Column(name = "KEY", length = 256, nullable = false)
    private String key;

    @Column(name = "VALUE", length = 2048, nullable = true)
    private String value;

    @Column(name = "DESCRIPTION", length = 2048, nullable = true)
    private String description;

    @Column(name = "JBOSS_PROPERTY", nullable = false)
    private boolean jbossProperty;

    @Column(name = "GROUP_CODE", length = 2048, nullable = true)
    private String groupCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_CODE", insertable = false, updatable = false)
    @ForeignKey(name = "NOT_CONFIG_TYPE_FK")
    private ConfigTypeEntity type;

    @Column(name = "POSITION")
    private int position;
    
    @LastModifiedBy
    @ManyToOne
    private UsuariEntity lastModifiedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    /**
     * Per a mapejar el Dto de la vista.
     *
     * @return El llistat de possibles valors que pot prendre la propietat
     */
    public List<String> getValidValues() {
       return type.getValidValues();
    }
    public String getTypeCode() {
        return type.getCode();
    }

    public void updateValue(String value) {
        this.value = value;
    }
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public String getDescription() {
		return description;
	}
	public boolean isJbossProperty() {
		return jbossProperty;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public ConfigTypeEntity getType() {
		return type;
	}
	public int getPosition() {
		return position;
	}

	public ConfigEntity() {
	}
	
    
    
}
