package es.caib.distribucio.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.core.api.dto.ConfigGroupDto;

/**
 * Declaració dels mètodes per a la gestió dels paràmetres de configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigService {

//	<T> T findProperty (String key);

	/**
	 * Actualitza el valor d'una propietat de configuració.
	 *
	 * @param property Informació que es vol actualitzar.
	 * @return El DTO amb les dades modificades.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public ConfigDto updateProperty(ConfigDto property) throws Exception;

	@PreAuthorize("hasRole('DIS_SUPER')")
	public List<ConfigGroupDto> findAll();

	@PreAuthorize("hasRole('DIS_SUPER')")
	public void synchronize();
	
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public String getConcsvBaseUrl() ;

	@PreAuthorize("hasRole('DIS_SUPER')")
	public ConfigDto findByKey(String key);
	

}

