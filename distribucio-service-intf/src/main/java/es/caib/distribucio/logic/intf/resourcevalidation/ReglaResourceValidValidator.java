package es.caib.distribucio.logic.intf.resourcevalidation;

import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ReglaResource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validació bàsica de {@link ReglaResource}, veure {@link ReglaResourceValid}.
 *
 * @author Límit Tecnologies
 */
public class ReglaResourceValidValidator implements ConstraintValidator<ReglaResourceValid, ReglaResource> {

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }

    private void notNullViolation(ConstraintValidatorContext context, String field) {
        addViolation(context, field, "{javax.validation.constraints.NotNull.message}");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public boolean isValid(ReglaResource resource, ConstraintValidatorContext context) {
        boolean valid = true;

        // Com a mínim un filtre informat, llevat de les regles de tipus BACKOFFICE.
        if (resource.getTipus() != ReglaTipusEnumDto.BACKOFFICE
                && isBlank(resource.getAssumpteCodiFiltre())
                && isBlank(resource.getProcedimentCodiFiltre())
                && isBlank(resource.getServeiCodiFiltre())
                && resource.getUnitatOrganitzativaFiltre() == null
                && resource.getBustiaFiltre() == null) {
            notNullViolation(context, ReglaResource.Fields.assumpteCodiFiltre);
            notNullViolation(context, ReglaResource.Fields.procedimentCodiFiltre);
            notNullViolation(context, ReglaResource.Fields.serveiCodiFiltre);
            notNullViolation(context, ReglaResource.Fields.unitatOrganitzativaFiltre);
            notNullViolation(context, ReglaResource.Fields.bustiaFiltre);
            valid = false;
        }

        // Destí obligatori segons el tipus de regla.
        if (resource.getTipus() == ReglaTipusEnumDto.UNITAT && resource.getUnitatDesti() == null) {
            notNullViolation(context, ReglaResource.Fields.unitatDesti);
            valid = false;
        } else if (resource.getTipus() == ReglaTipusEnumDto.BUSTIA && resource.getBustiaDesti() == null) {
            notNullViolation(context, ReglaResource.Fields.bustiaDesti);
            valid = false;
        } else if (resource.getTipus() == ReglaTipusEnumDto.BACKOFFICE) {
            if (resource.getBackofficeDesti() == null) {
                notNullViolation(context, ReglaResource.Fields.backofficeDesti);
                valid = false;
            }
            if (isBlank(resource.getProcedimentCodiFiltre()) && isBlank(resource.getServeiCodiFiltre())) {
                notNullViolation(context, ReglaResource.Fields.procedimentCodiFiltre);
                notNullViolation(context, ReglaResource.Fields.serveiCodiFiltre);
                valid = false;
            }
        }

        // Només un dels dos codis (procediment o servei), mai els dos alhora.
        if (!isBlank(resource.getProcedimentCodiFiltre()) && !isBlank(resource.getServeiCodiFiltre())) {
            String message = "Només es pot informar el codi de procediment o el de servei, no els dos alhora";
            addViolation(context, ReglaResource.Fields.procedimentCodiFiltre, message);
            addViolation(context, ReglaResource.Fields.serveiCodiFiltre, message);
            valid = false;
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
        }
        return valid;
    }

}
