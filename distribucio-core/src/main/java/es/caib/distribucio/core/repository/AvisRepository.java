/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.distribucio.core.entity.AvisEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisRepository extends JpaRepository<AvisEntity, Long> {


	@Query(	"from " +
			"    AvisEntity a " +
			"where " +
			"    a.actiu = true " +
			"and a.dataInici<=CURRENT_DATE " +
			"and a.dataFinal+1>CURRENT_DATE")
	List<AvisEntity> findActive();
	

	@Query(	"select a.dataFinal, a.dataFinal+1, CURRENT_DATE from " +
			"    AvisEntity a ")
	List<Object> findDate();
	

	

}
