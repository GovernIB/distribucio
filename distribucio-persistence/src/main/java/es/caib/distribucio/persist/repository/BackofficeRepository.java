package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.BackofficeEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;

public interface BackofficeRepository extends JpaRepository<BackofficeEntity, Long> {
	
	
	
	@Query(	"from " +
			"    BackofficeEntity backoffice " +
			"where " +
			"    backoffice.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(backoffice.codi) like lower('%'||:filtre||'%') or lower(backoffice.nom) like lower('%'||:filtre||'%') or lower(backoffice.url) like lower('%'||:filtre||'%') or lower(backoffice.nom) like lower('%'||:filtre||'%') or lower(backoffice.tipus) like lower('%'||:filtre||'%'))")
	Page<BackofficeEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	

	@Query("from BackofficeEntity " +
			"where entitat = :entitat " +
			"order by nom")
	List<BackofficeEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat);
	
	BackofficeEntity findByEntitatAndCodi(
			@Param("entitat") EntitatEntity entitat,
			@Param("codi") String codi);
	
	@Modifying
	@Query(value = "update dis_backoffice " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
}
