package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.model.UsuariLlistatResource;

/**
 * Definició del servei de consulta del llistat d'usuaris de l'aplicació.
 * <p>
 * És un servei mutable només per seguir la convenció de la resta de recursos: el
 * {@code HalFormsConfig} de base-boot falla amb controladors de només lectura. El recurs només
 * concedeix permís de lectura i l'entitat és immutable.
 *
 * @author Límit Tecnologies
 */
public interface UsuariLlistatResourceService extends MutableResourceService<UsuariLlistatResource, String> {}
