package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ContingutResource;
import es.caib.distribucio.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entitat de base de dades del subtipus REGISTRE de {@link ContingutResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.RegistreEntity}
 * (herència JOINED sobre {@code dis_contingut}/{@code dis_registre}), dedicada exclusivament al
 * mapeig genèric per reflexió del recurs REST. Encara no hi ha cap {@code RegistreResource} propi,
 * així que els continguts de tipus REGISTRE es representen amb el {@link ContingutResource} genèric.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre")
@DiscriminatorValue("REGISTRE")
@Getter
@Setter
@NoArgsConstructor
public class RegistreResourceEntity extends ContingutResourceEntity<ContingutResource> implements ResourceEntity<ContingutResource, Long> {
}
