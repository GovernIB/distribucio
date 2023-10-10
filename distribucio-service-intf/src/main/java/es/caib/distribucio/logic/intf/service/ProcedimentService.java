package es.caib.distribucio.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;

/**
 * Declaració dels mètodes per a gestionar procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentService {
	
	/**
	 * @param entitatId
	 * @param filtre
	 * @param paginacioParams
	 * @return La pàgina de procediments que compleixen el filtre.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public PaginaDto<ProcedimentDto> findAmbFiltre(
			Long entitatId, 
			ProcedimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams);

	/** 
	 * Mètode per actualitzar la llista de procediments disponibles.
	 * @throws Exception 
	 * 
	 */
	public void findAndUpdateProcediments(Long entitatId) throws Exception;
	

	/** 
	 * Mètode per cercar procediments pel seu codiSia.
	 * 
	 */
	public ProcedimentDto findByCodiSia(Long entitatId, String codiSia);
	

	/** 
	 * Mètode per cercar procediments pel seu nom.
	 * 
	 */
	public List<ProcedimentDto> findByNomOrCodiSia(Long entitatId, String nom);

}