/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.core.api.dto.RegistreSimulatDto;
import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.ReglaFiltreDto;
import es.caib.distribucio.core.api.exception.NotFoundException;


/**
 * Declaració dels mètodes per a la gestió de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ReglaService {

	/**
	 * Crea una nova regla.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param regla
	 *            Informació de la regla a crear.
	 * @return La regla creada.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto create(
			Long entitatId,
			ReglaDto regla);

	/**
	 * Actualitza la informació d'una regla.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param regla
	 *            Informació de la regla a modificar.
	 * @return La regla modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto update(
			Long entitatId,
			ReglaDto regla) throws NotFoundException;

	/**
	 * Marca la regla com a activa/inactiva.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a modificar.
	 * @param activa
	 *            true si es vol activar o false en cas contrari.
	 * @return La regla modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			boolean activa) throws NotFoundException;

	/**
	 * Esborra una regla.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a esborrar.
	 * @return La regla esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto delete(
			Long entitatId,
			Long reglaId) throws NotFoundException;

	/**
	 * Mou un regla una posició per amunt dins l'ordre de l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a esborrar.
	 * @return La regla moguda.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto moveUp(
			Long entitatId,
			Long reglaId) throws NotFoundException;

	/**
	 * Mou un regla una posició per avall dins l'ordre de l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a esborrar.
	 * @return La regla moguda.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto moveDown(
			Long entitatId,
			Long reglaId) throws NotFoundException;

	/**
	 * Mou un regla a la posició especificada dins l'ordre de l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a esborrar.
	 * @param posicio
	 *            Nova posició de la regla.
	 * @return La regla moguda.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto moveTo(
			Long entitatId,
			Long reglaId,
			int posicio) throws NotFoundException;

	/**
	 * Consulta una regla donat el seu id.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a trobar.
	 * @return La regla amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ReglaDto findOne(
			Long entitatId,
			Long reglaId);

	/**
	 * Llistat amb totes les regles d'una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de regles.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public PaginaDto<ReglaDto> findAmbEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams);

	List<ReglaDto> findByEntitatAndUnitatCodi(Long entitatId, String unitatCodi);

	PaginaDto<ReglaDto> findAmbFiltrePaginat(Long entitatId, ReglaFiltreDto filtre, PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('DIS_ADMIN')")
	public List<RegistreSimulatAccionDto> simularReglaAplicacio(
			RegistreSimulatDto registreSimulatDto);

	/**
	 * Consulta les regles per codi de procediment.
	 * @param procediments
	 * 			List de codis procediment
	 * @return Map<codiProcediment, List<ReglasExistents>>
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public Map<String, List<ReglaDto>> findReglesByCodiProcediment(List<String> procediments);
}
