package es.caib.distribucio.logic.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ContingutResource.Fields.nom,
        quickFilterFields = { ContingutResource.Fields.nom }
)
public class ContingutResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 1024)
	protected String nom;
//	@NotNull
	protected ContingutTipusEnumDto tipus;
	protected int esborrat = 0;
	@Size(max = 36)
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;

	protected ResourceReference<EntitatResource, Long> entitat;
	protected ResourceReference<ContingutResource, Long> pare;

}