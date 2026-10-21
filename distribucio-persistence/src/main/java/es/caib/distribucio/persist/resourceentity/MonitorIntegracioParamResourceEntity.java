/**
 * 
 */
package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.MonitorIntegracioParamResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Classe del model de dades que representa un MonitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "mon_int_param")
@Getter
@Setter
@NoArgsConstructor
public class MonitorIntegracioParamResourceEntity extends BaseResourceEntity<MonitorIntegracioParamResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;
	
	@Column(name = "nom", length = 256, nullable = false)
	private String key;
	
	@Column(name = "descripcio", length = 1024)
	private String value;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "mon_int_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "mon_int_param_monint_fk"))
    private MonitorIntegracioResourceEntity monitorIntegracio;

}
