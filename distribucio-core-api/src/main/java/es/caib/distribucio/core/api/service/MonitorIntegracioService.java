/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.IntegracioDto;
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
	 * Obté les integracions disponibles.
	 * 
	 * @return La llista d'integracions.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public List<IntegracioDto> integracioFindAll();

	/**
	 * Crea un nou item monitorIntegracio.
	 * 
	 * @param monitorIntegracio
	 *            Informació de l'item monitorIntegracio a crear.
	 * @return El/La MonitorIntegracio creat/creada
	 */
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio);

	/**
	 * Consulta un/una monitorIntegracio donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'item monitorIntegracio a trobar.
	 * @return L'item monitorIntegracio amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public MonitorIntegracioDto findById(
			Long id) throws NotFoundException;

	/**
	 * Llistat amb tots els items monitorIntegracio paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'items MonitorIntegracio.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public PaginaDto<MonitorIntegracioDto> findPaginat(PaginacioParamsDto paginacioParams, String codiMonitor);

	/** Consulta el número d'errors per integració. */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public Map<String, Integer> countErrors(int numeroHores);

	/** Mètode per esborrar dades anteriors a una data passada per paràmetre */
	public int esborrarDadesAntigues(Date data);

	/** Mètode per esborrar dades per a una integració específica.
	 * 
	 * @param codi Codi de la integració a esborrar.
	 * @return Retorna el número de registres esborrats.
	 */
	public int delete(String codi);


}
