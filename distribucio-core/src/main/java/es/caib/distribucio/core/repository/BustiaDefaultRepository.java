/**
 * 
 */
package es.caib.distribucio.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.core.entity.BustiaDefaultEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus bustia default
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BustiaDefaultRepository extends JpaRepository<BustiaDefaultEntity, Long> {

	BustiaDefaultEntity findByEntitatAndUsuari(EntitatEntity entitat, UsuariEntity usuari);
}
