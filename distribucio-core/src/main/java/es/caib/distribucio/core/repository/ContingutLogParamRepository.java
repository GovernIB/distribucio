/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.core.entity.ContingutLogEntity;
import es.caib.distribucio.core.entity.ContingutLogParamEntity;


public interface ContingutLogParamRepository extends JpaRepository<ContingutLogParamEntity, Long> {

	List<ContingutLogParamEntity> findByContingutLog(
			ContingutLogEntity contingutLog);
	
	

}
