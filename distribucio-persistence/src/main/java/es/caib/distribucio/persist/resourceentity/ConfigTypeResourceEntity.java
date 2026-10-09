package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.persist.base.entity.ResourceEntity;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ConfigTypeResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe del model de dades que representa un del tipus de dades possibles per a una propietat de configuració.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = BaseConfig.DB_PREFIX + "CONFIG_TYPE")
@Getter
@Setter
@NoArgsConstructor
public class ConfigTypeResourceEntity implements ResourceEntity<ConfigTypeResource, String> {
    @Id
    @Column(name = "CODE", length = 128, nullable = false)
    private String code;

    @Column(name = "VALUE", length = 2048, nullable = false)
    private String value;

    public List<String> getValidValues() {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }

        String[] values = value.split(",");
        return Arrays.asList(values);
    }


    @Override
    public void setId(String id) {
        this.code = id;
    }
	@Override
	public String getId() {
		return this.code;
	}
	@Override
	public boolean isNew() {
		return this.code == null;
	}

}
