/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.AvisEntity;

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
			"and a.dataInici <= :currentDate " +
			"and (a.dataFinal is null or a.dataFinal >= :currentDate)")
	List<AvisEntity> findActive(@Param("currentDate") Date currentDate);
	
	@Modifying
	@Query(value = "update dis_avis " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
}
