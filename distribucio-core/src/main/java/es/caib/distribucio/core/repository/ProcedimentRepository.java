package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.ProcedimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public interface ProcedimentRepository extends JpaRepository<ProcedimentEntity, Long>{
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and (:isNullUnitatOrganitzativa = true or pro.unitatOrganitzativa.id = :unitatOrganitzativaId) " + 
			"and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%')) " + 
			"and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%')) " + 
			"and (:isCodiSiaNull = true or lower(pro.codiSia) like lower('%'||:codiSia||'%'))")
	Page<ProcedimentEntity> findAmbFiltrePaginat(
			@Param("entitatId") Long entitatId, 
			@Param("isNullUnitatOrganitzativa") boolean isNullUnitatOrganitzativa, 
			@Param("unitatOrganitzativaId") Long unitatorganitzativaId, 
			@Param("isCodiNull") boolean isCodiNull, 
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom, 
			@Param("isCodiSiaNull") boolean isCodiSiaNull, 
			@Param("codiSia") String codiSia, 
			Pageable pageable);
	
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and pro.codi = :codi ")
	ProcedimentEntity findByCodi(
			@Param("entitatId") Long entitatId, 
			@Param("codi") String codi);
	
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and (lower(pro.codiSia) = lower(:codiSia))")
	ProcedimentEntity findByCodiSia(
			@Param("entitatId") Long entitatId, 
			@Param("codiSia") String codiSia);
	
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and ((pro.codiSia like '%'||:search||'%') " +
			"		or (lower(pro.nom) like lower('%'||:search||'%')))")
	List<ProcedimentEntity> findByNomOrCodiSia(
			@Param("entitatId") Long entitatId, 
			@Param("search") String search);

	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and pro.unitatOrganitzativa.codi = :unitatOrganitzativaCodi")
	List<ProcedimentEntity> findByCodiUnitatOrganitzativa(
			@Param("entitatId") Long entitatId, 
			@Param("unitatOrganitzativaCodi") String unitatOrganitzativaCodi);

}
