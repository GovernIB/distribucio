/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.core.entity.ConfigEntity;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<ConfigEntity, String> {
	
	List<ConfigEntity> findByJbossPropertyFalse();
	
	@Query(	"select " +
			"    count(c) " +
			"from " +
			"    ConfigEntity c " +
			"where " +
			"    c.value != null ")
	public int countNotNullValues();
	
	
	@Query( "from "
			+ "ConfigEntity c "
			+ "where "
			+ "c.entitatCodi = :entitatCodi "
			+ "order by c.position asc")
	public List<ConfigEntity> findAllPerEntitat(
			@Param("entitatCodi") String entitatCodi);
	
	
	@Query( "from "
			+ "ConfigEntity c "
			+ "where "
			+ "c.key = :key")
	public ConfigEntity findPerKey(
			@Param("key") String key);
	
	
	@Query( "from "
			+ "ConfigEntity c "
			+ "where "
			+ "c.configurable = 1"
			)
	public List<ConfigEntity> findConfigurables();
	
	
	@Query( "from "
			+ "ConfigEntity c "
			+ "where "
			+ "c.configurable = 1 "
			+ "and c.entitatCodi = null"
			)
	public List<ConfigEntity> findConfigurablesAmbEntitatNull();

}