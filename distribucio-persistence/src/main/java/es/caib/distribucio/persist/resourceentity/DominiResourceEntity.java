package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.MultiplicitatEnumDto;
import es.caib.distribucio.logic.intf.model.DominiResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.DominiEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link DominiResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link DominiEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "domini")
@Getter
@Setter
@NoArgsConstructor
public class DominiResourceEntity extends BaseAuditableEntity<DominiResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "codi")
    private String codi;
    @Column(name = "nom")
    private String nom;
    @Column(name = "descripcio")
    private String descripcio;
    @Column(name = "consulta")
    private String consulta;
    @Column(name = "cadena")
    private String cadena;
    @Column(name = "contrasenya")
    private String contrasenya;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entitat_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metadada_entitat_fk"))
    protected EntitatResourceEntity entitat;

}
