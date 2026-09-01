package es.caib.distribucio.back.controller;

import es.caib.distribucio.back.helper.EntitatHelper;
import es.caib.distribucio.back.helper.RolHelper;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.IdNomDto;
import es.caib.distribucio.logic.intf.dto.OpcionsPaginacio;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Opcions (entitats i bústies accessibles per a l'usuari actual, mides de pàgina) per a emplenar
 * els selectors del perfil de l'usuari a la interfície REACT (recurs {@code usuariResource}).
 * <p>
 * No formen part del motor genèric de recursos HAL-FORMS perquè els llistats depenen de l'usuari
 * autenticat actual (no de l'entitat administrada per {@code EntitatResource}, restringida al rol
 * {@code DIS_SUPER}) o, en el cas de les mides de pàgina, perquè el camp és numèric i el motor
 * genèric no en pot publicar les opcions (veure {@link #opcionsPaginacio()}).
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/usuariPreferencies")
@RequiredArgsConstructor
public class UsuariPreferenciesController {

	private final EntitatService entitatService;
	private final BustiaService bustiaService;
	private final AplicacioService aplicacioService;

	@GetMapping("/entitats")
	public List<EntitatDto> entitatsAccessibles() {
		return entitatService.findAccessiblesUsuariActual();
	}

	@GetMapping("/busties")
	public List<BustiaDto> bustiesAccessibles(@RequestParam Long entitatId) {
		return bustiaService.findBustiesPermesesPerUsuari(entitatId, false);
	}

	/**
	 * Mides de pàgina que pot triar l'usuari: les mateixes d'{@link OpcionsPaginacio} que ofereix
	 * el desplegable de la interfície JSP (veure {@code UsuariController} i {@code usuariForm.jsp}).
	 * <p>
	 * No es publiquen com a opcions del camp {@code numElementsPagina} amb
	 * {@code @ResourceField(enumType = true)} -- com es fa amb {@code idioma} -- perquè el motor
	 * genèric lliura els valors de les opcions com a text ({@code FieldOption.value} és un
	 * {@code String}) mentre que el camp és un {@code Long}: el desplegable no reconeixeria el
	 * valor desat (el compara amb {@code ===}) i sortiria buit.
	 */
	@GetMapping("/opcionsPaginacio")
	public List<IdNomDto> opcionsPaginacio() {
		return OpcionsPaginacio.toDtoList();
	}

	/**
	 * Estat (i, opcionalment, canvi) de l'entitat/rol actuals de la sessió, reutilitzant
	 * exactament la mateixa lògica que fa servir el decorador de la interfície JSP
	 * ({@link EntitatHelper}/{@link RolHelper}, tots dos basats en atributs de
	 * {@code HttpSession}) -- així el selector REACT i les pàgines JSP sempre reflecteixen la
	 * mateixa entitat/rol seleccionats, independentment de per on s'hagi fet el canvi.
	 * <p>
	 * Els paràmetres {@code canviEntitat}/{@code canviRol} (mateix nom que fa servir la JSP, veure
	 * {@link EntitatHelper#getRequestParameterCanviEntitat()}/
	 * {@link RolHelper#getRequestParameterCanviRol()}) són opcionals i es poden combinar amb la
	 * simple consulta de l'estat actual (petició GET, igual que l'enllaç
	 * {@code /index?canviEntitat=..}/{@code canviRol=..} de la JSP).
	 */
	@GetMapping("/entitatRolActual")
	public EntitatRolActualResource entitatRolActual(HttpServletRequest request) {
		EntitatHelper.processarCanviEntitats(request, entitatService);
		RolHelper.processarCanviRols(request, aplicacioService);
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
		return new EntitatRolActualResource(
				entitatActual != null ? entitatActual.getId() : null,
				RolHelper.getRolActual(request),
				RolHelper.getRolsUsuariActual(request));
	}

}
