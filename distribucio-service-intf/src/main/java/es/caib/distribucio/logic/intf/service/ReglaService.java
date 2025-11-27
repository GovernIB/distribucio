/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.ReglaFiltreDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;


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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			String sia,
			boolean activa) throws NotFoundException;

	/**
	 * Mètode per actualitzar la informació de la regla i establir si està activa
	 * o si té el filtre per presencial. Aquest mètode s'usa a l'API REST per part
	 * dels backoffices de Distribucio per activar o desactivar o poder fixar el valor
	 * pel filtre segons si l'anotació és presencial.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a modificar.
	 * @param activa
	 *            true si es vol activar o false en cas contrari.
	 * @param presencial
	 *            Amb valor true o false per si es vol filtrar o null en cas contrari.
	 * @return La regla modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
	public ReglaDto updateActivaPresencial(
			Long entitatId,
			Long reglaId,
			boolean activa,
			ReglaPresencialEnumDto presencial,
			String sia) throws NotFoundException;

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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public ReglaDto moveTo(
			Long entitatId,
			Long reglaId,
			int posicio) throws NotFoundException;

	/** 
	 * Mètode per aplicar manualment una regal. Avalua les anotacions pendents de bústia
	 * sense regla assignada i en cas de complir amb les condicions assigna la regla per a que
	 * s'apliqui en segon pla.
	 * 
	 * @param entitatId
	 * @param reglaId
	 * 
	 * @return Retorna la llista dels números dels registres als quals s'ha assignat la regla per a que se'ls apliqui.
	 * 
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public List<String> aplicarManualment(
			Long entitatId, 
			Long reglaId);

	/**
	 * Consulta una regla donat el seu id.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param reglaId
	 *            Atribut id de la regla a trobar.
	 * @return La regla amb l'id especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public PaginaDto<ReglaDto> findAmbEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<ReglaDto> findByEntitatAndUnitatFiltreCodi(Long entitatId, String unitatCodi);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<ReglaDto> findByEntitatAndUnitatDestiCodi(Long entitatId, String unitatCodi);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<ReglaDto> findByEntitatAndBackofficeDestiId(Long entitatId, Long backofficeDestiId);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	PaginaDto<ReglaDto> findAmbFiltrePaginat(Long entitatId, ReglaFiltreDto filtre, PaginacioParamsDto paginacioParams);
	
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<Long> findReglaIds(Long entitatId,ReglaFiltreDto filtre);	

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public List<RegistreSimulatAccionDto> simularReglaAplicacio(
			RegistreSimulatDto registreSimulatDto);

	/**
	 * Consulta les regles per codi de procediment.
	 * @param procediments
	 * 			List de codis procediment
	 * @return Map<codiProcediment, List<ReglasExistents>>
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public Map<String, List<ReglaDto>> findReglesByCodiProcediment(List<String> procediments);

	/** Mètode per trobar les regles a partir d'un codi SIA en la validació del mètode REST de creació
	 * de regles.
	 * 
	 * @param procedimentCodi
	 * @return
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
	public List<ReglaDto> findReglaBackofficeByProcediment (String procedimentCodi);

	/** Mètode per trobar les regles a partir d'un codi SIA en la validació del mètode REST de creació
	 * de regles.
	 *
	 * @param serveiCodi
	 * @return
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
	public List<ReglaDto> findReglaBackofficeByServei (String serveiCodi);

	/** Mètode per trobar les regles a partir d'un codi SIA en la validació del mètode REST de creació
	 * de regles.
	 *
	 * @param siaCodi
	 * @return
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
	public List<ReglaDto> findReglaBackofficeByCodiSia (String siaCodi);

	/** Mètode per trobar les regles a partir d'un codi SIA en la validació del mètode REST de update
	 * de regles.
	 * 
	 * @param procedimentCodi
	 * @return
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_REGLA + "')")
	public List<ReglaDto> findReglaByProcediment (String procedimentCodi);

}
