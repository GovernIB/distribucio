/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.AclSidEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclSidRepository extends JpaRepository<AclSidEntity, Long> {

	@Query(	"select " +
			"    sid " +
			"from " +
			"    AclSidEntity " +
			"where " +
			"    principal = false")
	public List<String> findSidByPrincipalFalse();

	@Modifying
	@Query(value = "update dis_acl_sid set sid = :codiNou where sid = :codiAntic and principal = 1", nativeQuery = true)
	int updateUsuariPermis(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
