package es.caib.distribucio.logic.intf.service;

import java.util.List;

import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;

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

	/** Mètode per consultar si hi ha cap actualització en curs per actualitzar procediments.
	 * 
	 * @return
	 */
	public boolean isUpdatingProcediments(Long entitatId);

	/** Mètode per consultar l'estat del progrés d'actualització, retorna un objecte amb la informació.
	 * 
	 * @return
	 */
	public UpdateProgressDto getProgresActualitzacio(Long entitatId);

}
