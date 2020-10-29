package es.caib.distribucio.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.BackofficeEntity;
import es.caib.distribucio.core.entity.EntitatEntity;

public interface BackofficeRepository extends JpaRepository<BackofficeEntity, Long> {
	
	
	
	@Query(	"from " +
			"    BackofficeEntity backoffice " +
			"where " +
			"    backoffice.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(backoffice.codi) like lower('%'||:filtre||'%') or lower(backoffice.nom) like lower('%'||:filtre||'%') or lower(backoffice.url) like lower('%'||:filtre||'%'))")
	Page<BackofficeEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	
	
}
