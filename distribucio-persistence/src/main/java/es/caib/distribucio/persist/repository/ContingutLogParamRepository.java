/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.persist.entity.ContingutLogEntity;
import es.caib.distribucio.persist.entity.ContingutLogParamEntity;


public interface ContingutLogParamRepository extends JpaRepository<ContingutLogParamEntity, Long> {

	List<ContingutLogParamEntity> findByContingutLog(
			ContingutLogEntity contingutLog);
	
	

}
