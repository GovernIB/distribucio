package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.logic.intf.model.BackofficeResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entitat de base de dades del recurs {@link BackofficeResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.EntitatEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "backoffice")
@Getter
@Setter
@NoArgsConstructor
public class BackofficeResourceEntity extends BaseAuditableEntity<BackofficeResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "codi", length = 20, nullable = false, unique = true)
    private String codi;
    @Column(name = "nom", length = 64, nullable = false)
    private String nom;
    @Column(name = "url", length = 256, nullable = false)
    private String url;
    @Column(name = "usuari", length = 255)
    private String usuari;
    @Column(name = "contrasenya", length = 255)
    private String contrasenya;
    @Column(name = "intents")
    private Integer intents;
    @Column(name = "temps_entre_intents")
    private Integer tempsEntreIntents;

    @Column(name = "tipus")
    @Enumerated(EnumType.STRING)
    private BackofficeTipusEnumDto tipus;

    @Column(name = "ENVIAR_EMAIL_RESPONSABLE")
    private Boolean enviamentEmail;
    @Column(name = "EMAIL_RESPONSABLE")
    private String emailResponsable;
    @Column(name = "DARRER_EMAIL")
    private LocalDateTime darrerEmailResponsable;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "entitat_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "backoffice_entitat_fk"))
    private EntitatResourceEntity entitat;

}
