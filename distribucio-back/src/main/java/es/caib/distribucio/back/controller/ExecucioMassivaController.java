package es.caib.distribucio.back.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import es.caib.distribucio.back.command.*;
import es.caib.distribucio.logic.helper.MessageHelper;
import es.caib.distribucio.logic.intf.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.back.helper.EnumHelper;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.back.helper.RolHelper;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ExecucioMassivaService;
import es.caib.distribucio.logic.intf.service.RegistreService;

/**
 * Controlador per al manteniment d'execucions massives
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiva")
public class ExecucioMassivaController extends BaseUserOAdminController {

	// TODO: Unificar cridada selecció registre?
	private static final String SESSION_ATTRIBUTE_SELECCIO_REGISTRES_ADMIN = "RegistreAdminController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_SELECCIO_REGISTRE_USER = "RegistreUserController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_SELECCIO_REGISTRE_MOVIMENTS = "RegistreUserController.session.seleccio.moviments";
	private static final String SESSION_ATTRIBUTE_SELECCIO_ANNEXOS_ADMIN = "AnnexosAdminController.session.seleccio";
	private static final String SESSION_COLLAPSE_SELECCIO = "ExecucioMassivaController.session.collapse.seleccio";
    private static final String SESSION_ATTRIBUTE_FILTRE = "ExecucioMassivaController.session.filtre";
	
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private AnnexosService annexosService;
	@Autowired
	private BustiaService bustiaService;
    @Autowired
    private MessageHelper messageHelper;

    private RegistreFiltreCommand getFiltreCommand(
            HttpServletRequest request) {
        RegistreFiltreCommand filtreCommand = (RegistreFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
                request,
                SESSION_ATTRIBUTE_FILTRE);
        if (filtreCommand == null) {
            filtreCommand = new RegistreFiltreCommand();
            RequestSessionHelper.actualitzarObjecteSessio(
                    request,
                    SESSION_ATTRIBUTE_FILTRE,
                    filtreCommand);
        }
        return filtreCommand;
    }

	// Consultes modal execució massiva //
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/consulta/{pagina}", method = RequestMethod.GET)
	public String getConsultaExecucions(
			HttpServletRequest request,
			@PathVariable int pagina,
			@RequestParam(value = "isRefrescant", required = false) boolean isRefrescant,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		pagina = (pagina < 0 ? 0 : pagina);
        RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
        model.addAttribute("remitentFiltreCommand", filtreCommand);
		UsuariDto usuariActual = null;
		if (RolHelper.isRolActualAdministrador(request)) {
            if (filtreCommand.getRemitent() != null)
                usuariActual = aplicacioService.findUsuariAmbCodi(filtreCommand.getRemitent());
			model.addAttribute(
					"titolConsulta",
					getMessage(request, "accio.massiva.consulta.titol.gobal"));
		} else {
			usuariActual = aplicacioService.getUsuariActual();
			model.addAttribute(
					"titolConsulta",
					getMessage(request, "accio.massiva.consulta.titol.usuari", new String[]{usuariActual.getNom()}));
		}
        List<ExecucioMassivaDto> execucionsMassives = execucioMassivaService.findExecucionsMassivesPerUsuari(entitatActual.getId(), usuariActual, pagina);
		if (execucionsMassives.size() < 8) {
			model.addAttribute("sumador", 0);
		} else {
			model.addAttribute("sumador", 1);
		}
		model.addAttribute("isRefrescant", isRefrescant);
		model.addAttribute("pagina",pagina);
		model.addAttribute("execucionsMassives", execucionsMassives);
		Object idsDesplegat = RequestSessionHelper.obtenirObjecteSessio(request, SESSION_COLLAPSE_SELECCIO);
		if (idsDesplegat!=null) {
			model.addAttribute("idsDesplegats", (List<Long>)idsDesplegat);
		}
		return "consultaExecucionsMassives";
	}

    @RequestMapping(value = "/consulta/{pagina}", method = RequestMethod.POST)
    public String consultaExecucionsPost(
            HttpServletRequest request,
            @PathVariable int pagina,
            @Valid RegistreFiltreCommand registreFiltreCommand,
            BindingResult bindingResult,
            @RequestParam(value = "accio", required = false) String accio,
            Model model) {
        if ("netejar".equals(accio)) {
            RequestSessionHelper.esborrarObjecteSessio(
                    request,
                    SESSION_ATTRIBUTE_FILTRE);
        } else {
            if (!bindingResult.hasErrors()) {
                RequestSessionHelper.actualitzarObjecteSessio(
                        request,
                        SESSION_ATTRIBUTE_FILTRE,
                        registreFiltreCommand);
            }
        }
        return this.getConsultaExecucions(request, pagina, false, model);
    }
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/consultaContingut/{execucioMassivaId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExecucioMassivaContingutDto> getConsultaContinguts(
			HttpServletRequest request,
			@PathVariable Long execucioMassivaId) {
		
		if (RolHelper.isRolActualUsuari(request)) {
			getEntitatActualComprovantPermisos(request);
		}

		Object idDesplegat = RequestSessionHelper.obtenirObjecteSessio(request, SESSION_COLLAPSE_SELECCIO);
		List<Long> idsDesplegats = new ArrayList<Long>();
		if (idDesplegat!=null) { idsDesplegats = (List<Long>)idDesplegat; }
		if (!idsDesplegats.contains(execucioMassivaId)) { idsDesplegats.add(execucioMassivaId); }
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_COLLAPSE_SELECCIO, idsDesplegats);

		return execucioMassivaService.findContingutPerExecucioMassiva(execucioMassivaId);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/unloadContingut/{execucioMassivaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean unloadContingutContinguts(
			HttpServletRequest request,
			@PathVariable Long execucioMassivaId) {

		Object idDesplegat = RequestSessionHelper.obtenirObjecteSessio(request, SESSION_COLLAPSE_SELECCIO);
		List<Long> idsDesplegats = new ArrayList<Long>();
		if (idDesplegat!=null) { idsDesplegats = (List<Long>)idDesplegat; }
		if (idsDesplegats.contains(execucioMassivaId)) { idsDesplegats.remove(execucioMassivaId); }
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_COLLAPSE_SELECCIO, idsDesplegats);

		return true;
	}
	
	@RequestMapping(value = "/cancelar/{execucioMassivaId}/{pagina}", method = RequestMethod.GET)
	public String cancelarExecucioMassiva(
			HttpServletRequest request,
			@PathVariable Long execucioMassivaId,
			@PathVariable Long pagina) {
		
		if (RolHelper.isRolActualUsuari(request)) {
			getEntitatActualComprovantPermisos(request);
		}

		try {
			execucioMassivaService.updateExecucioMassiva(ExecucioMassivaAccioDto.CANCELAR, execucioMassivaId);
			MissatgesHelper.success(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.cancelar.ok")
					);
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.cancelar.ko")
					);
		}
		
		return "redirect:/modal/massiva/consulta/" + pagina;
	}

	@RequestMapping(value = "/pausar/{execucioMassivaId}/{pagina}", method = RequestMethod.GET)
	public String pausarExecucioMassiva(
			HttpServletRequest request,
			@PathVariable Long execucioMassivaId,
			@PathVariable Long pagina) {
		
		if (RolHelper.isRolActualUsuari(request)) {
			getEntitatActualComprovantPermisos(request);
		}

		try {
			execucioMassivaService.updateExecucioMassiva(ExecucioMassivaAccioDto.PAUSAR, execucioMassivaId);
			MissatgesHelper.success(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.pausar.ok")
					);
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.pausar.ko")
					);
		}
		
		return "redirect:/modal/massiva/consulta/" + pagina;
	}
	
	@RequestMapping(value = "/reprendre/{execucioMassivaId}/{pagina}", method = RequestMethod.GET)
	public String reprendreExecucioMassiva(
			HttpServletRequest request,
			@PathVariable Long execucioMassivaId,
			@PathVariable Long pagina) {
		
		if (RolHelper.isRolActualUsuari(request)) {
			getEntitatActualComprovantPermisos(request);
		}

		try {
			execucioMassivaService.updateExecucioMassiva(ExecucioMassivaAccioDto.REPRENDRE, execucioMassivaId);
			MissatgesHelper.success(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.reprendre.ok")
					);
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.reprendre.ko")
					);
		}
		
		return "redirect:/modal/massiva/consulta/" + pagina;
	}
	
	//#### Accions massives disponibles
	
	@RequestMapping(value = "/classificar/{rol}", method = RequestMethod.GET)
	public String registreClassificarGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, rol, false);
		
		if (registresSeleccionats != null && !registresSeleccionats.isEmpty()) {
			boolean mateixPare = emplenarModelClassificarMultiple(
					request,
					registresSeleccionats,
					model, 
					rol);
			if (mateixPare) {
				String redireccio = comprovarExistenciaExecucioMassivaPendent(
						request, 
						"redirect:../registreUser", 
						registresSeleccionats);
				
				if (redireccio != null)
					return redireccio;
				
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

	@RequestMapping(value = "/classificar/{rol}", method = RequestMethod.POST)
	public String registreClassificarPost(
			HttpServletRequest request,
			@PathVariable String rol,
			@Valid RegistreClassificarCommand command,
			Model model) {
		String titol = command.getTitol();
		String codiProcediment = command.getCodiProcediment();
		String codiServei = command.getCodiServei();
		
		if (command.getTipus() == null || !command.getTipus().equals(RegistreClassificarTipusEnum.PROCEDIMENT.name())) {
			codiProcediment = null;
		}
		if (command.getTipus() == null || !command.getTipus().equals(RegistreClassificarTipusEnum.SERVEI.name())) {
			codiServei = null;
		}
		
		try {
			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, rol, false);
			
			Map<String, Object> params = new HashMap<>();
		    params.put("titol", titol);
		    params.put("codiProcediment", codiProcediment);
		    params.put("codiServei", codiServei);
		    
			return executarAccioMassivaRegistres(
					request, 
					rol, 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.CLASSIFICAR, 
					params, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}
		
		return "registreClassificarMultiple";
	}
	
	@RequestMapping(value = "/reenviar/{rol}", method = RequestMethod.GET)
	public String registreReenviarGet(
			HttpServletRequest request,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model, 
			@PathVariable String rol) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermis(request, rol);
			List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, rol, isVistaMoviments);

			String redireccio = comprovarExistenciaExecucioMassivaPendent(
					request, 
					"redirect:../registreUser", 
					registresSeleccionats);
			
			if (redireccio != null)
				return redireccio;
			
			omplirModelPerReenviarMultiple(
					request, 
					entitatActual, 
					model, 
					registresSeleccionats, 
					rol);
			
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
	
	@RequestMapping(value = "/reenviar/{rol}", method = RequestMethod.POST)
	public String registreReenviarPost(
			HttpServletRequest request,
			@PathVariable String rol,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@Valid ContingutReenviarMassiveCommand command,
			BindingResult bindingResult,
			Model model
			) {
		if (bindingResult.hasErrors()) {
            if (command.getDestins() == null || command.getDestins().length <= 0) {
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "registre.user.controller.massiva.no.desti"));
                model.addAttribute("maxLevel", getMaxLevelArbre());
                model.addAttribute("isReenviarBustiaDefaultEntitatDisabled", isReenviarBustiaDefaultEntitatDisabled());
                model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
            } else {
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "processamentMultiple.error.validacio"));
            }

			return registreReenviarGet(request, isVistaMoviments, model, rol);
		}

		try {
//			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, rol, isVistaMoviments);
            EntitatDto entitatActual = getEntitatActualComprovantPermis(request, "admin");

            List<RegistreDto> registresSeleccionats = registreService.findMultiple(
                    entitatActual.getId(),
                    command.getIds(),
                    "admin".equals(rol));
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("isVistaMoviments", isVistaMoviments);
			params.put("destins", command.getDestins());
			params.put("destinsUsuari", command.getDestinsUsuari());
			params.put("deixarCopia", command.isDeixarCopia());
			params.put("comentari", command.getComentariEnviar());
			params.put("perConeixement", command.getPerConeixement());
		    
			return executarAccioMassivaRegistres(
					request, 
					rol, 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.REENVIAR, 
					params, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}

        return registreReenviarGet(request, isVistaMoviments, model, rol);
	}
	

	@RequestMapping(value = "/marcarProcessat/{rol}", method = RequestMethod.GET)
	public String registreMarcarProcessatGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, rol, false);
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, rol, false);
		
		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../registreUser", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;
		
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute("marcarProcessatCommand", command);
		model.addAttribute("registres", registres);
		
		return "registreUserMarcarProcessat";
	}
	
	@RequestMapping(value = "/marcarProcessat/{rol}", method = RequestMethod.POST)
	public String registreMarcarProcessatPost(
			HttpServletRequest request,
			@PathVariable String rol,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		
		try {
			if (bindingResult.hasErrors()) {
				omplirModelAmbRegistres(request, rol, model);
				return "registreUserMarcarProcessat";
			}
			
			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, rol, false);
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("motiu", command.getMotiu());
		    
			return executarAccioMassivaRegistres(
					request, 
					rol, 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.MARCAR_PROCESSAT, 
					params, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}
	
		return "registreUserMarcarProcessat";
	}
	
	@RequestMapping(value = "/marcarPendent/{rol}", method = RequestMethod.GET)
	public String registreMarcarPendentGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, rol, false);
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, rol, false);
		
		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../registreUser", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;
		
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute("marcarPendentCommand", command);
		model.addAttribute("registres", registres);
		
		return "registreUserMarcarPendent";
	}
	
	@RequestMapping(value = "/marcarPendent/{rol}", method = RequestMethod.POST)
	public String registreMarcarPendentPost(
			HttpServletRequest request,
			@PathVariable String rol,
			@Valid @ModelAttribute("marcarPendentCommand") MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		
		try {
			if (bindingResult.hasErrors()) {
				omplirModelAmbRegistres(request, rol, model);
				return "registreUserMarcarPendent";
			}
			
			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, rol, false);
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("motiu", command.getMotiu());
		    
			return executarAccioMassivaRegistres(
					request, 
					rol, 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.MARCAR_PENDENT, 
					params, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}
		
		return "registreUserMarcarPendent";
	}
	
	@RequestMapping(value = "/enviarViaEmail/{rol}", method = RequestMethod.GET)
	public String registreEnviarViaEmailGet(
			HttpServletRequest request,
			Model model, 
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@PathVariable String rol) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, rol, isVistaMoviments);
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, rol, isVistaMoviments);

		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../registreUser", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;
		
		RegistreEnviarViaEmailCommand command = new RegistreEnviarViaEmailCommand();
		model.addAttribute("registreEnviarViaEmailCommand", command);
		model.addAttribute("registres", registres);
		return "registreViaEmail";
	}
	
	@RequestMapping(value = "/enviarViaEmail/{rol}", method = RequestMethod.POST)
	public String registreEnviarViaEmailPost(
			HttpServletRequest request,
			@PathVariable String rol,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@Valid RegistreEnviarViaEmailCommand command,
			BindingResult bindingResult,
			Model model) {
		
		try {
			if (bindingResult.hasErrors()) {
				omplirModelAmbRegistres(request, rol, model);
				return "registreViaEmail";
			}
			
			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, rol, isVistaMoviments);
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("isVistaMoviments", isVistaMoviments);
			params.put("destinataris", command.getAddresses());
			params.put("motiu", command.getMotiu());
		    
			return executarAccioMassivaRegistres(
					request, 
					rol, 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.ENVIAR_VIA_EMAIL, 
					params, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}
		
		return "registreViaEmail";
	}
	
	@RequestMapping(value = "/enviarIProcessar/{rol}", method = RequestMethod.GET)
	public String registreEnviarIProcessarGet(
			HttpServletRequest request,
			Model model, 
			@PathVariable String rol) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, rol, false);
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, rol, false);
		
		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../registreUser", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;
		
		RegistreEnviarIProcessarCommand command = new RegistreEnviarIProcessarCommand();
		model.addAttribute("registres", registres);
        model.addAttribute("registresAdvertencies", this.getAdvertencies(registres));
		model.addAttribute(command);
		return "registreUserEnviarIProcessar";
	}
	
	@RequestMapping(value = "/enviarIProcessar/{rol}", method = RequestMethod.POST)
	public String registreEnviarIProcessarPost(
			HttpServletRequest request,
			@PathVariable String rol,
			@Valid RegistreEnviarIProcessarMassiveCommand command,
			BindingResult bindingResult,
			Model model) {
		
		try {
			if (bindingResult.hasErrors()) {
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "processamentMultiple.error.validacio"));
				return registreEnviarIProcessarGet(request, model, rol);
			}
			
//			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, rol, false);
            EntitatDto entitatActual = getEntitatActualComprovantPermis(request, "admin");

            List<RegistreDto> registresSeleccionats = registreService.findMultiple(
                    entitatActual.getId(),
                    command.getIds(),
                    "admin".equals(rol));
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("destinataris", command.getAddresses());
			params.put("motiu", command.getMotiu());
		    
			return executarAccioMassivaRegistres(
					request, 
					rol, 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.ENVIAR_VIA_EMAIL_PROCESSAR, 
					params, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}

        return registreEnviarIProcessarGet(request, model, rol);
	}

	@RequestMapping(value = "/reintentarProcessament", method = RequestMethod.GET)
	public String registreReintentarProcessamentGet(
			HttpServletRequest request,
			Model model) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, "admin", false);
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, "admin", false);
		
		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../registreUser", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;

		MassiveCommand command = new MassiveCommand();
		model.addAttribute("registres", registres);
		model.addAttribute("registresAdvertencies", this.getAdvertencies(registres));
		model.addAttribute("reintentarProcessamentCommand", command);
		
		return "reintentarProcessamentMultiple";
	}
	
	@RequestMapping(value = "/reintentarProcessament", method = RequestMethod.POST)
	public String registreReintentarProcessamentPost(
			HttpServletRequest request,
            @Valid MassiveCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"processamentMultiple.error.validacio"));
			return "redirect:reintentarProcessament";
		}
		
		try {
//			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, "admin", false);
            EntitatDto entitatActual = getEntitatActualComprovantPermis(request, "admin");

            List<RegistreDto> registresSeleccionats = registreService.findMultiple(
                    entitatActual.getId(),
                    command.getIds(),
                    true);

			return executarAccioMassivaRegistres(
					request,
					"admin",
					registresSeleccionats,
					ExecucioMassivaTipusDto.PROCESSAR,
					null,
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}

        return "redirect:reintentarProcessament";
	}
	
	@RequestMapping(value = "/reintentarEnviamentBackoffice", method = RequestMethod.GET)
	public String registreReintentarEnviamentBackofficeGet(
			HttpServletRequest request,
			Model model) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, "admin", false);
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, "admin", false);
		
		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../registreUser", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;
		
		Object command = new Object();
		model.addAttribute("registres", registres);
		model.addAttribute("reintentarProcessamentCommand", command);
		
		return "reintentarEnviamentBackofficeMultiple";
	}
	
	@RequestMapping(value = "/reintentarEnviamentBackoffice", method = RequestMethod.POST)
	public String registreReintentarEnviamentBackofficePost(
			HttpServletRequest request, 
			@Valid Object command, 
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"enviamentMultiple.error.validacio"));
			return "reintentarEnviamentBackofficeMultiple";
		}
		
		try {
			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, "admin", false);
			
			return executarAccioMassivaRegistres(
					request, 
					"admin", 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.BACKOFFICE, 
					null, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}
		
		return "reintentarEnviamentBackofficeMultiple";
	}
	
	@RequestMapping(value = "/marcarSobreescriure", method = RequestMethod.GET)
	public String registreMarcarSobreescriureGet(
			HttpServletRequest request,
			Model model) {
		List<Long> registresSeleccionats = obtenirIdsSeleccioRegistres(request, "admin", false);
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, "admin", false);
		
		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../registreUser", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;
		
		Object command = new Object();
		model.addAttribute("registres", registres);
		model.addAttribute("reintentarProcessamentCommand", command);
		
		return "marcarSobreescriure";
	}
	
	@RequestMapping(value = "/marcarSobreescriure", method = RequestMethod.POST)
	public String registreMarcarSobreescriurePost(
			HttpServletRequest request, 
			@Valid Object command, 
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"processamentMultiple.error.validacio"));
			return "marcarSobreescriure";
		}
		
		try {
			List<RegistreDto> registresSeleccionats = obtenirSeleccioRegistres(request, "admin", false);
			
			return executarAccioMassivaRegistres(
					request, 
					"admin", 
					registresSeleccionats, 
					ExecucioMassivaTipusDto.SOBREESCRIURE, 
					null, 
					"redirect:../registreUser");
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}
		
		return "marcarSobreescriure";
	}
	
	@RequestMapping(value = "/guardarDefinitiu", method = RequestMethod.GET)
	public String annexGuardarDefinitiuGet(
			HttpServletRequest request,
			Model model) {
		Object command = new Object();
		List<Long> registresSeleccionats = this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO_ANNEXOS_ADMIN);
		
		String redireccio = comprovarExistenciaExecucioMassivaPendent(
				request, 
				"redirect:../annexosAdmin", 
				registresSeleccionats);
		
		if (redireccio != null)
			return redireccio;
		
		model.addAttribute("processamentAnnexosMultiple", command);
		model.addAttribute("annexos", obtenirSeleccioAnnexos(request));
		
		return "processamentAnnexosMultiple";
	}
	
	@RequestMapping(value = "/guardarDefinitiu", method = RequestMethod.POST)
	public String annexGuardarDefinitiu(
			HttpServletRequest request,
			Model model) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			List<RegistreAnnexDto> annexosSeleccionats = obtenirSeleccioAnnexos(request);
			
			if (! annexosSeleccionats.isEmpty()) {
				crearExecucioMassivaAnnexos(
						request, 
						annexosSeleccionats, 
						entitatActual, 
						ExecucioMassivaTipusDto.CUSTODIAR, 
						null);
			} else {
				return getModalControllerReturnValueError(
						request,
						"redirect:../annexosAdmin",
						"accio.massiva.controller.annex.seleccio.buida");
			}
	
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"accio.massiva.controller.accion.crear.ok")
					);
	
			return "redirect:/modal/massiva/consulta/0";
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request,
							"accio.massiva.controller.accion.crear.ko")
					);
		}
		
		return "processamentAnnexosMultiple";
	}

	//Private methods//

    private HashMap<Long, String> getAdvertencies(List<RegistreDto> registres) {
        HashMap<Long, String> registresAdvertencies = new HashMap<>();
        registresAdvertencies.put(0L, messageHelper.getMessage("historic.taula.header.estats.error"));
        registres.forEach(registre -> {
            switch (registre.getProcesEstat()) {
                case ARXIU_PENDENT:
                    registresAdvertencies.put(registre.getId(), messageHelper.getMessage("registre.proces.estat.enum.ARXIU_PENDENT"));
                    break;
                case REGLA_PENDENT:
                    registresAdvertencies.put(registre.getId(), messageHelper.getMessage("registre.proces.estat.enum.REGLA_PENDENT"));
                    break;
                case BUSTIA_PROCESSADA:
                    registresAdvertencies.put(registre.getId(), messageHelper.getMessage("registre.proces.estat.enum.BUSTIA_PROCESSADA"));
                    break;
                case BACK_PROCESSADA:
                    registresAdvertencies.put(registre.getId(), messageHelper.getMessage("registre.proces.estat.enum.BACK_PROCESSADA"));
                    break;
            }
        });
        return registresAdvertencies;
    }
	
	private void crearExecucioMassivaRegistres(
			HttpServletRequest request, 
			List<RegistreDto> registres, 
			EntitatDto entitatActual,
			ExecucioMassivaTipusDto tipus,
			String parametres) {
		List<ExecucioMassivaContingutDto> continguts = new ArrayList<ExecucioMassivaContingutDto>();
		for (RegistreDto registre : registres) {
			ExecucioMassivaContingutDto contingut = new ExecucioMassivaContingutDto();
			contingut.setDataCreacio(new Date());
			contingut.setDataInici(new Date());
			contingut.setElementId(registre.getId());
			contingut.setElementNom(registre.getNom());
			contingut.setElementTipus(ElementTipusEnumDto.REGISTRE);
			continguts.add(contingut);
		}
		
		ExecucioMassivaDto execucioMassiva = new ExecucioMassivaDto();
		execucioMassiva.setDataCreacio(new Date());
		execucioMassiva.setDataInici(new Date());
		execucioMassiva.setTipus(tipus);
		execucioMassiva.setContinguts(continguts);
		execucioMassiva.setParametres(parametres);
		
		execucioMassivaService.crearExecucioMassiva(
				entitatActual.getId(), 
				execucioMassiva);
	}
	
	private void crearExecucioMassivaAnnexos(
			HttpServletRequest request, 
			List<RegistreAnnexDto> annexosSeleccionats, 
			EntitatDto entitatActual,
			ExecucioMassivaTipusDto tipus,
			String parametres) {
		List<ExecucioMassivaContingutDto> continguts = new ArrayList<ExecucioMassivaContingutDto>();
		for (RegistreAnnexDto annex : annexosSeleccionats) {
			ExecucioMassivaContingutDto contingut = new ExecucioMassivaContingutDto();
			contingut.setDataCreacio(new Date());
			contingut.setElementId(annex.getId());
			contingut.setElementNom(annex.getFitxerNom());
			contingut.setElementTipus(ElementTipusEnumDto.ANNEX);
			continguts.add(contingut);
		}
		
		ExecucioMassivaDto execucioMassiva = new ExecucioMassivaDto();
		execucioMassiva.setDataCreacio(new Date());
		execucioMassiva.setTipus(tipus);
		execucioMassiva.setContinguts(continguts);
		execucioMassiva.setParametres(parametres);
		
		execucioMassivaService.crearExecucioMassiva(
				entitatActual.getId(), 
				execucioMassiva);
	}
	
	private String comprovarExistenciaExecucioMassivaPendent(HttpServletRequest request, String redireccio, List<Long> seleccio) {
		List<String> elementsNom = execucioMassivaService.findElementNomExecucioPerContingut(seleccio);
		
		if (elementsNom != null && !elementsNom.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					redireccio,
					"accio.massiva.controller.comprovacio.duplicat",
					new Object[] {elementsNom});
		}
		
		return null;
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
		model.addAttribute(
				"tipus",
				EnumHelper.getOptionsForEnum(
						RegistreClassificarTipusEnum.class,
						"registre.classificar.tipus.enum."));
		if (mateixPare && bustiaIdActual != null) {
			model.addAttribute(
					"procediments",
					registreService.classificarFindProcediments(
							entitatActual.getId(),
							bustiaIdActual));
			model.addAttribute(
					"serveis",
					registreService.classificarFindServeis(
							entitatActual.getId(),
							bustiaIdActual));
		}
		model.addAttribute("rol", rol);
		return mateixPare;
	}
	
	private void omplirModelPerReenviarMultiple(
			HttpServletRequest request, 
			EntitatDto entitatActual,
			Model model, 
			List<Long> registresSeleccionats,
			String rol) {
		List<BustiaDto> busties = bustiaService.findActivesAmbEntitat(
				entitatActual.getId());
		
		boolean disableDeixarCopia = true;

		model.addAttribute(
				"selectMultiple",
				false);
		model.addAttribute("rol", rol);
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
        List<RegistreDto> registres = registreService.findMultiple(
                entitatActual.getId(),
                registresSeleccionats,
                "admin".equals(rol));
        model.addAttribute("registres", registres);
        model.addAttribute("registresAdvertencies", this.getAdvertencies(registres));
	}
	
	private String executarAccioMassivaRegistres(
	        HttpServletRequest request,
	        String rol,
	        List<RegistreDto> registresSeleccionats,
	        ExecucioMassivaTipusDto tipus,
	        Map<String, Object> params,
	        String vistaError) {

	    EntitatDto entitatActual = getEntitatActualComprovantPermis(request, rol);

	    List<RegistreDto> registresModificables = registresSeleccionats.stream()
	            .filter(RegistreDto::isPotModificar)
	            .collect(Collectors.toList());

	    if (registresSeleccionats.isEmpty()) {
	        return getModalControllerReturnValueError(
	                request,
	                vistaError,
	                "accio.massiva.controller.registre.seleccio.buida");
	    }

	    if (registresModificables.isEmpty()) {
	        return getModalControllerReturnValueError(
	                request,
	                vistaError,
	                "accio.massiva.controller.error.permis.bustia.descripcio");
	    }

	    crearExecucioMassivaRegistres(
	            request,
	            registresModificables,
	            entitatActual,
	            tipus,
	            construirParametres(params)
	    );

	    MissatgesHelper.success(request, getMessage(request, "accio.massiva.controller.accion.crear.ok"));
	    return "redirect:/modal/massiva/consulta/0";
	}


	private void omplirModelAmbRegistres(HttpServletRequest request, String rol, Model model) {
		List<RegistreDto> registres = obtenirSeleccioRegistres(request, rol, false);
		model.addAttribute("registres", registres);
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
	
	private List<RegistreDto> obtenirSeleccioRegistres(
			HttpServletRequest request, 
			String rol,
			boolean isVistaMoviments) {
		var entitatActual = getEntitatActualComprovantPermis(request, rol);
	    var isAdmin = "admin".equals(rol);
	    List<Long> registresSeleccionats;

	    if (isVistaMoviments) {
	        registresSeleccionats = getRegistresSeleccionatsMoviments(request);
	    } else {
	        String sessionAttribute = isAdmin 
	            ? SESSION_ATTRIBUTE_SELECCIO_REGISTRES_ADMIN 
	            : SESSION_ATTRIBUTE_SELECCIO_REGISTRE_USER;
	        registresSeleccionats = getRegistresSeleccionats(request, sessionAttribute);
	    }

	    return registreService.findMultiple(
	            entitatActual.getId(),
	            registresSeleccionats,
	            isAdmin);
	}
	
	private List<Long> obtenirIdsSeleccioRegistres(
			HttpServletRequest request, 
			String rol,
			boolean isVistaMoviments) {
		
		if (isVistaMoviments) {
			return getRegistresSeleccionatsMoviments(request);
		}

		String sessionAttribute = "admin".equals(rol) 
				? SESSION_ATTRIBUTE_SELECCIO_REGISTRES_ADMIN
				: SESSION_ATTRIBUTE_SELECCIO_REGISTRE_USER;

		return getRegistresSeleccionats(request, sessionAttribute);
	}
	
	private List<RegistreAnnexDto> obtenirSeleccioAnnexos(HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		List<Long> registresSeleccionats = this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO_ANNEXOS_ADMIN);

		return annexosService.findMultiple(
				entitatActual.getId(),
				registresSeleccionats,
				true);
	}
	
	private List<Long> getRegistresSeleccionatsMoviments(HttpServletRequest request) {
		Set<Long> seleccio = new HashSet<Long>();
//		## ID = ID_REGISTRE + ID_DESTI (extreure registre)
		@SuppressWarnings("unchecked")
		Set<String> seleccioMoviments = (Set<String>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_REGISTRE_MOVIMENTS);
		if (seleccioMoviments != null && !seleccioMoviments.isEmpty()) {
			for (String idVistaMoviment: seleccioMoviments) {
				seleccio.add(Long.valueOf(idVistaMoviment.split("_")[0]));
			}
		}
		
		return new ArrayList<Long>(seleccio);
	}
	
	private String construirParametres(Map<String, Object> params) {
	    ObjectMapper mapper = new ObjectMapper();
	    try {
	        return mapper.writeValueAsString(params);
	    } catch (JsonProcessingException e) {
	        throw new RuntimeException("Error al convertir els paràmetres a JSON", e);
	    }
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExecucioMassivaController.class);


}
