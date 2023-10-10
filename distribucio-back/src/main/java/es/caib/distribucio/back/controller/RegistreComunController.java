package es.caib.distribucio.back.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.ContingutReenviarCommand;
import es.caib.distribucio.back.command.MarcarProcessatCommand;
import es.caib.distribucio.back.command.RegistreClassificarCommand;
import es.caib.distribucio.back.command.RegistreEnviarIProcessarCommand;
import es.caib.distribucio.back.command.RegistreEnviarViaEmailCommand;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.RegistreHelper;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.ClassificacioResultatDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.RegistreService;

/**
 * Controlador per a la consulta d'arxius 
 *	comuns a administradors i usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/registreComun")
public class RegistreComunController extends BaseController{	

	private static final String SESSION_ATTRIBUTE_SELECCIO_ADMIN = "RegistreAdminController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_SELECCIO_USER = "RegistreUserController.session.seleccio";
	
	@Autowired
	private RegistreService registreService;
	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private RegistreHelper registreHelper;

	@RequestMapping(value = "/classificarMultiple/{registreId}/{codiProcediment}", method = RequestMethod.GET)
	@ResponseBody
	public ClassificacioResultatDto classificarMultiplePost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable String codiProcediment,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermis(request, "tothom");
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatActual.getId(),
				registreId,
				codiProcediment,
				null);
		return resultat;
	}

	@RequestMapping(value = "/classificarMultiple/{rol}", method = RequestMethod.GET)
	public String classificarMultipleGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		String sessionAttributeSeleccio = "";
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
			isAdmin = true;
		}else {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_USER;
		}
		List<RegistreDto> seleccio =
				registreService.findMultiple(
						getEntitatActualComprovantPermis(request, rol).getId(),
						this.getRegistresSeleccionats(request, sessionAttributeSeleccio),
						isAdmin);
		if (seleccio != null && !seleccio.isEmpty()) {
			List<Long> seleccioList = new ArrayList<Long>();
			for (RegistreDto registreDto : seleccio) {
				seleccioList.add(registreDto.getId());
			}
			
			boolean mateixPare = emplenarModelClassificarMultiple(
					request,
					seleccioList,
					model, 
					rol);
			if (mateixPare) {
				RegistreClassificarCommand command = new RegistreClassificarCommand();
				model.addAttribute(command);
				return "registreClassificarMultiple";
			} else {
				return getModalControllerReturnValueError(
						request,
						"redirect:../registreUser",
						"bustia.controller.pendent.contingut.classificar.no.mateix.pare.error");
			}
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:../registreUser",
					"bustia.controller.pendent.contingut.classificar.seleccio.buida");
		}
	}

	private boolean emplenarModelClassificarMultiple(
			HttpServletRequest request,
			List<Long> multipleRegistreIds,
			Model model, 
			String rol) {
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			isAdmin = true;
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermis(request, rol);
		List<RegistreDto> registres = registreService.findMultiple(
				entitatActual.getId(),
				multipleRegistreIds,
				isAdmin);
		model.addAttribute("registres", registres);
		boolean mateixPare = true;
		Long bustiaIdActual = null;
		if (!registres.isEmpty()) {
			for (RegistreDto registre: registres) {
				if (bustiaIdActual == null) {
					bustiaIdActual = registre.getPareId();
				}
				if (!bustiaIdActual.equals(registre.getPareId())) {
					mateixPare = false;
					break;
				}
			}
		}
		if (mateixPare && bustiaIdActual != null) {
			model.addAttribute(
					"procediments",
					registreService.classificarFindProcediments(
							entitatActual.getId(),
							bustiaIdActual));
		}
		return mateixPare;
	}

	
	@RequestMapping(value = "/registreReenviarMultiple/{rol}", method = RequestMethod.GET)
	public String registreReenviarMultipleGet(
			HttpServletRequest request,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model, 
			@PathVariable String rol) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermis(request, rol);
			omplirModelPerReenviarMultiple(request, entitatActual, model, rol);
			ContingutReenviarCommand command = new ContingutReenviarCommand();
			model.addAttribute(command);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (NotFoundException.class.equals((e.getCause() != null ? e.getCause() : e).getClass())) {
				return getModalControllerReturnValueError(
						request,
						"",
						"registre.user.controller.reenviar.error.registreNoTrobat");
			} else {
				return getModalControllerReturnValueErrorNoKey(
						request,
						"",
						e.getMessage());
			}
		}
		return "registreReenviarForm";
		

	}
	
	private void omplirModelPerReenviarMultiple(
			HttpServletRequest request, 
			EntitatDto entitatActual,
			Model model, 
			String rol) {
		String sessionAttributeSeleccio = "";
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
			isAdmin = true;
		}else {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_USER;
		}

		List<BustiaDto> busties = bustiaService.findActivesAmbEntitat(
				entitatActual.getId());
		
		boolean disableDeixarCopia = true;

		
		model.addAttribute(
				"selectMultiple",
				false);
		
		model.addAttribute(
				"disableDeixarCopia",
				disableDeixarCopia);
		
		model.addAttribute("maxLevel", getMaxLevelArbre());
		
		model.addAttribute(
				"busties",
				busties);
		model.addAttribute("isEnviarConeixementActiu", isEnviarConeixementActiu());
		model.addAttribute("isFavoritsPermes", isFavoritsPermes());
		model.addAttribute("isMostrarPermisosBustiaPermes", isMostrarPermisosBustiaPermes());
		model.addAttribute("isReenviarBustiaDefaultEntitatDisabled", isReenviarBustiaDefaultEntitatDisabled());
		model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
		model.addAttribute(
				"arbreUnitatsOrganitzatives",
				bustiaService.findArbreUnitatsOrganitzatives(
						entitatActual.getId(),
						true,
						false,
						true));
		model.addAttribute("registres", 
				registreService.findMultiple(
						entitatActual.getId(),
						this.getRegistresSeleccionats(request, sessionAttributeSeleccio),
						isAdmin));
	}

	private int getMaxLevelArbre() {
		String maxLevelStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.enviar.arbre.nivell");
		int maxLevel = maxLevelStr != null ? Integer.parseInt(maxLevelStr) : 1;
		return maxLevel;
	}
	
	private boolean isEnviarConeixementActiu() {
		String isEnviarConeixementStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.enviar.coneixement");
		return Boolean.parseBoolean(isEnviarConeixementStr);
	}
	
	private Object isReenviarBustiaDefaultEntitatDisabled() {
		String isReenviarBustiaDefaultEntitatDisabled = aplicacioService.propertyFindByNom("es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat");
		return Boolean.parseBoolean(isReenviarBustiaDefaultEntitatDisabled);
	}

	
	private boolean isFavoritsPermes() {
		String isFavoritsPermesStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.reenviar.favorits");
		return Boolean.parseBoolean(isFavoritsPermesStr);
	}

	private boolean isMostrarPermisosBustiaPermes() {
		String isMostrarPermisosBustiaPermesStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.reenviar.mostrar.permisos");
		return Boolean.parseBoolean(isMostrarPermisosBustiaPermesStr);
	}
	
	private boolean isPermesAssignarAnotacions() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.distribucio.assignar.anotacions"));
	}
	
	@RequestMapping(value = "/marcarProcessatMultiple/{rol}", method = RequestMethod.GET)
	public String marcarProcessatMultipleGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		String sessionAttributeSeleccio = "";
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
			isAdmin = true;
		}else {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_USER;
		}
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute("marcarProcessatCommand", command);
		model.addAttribute("registres", 
				registreService.findMultiple(
						getEntitatActualComprovantPermis(request, rol).getId(),
						this.getRegistresSeleccionats(request, sessionAttributeSeleccio),
						isAdmin));
		
		return "registreUserMarcarProcessat";
	}	
	
	@RequestMapping(value = "/marcarPendentMultiple/{rol}", method = RequestMethod.GET)
	public String marcarPendentMultipleGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		String sessionAttributeSeleccio = "";
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
			isAdmin = true;
		}else {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_USER;
		}
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute("marcarPendentCommand", command);
		model.addAttribute("registres", 
				registreService.findMultiple(
						getEntitatActualComprovantPermis(request, rol).getId(),
						this.getRegistresSeleccionats(request, sessionAttributeSeleccio),
						isAdmin));
		return "registreUserMarcarPendent";
	}
	
	
	@RequestMapping(value = "/enviarViaEmailMultiple/{rol}", method = RequestMethod.GET)
	public String enviarViaEmailMultipleGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		String sessionAttributeSeleccio = "";
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
			isAdmin = true;
		}else {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_USER;
		}
		RegistreEnviarViaEmailCommand command = new RegistreEnviarViaEmailCommand();
		model.addAttribute("registreEnviarViaEmailCommand", command);
		
		List<RegistreDto> registres;
		registres = registreService.findMultiple(
						getEntitatActual(request).getId(),
						this.getRegistresSeleccionats(request, sessionAttributeSeleccio),
						isAdmin);
		model.addAttribute("registres", registres);
		return "registreViaEmail";
	}
	
	
	@RequestMapping(value = "/enviarIProcessarMultiple/{rol}", method = RequestMethod.GET)
	public String enviarIProcessarMultipleGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		String sessionAttributeSeleccio = "";
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
			isAdmin = true;
		}else {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_USER;
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermis(request, rol);
		RegistreEnviarIProcessarCommand command = new RegistreEnviarIProcessarCommand();
		model.addAttribute("registres", 
				registreService.findMultiple(
						entitatActual.getId(),
						this.getRegistresSeleccionats(request, sessionAttributeSeleccio),
						isAdmin));
		model.addAttribute(command);
		return "registreUserEnviarIProcessar";
	}	
	
	
	/** Mètode per exportar la selecció d'anotacions de registre en format CSV o ODT */
	@RequestMapping(value="/exportar/{rol}", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			HttpServletResponse response, 
			Model model, 
			@RequestParam String format, 
			@PathVariable String rol) throws IllegalAccessException, NoSuchMethodException  {
		String sessionAttributeSeleccio = "";
		boolean isAdmin = false;
		if ("admin".equals(rol)) {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_ADMIN;
			isAdmin = true;
		}else {
			sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO_USER;
		}
		List<Long> registresSeleccionatsIds = this.getRegistresSeleccionats(request, sessionAttributeSeleccio);
		List<RegistreDto> llistatRegistres = new ArrayList<>();
		List<RegistreDto> registres = new ArrayList<>();
		if (registresSeleccionatsIds != null) {
			// Consulta de 1000 en 1000
			int i = 0;
			int total = registresSeleccionatsIds.size();
			while (i < total ) {
				registres = registreService.findMultiple(
						getEntitatActual(request).getId(),
						registresSeleccionatsIds.subList(i, Math.min(i+1000, total)), 
						isAdmin);
				llistatRegistres.addAll(registres);
				i += 1000;
			}
		}
		try {
			FitxerDto fitxer = registreHelper.exportarAnotacions(
										request, 
										response, 
										llistatRegistres, 
										format);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception e) {
			String errMsg = this.getMessage(request, "registre.user.accio.grup.exportar.error", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			return "redirect:" + request.getHeader("referer");
		}
		return null;
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreComunController.class);

}
