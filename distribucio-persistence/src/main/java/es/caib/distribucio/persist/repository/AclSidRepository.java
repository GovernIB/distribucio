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

	/** Identificador del SID d'un usuari (principal), o null si l'usuari no té cap permís assignat. */
	@Query(value = "select id from dis_acl_sid where sid = :codi and principal = 1", nativeQuery = true)
	Long findIdPrincipalBySid(@Param("codi") String codi);

	/*
	 * Els mètodes següents serveixen per a unificar dos usuaris que ja tenen SID: no es pot canviar el codi
	 * d'un SID a un que ja existeix (restricció única sid + principal), així que es repunten les referències
	 * al SID de l'usuari antic cap al del nou i s'esborra el SID antic.
	 */

	/** Esborra les entrades ACL de l'usuari antic que el nou ja té idèntiques sobre el mateix objecte. */
	@Modifying
	@Query(value = "delete from dis_acl_entry e " +
			"where e.sid = :sidAntic " +
			"and exists (" +
			"    select 1 from dis_acl_entry n " +
			"    where n.sid = :sidNou " +
			"    and n.acl_object_identity = e.acl_object_identity " +
			"    and n.mask = e.mask " +
			"    and n.granting = e.granting)",
			nativeQuery = true)
	int deleteEntriesDuplicades(@Param("sidAntic") Long sidAntic, @Param("sidNou") Long sidNou);

	/** Passa les entrades ACL del SID antic al nou. */
	@Modifying
	@Query(value = "update dis_acl_entry set sid = :sidNou where sid = :sidAntic", nativeQuery = true)
	int updateEntriesSid(@Param("sidAntic") Long sidAntic, @Param("sidNou") Long sidNou);

	/** Passa la propietat dels objectes ACL del SID antic al nou. */
	@Modifying
	@Query(value = "update dis_acl_object_identity set owner_sid = :sidNou where owner_sid = :sidAntic", nativeQuery = true)
	int updateOwnerObjectIdentity(@Param("sidAntic") Long sidAntic, @Param("sidNou") Long sidNou);

	/** Esborra un SID per identificador. */
	@Modifying
	@Query(value = "delete from dis_acl_sid where id = :id", nativeQuery = true)
	int deleteSidById(@Param("id") Long id);
}
