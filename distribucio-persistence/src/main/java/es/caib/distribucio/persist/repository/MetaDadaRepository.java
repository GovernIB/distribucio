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

import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.MultiplicitatEnumDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.MetaDadaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDadaRepository extends JpaRepository<MetaDadaEntity, Long> {
		
	int countByEntitat(EntitatEntity entitat);
	
	List<MetaDadaEntity> findByEntitatOrderByOrdreAsc(
			EntitatEntity entitat);
	
	@Query(	"from " +
			"    MetaDadaEntity md " +
			"where " +
			"    md.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(md.codi) like lower('%'||:filtre||'%') or lower(md.nom) like lower('%'||:filtre||'%')) order by md.ordre asc")
	Page<MetaDadaEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);
	
	@Query(	"from " +
			"    MetaDadaEntity md " +
			"where " +
			"    md.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(md.codi) like lower('%'||:filtre||'%') or lower(md.nom) like lower('%'||:filtre||'%')) order by md.ordre asc")
	List<MetaDadaEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Sort sort);
	
//	List<MetaDadaEntity> findByMetaNodeIdInOrderByMetaNodeIdAscOrdreAsc(
//			List<Long> metaNodeIds);
//	List<MetaDadaEntity> findByMetaNodeIdAndTipusOrderByOrdreAsc(
//			Long metaNodeId,
//			MetaDadaTipusEnumDto tipus);
	List<MetaDadaEntity> findByEntitatAndTipusAndActivaTrueOrderByOrdreAsc(
			EntitatEntity entitat,
			MetaDadaTipusEnumDto tipus);
	List<MetaDadaEntity> findByEntitatAndActivaTrueOrderByOrdreAsc(
			EntitatEntity entitat);
	List<MetaDadaEntity> findByEntitatAndActivaTrueAndMultiplicitatIn(
			EntitatEntity entitat,
			MultiplicitatEnumDto[] multiplicitats);
	
	MetaDadaEntity findByCodi(String codi);

	List<MetaDadaEntity> findByEntitat(EntitatEntity entitat);
	
	@Modifying
	@Query(value = "update dis_metadada " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
}