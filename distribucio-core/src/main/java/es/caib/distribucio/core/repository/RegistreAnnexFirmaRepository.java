/**
 * 
 */
package es.caib.distribucio.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus firma d'annex de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreAnnexFirmaRepository extends JpaRepository<RegistreAnnexFirmaEntity, Long> {
	
}
