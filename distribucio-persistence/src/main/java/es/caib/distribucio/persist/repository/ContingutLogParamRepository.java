/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.ContingutLogEntity;
import es.caib.distribucio.persist.entity.ContingutLogParamEntity;


public interface ContingutLogParamRepository extends JpaRepository<ContingutLogParamEntity, Long> {

	List<ContingutLogParamEntity> findByContingutLog(
			ContingutLogEntity contingutLog);
	
	@Modifying
	@Query(value = "update dis_cont_log_param " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);

}
