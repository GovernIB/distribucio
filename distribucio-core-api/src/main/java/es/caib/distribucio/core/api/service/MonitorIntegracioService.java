/**
 * 
 */
package es.caib.distribucio.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.IntegracioAccioDto;
import es.caib.distribucio.core.api.dto.MonitorIntegracioDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió del item monitorIntegracio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorIntegracioService {

	/**
	 * Crea un nou item monitorIntegracio.
	 * 
	 * @param monitorIntegracio
	 *            Informació de l'item monitorIntegracio a crear.
	 * @return El/La MonitorIntegracio creat/creada
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio);

	/**
	 * Actualitza la informació de l'item monitorIntegracio que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param monitorIntegracio
	 *            Informació de l'item monitorIntegracio a modificar.
	 * @return L'item monitorIntegracio modificat
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public MonitorIntegracioDto update(
			MonitorIntegracioDto monitorIntegracio) throws NotFoundException;

	/**
	 * Esborra l'item monitorIntegracio amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id de l'item monitorIntegracio a esborrar.
	 * @return L'item monitorIntegracio esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public MonitorIntegracioDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un/una monitorIntegracio donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'item monitorIntegracio a trobar.
	 * @return L'item monitorIntegracio amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public MonitorIntegracioDto findById(
			Long id) throws NotFoundException;

	/**
	 * Consulta un/una monitorIntegracio donat el seu codi.
	 * 
	 * @param codi
	 *            Atribut codi de l'item monitorIntegracio a trobar.
	 * @return L'item monitorIntegracio amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public MonitorIntegracioDto findByCodi(String codi);

	/**
	 * Llistat amb tots els items monitorIntegracio paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'items MonitorIntegracio.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public PaginaDto<MonitorIntegracioDto> findPaginat(PaginacioParamsDto paginacioParams, String codiMonitor);	

	/**
	 * Alta d'un item monitorIntegracio
	 * 
	 * @param integracioCodi
	 * 			Codi de la integració
	 * 
	 * @param accio
	 * 			Accio amb les dades a desar en el MonitorIntegracio
	 * 
	 * @return void
	 *   
	 */
	public void addAccio(String integracioCodi, IntegracioAccioDto accio);

}
