/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.ContingutMovimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutMoviment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutMovimentRepository extends JpaRepository<ContingutMovimentEntity, Long> {

	List<ContingutMovimentEntity> findByContingutOrderByCreatedDateAsc(
			ContingutEntity contingut);
	
	ContingutMovimentEntity findFirstByContingutOrderByCreatedDateAsc(
			ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndOrigenIdNull(ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndOrigenIdNotNullOrderByCreatedDateAsc(ContingutEntity contingut);

	List<ContingutMovimentEntity> findByContingutAndOrigenIdNotNullOrderByCreatedDateDesc(ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndDestiId(ContingutEntity contingut, Long destiId);
	
	@Modifying
	@Query(value = "update dis_cont_mov " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
	@Modifying
	@Query(value = "update dis_cont_mov " +
			"set remitent_codi = :codiNou where remitent_codi = :codiAntic",
			nativeQuery = true)
	void updateUsuariCodi(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
}
