package es.caib.distribucio.logic.intf.resourcevalidation;

import es.caib.distribucio.logic.intf.model.AclEntryResource;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Constraint de validació que controla que es defineixi algun permis per al registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public class AclValidValidator implements ConstraintValidator<AclValid, AclEntryResource> {

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }

    @Override
    public boolean isValid(AclEntryResource resource, ConstraintValidatorContext context) {
        AtomicBoolean valid = new AtomicBoolean(true);

        if (!(
                resource.isReadAllowed()
                || resource.isWriteAllowed()
                || resource.isCreateAllowed()
                || resource.isDeleteAllowed()
                || resource.isAdminAllowed()
                || resource.isPerm0Allowed()
                || resource.isPerm1Allowed()
                || resource.isPerm2Allowed()
                || resource.isPerm3Allowed()
                || resource.isPerm4Allowed()
                || resource.isPerm5Allowed()
                || resource.isPerm6Allowed()
                || resource.isPerm7Allowed()
                || resource.isPerm8Allowed()
                || resource.isPerm9Allowed()
        )) {
            this.addViolation(context, AclEntryResource.Fields.readAllowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.writeAllowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.createAllowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.deleteAllowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.adminAllowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm0Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm1Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm2Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm3Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm4Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm5Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm6Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm7Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm8Allowed, "{permis.form.validacio.buit}");
            this.addViolation(context, AclEntryResource.Fields.perm9Allowed, "{permis.form.validacio.buit}");
            valid.set(false);
        }

        return valid.get();
    }
}
