package es.caib.distribucio.logic.intf.service;

import java.util.List;

import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;

/**
 * Declaració dels mètodes per a gestionar serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiService {
	
	/**
	 * @param entitatId
	 * @param filtre
	 * @param paginacioParams
	 * @return La pàgina de serveis que compleixen el filtre.
	 */
	public PaginaDto<ServeiDto> findAmbFiltre(
			Long entitatId, 
			ServeiFiltreDto filtre, 
			PaginacioParamsDto paginacioParams);

	/** 
	 * Mètode per actualitzar la llista de serveis disponibles.
	 * @throws Exception 
	 * 
	 */
	public void findAndUpdateServeis(Long entitatId) throws Exception;
	

	/** 
	 * Mètode per cercar serveis pel seu codiSia.
	 * 
	 */
	public ServeiDto findByCodiSia(Long entitatId, String codiSia);
	

	/** 
	 * Mètode per cercar serveis pel seu nom.
	 * 
	 */
	public List<ServeiDto> findByNomOrCodiSia(Long entitatId, String nom);

}
