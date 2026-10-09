package es.caib.distribucio.ejb;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

import es.caib.distribucio.logic.intf.model.sse.AvisosActiusEvent;

/**
 * Implementació d'EventService com a EJB que empra una classe delegada per a accedir a la
 * funcionalitat del servei.
 * <p/>
 * {@code @PermitAll} perquè {@link #getAvisosActius(String, Long)} es crida des del fil del
 * {@code @JmsListener} de {@code SseResourceController}, que no ve de cap petició web i per tant
 * no té cap usuari autenticat: amb {@code @RolesAllowed} el contenidor rebutjaria la crida. El
 * mètode només llegeix avisos, i qui es pot subscriure -- i amb quin rol i entitat-- ja s'ha
 * comprovat en obrir la connexió SSE.
 *
 * @author Límit Tecnologies
 */
@Stateless
@PermitAll
public class EventService extends AbstractService<es.caib.distribucio.logic.intf.service.EventService> implements es.caib.distribucio.logic.intf.service.EventService {

	@Override
	public void notifyAvisosActius() {
		getDelegateService().notifyAvisosActius();
	}

	@Override
	public AvisosActiusEvent getAvisosActius(String rol, Long entitatId) {
		return getDelegateService().getAvisosActius(rol, entitatId);
	}

}
