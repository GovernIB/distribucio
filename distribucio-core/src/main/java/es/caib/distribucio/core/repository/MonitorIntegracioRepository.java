/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
					" lower(mon.codiUsuari) like lower('%'||:filtre||'%')" +		
					" or lower(mon.descripcio) like lower('%'||:filtre||'%')" +
					") " +
			"	)")
	Page<MonitorIntegracioEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("codiMonitor") String codiMonitor,			
			Pageable pageable);

	@Query(	"select mon.codi, count(mon)" +
			"from MonitorIntegracioEntity mon " +
			"where mon.estat = 'ERROR' " +
			"group by mon.codi ")
	public List<Object[]> countErrorsGroupByCodi();

	/** Esborra les dades anteriors a la data passada per paràmetre. */
	@Query(	"delete  from MonitorIntegracioEntity mon " +
			"where mon.data < :data ")
	@Modifying
	public void deleteDataBefore(@Param("data") Date data);

	/** Consulta les dades antigues */
	@Query(	"from MonitorIntegracioEntity mon " +
			"where mon.data < :data ")
	public List<MonitorIntegracioEntity> getDadesAntigues(@Param("data") Date data);

}
