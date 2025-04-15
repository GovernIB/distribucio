/**
 * 
 */
package es.caib.distribucio.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.RegistreFirmaDetallEntity;

public interface RegistreFirmaDetallRepository extends JpaRepository<RegistreFirmaDetallEntity, Long> {

	@Modifying
	@Query(value = "update dis_registre_firma_detall " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
}
