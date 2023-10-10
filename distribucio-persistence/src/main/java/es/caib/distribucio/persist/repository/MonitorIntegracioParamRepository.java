/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.MonitorIntegracioParamEntity;

/**
 * Definició dels mètodes necessaris per a gestionar un paràmetre del monitor d'integracio de base
 * de dades del tipus monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorIntegracioParamRepository extends JpaRepository<MonitorIntegracioParamEntity, Long> {

	/** Esborra les dades anteriors a la data passada per paràmetre. */
	@Modifying
	@Query("delete MonitorIntegracioParamEntity monParam " +
			"where monParam.monitorIntegracio.id in " +
			"( 	select mon.id " + 
			"	from MonitorIntegracioEntity mon " +
			"	where mon.data < :data )")
	public void deleteDataBefore(
			@Param("data") Date data);
	
	
	/** Esborra les dades filtrant per l'id del monitor d'integració */
	@Modifying
	@Query("delete from MonitorIntegracioParamEntity monParam " +
			"where monParam.monitorIntegracio.id = :idMonitorIntegracio")
	public void deleteByIdMonitorIntegracio(
			@Param("idMonitorIntegracio") long idMonitorIntegracio);
			
				
	/** Esborra els paràmetres per codi el monitor. */
	@Modifying
	@Query("delete MonitorIntegracioParamEntity monParam " +
			"where monParam.monitorIntegracio.id in " +
			"( 	select mon.id " + 
			"	from MonitorIntegracioEntity mon " +
			"	where mon.codi = :codi )")
	public void deleteByMonitorIntegracioCodi(
			@Param("codi") String codi);

}
