package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatCanalEnum;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatTipusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

/**
 * Informació d'una bústia.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = RegistreInteressatResource.Fields.nomComplet,
		quickFilterFields = { RegistreInteressatResource.Fields.documentNum, RegistreInteressatResource.Fields.nom, RegistreInteressatResource.Fields.llinatge1, RegistreInteressatResource.Fields.llinatge2 },
		accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER },
                        grantedPermissions = { PermissionEnum.READ }
                )
        },
        artifacts = {
        }
)
public class RegistreInteressatResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER";

    private RegistreInteressatTipusEnum tipus;
    private RegistreInteressatDocumentTipusEnum documentTipus;
    private String documentNum;
    private String nom;
    private String llinatge1;
    private String llinatge2;
    private String raoSocial;
    private String pais;
    private String paisCodi;
    private String provincia;
    private String provinciaCodi;
    private String municipi;
    private String municipiCodi;
    private String adresa;
    private String codiPostal;
    private String email;
    private String telefon;
    private String emailHabilitat;
    private RegistreInteressatCanalEnum canalPreferent;
    private String observacions;
    private String codiDire;

    private long version = 0;

    @Transient private String nomComplet;

    protected ResourceReference<RegistreResource, Long> registre;
    protected ResourceReference<RegistreInteressatResource, Long> representant;
    protected ResourceReference<RegistreInteressatResource, Long> representat;

}
