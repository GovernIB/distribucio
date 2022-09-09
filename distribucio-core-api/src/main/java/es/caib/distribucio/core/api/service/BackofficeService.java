package es.caib.distribucio.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a gestionar backoffices.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BackofficeService {

	/**
	 * Crea un nou backoffice.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param backoffice
	 *            Informació del backoffice a crear;
	 * @return El backoffice creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public BackofficeDto create(
			Long entitatId,
			BackofficeDto backoffice) throws NotFoundException;

	/**
	 * Actualitza la informació del backoffice que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param backoffice
	 *            Informació del backoffice a modificar.
	 * @return El backoffice modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public BackofficeDto update(
			Long entitatId,
			BackofficeDto backoffice) throws NotFoundException;

	/**
	 * Esborra el backoffice amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del backoffice a esborrar.
	 * @return El backoffice esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public BackofficeDto delete(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta un backoffice donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del backoffice a trobar.
	 * @return El backoffice amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA') or hasRole('DIS_REGLA')")
	public BackofficeDto findById(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb tots els backoffices de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de backoffices.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA')")
	public PaginaDto<BackofficeDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA')")
	public List<BackofficeDto> findByEntitat(Long entitatId) throws NotFoundException;

	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA') or hasRole('DIS_REGLA')")
	public BackofficeDto findByCodi(
			Long entitatId,
			String backofficeCodi) throws NotFoundException;


	
}
