package es.caib.distribucio.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.dto.ConfigDto;
import es.caib.distribucio.logic.intf.dto.ConfigGroupDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;

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
	public List<ConfigDto> findAllPerEntitat(EntitatDto entitat);

	@PreAuthorize("hasRole('DIS_SUPER')")
	public void synchronize();

	@PreAuthorize("hasRole('DIS_SUPER')")
	public void reiniciarTasquesEnSegonPla();

	@PreAuthorize("isAuthenticated()")
	public String getConcsvBaseUrl() ;

	@PreAuthorize("hasRole('DIS_SUPER')")
	public ConfigDto findByKey(String key);

	@PreAuthorize("hasRole('DIS_SUPER')")
	List<ConfigDto> findEntitatsConfigByKey(String key);

	@PreAuthorize("isAuthenticated()")
	public void setEntitatPerPropietat(EntitatDto entitatDto);

	@PreAuthorize("isAuthenticated()")
	public String getTempsErrorsMonitorIntegracio() ;

	public String getConfig(String key);

	public Long getConfigAsLong(String key);

}
