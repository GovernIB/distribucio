/**
 * 
 */
package es.caib.distribucio.back.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.back.command.ReglaCommand;
import es.caib.distribucio.back.helper.EntitatHelper;
import es.caib.distribucio.back.helper.MessageHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.service.ReglaService;

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
		
		if (command.getNom() == null || command.getNom().trim().isEmpty()) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
					.addNode("nom")
					.addConstraintViolation();	
			valid = false;
		}
		
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		// Comprova que almenys un camp del firtre esta informat
		if (command.getTipus() != ReglaTipusEnumDto.BACKOFFICE && // Si es Tipo UNITAT o BUSTIA
			(command.getAssumpteCodiFiltre() == null || command.getAssumpteCodiFiltre().trim().isEmpty()) && 
			(command.getProcedimentCodiFiltre() == null || command.getProcedimentCodiFiltre().trim().isEmpty()) &&
			(command.getServeiCodiFiltre() == null || command.getServeiCodiFiltre().trim().isEmpty()) &&
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
					.addNode("serveiCodiFiltre")
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
			if ((command.getProcedimentCodiFiltre() == null || command.getProcedimentCodiFiltre().trim().isEmpty()) &&
                    (command.getServeiCodiFiltre() == null || command.getServeiCodiFiltre().trim().isEmpty())) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
						.addNode("procedimentCodiFiltre")
						.addConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                MessageHelper.getInstance().getMessage(codiMissatge + ".tipus.desti.buit", null, new RequestContext(request).getLocale()))
                        .addNode("serveiCodiFiltre")
                        .addConstraintViolation();
				valid = false;
			}
			
			// Comprova que els codis SIA de procediment no s'usin en altres regles ja sigui per procediment o servei.
			boolean mateixCodiSiaMateixFiltreUnitat = false;
			Long unitatOrganitzativaId = command.getUnitatFiltreId();
			String unitatOrganitzativaComparada;
			Map<String, List<ReglaDto>> reglesExistents;
			if (command.getProcedimentCodiFiltre() != null) {
				unitatOrganitzativaComparada = MessageHelper.getInstance().getMessage(codiMissatge + ".unitat.buida", null, new RequestContext(request).getLocale());
				String procedimentCodiFiltre = command.getProcedimentCodiFiltre().trim();
				List<String> procediments = Arrays.asList(procedimentCodiFiltre.split("\\s+"));
				reglesExistents = reglaService.findReglesByCodisSia(procediments);
				for(String codiProcediment : reglesExistents.keySet()) {				
					StringBuilder reglesNoms = new StringBuilder("");
					for(ReglaDto regla : reglesExistents.get(codiProcediment)) {
						if (!regla.getId().equals(command.getId()) && ReglaTipusEnumDto.BACKOFFICE.equals(regla.getTipus())) {
							if (
									(unitatOrganitzativaId==null) ||
									(regla.getUnitatOrganitzativaFiltre()==null) ||
									(unitatOrganitzativaId.equals(regla.getUnitatOrganitzativaFiltre().getId())) 
							) {			
								if (regla.getUnitatOrganitzativaFiltre()!=null) {
									unitatOrganitzativaComparada = regla.getUnitatOrganitzativaFiltre().getDenominacio();
								}
								mateixCodiSiaMateixFiltreUnitat = true;
								reglesNoms.append(regla.getNom());
								if (!regla.getEntitatId().equals(entitatActual.getId())) {
									reglesNoms.append(" (").append(regla.getEntitatNom()).append(")");
								}
								reglesNoms.append(", ");
							}
						}
					}
					if (mateixCodiSiaMateixFiltreUnitat) {
						if (reglesNoms != null && !reglesNoms.toString().isEmpty()) {
							String[] args = {reglesNoms.substring(0, reglesNoms.length()-2).toString(), codiProcediment, unitatOrganitzativaComparada};
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(codiMissatge + ".backoffice.codisia.igualUnitat.existent", args, new RequestContext(request).getLocale()))
							.addNode("procedimentCodiFiltre")
							.addConstraintViolation();	
							valid = false;
						}
					}
				}
			}
			// Comprova que els codis SIA de serveis no s'usin en altres regles ja sigui per procediment o servei.
			if (command.getServeiCodiFiltre() != null) {
				mateixCodiSiaMateixFiltreUnitat = false;
				unitatOrganitzativaComparada = MessageHelper.getInstance().getMessage(codiMissatge + ".unitat.buida", null, new RequestContext(request).getLocale());
				String serveiCodiFiltre = command.getServeiCodiFiltre().trim();
				List<String> serveis = Arrays.asList(serveiCodiFiltre.split("\\s+"));
				reglesExistents = reglaService.findReglesByCodisSia(serveis);
				for(String codiServei : reglesExistents.keySet()) {						
					StringBuilder reglesNoms = new StringBuilder("");
					for(ReglaDto regla : reglesExistents.get(codiServei)) {
						if (!regla.getId().equals(command.getId()) && ReglaTipusEnumDto.BACKOFFICE.equals(regla.getTipus())) {
							if (
									(unitatOrganitzativaId==null) ||
									(regla.getUnitatOrganitzativaFiltre()==null) ||
									(unitatOrganitzativaId.equals(regla.getUnitatOrganitzativaFiltre().getId())) 
							) {			
								if (regla.getUnitatOrganitzativaFiltre()!=null) {
									unitatOrganitzativaComparada = regla.getUnitatOrganitzativaFiltre().getDenominacio();
								}
								mateixCodiSiaMateixFiltreUnitat = true;
								reglesNoms.append(regla.getNom());
								if (!regla.getEntitatId().equals(entitatActual.getId())) {
									reglesNoms.append(" (").append(regla.getEntitatNom()).append(")");
								}
								reglesNoms.append(", ");
							}
						}
					}
					if (mateixCodiSiaMateixFiltreUnitat) {
						if (reglesNoms != null && !reglesNoms.toString().isEmpty()) {
							String[] args = {reglesNoms.substring(0, reglesNoms.length()-2).toString(), codiServei, unitatOrganitzativaComparada};
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(codiMissatge + ".backoffice.codisia.igualUnitat.existent", args, new RequestContext(request).getLocale()))
							.addNode("serveiCodiFiltre")
							.addConstraintViolation();	
							valid = false;
						}
					}
				}
			}
		}
		
		
		// Comrova que només s'informi el codi de procediment o el codi de servei, però no tots dos a l'hora
		if (command.getProcedimentCodiFiltre() != null 
				&& command.getServeiCodiFiltre() != null) {
			String errMsg = MessageHelper.getInstance().getMessage(codiMissatge + ".codis.procediment.servei", null, new RequestContext(request).getLocale());
			context.buildConstraintViolationWithTemplate(
					errMsg)
					.addNode("procedimentCodiFiltre")
					.addConstraintViolation();	
			context.buildConstraintViolationWithTemplate(
					errMsg)
					.addNode("serveiCodiFiltre")
					.addConstraintViolation();	
			valid = false;
		}
		
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}
}
