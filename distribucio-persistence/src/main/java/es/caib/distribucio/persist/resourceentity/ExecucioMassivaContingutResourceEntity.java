package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ElementTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaEstatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaTipusDto;
import es.caib.distribucio.logic.intf.model.ExecucioMassivaContingutResource;
import es.caib.distribucio.logic.intf.model.ExecucioMassivaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entitat de base de dades del recurs {@link ExecucioMassivaResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link EntitatEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "execucio_massiva_cont")
@Getter
@Setter
@NoArgsConstructor
public class ExecucioMassivaContingutResourceEntity extends BaseAuditableEntity<ExecucioMassivaContingutResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "estat", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecucioMassivaContingutEstatDto estat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_creacio", nullable = false)
    private Date dataCreacio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inici")
    private Date dataInici;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_final")
    private Date dataFi;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "execucio_massiva_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "execucio_massiva_contingut_massiva_fk"))
    private ExecucioMassivaResourceEntity execucioMassiva;

    @Column(name = "element_id", nullable = false)
    private Long elementId;

    @Column(name = "element_nom")
    private String elementNom;

    @Column(name = "element_tipus", nullable = false)
    @Enumerated(EnumType.STRING)
    private ElementTipusEnumDto elementTipus;

    @Column(name = "error")
    private String error;

    @Column(name = "missatge")
    private String missatge;

    @Column(name = "ordre", nullable = false)
    private int ordre;

}
