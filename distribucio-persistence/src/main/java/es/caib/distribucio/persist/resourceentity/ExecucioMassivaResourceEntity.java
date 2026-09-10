package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaEstatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaTipusDto;
import es.caib.distribucio.logic.intf.model.ExecucioMassivaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
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
@Table(name = BaseConfig.DB_PREFIX + "execucio_massiva")
@Getter
@Setter
@NoArgsConstructor
public class ExecucioMassivaResourceEntity extends BaseAuditableEntity<ExecucioMassivaResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "tipus", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecucioMassivaTipusDto tipus;

    @Setter
    @Column(name = "estat", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecucioMassivaEstatDto estat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_creacio", nullable = false)
    private Date dataCreacio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inici")
    private Date dataInici;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_final")
    private Date dataFi;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entitat_id")
    private EntitatResourceEntity entitat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuari_codi")
    private UsuariResourceEntity usuari;

    @Column(name = "parametres")
    private String parametres;

    @Setter
    @Column(name = "nom_document")
    private String nomDocument;

    @OneToMany(
            mappedBy = "execucioMassiva",
            cascade = {CascadeType.ALL},
            fetch = FetchType.EAGER)
    private List<ExecucioMassivaContingutResourceEntity> continguts = new ArrayList<>();

}
