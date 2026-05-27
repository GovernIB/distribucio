package es.caib.distribucio.persist.repository;

import java.util.List;

import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
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
            "and (:esNullFiltreCodi = true or lower(backoffice.codi) like lower('%'||:codi||'%')) " +
            "and (:esNullFiltreNom = true or lower(backoffice.nom) like lower('%'||:nom||'%')) " +
            "and (:esNullFiltreUrl = true or lower(backoffice.url) like lower('%'||:url||'%')) " +
            "and (:esNullFiltreTipus = true or backoffice.tipus = :tipus) "
    )
	Page<BackofficeEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
            @Param("esNullFiltreCodi") boolean esNullFiltreCodi,
            @Param("codi") String codi,
            @Param("esNullFiltreNom") boolean esNullFiltreNom,
            @Param("nom") String nom,
            @Param("esNullFiltreUrl") boolean esNullFiltreUrl,
            @Param("url") String url,
            @Param("esNullFiltreTipus") boolean esNullFiltreTipus,
            @Param("tipus") BackofficeTipusEnumDto tipus,
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

    @Query(	"Select b.id from " +
            "    BackofficeEntity b " +
            "where " +
            "    b.entitat = :entitat " +
            "and (:esNullFiltreCodi = true or lower(b.codi) like lower('%'||:codi||'%')) " +
            "and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:nom||'%')) " +
            "and (:esNullFiltreUrl = true or lower(b.url) like lower('%'||:url||'%')) " +
            "and (:esNullFiltreTipus = true or b.tipus = :tipus) "
    )
    List<Long> findIdsByFiltre(
            @Param("entitat") EntitatEntity entitat,
            @Param("esNullFiltreCodi") boolean esNullFiltreCodi,
            @Param("codi") String codi,
            @Param("esNullFiltreNom") boolean esNullFiltreNom,
            @Param("nom") String nom,
            @Param("esNullFiltreUrl") boolean esNullFiltreUrl,
            @Param("url") String url,
            @Param("esNullFiltreTipus") boolean esNullFiltreTipus,
            @Param("tipus") BackofficeTipusEnumDto tipus);
}
