/**
 * 
 */
package es.caib.distribucio.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.RegistreInteressatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus interessat de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreInteressatRepository extends JpaRepository<RegistreInteressatEntity, Long> {
	
	@Modifying
	@Query(value = "update dis_registre_inter " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
}
