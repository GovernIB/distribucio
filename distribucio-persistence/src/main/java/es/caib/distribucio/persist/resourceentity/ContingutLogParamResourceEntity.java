package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ContingutLogParamResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.ContingutLogEntity;
import es.caib.distribucio.persist.entity.ContingutLogParamEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat de base de dades del recurs {@link ContingutLogParamResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci
 * {@link ContingutLogParamEntity} ({@code dis_cont_log}), dedicada
 * exclusivament al mapeig genèric per reflexió del recurs REST. La col·lecció {@code params}
 * reutilitza directament l'entitat legacy {@link ContingutLogParamEntity} -- mateix patró que
 * {@code ContingutResourceEntity.logs}/{@code .alertes}, que ja reutilitzen entitats legacy per a
 * relacions {@code @OneToMany} en lloc de duplicar-les com a "Resource".
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_log_param")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContingutLogParamResourceEntity extends BaseAuditableEntity<ContingutLogParamResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
	private Long id;

    @Column(name = "numero", nullable = false)
    private long numero;

    @Column(name = "valor", length = 256, nullable = false)
    private String valor;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "cont_log_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_log_param_contlog_fk"))
    private ContingutLogResourceEntity contingutLog;

}
