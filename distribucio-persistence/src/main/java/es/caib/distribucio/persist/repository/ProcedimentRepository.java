package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ProcedimentEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public interface ProcedimentRepository extends JpaRepository<ProcedimentEntity, Long>{
	
	List<ProcedimentEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat);
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and (:isNullUnitatOrganitzativa = true or pro.unitatOrganitzativa = :unitatOrganitzativa) " + 
			"and (:isCodiNull = true or lower(pro.codi) like lower('%'||:codi||'%')) " + 
			"and (:isNomNull = true or lower(pro.nom) like lower('%'||:nom||'%')) " + 
			"and (:isCodiSiaNull = true or lower(pro.codiSia) like lower('%'||:codiSia||'%'))" + 
			"and (:isEstatNull = true or pro.estat = :estat)")
	Page<ProcedimentEntity> findAmbFiltrePaginat(
			@Param("entitatId") Long entitatId, 
			@Param("isNullUnitatOrganitzativa") boolean isNullUnitatOrganitzativa, 
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatorganitzativa, 
			@Param("isCodiNull") boolean isCodiNull, 
			@Param("codi") String codi,
			@Param("isNomNull") boolean isNomNull,
			@Param("nom") String nom, 
			@Param("isCodiSiaNull") boolean isCodiSiaNull, 
			@Param("codiSia") String codiSia, 
			@Param("isEstatNull") boolean isEstatNull, 
			@Param("estat") ProcedimentEstatEnumDto estat,
			Pageable pageable);
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where pro.codi = :codi ")
	ProcedimentEntity findByCodi(
			@Param("codi") String codi);
	
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
			"and (pro.codiSia like :codiSia )")
	ProcedimentEntity findByCodiSia(
			@Param("entitatId") Long entitatId, 
			@Param("codiSia") String codiSia);
	
	
	@Query( "from " + 
			"ProcedimentEntity pro " + 
			"where (pro.entitat.id = :entitatId) " + 
			"and (:esNullNom = true or pro.nom like '%'||:nom||'%' )")
	List<ProcedimentEntity> findByNom(
			@Param("entitatId") Long entitatId, 
			@Param("esNullNom") boolean esNullNom, 
			@Param("nom") String nom);
	
	
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
	
	/** Troba tots els procediments per estat.
	 * 
	 * @param estat
	 * @return Llistat de procediments que tenen aquell estat.
	 */
	List<ProcedimentEntity> findAllByEstat(ProcedimentEstatEnumDto estat);

}
