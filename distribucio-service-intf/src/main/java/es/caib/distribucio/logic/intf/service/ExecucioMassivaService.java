/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.List;

import javax.annotation.security.PermitAll;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaAccioDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

/**
 * Servei per gestionar les execucions massives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaService {
	
	/**
	 * Crea una nova execució massiva
	 * 
	 * @param dto
	 *            Dto amb la informació de l'execució massiva a programar
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException;
	
	/**
	 * Cancel·la una nova execució massiva
	 * 
	 * @param exm_id
	 *            Id de l'execució massiva a cancel·lar
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public void updateExecucioMassiva(ExecucioMassivaAccioDto accio, Long exm_id) throws NotFoundException;

	/**
	 * Recuperar les execucions massives per usuari actual
	 * 
	 * @param entitatId
	 * 				ID de l'entitat actual
	 * @param usuari
	 * 				DTO de l'usuari actual
	 * @param paginacioParams
	 * 				Paràmetres paginació
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException;

	/**
	 * Recupera el llistat de continguts d'una execució massiva
	 * 
	 * @param exm_id
	 * 				ID de l'execució massiva
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException;
	
	/**
	 * Recupera el nom dels elements d'una execució massiva
	 * 
	 * @param exm_id
	 * 				ID de l'execució massiva
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("isAuthenticated()")
	public List<String> findElementNomExecucioPerContingut(List<Long> continguts) throws NotFoundException;
	
	/**
	 * Recupera les execucions per tipus i llistat de continguts
	 * 
	 * @param tipus
	 * 				Tipus de l'execució massiva
	 * @param contingut
	 * 				IDs dels continguts
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ExecucioMassivaContingutDto> findExecucioPerContingut(List<Long> continguts) throws NotFoundException;

	/**
	 * Executa la següent execució massiva pendent
	 * @param entitat 
	 * 
	 */
	@PermitAll
	public void executeNextMassiveScheduledTask(Long entitatId);


}
