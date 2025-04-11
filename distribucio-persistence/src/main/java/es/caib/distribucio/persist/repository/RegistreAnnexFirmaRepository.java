/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.RegistreAnnexFirmaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus firma d'annex de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreAnnexFirmaRepository extends JpaRepository<RegistreAnnexFirmaEntity, Long> {
	
	
	/**
	 *  Consulta per retornar les dades de les firmes dels 
	 *  annexos sense detall
	 **/
	@Query("from RegistreAnnexFirmaEntity raf " + 
			"where raf.annex.id = :registreAnnexId")
	public List<RegistreAnnexFirmaEntity> getRegistreAnnexFirmesSenseDetall(
			@Param("registreAnnexId") Long registreAnnexId);
	
	@Modifying
	@Query(value = "update dis_registre_annex_firma " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
}
