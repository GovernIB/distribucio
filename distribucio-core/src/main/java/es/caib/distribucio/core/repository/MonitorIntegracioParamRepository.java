/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.MonitorIntegracioParamEntity;

/**
 * Definició dels mètodes necessaris per a gestionar un paràmetre del monitor d'integracio de base
 * de dades del tipus monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorIntegracioParamRepository extends JpaRepository<MonitorIntegracioParamEntity, Long> {

	/** Esborra les dades anteriors a la data passada per paràmetre. */
	@Query(	"delete from MonitorIntegracioParamEntity monParam " +
			"where monParam.monitorIntegracio.data < :data ")
	@Modifying
	public void deleteDataBefore(@Param("data") Date data);

}
