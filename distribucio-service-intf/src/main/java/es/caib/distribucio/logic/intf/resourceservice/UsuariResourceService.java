package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.model.UsuariResource;

/**
 * Definició del servei de consulta i modificació del perfil de l'usuari autenticat actual.
 *
 * @author Límit Tecnologies
 */
public interface UsuariResourceService extends MutableResourceService<UsuariResource, String> {

    /**
     * Refresca la informació de l'usuari autenticat.
     */
    void refresh();
}
