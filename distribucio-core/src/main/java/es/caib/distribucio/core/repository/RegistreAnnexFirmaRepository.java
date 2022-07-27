/**
 * 
 */
package es.caib.distribucio.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;

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
	public RegistreAnnexFirmaEntity getRegistreAnnexFirmaSenseDetall(
			@Param("registreAnnexId") Long registreAnnexId);
	
}
