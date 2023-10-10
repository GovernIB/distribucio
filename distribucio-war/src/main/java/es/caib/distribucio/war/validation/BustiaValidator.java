/**
 * 
 */
package es.caib.distribucio.war.validation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;
import es.caib.distribucio.war.command.BustiaCommand;
import es.caib.distribucio.war.helper.EntitatHelper;
import es.caib.distribucio.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que no es repeteixi el nom de la bústia per una unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaValidator implements ConstraintValidator<Bustia, BustiaCommand> {

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;

	@Autowired
	private BustiaService bustiaService;

	@Override
	public void initialize(final Bustia anotacio) {
		
	}

	@Override
	public boolean isValid(final BustiaCommand command, final ConstraintValidatorContext context) {
		boolean valid = true;
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null
				&& command != null 
				&& command.getUnitatId() != null) {			
			UnitatOrganitzativaDto uo = unitatOrganitzativaService.findById(command.getUnitatId());
			if (uo != null) {
				// Consulta totes les bústies
				List<BustiaDto> busties = bustiaService.findAmbUnitatCodiAdmin(entitat.getId(), uo.getCodi());
				for (BustiaDto bustia : busties) {
					if (bustia.getNom().equals(command.getNom())
							&& !bustia.getId().equals(command.getId())) {
						valid = false;
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("bustia.validator.existeix.nom", null, new RequestContext(request).getLocale()))
								.addNode("nom")
								.addConstraintViolation();	
					}
				}
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}
}
