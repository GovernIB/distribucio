/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.MonitorIntegracioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar un monitorIntegracio de base
 * de dades del tipus monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorIntegracioRepository extends JpaRepository<MonitorIntegracioEntity, Long> {

	MonitorIntegracioEntity findByCodi(String codi);
	
	@Query(	"from " +
			"    MonitorIntegracioEntity mon " +
			"where " +
			"lower(mon.codi) like lower(:codiMonitor) " +
			"and (" +
				":esNullFiltre = true " +			
				" or (" +			
					" lower(mon.codiUsuari) like lower('%'||:filtre||'%'))" +		
					" or lower(mon.descripcio) like lower('%'||:filtre||'%'))" +
					") " +
			") ")
	Page<MonitorIntegracioEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("codiMonitor") String codiMonitor,			
			Pageable pageable);
	
//	@Query(	"from " +
//			"    MonitorIntegracioEntity mon " +
//			"where " +
//			"    :esNullFiltre = true " +
//			" or lower(mon.codi) like lower(:codi) " +			
//			" or lower(mon.descripcio) like lower('%'||:filtre||'%')) ")
//	List<MonitorIntegracioEntity> findByFiltrePaginat(
//			@Param("esNullFiltre") boolean esNullFiltre,
//			@Param("filtre") String filtre,
//			@Param("codi") String codi,
//			Sort sort);

}
