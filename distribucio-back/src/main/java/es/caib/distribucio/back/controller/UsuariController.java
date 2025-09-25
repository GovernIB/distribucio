/**
 * 
 */
package es.caib.distribucio.back.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import es.caib.distribucio.back.helper.EntitatHelper;
import es.caib.distribucio.logic.intf.service.EntitatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.UsuariCommand;
import es.caib.distribucio.back.helper.EnumHelper;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.SessioHelper;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.IdiomaEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import lombok.Builder;
import lombok.Data;

/**
 * Controlador per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari")
public class UsuariController  extends BaseAdminController {

	@Autowired
	private AplicacioService aplicacioService;
    @Autowired private EntitatService entitatService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			Model model) {
		model.addAttribute(
				"reglaTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						ReglaTipusEnumDto.class,
						"regla.tipus.enum."));
		return "reglaList";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		
		UsuariDto usuari = aplicacioService.getUsuariActual();
		// Consulta l'usuari amb el plugin i actualitza les dades a la taula d'usuaris.
		try {
			usuari = aplicacioService.updateUsuari(usuari.getCodi());
			SessioHelper.setUsuariActual(request, usuari);
		} catch(Exception e) {
			MissatgesHelper.error(request, getMessage(request, "usuari.form.actualitzar.usuari.error", new Object[] {e.toString()}));
		}
		
		EntitatDto entitatActual = this.getEntitatActual(request);
		BustiaDto bustiaPerDefecte = aplicacioService.getBustiaPerDefecte(usuari, entitatActual.getId());

        List<EntitatDto> entitatsAccessibles = EntitatHelper.findEntitatsAccessibles(request, entitatService);
        model.addAttribute("entitats", entitatsAccessibles);
		
		model.addAttribute(UsuariCommand.asCommand(usuari));
		model.addAttribute("rolsPerMostrar", this.getFiltraRolsPerMostrar(usuari.getRols()));
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"usuari.form.camp.idioma.enum."));
		if (bustiaPerDefecte != null)
			model.addAttribute("bustiaPerDefecte", bustiaPerDefecte.getId());
		return "usuariForm";
	}

	/** Filtra els rols per a que es mostrin només els que comencen per 'DIS_'*/
	private String[] getFiltraRolsPerMostrar(String[] rols) {
		List<String> rolsPerMostrar = new ArrayList<>();
		if (rols != null) {
			for (int i = 0; i < rols.length; i++) {
				if (rols[i].startsWith("DIS_")) {
					rolsPerMostrar.add(rols[i]);
				}
			}
		}
		return rolsPerMostrar.toArray(String[]::new);
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid UsuariCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "usuariForm";
		}
		EntitatDto entitatActual = getEntitatActual(request);
		UsuariDto usuari = aplicacioService.updateUsuariActual(UsuariCommand.asDto(command), entitatActual.getId());
		SessioHelper.setUsuariActual(request, usuari);
		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/",
					"usuari.controller.modificat.ok");
	}

	///////////////////////////////////////

	@RequestMapping(value = "/usernames/change", method = RequestMethod.GET)
	public String getCanviCodis(
			HttpServletRequest request,
			Model model) {
		return "usuarisCanviCodi";
	}

	@RequestMapping(value = "/usernames/{codiAntic}/validateTo/{codiNou}", method = RequestMethod.POST, produces = "application/json" )
	@ResponseBody
	public UsuariChangeValidation validaCanviCodis(
			HttpServletRequest request,
			@PathVariable("codiAntic") String codiAntic,
			@PathVariable("codiNou") String codiNou) {

		UsuariDto usuariAntic = aplicacioService.findUsuariAmbCodi(codiAntic);
		UsuariDto usuariNou = aplicacioService.findUsuariAmbCodi(codiNou);

		return UsuariChangeValidation.builder()
				.usuariAnticExists(usuariAntic != null)
				.usuariNouExists(usuariNou != null)
				.build();
	}

	@RequestMapping(value = "/usernames/{codiAntic}/changeTo/{codiNou}", method = RequestMethod.POST, produces = "application/json" )
	@ResponseBody
	public UsuariChangeResponse setCanviCodis(
			HttpServletRequest request,
			@PathVariable("codiAntic") String codiAntic,
			@PathVariable("codiNou") String codiNou) {

		Long t0 = System.currentTimeMillis();
		try {
			Long registresModificats = aplicacioService.updateUsuariCodi(codiAntic, codiNou);
			return UsuariChangeResponse.builder()
					.estat(ResultatEstatEnum.OK)
					.registresModificats(registresModificats)
					.duracio(System.currentTimeMillis() - t0)
					.build();
		} catch (Exception e) {
			logger.error("Error modificant el codi de l'usuari", e);
			return UsuariChangeResponse.builder()
					.estat(ResultatEstatEnum.ERROR)
					.errorMessage(getMessage(request, "usuari.controller.codi.modificat.error", null) + ": " + e.getMessage())
					.duracio(System.currentTimeMillis() - t0)
					.build();
		}
	}

	@Data
	@Builder
	public static class UsuariChangeValidation {
		private boolean usuariAnticExists;
		private boolean usuariNouExists;
	}

	@Data
	@Builder
	public static class UsuariChangeResponse {
		private ResultatEstatEnum estat;
		private String errorMessage;
		private Long registresModificats;
		private Long duracio;
	}

	public enum ResultatEstatEnum { OK, ERROR }
	
	private static final Logger logger = LoggerFactory.getLogger(UsuariController.class);

}
