package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.UsuariLlistatResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Servei REST de consulta del llistat d'usuaris de l'aplicació (per a desplegables d'usuaris).
 * El perfil de l'usuari autenticat és a {@link UsuariResourceController}.
 * <p>
 * Hereta del controlador mutable perquè el {@code HalFormsConfig} de base-boot falla amb
 * controladors de només lectura; les escriptures queden prohibides pels permisos del recurs.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequestMapping(BaseConfig.API_PATH + "/usuarisLlistat")
public class UsuariLlistatResourceController extends BaseMutableResourceController<UsuariLlistatResource, String> {

}
