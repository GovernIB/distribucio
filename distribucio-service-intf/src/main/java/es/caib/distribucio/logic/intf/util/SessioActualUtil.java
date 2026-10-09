package es.caib.distribucio.logic.intf.util;

import es.caib.distribucio.logic.intf.base.util.RequestSessionUtil;
import es.caib.distribucio.logic.intf.model.UserSession;

/** Accés a la sessió de la petició actual de la interfície REACT.
 *
 * El front envia l'entitat seleccionada a cada petició amb la capçalera {@code X-App-Session};
 * {@code WebMvcConfig.userSessionInterceptor()} la converteix en un {@link UserSession} i el
 * desa al ThreadLocal de {@link RequestSessionUtil}. Aquesta classe és el punt únic per a
 * llegir-lo des dels serveis.
 *
 * {@link RequestSessionUtil} és codi de base-boot i tipa la sessió com a {@code Object} a
 * propòsit, perquè no coneix el model de cada aplicació. Per això la conversió a
 * {@link UserSession} viu aquí i no allà: qualsevol mètode afegit a base-boot es perdria en la
 * propera sincronització i, a més, hi introduiria una dependència cap a codi d'aplicació.
 */
public class SessioActualUtil {

	/** Identificador de l'entitat seleccionada al selector de la capçalera, o null si la
	 * petició no duu capçalera de sessió (p. ex. amb el rol DIS_SUPER, que no en selecciona cap,
	 * o des de la interfície JSP clàssica). Compte: només retorna l'identificador; les
	 * propietats per entitat i els plugins llegeixen el codi d'entitat del ThreadLocal de
	 * ConfigHelper, que és independent d'aquest. */
	public static Long getEntitatId() {
		Object session = RequestSessionUtil.getRequestSession();
		if (session instanceof UserSession) {
			return ((UserSession) session).getEntitatId();
		}
		return null;
	}

}
