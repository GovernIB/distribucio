package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaAccioDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaEstatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaTipusDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "tipus", "estat" },
        descriptionField = "tipus",
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER },
                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE }
                )
        },
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ExecucioMassivaResource.FILTER_CODE,
                        formClass = ExecucioMassivaResource.FormFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = ExecucioMassivaResource.REPORT_DOWNLOAD_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ExecucioMassivaResource.ACTION_CANVI_ESTAT_CODE,
                        requiresId = true,
                        formClass = ExecucioMassivaAccioDto.class),
        }
)
public class ExecucioMassivaResource extends BaseAuditableResource<Long> {

    public static final String FILTER_CODE = "FILTER";
    public static final String REPORT_DOWNLOAD_CODE = "DOWNLOAD";
    public static final String ACTION_CANVI_ESTAT_CODE = "CANVI_ESTAT";

    private ExecucioMassivaTipusDto tipus;
    private ExecucioMassivaEstatDto estat;
    private Date dataCreacio;
    private Date dataInici;
    private Date dataFi;
    private String parametres;
    private String nomDocument;

    @Transient private int exec;
    @Transient private double percExec;
    @Transient private int errors;

    private ResourceReference<EntitatResource, Long> entitat;
    private ResourceReference<UsuariResource, String> usuari;

    @Getter
    @Setter
    public static class FormFilter implements Serializable {
//        private ResourceReference<UsuariResource, String> usuari;
        private String usuari;
        private ExecucioMassivaTipusDto tipus;
    }

}