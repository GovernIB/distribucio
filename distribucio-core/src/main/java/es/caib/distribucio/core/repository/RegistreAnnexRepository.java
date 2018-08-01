/**
 * 
 */
package es.caib.distribucio.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.core.entity.RegistreAnnexEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreAnnexRepository extends JpaRepository<RegistreAnnexEntity, Long> {
	
}
