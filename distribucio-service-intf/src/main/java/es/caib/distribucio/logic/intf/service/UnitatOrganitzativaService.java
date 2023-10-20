/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaFiltreDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

/**
 * Declaració dels mètodes per a gestionar continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatOrganitzativaService {

	/**
	 * Consulta les unitats organitzatives de l'entitat.
	 * 
	 * @param entitatCodi
	 *            Atribut codi de l'entitat a la qual pertany l'interessat.
	 * @return La llista d'unitats organitzatives.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi);

	/**
	 * Consulta una unitat organitzativa donat el seu codi.
	 * 
	 * @param codi
	 *            Codi DIR3 de la unitat organitzativa.
	 * @return La unitat organitzativa trobada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb el codi especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public UnitatOrganitzativaDto findByCodi(
			String codi);

	/**
	 * Consulta les unitats organitzatives segons el filtre.
	 * 
	 * @param codiDir3
	 *            Codi DIR3 de la unitat organitzativa.
	 * @param denominacio
	 *            Denominació de la unitat organitzativa.
	 * @param nivellAdministracio
	 *            Nivel de l'administració.
	 * @param comunitatAutonoma
	 *            Valor del paràmetre comunitatAutonoma.
	 * @param provincia
	 *            Valor del paràmetre província.
	 * @param municipi
	 *            Valor del paràmetre municipi.
	 * @param arrel
	 *            Indica si s'ha de consultar únicament les unitats arrel.
	 *            Atribut codi de l'unitat.
	 * @return La llista d'unitats organitzatives que compleixen el filtre.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio, 
			String nivellAdm,
			String comunitat, 
			String provincia, 
			String localitat, 
			Boolean arrel);

	/**
	 * @param entitatId
	 * @param filtre
	 * @param paginacioParams
	 * @return La pàgina d'unitats organitzatives que compleixen el filtre.
	 */
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltre(Long entitatId, UnitatOrganitzativaFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	UnitatOrganitzativaDto findById(Long id);

	@PreAuthorize("isAuthenticated()")
	void synchronize(Long entitatId);

	@PreAuthorize("isAuthenticated()")
	ArbreDto<UnitatOrganitzativaDto> findTree(Long id);

	@PreAuthorize("isAuthenticated()")
	List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId);

	@PreAuthorize("isAuthenticated()")
	List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId);

	@PreAuthorize("isAuthenticated()")
	boolean isFirstSincronization(Long entidadId);

	@PreAuthorize("isAuthenticated()")
	List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entitatId);

	@PreAuthorize("isAuthenticated()")
	List<UnitatOrganitzativaDto> findByEntitatAndFiltre(String entitatCodi, String filtre, boolean ambArrel, boolean nomesAmbBusties);

	@PreAuthorize("isAuthenticated()")
	List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(String entitatCodi, String codiUnitatSuperior, String filtre, boolean ambArrel, boolean nomesAmbBusties);

	@PreAuthorize("isAuthenticated()")
	UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo);

	@PreAuthorize("isAuthenticated()")
	List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId);

	@PreAuthorize("isAuthenticated()")
	List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(String filtre);	

}
