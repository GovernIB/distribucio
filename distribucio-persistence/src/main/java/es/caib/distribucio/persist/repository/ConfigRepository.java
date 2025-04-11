/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.persist.entity.ConfigEntity;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<ConfigEntity, String> {
	
	ConfigEntity findByKeyAndEntitatCodi(String key, String entitatCodi);
	
	List<ConfigEntity> findByEntitatCodiIsNull();
	
	@Query("FROM ConfigEntity c WHERE c.key like concat('%', :key, '%') AND c.entitatCodi IS NOT NULL AND c.configurable = true")
	List<ConfigEntity> findLikeKeyEntitatNotNullAndConfigurable(
			@Param("key") String key);
	
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ConfigEntity c WHERE c.entitatCodi = :entitatCodi")
	public int deleteByEntitatCodi(
			@Param("entitatCodi") String entitatCodi);
	
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
			+ "c.configurable = true"
			)
	public List<ConfigEntity> findConfigurables();
	
	
	@Query( "from "
			+ "ConfigEntity c "
			+ "where "
			+ "c.configurable = true "
			+ "and c.entitatCodi = null"
			)
	public List<ConfigEntity> findConfigurablesAmbEntitatNull();
	
	@Modifying
	@Query(value = "update dis_config " +
			"set lastmodifiedby_codi = :codiNou " +
			"where lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);

}