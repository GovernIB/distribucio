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
			"and (:isNullUnitatOrganitzativa = true or pro.unitatOrganitzativa.codi = :unitatOrganitzativa) " + 
			"and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%')) " + 
			"and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%')) " + 
			"and (:isCodiSiaNull = true or lower(pro.codiSia) like lower('%'||:codiSia||'%'))")
	Page<ProcedimentEntity> findAmbFiltrePaginat(
			@Param("entitatId") Long entitatId, 
			@Param("isNullUnitatOrganitzativa") boolean isNullUnitatOrganitzativa, 
			@Param("unitatOrganitzativa") String unitatorganitzativa, 
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
			"where (pro.unitatOrganitzativa.codi = :codiDir3) ")
	List<ProcedimentEntity> findByCodiDir3(
			@Param("codiDir3") String codiDir3);
	
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and (:esNullCodiSia = true or pro.codiSia like '%'||:codiSia||'%' )")
	List<ProcedimentEntity> findByCodiSia(
			@Param("entitatId") Long entitatId, 
			@Param("esNullCodiSia") boolean esNullCodiSia, 
			@Param("codiSia") String codiSia);
	
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and (:esNullNom = true or pro.nom like '%'||:nom||'%' )")
	List<ProcedimentEntity> findByNom(
			@Param("entitatId") Long entitatId, 
			@Param("esNullNom") boolean esNullNom, 
			@Param("nom") String nom);

}
