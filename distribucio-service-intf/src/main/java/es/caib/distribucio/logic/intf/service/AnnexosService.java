/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

/**
 * Declaració dels mètodes per a gestionar annexos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AnnexosService {
	
	/**
	 * Obté una llista dels continguts esborrats permetent especificar dades
	 * per al seu filtratge.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            El filtre de la consulta.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return Una pàgina amb els continguts trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public PaginaDto<RegistreAnnexDto> findAdmin(			
			Long entitatId, 
			AnnexosFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<Long> findAnnexIds(AnnexosFiltreDto filtre) throws NotFoundException;
	
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public ResultatAnnexDefinitiuDto guardarComADefinitiu(Long id);
	
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public List<RegistreAnnexDto> findMultiple(
			Long entitatId,
			List<Long> multipleAnnexosIds,
			boolean isAdmin) throws NotFoundException;

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public List<Integer> findCopiesRegistre(String numero);
	
//	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
//	public String guardarComADefinitiuMultiple(List<Long> ids);
}
