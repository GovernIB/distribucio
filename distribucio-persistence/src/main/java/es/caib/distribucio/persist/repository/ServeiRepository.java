package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ServeiEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public interface ServeiRepository extends JpaRepository<ServeiEntity, Long>{
	
	List<ServeiEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat);
	
	@Query( "from " + 
			"ServeiEntity srv " + 
			"where (srv.entitat.id = :entitatId) " + 
			"and (:isNullUnitatOrganitzativa = true or srv.unitatOrganitzativa = :unitatOrganitzativa) " + 
			"and (:isCodiNull = true or lower(srv.codi) like lower('%'||:codi||'%')) " + 
			"and (:isNomNull = true or lower(srv.nom) like lower('%'||:nom||'%')) " + 
			"and (:isCodiSiaNull = true or lower(srv.codiSia) like lower('%'||:codiSia||'%'))" + 
			"and (:isEstatNull = true or srv.estat = :estat)")
	Page<ServeiEntity> findAmbFiltrePaginat(
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
			@Param("estat") ServeiEstatEnumDto estat,
			Pageable pageable);
	
	
	@Query( "from " + 
			"ServeiEntity srv " + 
			"where (srv.entitat.id = :entitatId) " + 
			"and srv.codi = :codi ")
	ServeiEntity findByCodi(
			@Param("entitatId") Long entitatId, 
			@Param("codi") String codi);
	
	
	@Query( "from " + 
			"ServeiEntity srv " + 
			"where (srv.unitatOrganitzativa.codi = :codiDir3) ")
	List<ServeiEntity> findByCodiDir3(
			@Param("codiDir3") String codiDir3);
	
	
	@Query( "from " + 
			"ServeiEntity srv " + 
			"where (srv.entitat.id = :entitatId) " + 
			"and (srv.codiSia like :codiSia )")
	ServeiEntity findByCodiSia(
			@Param("entitatId") Long entitatId, 
			@Param("codiSia") String codiSia);
	
	
	@Query( "from " + 
			"ServeiEntity srv " + 
			"where (srv.entitat.id = :entitatId) " + 
			"and (:esNullNom = true or srv.nom like '%'||:nom||'%' )")
	List<ServeiEntity> findByNom(
			@Param("entitatId") Long entitatId, 
			@Param("esNullNom") boolean esNullNom, 
			@Param("nom") String nom);
	
	
	@Query( "from " + 
			"ServeiEntity srv " + 
			"where (srv.entitat.id = :entitatId) " + 
			"and ((srv.codiSia like '%'||:search||'%') " +
			"		or (lower(srv.nom) like lower('%'||:search||'%')))")
	List<ServeiEntity> findByNomOrCodiSia(
			@Param("entitatId") Long entitatId, 
			@Param("search") String search);

	
	@Query( "from " + 
			"ServeiEntity srv " + 
			"where (srv.entitat.id = :entitatId) " + 
			"and srv.unitatOrganitzativa.codi = :unitatOrganitzativaCodi")
	List<ServeiEntity> findByCodiUnitatOrganitzativa(
			@Param("entitatId") Long entitatId, 
			@Param("unitatOrganitzativaCodi") String unitatOrganitzativaCodi);
	
	/** Troba tots els serveis per entitat i estat.
	 * 
	 * @param entitat
	 * @param estat
	 * @return Llistat de serveis que tenen aquell estat per l'entitat.
	 */
	List<ServeiEntity> findAllByEntitatAndEstat(
			EntitatEntity entitat, 
			ServeiEstatEnumDto estat);
	
	@Modifying
	@Query(value = "update dis_servei " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
}
