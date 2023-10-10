/**
 * 
 */
package es.caib.distribucio.war.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.service.ReglaService;
import es.caib.distribucio.war.command.ReglaCommand;
import es.caib.distribucio.war.helper.EntitatHelper;
import es.caib.distribucio.war.helper.MessageHelper;

/**
 * Constraint de validació per a les regles de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglaValidator implements ConstraintValidator<Regla, ReglaCommand> {

	private String codiMissatge;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ReglaService reglaService;

	@Override
	public void initialize(final Regla anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(final ReglaCommand command, final ConstraintValidatorContext context) {
		boolean valid = true;
		
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		
		// Comprova que almenys un camp del firtre esta informat
		if (command.getTipus() != ReglaTipusEnumDto.BACKOFFICE && // Si es Tipo UNITAT o BUSTIA
			(command.getAssumpteCodiFiltre() == null || command.getAssumpteCodiFiltre().trim().isEmpty()) && 
			(command.getProcedimentCodiFiltre() == null || command.getProcedimentCodiFiltre().trim().isEmpty()) &&
			 command.getUnitatFiltreId() == null &&
			 command.getBustiaFiltreId() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("assumpteCodiFiltre")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("procedimentCodiFiltre")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("unitatFiltreId")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".codi.buit", null, new RequestContext(request).getLocale()))
					.addNode("bustiaFiltreId")
					.addConstraintViolation();	
			valid = false;
		}
		

		if (command.getTipus() == ReglaTipusEnumDto.UNITAT && command.getUnitatDestiId() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
					.addNode("unitatDestiId")
					.addConstraintViolation();	
			valid = false;
		} else if (command.getTipus() == ReglaTipusEnumDto.BUSTIA && command.getBustiaDestiId() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
					.addNode("bustiaDestiId")
					.addConstraintViolation();	
			valid = false;
		} else if (command.getTipus() == ReglaTipusEnumDto.BACKOFFICE) {
			if (command.getBackofficeDestiId() == null) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
						.addNode("backofficeDestiId")
						.addConstraintViolation();	
				valid = false;
			}
			if (command.getProcedimentCodiFiltre() == null || command.getProcedimentCodiFiltre().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
						.addNode("procedimentCodiFiltre")
						.addConstraintViolation();	
				valid = false;
			}

			// No permetre crear regles de tipus "Gestionar amb backoffice" amb codis SIA ja definits en altres regles
			if (command.getProcedimentCodiFiltre() != null) {
				
				Map<String, List<ReglaDto>> mapa;
				String procedimentCodiFiltre = command.getProcedimentCodiFiltre().trim();
				List<String> procediments = Arrays.asList(procedimentCodiFiltre.split("\\s+"));
				mapa = reglaService.findReglesByCodiProcediment(procediments);
				
				if (mapa != null && !mapa.isEmpty()) {
					for(String codiProcediment : mapa.keySet()) {
						StringBuilder reglesNoms = new StringBuilder("");
						for(ReglaDto regla : mapa.get(codiProcediment)) {
							if (!regla.getId().equals(command.getId())) {
								
								reglesNoms.append(regla.getNom());
								if (!regla.getEntitatId().equals(entitatActual.getId())) {
									reglesNoms.append(" (").append(regla.getEntitatNom()).append(")");
								}
								reglesNoms.append(", ");
							}
						}
						if (reglesNoms != null && !reglesNoms.toString().isEmpty()) {
							String[] args = {reglesNoms.substring(0, reglesNoms.length()-2).toString(), codiProcediment};
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(codiMissatge + ".backoffice.codiprocediment.existent", args, new RequestContext(request).getLocale()))
							.addNode("procedimentCodiFiltre")
							.addConstraintViolation();	
							valid = false;
						}
					}
				}
			}
		}
		

		if (!valid)
			context.disableDefaultConstraintViolation();
		
        return valid;
	}
}
