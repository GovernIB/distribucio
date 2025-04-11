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
import es.caib.distribucio.persist.entity.ContingutMovimentEmailEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutMoviment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutMovimentEmailRepository extends JpaRepository<ContingutMovimentEmailEntity, Long> {

	public List<ContingutMovimentEmailEntity> findByEnviamentAgrupatFalseOrderByDestinatariAscBustiaAsc();
	public List<ContingutMovimentEmailEntity> findByEnviamentAgrupatTrueOrderByDestinatariAscBustiaAsc();

	public List<ContingutMovimentEmailEntity> findByContingutOrderByDestinatariAscBustiaAsc(ContingutEntity contingut);
	
	@Modifying
	@Query(value = "update dis_cont_mov_email " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
	@Modifying
	@Query(value = "update dis_cont_mov_email " +
			"set destinatari_codi = :codiNou where destinatari_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariCodi(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
}
