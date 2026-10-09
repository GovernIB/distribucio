package es.caib.distribucio.back.resourcecontroller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.model.sse.AvisosActiusEvent;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.logic.intf.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manté les connexions SSE (Server-Sent Events) obertes amb els navegadors i hi envia els
 * esdeveniments de l'aplicació.
 * <p/>
 * De moment l'únic esdeveniment és el dels avisos: en subscriure's, cada connexió rep els avisos
 * que li toquen, i quan la taula {@code dis_avis} canvia la capa de negoci ho publica a la cua JMS
 * {@code avisos} (veure {@code EventHelper}) i {@link #handleEventAvisos} els torna a enviar a
 * totes les connexions obertes.
 * <p/>
 * <b>Per què el rol i l'entitat arriben com a paràmetres:</b> els avisos que veu un usuari depenen
 * del rol amb què opera i de l'entitat de treball, i a la interfície REACT tots dos viatgen a les
 * capçaleres {@code X-App-Role} i {@code X-App-Session} de cada petició. Aquí no s'hi pot recórrer,
 * perquè l'EventSource del navegador no permet posar capçaleres a la petició de subscripció. Per
 * això el client els declara a la URL i el controlador els comprova un sol cop, en obrir la
 * connexió: el rol ha de ser un dels que té l'usuari i l'entitat, una de les que té accessibles.
 * A partir d'aquí queden lligats a aquella subscripció, que és el que permet servir cada pestanya
 * del navegador amb el seu propi rol i la seva pròpia entitat.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/sse")
@Tag(name = "SSE", description = "Servei d'esdeveniments SSE cap al navegador")
public class SseResourceController {

	/** Cua JMS on la capa de negoci publica que els avisos han canviat. */
	private static final String DESTINACIO_AVISOS = "avisos";
	/** Sense timeout: la connexió la tanca el client o un error d'enviament. */
	private static final long SENSE_TIMEOUT = 0L;

	private final EventService eventService;
	private final EntitatService entitatService;

	/**
	 * Subscripcions obertes per usuari. N'hi pot haver més d'una alhora -- una per pestanya del
	 * navegador-- i cadascuna pot estar operant amb un rol i una entitat diferents.
	 */
	private final Map<String, List<Subscripcio>> clientsUsuaris = new ConcurrentHashMap<>();

	/** Noms dels esdeveniments que reben els clients subscrits com a usuari. */
	private enum UserEventType {
		USER_CONNECT, AVISOS;
		public String getEventName() {
			return name().toLowerCase();
		}
	}

	/** Una connexió oberta, amb el rol i l'entitat amb què s'hi opera. */
	@Getter
	@RequiredArgsConstructor
	private static class Subscripcio {
		private final SseEmitter emitter;
		private final String rol;
		private final Long entitatId;
	}

	/**
	 * S U B S C R I P C I Ó
	 */

	/**
	 * Obre una connexió SSE per a l'usuari indicat.
	 *
	 * @param usuariCodi codi de l'usuari; ha de ser el de l'usuari autenticat.
	 * @param rol rol amb què opera l'usuari a la pestanya que es subscriu.
	 * @param entitatId entitat de treball de la pestanya que es subscriu.
	 * @return l'emissor de la connexió.
	 */
	@GetMapping("/subscribe/user/{usuariCodi}")
	public SseEmitter subscribeUsuari(
			@PathVariable String usuariCodi,
			@RequestParam(required = false) String rol,
			@RequestParam(required = false) Long entitatId) {
		Authentication auth = getAuthenticationOrThrow();
		comprovarUsuari(auth, usuariCodi);
		comprovarRol(auth, rol);
		comprovarEntitat(rol, entitatId);
		SseEmitter emitter = new SseEmitter(SENSE_TIMEOUT);
		Subscripcio subscripcio = new Subscripcio(emitter, rol, entitatId);
		// L'alta es fa dins del compute() perquè no s'hi pugui colar pel mig la neteja d'un altre
		// fil, que esborra l'entrada del mapa quan un usuari es queda sense cap subscripció.
		clientsUsuaris.compute(usuariCodi, (codi, subscripcions) -> {
			List<Subscripcio> resultat = subscripcions != null ? subscripcions : new CopyOnWriteArrayList<>();
			resultat.add(subscripcio);
			return resultat;
		});
		emitter.onCompletion(() -> baixaSubscripcio(usuariCodi, subscripcio));
		emitter.onTimeout(() -> baixaSubscripcio(usuariCodi, subscripcio));
		emitter.onError(ex -> baixaSubscripcio(usuariCodi, subscripcio));
		log.debug("Usuari {} subscrit a events amb rol {} i entitat {} (emissor {})",
				usuariCodi, rol, entitatId, emitter.hashCode());
		enviarEventsInicials(usuariCodi, subscripcio);
		return emitter;
	}

	/** Envia a una connexió acabada d'obrir la confirmació i l'estat actual dels avisos. */
	private void enviarEventsInicials(String usuariCodi, Subscripcio subscripcio) {
		try {
			subscripcio.getEmitter().send(SseEmitter.event().
					name(UserEventType.USER_CONNECT.getEventName()).
					data("Connexió establerta a " + LocalDateTime.now()).
					id(String.valueOf(System.currentTimeMillis())));
			subscripcio.getEmitter().send(avisosEvent(subscripcio));
		} catch (Exception ex) {
			log.error("Error enviant els esdeveniments inicials a l'usuari " + usuariCodi, ex);
			// El callback onError/onCompletion ja s'encarrega de treure la subscripció del mapa.
			subscripcio.getEmitter().completeWithError(ex);
		}
	}

	/**
	 * C O M P R O V A C I O N S   D ' A C C É S
	 */

	/**
	 * Les comprovacions llancen {@code AccessDeniedException} i no {@code ResponseStatusException}
	 * perquè és l'única que {@code ResourceGlobalExceptionHandler} tradueix a un 403: la resta
	 * cauen al seu tractament genèric i sortirien com un 500.
	 */
	private Authentication getAuthenticationOrThrow() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			throw new AccessDeniedException("Cal estar autenticat per a subscriure's als esdeveniments");
		}
		return auth;
	}

	/** Ningú no es pot subscriure als esdeveniments d'un altre usuari. */
	private void comprovarUsuari(Authentication auth, String usuariCodi) {
		if (usuariCodi == null || !usuariCodi.equals(auth.getName())) {
			throw new AccessDeniedException(
					"Només es pot obrir una connexió d'esdeveniments per a l'usuari autenticat");
		}
	}

	/**
	 * El rol declarat ha de ser un dels que té l'usuari. La petició de subscripció no du la
	 * capçalera del rol seleccionat, de manera que aquí les autoritats són totes les de l'usuari
	 * (veure {@code RolSeleccionatFilter}), que és justament amb el que s'ha de contrastar.
	 */
	private void comprovarRol(Authentication auth, String rol) {
		if (rol == null) {
			return;
		}
		boolean teElRol = auth.getAuthorities().stream().
				map(GrantedAuthority::getAuthority).
				anyMatch(rol::equals);
		if (!teElRol) {
			throw new AccessDeniedException("L'usuari no té el rol " + rol);
		}
	}

	/**
	 * L'entitat declarada ha de ser una de les accessibles per l'usuari. Amb el rol de superusuari
	 * no cal comprovar res perquè l'entitat no intervé en el filtre d'avisos (veure
	 * {@code EventHelper.getAvisosActius}).
	 */
	private void comprovarEntitat(String rol, Long entitatId) {
		if (entitatId == null || BaseConfig.ROLE_SUPER.equals(rol)) {
			return;
		}
		boolean accessible = entitatService.findAccessiblesUsuariActual().stream().
				map(EntitatDto::getId).
				anyMatch(entitatId::equals);
		if (!accessible) {
			throw new AccessDeniedException("L'usuari no té accés a l'entitat " + entitatId);
		}
	}

	/**
	 * R E C E P C I Ó   D ' E S D E V E N I M E N T S
	 */

	/**
	 * Els avisos han canviat: es tornen a consultar i a enviar a totes les connexions obertes.
	 * <p/>
	 * El missatge JMS arriba buit a posta: la llista d'avisos depèn del rol i de l'entitat de cada
	 * connexió, així que no hi ha cap llista única que es pugui enviar a tothom.
	 */
	@Async
	@JmsListener(destination = DESTINACIO_AVISOS)
	public void handleEventAvisos(AvisosActiusEvent event) {
		log.debug("Actualització dels avisos a les connexions obertes");
		enviarACadaSubscripcio((usuariCodi, subscripcio) -> subscripcio.getEmitter().send(avisosEvent(subscripcio)));
	}

	private SseEmitter.SseEventBuilder avisosEvent(Subscripcio subscripcio) {
		AvisosActiusEvent avisos = eventService.getAvisosActius(subscripcio.getRol(), subscripcio.getEntitatId());
		return SseEmitter.event().name(UserEventType.AVISOS.getEventName()).data(avisos);
	}

	/**
	 * P I N G   D E   M A N T E N I M E N T
	 */

	/**
	 * Manté vives les connexions obertes.
	 * <p/>
	 * Entre esdeveniment i esdeveniment pel canal no hi circula res, i qualsevol proxy intermedi
	 * acaba tallant la connexió pel seu timeout d'inactivitat (60 s amb la configuració per defecte
	 * d'Apache). El client ho detecta com un error, reconnecta al cap de 5 s i torna a demanar
	 * l'estat inicial: un cicle d'uns 65 s per pestanya oberta que no aporta res.
	 * <p/>
	 * S'envia un <b>comentari</b> SSE (":ping"), no un esdeveniment amb nom: el navegador l'ignora
	 * i no arriba a cap listener, de manera que la connexió es manté viva sense provocar cap
	 * repintat a REACT. De passada, aquest enviament periòdic és el que detecta els emissors morts
	 * -- altrament només es descobririen quan arribàs un esdeveniment de veritat-- i els dóna de baixa.
	 * <p/>
	 * L'interval es configura amb {@code es.caib.distribucio.sse.ping.interval} (en mil·lisegons);
	 * per defecte 20 s, que deixa marge per a perdre un parell de pings dins la finestra de 60 s.
	 */
	@Scheduled(fixedDelayString = "${es.caib.distribucio.sse.ping.interval:20000}")
	public void pingClientsSse() {
		try {
			// Un builder nou per a cada emissor: SseEventBuilder acumula estat a cada build(), i
			// reaprofitar-ne un afegiria salts de línia de més a cada enviament.
			enviarACadaSubscripcio((usuariCodi, subscripcio) ->
					subscripcio.getEmitter().send(SseEmitter.event().comment("ping")));
		} catch (Exception ex) {
			// Cap error no pot impedir que es programi la següent execució.
			log.error("Error enviant el ping de manteniment de les connexions SSE", ex);
		}
	}

	/**
	 * E N V I A M E N T   I   N E T E J A
	 */

	/** Aplica un enviament a totes les subscripcions obertes i dóna de baixa les que fallin. */
	private void enviarACadaSubscripcio(SubscripcioSender sender) {
		for (Map.Entry<String, List<Subscripcio>> entry : clientsUsuaris.entrySet()) {
			String usuariCodi = entry.getKey();
			List<Subscripcio> mortes = new ArrayList<>();
			for (Subscripcio subscripcio : entry.getValue()) {
				try {
					sender.send(usuariCodi, subscripcio);
				} catch (Exception ex) {
					mortes.add(subscripcio);
					log.debug("... donada de baixa la subscripció {} de l'usuari {} per error: {}",
							subscripcio.getEmitter().hashCode(), usuariCodi, ex.getMessage());
				}
			}
			mortes.forEach(morta -> baixaSubscripcio(usuariCodi, morta));
		}
	}

	/**
	 * Treu una subscripció del mapa i, si l'usuari es queda sense cap, també la seva entrada. Les
	 * dues coses dins del mateix {@code computeIfPresent} perquè no es pugui esborrar una entrada a
	 * la qual un altre fil acaba d'afegir una subscripció nova.
	 */
	private void baixaSubscripcio(String usuariCodi, Subscripcio subscripcio) {
		clientsUsuaris.computeIfPresent(usuariCodi, (codi, subscripcions) -> {
			subscripcions.remove(subscripcio);
			return subscripcions.isEmpty() ? null : subscripcions;
		});
	}

	@FunctionalInterface
	private interface SubscripcioSender {
		void send(String usuariCodi, Subscripcio subscripcio) throws Exception;
	}

}
