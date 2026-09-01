package es.caib.distribucio.logic.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.sse.AvisSseDto;
import es.caib.distribucio.logic.intf.model.sse.AvisosActiusEvent;
import es.caib.distribucio.persist.entity.AvisEntity;
import es.caib.distribucio.persist.repository.AvisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Lògica dels esdeveniments que s'envien als clients per SSE.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventHelper {

	/** Cua on es publica que els avisos han canviat. */
	public static final String DESTINACIO_AVISOS = "avisos";

	private final JmsTemplate jmsTemplate;
	private final AvisRepository avisRepository;

	/**
	 * Publica que els avisos actius han canviat.
	 * <p/>
	 * El missatge viatja buit: qui el rep torna a consultar els avisos de cada connexió oberta,
	 * perquè el resultat depèn del rol i de l'entitat de cadascuna. Per això l'enviament es
	 * difereix al commit de la transacció: si es publicàs amb la transacció encara oberta, la
	 * consulta del consumidor podria llegir l'estat anterior al canvi.
	 */
	public void notifyAvisosActius() {
		TransactionAfterCommitUtils.run(() -> {
			try {
				log.debug("Notificant als clients que els avisos actius han canviat");
				jmsTemplate.convertAndSend(DESTINACIO_AVISOS, new AvisosActiusEvent(null));
			} catch (Exception ex) {
				// Que no es pugui notificar no pot fer fallar l'operació que ha canviat l'avís:
				// com a molt els clients connectats no el veuran fins que recarreguin la pàgina.
				log.error("Error notificant als clients que els avisos actius han canviat", ex);
			}
		});
	}

	/**
	 * Avisos actius que ha de veure un usuari que opera amb el rol i l'entitat indicats.
	 * <p/>
	 * Mateix criteri que {@code AvisHelper.findAvisos} de la interfície JSP: amb el rol de
	 * superusuari, tots els avisos actius; amb qualsevol altre rol, els globals (sense entitat) i
	 * els de l'entitat de treball.
	 */
	@Transactional(readOnly = true)
	public AvisosActiusEvent getAvisosActius(String rol, Long entitatId) {
		List<AvisEntity> actius = avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE));
		boolean superusuari = BaseConfig.ROLE_SUPER.equals(rol);
		List<AvisSseDto> avisos = actius.stream().
				filter(avis -> superusuari
						|| avis.getEntitatId() == null
						|| avis.getEntitatId().equals(entitatId)).
				map(this::toAvisSseDto).
				collect(Collectors.toList());
		return new AvisosActiusEvent(avisos);
	}

	private AvisSseDto toAvisSseDto(AvisEntity avis) {
		return new AvisSseDto(
				avis.getId(),
				avis.getAssumpte(),
				avis.getMissatge(),
				avis.getAvisNivell(),
				avis.getDataInici(),
				avis.getDataFinal());
	}

}
