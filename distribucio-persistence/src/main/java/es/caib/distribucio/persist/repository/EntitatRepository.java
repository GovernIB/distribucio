/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {

	EntitatEntity findByCodi(String codi);

	EntitatEntity findByCodiDir3(String codiDir3);

	List<EntitatEntity> findByActiva(boolean activa);

	@Query(	"from " +
			"    EntitatEntity ent " +
			"where " +
			"    :esNullFiltre = true " +
			" or lower(ent.codi) like lower('%'||:filtre||'%') " +
			" or lower(ent.nom) like lower('%'||:filtre||'%') " +
			" or lower(ent.cif) like lower('%'||:filtre||'%') ")
	Page<EntitatEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	@Query(	"from " +
			"    EntitatEntity ent " +
			"where " +
			"    :esNullFiltre = true " +
			" or lower(ent.codi) like lower('%'||:filtre||'%') " +
			" or lower(ent.nom) like lower('%'||:filtre||'%') " +
			" or lower(ent.cif) like lower('%'||:filtre||'%') ")
	List<EntitatEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Sort sort);
	
	@Modifying
	@Query(value = "update dis_entitat " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);

}
