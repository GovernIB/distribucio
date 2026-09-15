package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.RegistreNombreAnnexesEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
		descriptionField = ContingutResource.Fields.nom,
		quickFilterFields = { ContingutResource.Fields.nom },
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
public class RegistreAnnexResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER";

    private String titol;
    private String fitxerNom;
    private int fitxerTamany;
    private String fitxerTipusMime;
    private String fitxerArxiuUuid;
//    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCaptura;
    private String localitzacio;
    private String origenCiutadaAdmin;
    private String ntiTipusDocument;
    private String sicresTipusDocument;
    private String ntiElaboracioEstat;
    private String observacions;
    private Integer firmaMode;
    private String firmaCsv;
    private String timestamp;
    private String validacioOCSP;
    private String gesdocDocumentId;
    private boolean signaturaDetallsDescarregat;
    private String metaDades;
    private ValidacioFirmaEnum validacioFirmaEstat;
    private String validacioFirmaError;
    private AnnexEstat arxiuEstat;

    private long version = 0;

    private ResourceReference<RegistreResource, Long> registre;
//    private List<RegistreAnnexFirmaEntity> firmes;

}
