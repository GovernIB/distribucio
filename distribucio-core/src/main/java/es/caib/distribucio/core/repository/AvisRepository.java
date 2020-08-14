/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.distribucio.core.entity.AvisEntity;

/**
 * Repositori per gestionar una entitat de base de dades del tipus avís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisRepository extends JpaRepository<AvisEntity, Long> {

	@Query(	"from " +
			"    AvisEntity a " +
			"where " +
			"    a.actiu = true " +
			"and a.dataInici<=CURRENT_DATE " +
			"and a.dataFinal>=trunc(CURRENT_DATE)")
	List<AvisEntity> findActive();	
}
