/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnosticDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDto;
import es.caib.distribucio.logic.intf.dto.IntegracioFiltreDto;
import es.caib.distribucio.logic.intf.dto.MonitorIntegracioDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

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
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public List<IntegracioDto> integracioFindAll();

	/** Mètode per treure el llistat d'integracions
	 *  per comprovar la conexió amb el plugin.
	 * 
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public List<IntegracioDto> findPerDiagnostic();

//	/**
//	 * Crea un nou item monitorIntegracio.
//	 * 
//	 * @param monitorIntegracio
//	 *            Informació de l'item monitorIntegracio a crear.
//	 * @return El/La MonitorIntegracio creat/creada
//	 */
//	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio);

	/**
	 * Consulta un/una monitorIntegracio donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'item monitorIntegracio a trobar.
	 * @return L'item monitorIntegracio amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public MonitorIntegracioDto findById(
			Long id) throws NotFoundException;

	/**
	 * Llistat amb tots els items monitorIntegracio paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'items MonitorIntegracio.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public PaginaDto<MonitorIntegracioDto> findPaginat(PaginacioParamsDto paginacioParams, IntegracioFiltreDto integracioFiltreDto);

	/** Consulta el número d'errors per integració. */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public Map<String, Integer> countErrors(int numeroHores);

	/** Mètode per esborrar dades anteriors a una data passada per paràmetre */
	public int esborrarDadesAntigues(Date data);

	/** Mètode per esborrar dades per a una integració específica.
	 * 
	 * @param codi Codi de la integració a esborrar.
	 * @return Retorna el número de registres esborrats.
	 */
	public int delete(String codi);

	/**Mètode per comprovar la situació de la integració
	 * 
	 * @param codiIntegracio a diagnosticar
	 * @param objecte usuari 
	 * @return
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public IntegracioDiagnosticDto diagnostic(String codiIntegracio, UsuariDto usuari);

}
