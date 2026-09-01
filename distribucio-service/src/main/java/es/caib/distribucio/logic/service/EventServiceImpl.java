package es.caib.distribucio.logic.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.EventHelper;
import es.caib.distribucio.logic.intf.model.sse.AvisosActiusEvent;
import es.caib.distribucio.logic.intf.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'esdeveniments SSE.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventHelper eventHelper;

	@Override
	public void notifyAvisosActius() {
		eventHelper.notifyAvisosActius();
	}

	@Override
	@Transactional(readOnly = true)
	public AvisosActiusEvent getAvisosActius(String rol, Long entitatId) {
		return eventHelper.getAvisosActius(rol, entitatId);
	}

	/**
	 * Un avís s'activa i es desactiva tot sol en arribar la seva data d'inici o de final, sense que
	 * ningú no toqui la taula. Just passada la mitjanit es notifica el canvi perquè les pantalles
	 * obertes no hagin d'esperar a una recàrrega per a veure'l.
	 */
	@Scheduled(cron = "1 0 0 * * *")
	public void notifyAvisosActiusCron() {
		log.debug("Notificació dels avisos activats o desactivats per data");
		notifyAvisosActius();
	}

}
